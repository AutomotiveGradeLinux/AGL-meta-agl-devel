SUMMARY = "Remote virtual display device driver"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=688693ebbe31e3eadf819d7d007fa654"

require virtio-loopback-driver.inc

inherit module

EXTRA_OEMAKE = "M=${S} -C ${STAGING_KERNEL_DIR}"

do_install:append() {
    install -d ${D}${includedir}/linux
    install -m 644 ${S}/virtio_lo.h ${D}${includedir}/linux/
    # Add this section to install virtio_lo.conf
    install -d ${D}${sysconfdir}/modules-load.d
    echo "virtio_lo" > ${D}${sysconfdir}/modules-load.d/virtio_lo.conf
}

FILES:${PN} = " \
    ${libdir}/modules/${KERNEL_VERSION}/extra/virtio_lo* \
    ${sysconfdir}/modules-load.d \
"

# Autoload virtio lo driver
KERNEL_MODULE_AUTOLOAD:append = " virtio_lo"
