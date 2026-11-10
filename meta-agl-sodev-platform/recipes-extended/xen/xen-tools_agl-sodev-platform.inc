FILESEXTRAPATHS:prepend := "${THISDIR}/xen-tools:"

SRC_URI += "file://weston-env.conf"

# meta-selinux is required for the checkpolicy utility.
# Technically the "selinux" feature is not required for that, only
# meta-selinux being in the layer stack, but this seems the simplest
# check.
inherit features_check
REQUIRED_DISTRO_FEATURES += "selinux"

#PACKAGECONFIG:append = " ${@bb.utils.contains_any("AGL_FEATURES", [ "agl-xen-dom0", "agl-xen-domd" ], "xsm", "", d)}"
# Always build xsm support, Xen configuration can choose whether to enable it or not
PACKAGECONFIG:append = " xsm"

# We need the QEMU used to run backends to match the dom0/domd arch
QEMU_SYSTEM:aarch64 = "qemu-system-aarch64"

do_install:append() {
    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd wayland', 'true', 'false', d)}; then
        install -d ${D}${systemd_system_unitdir}/xendriverdomain.service.d
        install -m 0644 ${UNPACKDIR}/weston-env.conf ${D}${systemd_system_unitdir}/xendriverdomain.service.d/
    fi
}

FILES:${PN}-devd += "${systemd_system_unitdir}/xendriverdomain.service.d/"

FILES:${PN}-flask += "/boot/xenpolicy-${XEN_REL}*"

# We want to use the tools in images that may not be responsible for
# managing the network, so remove default dependency.
RDEPENDS:${PN}:remove = "${PN}-net-conf"

