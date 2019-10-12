# Suraksha Mobile Application

**Suraksha** is a mobile application built during **Manipal Hackathon 2019**.
It's aim is to lend a helping hand to women by keeping them alert of their surroundings. At the same time, Suraksha also provides various services in case of emergency situations.


## Installation
Clone this repository and import into **Android Studio v3.6 (Canary Version)**
```bash
git clone https://gitlab.com/maheshtamse.13/surakshaandroidnode.git
```

## Permissions
Following permissions **must be enabled** either through the app or settings before testing:
- Camera
- Location
- SMS

## Work Around
- Authentication using mobile number (OTP will be sent on SMS for confirmation).
- Fill in the initial details and proceed to homepage.
- Homepage contains various features which are highlighted below.

## Features
- `SOS` - Raises an alert to nearby users, Police authorities*, and Emergency contacts.
- `Nearest Safe Place` - Displays a map with 'safe' locations like Hospitals, ATMs, Railway Stations.
- `Sound Light` - Uses flashlight to say SOS in Morse Code accompanied with a loud sound.
- `I'm Feeling Unsafe` - Provides various options which may come handy for various situations which cause insecurity. Depending on level of danger, an SOS may also be raised.
- `Track My Travel` - Tracks user's journey from current location to a user entered location. Based on certain criteria, a notification will be raised if user deteriorates from the path.

I'm Feeling Unsafe provides options for the following situations -
- Danger at Home - for situations relating to domestic violence.
- Travelling alone on road - sends notification at regular intervals for user acknowledgement.
- I'm in a cab - receive a call in a natural human voice so as let a cab member know that the woman is aware of herself and has people expecting her at the destination.
- Fake call - to escape certain awkward situations in office, family, etc.

## Background Services
Suraksha makes use of various background services to monitor the surroundings. Some of these are Activity Recognition, User Phone Activity Detection, Tracking user's journey, etc.


**NOTE**
*Police contact not added due to absence of permission from authorities.