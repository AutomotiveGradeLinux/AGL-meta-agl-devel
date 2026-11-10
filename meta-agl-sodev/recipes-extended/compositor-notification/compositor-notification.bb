SUMMARY = "Service for the compositor 'up and ready' notification"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

SRC_URI = "file://${BPN}.service"

S = "${UNPACKDIR}"

inherit systemd

SYSTEMD_SERVICE:${PN} = "${BPN}.service"

do_install() {
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/${BPN}.service ${D}${systemd_system_unitdir}
}

FILES:${PN} = "${systemd_system_unitdir}/${BPN}.service"

RDEPENDS:${PN} += "xen-tools-xenstore"
