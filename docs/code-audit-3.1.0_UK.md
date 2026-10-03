# Звіт аудиту коду 3.1.0 (версії після 2.X)

> Обсяг аудиту: `git diff v2.0.0..HEAD` (усі зміни App 3.0.0 + 3.1.0, 264 файли / 48 828 доданих
> рядків).
> Глибокий огляд зосереджений на новому в 3.1.0 (`v3.0.0..HEAD`, 71 файл / 20 640 рядків, з яких
> 12 423 рядки baseline-prof.txt — згенеровані артефакти).
> Метод: механічне сканування конвенцій (весь репозиторій) + статичний аналіз `lintDebug` +
> 3 паралельні субагенти глибокого огляду (тестовий інтелект / тривоги·синхронізація·KB·доки /
> сервісний шар·навігація·шлюзи).
> Дата: 2026-09-22.

## 1. Результати механічного сканування (усе пройдено)

Обсяг сканування: увесь `app/src/main` (сканування конвенцій «по всьому репозиторію»), тобто **не**
обмежене дифом `v2.0.0..HEAD`, наведеним вище.

| Пункт | Результат |
|---|---|
| Один файл >1500 рядків | немає |
| Copyright у заголовку файлу | присутній усюди (частина `Date` без `HH:mm:ss`, див. Low) |
| Заборонені анотації (@Tag/@Operation/@Schema/@Api/Knife4j/Swagger/SpringDoc/@SuppressFBWarnings) | немає |
| Вирівнювання ключів strings EN/ZH | повністю вирівняно (0 розбіжностей) |
| TODO/FIXME/HACK | 1 у `app/src/main`: `SharedSessionRepository.kt:42` — `TODO(UI 接入点)`, що зазначає, що UI входу для «Поділитися» ще не під'єднано. Існував раніше (додано у 1.4.0, `c7bf098`), отже поза дифом `v2.0.0..HEAD`, але в межах сканування по всьому репозиторію. |
| `check-secrets.sh --all` | чисто |
| `lintDebug` | без помилок (BUILD SUCCESSFUL) |

## 2. Висока серйозність (виправити до випуску)

- **H1 Вхід до бази знань завжди падає** — `NavGraphSessions.kt:331/481` виконує навігацію до
  `Screen.KnowledgeBase` (маршрут `knowledge_base`), але цей маршрут **взагалі не зареєстрований
  у NavGraph** (`NavGraphKb.kt` реєструє лише `kb`/`kb_collection`); щойно бекенд готовий,
  дотик до іконки бази знань у списку сесій гарантовано кидає `IllegalArgumentException`.
  Виправлення: вести вхід через `Screen.Kb.createRoute(...)` і видалити сирітські
  `KnowledgeBase`/`KnowledgeBaseCollection` у `Screen.kt`.
- **H2 Intel push назавжди мертвий після перебудови SSH-тунелю** — `intelPushJob` запускається
  лише один раз у `connectInternal` (`:544`); коли `rebuildConnection` (`:939`)/`replaceSshSession`
  (`:790`) перебудовують тунель (`setPortForwardingL(0,…)` щоразу обирає випадковий порт),
  перезапускається лише `pushJob`, але не `intelPushJob`, який продовжує підписку на старий
  `127.0.0.1:<старий порт>`; після зміни мережі або відновлення від «зомбі-з'єднання»
  сповіщення `intel.*`/`alert.hardware` тихо перестають працювати, а холостий job залишається.
  Виправлення: при перебудові скасовувати та перезапускати за новим `backendLocalPort`.
- **H3 Знахідки аудиту рівня high ніколи не сповіщають** — `StarBurstConnectionServiceGatewayExt.kt`
  фільтрує через `severity in {"warning","critical"}`, але значення `finding.Severity` на бекенді —
  `critical|high|medium|low|info` (`warning` не існує). Виправлення: замінити на
  `severity in setOf("high","critical")`.
