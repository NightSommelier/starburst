# StarBurst Performance Baseline

> Records measured startup-time and memory baselines as reference points for future optimization
> and regression comparison.
> Re-measurement commands and methodology: see `scripts/measure-startup.sh`. **Data varies by
> environment; register a fresh entry in this document first.**

## Measurement environment (2026-09-22)

| Item | Value |
|---|---|
| Build | `app-debug.apk` (48,856,369 B), unobfuscated, no Baseline Profile |
| AVD | `starburst_test` (x86_64 / Android 14 / API 34, `-no-window -no-snapshot -gpu swiftshader_indirect -memory 4096`) |
| Rendering | swiftshader (software rendering; higher in-window frame times than real hardware are normal) |
| Collection | `adb shell am start -W` `TotalTime`; 5 cold starts (each preceded by `force-stop`); memory via `dumpsys meminfo` after 4s of stable foreground |

## Cold start (TotalTime)

| Stat | Value |
|---|---|
| median | **1054 ms** |
| avg | 1048 ms |
| raw | 1058 / 1052 / 1021 / 1054 / 1054 ms |

## Memory (stable foreground state)

| Metric | Value |
|---|---|
| TOTAL PSS | **50,662 KB** (~49.5 MiB) |
| TOTAL RSS | 177,208 KB (~173 MiB) |

## Methodology notes and caveats

- The figures above are **debug + software-rendered emulator** data, for relative regression only;
  release / real-device data will be notably lower and must be measured separately.
- Startup time includes `MainActivity` cold start (Compose init, Hilt, background
  `StarBurstConnectionService` start-up); debug-disabled state adds more overhead.
- The memory baseline does not include on-device MNN model loading (RSS rises significantly after
  the ASR/completion models load on demand — expected).

## Re-measuring

```bash
# After connecting a device/emulator:
APK=app/build/outputs/apk/debug/app-debug.apk bash scripts/measure-startup.sh 5
```

## Baseline Profile (introduced 2026-09-22)

- Dependency `androidx.profileinstaller:profileinstaller:1.3.1` (applies the AOT profile at install time).
- New `:baselineprofile` module (`com.android.test` + `androidx.baselineprofile` 1.3.1 + managed device `pixel2Api34`).
- Artifact `app/src/main/baseline-prof.txt` (12,423 lines; cold start to main screen hot path);
  release merged ART profile confirmed wired in: 10,596 → 17,908 lines.
- Generation: `./gradlew :baselineprofile:connectedNonMinifiedReleaseAndroidTest` then
  `./gradlew :baselineprofile:collectNonMinifiedReleaseBaselineProfile`; the artifact lands in
  `baselineprofile/build/outputs/connected_android_test_additional_output/.../BaselineProfileGenerator_generate-baseline-prof.txt`;
  copy it to `app/src/main/baseline-prof.txt`.

## Real-device baseline (2026-09-27, release build)

> Adds the previously missing "release + real device" data point. The AVD numbers above are
> debug + swiftshader software rendering, for relative regression only — do not compare them
> horizontally against this section.

### Measurement environment

| Item | Value |
|---|---|
| Build | `starburst-release-3.0.0-v2fix75.apk` (28,165,139 B), signed release |
| Device | Xiaomi 2308CPXD0C (Android 16 / MIUI, USB debugging) |
| Collection | `scripts/measure-startup.sh 5` (each run: `force-stop` then `am start -W` → `TotalTime`); memory via `dumpsys meminfo` after 4s of stable foreground |

### Cold start (TotalTime)

| Stat | Value |
|---|---|
| median | **272 ms** |
| avg | 297 ms |
| raw | 405 / 250 / 268 / 272 / 288 ms |

Compared with the AVD debug 1054 ms this is ~3.9× faster — the gap comes mainly from real
hardware and GPU acceleration; **do not infer release-vs-debug gains from it** (the build types
and rendering paths differ).

### Memory (stable foreground state)

| Metric | Value |
|---|---|
| TOTAL PSS | **52,323 KB** (~51.1 MiB) |
| TOTAL RSS | 172,824 KB (~168.8 MiB) |
| TOTAL SWAP PSS | 240 KB |

PSS is essentially flat vs the AVD's 50,662 KB (+1.6 MiB), consistent with a real device
running more background services.

### Frame rate / jank: no valid samples this round

`dumpsys gfxinfo` under programmatic scrolling rendered only 3 frames
(`Janky frames: 1 (33.33%)`); the sample size is insufficient and the percentiles have a single
value (36ms / 4950ms) — **statistically meaningless, therefore not recorded**. A valid jank
baseline requires real interaction on a real device (session-list scrolling + long-session
scrolling + tool-card expansion); it is a real-device interaction measurement item.

## TODO

- [x] Release-build baseline: signed release + real device (2308CPXD0C) — see "Real-device baseline" above.
- [ ] Frame-rate/jank baseline: needs real interaction on a real device (programmatic scrolling
      cannot drive this app's scroll views).
