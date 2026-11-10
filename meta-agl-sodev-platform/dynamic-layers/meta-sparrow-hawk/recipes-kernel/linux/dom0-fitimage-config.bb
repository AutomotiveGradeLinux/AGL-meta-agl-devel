SUMMARY = "AGL Renesas V4H Xen + dom0 Fit image configuration"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

SRC_URI = "file://fit-image.its \
           file://boot.cmd \
"

S = "${UNPACKDIR}"

inherit deploy

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_deploy() {
    install -d ${DEPLOYDIR}/${BPN}
    install -m 0644 ${UNPACKDIR}/fit-image.its ${DEPLOYDIR}/${BPN}/
    install -m 0644 ${UNPACKDIR}/boot.cmd ${DEPLOYDIR}/${BPN}/
}

addtask deploy after do_install before do_build

ALLOW_EMPTY:${PN} = "1"
