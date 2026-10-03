# 3.1.0 Code Audit Report (versions after 2.X)

> Audit scope: `git diff v2.0.0..HEAD` (all App 3.0.0 + 3.1.0 changes, 264 files / 48,828 added lines).
> The deep review focused on what is new in 3.1.0 (`v3.0.0..HEAD`, 71 files / 20,640 lines, of which
> baseline-prof.txt's 12,423 lines are generated artifacts).
> Method: mechanical convention scan (whole repo) + `lintDebug` static analysis + 3 parallel
> deep-review subagents (test intelligence / alerts·sync·KB·docs / service layer·navigation·gating).
> Date: 2026-09-22.

## 1. Mechanical Scan Results (all pass)

Scanned scope: the whole `app/src/main` tree (the "whole repo" convention scan), i.e. **not** limited
to the `v2.0.0..HEAD` diff above.

| Item | Result |
|---|---|
| Single file >1500 lines | none |
| File-header Copyright | all present (some `Date` fields lack `HH:mm:ss`, see Low) |
| Banned annotations (@Tag/@Operation/@Schema/@Api/Knife4j/Swagger/SpringDoc/@SuppressFBWarnings) | none |
| EN/ZH strings key alignment | fully aligned (0 diffs) |
| TODO/FIXME/HACK | 1 in `app/src/main`: `SharedSessionRepository.kt:42` — a `TODO(UI 接入点)` noting the share-entry UI is not wired yet. Pre-existing (introduced in 1.4.0, `c7bf098`), so it is outside the `v2.0.0..HEAD` diff but inside the whole-repo scan. |
| `check-secrets.sh --all` | clean |
| `lintDebug` | no errors (BUILD SUCCESSFUL) |

## 2. High Severity (must fix before release)

- **H1 Knowledge-base entry always crashes** — `NavGraphSessions.kt:331/481` navigates to
  `Screen.KnowledgeBase` (route `knowledge_base`), but that route has **no NavGraph registration
  at all** (`NavGraphKb.kt` registers only `kb`/`kb_collection`); once the backend is ready,
  tapping the session list's knowledge-base icon always throws `IllegalArgumentException`.
  Fix: route the entry through `Screen.Kb.createRoute(...)` and delete the orphan
  `KnowledgeBase`/`KnowledgeBaseCollection` in `Screen.kt`.
- **H2 Intel push permanently dead after SSH tunnel rebuild** — `intelPushJob` is started only
  once in `connectInternal` (`:544`); when `rebuildConnection` (`:939`)/`replaceSshSession`
  (`:790`) rebuild the tunnel (`setPortForwardingL(0,…)` picks a random port each time), only
  `pushJob` is restarted, not `intelPushJob`, which keeps subscribing to the old
  `127.0.0.1:<old port>`; after a network switch or zombie-connection recovery,
  `intel.*`/`alert.hardware` notifications silently stop while the idle job lingers.
  Fix: on rebuild, cancel and restart it against the new `backendLocalPort`.
- **H3 Audit findings at high severity never notify** — `StarBurstConnectionServiceGatewayExt.kt`
  filters with `severity in {"warning","critical"}`, but the backend `finding.Severity` values
  are `critical|high|medium|low|info` (there is no `warning`). Fix: change to
  `severity in setOf("high","critical")`.
- **H4 `disconnect()` wrongly wipes all credentials** — `SyncRepository.kt:389` calls
  `secretStore.clearAll()`, which also clears the sync-unrelated `LLM_PROVIDER_API_KEY` and
  `SFTP_PASSWORD`. Fix: disconnect clears only the four sync-related keys
  `GITHUB_TOKEN/WEBDAV_PASSWORD/SYNC_PASSPHRASE/BACKEND_TOKEN`.
- **H5 KB file ingestion OOM** — `KbCollectionDetailViewModel`'s `readTextFile` does `readBytes()`
  (a full read into memory) before checking the 5MB cap. Fix: pre-check with
  `OpenableColumns.SIZE` + streamed truncation.
- **H6 Alert history dual-path duplicate writes** — the push path (`GatewayExt`) and the 60s poll
  (`reconcileAlerts`) do not deduplicate; the same threshold-crossing event can be stored twice.
  Fix: reconcile checks for duplicates within a time window before persisting, or the push path
  updates the snapshot baseline synchronously.
- **H7 Document preview/download security** — `DocumentPreviewSheet` uses a
  `requestUrl.startsWith(backendUrl)` allowlist (prefix bypass); `BackendDocumentsApi.resolveDocumentUrl`
  passes absolute URLs through unchanged, so `downloadDocument` would attach the Bearer token to
  any third-party host. Fix: parse host+port and compare exactly; reject cross-origin or strip
  Authorization.

## 3. Medium

- The three new Intel/alert/audit notifications ignore `notificationsEnabled`/`silentNotifications`
  (they ring even when the user disabled or silenced notifications).
- The three notifications' title/body are hardcoded Chinese (not routed through strings.xml).
- notifId is not isolated per server (`100_000+run.id` etc.); servers overwrite each other;
  `abs(Int.MIN_VALUE)` is still negative.
- `createNotificationChannels` deletes and recreates channels on every process start
  (`BuildConfig.VERSION_CODE>=1` is always true, no persisted marker), resetting user channel
  customization.
- `intelPushJob` starts inside `connections.compute` before the state enters the map — a very
  small race window.
- A failed run-result load is permanently cached as empty (`loadRunResults` writes `emptyList`
  on failure; the idempotency guard makes retry impossible).
- `task.failure` parsing contract mismatch (the backend has no `status` field; the event is
  silently dropped; no functional impact today).
- `TestIntelProjectViewModel` silently falls back `projectId` to 0, with no UI error explanation.
- Notification PendingIntents point at the bare MainActivity; cannot deep-link to run/project/collection.
- `ServerManagementViewModel.refresh()` does not reload alert history; after `reconcileAlerts`
  the list is not refreshed.

## 4. Low

- Unused imports (ServerManagementViewModel/ServerManagementScreen/SyncSettingsScreen/DocumentGenerateDialog/ChatInputBar
  etc., 6 places).
- File headers `Date : 2026/09/22` lack `HH:mm:ss` and lack a `Contact` line
  (BackendAlerts/BackendSync/AlertHistoryRepository/Kb*/NavGraphIntel/NavGraphKb etc.).
- Line width over 120 (NavGraphIntel.kt:30/62, BackendPushListener.kt:168/171,
  StarBurstConnectionServiceGatewayExt.kt:290 etc.).
- `DocumentPreviewSheet` hardcodes Chinese literals ("Document Preview" / "Failed to load preview page");
  WebView `databaseEnabled/allowContentAccess=true` are unnecessary; closed without `destroy()`.
- Alert/audit notifications use `VISIBILITY_PUBLIC`; the body contains sensitive info such as file
  paths, visible on the lock screen.
- Accessibility: project cards / expandable rows use bare `clickable` without role/contentDescription.
- `feature.ends()` re-parses JSON on every recomposition (should be `remember`-ed).
- `test_intel_feature_security` and other resources unused; `BackendApi.intelRunDetail`/`featureRunTest`
  have no callers (dead code).
- Alert history `onUpgrade` drops the table outright; deleting a server does not clear
  `AlertHistoryRepository.clear(serverId)`.
- After triggering one channel rebuild, nothing is persisted (same root cause as H11, listed under Medium).

## 5. Conclusion

Overall the 3.1.0 code quality is high: exception isolation, token masking, concurrency cleanup,
safe parse degradation, and EN/ZH resource alignment are all solid. **Before release, H1
(guaranteed crash), H2/H3 (silent functional failure), H4 (data loss), H5 (OOM) and H7 (security)
must be fixed**; H6/H8/H11 and the medium/low items can be handled in later patch releases.

## 6. Fix Status (2026-09-22, compiled + all 272 unit tests green + check-secrets clean)

Fixed:
- H1 ✅ the session-list knowledge-base entry now goes through the registered `Screen.Kb`;
  the orphan `Screen.KnowledgeBase`/`KnowledgeBaseCollection` routes were removed
- H2 ✅ `rebuildConnection`/`replaceSshSession` now also restart `intelPushJob` when rebuilding
  the tunnel (against the new backendLocalPort)
- H3 ✅ audit-finding notification filter changed to `severity in {high, critical}`
- H4 ✅ `LocalSyncSecretStore` gained `clearSyncSecrets()` (clears only the 4 sync credentials);
  `disconnect()` now calls it
- H5 ✅ KB ingestion: `OpenableColumns.SIZE` pre-check + 8KB streaming capped at 5MB
- H6 ✅ `reconcileAlerts` dedupes by metric+state within a 5-minute window before persisting
- H7 ✅ document preview/download now uses exact scheme+host+port same-origin validation;
  WebView `databaseEnabled`/`allowContentAccess` removed; DisposableEffect destroy
- H8 ✅ the three new notifications honor `notificationsEnabled`/`silentNotifications`
  (silent uses TASKS_SILENT)
- H9 ✅ notification copy moved into strings.xml (EN/ZH, 4 new keys + reuse of existing ones)
- H10 ✅ notifId salted with serverId + `and Int.MAX_VALUE` to avoid negative values
- H11 ✅ channel migration uses a SharedPreferences marker `notif_channels_migrated_v1`,
  rebuilt only on first run

Remaining (low, suggested for later patch releases): unified file-header `Date`/`Contact`,
line width >120, unused resources/dead code, accessibility role/contentDescription, notification
PendingIntent deep links, lock-screen visibility, empty cache for failed `run` results,
the `task.failure` contract, and alert history `onUpgrade` DROP table.
