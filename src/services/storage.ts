/**
 * HBot v2 Storage Service
 * Manages persistent offline-first synchronization of app state (messages, memories, approvals)
 * utilizing a combination of Firebase Firestore and localStorage.
 */

import { AgentMessage, ApprovalCardBlock, MemoryItem } from '../types/agent';

// Storage Keys
const STORAGE_KEYS = {
  MESSAGES_PREFIX: 'hbot_messages_',
  MEMORIES: 'hbot_memories',
  APPROVALS: 'hbot_approvals',
  SYNC_QUEUE: 'hbot_sync_queue',
  LAST_SYNC: 'hbot_last_sync_timestamp',
};

// Sync Queue Item Contract
export interface SyncQueueItem {
  id: string;
  type: 'MESSAGE' | 'APPROVAL' | 'MEMORY';
  operation: 'CREATE' | 'UPDATE' | 'DELETE';
  collection: string;
  docId: string;
  payload: any;
  timestamp: number;
}

// Minimal Firestore type contracts for optional SDK injection
export interface FirestoreLike {
  collection(name: string): any;
  doc?(path: string): any;
}

class StorageService {
  private firestore: FirestoreLike | null = null;
  private isOnline: boolean = typeof navigator !== 'undefined' ? navigator.onLine : true;
  private messageListeners: Map<string, Set<(messages: AgentMessage[]) => void>> = new Map();
  private approvalListeners: Set<(approvals: ApprovalCardBlock[]) => void> = new Set();
  private memoryListeners: Set<(memories: MemoryItem[]) => void> = new Set();

  constructor() {
    this.initNetworkListeners();
    this.ensureSeedData();
  }

  /**
   * Optional manual or automated Firebase Firestore initialization
   */
  public initStorage(firestoreInstance?: FirestoreLike) {
    if (firestoreInstance) {
      this.firestore = firestoreInstance;
      this.flushSyncQueue().catch(console.error);
    }
  }

  // ==========================================
  // NETWORK & SYNC MANAGEMENT
  // ==========================================

  private initNetworkListeners() {
    if (typeof window === 'undefined') return;

    window.addEventListener('online', () => {
      this.isOnline = true;
      this.flushSyncQueue().catch(console.error);
    });

    window.addEventListener('offline', () => {
      this.isOnline = false;
    });
  }

  private isLocalStorageAvailable(): boolean {
    return typeof window !== 'undefined' && !!window.localStorage;
  }

  private readLocalStorage<T>(key: string, defaultValue: T): T {
    if (!this.isLocalStorageAvailable()) return defaultValue;
    try {
      const raw = window.localStorage.getItem(key);
      return raw ? JSON.parse(raw) : defaultValue;
    } catch (err) {
      console.warn(`[StorageService] Failed to read ${key}:`, err);
      return defaultValue;
    }
  }

  private writeLocalStorage<T>(key: string, value: T): void {
    if (!this.isLocalStorageAvailable()) return;
    try {
      window.localStorage.setItem(key, JSON.stringify(value));
    } catch (err) {
      console.error(`[StorageService] Failed to write ${key}:`, err);
    }
  }

  private enqueueSync(item: Omit<SyncQueueItem, 'id' | 'timestamp'>) {
    const queue = this.readLocalStorage<SyncQueueItem[]>(STORAGE_KEYS.SYNC_QUEUE, []);
    const newItem: SyncQueueItem = {
      ...item,
      id: `sync_${Date.now()}_${Math.random().toString(36).substring(2, 7)}`,
      timestamp: Date.now(),
    };
    queue.push(newItem);
    this.writeLocalStorage(STORAGE_KEYS.SYNC_QUEUE, queue);

    if (this.isOnline && this.firestore) {
      this.flushSyncQueue().catch(console.error);
    }
  }

