<!--
Copyright(c) 2016 - Present, Clouds Studio Holding Limited. All rights reserved.
-->

---
version: alpha
name: StarBurst-design-system
description: |
  Дизайн-система клієнта StarBurst, побудована на Material 3 + Jetpack Compose. Бренд
  центрується на індиго (#6366F1), фіолетовий і циан утворюють трирівневу брендову палітру.
  UI постачається з трьома семантичними колірними схемами — Light / Dark / AMOLED, де AMOLED
  використовує чисто-чорні поверхні (#000000) заради енергозбереження OLED. Кожен візуальний
  токен напряму мапиться на MaterialTheme.colorScheme / Typography Compose; усі радіуси,
  відступи та стилі компонентів живуть у ui/components та ui/theme, готові до споживання кодом.
  Опційний шар мультяшного стилю (CartoonStyle.kt) стоїть ортогонально до палітр: він розширює
  радіуси (включно з усією сходинкою MaterialTheme.shapes), додає чорнильні контури 2.5dp,
  жорсткі тіні зі зсувом 3dp, тло з паперової крапкової сітки, заокруглений шрифт заголовків
  Baloo 2, іконки з чорнильним контуром і стікерні підкладки та пружну анімацію
  натискання/появи — не торкаючись жодного колірного токена.

colors:
  primary: "#6366F1"
  secondary: "#8B5CF6"
  tertiary: "#06B6D4"
  dark-primary: "#9DA3FF"
  dark-primary-container: "#2D2F6E"
  dark-surface: "#121218"
  dark-on-surface: "#E5E1E9"
  dark-surface-container: "#1E1E25"
  light-primary: "#4F52B8"
  light-primary-container: "#E0E0FF"
  light-surface: "#FCF8FF"
  light-on-surface: "#1C1B22"
  light-surface-container: "#F3EFF7"
  amoled-surface: "#000000"
  amoled-surface-container: "#0D0D12"
  status-connected: "#4CAF50"
  status-error: "#EF4444"
  status-warning: "#F59E0B"

typography:
  # значення нижче — звичайні (не-мультяшні) токени; з увімкненим мультяшним стилем
  # display*/headline*/title*/label* розв'язуються в Baloo 2 + важчі насиченості + вільніші
  # міжрядкові інтервали через CartoonTypography (див. блок `cartoon:` і «Мультяшний стиль»)
  display-large:
    fontFamily: system sans-serif
    fontSize: 57sp
    fontWeight: 400
    lineHeight: 64sp
    letterSpacing: -0.25sp
  display-medium:
    fontFamily: system sans-serif
    fontSize: 45sp
    fontWeight: 400
    lineHeight: 52sp
  display-small:
    fontFamily: system sans-serif
    fontSize: 36sp
    fontWeight: 400
    lineHeight: 44sp
  headline-large:
    fontFamily: system sans-serif
    fontSize: 32sp
    fontWeight: 400
    lineHeight: 40sp
  headline-medium:
    fontFamily: system sans-serif
    fontSize: 28sp
    fontWeight: 400
    lineHeight: 36sp
  headline-small:
    fontFamily: system sans-serif
    fontSize: 24sp
    fontWeight: 400
    lineHeight: 32sp
  title-large:
    fontFamily: system sans-serif
    fontSize: 22sp
    fontWeight: 700
    lineHeight: 28sp
  title-medium:
    fontFamily: system sans-serif
    fontSize: 16sp
    fontWeight: 700
    lineHeight: 24sp
    letterSpacing: 0.15sp
  title-small:
    fontFamily: system sans-serif
    fontSize: 14sp
    fontWeight: 500
    lineHeight: 20sp
    letterSpacing: 0.1sp
  body-large:
    fontFamily: system sans-serif
    fontSize: 16sp
    fontWeight: 400
    lineHeight: 24sp
    letterSpacing: 0.5sp
  body-medium:
    fontFamily: system sans-serif
    fontSize: 14sp
    fontWeight: 400
    lineHeight: 20sp
    letterSpacing: 0.25sp
  body-small:
    fontFamily: system sans-serif
    fontSize: 12sp
    fontWeight: 400
    lineHeight: 16sp
    letterSpacing: 0.4sp
  label-large:
    fontFamily: system sans-serif
    fontSize: 14sp
    fontWeight: 500
    lineHeight: 20sp
    letterSpacing: 0.1sp
  label-medium:
    fontFamily: system sans-serif
    fontSize: 12sp
    fontWeight: 500
    lineHeight: 16sp
    letterSpacing: 0.5sp
  label-small:
    fontFamily: system sans-serif
    fontSize: 11sp
    fontWeight: 500
    lineHeight: 16sp
    letterSpacing: 0.5sp
  code:
    fontFamily: Monospace
    fontSize: 13sp
    fontWeight: 400
    lineHeight: 20sp

rounded:
  dialog: 20dp
  card: 12dp
  picker: 12dp
  search: 14dp
  chip: 9999px
  # мультяшний стиль (опційний, див. «Мультяшний стиль»)
  cartoon-dialog: 32dp
  cartoon-card: 26dp
  cartoon-picker: 24dp
  cartoon-search: 30dp
  cartoon-primary-button: 28dp
  cartoon-secondary-button: 22dp

cartoon:
  default-enabled: false
  ink-stroke-width: 2.5dp
  ink-color-light: "#241F33 @ 85%"
  ink-color-dark: "onSurface @ 45%"
  shadow-offset: 3dp
  shadow-blur: 0dp
  shadow-color-light: "#3A2E5C @ 30%"
  shadow-color-dark: "#000000 @ 65%"
  backdrop-dot-spacing: 18dp
  backdrop-dot-radius: 1.3dp
  backdrop-dot-color-light: "#241F33 @ 7%"
  backdrop-dot-color-dark: "#FFFFFF @ 5%"
  button-press-scale: 0.9
  button-spring: "damping 0.5 / stiffness MediumLow"
  dialog-pop-from: 0.8
  dialog-spring: "damping 0.55 / stiffness MediumLow"
  # сходинка MaterialTheme.shapes (замінює типові значення M3, поки мультяшний стиль увімкнено)
  shapes-extra-small: 10dp
  shapes-small: 16dp
  shapes-medium: 26dp
  shapes-large: 32dp
  shapes-extra-large: 38dp
  # заокруглений шрифт заголовків (лише латиниця; body + CJK залишаються на системних шрифтах)
  display-font: "Baloo 2 (OFL), semi-bold / bold / extra-bold"
  display-font-scope: "display * / headline * / title * / label *"
  display-font-sizes: "57 / 45 / 36 / 32 / 28 / 24 / 22 / 16 / 14 / 14 / 12 / 11sp"
  display-font-line-heights: "80 / 64 / 52 / 46 / 40 / 34 / 31 / 23 / 21 / 20 / 17 / 16sp"
  display-font-weights: "ExtraBold x3 / Bold x6 / SemiBold x3 (vs M3 default 400)"
  icon-ink-ring-samples: 12
  icon-ink-ring-width: 1.2dp
  sticker-chip-padding: 9-14dp
  sticker-chip-tilt: -6deg

spacing:
  xxs: 2dp
  xs: 4dp
  sm: 8dp
  md: 12dp
  lg: 16dp
  xl: 24dp
  xxl: 32dp

components:
  primary-button:
    backgroundColor: "{colors.primary}"
    textColor: "{colors.light-surface}"
    typography: "{typography.label-large}"
    rounded: full
    height: 40dp
  primary-button-destructive:
    backgroundColor: "{colors.status-error}"
    textColor: "{colors.light-surface}"
    typography: "{typography.label-large}"
    rounded: full
  primary-button-amoled:
    backgroundColor: "#000000"
    textColor: "{colors.primary}"
    typography: "{typography.label-large}"
    rounded: full
  secondary-button:
    backgroundColor: transparent
    textColor: "{colors.primary}"
    typography: "{typography.label-large}"
    rounded: full
  secondary-button-outlined:
    backgroundColor: transparent
    textColor: "{colors.primary}"
    typography: "{typography.label-large}"
    rounded: full
  dialog:
    backgroundColor: "{colors.light-surface}"
    textColor: "{colors.light-on-surface}"
    typography: "{typography.title-medium}"
    rounded: "{rounded.dialog}"
  dialog-amoled:
    backgroundColor: "#000000"
    textColor: "{colors.dark-on-surface}"
    rounded: "{rounded.dialog}"
  card:
    backgroundColor: "{colors.dark-surface-container}"
    textColor: "{colors.dark-on-surface}"
    typography: "{typography.body-medium}"
    rounded: "{rounded.card}"
  list-item:
    backgroundColor: transparent
    textColor: "{colors.dark-on-surface}"
    typography: "{typography.body-large}"
    rounded: none
  section-header:
    textColor: "{colors.primary}"
    typography: "{typography.label-medium}"
  loading-edge:
    backgroundColor: "{colors.primary}"
    typography: "{typography.body-small}"
  switch:
    backgroundColor: transparent
    textColor: "{colors.primary}"
    typography: "{typography.label-medium}"
    rounded: full
---

## Огляд

StarBurst — мобільний клієнт «термінал як сервіс»: користувачі керують кількома серверами зі
своого пристрою, переглядають сесії, виконують команди в справжньому емуляторі терміналу та
дивляться AI-згенерований вміст. Увесь UI побудований на **Material 3 (Material You)** з
Jetpack Compose. Кожен токен дизайну походить з
`app/src/main/kotlin/org/hiylo/starburst/ui/theme/` (`Color.kt` / `Theme.kt` / `Type.kt`) та
`ui/components/` (`AppSurfaces.kt` тощо).

Брендова ідентичність тримається на трьох стовпах: **індиго-primary** (`#6366F1`),
**чисто-чорні поверхні AMOLED** (`#000000`) та **семантична ієрархія контейнерів M3**. Це не
маркетинговий веб-стиль; це стримана, dark-first інструментальна система — типова темна
поверхня `#121218`, а картки виражають висоту сходинкою `surfaceContainer` (`#1E1E25` →
`#262630` → `#31313B`) замість тіней. У режимі AMOLED кожна поверхня стискається до чистого
чорного, а картки розділяються контуром 1dp заради максимального енергозбереження OLED.

Система підтримує три візуальні рівні: системне світле/темне оформлення, динамічний колір
Android 12+ (Material You) та опційний для користувача режим чистого чорного AMOLED
(`AmoledDarkColorScheme`). AMOLED не змінює семантику компонентів — він лише міняє поверхні на
чорні, а заповнені кнопки на контурні.

**Ключові характеристики:**
- Стандартна `ColorScheme` Material 3; брендові кольори входять лише як акценти на кшталт
  `StarBurstPrimary` (`#6366F1`); основний UI споживає семантичні токени (`primary` /
  `surfaceContainer` / `outline`…)
- Dark-first: типова поверхня `#121218`, основний текст `#E5E1E9`, контейнери використовують
  сходинку `surfaceContainer*` замість тіней
- Режим чистого чорного AMOLED: поверхні `#000000` + контурні картки 1dp + контурні кнопки
- Централізована система радіусів: діалоги 20dp, картки/елементи списків 12dp, поля пошуку 14dp
- Емуляція терміналу та рендеринг коду використовують монопросторовий шрифт
  (`FontFamily.Monospace`); усе інше — системний sans-serif — окрім шару мультяшного стилю,
  який підмінює заокруглений шрифт заголовків на headings та labels (див. «Мультяшний стиль») і
  ніколи — на код
- Фіксовані семантичні кольори статусів: з'єднано зелений `#4CAF50`, помилка червона `#EF4444`,
  попередження янтарне `#F59E0B`

## Кольори

> **Файли джерел**: `Color.kt` (брендові/статусні кольори), `Theme.kt` (три палітри M3). Усі
> токени впорскуються через `StarBurstTheme`; компоненти завжди читають
> `MaterialTheme.colorScheme` і ніколи не вписують hex жорстко.

### Брендові кольори (Accent)
- **Primary Indigo** (`{colors.primary}` — `#6366F1`): основний брендовий колір. Заповнення
  primary-кнопки у світлому режимі, контур/текст кнопок AMOLED, підсвітка вибору, смуга
  завантаження, назви секцій.
- **Secondary Purple** (`{colors.secondary}` — `#8B5CF6`): вторинний брендовий акцент,
  використовується для особливих підсвіток і градієнтів (наприклад, колір переходу смуги
  завантаження).
- **Tertiary Cyan** (`{colors.tertiary}` — `#06B6D4`): третій акцент, для акцентів, які треба
  відрізняти від індиго.

### Семантичні кольори (Status)
- **Connected** (`{colors.status-connected}` — `#4CAF50`): здоров'я сервера, успіх з'єднання,
  успішні тости.
- **Error** (`{colors.status-error}` — `#EF4444`): помилки, руйнівні дії, попередження про
  втрату з'єднання.
- **Warning** (`{colors.status-warning}` — `#F59E0B`): повідомлення про оптимізацію батареї,
  застереження.

### Темна палітра (типова)
- **Surface** (`{colors.dark-surface}` — `#121218`): тло сторінок.
- **SurfaceContainer** (`#1E1E25`) → **SurfaceContainerHigh** (`#262630`) →
  **SurfaceContainerHighest** (`#31313B`): шарування карток та оверлеїв.
- **Primary** (`{colors.dark-primary}` — `#9DA3FF`): primary у темному режимі (освітлений за
 ради контрасту).
- **PrimaryContainer** (`#2D2F6E`) / **OnPrimaryContainer** (`#DEE0FF`): контейнер вибраного
  стану та його текст.
- **OnSurface** (`#E5E1E9`) / **OnSurfaceVariant** (`#C8C5D0`): основний і вторинний текст.
- **Outline** (`#918F9A`) / **OutlineVariant** (`#47464F`): рамки та розділювачі.

### Світла палітра
- **Surface** (`{colors.light-surface}` — `#FCF8FF`) / **OnSurface** (`#1C1B22`).
- **Primary** (`{colors.light-primary}` — `#4F52B8`) / **PrimaryContainer** (`#E0E0FF`):
  глибше індиго, яке лишається читабельним на білому.
- Сходинка **SurfaceContainer** (`#F3EFF7`) використовується для карток.

### Палітра AMOLED
`AmoledDarkColorScheme = DarkColorScheme.copy(...)` перевизначає на чистий чорний:
- **Surface / Background / SurfaceContainerLowest**: `#000000`
- **SurfaceContainer**: `#0D0D12` · **Low**: `#080810` · **High**: `#141419` · **Highest**:
  `#1C1C24` · **SurfaceVariant**: `#1A1A22`
- Основний текст `OnSurface` залишається `#E5E1E9`

## Типографіка

### Родина шрифтів
- **Основний текст/заголовки**: `FontFamily.Default` (системний sans-serif) типово, тож
  китайський та латинський текст рендеряться узгоджено. З увімкненим мультяшним стилем
  `display/headline/title/label` перемикаються на **Baloo 2** (`res/font/`, SIL OFL, 3 статичні
  насиченості ≈ 268КБ разом) через `CartoonTypography`; `body*` свідомо залишається на
  системному шрифті.
- **Код/термінал**: `FontFamily.Monospace`, фіксовано 13sp/20sp (`CodeTypography`), для блоків
  коду чату та виводу терміналу — в обох стилях.

### Ієрархія (повна шкала M3)

| Токен | Розмір | Насиченість | Міжрядковий | Трекінг | Використання |
|---|---|---|---|---|---|
| display-large | 57sp | 400 | 64sp | -0.25sp | Рідкісні великі числа / перші екрани |
| display-medium | 45sp | 400 | 52sp | 0 | — |
| display-small | 36sp | 400 | 44sp | 0 | Великі іконки порожніх станів |
| headline-large | 32sp | 400 | 40sp | 0 | — |
| headline-medium | 28sp | 400 | 36sp | 0 | Заголовки діалогів |
| headline-small | 24sp | 400 | 32sp | 0 | — |
| title-large | 22sp | 700 | 28sp | 0 | Заголовки сторінок |
| title-medium | 16sp | 700 | 24sp | 0.15sp | Заголовки елементів списків, текст тіла карток |
| title-small | 14sp | 500 | 20sp | 0.1sp | Вторинні заголовки |
| body-large | 16sp | 400 | 24sp | 0.5sp | Текст списків, підписи кнопок |
| body-medium | 14sp | 400 | 20sp | 0.25sp | Типовий основний текст, описи |
| body-small | 12sp | 400 | 16sp | 0.4sp | Довідковий текст, метадані |
| label-large | 14sp | 500 | 20sp | 0.1sp | Кнопки, підписи |
| label-medium | 12sp | 500 | 16sp | 0.5sp | Назви секцій, чіпи |
| label-small | 11sp | 500 | 16sp | 0.5sp | Текст статусів, бейджі |

Розміри та насиченості вище — звичайна сходинка; `body-large/medium/small` ідентичні під
мультяшним стилем. Мультяшна сходинка зберігає ті самі розміри (тож без регресій розташування)
і лише посилює насиченості та вільніші міжрядкові інтервали — див. `display-font-*` у блоці
токенів `cartoon:`.

### Принципи
Ієрархія будується головно з **насиченості + розміру**: заголовки елементів списків
використовують `titleMedium` (700), основний текст — `bodyLarge/bodyMedium` (400), а
статусний/довідковий текст — `labelSmall/bodySmall`. Заголовки тяжіють до 700
(`titleLarge/titleMedium`), основний текст до 400, вторинний/довідковий до 500. Увесь текст
посилається через `MaterialTheme.typography.*` — ніколи не розкидані сирі `TextStyle`.

## Розташування

### Система відступів
- **Базова одиниця: 4dp**, типові кроки 2/4/8/12/16/24/32dp.
- Горизонтальний відступ елемента списку `12–16dp`; відступ картки `14–16dp`; вміст діалогу
  `24dp`.
- Горизонтальні поля екрана однакові `16dp` (деякі списки — 12dp); відступ між компонентами
  `8dp` (`Arrangement.spacedBy(8.dp)`).
- Порожні стани починаються з вертикального відступу `48dp`.

### Структура сторінок
- **Одноколонкове розташування телефона**, без адаптації під настільні. Кожна сторінка —
  `Scaffold` + `TopAppBar` (стрілка назад + заголовок + дії праворуч).
- **Список сесій**: `LazyColumn` + картки сесій із заголовком (`titleMedium`), прев'ю
  (`bodySmall` монопросторове), часом і бейджем статусу.
- **Сторінка чату**: термінальна зона (монопросторова, `terminal-area`) + нижній редактор
  (заокруглене введення `24dp` + кругла кнопка надсилання).
- **Сторінка налаштувань**: секції, згруповані семантично (Загальні/Сповіщення/Вигляд/Показ
  чату/Поведінка чату/Розширені); кожна секція використовує `SectionHeader` (`labelMedium`
  індиго) плюс `ListItem` (заголовок + допоміжний текст + керуючий елемент у кінці).

### Сітка та контейнери
- Без фіксованої сітки; картки повної ширини з радіусом 12dp.
- Сітка іконок провайдерів `MCP grid` — 3 колонки; вибірники кольорів використовують рядки з
  горизонтальною прокруткою.

### Філософія повітря
Щільність інструмента: спершу інформація, картки через `8dp`, секції через `16dp`. У режимі
AMOLED чисто-чорне тло підсилює енергозберігаючу цінність «чорного простору»; ієрархію несуть
контури, а не повітря.

## Висота та глибина

| Рівень | Обробка | Використання |
|---|---|---|
| 0 — Плоско | Без тіні, лише колір тла | Тла сторінок, елементи списків, секції |
| 1 — Сходинка контейнерів | `surfaceContainer → High → Highest` поступово світлішає | Картки, випадні меню, нижні контейнери оверлеїв |
| 2 — Діалог | `surface` + tonalElevation 6dp (AMOLED: 0dp + контур 1dp) | AppDialog |
| 3 — Контур AMOLED | Чисто-чорна поверхня + `1dp outlineVariant(alpha 0.55)` | Усі картки/оверлеї в режимі AMOLED |
| C — Мультяшний | Жорстка тінь зі зсувом униз-праворуч 3dp, нуль розмиття, без M3-висоти; зверху чорнильний контур 2.5dp | Усі діалоги, кнопки, картки, бульбашки чату, коли мультяшний стиль увімкнено |

Система **замінює тіні сходинкою кольорів контейнерів**: «вищий» шар виражається освітленням
через `surfaceContainer*`, а не тінями. Єдина типова анімація — `AppLoadingEdge`: верхня
підсвітка 3dp, що пульсує горизонтально з primary-градієнтом (`FastOutSlowInEasing`, 700мс на
туди-назад), сигналізуючи завантаження.

## Форми

### Шкала радіусів

| Токен | Значення | Використання |
|---|---|---|
| `{rounded.dialog}` | 20dp | Контейнери діалогів (AppDialog) |
| `{rounded.card}` | 12dp | Картки, картки сесій, вибраний стан елемента списку (AppCardShape / AppPickerItemShape) |
| `{rounded.search}` | 14dp | Поля пошуку (AppSearchShape) |
| full | 9999px | Кнопки, введення, кнопка надсилання, чіпи, перемикачі |
| `{rounded.cartoon-dialog}` | 32dp | AppDialog з мультяшним стилем |
| `{rounded.cartoon-card}` | 26dp | Картки з мультяшним стилем |
| `{rounded.cartoon-picker}` | 24dp | Елементи вибірника з мультяшним стилем |
| `{rounded.cartoon-search}` | 30dp | Поля пошуку з мультяшним стилем |

Словник радіусів: **інтерактивні елементи — максимально круглі** (кнопки/введення/клавіша
надсилання повністю круглі), **контейнери 12dp**, **діалоги 20dp**. Іконки не обрізаються у
заокруглені форми (Material-іконки зберігають початковий вигляд).

## Компоненти

> **Примітка про стани**: компоненти Material 3 нативно несуть стани hover/focus/pressed/
> disabled; цей документ позначає лише ключові перевизначення.

### Кнопки (AppSurfaces.kt)
- **`primary-button` (AppPrimaryButton)**: суцільне заповнення `{colors.primary}`, текст
  `onPrimary`. Світлий режим використовує типову M3 `Button`; **режим AMOLED автоматично
  перемикається на контурну кнопку** — чорне тло + контур 1dp `{colors.primary}` + primary-текст.
- **`primary-button-destructive`**: заповнення `{colors.status-error}`, текст `onError`
  (світлий); AMOLED рендерить як контурну кнопку кольору помилки.
- **`secondary-button` (AppSecondaryButton)**: `TextButton`, текст `{colors.primary}`, без тла.
  З `outlined = true` стає контуром 1dp primary на прозорому/чорному тлі.
- **`dialog-actions` (AppDialogActions)**: вирівняна праворуч пара кнопок
  `[secondary, primary]` зі `spacedBy(8.dp)`; для руйнівних підтверджень передайте
  `destructiveConfirm`.

### Діалоги
- **`dialog` (AppDialog)**: `BasicAlertDialog` + Surface, радіус 20dp,
  `AlertDialogDefaults.containerColor`, tonalElevation 6dp. В AMOLED контейнер — `#000000` з
  тінню 0dp та контуром 1dp.
- Заголовки використовують `titleMedium/headlineSmall`; тіло — `bodyMedium` на тлі
  `onSurfaceVariant`.

### Картки та елементи списків
- **`card`**: радіус 12dp, тло зі сходинки `surfaceContainer`. Використовується для карток
  серверів, карток сесій та елементів списків файлів.
- **`list-item`**: прозоре тло, заголовок `bodyLarge` + допоміжний текст `bodySmall` +
  кінцевий `Switch`/`TextButton`/`›`. Активно використовується в Налаштуваннях та Діагностиці.
- **Вибраний стан** (`AppPickerItemShape` 12dp): тло `primaryContainer.copy(alpha 0.5)` +
  primary-текст + ✓ (AMOLED: чорне тло + контур 1dp primary).

### Інше
- **`switch`**: увімкнена доріжка `{colors.primary}`; AMOLED використовує чорну доріжку +
  контур primary.
- **`loading-edge` (AppLoadingEdge)**: пульсуюча верхня смуга 3dp з primary-градієнтом.
- **Бейджі/чіпи статусів**: з'єднано `{colors.status-connected}`, помилка
  `{colors.status-error}`, попередження `{colors.status-warning}`.
- **Заголовки секцій**: `labelMedium` 500, `{colors.primary}`,
  `padding(start 16dp, top 16dp, bottom 4dp)`.

## Мультяшний стиль

> **Файли джерел**: `ui/theme/CartoonStyle.kt` (стан, форми, токени чорнила/тіней, стікерний
> чіп), `ui/theme/Type.kt` (`CartoonTypography`), `ui/theme/Theme.kt` (параметр `cartoonStyle`,
> підміна `MaterialTheme.shapes`), `ui/components/CartoonIcon.kt` (іконки з чорнильним контуром
> та стікерні), `ui/components/AppSurfaces.kt` (адаптивні форми + обрамлення діалогів/кнопок).
> Перемикач: Налаштування → Вигляд → **Cartoon style**.

Мультяшне відчуття не виникає **з** підміни кольорів — наявні палітри `theme_scheme` (candy /
ocean / sunset / flame / bubble) змінюють лише `ColorScheme`, тому й читаються як
«перефарбування». Мультяшний стиль — окремий візуальний шар із сімома важелями:

| Важіль | Звичайний | Мультяшний |
|---|---|---|
| Радіус | діалог 20 / картка 12 / вибірник 12 / пошук 14 / бульбашка 12 | діалог 32 / картка 26 / вибірник 24 / пошук 30 / бульбашка 26-6-26-22 (користувач), 24 (асистент) |
| `MaterialTheme.shapes` | типові M3 (4 / 8 / 12 / 16 / 28) | 10 / 16 / 26 / 32 / 38 — покриває компоненти M3, які ніколи не беруть `AppCardShape` |
| Чорнильний контур | немає (AMOLED: 1dp `outlineVariant`) | чорнило 2.5dp — `#241F33`@85% на світлих поверхнях, `onSurface`@45% на темних |
| Тінь | немає (натомість сходинка кольорів контейнерів) | жорсткий блок 3dp униз-праворуч, **нуль розмиття**, `#3A2E5C`@30% світле / чорний@65% темне; без M3-висоти |
| Тло | плоский колір контейнера | паперова крапкова сітка: крок 18dp, крапки 1.3dp, `#241F33`@7% світле / білий@5% темне |
| Рух | типові M3 | кнопки стискаються до 0.9 при натисканні з малопружним spring; діалоги з'являються з 0.8 |
| Шрифт | системний sans, насиченості M3 | заокруглений шрифт заголовків Baloo 2 на `display/headline/title/label` (ExtraBold→SemiBold); `body*` і CJK залишаються на системному шрифті |
| Іконки | плоскі M3 `Icon` | чорнильне кільце навколо силуету гліфа або нахилений стікерний чіп позаду |

### Як це з'єднано

- `LocalCartoonStyle` — це `staticCompositionLocalOf { false }`; `isCartoonStyle()` його читає.
  `StarBurstTheme` постачає і локальний стан, і глобальний перемикач в одному кадрі.
- `AppCardShape` / `AppDialogShape` / `AppPickerItemShape` / `AppSearchShape` — екземпляри
  `AdaptiveRoundedShape(normal, cartoon)`. `createOutline` розв'язує радіус **у час
  малювання** з `CartoonStyleState.enabled`, тож ~60 наявних місць виклику
  `shape = AppCardShape` не потребують змін і оновлюються по всьому застосунку.
- **Один модифікатор робить усю роботу**: `Modifier.cartoonChrome(shape = AppCardShape)` =
  `cartoonOffsetShadow(shape).cartoonInkOutline(shape)`. Він огортає будь-який `Card` /
  `Surface` / `Box`, не торкаючись параметрів `border` чи `elevation`, тож наявні місця
  виклику зберігають свої рамки AMOLED і лише додають обрамлення. Коли стиль вимкнено,
  модифікатор повертається без змін — жодних вузлів малювання взагалі.
- Порядок малювання має значення і зафіксований у `cartoonChrome`: `drawWithContent` тіні
  виконує `drawShape` **до** `drawContent()` (тож тінь сидить поза власним тлом контейнера),
  тоді як `drawWithContent` чорнильного контуру виконує `drawContent()` **спершу** (тож лінія
  2.5dp не перекривається тлом контейнера і лишається штрихом повної ширини).
- `cartoonOffsetShadow` **не** використовує `Modifier.shadow` — той API не має параметра зсуву
  і лише розмиває. Тінь — це canvas-перенесення того самого шляху форми, саме це робить край
  жорстким і схожим на комікс. Форма → шлях іде через `DrawScope.drawShapePath`: рівномірні
  радіуси повертаються як `Outline.Rectangle` (яке не несе радіуса), тож радіус перечитується з
  `AdaptiveRoundedShape`; асиметричні бульбашки повертаються як `Outline.Generic` і
  повторно використовують шлях.
- Пружне підстрибування при натисканні живе в `cartoonButtonStyle()`:
  `MutableInteractionSource` + `collectIsPressedAsState()` керують
  `animateFloatAsState(0.9f)` з `spring(dampingRatio = 0.5f, stiffness = MediumLow)`, потім
  `.scale()` + `.cartoonChrome()`. `AppPrimaryButton` / `AppSecondaryButton` передають свій
  `interactionSource` у M3-кнопку, тож рябь і масштаб читають той самий стан натискання.
- Поява діалогу — `Animatable(0.8f → 1f)` на `AppDialog` з
  `spring(dampingRatio = 0.55f, stiffness = MediumLow)`. Зверніть увагу: імпорт `Animatable` —
  це `androidx.compose.animation.core.Animatable`; той, що з `androidx.compose.ui.graphics`,
  є `Animatable<Color>` і мовчки не збігається за типом.
- Тло — `Modifier.cartoonBackdrop()` на кореневому `Box` у `MainActivity`, тож кожен екран
  отримує паперову текстуру з одного місця. Альфа свідомо майже невидима: вона не має
  змагатися з інформаційною ієрархією.
- Збереження: `cartoon_style` у DataStore, дзеркалиться до `SyncSettings.cartoonStyle`, тож
  синхронізується між пристроями, як кожне інше налаштування вигляду.
- **Сходинка форм, а не радіуси за місцем виклику**: `StarBurstTheme` передає
  `shapes = if (cartoonStyle) cartoonShapes() else Shapes()` у `MaterialTheme`. Компоненти M3
  розв'язують типову форму через `ShapeKeyTokens` → `MaterialTheme.shapes`, тож 142 Card, 51
  Snackbar, 77 OutlinedTextField, 18 Chip, 7 FAB і 15 DropdownMenu отримують крутіші кути з
  одного рядка — без правок у місцях виклику. Саме це дає шару досягти компонентів, які
  ніколи не торкаються `AppCardShape`.
- **Дві гарнітури, один перемикач**: `Type.kt` експортує типову `Typography` M3 плюс
  `CartoonTypography` (`Typography.copy(...)` над родиною Baloo 2); тема обирає одну, тож
  жоден екран не галузиться за стилем. Перемикаються лише `display/headline/title/label` —
  `body*` залишається на системному шрифті заради читабельності довгих текстів, тож китайський
  основний текст не зачеплений.
  Baloo 2 не постачає CJK-гліфів, тож CJK усередині заголовка падає назад на системний шрифт
  посеред рядка: прийнятий компроміс і причина, чому шрифт обмежений короткими показовими
  рядками. Міжрядкові інтервали послаблені (шрифтова метрика рядка ≈ 1.602em), щоб високі
  заокруглені верхні виноски не наштовхувалися одна на одну. `CodeTypography` —
  монопросторова в обох режимах — код ніколи не бере мультяшний шрифт.
- **Іконки отримують дві обробки, розподілені за позицією** (`ui/components/CartoonIcon.kt`):
  `CartoonInkIcon` малює чорнильне кільце, перевимальовуючи vector painter 12× зі зсувом по
  малому колу, а потім підфарбований гліф зверху — контур силуету, який і читається як
  намальований від руки. Він свідомо **не** обводить кожен підшлях через `PathParser`: це
  зачорнило б середину кожної діри та заглиблення і перетворило б 24dp-гліфи на каламуть.
  `CartoonStickerIcon` садить гліф на заповнений чіп (типово `primaryContainer`) з нахилом
  −6°, чорнильним контуром і тінню зі зсувом — у стилі стікера, використовується лише для
  великих hero/порожньо-станних іконок. Обидві деградують до простої `Icon`, коли стиль
  вимкнено, і обидві зберігають `modifier` / `tint` викликача дослівно, тож не-мультяшний
  рендер біт-ідентичний.
- Чорнило тримається подалі від довгих списків (39 рядків Налаштувань, елементи `LazyColumn`):
  12 додаткових викликів малювання на іконку — нормально для жмені hero-гліфів, але марно й
  візуально шумно при щільності списку.

### Покриття

Повне обрамлення (радіус + чорнило + тінь зі зсувом): усі діалоги, усі кнопки застосунку
(`AppPrimaryButton` / `AppSecondaryButton`), картки головного екрана, усі картки Налаштувань
(спільна `SettingsCard`), бульбашки повідомлень чату.

Іконки з чорнильним контуром: панель введення чату (stop / send / attach / mic), верхні панелі
(назад / переповнення), дії головного екрана та списку сесій, FAB, стрілка назад у
налаштуваннях. Стікерні чіпи: hero-гліфи та гліфи порожніх станів від 40dp і вище (додати на
головній, hero чату, попередження, закладки, пошук, зірка, дерево git, діагностична інформація).

Лише радіус, без обрамлення: ~55 решти місць `Surface(shape = AppCardShape, ...)` поза
`HomeScreen` / `SettingsDisplayNames`. Вони читаються як «крутіші», але не «стікерні» —
додайте `.cartoonChrome(AppCardShape)` до їхнього `modifier`, щоб підняти їх.

### Відомі прогалини

- **`cartoonStickerChip` малює за межами меж вузла** (він розширюється на `padding` з усіх
  боків). Будь-який предок з `clipToBounds` — або батько фіксованого розміру, що обрізає —
  зріже контур і тінь чіпа. Викликачі мусять тримати чіп необрізаним і передавати власний
  `.size(...)` у `modifier`.
- **Насиченість чорнила не масштабується з розміром гліфа.** `icon-ink-ring-width` — фіксований
  dp, тож гліфу 14dp потрібне тонше кільце, ніж 24dp; кнопки stop/send чату передають 0.9–1.0dp
  явно. Не перевірено на рендерингу справжнього пристрою.
- **`cornerRadiusPx` розуміє лише `AdaptiveRoundedShape`.** `RoundedCornerShape` зберігає свої
  радіуси як `CornerSize` і не віддає `Dp`, тож простий `RoundedCornerShape`, переданий у
  `cartoonChrome`, отримає чорнильний контур з нульовим радіусом. Завжди передавайте
  `AdaptiveRoundedShape` (рівномірний) або асиметричний `RoundedCornerShape` (який іде гілкою
  шляху `Outline.Generic`).
- Верхні панелі не мають обрамлення: немає спільного компонента `TopAppBar`, тож це означало б
  ~30 правок по екранах. Їхні іконки зачорнілені, але сама поверхня панелі лишається плоскою.
- Ще немає ілюстрацій, маскота чи системи «емодзі як іконка».

## Можна і не можна

### Можна
- Читайте всі кольори з `MaterialTheme.colorScheme` і весь текст з `MaterialTheme.typography`;
  **ніколи не вписуйте hex чи розміри в компоненти жорстко**. Брендові кольори входять у
  систему лише через `StarBurstPrimary/Secondary/Tertiary` у `Color.kt`.
- Виражайте ієрархію сходинкою `surfaceContainer*`; без тіней типово.
- Використовуйте обгортки `AppPrimaryButton / AppSecondaryButton` — семантика AMOLED (чорний +
  контур) обробляється обгорткою, бізнес-код про це не знає.
- Тримайте семантичні кольори статусів фіксованими: з'єднано зелений `#4CAF50`, помилка червона
  `#EF4444`, попередження янтарне `#F59E0B`.
- Рендерте код і вміст терміналу `FontFamily.Monospace` 13sp; китайський та UI-текст
  використовують системний sans-serif. Під мультяшним стилем заголовки/підписи можуть брати
  вбудований заокруглений шрифт заголовків, але код — ніколи.
- Дотримуйтеся системи радіусів: кнопки/введення повністю круглі, контейнери 12dp, діалоги
  20dp, пошук 14dp. Під мультяшним стилем ті самі токени автоматично розв'язуються у свої
  значення `cartoon-*`.
- Читайте мультяшний стан лише через `isCartoonStyle()` / `LocalCartoonStyle`; ніколи не
  галузьтеся на `themeScheme`, щоб вгадувати, чи має виглядати застосунок мультяшно.

### Не можна
- Не додавайте четверту тематичну палітру; будь-який колір поза Light/Dark/AMOLED має
  мапитися назад на наявний токен.
- Не вигадуйте ad-hoc палітри через `darkColorScheme/lightColorScheme` у бізнес-сторінках —
  завжди йдіть через `StarBurstTheme`.
- Не сприймайте AMOLED як «ще один темний режим» — він лише міняє поверхні на чистий чорний +
  контури, не змінюючи семантику чи ієрархію компонентів.
- Не вшивайте мультяшні кольори в `theme_scheme`. Мультяшний стиль змінює радіуси, контури та
  тіні; він має лишатися ортогональним, щоб компонуватися з будь-якою палітрою та з AMOLED.
- Не вводьте маркетингову візуальність (градієнтні тла, декоративні ілюстрації) у типовому
  стилі. Мультяшний стиль — єдиний санкціонований опційний вхід для тіней і контурів, і він
  типово вимкнений — система залишається стриманим, інформація-спершу інструментом.
- Не рендерте код/вміст терміналу не-монопросторовим шрифтом, з мультяшним стилем чи без.

## Адаптивна поведінка

### Точки перелому та адаптація
Android нативно адаптивний; веб-точок перелому немає. Осі адаптації — **ширина екрана
(телефон проти планшета) та щільність (dp)**:
- Телефон у портреті (типово): одна колонка, картки повної ширини, елементи списків
  розтягуються горизонтально.
- Ландшафт/планшет: вміст `LazyColumn` лишається одноколонковим; редактор чату лишається
  пришпиленим до низу; за потреби зовнішній `Box` обмежує максимальну ширину вмісту й
  центрує його.
- Системний масштаб шрифту: Compose `Text` всюди використовує `sp` і автоматично слідує
  масштабуванню доступності.

### Цілі дотику
- Кнопки ≥ 40dp заввишки; іконкові кнопки `36dp` з радіусом `12dp`; кнопка надсилання редактора
  `36dp`, повністю кругла.
- Елементи списків повністю торкабельні (`Modifier.clickable` заповнює рядок), висота рядка
  ≥ 48dp.
- M3 перемикачі, слайдери тощо несуть власну мінімальну зону дотику 48dp.

### Стратегія стискання
- Верхня панель: на вузьких екранах лишайте лише назад + заголовок + суттєві дії; переповнення
  йде в меню «⋮».
- Довгі списки (Налаштування/Діагностика): `verticalScroll` усієї сторінки; групування всередині
  секцій через `ListItem`.
- Термінал: горизонтальне переповнення прокручується (`horizontalScroll`); вертикаль зберігає
  буфер прокрутки назад.

## Настанови з ітерації

1. Ітеруйте компонент за компонентом: змінюйте токени в `ui/components` чи `ui/theme`, і
   бізнес-сторінки набирають чинності негайно — спершу перевірте парсинг кольорів/типографіки,
   потім рівні AMOLED і Light, потім увімкніть мультяшний стиль і переконайтеся, що
   радіуси/контури/тіні йдуть слідом.
2. Надавайте перевагу наявному словникові для нових компонентів (`AppDialog` / `AppCard` /
   `AppPrimaryButton` / `AppPickerItemShape` 12dp + сходинка контейнерів); нові токени
   вводьте лише за потреби.
3. Додаючи колір, заповнюйте всі три рівні одразу: `DarkColorScheme`, `LightColorScheme`,
   `AmoledDarkColorScheme` — ніколи менше.
4. Завжди використовуйте наявні рівні `MaterialTheme.typography` для тексту; розширюйте
   `Typography` у `Type.kt` лише коли жоден рівень не пасує.
5. Після кожної зміни палітри перевіряйте контраст AMOLED: основний текст має лишатися
   `#E5E1E9` на чистому чорному, контури — `outlineVariant(0.55)`.
6. Руйнівні дії однаково використовують колір помилки (`{colors.status-error}`), ніколи новий
   червоний.
7. Нові поверхні мають явно вмикати мультяшне обрамлення: `shape` походить із токена
   `AdaptiveRoundedShape` (оновлення радіусів безкоштовне), а контур + тінь — з одного
   `.cartoonChrome(shape)`, доданого до `modifier`. Не додавайте `border =` чи `elevation =`
   заради мультяшного шару — обрамлення малює обидва, а `border`/`elevation` зберігають своє
   наявне значення AMOLED.
8. Тримайте мультяшну логіку поза колірними палітрами: `CartoonStyle.kt` володіє формою/
   контуром/тінню, `Theme.kt` — кольором. Мультяшний акцентний колір належить
   `StarBurstAccents`, а не мультяшному шару.

## Відомі прогалини

- **Одиниці шрифту `sp`**: це застосунок Android Compose, тож розміри/міжрядкові використовують
  `sp` (плюс дещо `dp`). `npx @google/design.md lint` — інструмент, орієнтований на CSS/px, і
  позначає `sp`/`dp` як «не валідний вимір». Це розбіжність одиниць інструменту та платформи, а
  не вада дизайну — лишайте `sp`/`dp` як є під час генерації Compose-коду.
- **Стани hover/focus** не документовано окремо — обробляються нативно компонентами M3, без
  власних перевизначень.
- **Динамічний колір (Material You)**: щойно увімкнено на Android 12+, палітру генерує система,
  і індиго-палітра цього документа більше не застосовується (лише чорна поверхня AMOLED
  нашаровується зверху); правила узгодженості під динамічним кольором окремо не документовано.
- **Доступність**: поза розмірами `sp` і висотами дотику, контраст та семантика TalkBack тут не
  покриті. Білий текст на `#6366F1` дає контраст ~4.26:1, близький, але трохи нижчий за WCAG AA
  4.5:1 — для сценаріїв основного тексту надавайте перевагу
  `primaryContainer`/`onPrimaryContainer`.
- **Ландшафтні/планшетні розташування** не вичерпно покриті у прев'ю та документах; поточна
  базова лінія — портретний телефон.
- **Вбудовані WebView-сторінки** (наприклад, вхід GitHub) поза межами цієї дизайн-системи;
  оформлення слідує за сторінкою-господарем.
