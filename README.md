# Tap to Pay Android

<div align="center">

**A modern Android NFC card reader application for reading and parsing EMV contactless payment cards**

[![CI](https://github.com/Pedro-kt/TaptoPayAndroid/actions/workflows/ci.yml/badge.svg)](https://github.com/Pedro-kt/TaptoPayAndroid/actions/workflows/ci.yml)
[![codecov](https://codecov.io/gh/Pedro-kt/TaptoPayAndroid/branch/main/graph/badge.svg)](https://codecov.io/gh/Pedro-kt/TaptoPayAndroid)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-blue.svg?style=flat&logo=kotlin)](https://kotlinlang.org)
[![API](https://img.shields.io/badge/API-24%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=24)

[Features](#features) • [Tech Stack](#tech-stack) • [Installation](#installation) • [Testing](#testing) • [Disclaimer](#important-disclaimer)

</div>

---

## IMPORTANT DISCLAIMER

**THIS APPLICATION IS FOR EDUCATIONAL AND DEMONSTRATION PURPOSES ONLY.**

- **NOT intended for commercial or production use**
- **Does NOT store or transmit any card data**
- **All processing happens locally on the device**
- **PCI-DSS compliance is NOT guaranteed**
- **User assumes all responsibility for usage**

**By using this application, you acknowledge that it is intended solely for educational purposes to demonstrate NFC and EMV protocol knowledge.**

---

## Screenshots

**Note:** All card data shown in screenshots are test/sample cards, not real payment cards.

<div align="center">
  <table>
    <tr>
      <td><img src="screenshots/home_screen.jpeg" width="200"/></td>
      <td><img src="screenshots/reader_screen.jpeg" width="200"/></td>
      <td><img src="screenshots/card_detail.jpeg" width="200"/></td>
      <td><img src="screenshots/settings_screen.jpeg" width="200"/></td>
    </tr>
  </table>
</div>

---

## Features

### NFC Card Reading
- **EMV Protocol Support**: Reads contactless payment cards using ISO-DEP technology
- **Real-time Detection**: Instant card detection with visual and audio feedback
- **APDU Command Logging**: Captures all communication between device and card

### Card Data Display
- **Application Information**: AID, application label, and priority indicator
- **Transaction Data**: Card number, expiration date, and card type detection
- **Cardholder Data**: Cardholder name when available
- **EMV Tags Viewer**: Browse all EMV tags organized by category with search functionality
- **Tag Documentation**: Detailed information for 43 EMV tags with purpose, format, and source
- **PAN Validation**: Automatic Luhn algorithm validation for card numbers with visual indicators
- **AIP Decoder**: Bit-level decoding of Application Interchange Profile showing authentication capabilities

### Modern UI/UX
- **Material Design 3**: Clean, modern interface following Material You guidelines
- **Theme Support**: Light, Dark, and System theme options
- **Reactive Design**: Built entirely with Jetpack Compose
- **Sound Feedback**: Success and failure audio cues (toggleable)

### Developer Features
- **APDU Commands Tab**: View all command-response pairs with status codes
- **Search & Filter**: Find specific tags, AIDs, or APDU commands
- **Detailed Logging**: Configurable log verbosity levels (Not yet implemented)
- **Settings Panel**: Customize app behavior and appearance

---

## Tech Stack

### Language & Technologies
- **Kotlin** 2.0.21
- **Jetpack Compose**
- **Material 3**

### Architecture & Libraries
- **Clean Architecture** - Domain, Data, and Presentation layer separation
- **MVVM Pattern** - ViewModel-based architecture
- **Hilt** - Dependency injection
- **Coroutines** - Asynchronous programming
- **StateFlow** - Reactive state management

### Core Technologies
- **NFC API** - Android NFC framework
- **ISO-DEP** - ISO 14443-4 protocol for contactless cards
- **EMV Parsing** - Custom EMV tag parser implementation

### Testing
- **JUnit** - Unit testing framework
- **Truth** - Fluent assertion library
- **MockK** - Kotlin mocking library
- **Coroutines Test** - Testing coroutines and flows

### Tools
- **Gradle KTS**
- **Android Studio** - Koala Feature Drop | 2024.1.2 or higher

---

## Installation

### Prerequisites
- Android device with **NFC capability**
- Android **15.0 (API 35)** or higher
- NFC enabled in device settings

### Build from Source

1. **Clone the repository**
   ```bash
   git clone https://github.com/Pedro-kt/tap-to-pay-android.git
   cd tap-to-pay-android
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an Existing Project"
   - Navigate to the cloned directory

3. **Build and Run**
   - Connect your Android device via USB
   - Click "Run" or press `Shift + F10`
   - Grant NFC permissions when prompted

### APK Release

> Pre-built APKs will be available in the [Releases](https://github.com/Pedro-kt/tap-to-pay-android/releases) section

---

## How It Works

1. **PPSE Selection**: App sends SELECT command for Proximity Payment System Environment
2. **AID Discovery**: Extracts Application Identifier (AID) from PPSE response
3. **Application Selection**: Selects the payment application using discovered AID
4. **GPO Command**: Sends Get Processing Options with PDOL data
5. **Record Reading**: Reads card records from Application File Locator (AFL)
6. **EMV Parsing**: Parses all EMV tags and organizes by category
7. **Data Display**: Shows formatted card information with search capabilities

---

## Testing

The project includes comprehensive unit tests covering core functionality with high code coverage.

### Running Tests

**Via Android Studio:**
1. Open the project in Android Studio
2. Navigate to the test file or package you want to run
3. Right-click and select "Run Tests"
4. View results in the test runner panel

**Via Command Line:**
```bash
# Run all unit tests
./gradlew testDebugUnitTest

# Run tests with coverage report
./gradlew testDebugUnitTestCoverage

# Run specific test class
./gradlew testDebugUnitTest --tests "com.yumedev.taptopayandroid.util.CardValidatorTest"

# Clean and run all tests
./gradlew cleanTest testDebugUnitTest
```

**View Test Reports:**
```bash
# Test results HTML report
open app/build/reports/tests/testDebugUnitTest/index.html

# Coverage report (if generated)
open app/build/reports/coverage/test/debug/index.html
```

### Test Coverage

Current test suite includes:

- **CardValidatorTest**: 22 tests for Luhn algorithm validation
- **ValidatePanUseCaseTest**: 19 tests for PAN validation use case
- **AipDecoderTest**: 25 tests for AIP bit decoding
- **EmvTagParserTest**: 15 tests for EMV tag parsing
- **NfcCardReaderTest**: 8 tests for NFC communication
- **Repository Tests**: Coverage for audio, NFC events, and preferences
- **UseCase Tests**: Coverage for settings, theme, and NFC handling
- **ViewModel Tests**: Coverage for card details, settings, and main flow

All tests use **JUnit**, **Truth** assertions, and **MockK** for mocking.

---

## EMV Tags Supported

The app recognizes and parses EMV tags including:

- **Application Info**: AID (4F), Application Label (50), Priority (87)
- **Transaction Data**: PAN (5A) with Luhn validation, Expiration (5F24), Service Code (5F30)
- **Cardholder Data**: Name (5F20), Language (5F2D)
- **Card Details**: Track 2 (57), PDOL (9F38), AFL (94), AIP (82) with bit decoder
- And many more...

### Tag Documentation System

The app includes comprehensive documentation for 43 EMV tags, accessible via info buttons on each tag card:

- **Detailed descriptions**: Purpose and meaning of each tag
- **Format specifications**: Data structure and encoding information
- **Source references**: EMV specification sources (EMVCo Book 3, ISO 7816)
- **Category organization**: Tags grouped by Card, Application, Transaction, Cryptographic, and Processing Data

---

## Contributing

This is primarily a portfolio project, but suggestions and feedback are welcome!

1. Fork the repository
2. Create a feature branch (`git checkout -b feat/AmazingFeature`)
3. Commit your changes with conventional commits (`git commit -m 'feat(feature): description of feature'`)
4. Push to the branch (`git push origin feat/AmazingFeature`)
5. Open a Pull Request

---

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## Developer

**Bustamante Pedro**

- GitHub: [@Pedro-kt](https://github.com/Pedro-kt)
- Project: [tap-to-pay-android](https://github.com/Pedro-kt/tap-to-pay-android)

---

## Security & Privacy

- **No data storage**: Card data is never persisted to disk
- **No network requests**: All processing is done locally
- **No analytics**: No tracking or telemetry
- **Open source**: Code is fully transparent and auditable

---

## Acknowledgments

- EMV specifications by EMVCo
- Android NFC documentation
- Material Design 3 guidelines
- Jetpack Compose community

---

<div align="center">

**Remember: This is an educational project. Never use it to process real payment transactions.**

Made for learning and demonstration purposes

</div>