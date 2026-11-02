# RoomSaathi – Premium Room & Hostel Rental Android App

RoomSaathi is a native Android application built with Kotlin and Jetpack Compose (Material 3) connecting room owners and students searching for rooms, hostels, and PGs.

---

## Key Features

1. **Native Jetpack Compose Architecture**: Modern reactive UI with MVVM architecture, StateFlow, and Coroutines.
2. **Strict Role Separation**:
   - **Room Owner Experience**: Dedicated property dashboard, occupancy metrics, room creation with multi-photo gallery picker (up to 15 photos), furniture & electrical amenities management, booking request acceptance/rejection.
   - **Student Experience**: Search by city, locality, landmark; multi-criteria filters (Budget, Room Type, Wi-Fi, AC, Attached Bath, Parking, Furnished status); saved/bookmarked rooms; booking request creation; direct phone dialing via `Intent.ACTION_DIAL`.
   - **Safety & Admin Console**: Review and flag suspicious listings, verify owner guidelines, and enforce community safety.
3. **Phone & OTP Authentication**:
   - +91 Indian phone number entry, 6-digit OTP verification with resend countdown timer.
   - Fast demo 1-tap login mode for testing.
4. **Security & Data Layer**:
   - Zero-trust role separation.
   - Production `firestore.rules` and `storage.rules` included.
   - Clean Android Photo Picker (zero-permission storage access).

---

## Firebase Setup Instructions

To link the project to your own Firebase project:

1. **Create a Firebase Project**:
   - Go to the [Firebase Console](https://console.firebase.google.com/).
   - Click **Add project** and name it `RoomSaathi`.

2. **Add Android App**:
   - Package name: `com.aistudio.roomsaathi.rzpkfa`
   - Download the generated `google-services.json` file.
   - Place `google-services.json` in the `/app` directory.

3. **Enable Authentication**:
   - In Firebase Console > **Authentication** > **Sign-in method**, enable **Phone**.
   - (Optional for development): Add test phone numbers (e.g., `+91 98260 12345` with OTP `123456`).

4. **Enable Cloud Firestore**:
   - In Firebase Console > **Firestore Database**, create a database.
   - Deploy `firestore.rules` using the Firebase CLI:
     ```bash
     firebase deploy --only firestore:rules
     ```

5. **Enable Firebase Storage**:
   - In Firebase Console > **Storage**, initialize the default storage bucket.
   - Deploy `storage.rules`:
     ```bash
     firebase deploy --only storage
     ```

---

## Building the APK

### Debug APK
Run the Gradle assemble command:
```bash
gradle :app:assembleDebug
```
The resulting debug APK will be generated at:
`app/build/outputs/apk/debug/app-debug.apk`

### Signed Release APK
Configure your signing keystore credentials in environment variables or Gradle properties, then run:
```bash
gradle :app:assembleRelease
```
The release APK will be located at:
`app/build/outputs/apk/release/app-release-unsigned.apk` (or signed per configured keystore).
