<!--
Copyright(c) 2016 - Present, Clouds Studio Holding Limited. All rights reserved.
-->

# Known Issues

Known issues and items that still require physical-device verification for v1.0.0.
Items are removed from this list once fixed and verified.

> **Device verification status: full verification passed on a Xiaomi 2308CPXD0C physical device
> (Android 16 / MIUI) on 2026-09-16.**
> The code fixes behind the items below are all in effect; no physical-device re-verification needed:
> - Terminal: extra-key row borders no longer overlap, AMOLED drawer inset closure, DEC cursor
>   escapes, resize dispatch ✅
> - Chat & rendering: code-block `=`/`-` highlighting, parallel subagent cards opening their child
>   session, paginated rendering of oversized messages without OOM, raw-value rendering of
>   `error.data`, question cards answerable in remote sessions ✅
> - Navigation & connection: share flow (plain text + files, always shows the target session
>   picker), long sessions show the newest 10 messages first with stable scrolling, notification/SSE
>   disconnect banners ✅
> - Settings sync: cross-device sync remains a known theoretical race/bandwidth limitation, not a
>   physical-device-verifiable item (no changes)

## Settings sync

- GitHub does not document atomic compare-and-swap support for Gist PATCH requests. Sync verifies
  the observed revision immediately before writing and verifies content afterward, but a concurrent
  Gist writer can still create a narrow lost-update race (`GistSyncTransport.kt`).
- Android document providers do not expose a portable atomic compare-and-swap operation. File-based
  settings sync hashes the full content, checks it immediately before writing, and verifies it after
  writing, but a concurrent writer can still create a narrow lost-update race. Persisted access and
  background operation also need verification with Google Drive and other providers
  (`DocumentSyncTransport.kt`).
- Settings sync omits per-server session category assignments, favorites, offline favorite
  snapshots, global favorite ordering, and hidden models in some upgrade paths. The payload now
  includes them and remaps remote server IDs to local IDs by normalized URL, but cross-device
  verification is still outstanding.

## Chat & rendering

- Syntax highlighting could apply an adjacent token color to `=` and `-` in fenced code blocks.
  These operators now inherit the base code color, but the rendering fix still needs physical-device
  verification (`SafeMarkdownHighlighting.kt`).
- Running custom subagent cards could not open their child session because a late
  `session.next.tool.called` or `session.next.tool.input.ended` event replaced `metadata.sessionId`
  received from `message.part.updated`. Tool lifecycle updates are now monotonic and preserve title
  and metadata, but parallel custom subagents still need physical-device verification
  (`EventReducerStreamingExt.kt`, `handleNextToolCalled` / `handleNextToolInputEnded`).
- A single message containing hundreds of megabytes of tool/file content could exhaust the Android
  heap while Ktor buffered and deserialized a history page. History loading now reduces the page size
  from the response `Content-Length` before reading its body, streams accepted pages from disk, and
  caches Base64 image data outside the deserialized model. The refined fallback and cached image
  rendering still need verification on oversized sessions (`StarBurstApi.kt`).
- Assistant error rendering crashed when a server returned `error.data` as a JSON primitive instead
  of an object. Error messages now accept object and primitive payloads, but the reported
  provider-error flow still needs physical-device verification (`Message.kt`).
