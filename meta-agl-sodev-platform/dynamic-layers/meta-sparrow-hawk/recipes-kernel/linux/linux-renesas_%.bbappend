#
# Configures a generic arm64 kernel for agl-xen-default (aka dom0),
# otherwise applies Xen Troops and Renesas patches/configuration if
# building for agl-xen-domd.  It may make more sense to split the
# generic kernel for dom0 back out and use a specific multiconfig
# for building dom0, further consideration required.
# 
# Adapted from:
# meta-xt-common/meta-xt-dom0/recipes-kernel/linux/linux-generic-armv8.bbappend
# meta-rcar-demo/meta-xen-dom0/recipes-kernel/linux/linux-generic-armv8.bbappend
# meta-rcar-demo/meta-xen-domd/recipes-kernel/linux/linux-renesas_%.bbappend

FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

# dom0 uses a full defconfig to force building a generic aarch64 kernel
SRC_URI:append:agl-xen-default = " file://defconfig"
AGL_KCONFIG_FRAGMENTS_HARDWARE = ""
SRC_URI:remove:agl-xen-default = "file://sparrow_hawk.cfg"

# Port Xen patch from v6.1.102/rcar-6.0.0.rc5-xt on xen-troops/linux
SRC_URI_XEN_TROOPS = " \
    file://xen_patchset/0001-xen-unpopulated-alloc-Introduce-helpers-for-contiguo.patch \
    file://xen_patchset/0002-xen-grant-table-Use-unpopulated-contiguous-pages-ins.patch \
    file://xen_patchset/0003-unpopulated-alloc.c-Drop-restriction-for-DMA_BIT_MAS.patch \
    file://xen_patchset/0004-vhost_xen-Implement-Xen-grant-mappings-module-for-vh.patch \
    file://xen_patchset/0005-vhost_xen-Get-the-guest-domid-from-Xenstore.patch \
    file://xen_patchset/0006-vhost_xen-Implement-Xen-foreign-mappings-along-with-.patch \
    file://xen_patchset/0007-vhost_xen-Adapt-net-for-Xen-specific-mappings.patch \
    file://xen_patchset/0008-vhost_xen-Change-a-logic-to-get-the-guest-domid.patch \
    file://xen_patchset/0009-Use-mhp_get_pluggable_range-in-balloon-as-well.patch \
    file://xen_patchset/0011-vhost_xen-Fix-build-error.patch \
"

# NOTE: kernel configuration in append.cfg and vsock.cfg should be further
#       investigated, and potentially rationalized into something more generic.
# NOTE: subdir parameter cannot be used to install dts files, as target
#       locations outside of UNPACKDIR are now an error.
SRC_URI:append:agl-xen-domd = " \
    ${SRC_URI_XEN_TROOPS} \
    file://0001-xen-Initial-version-of-Xen-passthrough-helper-driver.patch \
    file://0004-HACK-Allow-DomD-enumerate-PCI-devices.patch \
    file://0001-Fix-build-error-for-kernel-6.12.34.patch \
    file://0001-drm-rcar-du-make-MIPI-DSI-configuration-more-generic.patch \
    file://r8a779g3-xen-chosen.dtsi \
    file://r8a779g3-xen.dts \
    file://r8a779g3-domd.dts \
    file://r8a779g3-sparrow-hawk-domd.dts \
    file://r8a779g3-sparrow-hawk-xen.dts \
    file://append.cfg \
    file://vsock.cfg \
"

do_patch[postfuncs] += "install_dts_files"

install_dts_files() {
    install -d ${S}/arch/arm64/boot/dts/renesas
    uris="${@' '.join(list(filter(lambda x: x.endswith('dts') or x.endswith('dtsi'), (d.getVar('SRC_URI') or '').split())))}"
    for uri in $uris; do
        f=`basename $uri`
	install -m 0644 ${UNPACKDIR}/$f ${S}/arch/arm64/boot/dts/renesas/
    done
}

# Ignore in-tree defconfig in favour of our supplied one for dom0
KBUILD_DEFCONFIG:agl-xen-default = ""

# Only build specific DTBs
KERNEL_DEVICETREE:agl-xen-default = ""
KERNEL_DEVICETREE:agl-xen-domd = " \
    renesas/r8a779g3-sparrow-hawk-xen.dtb \
    renesas/r8a779g3-sparrow-hawk-domd.dtb \
"

# For dt-overlay
KERNEL_DTC_FLAGS += "-@"
