SUMMARY = "AGL Renesas V4H Xen domd configuration & systemd unit"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

SRC_URI = "file://domd.service \
           file://domd-set-root \
	   file://domd-sparrow-hawk.cfg \
"

S = "${UNPACKDIR}"

inherit systemd

SYSTEMD_SERVICE:${PN} = "domd.service"

do_compile[noexec] = "1"

do_install() {
    install -d ${D}${sysconfdir}/xen
    install -m 0644 ${UNPACKDIR}/domd-sparrow-hawk.cfg ${D}${sysconfdir}/xen/domd.cfg

    install -d ${D}${libdir}/xen/bin
    install -m 0744 ${UNPACKDIR}/domd-set-root ${D}${libdir}/xen/bin

    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${UNPACKDIR}/domd.service ${D}${systemd_unitdir}/system/
}

FILES:${PN} += "${libdir}/xen ${systemd_system_unitdir}"

RDEPENDS:${PN} += "dtc"
