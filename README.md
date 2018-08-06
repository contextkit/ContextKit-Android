# Context Kit

[ ![Download](https://api.bintray.com/packages/matbell/ASK/ASK/images/download.svg) ](https://bintray.com/matbell/ASK/ASK/_latestVersion)
[![License: LGPL v3](https://img.shields.io/badge/License-LGPL%20v3-blue.svg)](https://www.gnu.org/licenses/lgpl-3.0)

![alt text](https://user-images.githubusercontent.com/1859476/34232195-14e95404-e594-11e7-8cc8-6361b50433d5.png)

**C**ontext**K**it (**CK**) is a development framework
written in Java, designed for large-scale sensing experiments with
Android devices.
It supports multiple sensors such as Location, Motion, and Proximity
(using both Bluetooth and Wi-Fi Direct).
CK provides also some functionalities that can be used to characterize
the users' behaviour and interests, such as the apps usage statistics,
activity recognition, calls and messaging logs.

Due to its modular design, a developer can easily extend CK
implementing his/her own functionalities or reusing part of the
built-in features offered by the framework.
In addition, CK provides a useful set of features to automatically
store and uploads the sensed information to remote servers, simplifying
the data collection during the experiments.

This framework is freely inspired by similar works (e.g.,
[Funf Open Sensing Framework](http://funf.org), or
[SensingKit](https://sensingkit.org)) but, differently from
them, CK supports both the features and building process of the most
recent versions of the Android operating system.

## Built-in probes

CK provides a set of built-in probes to gather rich data about the
users' activities, behaviour, and social relationships. The currently
implemented built-in probes are able to acquire the following
data:

   * **Audio**
   * **Battery**
   * **Weather**
   * **General hardware info**
   * **Display**
   * **Telephony** (cells, calls, and sms/mms)
   * **Calendar** events
   * **Bluetooth** connections and devices in proximity
   * **Wi-Fi** Access Points and **Wi-Fi Direct** devices in proximity
   * **Activity** Recognition
   * **Raw Sensors** (e.g., temperature, light, accelerometer, etc...)
   * **Location**
   * **Applications** (installed and running statistics)

## Install

To install CK, add the following dependency to your `.gradle` files.

Add `jitpack.io` as repository
```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
 ```
 
and then, add the dependency:
```
dependencies {
    implementation 'com.github.contextkit:ContextKit-Android:1.0.1'
}
```

## Using the library

### Configuration

CK configuration is in the JSON format. For example, the following
configuration requires to monitor two different things: the user's
activity (`ActivityRecognitionProbe`), and the battery status
(`BatteryProbe`). The first probe is executed every 5 minutes and data
related to the user's activity is stored in a file named `activity.csv`,
while the second probe is executed every 60 seconds and stores its data
in another file, called `battery.csv`.

```json
{
    "probes" : [
        {
            "name" : "ActivityRecognitionProbe",
            "updateInterval" : 300,
            "logFile" : "activity.csv"
        },
        {
            "name" : "BatteryProbe",
            "interval" : 60,
            "logFile" : "battery.csv"
        }
    ]
}
```

### Start CK

Starting CK is very simple. The following lines load the configuration
and start the background service which executes the required probes
until it is stopped.

```java
CK ck = new CK(this, getResources().getString(R.string.ck_conf));
ck.start();
```

### Stop CK

To stop the probes execution, use the following code:

```java
ck.stop();
```

## License

```
Copyright (c) 2017. Mattia Campana, m.campana@iit.cnr.it, Franca Delmastro, franca.delmastro@iit.cnr.it

This file is part of Context Kit (CK).

Context Kit (CK) is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

Context Kit (CK) is distributed in the hope that it will be
useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Context Kit (CK).  If not, see
<http://www.gnu.org/licenses/>.`
```

This library is available under the GNU Lesser General Public License 3.0, allowing to use the library in your applications.
