/**
 * HBot v2 Multi-Agent UI Protocol & Structured Components
 * Tailored for technical founders, researchers, and creators
 */

export type BotId = 'atlas' | 'reactor' | 'helix' | 'pulse' | 'forge';

export type MascotState =
  | 'idle'
  | 'listening'
  | 'thinking'
  | 'working'
  | 'waiting_approval'
  | 'celebrating';

export interface BotAgent {
  id: BotId;
  name: string;
  role: string;
  systemPrompt: string;
  primaryColor: string;
  lightColor: string;
  darkColor: string;
  status: MascotState;
  specialties: string[];
}

// ==========================================
// UIBLOCK TYPES & INTERFACES
// ==========================================

export type UIBlockType =
  | 'bento_briefing'
  | 'handoff_card'
  | 'approval_card'
  | 'vendor_scorecard'
  | 'portal_takeover_card'
  | 'skill_learned_card';

export interface BaseUIBlock {
  type: UIBlockType;
  id: string;
}

// --- 1. bento_briefing ---
export interface BentoScheduleItem {
  time: string;
  event: string;
  tag?: string;
  location?: string;
}

export interface BentoPriorityItem {
  id: string;
  task: string;
  ownerBotId: BotId;
  status: 'pending' | 'in_progress' | 'blocked' | 'completed';
  deadline?: string;
  importance: 'urgent' | 'high' | 'medium';
}

export interface BentoMilestone {
  title: string;
  progressPercent: number;
  grantRef?: string;
  trancheInfo?: string;
  nextStep: string;
}

/**
 * 2x2 Bento Briefing Morning Grid
 */
export interface BentoBriefingBlock extends BaseUIBlock {
  type: 'bento_briefing';
  headline: string;
  dateStr: string;
  schedule: BentoScheduleItem[];
  priorities: BentoPriorityItem[];
  milestone: BentoMilestone;
  pendingApprovalsCount: number;
  connectedSheets: {
    dailyScheduleId: string;
    contentScheduleId: string;
  };
}

// --- 2. handoff_card ---
/**
 * Multi-Bot Handoff Banner & Reasoning Chain
 */
export interface HandoffCardBlock extends BaseUIBlock {
  type: 'handoff_card';
  fromBotId: BotId;
  toBotId: BotId;
  intent: string;
  reasoning: string;
  contextPayload?: Record<string, unknown>;
  suggestedNextStep?: string;
  status: 'delegated' | 'acknowledged' | 'in_progress' | 'completed';
  timestamp: string;
}

// --- 3. approval_card ---
export type ApprovalServiceType =
  | 'GMAIL'
  | 'GOOGLE_SHEETS'
  | 'GOOGLE_CALENDAR'
  | 'GOOGLE_DOCS'
  | 'GRANT_DOC';

export type ApprovalOperationType =
  | 'GMAIL_DRAFT'
  | 'GMAIL_SEND'
  | 'SHEET_ROW_UPDATE'
  | 'CALENDAR_SLOT'
  | 'GRANT_DOC_NOTE'
  | 'DOC_CREATE';

export interface ApprovalBudgetTag {
  trancheName: string;
  amount: number;
  currency: 'INR' | 'USD';
  remainingTranche: number;
}

/**
 * Human-in-the-Loop Strict Security Gate Action Card
 */
export interface ApprovalCardBlock extends BaseUIBlock {
  type: 'approval_card';
  actionId: string;
  botId: BotId;
  service: ApprovalServiceType;
  operationType: ApprovalOperationType;
  title: string;
  previewText: string;
  diffPreview?: {
    before?: string;
    after: string;
    targetSheetOrDoc?: string;
  };
  budgetTag?: ApprovalBudgetTag;
  status: 'PENDING' | 'APPROVED' | 'EDITED' | 'REJECTED';
  targetPayload: Record<string, unknown>;
  createdAt: string;
  resolvedAt?: string;
}

