## Indoor location monitoring

ILM is an indoor location monitoring application, using BLE Beacons. It aims to let the teachers and students find their way to a point of interest indoor (or navigate inside of University building) such as specific laboratory/lecture room, coffee machine, etc.. where GPS/GNSS singal is not available.

<img src="/arts/beacons.png" width="600" height="300">

## Tech stack 🏗
* [Java](https://docs.oracle.com/en/java/)
* [Firebase](https://firebase.google.com/)
* [Google Cloud Platform](https://cloud.google.com/)
* [Google Maps SDK](https://developers.google.com/maps/documentation/android-sdk/overview)
* [Directions API](https://developers.google.com/maps/documentation/directions/overview)
* [MVVM Architecture](https://developer.android.com/jetpack/guide)
* [Jetpack Components](https://developer.android.com/jetpack)
* [Material Design](https://material.io/design)
* [BLE Indoor positioning](https://github.com/neXenio/BLE-Indoor-Positioning)

## Screenshots 📷
<img src="/arts/onboarding.png" width="450" height="290">
Teachers and students can sign up to the application, and once they verify their email address 
with the mail sent to their University email address, they will be able to use the application with its full functionality.



<img src="/arts/main.png" width="450" height="290">
This work also covers the outdoor navigation when the user is far away from the target destination. Once the user enters to the University building most of the signals will not be available indoor due to signals are not being able to go through walls, and this is where the user should switch the outdoor navigation map to the indoor positioning map with floating action button with the related icon above.

## Credits 🙏
Thanks to [neXenio](https://github.com/neXenio/BLE-Indoor-Positioning) for their reference app which uses their BLE indoor positioning library.