  public async flushSyncQueue(): Promise<void> {
    if (!this.isOnline || !this.firestore) return;

    const queue = this.readLocalStorage<SyncQueueItem[]>(STORAGE_KEYS.SYNC_QUEUE, []);
    if (queue.length === 0) return;

    const remainingQueue: SyncQueueItem[] = [];

    for (const task of queue) {
      try {
        if (task.operation === 'CREATE' || task.operation === 'UPDATE') {
          // If firestore instance has standard doc().set() or collection().doc().set()
          const colRef = this.firestore.collection(task.collection);
          if (colRef && typeof colRef.doc === 'function') {
            await colRef.doc(task.docId).set(task.payload, { merge: true });
          }
        } else if (task.operation === 'DELETE') {
          const colRef = this.firestore.collection(task.collection);
          if (colRef && typeof colRef.doc === 'function') {
            await colRef.doc(task.docId).delete();
          }
        }
      } catch (err) {
        console.warn(`[StorageService] Sync task ${task.id} failed, will retry:`, err);
        remainingQueue.push(task);
      }
    }

    this.writeLocalStorage(STORAGE_KEYS.SYNC_QUEUE, remainingQueue);
  }

  // ==========================================
  // MESSAGES API
  // ==========================================

  public async getMessages(channelId: string = 'command-center'): Promise<AgentMessage[]> {
    const key = `${STORAGE_KEYS.MESSAGES_PREFIX}${channelId}`;
    const local = this.readLocalStorage<AgentMessage[]>(key, []);

    // Opportunistically fetch latest from Firestore if online
    if (this.isOnline && this.firestore) {
      try {
        const snapshot = await this.firestore.collection(`channels/${channelId}/messages`).get();
        if (snapshot && snapshot.docs) {
          const remoteMessages: AgentMessage[] = snapshot.docs.map((d: any) => ({
            id: d.id,
            ...d.data(),
          }));
          if (remoteMessages.length > 0) {
            this.writeLocalStorage(key, remoteMessages);
            return remoteMessages;
          }
        }
      } catch (err) {
        console.warn(`[StorageService] Firestore getMessages failed for ${channelId}, using local:`, err);
      }
    }

    return local;
  }

  public async saveMessage(channelId: string = 'command-center', message: AgentMessage): Promise<void> {
    const key = `${STORAGE_KEYS.MESSAGES_PREFIX}${channelId}`;
    const messages = this.readLocalStorage<AgentMessage[]>(key, []);
    
    // De-duplicate by ID
    const index = messages.findIndex((m) => m.id === message.id);
    if (index >= 0) {
      messages[index] = message;
    } else {
      messages.push(message);
    }

    this.writeLocalStorage(key, messages);
    this.notifyMessageListeners(channelId, messages);

    // Enqueue Firestore write
    this.enqueueSync({
      type: 'MESSAGE',
      operation: 'CREATE',
      collection: `channels/${channelId}/messages`,
      docId: message.id,
      payload: message,
    });
  }

  public subscribeMessages(channelId: string, callback: (messages: AgentMessage[]) => void): () => void {
    if (!this.messageListeners.has(channelId)) {
      this.messageListeners.set(channelId, new Set());
    }
    this.messageListeners.get(channelId)!.add(callback);

    // Initial emit
    this.getMessages(channelId).then(callback);

    return () => {
      this.messageListeners.get(channelId)?.delete(callback);
    };
  }

  private notifyMessageListeners(channelId: string, messages: AgentMessage[]) {
    this.messageListeners.get(channelId)?.forEach((cb) => cb(messages));
  }

  // ==========================================
  // APPROVALS API
  // ==========================================

  public async getApprovals(): Promise<ApprovalCardBlock[]> {
    const local = this.readLocalStorage<ApprovalCardBlock[]>(STORAGE_KEYS.APPROVALS, []);

    if (this.isOnline && this.firestore) {
      try {
        const snapshot = await this.firestore.collection('approvals').get();
        if (snapshot && snapshot.docs) {
          const remote: ApprovalCardBlock[] = snapshot.docs.map((d: any) => ({
            actionId: d.id,
            ...d.data(),
          }));
          if (remote.length > 0) {
            this.writeLocalStorage(STORAGE_KEYS.APPROVALS, remote);
            return remote;
          }
        }
      } catch (err) {
        console.warn('[StorageService] Firestore getApprovals failed, using local:', err);
      }
    }

    return local;
  }