// --- 4. vendor_scorecard ---
export interface VendorQuote {
  id: string;
  name: string;
  priceInr: number;
  turnaroundDays: number;
  toleranceMm: number;
  surfaceFinishRating: number; // 1 to 5
  pros: string[];
  cons: string[];
  isRecommended?: boolean;
}

/**
 * Side-by-side RFQ Vendor Scorecard (e.g. SLA 3D printing for hardware prototype)
 */
export interface VendorScorecardBlock extends BaseUIBlock {
  type: 'vendor_scorecard';
  partName: string;
  materialSpec: string;
  budgetTranche: string;
  recommendedVendorId: string;
  vendors: VendorQuote[];
  notes?: string;
}

// --- Additional Structured Cards ---
export interface PortalTakeoverCardBlock extends BaseUIBlock {
  type: 'portal_takeover_card';
  portalName: string;
  portalUrl: string;
  instrumentName: string;
  steps: Array<{
    stepNumber: number;
    instruction: string;
    copyableToken?: string;
  }>;
}

export interface SkillLearnedCardBlock extends BaseUIBlock {
  type: 'skill_learned_card';
  skillId: string;
  title: string;
  botOwner: BotId;
  category: string;
  procedure: string[];
  requiredTools: string[];
  examplePrompt: string;
  learnedFromChannel?: string;
}

// ==========================================
// CANONICAL UIBLOCK UNION & ALIASES
// ==========================================

export type UIBlock =
  | BentoBriefingBlock
  | HandoffCardBlock
  | ApprovalCardBlock
  | VendorScorecardBlock
  | PortalTakeoverCardBlock
  | SkillLearnedCardBlock;

export type AgentUIBlock = UIBlock;

export type BentoBriefing = BentoBriefingBlock;
export type HandoffCard = HandoffCardBlock;
export type ApprovalCard = ApprovalCardBlock;
export type VendorScorecard = VendorScorecardBlock;
export type PortalTakeoverCard = PortalTakeoverCardBlock;
export type SkillLearnedCard = SkillLearnedCardBlock;

// ==========================================
// TYPE GUARDS FOR STRUCTURED VALIDATION
// ==========================================

export function isBentoBriefing(block: UIBlock): block is BentoBriefingBlock {
  return block.type === 'bento_briefing';
}

export function isHandoffCard(block: UIBlock): block is HandoffCardBlock {
  return block.type === 'handoff_card';
}

export function isApprovalCard(block: UIBlock): block is ApprovalCardBlock {
  return block.type === 'approval_card';
}

export function isVendorScorecard(block: UIBlock): block is VendorScorecardBlock {
  return block.type === 'vendor_scorecard';
}

export function isPortalTakeover(block: UIBlock): block is PortalTakeoverCardBlock {
  return block.type === 'portal_takeover_card';
}

export function isSkillLearned(block: UIBlock): block is SkillLearnedCardBlock {
  return block.type === 'skill_learned_card';
}

// ==========================================
// CITATIONS & MESSAGES
// ==========================================

export interface CitationChip {
  id: string;
  title: string;
  url: string;
  sourceType: 'SHEET' | 'GMAIL' | 'DRIVE' | 'WEB' | 'PUBMED' | 'PATENT';
}

export interface AgentMessage {
  id: string;
  botId: BotId;
  senderType: 'BOT' | 'USER' | 'SYSTEM';
  text: string;
  timestamp: string;
  stepsUsed?: string[];
  citationChips?: CitationChip[];
  blocks?: UIBlock[];
  rawHandoff?: HandoffCardBlock;
  rawApproval?: ApprovalCardBlock;
  rawScorecard?: VendorScorecardBlock;
  rawBento?: BentoBriefingBlock;
}

export interface MemoryItem {
  id: string;
  category: 'BIOTECH' | 'ACADEMIC' | 'CONTENT' | 'PERSONAL' | 'WORKFLOW';
  fact: string;
  confidence: number;
  source: string;
  timestamp: string;
}
