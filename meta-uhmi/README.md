## Unified HMI Support Layer (meta-uhmi)
"Unified HMI" is a common platform technology for UX innovation in integrated cockpits, by flexible information display on multiple displays of various applications. Applications can be rendered to any display via a unified virtual display.

Unified HMI consists of two main components.

* Remote Virtio GPU Device(RVGPU): Render applications remotely in different SoCs/VMs, even when different OSes are running.
* Distributed Display Framework(DDFW): Flexible layout control of applications across multiple displays and lifecycle management of applications.

The meta-uhmi provides Unified HMI v3.0.

The meta-uhmi currently supports x86, Raspberry Pi4, and Renesas Boards platforms.
There are two layers under meta-uhmi to support these two frameworks.

## Remote Virtio GPU Device(RVGPU)
RVGPU rendering engine, operating on a client-server model, creates 3D rendering commands on a client device and transmits them to a server device via the network, where the server-side device performs GPU-based rendering and displays the results.

RVGPU currently supports the following images: `agl-image-weston`, `agl-ivi-demo-qt`, `agl-ivi-demo-flutter`

RVGPU is open-source software (OSS). For more details, visit the [Unified HMI - Remote Virtio GPU](https://github.com/unified-hmi/remote-virtio-gpu).

## Distributed Display Framework(DDFW)
DDFW is an open-source software layer that provides essential services for managing distributed display applications. It maps the physical displays of the cockpit into a single large virtual screen, controls application layouts (location, size, and display order) on that virtual screen, and manages the lifecycle of applications and RVGPU modules.

Since Unified HMI v3.0, the former Unified Clustering Tools (UCL) and Unified Layout Tools (ULA) are merged into one framework and released as the [unified-hmi](https://github.com/unified-hmi/unified-hmi) repository. The main features of v3.0 are:

* Simplified APIs: launching an application and laying it out on any display takes a single API call, without preparing JSON files at runtime.
* Dynamic layout control at runtime, such as moving windows with smooth animation, mirroring applications, and priority groups.

unified-hmi consists of the following commands:

* `uhmi-master-node`: Central gRPC server that hosts the Unified HMI API (`UHMIService`). It runs only on the manager node.
* `uhmi-worker-node`: Per-node agent that runs on all nodes. It launches applications and RVGPU modules (`rvgpu-proxy`, `rvgpu-wlproxy`, and `rvgpu-renderer`), and applies layouts.
* `uhmi-comm`: Sample command line client that sends gRPC commands to `uhmi-master-node`.

The Unified HMI API is exposed with gRPC (defined in [proto/uhmi.proto](https://github.com/unified-hmi/unified-hmi/blob/main/proto/uhmi.proto)). Once `uhmi-master-node` is running, you can call the API from any application or from the command line with `uhmi-comm`. Reference implementations of gRPC clients for Go and Dart are also available in the repository.

For more details about the framework, please visit our GitHub repository.

## How to build
Follow the [AGL documentation](https://docs.automotivelinux.org/en/master/#01_Getting_Started/02_Building_AGL_Image/01_Build_Process_Overview/) for the build process, and set up the "[Initializing Your Build Environment](https://docs.automotivelinux.org/en/master/#01_Getting_Started/02_Building_AGL_Image/04_Initializing_Your_Build_Environment/)" section as described below to enable the AGL feature 'agl-uhmi'. 

For example:
```
$ cd $AGL_TOP/master
$ source ./meta-agl/scripts/aglsetup.sh -m qemux86-64 -b qemux86-64 agl-demo agl-devel agl-uhmi
```

To add RVGPU and DDFW to the build images, please add the packagegroup that enable RVGPU(packagegroup-rvgpu) and DDFW(packagegroup-ddfw) to the installation target using the following command.
```
$ echo 'IMAGE_INSTALL:append = " packagegroup-rvgpu packagegroup-ddfw"' >> conf/local.conf
```
packagegroup-ddfw installs `unified-hmi` (`uhmi-master-node`, `uhmi-worker-node`, `uhmi-comm`), its systemd services, and the default configuration files under `/etc/uhmi-framework/`.

**Note**: RVGPU can be used independently without DDFW. However, DDFW uses RVGPU to render applications, so if you use DDFW, please add both packagegroups to the installation target.

After adding the feature, execute the command:
```
$ bitbake <image_name>
```
Replace the `<image_name>` with the appropriate values you want. We have confirmed the operation with the **agl-ivi-demo-flutter**.

## How to setup and boot
For Environment setup instructions for each platform, refer to the following links in the AGL Documentation:
[Building for x86(Emulation and Hardware)](https://docs.automotivelinux.org/en/master/#01_Getting_Started/02_Building_AGL_Image/06_Building_the_AGL_Image/02_Building_for_x86_%28Emulation_and_Hardware%29/)
[Building for Raspberry Pi 4](https://docs.automotivelinux.org/en/master/#01_Getting_Started/02_Building_AGL_Image/06_Building_the_AGL_Image/03_Building_for_Raspberry_Pi_4/)
[Building for Supported Renesas Boards](https://docs.automotivelinux.org/en/master/#01_Getting_Started/02_Building_AGL_Image/06_Building_the_AGL_Image/04_Building_for_Supported_Renesas_Boards/)

For Raspberry Pi 4 and Supported Renesas Boards, refer to the above URL for boot methods.
For x86 emulation (qemu), network bridge is required to enable communication with other devices when using RVGPU. 

Here’s an example procedure for your reference.
```
$ sudo ip link add <bridge_name> type bridge
$ sudo ip addr add <IP address> dev <bridge_name>
$ sudo ip link set dev <interface> master <bridge_name>
$ sudo ip link set dev <bridge_name> up
```
Replace the placeholders with the appropriate values:
- `<bridge_name>`: You can assign any name, for example: `br0`
- `<IP_address>`: Enter an available IP address, for example: `192.168.0.100/24`
- `<interface>`: Specify the network interface, for example: `eth0`

To enable the use of the bridge, create or append /etc/qemu directory and /etc/qemu/bridge.conf file.
```
allow <bridge_name>
```
Make sure /etc/qemu/ has 755 permissions.
Create the following bash file named **run_qemu_bridge.sh** in any `<WORKDIR>`.
```
#!/bin/bash

KERNEL_PATH=$1
DRIVE_PATH=$2
BRIDGE_NAME="<bridge_name>"

printf -v macaddr "52:54:%02x:%02x:%02x:%02x" $(( $RANDOM & 0xff)) $(( $RANDOM & 0xff )) $(( $RANDOM & 0xff)) $(( $RANDOM & 0xff ))

qemu-system-x86_64 -enable-kvm -m 2048 \
    -kernel ${KERNEL_PATH} \
    -drive file=${DRIVE_PATH},if=virtio,format=raw \
    -cpu kvm64 -cpu qemu64,+ssse3,+sse4.1,+sse4.2,+popcnt \
    -vga virtio -show-cursor \
    -device virtio-net-pci,netdev=net0,mac=$macaddr \
    -netdev bridge,br=$BRIDGE_NAME,id=net0 \
    -serial mon:stdio -serial null \
    -soundhw hda \
    -append 'root=/dev/vda swiotlb=65536 rw console=ttyS0,115200n8 fstab=no'
```
Save the file and run the following to start QEMU.
```
sudo <WORKDIR>/run_qemu_bridge.sh <build_directory>/tmp/deploy/images/qemux86-64/bzImage <build_directory>/tmp/deploy/images/qemux86-64/agl-ivi-demo-flutter-qemux86-64.rootfs.ext4
```
When QEMU boot, you can log in to qemu on the terminal where you executed above command, so please assign an IP address there.

For example:
```
ifconfig <your environment> 192.168.0.121 netmask 255.255.255.0
```

## How to run RVGPU remotely
RVGPU uses `rvgpu-renderer` as the server on the Receiver and `rvgpu-proxy` as the client on the Sender. The two images must be able to communicate over the network. The following example uses a 1280x720 display and port 55667.

**Receiver side** (run in the active Wayland session):
```
$ export XDG_RUNTIME_DIR=/run/user/<your_UID>
$ rvgpu-renderer -b 1280x720@0,0 -p 55667
```

Keep `rvgpu-renderer` running. On the Sender, load the required kernel modules and start the proxy as root:
```
$ sudo modprobe virtio-gpu
$ sudo modprobe virtio-lo
$ sudo rvgpu-proxy -s 1280x720@0,0 -n <Receiver_IP_address>:55667 &
```

Replace `<your_UID>` with the UID of the user running the Wayland compositor and `<Receiver_IP_address>` with the Receiver's IP address. If the kernel modules are already loaded, the `modprobe` commands can be skipped.

In the same Sender session, start `rvgpu-wlproxy` on the GPU device created by `rvgpu-proxy`:
```
$ export XDG_RUNTIME_DIR=/tmp
$ export LD_LIBRARY_PATH=/usr/lib/mesa-virtio
$ export EGLWINSYS_DRM_DEV_NAME=/dev/dri/rvgpu_virtio0
$ rvgpu-wlproxy -s 1280x720 -S wayland-rvgpu-0 &
```

`EGLWINSYS_DRM_DEV_NAME` must point to the RVGPU DRM device created on the Sender. Adjust the device path if its generated name differs. Once `rvgpu-wlproxy` is running, launch `glmark2-es2-wayland` using its Wayland socket:
```
$ WAYLAND_DISPLAY=wayland-rvgpu-0 glmark2-es2-wayland -s 1280x720
```

The rendered output should appear in the `rvgpu-renderer` window on the Receiver. Keep the renderer, proxy, and wlproxy processes running while using the application. Ensure that the Sender can reach the Receiver on the selected port.

## How to set up Distributed Display Framework (DDFW)

To utilize DDFW, you need to set up each image with a distinct hostname and static IP address. Please note that the following configurations are examples and should be adapted to fit your specific network environment.

In the following examples, we assume two images, `host0` and `host1`. `host0` is the manager node that runs `uhmi-master-node`, and both images run `uhmi-worker-node`.

### Set Unique Hostnames

Edit the `/etc/hostname` file on each image to set a unique hostname. Here are the suggested hostnames for the first two images:

- **For the first image:**
```
echo "host0" | tee /etc/hostname
```
- **For the second image:**
```
echo "host1" | tee /etc/hostname
```

**Note**: Unified HMI identifies each node by its hostname, so the hostnames must match the `hostname` fields defined in virtual-screen-def.json.

### Assign Static IP Addresses

Configure a static IP address for each image with ConnMan. On each image, list the available services and identify the wired Ethernet service ID (usually shown as `ethernet_..._cable`):

```
$ connmanctl services
```

Run the following command as root, replacing `<wired_service_id>` with the service ID shown by `connmanctl services`. ConnMan requires an address, netmask, and gateway for manual IPv4 configuration:

```
$ connmanctl config <wired_service_id> ipv4 manual <IP_address> <Netmask> <Gateway>
```

For example, assuming the network gateway is `192.168.0.1`, configure the first two images as follows:

- **For the first image:**
```
$ connmanctl config <host0_wired_service_id> ipv4 manual 192.168.0.121 255.255.255.0 192.168.0.1
```
- **For the second image:**
```
$ connmanctl config <host1_wired_service_id> ipv4 manual 192.168.0.122 255.255.255.0 192.168.0.1
```

Replace the example gateway with the gateway used by your network. ConnMan applies the settings immediately; verify the assigned addresses with `ip addr`.

### Customizing Virtual Screen Definitions

Adjust the `/etc/uhmi-framework/virtual-screen-def.json` file to match your environment. This file is required on all images, and its contents must be the same on all images. In the following example, we assume an environment where two images, host0 and host1, each have a display output with a resolution of 1920x1080(FullHD), arranged in order from left to right as host0 and then host1:

```
{
    "virtual_screen_2d": {
        "size": {"virtual_w": 3840, "virtual_h": 1080},
        "virtual_displays": [
            {"vdisplay_id": 0, "disp_name": "screen0", "virtual_x": 0, "virtual_y": 0, "virtual_w": 1920, "virtual_h": 1080},
            {"vdisplay_id": 1, "disp_name": "screen1", "virtual_x": 1920, "virtual_y": 0, "virtual_w": 1920, "virtual_h": 1080}
        ],
        "virtual_display_areas": [
            {"area_name": "screen0_TR", "virtual_x": 960, "virtual_y": 0, "virtual_w": 960, "virtual_h": 540},
            {"area_name": "screen1_BR", "virtual_x": 2880, "virtual_y": 540, "virtual_w": 960, "virtual_h": 540}
        ]
    },
    "real_displays": [
        {"node_id": 0, "vdisplay_id": 0, "pixel_w": 1920, "pixel_h": 1080, "rdisplay_id": 0},
        {"node_id": 1, "vdisplay_id": 1, "pixel_w": 1920, "pixel_h": 1080, "rdisplay_id": 0}
    ],
    "node": [
        {"node_id": 0, "hostname": "host0", "ip": "192.168.0.121", "platform": "linux"},
        {"node_id": 1, "hostname": "host1", "ip": "192.168.0.122", "platform": "linux"}
    ],
    "distributed_window_system": {
        "uhmi_master_node": {"node_id": 0, "port": 6643},
        "framework_node": [
            {"node_id": 0, "env": ["WAYLAND_DISPLAY=wayland-0", "XDG_RUNTIME_DIR=/run/user/1001"]},
            {"node_id": 1, "env": ["WAYLAND_DISPLAY=wayland-0", "XDG_RUNTIME_DIR=/run/user/1001"]}
        ]
    }
}
```

In this configuration:

- **virtual_displays** defines the virtual displays on the virtual screen. The `disp_name` of each virtual display can be used as a destination to place applications.
- **virtual_display_areas** defines optional named areas on the virtual screen. The `area_name` of each area can also be used as a destination. In addition, built-in sub-areas such as `<disp_name>_LEFT`, `<disp_name>_RIGHT`, `<disp_name>_TOP`, `<disp_name>_BOTTOM`, `<disp_name>_TL`, `<disp_name>_TR`, `<disp_name>_BL`, and `<disp_name>_BR` are available for each virtual display without defining them.
- **real_displays** maps the physical display of each node to a virtual display.
- **node** defines the hostname and IP address of each node.
- **uhmi_master_node** specifies the node that runs `uhmi-master-node` and the port of its gRPC server.
- **framework_node** specifies the environment variables passed to applications and RVGPU modules launched on each node.

By setting `XDG_RUNTIME_DIR=/run/user/<your_UID>` and `WAYLAND_DISPLAY=<your_WAYLAND_DISPLAY>` correctly in the `env` of `framework_node`, you can display the applications launched by Unified HMI on the Wayland compositor (e.g., agl-compositor, Weston, etc...).
Please execute `$ls /run/user/` in your environment to check <your_UID>.
Additionally, when the Wayland compositor is launched, <your_WAYLAND_DISPLAY> will be generated under /run/user/<your_UID>, so please check that as well.

Be sure to update the virtual_w, virtual_h, virtual_x, virtual_y, pixel_w, pixel_h, hostname, ip, and env fields to accurately reflect your specific environment.

### Customizing Application Definitions

Adjust the `/etc/uhmi-framework/app-list-def.json` file to define the applications launched by Unified HMI. This file is required only on the manager node that runs `uhmi-master-node`. Here is an example of `app-list-def.json`:

```
{
    "apps": [
        {
            "app_name": "glmark2",
            "sender": "host0",
            "app_command": "/usr/bin/glmark2-es2-wayland -s 1920x1080",
            "app_size": {"w": 1920, "h": 1080},
            "command_env": "LD_LIBRARY_PATH=/usr/lib/mesa-virtio",
            "primary": true
        }
    ]
}
```

In this configuration:

- **app_name**: The name used to specify the application in the Unified HMI API.
- **sender**: The hostname of the node where the application runs.
- **app_command**: The command line to launch the application.
- **app_size**: The size of the application window.
- **command_env**: Additional environment variables for the application. To render the application with RVGPU, specify `LD_LIBRARY_PATH=/usr/lib/mesa-virtio`.
- **primary**: If `true`, the application belongs to the `Primary` priority group. Otherwise, it belongs to the `General` priority group. Applications in the `Primary` group are always displayed in front of applications in the `General` group.

### Restarting Services

The systemd services `uhmi-master-node` and `uhmi-worker-node` are enabled by default. Once you have prepared the virtual-screen-def.json and app-list-def.json files and configured the system, you need to restart the system or the following services for the changes to take effect.

Please execute the following command on all nodes.
```
systemctl restart uhmi-worker-node
```

Please execute the following command only on the node specified as uhmi_master_node in the virtual-screen-def.json file.
```
systemctl restart uhmi-master-node
```

After restarting these services, your system should be ready to use the DDFW with the new configuration.

## How to use Unified HMI API

`uhmi-comm` is a sample command line client of the Unified HMI API. It reads `/etc/uhmi-framework/virtual-screen-def.json` to find `uhmi-master-node`, so you can run it on any node defined in the file. Commands are selected with the `-c` option.

**Note**: Every command of `uhmi-comm` calls the Unified HMI gRPC API, so your applications can also call the same API directly. For more details, please refer to the [unified-hmi](https://github.com/unified-hmi/unified-hmi) repository.

### Launching an Application

You can launch an application defined in app-list-def.json and place it on any destination with a single command. `uhmi-master-node` launches the application and RVGPU modules on the sender and receiver nodes, and applies the layout automatically.

```
uhmi-comm -c ListAvailableApps
uhmi-comm -c ListDestinations
uhmi-comm -c LaunchApp <appName> <destName> [async]
```

- `<appName>`: The `app_name` defined in app-list-def.json, for example: glmark2
- `<destName>`: A destination listed by `ListDestinations`, such as a virtual display (`screen0`), a virtual display area (`screen1_BR`), or a built-in sub-area (`screen0_LEFT`).
- `async` (optional): Return immediately instead of blocking until the application exits.

For example, the following command launches glmark2 on host0 and displays it on the display of host0:
```
uhmi-comm -c LaunchApp glmark2 screen0 async
```

You can check and stop the running applications with the following commands.
```
uhmi-comm -c ListRunningApps
uhmi-comm -c StopApp <appName>
uhmi-comm -c StopAll
```

### Controlling Layouts at Runtime

You can change the layout of the running applications at runtime with the following commands.

**Move an application window**

```
uhmi-comm -c MoveWindow <appName> <destName> [durationMs]
```

The window moves to the destination with smooth animation. `durationMs` (optional) is the animation duration in milliseconds (default: 1000). For example, the following command moves glmark2 from the display of host0 to the bottom-right area of the display of host1 in 500 ms:
```
uhmi-comm -c MoveWindow glmark2 screen1_BR 500
```

**Widen an application window across two displays**

```
uhmi-comm -c WidenApp <appName> <dispName1> <dispName2>
```

For example, the following command renders glmark2 across the displays of host0 and host1:
```
uhmi-comm -c WidenApp glmark2 screen0 screen1
```

**Mirror an application**

```
uhmi-comm -c CreateAppMirror <appName> <destName>
uhmi-comm -c RemoveAppMirror <mirrorName>
```

`CreateAppMirror` displays a mirror of the application on the destination and prints the name of the mirror (e.g., `glmark2_mirror0`). Use the name to remove the mirror with `RemoveAppMirror`.

**Show or hide an application**

```
uhmi-comm -c ActivateWindow <appName>
uhmi-comm -c DeactivateWindow <appName>
```

**Set priority groups**

Priority groups prevent other applications from being displayed in front of specific applications. Applications in a higher priority group are always displayed in front of applications in a lower priority group.
```
uhmi-comm -c ListPriorityGroups
uhmi-comm -c SetAppPriorityGroup <appName> <group>
```

**Note**: To check other commands and options for uhmi-comm, please use the -h option.
