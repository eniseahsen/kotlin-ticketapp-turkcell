# TicketApp

**TicketApp** is a modular ticket management application for Android. This project:

- Creates the user interface and application flow with the `app` module
- Contains dependency-free domain models, repository interfaces, and common UI/helper structures with the `core` module
- Provides API, data access, token management, and communication with the external layer through the `data` module

The project also includes:
- Modern UI with Jetpack Compose
- Dependency management with Koin
- REST API communication with Retrofit / OkHttp
- Token storage with DataStore
- Reactive state management with MVVM + StateFlow
- Screen navigation with Compose Navigation
- ZXing-supported libraries for QR code and ticket validation scenarios

---


## 1. Root Files

- `settings.gradle.kts`
  - Defines the project modules: `:app`, `:core`, `:data`
  - Contains repository configuration for Gradle plugin management and dependency resolution

- `build.gradle.kts`
  - Defines common Gradle plugins as aliases
  - Allows submodules to use these plugins

- `gradle/libs.versions.toml`
  - Centrally stores project dependency versions
  - `app`, `core`, and `data` modules reference this version catalog

---

## 2. `app` Module

The `app` module contains the runtime side of the application and the user interface.



### 2.1 Dependency Injection

- `app/src/main/java/com/turkcell/ticketapp/di/appModule.kt`
  - Registers ViewModel classes using Koin `viewModelOf(::XyzViewModel)`
  - `LoginViewModel`, `RegisterViewModel`, `HomeViewModel`, `EventDetailViewModel`,
    `PurchaseViewModel`, `MyTicketViewModel`, `TicketDetailViewModel`, `CheckinViewModel`
    are defined here

### 2.3 Navigation

- `app/src/main/java/com/turkcell/ticketapp/navigation/AppNavHost.kt`
  - Performs navigation according to the authentication state of the application
  - Observes the `AuthRepository.isLoggedIn` flow:
    - `null` → splash loading screen
    - `true` → authenticated navigation
    - `false` → login/register flow
  - Manages `Home`, `EventDetail`, `MyTickets`, `TicketDetail`, `Staff`
    screens with `AuthedNavHost`

- `app/src/main/java/com/turkcell/ticketapp/navigation/AppDestinations.kt`
  - Defines serializable destination objects for each screen
  - Provides navigation between screens

### 2.4 Screens

`app/src/main/java/com/turkcell/ticketapp/screen/`

- `LoginScreen.kt`
  - Email and password input
  - Toggle password visibility
  - Triggers login operation
  - Displays error messages with `Snackbar`

- `RegisterScreen.kt`
  - Form for creating a new user account
  - Navigates to the `Home` screen after registration is completed

- `HomeScreen.kt`
  - Displays event and ticket lists
  - Provides `Staff` access according to user role
  - Enables navigation to Event / Ticket detail pages

- `EventDetailScreen.kt`
  - Displays details of the selected event
  - Ticket type selection and quantity increase/decrease
  - Starts the purchase flow

- `PurchaseConfirmDialog.kt`
  - Modal dialog to confirm purchase operation
  - Contains post-payment navigation logic

- `MyTicketsScreen.kt`
  - Lists the user's owned tickets
  - Provides navigation to ticket details

- `TicketDetailScreen.kt`
  - Displays the details of the selected ticket

- `StaffScreen.kt`
  - Contains ticket validation/scanning scenarios for staff members
  - Uses ZXing libraries for QR code scanning

### 2.5 ViewModel Layer

`app/src/main/java/com/turkcell/ticketapp/viewmodel/`

Each ViewModel:
- Manages UI state with `StateFlow`
- Fetches data using repository interfaces
- Converts errors into user-friendly messages with `toUserMessage()`
- Performs coroutine-based asynchronous operations with `viewModelScope.launch { ... }`

- `LoginViewModel.kt` → login operation, form validation, error handling
- `RegisterViewModel.kt` → new user registration
- `HomeViewModel.kt` → loading events and tickets, refresh, observing user role
- `EventDetailViewModel.kt` → single event details, ticket quantity selection
- `PurchaseViewModel.kt` → creating purchase and payment confirmation
- `MyTicketViewModel.kt` → loading user's ticket list
- `TicketDetailViewModel.kt` → single ticket details
- `CheckinViewModel.kt` → managing staff or check-in scenarios



---

## 3. `core` Module

The `core` module contains domain definitions independent from application dependencies.



### 3.1 Domain and Interfaces

`core/src/main/java/com/turkcell/core/domain/`

- `auth/`
  - `AuthRepository.kt` → contract for login, registration, logout, session state, and user role
  - `AuthSession.kt`, `User.kt`, `UserRole.kt` → authentication information and roles

