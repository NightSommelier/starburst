# 3.1.0 Release-Readiness Audit Report (functional completeness)

> Purpose: against the ROADMAP 3.1.0 commitments, verify item by item whether features are
> **truly closed-loop and usable**, and whether gaps / half-finished work / defects remain.
> Method: 3 parallel subagents per functional domain (test intelligence / alerts·sync / KB·docs)
> performed three-layer wiring checks (entry → VM → API → backend contract) plus closed-loop /
> feedback / edge-case checks; release readiness was audited separately.
> Date: 2026-09-22. Verdict: **no release-blocking items**; 1 confirmed defect already fixed;
> several requirement-decision items are listed at the end.

## 1. Test Intelligence M9 — ✅ shippable

- **Complete**: session-list entry (backendReady-gated) → project list → detail with four blocks
  (features + unit tests + issues + fixes) → AI chat; all three wiring layers pass and match the
  backend contract field by field; issue ack / feature linkage, fix apply (file/patch/branch) /
  rollback / reject, diff preview, failed/empty/loading states all present.
- **⚠️ Gaps (non-blocking, addressed 2026-09-22)**:
  - `intel.gate.blocked`/`fix.suggested`/`fix.applied`/`chat.answer`/`env.ready` events were parsed
    but had no notification/UI outlet (parsed to `Unit`); a gate failure only showed a "run failed"
    Toast with no reason → **fixed**: all five event types now produce notifications
    (`showGateBlockedNotification` etc.), and `startRun` failures surface the backend reason.
  - After starting a test run, the run status **did not update automatically** (`intel.run.event`
    was not consumed to refresh the list; manual refresh required) → **fixed**: a global
    `IntelRunEventBus` was added; the push layer publishes `intel.run.event`, the project detail
    screen subscribes and merges the run list in real time by projectId; terminal run states also
    refresh the issues/fixes blocks.
  - A failed `loadRunResults` was **permanently cached as empty** (the idempotency guard made
    retry impossible on this screen) → **fixed**: failures no longer write an empty cache; a
    `runResultsFailed` set marks it and the UI shows "load failed, tap to retry".
  - **SECURITY_WARNING highlight is dead code**: the backend's `recordRunIssues` hardcodes issue
    severity to `"medium"` and `featureId=0`, so the app's red "!" badge **never triggers**; the
    real security findings (`/api/intel/findings`) have no client.
  - The project list lacks a "modules" display (`BackendApi.intelModules` has zero callers);
    feature `anchor` is not rendered.
  - Dead code: `intelRunDetail`/`featureRunTest` have no callers (and the backend allows admin
    scope only, so harmless).

## 2. Server monitoring alerts — ✅ shippable

- **Complete**: GET/POST `/api/alerts` contract matches; the server-management card
  (enable + threshold editing 0-100 + threshold-crossing red dot); `alert.hardware` push →
  notification + persisted history; local history (SQLite, 7 days) + 60s poll reconcile with
  dedup.
- **⚠️ Gaps (non-blocking)**:
  - severity **warning/info tiering not implemented** (parsed but unused; alert/ok share the same
    channel and priority).
  - Alert history **does not auto-refresh the UI after persisting** (manual refresh required).
  - A threshold of 0 silently falls back to 90 on the backend (UI echoes 90, mismatching input).
  - Deleting a server does not clear its history (7-day auto-expiry).
  - Non-2xx responses deserialize into an empty snapshot (401/500 render "monitoring off" instead
    of a failure hint; consistent with the project-wide no-expectSuccess pattern).

## 3. Multi-device sync — ✅ shippable (1 formal defect fixed)

- **Complete**: GET/PUT `/api/sync` contract matches (key=global, revision drift detection);
  SyncSettingsScreen has the full backend option set (https/loopback only + mandatory token);
  15-minute WorkManager auto sync; `disconnect()` clears only sync credentials (fix verified).
- **❌→✅ fixed**: `syncGetBundle`'s 404→null handling was broken (HttpClient without
  expectSuccess never throws `ClientRequestException`, so 404 deserialized into an empty Bundle)
  → **first sync against a brand-new backend always produced a false CONFLICT**. Changed to
  return null explicitly on `status==404` (`BackendApi.kt:764`), restoring the MISSING_UPLOAD
  semantics on first sync. `read()/write()`'s `catch (ClientRequestException)` is kept as
  defensive code.

## 4. Knowledge base (KB) — ✅ shippable (1 commitment gap needs a decision)

- **Complete**: dual entries (server management + session list, both now pointing to the
  registered `Kb` route); collection list/create, document list, text/file ingestion
  (size pre-check + streaming), semantic search (score + snippets); all three wiring layers pass
  and the contract matches.
