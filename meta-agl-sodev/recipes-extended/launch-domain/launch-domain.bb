SUMMARY = "This package delivers the script, which is used to \
launch the guest domains in case when we work with the virtio device sharing."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

SRC_URI = "file://launch-domain"

S = "${UNPACKDIR}"

do_install() {
    install -d ${D}${libdir}/xen/bin
    install -m 0755 ${UNPACKDIR}/launch-domain ${D}${libdir}/xen/bin/launch-domain
}

FILES:${PN} = "${libdir}/xen/bin/launch-domain"

RDEPENDS:${PN} = "xen-tools-xenstore"