- **H4 `disconnect()` помилково стирає всі облікові дані** — `SyncRepository.kt:389` викликає
  `secretStore.clearAll()`, що також очищає не-синхронізаційні `LLM_PROVIDER_API_KEY` та
  `SFTP_PASSWORD`. Виправлення: disconnect очищає лише чотири sync-ключі
  `GITHUB_TOKEN/WEBDAV_PASSWORD/SYNC_PASSPHRASE/BACKEND_TOKEN`.
- **H5 OOM при прийманні файлів KB** — `readTextFile` у `KbCollectionDetailViewModel` спершу робить
  `readBytes()` (повне читання в пам'ять), а вже потім перевіряє ліміт 5МБ. Виправлення:
  попередня перевірка через `OpenableColumns.SIZE` + потокове обрізання.
- **H6 Дублі в історії тривог через подвійний запис** — шлях push (`GatewayExt`) та 60-секундне
  опитування (`reconcileAlerts`) не дедуплікуються; та сама подія перетинання порогу може
  записатися двічі. Виправлення: у reconcile перевіряти дублі у часовому вікні перед записом,
  або синхронно оновлювати базову лінію знімка після push.
- **H7 Безпека попереднього перегляду/завантаження документів** — `DocumentPreviewSheet`
  використовує allowlist через `requestUrl.startsWith(backendUrl)` (обхід за префіксом);
  `BackendDocumentsApi.resolveDocumentUrl` пропускає абсолютні URL без змін, тож
  `downloadDocument` додасть би Bearer-токен до будь-якого стороннього хоста. Виправлення:
  парсити host+port і порівнювати точно; відхиляти cross-origin або знімати Authorization.

## 3. Середні

- Три нові сповіщення Intel/тривог/аудиту ігнорують `notificationsEnabled`/`silentNotifications`
  (дзвонять, навіть коли користувач вимкнув/заглушив сповіщення).
- Заголовок/тіло трьох сповіщень — жорстко вписаний китайський текст (не через strings.xml).
- notifId не ізольований за сервером (`100_000+run.id` тощо); сервери перезаписують одне одного;
  `abs(Int.MIN_VALUE)` досі від'ємне.
- `createNotificationChannels` щоразу при старті процесу видаляє та перестворює канали
  (`BuildConfig.VERSION_CODE>=1` завжди істина, збереженого маркера немає), скидаючи
  користувацькі налаштування каналів.
- `intelPushJob` стартує всередині `connections.compute` до входження стану в map — дуже мале
  вікно гоночних умов.
- Невдалий запуск завантаження результатів назавжди кешується як порожній (`loadRunResults`
  пише `emptyList` при збої; захист ідемпотентності робить повторну спробу неможливою).
- Невідповідність контракту парсингу `task.failure` (на бекенді немає поля `status`; подія тихо
  відкидається; сьогодні без функціонального впливу).
- `TestIntelProjectViewModel` тихо підставляє `projectId` = 0 без пояснення помилки в UI.
- PendingIntent сповіщень вказує на голий MainActivity; не може глибоко вести до
  run/проєкту/колекції.
- `ServerManagementViewModel.refresh()` не перезавантажує історію тривог; після
  `reconcileAlerts` список не оновлюється.

## 4. Низькі

- Невикористані імпорти (ServerManagementViewModel/ServerManagementScreen/SyncSettingsScreen/DocumentGenerateDialog/ChatInputBar
  тощо, 6 місць).
- Заголовки файлів `Date : 2026/09/22` без `HH:mm:ss` та без рядка `Contact`
  (BackendAlerts/BackendSync/AlertHistoryRepository/Kb*/NavGraphIntel/NavGraphKb тощо).
- Ширина рядка понад 120 (NavGraphIntel.kt:30/62, BackendPushListener.kt:168/171,
  StarBurstConnectionServiceGatewayExt.kt:290 тощо).
- `DocumentPreviewSheet` жорстко вписує китайські літерали («Документ» / «Помилка завантаження
  сторінки перегляду»); WebView `databaseEnabled/allowContentAccess=true` непотрібні; закриття
  без `destroy()`.
- Сповіщення тривог/аудиту використовують `VISIBILITY_PUBLIC`; тіло містить чутливу інформацію
  на кшталт шляхів файлів, видиму на екрані блокування.
- Доступність: картки проєктів / розгортувані рядки використовують голий `clickable` без
  role/contentDescription.
- `feature.ends()` щоразу при рекомпозиції заново парсить JSON (треба `remember`).
- Ресурси `test_intel_feature_security` тощо не використовуються; `BackendApi.intelRunDetail`/
  `featureRunTest` не мають викликачів (мертвий код).
- `onUpgrade` історії тривог просто скидає таблицю; видалення сервера не викликає
  `AlertHistoryRepository.clear(serverId)`.
- Після одного спрацювання перестворення каналів нічого не зберігається (та сама першопричина,
  що H11; перелічено в середніх).

## 5. Висновок

Загалом якість коду 3.1.0 висока: ізоляція винятків, маскування токенів, очищення конкурентності,
безпечна деградація парсингу та вирівнювання ресурсів EN/ZH — усе солідно. **До випуску
обов'язково виправити H1 (гарантований збій), H2/H3 (тихий вихід з ладу функцій), H4 (втрата
даних), H5 (OOM) та H7 (безпека)**; H6/H8/H11 і середні/низькі пункти можна обробити в
наступних малих релізах.

