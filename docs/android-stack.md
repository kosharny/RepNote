# Android stack

Проверено 30 октября 2026 года для доработки RepNote.

Существующие версии оставлены: AGP 9.4.1, Gradle Wrapper 9.6.0, Compose/serialization compiler plugins 2.2.10, Compose BOM 2026.02.01, явно выбранный Compose UI 1.12.1, Activity 1.13.0, Lifecycle 2.11.0, Navigation 3 1.2.0, Room 3.0.3, KSP 2.3.10, DataStore 1.3.0. Это зафиксированный стек проекта, а не утверждение о последних доступных версиях. Built-in Kotlin AGP сохранён; `org.jetbrains.kotlin.android` не добавлен. База обновлена до версии 6: добавлена AutoMigration 5→6 для метаданных расписания готовых планов и стабильных ключей упражнений. Предыдущие схемы и миграции сохранены.

Сборка выполняется на установленном JDK 21.0.2; существующий bytecode target Java 11 сохранён. Установленная платформа Android 37.0, compileSdk/targetSdk 37, minSdk 24. Новые SDK и эмуляторы не устанавливались.

Добавлена только production-библиотека AppCompat 1.8.0 для штатного переключения языка во всём приложении, включая отдельные окна Dialog, и синхронизации с системным выбором языка. Версия проверена по [официальным release notes](https://developer.android.com/jetpack/androidx/releases/appcompat#1.8.0): стабильная, minSdk 23 совместим с текущим minSdk 24. Activity остаётся единственной, весь UI остаётся Compose. AppCompatActivity и Theme.AppCompat.DayNight.NoActionBar нужны для совместимости языкового механизма с Android 12 и ниже. [Документация языков приложения](https://developer.android.com/guide/topics/resources/app-languages).

В Lint исходно не разрешалась уже существующая `ui-test-junit4` без версии: прежний Compose BOM не был подключён к androidTest-конфигурации. Тот же BOM подключён к этой конфигурации по [официальной схеме](https://developer.android.com/develop/ui/compose/bom). Новые тестовые библиотеки не добавлены, тестовые исходники и задачи не изменены, тесты не запускались.

Разделение App Bundle по языкам отключено: ru/uk/en/pl доступны для переключения без сети. Для ручной проверки предусмотрен необязательный `-Prepnote.verificationSuffix=.review`; обычный applicationId остаётся `com.example.repnote`.
