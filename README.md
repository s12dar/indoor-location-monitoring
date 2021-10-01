# Project objectives:

The project consists in the design and development of an indoor location monitoring application, using Bluetooth Low Energy (BLE) beacons. The application can be used for asset tracking in places where GPS/GNSS is not available, such as indoor tracking. Various types of Bluetooth beacons are used to test the accuracy between different standards (Bluetooth 4.2 vs 5.0). 

## Technology tools:
 
This work also covers the outdoor navigation when the user is far away from the target destination. Most of the signals will not be available indoor due to signals are not being able to go through walls, and this is where the user should switch the outdoor navigation map to the indoor positioning map with floating action button with the related icon. 

In outdoor, to receive the directions between the user with the target destination and draw the routes with their alternatives the Google Directions API is used. It’s a web service which returns XML or JSON formatted directions data and to do this job it uses HTTP request. And to obtain user’s geo-coordinates to the database in every four seconds, a location service is built. To navigate outdoor the Google Maps SDK is used which lets us to add the map to our Android application and to use the Google Maps data such as displaying the maps, gesture responses of the maps. 

In the indoor positioning part, to estimate the positions based on the received advertising packets from used beacons a third-party library called [BLE Indoor Positioning](https://github.com/neXenio/BLE-Indoor-Positioning) is used. Therefore, the Bluetooth scanning method to get the data from the beacons nearby is implemented, and once the beacon data such as mac address, advertising data, and RSSI data is obtained and pass through to the Beacon Manager singleton, everything else is taken care.

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

## License
[MIT](https://choosealicense.com/licenses/mit/)
