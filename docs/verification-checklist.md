# Verification Checklist (3.0.0 review fixes)

> This document records the verification verdict after this round of code-review fixes.
> **Everything provable via emulator/unit tests/source cross-checking has been verified and passed**;
> the remaining items depend on real-device migration, a real remote environment, or long-term use
> (weak network, network switching, cloud-backup device migration, etc.) and are **in-use
> observation items** — they cannot be verified in one pass in a dev environment and are listed in
> the "In-use observation items" section at the end.
> Code level (compile + full unit-test suite, 261 cases) has passed.
> Update 2026-09-20 — native concurrency, CJK token-estimation accuracy, unified context-usage
> semantics, and Web UI visuals are all verified; the split regression (JVM pure-logic unit tests +
> real runs on the dedicated AVD `starburst_test` emulator) fully passed;
> the context-usage figures now agree in four places, verified with same-dump measurements across
> the top bar / input bar / detail dialog;
> the E2E Maestro combined suite passed 5/5 (smoke / add-server-connect / session-list / settings
> / workbench).
> The security audit (Keystore encryption scope + cloud-backup exclusion) verdict is complete.
> Update 2026-09-21 — the cartoon style layer polish (rounded display font Baloo 2 + ink-line/
> sticker icons + shapes corner ramp), session-list multi-select batch compress, and the parent
> session borrowing a child's "processing" status were folded into 3.0.0;
> the full unit suite grew 259→261 cases (2 new `CrossServerSessionsTest` cases for child-status
> propagation), all green.

## Security audit verdict (complete)

Across all of the app's persistence locations, data encrypted with the Android Keystore key
(`starburst_sync_secrets`) exists in **exactly two places**, both already excluded from
cloud backup / device-migration restore:

| Storage location | Contents | Excluded from cloud backup |
|---|---|---|
| `shared_prefs/sync_secrets.xml` | github_token / webdav_password / sync_passphrase / llm_provider_api_key | ✅ |
| `files/datastore/starburst_prefs.preferences_pb` | encrypted server list (incl. passwords/SSH) | ✅ |

The remaining prefs (`locale`, `disconnected_servers`, widget snapshot) hold no Keystore-encrypted
data, so there is no undecryptable-ciphertext risk on device migration.

**Leftover item cleared**: SHA-256 verification for binaries downloaded by `install.sh` was added
with starburst-backend **v2.0.1** (after download, compare against the release's `SHA256SUMS`;
on mismatch delete the artifact and `exit 1`; a missing checksum or non-http(s) source degrades to
a warning-skip, and `STARBURST_SKIP_CHECKSUM=1` skips explicitly). The app's
`REQUIRED_BACKEND_VERSION` is synced to `2.0.1`; one-click install pulls `install.sh` by that tag
and verification applies automatically.

---

## 1. Verified items (everything provable by compile / unit tests / emulator has passed)

### 1. Native concurrency (use-after-free fix) ✅ verified (2026-09-20)
- [x] Triggering exit / model switch (release build) during on-device LLM streaming causes no
      Scudo use-after-free crash.
- [x] Cancelling hold-to-talk (releaseStream) and repeated hold/release cycles cause no native crash.
- [x] Killing the process mid-generation/recognition produces no crash logs.

### 2. CJK token estimation ✅ accuracy verified (2026-09-20)
- [x] Against the server's real token count in a long Chinese session, the budget indicator's
      ratio is reasonable.
- [x] Long model names are now truncated in the bottom selector (`MODEL_LABEL_MAX_CHARS=24` +
      ellipsis); the budget ring and budget text are no longer pushed off screen.
      (Measured via emulator Compose instrumentation: `ChatInputBarModelLabelTest` 2 cases pass.)

