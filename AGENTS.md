<!--
Copyright(c) 2016 - Present, Clouds Studio Holding Limited. All rights reserved.
-->

# AGENTS.md — StarBurst Android Project Conventions

## Project Overview

StarBurst — a native Android client for an AI coding assistant (Material 3 + Jetpack Compose).
It connects to any server speaking the OpenCode-compatible V2 protocol for streaming chat,
file browsing/editing, a built-in Git screen, a PTY terminal, session management, on-device
LLM suggestions, on-device speech recognition, and a test-intelligence workbench.

- **namespace / applicationId**: `org.hiylo.starburst` (debug suffix `.debug`)
- **versionCode / versionName**: 8 / 3.0.0
- **minSdk / targetSdk / compileSdk**: 26 / 34 / 34

## Tech Stack

| Layer | Choice |
|---|---|
| Build | Gradle 8.6 + AGP 8.4.0 + Kotlin 2.0.21 (incl. compose/serialization plugins) |
| JDK | **JDK 17** (hard constraint; Gradle 8.6 fails outright on JDK 25) |
| UI | Jetpack Compose BOM 2024.12.01, Material3, navigation-compose, Coil, Accompanist |
| DI | Hilt 2.52 (kapt) |
| Networking | Ktor 2.3.11 (OkHttp + WebSockets + SSE streaming) |
| On-device ML | MNN (libMNN.so + OpenCV + Vulkan) + Qwen3.5-0.8B on-device LLM |
| On-device speech | sherpa-mnn streaming Zipformer, bilingual |
| Testing | JUnit 4.13.2 + Espresso 3.6.1 + Compose ui-test-junit4; E2E via **Maestro** |

`gradle.properties` already contains the workspace hard requirements:
`org.gradle.daemon=false`, `kotlin.compiler.execution.strategy=in-process`.

## Deployment and Send Protocol

Sending is **not** split by build type. `sendPrompt` uses one unified path for every build variant
(`data/api/StarBurstApiMessages.kt`): V2 acknowledged delivery — switch agent/model only when the
target differs, then `POST /api/session/{id}/prompt` with `resume=true` and `delivery=queue`.

- The **starburst-agent** server speaks the V2 `api` namespace, so delivery succeeds directly
  (tool calls and permission gating included).
- The **original backend** (opencode 1.18.30 + starburst-backend) has no usable V2 send path: tool
  calls fail server-side with `Failed to drain Session` and `queue` never starts generation. Its V2
  endpoints answer **404/405**, and only those two statuses trigger the fallback to V1
  `prompt_async`. Every other status (400/500/…) is a real rejection and is rethrown unchanged, so
  the V1 fallback cannot mask the cause (quota/parameters/session state).
- `[system]` / `[tools]` take effect only on V1 and are ignored by the V2 contract, so they are no
  longer a reason to route via V1.

There is **no `BuildConfig.DEBUG` gate in the send path**. Read the comments on `sendPrompt` and
`buildV2PromptRequest` before changing the send protocol; they document the delivery/resume
contract.
- Product APKs land in `/opt/starburst-apk/download/`; intranet download via
  `http://192.0.2.150:18882/<filename>`.

## Architecture / Package Structure

Modules: `:app` + `:baselineprofile`. 337 .kt source files, 149 of them under ui.

```
app/src/main/kotlin/org/hiylo/starburst/
├── MainActivity.kt / StarBurstApp.kt
├── ui/        (149)  screens/ components/ navigation/ theme/
├── data/      (57)   api/ backend/ repository/ search/ shell/ sync/ backup/
├── domain/    (15)
├── service/   (7)    StarBurstConnectionService et al.
├── ml/        (6)
├── widget/    (4)
├── di/        (2)
└── logging/   (1)
```

> This project is Android MVVM with hand-written repositories; the global Java layering
> convention (controller/service/entity/dto/vo) has no target in this project.

## Security Red Lines (self-check before commit / push)

1. **Never commit the following** (writing any of these into a commit is an incident
   requiring history rewrite):
   - Intranet/private/special-use IPs (`10.x`, `172.16-31.x`, `192.168.x`, CGNAT `100.64/10`, link-local `169.254.x`) and intranet domain names
   - Plaintext passwords, API keys, tokens (GitLab PAT, GitHub PAT, OpenAI `sk-`,
     AWS `AKIA`, etc.)
   - Private keys (`*.pem`, `id_rsa`, keystores, `.jks`), `.env`, credentials embedded in
     connection strings (`user:pass@host`)
2. **Use RFC 5737 documentation ranges for address placeholders**: `192.0.2.x` /
   `198.51.100.x` / `203.0.113.x` (e.g. `http://192.0.2.150:18882/<filename>`), or a
   `<host>:<port>` variable form
