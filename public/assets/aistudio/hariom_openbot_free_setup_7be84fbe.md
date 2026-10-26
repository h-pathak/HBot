# HBot: Multi-Agent Workspace Setup Guide

This guide describes how to configure and deploy **HBot** as a multi-agent personal workspace.

---

## 1. Architecture Overview

HBot provides a multi-agent workspace with specialized AI teammates:
- **Atlas** (Chief of Staff & Coordinator): Orchestrates daily schedule, tasks, triage, and task routing.
- **Reactor** (Hardware & Prototyping Ops): Manages specifications, BOMs, fabrication RFQs, and vendor comparisons.
- **Helix** (Research & Lab Operations): Tracks scientific literature, protocols, analysis pipelines, and facility slots.
- **Pulse** (Content Studio): Orchestrates editorial calendars, articles, technical briefs, and publications.
- **Forge** (Code & Technical Systems): Coordinates software scripts, CI/CD, data parsing, and infrastructure.

---

## 2. Zero-PII & Private Vault Architecture

HBot uses a **Zero-PII On-Device Private Vault**:
- No personal names, addresses, API secrets, or proprietary IDs are hardcoded in the codebase.
- User profile data (Display Name, Domain/Role, Organization/Lab, Connected Google Sheet IDs, and Custom Context Notes) is stored locally on-device.
- Users can import/export their private context as JSON, or wipe local storage with one click.
- All personal context is injected into agent reasoning at runtime only.

---

## 3. Human-in-the-Loop Approval Gate

- All mutating actions (sending emails, modifying spreadsheets, creating calendar events, committing external orders) require human review.
- Teammate agents generate structured `ApprovalCard` items before executing external writes.