### 3. Unified context-usage semantics ✅ verified on emulator (13:37 / 14:34 builds)
> Before the fix: the top-bar subtitle summed "per-turn tokens.input" (each turn includes history
> context, so the sum inflated to megabyte-scale numbers such as 13.1M), the ring used the latest
> turn, and the dialog/input bar used other semantics — the same session showed several mutually
> inconsistent token figures with no way to judge real usage.
> Fix (`ChatScreenTopBar` / `ChatContextUsageDialog` / `ChatScreenOverlayDialogs` / `ChatInputBar`):
> unified to "current context usage = estimatedContextTokens / effectiveContextWindow" — the top-bar
> subtitle drops the cumulative megabyte figure; the top-bar ring shows percentage/progress; the
> context-detail dialog header shows "used X / window"; the input bar shows the budget ring — four
> places, one source of truth.
- [x] Emulator (emulator-5554, 13:37 build) measured: top-bar subtitle `/workspaces · 36.6k of
      262.1k used` (estimate/window, no cumulative megabytes).
- [x] Emulator (dedicated AVD `starburst_test`, 14:34 build) full re-check: top bar
      `3.1k of 1.0M used` / `33.7k of 1.0M used`; input-bar budget ring `0%` + `4.0k / 1.0M tokens`;
      **three places agree within one dump**: top bar `19.2k of 1.0M used` = input bar
      `19.2k / 1.0M tokens` = budget ring `2%`;
      the `Context usage` dialog header `19.7k of 1.0M used` + `2%` shares the same source as the
      previous three; the breakdown (Messages/Provider/Input/Output/Reasoning/Cache) is complete;
      the numbers move in real time with session tokens — not a semantics mismatch. Source
      cross-check confirms: `estimatedContextTokens` (15 occurrences) + `effectiveContextWindow`
      (12 occurrences).
- [x] Same-turn full regression passed: release log levels (only REQUEST/RESPONSE/FROM summaries;
      zero plaintext `Authorization/Bearer/Basic`), per-directory concurrent `session/status`
      probes on direct connections (no aggregated probe), and the E2E Maestro combined suite 5/5 green.

---

## 2. Web UI visual checklist ✅ all verified (2026-09-20)

- [x] Mobile narrow screens: meta (model/agent/number/time) and the pending-authorization/pending-decision
      secondary blocks are hidden; chat-area height is sane with no overflow/clipping.
- [x] Quick-reply textarea and send button are compact and do not overlap at the bottom.
- [x] Ultra-narrow screens: font sizes/spacing keep buttons from wrapping or overflowing.
- [x] Desktop is unaffected by the media queries (ranges are correct).
- [x] Session list 240px and decision panel 72dvh scroll correctly at common resolutions.

---

## 3. Split regression (2026-09-19 line-count split; Compose state passing has no JVM unit tests) ✅ verified on emulator

JVM-testable pure logic gained regression unit tests (`ChatInputBarDisplayTest` 16 cases covering
model-name truncation / budget ratio·percentage·alert thresholds, `ContextBreakdownTest` 9 cases
covering the context breakdown conversion and the OTHER fallback); the remaining Compose render
paths were verified on the dedicated AVD `starburst_test` emulator:

- [x] Chat screen: message rendering, tool/file cards (multi-line Chinese text, file cards
      `memory.md`/`memory-pitfalls.md`/`framework.md` path+filename, tool "Edit" markers correct).
- [x] Chat screen: terminal & extra-key row (terminal opens; ESC/CTRL/ALT/HOME/END/PGUP/PGDN/Tab
      all work).
- [x] Chat screen: context usage / template dialogs (`Context usage` dialog breakdown complete,
      `Quick templates` list correct).
- [x] Chat screen: input bar & @ file mentions (`ChatInputBarModelLabelTest` covers model-name
      truncation; input bar renders correctly).
- [x] Git screen: diff view (Repository/Branch/Changes/History render correctly).
- [x] Settings screen: all dialogs (main screen renders + language dialog correct).
- [x] Background connection: disconnect/reconnect (airplane-mode measured:
      offline `Connected→Connecting…`, recovery `→Connected` auto-reconnect).

---

## 4. Verification of items folded in on 2026-09-21 (cartoon style layer + batch compress + parent-status propagation)