- **⚠️ Commitment gap**:
  - **No UI for deleting collections/documents**: `BackendKbApi.deleteCollection/deleteDocument`
    have zero callers, yet ROADMAP already ticks "collection list/create/delete" — **decision
    needed**: add the delete UI, or reword ROADMAP to "management stays on Web".
    → **Resolved**: the delete UI was added (`KbScreen.kt:219`, `KbCollectionDetailScreen.kt:307`);
    see §7 item 2.
  - File ingestion accepts only `text/*` (`contentBase64` has no callers; the backend supports
    PDF/Office via multipart).
  - Size-cap mismatch: client 5 MiB vs backend JSON body 4 MiB — 4–5 MiB text gets a 413 reject.
  - KB documents cannot be previewed (the backend has no download endpoint; ROADMAP marks it
    deferred).

## 5. Document generation — ✅ shippable (preview button needs a decision)

- **Complete**: input-bar entry → generation dialog (PPT/Word/Excel) → message card
  (download/revise) → regenerate per instruction; contract matches and the loop is closed.
- **⚠️ Defect (decision needed)**:
  - **The preview button always 401s at runtime**: `DocumentPreviewSheet`'s WebView sends no auth
    headers, while the backend download endpoint requires `X-Web-Session`/`Authorization` (the
    app token reaches the WebView as neither). ROADMAP lists preview as deferred, but the button
    is already exposed — **decision needed**: hide the preview entry, or fix auth (inject headers).
    → **Resolved**: auth headers are now injected by `shouldInterceptRequest`
    (`DocumentPreviewSheet.kt:166-188`); see §7 item 1.
  - Reserved APIs (listDocuments/getDocument/deleteDocument) have no callers (harmless).

## 6. Release readiness

| Item | Status |
|---|---|
| `build.gradle.kts` versionCode/versionName | **8 / 3.0.0** (not bumped → 9 / 3.1.0; change at release time) |
| `update.json` | 3.0.0 (update versionName/versionCode/APK URL/sha256 when releasing 3.1.0) |
| CHANGELOG | `[Unreleased] - 3.1.0` (date it at release) |
| release-notes | Draft (add sha256/install verification at release) |
| Backend | v2.1.0 in development (recent commits: KB improvements/document capabilities); App `MIN_BACKEND_VERSION=2.1.0` awaits the backend release |
| Performance baseline / Baseline Profile | ready |

## 7. Pre-release decision items (non-blocking)

1. ~~**Document preview button 401** — hide the preview entry, or inject auth headers into
   `DocumentPreviewSheet` (preferred; high feature value).~~ — **resolved (2026-09-22)**: the auth
   headers are injected. `DocumentPreviewSheet`'s `WebViewClient.shouldInterceptRequest`
   (`DocumentPreviewSheet.kt:166-188`) rewrites same-origin requests through `previewClient` with
   `.header("Authorization", "Bearer $token")`, gated on `isSameOrigin(url, backendUrl)`, so the
   preview page and its internal `fetch` both carry the backend token. Same-origin validation is
   the exact scheme+host+port comparison fixed as H7.
2. ~~**KB delete collection/document UI** — add the UI (~1 screen + 2 dialogs), or retract the
   ROADMAP "delete" tick and mark it "Web management".~~ — **resolved (2026-09-22)**: the delete UI
   was added rather than retracting the tick. `KbScreen.kt:219` drives `DeleteCollectionDialog` →
   `KbViewModel.deleteCollection` → `BackendKbApi.deleteCollection`; `KbCollectionDetailScreen.kt:307`
   drives `DeleteDocumentDialog` → `KbCollectionDetailViewModel.deleteDocument` →
   `BackendKbApi.deleteDocument`. ROADMAP's "collection list / create / delete + document list"
   tick is therefore accurate.
3. **SECURITY_WARNING highlight dead code** — the backend fixes issue severity/featureId, or the
   app consumes `/api/intel/findings` (crosses the backend v2.1.0 contract; next iteration).
4. ~~Test Intelligence real-time behavior (auto run-status updates, retryable failures),
   gate/fix/chat notification outlets~~ — **addressed (2026-09-22)**: `IntelRunEventBus` merges
   runs in real time + retryable `runResultsFailed` + notification outlets for the five event
   types + `startRun` failure reasons, covered by unit tests (`TestIntelRunEventsTest`).
5. Alert severity tiering, history auto-refresh, threshold-0 semantics, server-deletion cleanup —
   UX polish.
