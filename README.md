# BrewBuddy ☕️

**BrewBuddy** is a modern Android app designed for coffee enthusiasts to discover, order, and manage their favorite drinks with a seamless and delightful experience. This README provides an overview of the app's features, codebase structure, architecture, setup instructions, and contribution guidelines.

---

## Table of Contents

- [Features](#features)
- [Screenshots](#screenshots)
- [Project Structure](#project-structure)
- [Architecture](#architecture)
- [Technologies](#technologies)
- [Setup Instructions](#setup-instructions)
- [Testing](#testing)
- [Contributing](#contributing)
- [License](#license)

---

## Features

### 1. **Onboarding & Login**
- Welcomes users with a sleek splash screen and prompts for a username.
- Saves username locally for a personalized experience across sessions.
- Smooth transitions to the main app.

### 2. **Home Screen**
- Showcases **Best Seller** and **Weekly Recommendations** with vibrant visuals.
- Content is cached locally for offline access, ensuring uninterrupted browsing.
- Personalized greetings based on the user's saved name.

### 3. **Menu Browsing**
- Browse hot or iced coffees using intuitive toggles.
- Instant search by name, description, or ingredients for quick discovery.
- Add items to the order with a single tap or view details for more information.

### 4. **Coffee Detail Bottom Sheet**
- Displays coffee image, description, price, and a quantity selector.
- **Buy Now** opens a payment sheet for seamless checkout.
- **Favorite** button toggles favorite status with a confirmation dialog.

### 5. **Favorites**
- Centralized view of all favorited coffees for quick access.
- Tap to view details or place an order directly from the favorites list.

### 6. **Order Payment (Bottom Sheet)**
- Inline editing for delivery address with a modern, rounded input field.
- Detailed price breakdown including item cost, fees, and total.
- One-tap order placement for a frictionless experience.

### 7. **Orders History**
- View all past and recent orders in a clean, organized list.
- Filter orders by recent or previous for easy tracking.
- Offline access to order history for convenience.

### 8. **Offline Support**
- Core features (menu, recommendations, best seller, and order history) are fully functional offline after the initial sync.

---

## Screenshots

Below are the key screens of BrewBuddy, resized to smaller dimensions for better readability in this README. Full-size images are available in the `app/src/main/res/pic/` directory.

| **Splash** | **Login** | **Home** |
|------------|-----------|----------|
| ![Splash Screen](pic/splash.png) | ![Login Screen](pic/login.png) | ![Home Screen](pic/home.png) |

| **Menu** | **Coffee Detail** | **Favorites** |
|----------|-------------------|---------------|
| ![Menu Screen](pic/menu.png) | ![Coffee Detail](pic/coffee_detail.png) | ![Favorites Screen](pic/favorites.png) |

| **Payment** | **Orders** |
|-------------|------------|
| ![Payment Sheet](pic/payment.png) | ![Orders Screen](pic/orders.png) |

---

## Project Structure

The project follows a modular, clean architecture approach for maintainability and scalability.

### Root Structure
```plaintext
BrewBuddy/
├── app/            # Presentation layer (UI, Fragments, Activities, ViewModels)
├── domain/         # Domain layer (business logic, use cases, core models)
├── data/           # Data layer (Room DB, APIs, repositories)
└── README.md       # Project documentation
```

### Layer Breakdown

#### app/ (Presentation Layer)
```plaintext
app/
├── src/main/java/emad/space/brewbuddy/
│   ├── ui/
│   │   ├── home/                 # Home screen, best seller, and recommendations
│   │   ├── menu/                 # Menu browsing and search functionality
│   │   ├── detail/               # Coffee detail bottom sheet
│   │   ├── orders/               # Order history and filtering
│   │   ├── favourites/           # Favorites management
│   │   ├── payment/              # Payment bottom sheet
│   │   ├── onboarding/           # Splash, login, and onboarding flows
│   │   └── shared/               # Reusable adapters, base classes, utilities
│   ├── MainActivity.kt           # Main entry point for the app
│   └── BrewBuddyApp.kt           # Application class for initialization
├── res/
│   ├── layout/                   # XML layouts for fragments, sheets, dialogs
│   ├── drawable/                 # Icons, backgrounds, and shapes
│   ├── pic/                      # Screenshots for README (original and small sizes)
│   ├── values/                   # Strings, colors, themes, and styles
│   └── navigation/               # Navigation graph for seamless screen transitions
└── AndroidManifest.xml           # App configuration
```

#### domain/ (Domain Layer)
```plaintext
domain/
├── src/main/java/emad/space/domain/
│   ├── models/         # Core entities (CoffeeItem, Order, User)
│   ├── usecases/       # Business logic for app features
│   └── repo/           # Repository interfaces for data access
└── build.gradle        # Domain module dependencies
```

#### data/ (Data Layer)
```plaintext
data/
├── src/main/java/emad/space/data/
│   ├── local/                  # Room database, DAOs, and entities
│   ├── remote/                 # Retrofit API services and DTOs
│   ├── repo/                   # Repository implementations for data access
│   ├── pricing/                # Pricing logic for orders
│   ├── prefs/                  # User preferences via DataStore/SharedPreferences
│   ├── di/                     # Hilt dependency injection modules
│   └── DataModule.kt           # DI setup for data layer
└── build.gradle                # Data module dependencies
```

---

## Architecture

BrewBuddy follows **Clean Architecture** with **MVVM** (Model-View-ViewModel) to ensure separation of concerns, testability, and scalability:

- **Presentation Layer**: Handles UI components (Fragments, Activities) and ViewModels for UI logic.
- **Domain Layer**: Contains business logic (use cases) and core models, independent of frameworks.
- **Data Layer**: Manages data sources (Room for local storage, Retrofit for API calls) and repositories.

Dependency injection is handled via **Hilt**, ensuring modular and testable code. **Coroutines** and **Flow** manage asynchronous operations for smooth performance.

---

## Technologies

- **Kotlin**: Primary programming language.
- **Coroutines & Flow**: For asynchronous programming and reactive data streams.
- **Room**: Local database for offline caching.
- **Retrofit**: HTTP client for API interactions.
- **Hilt**: Dependency injection for modular code.
- **Material Components**: For modern, consistent UI design.
- **Navigation Component**: For seamless screen transitions.
- **MVVM & Clean Architecture**: For maintainable and testable codebase.

---

## Setup Instructions

### Prerequisites
- **Android Studio**: Arctic Fox (2020.3.1) or newer.
- **JDK**: Version 11 or higher.
- **Emulator/Device**: Android API 23 (Marshmallow) or above.
- **Gradle**: Version 7.0 or higher (handled by Android Studio).

### Steps
1. **Clone the Repository**:
   ```bash
   git clone https://github.com/your-repo/brewbuddy.git
   ```
2. **Open in Android Studio**:
   - Launch Android Studio and select "Open an existing project."
   - Navigate to the `BrewBuddy` folder and open it.
3. **Sync Project**:
   - Click "Sync Project with Gradle Files" to download dependencies.
4. **Run the App**:
   - Select an emulator or connected device (API 23+).
   - Click "Run" to build and launch the app.
5. **Offline Mode**:
   - The app works offline after the first sync, with no external API keys required.

---

## Testing

- **Unit Tests**: Located in `app/src/test` and `data/src/test` for ViewModels, use cases, and repositories.
- **UI Tests**: Located in `app/src/androidTest` for Espresso-based UI testing.
- **Running Tests**:
  ```bash
  ./gradlew test
  ./gradlew connectedAndroidTest
  ```

---

## Contributing

We welcome contributions to BrewBuddy! To contribute:

1. **Fork the Repository**: Create your own fork of the project.
2. **Create a Feature Branch**:
   ```bash
   git checkout -b feature/your-feature-name
   ```
3. **Submit a Pull Request**:
   - Open a pull request against the `main` branch.
   - Include a clear description of your changes and reference any related issues.
4. **Discuss Ideas**: Open an issue to propose new features or report bugs before starting work.

Please follow the [Code of Conduct](CODE_OF_CONDUCT.md) and ensure your code adheres to the project's style guidelines (e.g., Kotlin lint rules).

---

## License

BrewBuddy is licensed under the [MIT License](LICENSE). See the LICENSE file for details.

---

Enjoy brewing your perfect coffee with **BrewBuddy**! ☕️