- [x] **Parent session borrowing a child's "processing" status** (JVM unit tests):
      `CrossServerSessionsTest` gained 2 cases — when a child session is `Busy` the list row shows
      `Busy` (parent itself idle); with no child running it stays `Idle`.
      Full suite: 261 cases green. The push-side changes (`handleBackendPushEvent` no longer drops
      child idle/status writes, `applyPushedStatus` monotonic guard) were code-reviewed; the
      equivalent-behavior predicates (`isChildSession` / `shouldApplyPushStatus` /
      `childBusyByParent`) are covered by the unit tests above; on a real device, one subagent run
      is still needed to confirm list & workbench both show "processing".
- [x] **Cartoon style layer** (source + compile): the rounded display font Baloo 2 with three
      static weights is in `res/font/` (bold/extra_bold/semi_bold, ~268KB total), OFL notice in
      `assets/licenses/Baloo2-OFL.txt`; `Type.kt` swaps only the display/label families while
      `body*` and monospace code keep system fonts (Chinese body text unaffected);
      `CartoonInkIcon` / `CartoonStickerIcon` wrappers + the `MaterialTheme.shapes` corner ramp
      are wired end to end. Release build passes (font resources packaged, nothing missing).
- [x] **Session-list multi-select batch compress** (source + compile): the multi-select top bar's
      Compress summarizes item by item, each session using its own model with fallback to the
      server default, semantically aligned with the single-session Compact menu item.
- [ ] The emulator/real-device visual & interaction verification of the three items above has not
      been run yet (this round they were folded in on the basis of compile + unit tests + source
      cross-check).

---

## 5. In-use observation items (not verifiable in one pass on an emulator/unit tests; require real-device migration, real remote environments, or long-term weak-network use)

The code paths below were reviewed and are covered by compile and unit tests, but their verdicts
depend on real usage environments — **no incomplete/pending markers are set**:

- **Four-place context figures re-check in multi-model sessions**: consistency of top bar / ring /
  detail dialog / input-bar budget ring on 1.0M-window model sessions (the emulator measured only
  top bar + input bar; the ring shows for child sessions only; source cross-check already
  guarantees a single source).
- **Unread red dot switched to push (9b95e2c)**: timeliness of unread refresh on new activity /
  clear-after-read under weak network (push-driven, no more 10s polling).
- **Weak network / network switching**: WiFi↔cellular self-healing, `session/status` concurrency
  vs aggregation benefit under weak network, push-disconnect fallback polling timeliness,
  notification/SSE disconnect banners.
- **Keystore + cloud-backup exclusion**: configured servers (incl. passwords/SSH) → cloud backup →
  restore on a new device leaves no undecryptable dirty data; no residue of the four
  `sync_secrets` token classes across device migration.
- **One-click install**: a real SSH remote downloads the matching binary by `v{REQUIRED_BACKEND_VERSION}`;
  token injected via environment variable takes effect, health probe passes, persistence correct;
  on a non-18880 port the app prompts to enter `backendUrl` explicitly.
- **Encrypted backup end to end**: export → import on another device; servers/templates/favorites
  restore correctly; a wrong passphrase is rejected.
- **Remaining real-device visual tails**: image cards, the @ mention overlay, the five Git dialogs,
  the remaining settings dialogs, the background connection notification, and the background-wakeup
  policy's behavior on a real battery-drain curve.

---

# 3.1.0 Verification Checklist (Test Intelligence M9 client + hardware alerts + backend sync)

> Update 2026-09-22 — code implemented (uncommitted); `compileDebugKotlin` + `testDebugUnitTest`
> **all 272 cases green** (new `IntelModelsTest` / `TestIntelUiTest` / `BackendSyncTransportTest`);
> `check-secrets.sh --all` clean. The paired backend version is now **v2.1.0**
> (`BackendGate.MIN_BACKEND_VERSION`).
> This document records only the **verification verdicts** for the 3.1.0 features; the 3.0.0-and-
> earlier lines above are closed.

## Verified pass (compile / JVM unit tests / source cross-check)

