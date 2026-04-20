# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Test Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Build release AAB (Play Store) and APK
./gradlew bundleRelease
./gradlew assembleRelease

# Run all instrumented UI tests (requires connected device/emulator)
./gradlew connectedDebugAndroidTest

# Run a single test class
./gradlew connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.nedeme.LoginScreenTest

# Install on connected device
./gradlew installDebug
./gradlew installRelease
```

**Environment setup**: Requires JDK 21 (bundled with Android Studio at `"C:/Program Files/Android/Android Studio/jbr"`). Set `JAVA_HOME` and `ANDROID_HOME` before running Gradle from CLI.

## Architecture

Single-module Android app (`com.nedeme`) using **MVVM + Hilt DI + Jetpack Compose** with **Firebase as BaaS** (no custom backend).

### Two user roles with separate navigation flows
- **Client**: Home (categories) → Search → Profile → Booking → My Bookings
- **Tradesperson**: Dashboard (pending/accepted tabs) → Profile Setup

Role is determined at registration (`UserRole.CLIENT` vs `UserRole.TRADESPERSON`) and stored in Firestore `users` collection. `NeDemeNavGraph.kt` routes to the correct start screen and shows role-specific bottom navigation.

### Data flow pattern
```
Composable Screen
  → observes ViewModel.uiState (StateFlow<XxxUiState>)
    → ViewModel calls Repository methods
      → Repository returns Flow<Resource<T>> using callbackFlow with Firestore snapshot listeners
```

All async results are wrapped in `Resource<T>` (sealed class: `Success`, `Error`, `Loading`).

### Firebase collections
- `users` — all users (auth data, FCM token, role)
- `tradespeople` — extended profiles (categories, city, rating, `isFeatured`)
- `bookings` — service requests (status lifecycle: PENDING → ACCEPTED → COMPLETED)
- `categories` — pre-seeded, read-only (8 service types)
- `reviews` — client reviews, triggers rating recalculation on tradesperson

### Notification flow
Booking creation in Firestore triggers a Cloud Function (`functions/src/index.ts`) that sends an FCM push notification to the tradesperson. The on-device handler is `NeDemeMessagingService`.

### Monetization
`Tradesperson.isFeatured` flag causes priority sort in search results. Set manually in Firebase Console (no in-app payment yet).

### Location/Maps
`LocationHelper.kt` provides GPS location and distance calculations. Search filters tradespeople within 50km radius client-side (Firestore has no native geo queries). Tradesperson setup includes a map-based location picker.

## Testing

67 instrumented Compose UI tests across 7 suites. Tests use `createComposeRule()` with isolated composables — no Firebase dependency. UI elements are targeted via `testTag` strings defined in `util/TestTags.kt`.

Custom test runner: `com.nedeme.HiltTestRunner` (configured in `app/build.gradle.kts`).

## Key Files

- `ui/navigation/Screen.kt` — all route definitions (sealed class)
- `ui/navigation/NeDemeNavGraph.kt` — full navigation graph, bottom nav, auth routing
- `di/AppModule.kt` + `di/RepositoryModule.kt` — all Hilt-provided dependencies
- `util/Resource.kt` — async state wrapper used by all repositories
- `util/TestTags.kt` — test tag constants for UI testing
- `util/Constants.kt` — Firestore collection names
- `firestore.rules` — security rules (field-level restrictions on bookings)
- `functions/src/index.ts` — the only server-side code (30 lines)

## Conventions

- UI language is **French** (targeting West African market). Currency is **FCFA**.
- Each screen has a paired `ViewModel` in the same package (e.g., `screens/search/SearchViewModel.kt` + `SearchResultsScreen.kt`).
- ViewModels expose a single `val uiState: StateFlow<XxxUiState>` observed via `collectAsStateWithLifecycle()`.
- Repositories use Firestore `addSnapshotListener` wrapped in `callbackFlow` for real-time data.
- Release signing reads from `keystore.properties` (gitignored). Keystore file is `ne-deme-release.keystore` at project root.
