# Built-in Probes

* **General Information**
    * [Audio](#AudioProbe)
    * [Battery](#BatteryProbe)
    * [Hardware](#HardwareInfoProbe)
    * [Display](#DisplayProbe)
    * [Weather](#WeatherProbe)
    * [Multimedia](#MultimediaProbe)
* **Telephony**
    * [Cells Information](#CellInfoProbe)
    * [Phone calls](#CallsProbe)
* **Calendar**
    * [Current events](#CurrentEventsProbe)
* **Bluetooth**
    * [Bluetooth connections](#BluetoothConnProbe)
    * [Bluetooth scans](#BluetoothScanProbe)
* **Wi-Fi**
    * [Wi-Fi Access Points](#WiFiProbe)
    * [Wi-Fi P2P scans](#WiFiP2PProbe)
* **Sensors**
    * [Activity Recognition](#ActivityRecognitionProbe)
    * [Environment](#EnvironmentSensorProbe)
    * [Motion](#MotionSensorsProbe)
    * [Position](#PositionSensorProbe)
* **Applications**
    * [Installed](#InstalledAppsProbe)
    * [Running](#RunningAppsProbe)

# General Information

### <a name="AudioProbe"></a>AudioProbe (type: <b>ContinuousProbe</b>)

Monitors different events related to the audio system, e.g., the current
music volume level, if the speaker is on, or if a pair of headset is
connected to the local device.

#### Returns

Returns the following values:

NAME                | Value                                 | Description
--------------------|---------------------------------------|--------------------------------------------------
RING_MODE           | 0 (Silent), 1 (Vibrate), 2 (Normal)   | Ring mode
ALARM_VOLUME        | \[0, 1\]                              | Alarm volume level
MUSIC_VOLUME        | \[0, 1\]                              | Music volume level
NOTIFICATION_VOLUME | \[0, 1\]                              | Notifications volume level
RING_VOLUME         | \[0, 1\]                              | Ring volume level
BT_SCO              | TRUE / FALSE                          | If a Bluetooth SCO device is connected
MIC_MUTE            | TRUE / FALSE                          | If microphone is mute
MUSIC_ON            | TRUE / FALSE                          | If music is on
SPEAKER_ON          | TRUE / FALSE                          | If speaker is on
HEADSET             | TRUE / FALSE                          | If headset (bot wired and Bluetooth) is connected


### <a name="BatteryProbe"></a>BatteryProbe (type: <b>ContinuousProbe</b>)

Monitors the battery status.

#### Returns

Returns the following values:

NAME                | Value                 | Description
--------------------|-----------------------|--------------------
BATTERY_PCT         | \[0, 1\]              | Current battery level
PLUGGED             | TRUE / FALSE          | If the device is connected USB cable


### <a name="HardwareInfoProbe"></a>HardwareInfoProbe (type: <b>ContinuousProbe</b>)

Reads different information related to the device's hardware, e.g., the
Wi-Fi Mac Address, the phone model, or the phone number associated to
the SIM card.
These information can be used to uniquely identify the user/device.

#### Required Permissions

```
android.permission.ACCESS_WIFI_STATE
android.permission.CHANGE_WIFI_STATE
android.permission.BLUETOOTH
android.permission.READ_PHONE_STATE
android.permission.INTERNET
```

#### Returns

Returns the following information:

NAME                | Description
--------------------|--------------------------------------------
ANDROID_ID          | A 64-bit number, unique to each combination of app-signing key, user, and device. It is available only on Android API >= 26.
WI_FI_MAC           | The MAC address associated to the Wi-Fi interface
WI_FI_P2P_MAC       | The MAC address associated to the Wi-Fi P2P interface
BT_MAC              | The MAC address associated to the Bluetooth interface
BRAND               | The device's brand
MODEL               | The device's model
DEVICE_ID           | The unique device ID (i.e., the IMEI for GSM and the MEID or ESN for CDMA phones)
PHONE_NUMBER        | The phone number associated to the SIM card.


### <a name="DisplayProbe"></a>DisplayProbe (type: <b>onEventProbe</b>)

Monitors the display status.

#### Returns

Returns the following information:

NAME                | Value and Description
--------------------|--------------------------------------------
STATE               | _0_ (UNKNOWN), _1_ (OFF), _2_ (ON), _3_ (DOZE), _4_ (DOZE_SUSPEND). See the Android's [Display](https://developer.android.com/reference/android/view/Display.html) class.
ROTATION            | _0_ (NO_ROTATION), _1_ (ROTATION_90), _2_ (ROTATION_180), _3_ (ROTATION_270). See the Android's [getRotation()](https://developer.android.com/reference/android/view/Display.html#getRotation()) method.


### <a name="WeatherProbe"></a>WeatherProbe (type: <b>ContinuousProbe</b>)

Monitors the current weather conditions (e.g., temperature, humidity,
etc.) at the current device location.
Weather information comes from the [OpenweatherMap](http://www.openweathermap.com) web service.

#### Parameters

```java
int unit
```
The unit format to use. Possible formats are the following:
_1 = Fahrenheit_, _2 = Celsius_, _3 = Kelvin_.

```java
String appId
```
The API key required by OpenweatherMap to access the weather API.
For more information, see the following
[link](http://openweathermap.org/appid).

#### Required Permissions

```
com.google.android.gms.permission.ACCESS_FINE_LOCATION
android.permission.INTERNET
```

#### Returns

Returns the following information:

NAME                       | Value and Description
---------------------------|--------------------------------------------
WEATHER_CODE               | The weather conditions ID according to the codes defined in http://www.openweathermap.com/weather-conditions
TEMPERATURE                | Current temperature degrees
TEMP_MIN                   | Minimum temperature at the moment
TEMP_MAX                   | Maximum temperature at the moment
HUMIDITY                   | Percentage of humidity
PRESSURE                   | Atmospheric pressure
WIND_SPEED                 | Wind speed
WIND_DIRECTION             | Wind direction in degrees
CLOUDINESS                 | Percentage of cloudiness
RAIN_3H                    | Rain volume for the last 3 hours
SNOW_3H                    | Snow volume for the last 3 hours
SUNRISE_TIME               | Sunrise time (unix UTC timestamp)
SUNSET_TIME                | Sunset time (unix UTC timestamp)


### <a name="MultimediaProbe"></a>MultimediaProbe (type: <b>OnEventProbe</b>)

Monitors the creation of new multimedia files (i.e., pictures and videos).

#### Required Permissions

```
android.permission.READ_EXTERNAL_STORAGE
```

#### Returns

Returns the following information:

NAME                       | Value and Description
---------------------------|--------------------------------------------
FILE_NAME                  | The file name


# Telephony Probes


### <a name="CellInfoProbe"></a>CellInfoProbe (type: <b>ContinuousProbe</b>)

Monitors all observed cell information from all radios on the device
including the primary and neighboring cells.

#### Required Permissions

```
android.permission.ACCESS_COARSE_LOCATION
android.permission.READ_PHONE_STATE
```

#### Returns

For each device in proximity, returns the following information:

NAME                | Value and Description
--------------------|--------------------------------------------
CELL_TYPE           | _0_ (GSM), _1_ (LTE), _2_ (CDMA), _3_ (WCDMA)
BS_ID               | The Base Station ID. Refer to the [android.telephony.CellInfo](https://developer.android.com/reference/android/telephony/CellInfo.html) subclasses for the exact values.
SIGNAL_LEVEL        | The signal strength as dBm.


### <a name="CallsProbe"></a>CallsProbe (type: <b>OnEventProbe</b>)

Monitors both incoming and outgoing phone calls.

#### Required Permissions

```
android.permission.PROCESS_OUTGOING_CALLS
android.permission.READ_PHONE_STATE
```

#### Returns

For each phone call, returns the following information:

NAME                | Value and Description
--------------------|--------------------------------------------
PHONE_NUMBER        | Phone number
INCOMING            | _TRUE/FALSE_ if it is an incoming call


# Calendar

### <a name="CurrentEventsProbe"></a>CurrentEventsProbe (type: <b>ContinuousProbe</b>)

Reports the list of events that are active at a given time.

#### Required Permissions

```
android.permission.READ_CALENDAR
```

#### Returns

For each event, returns the following information:

NAME                | Value and Description
--------------------|--------------------------------------------
CALENDAR            | Name of the calendar (if available)
START               | When the event starts
END                 | When the event ends
TITLE               | The event's title
LOCATION            | Location of the event (if present)
ALL_DAY             | _TRUE/FALSE_ if the event lasts all the day


# Bluetooth


### <a name="BluetoothConnProbe"></a>BluetoothConnProbe (type: <b>OnEventProbe</b>)

Monitors the connections to bluetooth devices.

#### Required Permissions

```
android.permission.BLUETOOTH
```

#### Returns

For each connected device, returns the following information:

NAME                | Value and Description
--------------------|--------------------------------------------
NAME                | Device name (if present)
ADDRESS             | The device's Bluetooth MAC address.
BT_CLASS            | The device's [Bluetooth Major ID](https://developer.android.com/reference/android/bluetooth/BluetoothClass.Device.Major.html)
RSSI                | Always "null" here


### <a name="BluetoothScanProbe"></a>BluetoothScanProbe

Continuously performs Bluetooth scans to discover other devices in
proximity.

#### Required Permissions

```
android.permission.BLUETOOTH
android.permission.BLUETOOTH_ADMIN
```

#### Returns

For each device in proximity, returns the following information:

NAME                | Value and Description
--------------------|--------------------------------------------
NAME                | Device name (if present)
ADDRESS             | The device's Bluetooth MAC address.
BT_CLASS            | The device's [Bluetooth Major ID](https://developer.android.com/reference/android/bluetooth/BluetoothClass.Device.Major.html)
RSSI                | Signal strength


# Wi-Fi


### <a name="WiFiP2PProbe"></a>WiFiP2PProbe

Continuously performs Wi-Fi P2P scans to discover other devices in
proximity.

```
android.permission.CCESS_COARSE_LOCATION
android.permission.ACCESS_WIFI_STATE
android.permission.CHANGE_WIFI_STATE
android.permission.INTERNET
```

#### Returns

For device in proximity, returns the following information:

NAME                | Value and Description
--------------------|--------------------------------------------
ADDRESS             | The Wi-Fi P2P Mac address


### <a name="WiFiProbe"></a>WiFiProbe

Continuously performs Wi-Fi scans to discover Access Points in proximity.

```
android.permission.CCESS_COARSE_LOCATION
android.permission.ACCESS_WIFI_STATE
android.permission.CHANGE_WIFI_STATE
android.permission.ACCESS_NETWORK_STATE
```

#### Returns

For Access Point (AP) in proximity, returns the following information:

NAME                | Value and Description
--------------------|--------------------------------------------
SSID                | The AP's SSID
BSSID               | The AP's BSSID
RSSI                | The RSSI level in the \[0,4\] range
SIG_LEVEL           | The raw RSSI level
CAPABILITIES        | Authentication, key management, and encryption schemes supported by the AP
FREQUENCY           | The primary 20 MHz frequency (in MHz) of the channel over which the client is communicating with the access point
CONNECTED           | _TRUE/FALSE_ if the device is currently connected to the Access Point
CONFIGURED          | _TRUE/FALSE_ if the AP is in the list of already configured WIFi Networks


# Sensors


### <a name="ActivityRecognitionProbe"></a>ActivityRecognitionProbe

Monitors the current user activity using the Android's [Activity
Recognition API](https://developers.google.com/location-context/activity-recognition/).

#### Parameters

```java
long updateInterval
```
Controls the activity detection update interval (in <b>seconds</b>).
Larger values will result in fewer activity detections while improving
battery life.
Smaller values will result in more frequent activity detections but
will consume more power since the device must be woken up more
frequently.

**Default value**: 300 seconds (i.e., 5 minutes).

#### Required Permissions

```
com.google.android.gms.permission.ACTIVITY_RECOGNITION
```

#### Returns

Returns the following list of activities, where each of them is
associated to its confidence value (i.e., an int value in \[0, 100\])

Activity      | Description
--------------|--------------------------------------------
IN_VEHICLE    | The device is in a vehicle, such as a car.
ON_BICYCLE    | The device is on a bicycle.
ON_FOOT       | The device is on a user who is walking or running.
RUNNING       | The device is on a user who is running.
STILL         | The device is still (not moving).
TILTING       | The device angle relative to gravity changed significantly.
WALKING       | The device is on a user who is walking.
UNKNOWN       | Unable to detect the current activity.


### <a name="EnvironmentSensorProbe"></a>EnvironmentSensorProbe

Monitors sensors that measure various environmental parameters, such as
ambient air temperature and pressure, illumination, and humidity.
Specifically, this probe monitors the following sensors:

* **TEMPERATURE** : Ambient air temperature in °C.
* **LIGHT** : Illuminance in lx.
* **PRESSURE** : Ambient air pressure in hPa or mbar, it depends on the hardware.
* **HUMIDITY** : The percentage of the ambient relative humidity.

For a full description of these sensors, please refer to the [Android's
official documentation](https://developer.android.com/guide/topics/sensors/sensors_environment.html).

#### Parameters

```java
int maxSamples
```
Maximum number of samples for each sensor to use for the statistics.

#### Returns

For each sensor, returns the following statistics calculated by the
[Apache Commons Math](http://commons.apache.org/proper/commons-math/javadocs/api-3.3/org/apache/commons/math3/stat/descriptive/DescriptiveStatistics.html) library:

NAME                | Value and Description
--------------------|--------------------------------------------
MIN                 | Minimum
MAX                 | Maximum
MEAN                | Arithmetic mean
UAD_MEAN            | Quadratic mean
25_PERC             | 25<sup>th</sup> Percentile
50_PERC             | 50<sup>th</sup> Percentile
75_PERC             | 75<sup>th</sup> Percentile
100_PERC            | 100<sup>th</sup> Percentile
VAR                 | Variance
POP_VAR             | Population variance
SKEW                | Skewness
SUM_SQ              | Sum of the squares
STD                 | Standard deviation
KURT                | Kurtosis

Therefore, for each reading, this probe returns an array of
4 (Sensors' dimensions) * 14 (Statistics) = **56 elements**.


### <a name="MotionSensorsProbe"></a>MotionSensorsProbe

Monitors sensors that measure acceleration forces and rotational forces
along three axes. This category includes accelerometers, gravity
sensors, gyroscopes, and rotational vector sensors.
Specifically, this probe monitors the following sensors:

* **ACCELEROMETER** : Acceleration force along the _x,y,z_ axes in m/s<sup>2</sup>
* **GRAVITY** : Force of gravity along the _x,y,z_ axes in m/s<sup>2</sup>
* **GYROSCOPE** : Rate of rotation along the _x,y,z_ axes in rad/s
* **ACCELERATION** : Acceleration force along the _x,y,z_ axes in m/s<sup>2</sup>
* **ROTATION** : Rotation vector component along the _x,y,z_ axes

For a full description of these sensors, please refer to the [Android's
official documentation](https://developer.android.com/guide/topics/sensors/sensors_motion.html).

#### Parameters

```java
int maxSamples
```
Maximum number of samples for each sensor to use for the statistics.

#### Returns

For each sensor, returns the following statistics calculated by the
[Apache Commons Math](http://commons.apache.org/proper/commons-math/javadocs/api-3.3/org/apache/commons/math3/stat/descriptive/DescriptiveStatistics.html) library:

NAME                | Value and Description
--------------------|--------------------------------------------
MIN                 | Minimum
MAX                 | Maximum
MEAN                | Arithmetic mean
UAD_MEAN            | Quadratic mean
25_PERC             | 25<sup>th</sup> Percentile
50_PERC             | 50<sup>th</sup> Percentile
75_PERC             | 75<sup>th</sup> Percentile
100_PERC            | 100<sup>th</sup> Percentile
VAR                 | Variance
POP_VAR             | Population variance
SKEW                | Skewness
SUM_SQ              | Sum of the squares
STD                 | Standard deviation
KURT                | Kurtosis

Therefore, for each reading, this probe returns an array of
15 (Sensors' dimensions) * 14 (Statistics) = **210 elements**.


### <a name="PositionSensorProbe"></a>PositionSensorProbe

Monitors sensors that measure the physical position of a device. This
category includes orientation sensors and magnetometers.
Specifically, this probe monitors the following sensors:

* **GAME ROTATION** : Rotation vector component along the _x,y,z_ axes
* **GEOMAGNETIC ROTATION** : Rotation vector component along the _x,y,z_ axes
* **MAGNETIC FIELD** : Geomagnetic field strength along the _x,y,z_ axes in μT
* **PROXIMITY** : Distance from object in cm

For a full description of these sensors, please refer to the [Android's
official documentation](https://developer.android.com/guide/topics/sensors/sensors_position.html).

#### Parameters

```java
int maxSamples
```
Maximum number of samples for each sensor to use for the statistics.

#### Returns

For each sensor, returns the following statistics calculated by the
[Apache Commons Math](http://commons.apache.org/proper/commons-math/javadocs/api-3.3/org/apache/commons/math3/stat/descriptive/DescriptiveStatistics.html) library:

NAME                | Value and Description
--------------------|--------------------------------------------
MIN                 | Minimum
MAX                 | Maximum
MEAN                | Arithmetic mean
UAD_MEAN            | Quadratic mean
25_PERC             | 25<sup>th</sup> Percentile
50_PERC             | 50<sup>th</sup> Percentile
75_PERC             | 75<sup>th</sup> Percentile
100_PERC            | 100<sup>th</sup> Percentile
VAR                 | Variance
POP_VAR             | Population variance
SKEW                | Skewness
SUM_SQ              | Sum of the squares
STD                 | Standard deviation
KURT                | Kurtosis

Therefore, for each reading, this probe returns an array of
10 (Sensors' dimensions) * 14 (Statistics) = **140 elements**.


# Applications


### <a name="InstalledAppsProbe"></a>InstalledAppsProbe

Monitors the installed applications.

#### Returns

For each application, returns the following information:

NAME                | Value and Description
--------------------|--------------------------------------------
PKG_NAME            | Name of the application's package


### <a name="RunningAppsProbe"></a>RunningAppsProbe

Monitors the running applications.

#### Returns

For each application, returns the following information:

NAME                | Value and Description
--------------------|--------------------------------------------
NAME                | Process name
IMPORTANCE          | Level of importance as defined in [RunningAppProcessInfo](https://developer.android.com/reference/android/app/ActivityManager.RunningAppProcessInfo.html#importance), e.g., if the app is in foreground or background mode.