- Question prompts cannot be answered from the Android UI in some remote-server sessions (#33),
  although the question card and matching server endpoints are present. The root cause still
  requires a diagnostic export captured immediately after reproduction.

## Navigation & connection

- The share-target session picker could close and immediately reopen repeatedly because pending
  attachments remained set until the destination chat consumed them. Automatic reopening is now
  limited to the explicit wait-for-server-connection flow and blocked once a target session is set,
  but the Android share flow still needs physical-device verification (`NavGraph.kt`).
- Opening a long session could block the chat until the complete configured history page had been
  transferred and processed. The newest 10 messages are now displayed first and the remainder is
  appended in background pages, but time-to-first-content and scroll stability still need
  verification on a slow remote connection (`ChatViewModelHistoryExt.kt`, `loadMessages` /
  `loadOlderMessages`).
- A chat opened from a notification or left open during an SSE disconnect did not show that its
  server was offline. The chat now observes the same connection state as Home and displays a
  persistent disconnected banner, but disconnect/reconnect transitions still need physical-device
  verification (`ChatScreenTopBar.kt`).

## Terminal

- Terminal extra-key row borders could overlap, the AMOLED drawer border stopped at system insets,
  and cursor visibility/blink/style escape sequences were ignored. Borders, DEC cursor behavior, and
  resize dispatch have been corrected, but the complete interaction still needs physical-device
  verification (`TerminalEmulator.kt`).

## Structure and line-count split (2026-09-19)

- To enforce the single-file ≤1000-line target / 1500-line hard cap, the following files were
  refactored using "whole-block moves + same-package extension functions / regional composables",
  with line-for-line conservation checks and all of `compileDebugKotlin`, `./gradlew test`, and
  `assembleDebug` green: the chat screen (message rendering, tool/file/image cards, terminal &
  extra-key row, context-usage/diff/template dialogs, input bar & @ file mentions), the Git
  screen's five dialogs and diff view, all settings dialogs, and the background connection's
  notifications and disconnect/reconnect (`ChatScreen*.kt`, `ChatInputBar.kt`, `ChatMessageBubble.kt`,
  `ChatDialogs.kt`, `ChatOverlayCards.kt`, `ChatTerminal.kt`, `GitScreen*.kt`,
  `SettingsScreen.kt`, `StarBurstConnectionService*.kt`, `EventReducer*.kt`,
  `ChatViewModel*Ext.kt`).
- 2026-09-20: JVM-testable pure-logic regression unit tests were added:
  `ChatInputBarDisplayTest` (16 cases: model-name truncation `displayModelLabel` incl.
  surrogate-pair safety, and the context budget ratio/percentage/alert-level thresholds
  `contextBudgetRatio` / `contextBudgetPercentage` / `contextBudgetLevel`) and
  `ContextBreakdownTest` (9 cases: `computeContextBreakdown` downward breakdown, OTHER fallback,
  tool input/output metering, zero-segment filtering).
- 2026-09-20: most of the split regression was verified on the dedicated AVD `starburst_test`
  headless emulator (Android 34): message rendering / file cards, terminal + extra keys, the
  `Context usage` dialog / `Quick templates`, the Git diff view, the settings main screen +
  language dialog, and background airplane-mode disconnect/reconnect
  (`Connected→Connecting…→Connected`); Compose instrumentation `ChatInputBarModelLabelTest`
  2 cases OK. See `docs/verification-checklist.md` for details.
- **Only physical-device in-use observation items remain** (the emulator has no real server
  connection/migration/weak network, so these cannot be verified in one pass): image cards,
  the @ mention overlay, each of the five Git dialogs, the remaining settings dialogs, the
  background connection notification, and the background-wakeup policy's real battery drain
  curve — tracked in the "In-use observation items" section of `docs/verification-checklist.md`.
- `scripts/check-file-size.sh` currently reports **13 Kotlin files over the 1000-line target** (none
  over the 1500-line hard cap, so the gate does not block). Actual counts as of 2026-10-02:
  `SessionListScreen.kt` (1446), `WorkbenchViewModel.kt` (1341), `TerminalEmulator.kt` (1278),
  `ChatInputBar.kt` (1272), `KbCollectionDetailScreen.kt` (1248), `StarBurstConnectionService.kt`
  (1231), `StarBurstConnectionServiceExt.kt` (1125), `SessionListViewModel.kt` (1120),
  `WorkbenchScreen.kt` (1111), `SettingsScreen.kt` (1094), `TestIntelProjectScreen.kt` (1015),
  `ServerProvidersScreen.kt` (1013), `ChatScreen.kt` (1004). This supersedes the earlier counts
  recorded for this section (`ChatInputBar.kt` 1070 / `SettingsScreen.kt` 1073 /
  `StarBurstConnectionService.kt` 1075, and a list that omitted the other over-target files).
  Splitting these further would require relaxing 30+ mutable fields (including 9
  `private set` properties such as `cursorRow`/`cursorCol`) to `internal`/`internal set`,
  splitting the giant composables into long-parameter-list regional functions, and then carving
  out notification/UI blocks — the benefit is lower than the regression risk, so this round
  skips it; `scripts/check-file-size.sh` has wired the 1500-line hard cap into CI to prevent
  further deterioration.

## Repository history note (2026-10-01)

- The private/special-use IP gate in `scripts/check-secrets.sh` now covers CGNAT `100.64/10` (plus
  link-local `169.254/16`) on top of RFC1918. Re-running it over the tree flagged one CGNAT
  test-server address (masked here as `100.66.1.x`; the literal is deliberately not restated, and
  is recoverable with `git show 2d5998b`). It is **not** owner infrastructure: it sits in shared
  carrier-grade NAT space and is not publicly routable.
- It remains in committed history. It was introduced in `2d5998b`
  (`app/src/test/kotlin/org/hiylo/starburst/data/api/ProviderCatalogFallbackTest.kt`) and last
  modified in `5fbb354`; both are ancestors of `main`, and the literal is present in the trees of
  the 17 commits from `2d5998b` through `1743e26`.
- The working tree is clean: no tracked file contains the address, that fixture now uses the
  RFC 5737 documentation range (`192.0.2.10`), and `scripts/check-secrets.sh --all` exits 0.
- **Decision: ACCEPT and document — no history rewrite.** The literal is not a credential and the
  address is not publicly routable, while rewriting shared history would invalidate every existing
  clone and fork. The gate above, not a rewrite, is the durable control.