  public async saveApproval(approval: ApprovalCardBlock): Promise<void> {
    const approvals = this.readLocalStorage<ApprovalCardBlock[]>(STORAGE_KEYS.APPROVALS, []);
    const idx = approvals.findIndex((a) => a.actionId === approval.actionId);
    if (idx >= 0) {
      approvals[idx] = approval;
    } else {
      approvals.unshift(approval);
    }

    this.writeLocalStorage(STORAGE_KEYS.APPROVALS, approvals);
    this.notifyApprovalListeners(approvals);

    this.enqueueSync({
      type: 'APPROVAL',
      operation: 'CREATE',
      collection: 'approvals',
      docId: approval.actionId,
      payload: approval,
    });
  }

  public async updateApprovalStatus(
    actionId: string,
    status: 'APPROVED' | 'REJECTED' | 'EDITED',
    resolvedPayload?: Record<string, unknown>
  ): Promise<void> {
    const approvals = this.readLocalStorage<ApprovalCardBlock[]>(STORAGE_KEYS.APPROVALS, []);
    const target = approvals.find((a) => a.actionId === actionId);

    if (target) {
      target.status = status;
      target.resolvedAt = new Date().toISOString();
      if (resolvedPayload) {
        target.targetPayload = { ...target.targetPayload, ...resolvedPayload };
      }

      this.writeLocalStorage(STORAGE_KEYS.APPROVALS, approvals);
      this.notifyApprovalListeners(approvals);

      this.enqueueSync({
        type: 'APPROVAL',
        operation: 'UPDATE',
        collection: 'approvals',
        docId: actionId,
        payload: {
          status: target.status,
          resolvedAt: target.resolvedAt,
          targetPayload: target.targetPayload,
        },
      });
    }
  }

  public subscribeApprovals(callback: (approvals: ApprovalCardBlock[]) => void): () => void {
    this.approvalListeners.add(callback);
    this.getApprovals().then(callback);
    return () => {
      this.approvalListeners.delete(callback);
    };
  }

  private notifyApprovalListeners(approvals: ApprovalCardBlock[]) {
    this.approvalListeners.forEach((cb) => cb(approvals));
  }

  // ==========================================
  // MEMORIES API
  // ==========================================

  public async getMemories(): Promise<MemoryItem[]> {
    const local = this.readLocalStorage<MemoryItem[]>(STORAGE_KEYS.MEMORIES, []);

    if (this.isOnline && this.firestore) {
      try {
        const snapshot = await this.firestore.collection('memories').get();
        if (snapshot && snapshot.docs) {
          const remote: MemoryItem[] = snapshot.docs.map((d: any) => ({
            id: d.id,
            ...d.data(),
          }));
          if (remote.length > 0) {
            this.writeLocalStorage(STORAGE_KEYS.MEMORIES, remote);
            return remote;
          }
        }
      } catch (err) {
        console.warn('[StorageService] Firestore getMemories failed, using local:', err);
      }
    }

    return local;
  }

  public async saveMemory(memory: MemoryItem): Promise<void> {
    const memories = this.readLocalStorage<MemoryItem[]>(STORAGE_KEYS.MEMORIES, []);
    const idx = memories.findIndex((m) => m.id === memory.id);
    if (idx >= 0) {
      memories[idx] = memory;
    } else {
      memories.unshift(memory);
    }

    this.writeLocalStorage(STORAGE_KEYS.MEMORIES, memories);
    this.notifyMemoryListeners(memories);

    this.enqueueSync({
      type: 'MEMORY',
      operation: 'CREATE',
      collection: 'memories',
      docId: memory.id,
      payload: memory,
    });
  }

  public async deleteMemory(memoryId: string): Promise<void> {
    let memories = this.readLocalStorage<MemoryItem[]>(STORAGE_KEYS.MEMORIES, []);
    memories = memories.filter((m) => m.id !== memoryId);

    this.writeLocalStorage(STORAGE_KEYS.MEMORIES, memories);
    this.notifyMemoryListeners(memories);

    this.enqueueSync({
      type: 'MEMORY',
      operation: 'DELETE',
      collection: 'memories',
      docId: memoryId,
      payload: null,
    });
  }

  public subscribeMemories(callback: (memories: MemoryItem[]) => void): () => void {
    this.memoryListeners.add(callback);
    this.getMemories().then(callback);
    return () => {
      this.memoryListeners.delete(callback);
    };
  }

  private notifyMemoryListeners(memories: MemoryItem[]) {
    this.memoryListeners.forEach((cb) => cb(memories));
  }

