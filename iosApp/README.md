# Citizen Complaint TN - Native iOS (SwiftUI)

This directory contains the complete native iOS application ported from the Android project.

## 📱 Features Included
* **All 38 Tamil Nadu Districts**: Structured across Northern, Western, Central, and Southern zones with popular wards and exact geographical coordinates.
* **Apple MapKit Interactive Pin Placement**:
  * Tap anywhere on the map to drop/move the complaint pin.
  * Real-time geocoding resolving street, ward, district, and latitude/longitude.
  * Quick jump chips for major Tamil Nadu cities (Chennai, Coimbatore, Madurai, Tiruchirappalli, Salem, etc.).
* **Apple CoreLocation Live GPS**:
  * One-tap "Live GPS" detection.
  * Automatic ward/district identification.
* **Civic Complaint Lifecycle**:
  * Submit grievance with department category, priority level, photo evidence, and anonymous options.
  * Live status tracking and upvoting.
  * Tamil Nadu interactive live map showing all registered grievances.

## 🚀 How to Run on iPhone

### Method 1: Using Xcode on Mac (Easiest)
1. In Google AI Studio, select **Export to ZIP** or **Push to GitHub**.
2. Unzip or clone the repository on your Mac.
3. In Xcode, select **File > Open** and select the `iosApp` folder (or `iosApp/Package.swift`).
4. Connect your iPhone via USB cable (or select an iPhone Simulator in the device picker).
5. In Xcode's Signing settings, select your Personal Apple ID (Free Apple Developer account).
6. Click **Run (Cmd + R)**. The app will install directly onto your iPhone!

### Method 2: GitHub Actions (Automated Cloud Build)
1. Push this repository to GitHub via the AI Studio Git/GitHub menu.
2. The included `.github/workflows/ios-build.yml` workflow will automatically run on a GitHub-hosted macOS runner to build the iOS app.