- `event/`
  - `EventRepository.kt` → defines event list and single event operations
  - `Event.kt`, `TicketType.kt` → event model and ticket type model

- `ticket/`
  - `TicketRepository.kt` → ticket query contract
  - `Ticket.kt`, `TicketStatus.kt` → ticket status information

- `purchase/`
  - `PurchaseRepository.kt` → creating purchase and payment confirmation
  - `PurchaseDomain.kt` → domain model for purchase

- `checkin/`
  - `CheckinRepository.kt` → ticket check-in / staff validation logic

### 3.2 Common Utilities

- `core/exception/`
  - Error types such as `ApiException`, `NetworkException`

- `core/util/ErrorMessages.kt`
  - `Throwable.toUserMessage()`
  - Converts errors into user-friendly messages
  - Examples: `401`, `403`, `404`, `409`, `500+`, network error

- `core/ui/`
  - Contains theme and common UI components



---

## 4. `data` Module

The `data` module provides the real implementations of repository interfaces defined in `core`.





### 4.1 Dependency Module

- `data/src/main/java/com/turkcell/data/di/DataModule.kt`
  - `Json` serializer
  - OkHttp `HttpLoggingInterceptor`
  - `AuthInterceptor` and `TokenAuthenticator`
  - `Retrofit` instances
  - API interfaces (`AuthApi`, `EventApi`, `PurchaseApi`, `MeApi`, `CheckinApi`)
  - Repository implementations (`AuthRepositoryImpl`, `EventRepositoryImpl`, `PurchaseRepositoryImpl`, `TicketRepositoryImpl`, `CheckinRepositoryImpl`)
  - Provides `TokenStore` as a singleton inside Koin

### 4.2 Data Access Layer

- `data/remote/`
  - Retrofit interfaces defining API endpoints
  - Examples: `/auth/login`, `/auth/register`, `/auth/refresh`

- `data/local/`
  - `TokenStore.kt` stores access/refresh tokens and user role using DataStore
  - Provides Flow-based access and blocking helper functions

- `data/repository/`
  - `AuthRepositoryImpl.kt`
    - Performs API calls
    - Saves tokens when a successful response is received
    - Handles errors with the `Result` type
  - `EventRepositoryImpl`, `TicketRepositoryImpl`, `PurchaseRepositoryImpl`, `CheckinRepositoryImpl`
    - Converts API responses into domain models
    - Performs safe calls with the `runCatchingApi` helper function

- `data/dto/`
  - API request/response models
  - Contains data structures to be converted into domain models

- `data/mapper/`
  - Contains conversion logic between DTO and domain models

- `data/util/`
  - Helper functions for wrapping API calls, handling errors, and returning `Result`

### 4.3 Important Technologies

- `Retrofit` + `Kotlinx Serialization`
- `OkHttp Interceptor` + `Authenticator`
- Persistent token storage with `DataStore`
- Binding repositories and services with `Koin`

---

## 5. Important Files and Responsibilities

### `app/TicketAppApplication.kt`
Koin is initialized when the application starts.
- `androidContext(this)`
- `androidLogger()`
- `modules(dataModule, appModule)`

### `app/MainActivity.kt`
The Compose entry point of the application.
- `enableEdgeToEdge()`
- `setContent { TicketAppTheme { KoinAndroidContext { AppNavHost() } } }`

### `app/navigation/AppNavHost.kt`
Provides conditional navigation according to authentication state.
- `SplashScreen`, `AuthedNavHost`, `UnAuthedNavHost`
- Navigation to `Home` after login
- Back stack management with `popUpTo`

### `app/screen/LoginScreen.kt`
Login screen UI component.
- `OutlinedTextField` for email and password
- Login trigger with `Button`
- Error display with `SnackbarHost`

### `app/viewmodel/LoginViewModel.kt`
Manages form state.
- `canSubmit` validation
- Calls `login(email, password)`
- Applies `toUserMessage()` to error messages

### `core/domain/auth/AuthRepository.kt`
Defines the contract for authentication business logic.


### `data/local/TokenStore.kt`
Stores tokens securely.


### `data/di/DataModule.kt`
Connects network and data dependencies.
- Adds access token to all requests with `AuthInterceptor`
- Provides token refresh mechanism with `TokenAuthenticator`
- Creates `Retrofit` and services
- Provides `AuthRepositoryImpl` etc.

### `core/util/ErrorMessages.kt`
Converts device errors into user messages.
- `401`, `403`, `404`, `409`, `500+`
- Special message for network connection errors

---
