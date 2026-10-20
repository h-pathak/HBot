# HariomBot: 100% Free Mobile App (Google AI Studio) & OpenBot + Antigravity Kit

This guide gives you two ways to build and run **HariomBot** for **$0/month**:
1. **Path A (Recommended to Start — Mobile-First Full-Stack PWA in Google AI Studio)**: Zero Docker or OAuth setup. Uses [Google AI Studio Build Mode](https://www.google.com/search?q=https%3A%2F%2Fai.google.dev%2Fgemini-api%2Fdocs%2Faistudio-build-mode) (powered by the Antigravity Agent) to generate a mobile-installable web app with automatic **Gmail, Google Calendar, Google Sheets, Google Drive, Google Tasks, and Keep** integrations plus **Firebase Firestore** persistent memory ([Full-Stack Docs](https://www.google.com/search?q=https%3A%2F%2Fai.google.dev%2Fgemini-api%2Fdocs%2Faistudio-fullstack)).
2. **Path B (Desktop Power-User — Self-Hosted OpenBot + Google Antigravity)**: Adds per-Bot headless Chrome browsers for external portals without APIs (like I-STEM and GBRC) and local terminal execution via [CopilotKit/OpenBot](https://www.google.com/search?q=https%3A%2F%2Fgithub.com%2FCopilotKit%2Fopenbot).

---

## Path A: Build HariomBot for Your Phone in Google AI Studio (Zero Setup)

### Why Choose Web App (PWA) Over "Native Android" in AI Studio?
In [Google AI Studio Build Mode](https://www.google.com/search?q=https%3A%2F%2Fai.google.dev%2Fgemini-api%2Fdocs%2Faistudio-build-mode), always keep the platform picker set to **Web App (Full-Stack)** rather than **Android**:
* **Native Android mode is client-side only**: As documented in [Build Android Apps in Google AI Studio](https://www.google.com/search?q=https%3A%2F%2Fai.google.dev%2Fgemini-api%2Fdocs%2Faistudio-android), selecting Android disables the Node.js server runtime, which turns off **Google Workspace APIs (Gmail, Sheets, Calendar, Drive)**, **Firebase Firestore**, and **server-side secret management**.
* **Full-Stack Web App + PWA gives you everything on your phone**: By building in Full-Stack Web mode with a mobile layout and `manifest.json`, you keep 1-click Workspace OAuth, Firestore memory, and server-side `GEMINI_API_KEY` security, and you can install it onto your Android/iOS home screen via Chrome's **Add to Home Screen** (or wrap the URL in a lightweight Android WebView later).

### Step 1: Enable Workspace Integrations in Google AI Studio
1. Open [Google AI Studio Build Mode](https://www.google.com/search?q=https%3A%2F%2Fai.google.dev%2Faistudio) and keep the platform set to **Web**.
2. In the right-hand **Integrations** sidebar (or via the prompt below), enable: **Gmail**, **Google Calendar**, **Google Sheets**, **Google Drive**, **Google Tasks**, and **Google Keep**.

### Step 2: Copy-Paste This Master Prompt into Google AI Studio Build Mode

```text
Build a mobile-first, installable Progressive Web App (PWA) called "HariomBot" — an always-on personal AI agent workspace modeled after ChatGPT Dots and Grok Bot, tailored for Hariom Pathak (a biotech founder and microbiology researcher in Gujarat, India).

1. ARCHITECTURE & INTEGRATIONS (FULL-STACK NODE.JS + REACT):
- Provision Firebase Authentication ("Sign in with Google") and a Firebase Firestore database for persistent cross-session memory, agent threads, standing routines, and an Approval Queue.
- Connect Google Workspace APIs on the server side: Gmail, Google Calendar, Google Sheets, Google Drive, Google Tasks, and Google Keep.
- Use the server-side GEMINI_API_KEY with Gemini 2.5 Flash (and Google Search grounding) for agent reasoning, plus a floating voice-call button using the Gemini Live API / speech-to-text so I can talk to my agents on my phone.
- Include a valid manifest.json, service worker, and mobile viewport meta tags so I can install HariomBot on my phone via "Add to Home Screen".
- In metadata.json, request ["microphone"] in requestFramePermissions for voice commands.

2. ROSTER OF 4 SPECIALIST AI TEAMMATES (stored in Firestore, selectable via a Grok-Bot-style top roster bar with animated state indicators: Idle, Thinking, Working, Waiting for Approval, Done):
- "Atlas" (Personal Chief of Staff): Coordinates my Daily Schedule sheet and Google Calendar, triages unread Gmail threads, manages Google Tasks, and routes sub-tasks to other Bots. Includes a "/learn" skill-creator command that saves new reusable workflows into a Firestore `skills` collection.
- "Reactor" (NocVita & Blue Biorefinery Ops): Manages oscillatory flow photobioreactor (PBR) hardware prototyping, CAD/3D-printing specs, and vendor RFQs (Vexma Technologies, Soch3D, Trident Labortek, Amerging Technologies, BioKav, IndiaMART). Extracts specs from Drive/Gmail, compares vendor quotes in structured tables (price, lead time, material grade, student/startup discounts), and drafts RFQ/follow-up emails.
- "Helix" (Research, Manuscripts & Lab Bookings): Tracks scientific manuscripts (such as Nonlinear Science / Editorial Manager submissions) and conference deadlines (ICWS 2026, XXII INQUA Congress 2027), prepares Boltz API and AlphaFold biomolecular modeling notes in Google Docs/Keep, and tracks I-STEM (ICP-MS) and GBRC (MALDI-TOF / sequencing) lab booking schedules and proforma invoices from Gmail.
- "Pulse" (Weekly Content Studio): Reads and updates my Content Schedule Google Sheet to run my Thursday-to-Wednesday content cycle: Thursday (Blog SEO research & outline), Friday (Full blog draft in Google Docs), Saturday (LinkedIn post + carousel brief), and Sunday (Instagram Reels captions, hooks, CTAs, hashtags).

3. CORE DOTS & GROK BOT CAPABILITIES IN THE MOBILE UI:
- Bottom Navigation Bar with 4 tabs optimized for one-handed phone use:
  a) "Roster & Chat": Direct chat with any Bot or a shared "Command Center" group channel where Atlas delegates to Reactor, Helix, and Pulse.
  b) "Approvals (Auto-Review Gate)": HARD RULE — No Bot may ever send a Gmail message, modify a Calendar event, or overwrite a Sheet without human approval. Any write action must generate an inline "Approval Card" (showing Recipient/Target, Subject, Diff/Preview, and "Approve & Execute" / "Edit" / "Discard" buttons) saved in the Firestore `approvals` collection.
  c) "Routines & Heartbeat": A scheduler tab where I can toggle standing routines (Morning 8:30 AM Briefing, Tuesday/Friday 4:00 PM Vendor RFQ Tracker, Thursday 10:00 AM Content Kickoff) and trigger a "Run Proactive Check Now" heartbeat that performs read-only scans of Gmail, Calendar, and Sheets to surface next steps.
  d) "Memory & Skills": A searchable viewer for Firestore persistent memories (facts learned about my projects, preferences, and bottlenecks, plus a 1-question-at-a-time "Interview Me" onboarding mode) and saved reusable Skills.
```

### Step 3: Sync with Google Antigravity via GitHub
Once your mobile app is generated in AI Studio:
1. Open **Settings → GitHub** in Google AI Studio to link a repository for two-way sync ([AI Studio Build Docs](https://www.google.com/search?q=https%3A%2F%2Fai.google.dev%2Fgemini-api%2Fdocs%2Faistudio-build-mode)).
2. Clone that same repository in [Google Antigravity](https://www.google.com/search?q=https%3A%2F%2Fantigravity.google%2F) whenever you want coding subagents to add custom Python scripts, Boltz API helpers, or headless browser automation.

---

## Path B: Desktop Self-Hosted Stack (OpenBot + Google Antigravity)

Use this path when you want per-Bot headless Chrome browsers (for logging into portals like I-STEM and GBRC) running on your laptop via **[CopilotKit/OpenBot](https://www.google.com/search?q=https%3A%2F%2Fgithub.com%2FCopilotKit%2Fopenbot)** and **[agentmemory](https://www.google.com/search?q=https%3A%2F%2Fgithub.com%2Frohitg00%2Fagentmemory)**.

### 1. Zero-Cost Architecture Stack

| Layer | Pre-Existing Repo / Service | How It Stays 100% Free |
| :--- | :--- | :--- |
| **Agent Roster, UI & Routines** | [CopilotKit/OpenBot](https://www.google.com/search?q=https%3A%2F%2Fgithub.com%2FCopilotKit%2Fopenbot) | MIT-licensed self-hosted stack (`OPENBOT_SINGLE_USER=true`) running locally via Bun + Docker |
| **Durable Threads & Learning** | [CopilotKit Intelligence](https://www.google.com/search?q=https%3A%2F%2Fgithub.com%2FCopilotKit%2FOpenBot%2Fblob%2Fmain%2Fprompt.txt) | Free Developer plan (`npx copilotkit@latest local setup` or free managed project; renewable every 30 days via `npx copilotkit@latest local renew`) |
| **LLM Brain (Built-in & LangGraph Bots)** | Google AI Studio (Gemini Free Tier) or Ollama | Point `OPENAI_BASE_URL` at Gemini's OpenAI-compatible endpoint (`https://generativelanguage.googleapis.com/v1beta/openai/`) with `gemini-2.5-flash`, or local Ollama (`http://127.0.0.1:11434/v1`) |
| **Per-Bot Browser Computers** | [OpenBot Local Chrome Helper](https://www.google.com/search?q=https%3A%2F%2Fgithub.com%2FCopilotKit%2Fopenbot%2Fblob%2Fmain%2Fdocs%2Fconfiguration.md) | Uses your existing installed Google Chrome (`bun scripts/start-local-chrome-computer.ts`) with isolated per-Bot profiles and human **Take control** |
| **Persistent Memory** | [rohitg00/agentmemory](https://www.google.com/search?q=https%3A%2F%2Fgithub.com%2Frohitg00%2Fagentmemory) | Uses free on-device embeddings (`EMBEDDING_PROVIDER=local` with `Xenova/all-MiniLM-L6-v2`) and local SQLite (`$0/yr`) |
| **Connected Apps (Gmail, Drive, Sheets, Calendar)** | [Composio Free Tier](https://www.google.com/search?q=https%3A%2F%2Fcomposio.dev%2Fcontent%2Fguide-to-frok-bot) + [Parallel MCP](https://www.google.com/search?q=https%3A%2F%2Fgithub.com%2FCopilotKit%2FOpenDots) | Free developer tier for OAuth app connections (`COMPOSIO_API_KEY`) + free anonymous web search via `https://search.parallel.ai/mcp` |
| **Coding & Skills Harness** | [Google Antigravity](https://www.google.com/search?q=https%3A%2F%2Fantigravity.google%2F) + [addyosmani/agent-skills](https://www.google.com/search?q=https%3A%2F%2Fgithub.com%2Faddyosmani%2Fagent-skills) | Free for developers; connects via OpenBot's `agent-antigravity/` adapter and `.agents/skills/` |

### 2. One-Script Bootstrapper (`bootstrap_hariom_openbot.sh`)

```bash
#!/usr/bin/env bash
set -euo pipefail

# 1. Clone OpenBot and install dependencies
git clone https://github.com/CopilotKit/openbot.git hariom-openbot
cd hariom-openbot
cp .env.example .env
bun install

# 2. Create your personalized Tenant Package directory
mkdir -p examples/hariom-workspace/agents

# 3. Write brand.yaml
cat << 'EOF' > examples/hariom-workspace/brand.yaml
tenant:
  id: hariom-openbot
  product_name: HariomBot
EOF

# 4. Write model.yaml (uses OpenAI-compatible route so Gemini Free Tier, Groq Free, or Ollama work seamlessly)
cat << 'EOF' > examples/hariom-workspace/model.yaml
model:
  provider: openai
  credential_secret_ref: openai-api-key
  default_model: gemini-2.5-flash
EOF

# 5. Write knowledge.yaml (connects your Google Drive folders)
cat << 'EOF' > examples/hariom-workspace/knowledge.yaml
sources:
  - type: google-drive
    roots:
      - NocVita
      - Research
      - Content Schedule
      - Schedule
EOF

# 6. Write skills.yaml (includes the load-bearing skill-creator slug + workflow skills)
cat << 'EOF' > examples/hariom-workspace/skills.yaml
skills:
  - slug: skill-creator
    title: Create a Reusable Skill
    summary: Turn a demonstrated or discussed workflow into a reusable saved skill.
    instructions: >-
      When Hariom asks to save a workflow or routine as a skill, draft the clear step-by-step instructions, decision boundaries, and required tools, then present the skill card for approval.

  - slug: find-a-document
    title: Search & Read Workspace Files
    summary: Search connected Google Drive folders and Sheets before answering.
    instructions: >-
      Search first, then read the full file or spreadsheet you found rather than guessing from its title. Cite the exact document name and link.
    tools:
      - google-drive/search_files
      - google-drive/read_file_content

  - slug: vendor-rfq-comparison
    title: Multi-Vendor RFQ & Quote Comparison
    summary: Draft technical quotation requests and compare vendor pricing side by side.
    instructions: >-
      Extract exact technical specs, CAD/material tolerances, and target budget ceilings. Draft itemized RFQ emails for each vendor and build a Markdown comparison scorecard. Never send an email or accept a quote without explicit approval.

  - slug: lab-instrument-booking
    title: I-STEM & GBRC Instrument Slot Check
    summary: Check analytical equipment availability and prepare booking details.
    instructions: >-
      Navigate to the I-STEM Portal or GBRC Booking Portal in the Bot browser. If a login or CAPTCHA appears, request human Takeover. Check available slots for MALDI-TOF, ICP-MS, or sequencing, log proforma invoice amounts, and pause before submitting any reservation.

  - slug: weekly-content-pipeline
    title: Weekly Content Cycle (Blog, LinkedIn, Instagram)
    summary: Execute the Thursday-to-Wednesday content research, writing, and scheduling loop.
    instructions: >-
      Follow the weekly cadence: Thursday (Blog SEO research & outline), Friday (Full blog draft + graphic notes), Saturday (LinkedIn trend research & post + carousel brief), Sunday (Instagram Reels/post captions, hooks, CTAs, hashtags), Monday (Revisions & content calendar update). Keep all outputs in draft state for review.
EOF

# 7. Write agents.yaml (Your 5 Personalized AI Teammates)
cat << 'EOF' > examples/hariom-workspace/agents.yaml
agents:
  - id: chief-of-staff
    name: Atlas
    title: Personal Chief of Staff
    role_description: Coordinate daily schedules, triage Gmail, track action items across NocVita and MSU Baroda, and route tasks to specialist Bots.
    avatar_seed: atlas-chief
    type: built-in
    system_prompt: >-
      You are Atlas, Hariom Pathak's Personal Chief of Staff based in Gujarat, India (Asia/Kolkata).
      You coordinate across his four main domains:
      1) NocVita & Blue Biorefinery (biotech hardware prototyping, oscillatory flow photobioreactors, vendor RFQs).
      2) Scientific Research & Lab Operations (MSU Baroda microbiology, manuscript submissions on Editorial Manager, I-STEM and GBRC lab bookings, Boltz API and AlphaFold runs).
      3) Content & Growth Operations (weekly blog, LinkedIn, and Instagram pipeline).
      4) Civic & Event Coordination (Vadodara vendor comparisons and structured follow-ups).
      Always ground every answer in connected tools or files. Hold all outbound emails, calendar changes, and form submissions for Hariom's explicit approval.
    skills:
      - skill-creator
      - find-a-document

  - id: nocvita-ops
    name: Reactor
    title: NocVita Hardware & Procurement Lead
    role_description: Manage photobioreactor prototyping specs, CAD/3D-printing vendor RFQs, and biorefinery commercialization tracking.
    avatar_seed: nocvita-reactor
    type: built-in
    system_prompt: >-
      You manage operations and hardware sourcing for NocVita and the Blue Biorefinery project.
      When sourcing components (borosilicate glass columns, stainless-steel multi-port caps, 3D-printed baffles, laser-cut acrylics, or lab safety supplies) from vendors such as Vexma Technologies, Soch3D, Trident Labortek, Amerging Technologies, BioKav, or IndiaMART suppliers:
      - Always request itemized quotes, lead times, and student/startup discounts.
      - Compare competing vendors in a structured table across price, material grade, tolerance, and delivery location (Vadodara/Gandhinagar).
      - Draft outreach and follow-up emails in Hariom's direct, analytical voice, and stop for human approval before sending.
    skills:
      - vendor-rfq-comparison
      - find-a-document

  - id: bio-researcher
    name: Helix
    title: Research, Manuscripts & Lab Bookings
    role_description: Track journal submissions, run literature & biomolecular modeling workflows, and check I-STEM/GBRC instrument slots.
    avatar_seed: helix-research
    type: built-in
    system_prompt: >-
      You support Hariom's scientific research in microbiology, nonlinear stochastic synchronization, sedimentary eDNA, water sustainability, and structural biology.
      Your responsibilities:
      - Monitor manuscript statuses, reviewer comments, and conference deadlines (such as ICWS 2026 and XXII INQUA Congress 2027).
      - Prepare inputs and analyze outputs for Boltz API (BoltzMol-1 / BoltzProt-1) and AlphaFold3 biomolecular modeling.
      - Check equipment availability on I-STEM (ICP-MS) and GBRC (MALDI-TOF, Sanger/NGS sequencing). Request human browser takeover whenever authentication is required, and never finalize a paid booking without approval.
    skills:
      - lab-instrument-booking
      - find-a-document

  - id: content-studio
    name: Pulse
    title: Content & Social Media Operator
    role_description: Run the weekly Thursday-to-Wednesday blog, LinkedIn, and Instagram content workflow.
    avatar_seed: pulse-content
    type: built-in
    system_prompt: >-
      You manage Hariom's weekly content calendar and writing deliverables:
      - Thursday: Blog research, statistics, citations, SEO keywords, and structured outline.
      - Friday: Complete blog draft with self-editing and visual/graphic briefs.
      - Saturday: LinkedIn post grounded in current biotech/sustainability/tech trends plus carousel brief.
      - Sunday: Instagram Reels/post captions with strong hooks, CTAs, and hashtags.
      - Monday & Wednesday: Revisions from feedback and next-cycle topic planning.
      Always cite verified sources and present drafts for approval before publishing.
    skills:
      - weekly-content-pipeline
      - find-a-document

  - id: antigravity-coder
    name: Forge
    title: Antigravity Coding & Simulation Agent
    role_description: Write Python simulations, data pipelines, LaTeX manuscripts, and web tools via the AG-UI bridge, pausing before any deployment or destructive shell command.
    avatar_seed: forge-coder
    type: remote-ag-ui
    endpoint: ${MANAGED_AGENT_AG_UI_URL:-}
EOF

# 8. Write channels.yaml (Pre-configured group & specialist channels)
cat << 'EOF' > examples/hariom-workspace/channels.yaml
channels:
  - id: command-center
    name: Command Center
    description: Daily briefing, schedule coordination, and cross-project handoffs.
    permitted_agents: [chief-of-staff, nocvita-ops, bio-researcher, content-studio, antigravity-coder]
    allowed_groups: [personal]

  - id: nocvita-hardware
    name: NocVita & Biorefinery
    description: Photobioreactor CAD prototyping, vendor quotes, and bioprocess R&D.
    permitted_agents: [chief-of-staff, nocvita-ops, bio-researcher]
    allowed_groups: [personal]

  - id: research-and-labs
    name: Research, Papers & Lab Bookings
    description: Manuscript submissions, Boltz/AlphaFold modeling, and I-STEM/GBRC instrument slots.
    permitted_agents: [chief-of-staff, bio-researcher, antigravity-coder]
    allowed_groups: [personal]

  - id: content-engine
    name: Weekly Content Studio
    description: Blog research, LinkedIn posts, and Instagram Reels workflow.
    permitted_agents: [chief-of-staff, content-studio]
    allowed_groups: [personal]
EOF

echo "✅ Created examples/hariom-workspace/ tenant package!"
```

### 3. Free `.env` Configuration (`hariom-openbot/.env`)

```ini
# 1. Single-user personal mode (no login wall on localhost)
OPENBOT_SINGLE_USER=true
OPENBOT_SELF_HOST_BANNER=false
TENANT_PACKAGE_DIR=../examples/hariom-workspace
DEPLOYMENT_ID=hariom-openbot

# 2. Generate encryption key once via: openssl rand -base64 32
KEY_ENCRYPTION_KEY=replace_with_output_of_openssl_rand_base64_32

# 3. Free Model Option A: Google Gemini Free Tier (from https://aistudio.google.com/apikey)
OPENAI_BASE_URL=https://generativelanguage.googleapis.com/v1beta/openai/
OPENAI_API_KEY=your_free_google_ai_studio_api_key
GOOGLE_API_KEY=your_free_google_ai_studio_api_key
BOT_PROVIDER=google
BOT_MODEL=gemini-2.5-flash
AGENT_BOT_MODEL=gemini-2.5-flash

# 4. Optional: Composio Free Tier for Gmail, Calendar, Sheets, Drive (https://composio.dev)
COMPOSIO_API_KEY=your_free_composio_key

# 5. Lightweight Local Chrome Computer (uses your installed Chrome instead of heavy Docker browsers)
AGENT_COMPUTER_URL=http://127.0.0.1:4101
COMPUTER_SUPERVISOR_URL=
COMPUTER_SANDBOX_NAMESPACE=
```

### 4. Launching OpenBot + Free Local Memory + Antigravity Skills

```bash
# Sign in to CopilotKit's free Developer plan and link your project
npx --yes copilotkit@latest login
npx --yes copilotkit@latest project select
bun scripts/setup-learning.ts

# Start OpenBot (PostgreSQL, API on :3001, Web UI on :3010, and Routine Worker)
bash scripts/start.sh

# In a second terminal: start the Local Chrome Computer helper
bun scripts/start-local-chrome-computer.ts

# Start free local memory (rohitg00/agentmemory) & connect to Antigravity
mkdir -p ~/.agentmemory && echo "EMBEDDING_PROVIDER=local" >> ~/.agentmemory/.env
npx -y @agentmemory/agentmemory@latest
npx skills add rohitg00/agentmemory -y
agentmemory connect antigravity
```