3. **git remotes must not embed tokens in URLs**; use SSH or a git credential helper instead
4. **Local commits**: a pre-commit hook is provided; after a fresh clone run
   `git config core.hooksPath githooks` once, and every commit afterwards automatically
   runs `scripts/check-secrets.sh`
5. **Before pushing to GitHub**: manually run `scripts/check-secrets.sh --all` to re-check
   the whole repo; CI additionally has `.github/workflows/secret-scan.yml` (gitleaks)
   as a safety net
6. If a finding is confirmed as a false positive and must be allowed through: first fix it
   into a compliant form; only when truly unavoidable use `git commit --no-verify` and state
   the reason in the commit message

## Build & Test

```bash
# Build
./gradlew assembleDebug                     # debug
./gradlew assembleRelease                   # release (needs app/keystore/signing.properties)

# Test
./gradlew test                              # JVM unit tests
./gradlew test --no-daemon --stacktrace     # CI style (JDK must be 17)

# E2E (Maestro)
./scripts/run-e2e.sh                        # runs e2e/smoke.yml etc.

# Line-count gate
./scripts/check-file-size.sh                # target 1000 / hard cap 1500

# Performance baseline
./scripts/measure-startup.sh
```

### E2E Conventions
- Dedicated AVD `ocb_e2e`, port 5566, `JAVA_HOME_JDK17=/usr/lib/jvm/dragonwell-17`
- Artifacts land in `/opt/test-artifacts/opencode/e2e/<timestamp>/`
- Maestro logs are `tee`-ed to `/opt/test-artifacts/opencode/logs/maestro/`
- **Each emulator run uses a fresh instance, shut down after use**
  (`pkill -f qemu-system-x86_64`)

## Git Conventions

- Commit messages: `{type}({scope}): {subject}`; type is one of
  `feat`/`fix`/`refactor`/`docs`/`chore`/`test`
- Remotes: a single `origin` over SSH — `git@github.com:NightSommelier/starburst.git`
  > The security red line "no intranet IPs" targets **repository content**
  > (code/config/docs); local git remote configuration is not part of the repository
  > and may use real intranet addresses.

## CI

- `.github/workflows/ci.yml`: unit-tests (JDK 17 + line-count gate + `./gradlew test`).
  `push` carries a `paths-ignore` for `docs/**`, `**.md`, `release-notes/**`; `pull_request` has
  **no** paths filter on purpose, so a docs-only PR still reports the required
  "JVM unit tests" check and branch protection cannot deadlock.
- `.github/workflows/release.yml`: extracts the version from `build.gradle.kts`,
  validates `update.json`, signs with the GitHub Secrets keystore. Also `paths-ignore`s
  `docs/**`, `**.md`, `release-notes/**`.
- `.github/workflows/secret-scan.yml`: gitleaks **plus** a `bash scripts/check-secrets.sh --all`
  step (the repo's own rule set for private/CGNAT/link-local IPs and URL-embedded credentials,
  honouring RFC 5737 documentation ranges). Runs on every `push` and `pull_request` with no
  paths filter.

## Documentation Index

Every English document has a Ukrainian `_UK` twin; the twin path is in the last column. `*_UK`
files are the canonical Ukrainian sources for their English counterparts.

| File | Description | Ukrainian twin |
|------|-------------|----------------|
| `README.md` | Project overview and build/install instructions | `README_UK.md` |
| `ROADMAP.md` | Roadmap and per-version feature ticks | `ROADMAP_UK.md` |
| `DESIGN.md` / `DESIGN.zh-CN.md` | Design spec (EN / zh-CN) | `DESIGN_UK.md` |
| `CHANGELOG.md` | Change log | `CHANGELOG_UK.md` |
| `KNOWN_ISSUES.md` | Known issues and outstanding device verification | `KNOWN_ISSUES_UK.md` |
| `SECURITY.md` | Security notes and supported versions | `SECURITY_UK.md` |
| `CONTRIBUTING.md` | Contribution and commit conventions | — |
| `CODE_OF_CONDUCT.md` | Code of conduct | — |
| `TRADEMARKS.md` | Trademark statement | — |
| `docs/code-audit-3.1.0.md` | 3.1.0 code audit report | `docs/code-audit-3.1.0_UK.md` |
| `docs/release-readiness-audit-3.1.0.md` | Release-readiness audit | `docs/release-readiness-audit-3.1.0_UK.md` |
| `docs/verification-checklist.md` | 3.0.0 review-fix verification checklist | `docs/verification-checklist_UK.md` |
| `docs/performance-baseline.md` | Cold start / memory baseline | `docs/performance-baseline_UK.md` |
| `docs/screenshots/` | UI screenshots | — |
| `release-notes/` | Per-release notes, 1.0.0 → 3.1.0 (8 releases; each also has a `_UK` twin) | `release-notes/*_UK.md` |