## 6. Статус виправлень (2026-09-22; скомпільовано + усі 272 юніт-тести зелені + check-secrets чисто)

Виправлено:
- H1 ✅ вхід до бази знань зі списку сесій веде через зареєстрований `Screen.Kb`; сирітські
  маршрути `Screen.KnowledgeBase`/`KnowledgeBaseCollection` видалено
- H2 ✅ `rebuildConnection`/`replaceSshSession` при перебудові тунелю також перезапускають
  `intelPushJob` (за новим backendLocalPort)
- H3 ✅ фільтр сповіщень про знахідки аудиту змінено на `severity in {high, critical}`
- H4 ✅ `LocalSyncSecretStore` отримав `clearSyncSecrets()` (очищає лише 4 sync-облікові дані);
  `disconnect()` тепер викликає його
- H5 ✅ Приймання KB: попередня перевірка `OpenableColumns.SIZE` + потокова обробка 8КБ з лімітом
  5МБ
- H6 ✅ `reconcileAlerts` дедуплікує за metric+state у 5-хвилинному вікні перед записом
- H7 ✅ попередній перегляд/завантаження документів тепер перевіряють точну тотожність
  scheme+host+port; з WebView прибрано `databaseEnabled`/`allowContentAccess`; DisposableEffect
  destroy
- H8 ✅ три нові сповіщення враховують `notificationsEnabled`/`silentNotifications`
  (silent використовує TASKS_SILENT)
- H9 ✅ тексти сповіщень перенесено до strings.xml (EN/ZH, 4 нові ключі + повторне використання
  наявних)
- H10 ✅ notifId з сіллю serverId + `and Int.MAX_VALUE` для уникнення від'ємних значень
- H11 ✅ міграція каналів із SharedPreferences-маркером `notif_channels_migrated_v1`,
  перестворення лише при першому запуску

Залишок (низькі; рекомендовано для наступних малих релізів): уніфікація заголовків файлів
`Date`/`Contact`, ширина рядків >120, невикористані ресурси/мертвий код, accessibility
role/contentDescription, глибокі посилання PendingIntent сповіщень, видимість на екрані
блокування, порожній кеш для невдалих результатів `run`, контракт `task.failure`, скинання
таблиці в `onUpgrade` історії тривог.
