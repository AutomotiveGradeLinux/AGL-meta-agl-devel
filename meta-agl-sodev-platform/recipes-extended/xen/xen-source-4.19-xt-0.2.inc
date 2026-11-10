XEN_BRANCH = "xen-4.19-xt0.2"

FILESEXTRAPATHS:prepend := "${THISDIR}/xen-4.19-xt-0.2:"

SRC_URI = " \
    git://github.com/xen-troops/xen.git;protocol=https;branch=${XEN_BRANCH} \
    file://0001-menuconfig-mconf-cfg-Allow-specification-of-ncurses-location.patch \
    file://0001-ARM-Drop-ThumbEE-support.patch \
    file://0001-libxl-constify-some-local-variables-for-building-wit.patch \
    file://0001-Add-qemu_envs-guest-config-for-custom-environment-va.patch \
"

SRCREV = "8d17019373ad2d0928dfe9ce1ee4e3805209fc6c"

# Try to follow meta-virtualization's versioning scheme
XEN_REL = "4.19+xt"
PV = "${XEN_REL}-0.2"
