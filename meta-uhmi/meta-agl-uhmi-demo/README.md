# Unified HMI Preconfigured Demo Images

## Introduction
Unified HMI requires a somewhat complex environment setup, so this recipe provides instructions to create preconfigured images.

## Unified HMI frameworks
For a detailed explanation of Unified HMI, please refer to the [AGL Documentation](https://docs.automotivelinux.org/en/master/#06_Component_Documentation/11_Unified_HMI/)

## Demo environment
Demo images support three boards: qemux-86-64, raspberrypi4, and agl-refhw. 
Please prepare two boards, one as the sender and the other as the receiver, using any of the supported boards, and build the respective images for each.

* sender
```
IP: 192.168.0.121
HOSTNAME: host0
```

* receiver
```
IP: 192.168.0.122
HOSTNAME: host1
```

## How to build
Follow the [AGL documentation](https://docs.automotivelinux.org/en/master/#01_Getting_Started/02_Building_AGL_Image/01_Build_Process_Overview/) for the build process, and set up the "[Initializing Your Build Environment](https://docs.automotivelinux.org/en/master/#01_Getting_Started/02_Building_AGL_Image/04_Initializing_Your_Build_Environment/)" section as described below to enable the AGL feature 'agl-uhmi-demo'.

For example:
```
$ cd $AGL_TOP/master
$ source ./meta-agl/scripts/aglsetup.sh -m qemux86-64 -b qemux86-64 agl-devel agl-uhmi-demo
```

After adding the feature, execute the bitbake command:

For sender:
```
$ bitbake agl-uhmi-demo-preconfigured-ivi-demo-flutter
```

For receiver:
```
$ bitbake agl-uhmi-demo-preconfigured-weston
```

## How to use
After both the sender and receiver have successfully booted, set the correct IP addresses for both. Then, execute the following commands on the sender to confirm that the application is displayed across two displays, each connected to different boards.

### How to launch an application
Unified HMI v3 runs `uhmi-master-node` on the sender (`host0`) and
`uhmi-worker-node` on both images. The default application definition includes
glmark2. Use `uhmi-comm` to list applications and destinations, then launch it:

```
$ uhmi-comm -c ListAvailableApps
$ uhmi-comm -c ListDestinations
$ uhmi-comm -c LaunchApp glmark2 screen0 async
```

### How to control layouts
After launching the application, use the Unified HMI API to move it between
displays, widen it across displays, or stop it:

```
$ uhmi-comm -c MoveWindow glmark2 screen1_BR 500
$ uhmi-comm -c WidenApp glmark2 screen0 screen1
$ uhmi-comm -c StopApp glmark2
```

See the [meta-uhmi README](../README.md) for virtual-screen and application
configuration details and additional API commands.
