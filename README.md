# CMSC 495 Team Delta Capstone Project: ScreenTime Widget

## Team:

Kalim Dausuel

## Project Overview and Purpose

The ScreenTime widget is an Android widget application designed to help users monitor and control their device usage effectively. This app provides a widget-based interface that allows users to set and track daily and session time limits for their screen time.

The primary goals of this project are:
1. Develop a user-friendly widget for Android devices
2. Implement features like daily and session usage limits
3. Provide quick access to viewing time remaining and changing timer settings

## Setup Instructions

1. Clone the repository to your local machine.
2. Open the project in Android Studio (Koala or later).
3. Ensure you have the latest version of the Android SDK installed and Kotlin (version 2.0 or higher).
4. Sync the project with Gradle files.
5. Build the project to resolve any dependencies.

## Usage Guide

1. Install the app on your Android device.
2. Add the ScreenTime widget to your home screen.
3. Accept the permission requests to enable full functionality.
4. Enter the desired time limit for the daily and session timers in seconds.
5. Click the "Save Changes" button.
6. The widget will display your remaining time for both daily and session limits.
7. When you reach your set limit for either timer, you'll receive a notification over the application currently running.
8. Tap the settings icon on the widget to reconfigure your daily and session time limits.

## Key Features

1. **Widget-based Interface**: Easy-to-access widget showing daily and session timers.
2. **Customizable Time Limits**: Set separate limits for daily and session usage.
3. **Automatic Tracking**: The widget automatically tracks when the screen is on. The timers pause when the screen turns off!
4. **Notifications**: Receive alerts when you reach your set time limits.
5. **Session Reset**: Session timer automatically resets after the screen has been off for at least 15 continuous minutes.
6. **Daily Reset**: Daily timer resets at midnight every day.
7. **Persistent Across Reboots**: The app maintains its state even after device reboots.

## Troubleshooting Tips

1. **Widget Not Updating**: Ensure that battery optimization is disabled for the app.
2. **Notifications Not Showing**: Check if the app has permission to display overlay notifications.
3. **App Crashes on Launch**: Clear the app data and cache, then restart the app.
4. **Configuration Issues**: If the widget doesn't open configuration on first add, try removing and re-adding the widget.

## Permissions Required

The app requires the following permissions:
- SYSTEM_ALERT_WINDOW: To display overlay notifications
- RECEIVE_BOOT_COMPLETED: To persist timers across reboots
- SCHEDULE_EXACT_ALARM: For precise timer management

Make sure to grant these permissions for the app to function correctly.

## Contributing

Contributions to improve the ScreenTime widget are welcome (after the class is over). Please fork the repository, make your changes, and submit a pull request for review!


## Contact

For any queries or support, please open an issue in the GitHub repository.

