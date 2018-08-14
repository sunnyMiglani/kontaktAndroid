
# Repository Information

Repository is dedicated to the development of an android application to support a game called "Murder on the Orient Express".
The app communicates with the Kontakt API for Eddystone/IBeacon based BLE Beacons.

We expose variables to the JNI interface to be grabbed by the game mentioned above once they're both running on the same android phone.
Most of the development of this app has been done by Sydney Dimmock.

# Debug information

If the project doesn't compile on gradle due to a missing dependency for a `commons-math3` file, you can download and place the file [from this website](https://jar-download.com/artifacts/org.apache.commons/commons-math3/3.6.1/source-code)


# Beacon Information

Using this to document useful information/features of the Kontakt beacons.

# Data
The beacons constantly transmit data using **Advertising Packets**.
By default they send this information every **~350ms** but this can be changed.
The data they transmit is roughly **30 bytes**, more on the packet structure can be seen here: https://support.kontakt.io/hc/en-gb/articles/201492492-Advertising-packet-stcture

## UUID
This is a 32 hexidecimal string split into groups of five such as:

**ffffffff-1234-aaaa-1a2b-a1b2c3d4e5f6**

**12345678-abcd-88cc-1111aaaa2222**

**ffffffff-ffff-ffff-ffff-ffffffffffff**

It serves the purpose of distinguishing our beacons on a newtwork where
there may be others. This is unlikely to be a problem for us unless another
group is using beacons aswell.

This can be changed by us through the app.

## Major and Minor
These serve the purpose of dintinguishing beacons from one another. According to the documentation: https://support.kontakt.io/hc/en-gb/articles/201620741-iBeacon-Parameters-UUID-Major-and-Minor

**Major** is used to denote *Groups* of beacons, and **Minor** is used to identify *individual* beacons within that group. For our purposes we can follow this approach and put all of the beacons in the same major group *e.g. 1* and identify each *room* in our game by a minor value.

### Range
The app also lets us specify the effective range of each beacon, see :https://support.kontakt.io/hc/en-gb/articles/201621521-Transmission-power-Range-and-RSSI 
The minimum effective range we can go for is 2m. But we are still going to need some logic where we can handle the phone being pinged by multiple beacons and ensuring it does the correct thing in unity.