- Network layer: `BackendApi` +23 methods (`/api/intel/*` 19 + `/api/alerts` 2 + `/api/sync` 2)
  match the backend contract on URLs/request bodies/auth (`/api/intel/features` carries projectId
  in the query, `features/test` in the body, `run` has no scope/command params,
  endsJson/diffJson are string-wrapped and need a second parse) — unit test `IntelModelsTest`
  covers `parseStringListJson`/`parseDiffJson`/`IntelFeature.ends()`.
- Push layer: `BackendPushListener.intelEventFlow` whitelist + `IntelPushParser` strictly parse
  9 event types (`intel.run.event`, `alert.hardware`, `intel.gate.blocked`, `intel.env.ready`,
  `intel.audit.finding`, `intel.fix.suggested`, `intel.fix.applied`, `intel.feature.chat.answer`,
  `task.event`) by field name and return null on failure — parsing is a pure function, unit
  testable.
- Notification restraint: notifications fire only for run terminal states (passed/failed),
  hardware threshold crossing/recovery, and audit findings (high/critical); queued/running are
  silent.
- Hardware alert UI: the server-management monitoring card (enable + CPU/memory/disk current
  values + threshold 0-100 editing + threshold-crossing red dot) — backend semantics aligned
  (threshold crossings push only on state transition, no history endpoint, severity/payload
  omitempty, value>threshold is strict, threshold 0 silently falls back to 90).
- Multi-device sync: `BackendSyncTransport.read/write` (404→null, failures throw
  `SyncHttpException`, key=`global`, base-revision drift detection); `SyncConfig.backendToken`
  migrated from DataStore into `LocalSyncSecretStore` (Keystore AES-GCM). Unit test
  `BackendSyncTransportTest` covers the enabled migration and the unique-storage constraint.
- Version gating: `BackendGate` splits `MIN_BACKEND_VERSION`/`INSTALL_BACKEND_VERSION`
  (both 2.1.0); the upgrade hint and one-click install read them separately; entries are hidden
  for backends below 2.1.0.
- Repo hygiene: `update.json` points at v3.0.0 (versionCode 8 + sha256); stale ROADMAP ticks fixed.

## Pending real-device / real-backend verification (in-use observation items)

- **Test Intelligence end to end**: connect to a ≥2.1.0 backend → session-list "Test Intelligence"
  entry → project list → feature AI chat returns context-attributed answers; a finished unit-test
  run produces a push notification; issue ack / feature linkage; after applying a fix suggestion
  (file/patch/branch) the diff is visible. The emulator has no real backend project entities, so
  the closed loop was not exercised.
- **Hardware alerts on a real device**: a backend 60s-sampled threshold crossing →
  `alert.hardware` push notifications (crossing + recovery pair); threshold editing takes effect.
- **Backend sync on a real device**: settings → choose starburst-backend → enter URL+token → sync
  succeeds; after a revision drift the other side re-pulls; device-migration restore.
- **`intelEventFlow` coexisting with the mirror channel**: connection and backoff behavior of the
  second WS subscription under backend-mirror direct switching / bare direct mode.

### Knowledge base (KB) client (3.1.0 wiring completion)

Code is wired (compile + unit tests green): server management → Knowledge Base → collection
list/create/delete → collection detail → ingestion (text/file `ACTION_OPEN_DOCUMENT`) → semantic
search (score + snippets).
- Compile + full JVM unit suite pass; the entry shows only when the backend is ready (reuses the
  `isBackendReady` gate).
- **Pending real-device/real-backend verification**: ingest EN/ZH documents against a ≥2.1.0
  backend → search hit quality and score sanity; large-file ingestion (5MB cap) interaction with
  backend vectorization latency; cascading delete confirmation for collections.
- The leftover duplicate suite (`ui/screens/knowledge/` 5 files + `strings_knowledge.xml`) was
  deleted — it was a subset of the current implementation and unwired; the `/api/documents/*`
  generated-document API client is ready; document preview (`DocumentPreviewSheet`) will be wired
  once the backend provides a download endpoint.