  // ==========================================
  // INITIAL CONTEXT HYDRATION & SEEDING
  // ==========================================

  private ensureSeedData() {
    if (!this.isLocalStorageAvailable()) return;

    // 1. Seed Memories if empty
    const existingMemories = this.readLocalStorage<MemoryItem[]>(STORAGE_KEYS.MEMORIES, []);
    if (existingMemories.length === 0) {
      const defaultMemories: MemoryItem[] = [
        {
          id: 'mem_rnd_grant',
          category: 'BIOTECH',
          fact: 'Active Prototyping & R&D Innovation Project. Hardware and fabrication budgets tracked in expense ledger.',
          confidence: 0.99,
          source: 'Project Proposal & Award Documentation',
          timestamp: 'Oct 24, 2026',
        },
        {
          id: 'mem_bioreactor_specs',
          category: 'BIOTECH',
          fact: 'Bioreactor project: Subcritical fluid extraction and continuous culture monitoring for optimized biomass yield.',
          confidence: 0.96,
          source: 'Lab Protocol Notebook #04',
          timestamp: 'Oct 24, 2026',
        },
        {
          id: 'mem_genomics_pipeline',
          category: 'ACADEMIC',
          fact: 'Microbiology thesis research on environmental metagenomics and antimicrobial resistance markers.',
          confidence: 0.98,
          source: 'Institutional Research Department',
          timestamp: 'Oct 23, 2026',
        },
        {
          id: 'mem_connected_sheets',
          category: 'WORKFLOW',
          fact: 'Google Sheets synchronization ready. Configure Sheet IDs and custom credentials in the Private Vault.',
          confidence: 1.0,
          source: 'System Workspace Configuration',
          timestamp: 'Oct 25, 2026',
        },
      ];
      this.writeLocalStorage(STORAGE_KEYS.MEMORIES, defaultMemories);
    }

    // 2. Seed Approvals if empty
    const existingApprovals = this.readLocalStorage<ApprovalCardBlock[]>(STORAGE_KEYS.APPROVALS, []);
    if (existingApprovals.length === 0) {
      const defaultApprovals: ApprovalCardBlock[] = [
        {
          type: 'approval_card',
          id: 'appr_sla_printing',
          actionId: 'appr_sla_printing',
          botId: 'reactor',
          service: 'GOOGLE_SHEETS',
          operationType: 'SHEET_ROW_UPDATE',
          title: 'Confirm SLA 3D Printing Vendor (Supplier A)',
          previewText: 'Approve vendor selection for impeller housing prototype (₹4,200, 3-day turnaround).',
          diffPreview: {
            before: 'Pending RFQ Quotes (Supplier A ₹4,200 vs Supplier B ₹5,800)',
            after: 'Selected: Supplier A (PO #PROTO-SLA-001 allocated to Prototyping Budget)',
            targetSheetOrDoc: 'Expense Ledger (Tab: Prototypes)',
          },
          budgetTag: {
            trancheName: 'R&D Phase 1 (Consumables & Prototyping)',
            amount: 4200,
            currency: 'INR',
            remainingTranche: 82400,
          },
          status: 'PENDING',
          targetPayload: {
            vendor: 'Supplier A Fabrication',
            part: 'Impeller Housing v2',
            cost: 4200,
          },
          createdAt: '10:45 AM',
        },
        {
          type: 'approval_card',
          id: 'appr_instrument_email',
          actionId: 'appr_instrument_email',
          botId: 'helix',
          service: 'GMAIL',
          operationType: 'GMAIL_DRAFT',
          title: 'Draft Inquiry: High-Throughput Sequencing Facility Slot',
          previewText: 'Send formal slot inquiry to external instrumentation facility for 16S amplicon sequencing.',
          diffPreview: {
            after: 'To: lab-facility@example.org\nSubject: Slot Request: 16S rRNA Amplicon Sequencing',
          },
          status: 'PENDING',
          targetPayload: {
            recipient: 'lab-facility@example.org',
            subject: 'Slot Request: 16S rRNA Amplicon Sequencing',
          },
          createdAt: '11:15 AM',
        },
      ];
      this.writeLocalStorage(STORAGE_KEYS.APPROVALS, defaultApprovals);
    }
  }
}

// Export singleton instance as default and named
export const storageService = new StorageService();
export default storageService;
