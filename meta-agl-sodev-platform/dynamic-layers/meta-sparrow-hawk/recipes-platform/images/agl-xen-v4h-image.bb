#
# Sparrow Hawk base Xen dom0+domd image
#

# Use no-rootfs wks file since dom0 will be initramfs in FIT image
WKS_FILE = "agl-xen-bootpart-only.wks.in"
# Make boot partition large enough to handle potentially large dom0 initramfs in FIT image
BOOTPART_SIZE = "256"
WICVARS:append = " BOOTPART_SIZE"

# Make sure an initramfs compatible filesystem is generated.
# We also need to cover the case of the including recipe redefining
# IMAGE_FSTYPES as just INITRAMFS_FSTYPES, add AGL_DEFAULT_IMAGE_FSTYPES
# covers the need for both the wic type, and ext4, which logic in wic
# seems to require.
IMAGE_FSTYPES:append = " cpio.gz ${AGL_DEFAULT_IMAGE_FSTYPES}"

# Use bbclass that installs guest artifacts into the dom0 image
inherit agl-xen-dom0-image

# FIT image dependencies
DEPENDS += "u-boot-mkimage-native dtc-native virtual/kernel arm-trusted-firmware"

FITIMAGE_CONF = "dom0-fitimage-config"
# Files used for the FIT image other than fit-image.its
FITIMAGE_CONF_EXTRA_FILES = "boot.cmd"

# Install FIT image into boot partition
IMAGE_BOOT_FILES = "fitImage"

XEN_DTB_NAME = "r8a779g3-sparrow-hawk-xen.dtb"

do_generate_fit_image() {
    rm -rf ${WORKDIR}/build-fitimage
    mkdir -p ${WORKDIR}/build-fitimage
    cd ${WORKDIR}/build-fitimage
    ln -s ${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGETYPE} .
    ln -s ${DEPLOY_DIR_IMAGE}/xen-${MACHINE} xen
    ln -s ${DEPLOY_DIR_IMAGE}/xenpolicy-${MACHINE} xenpolicy
    # dtb comes from domd kernel build
    ln -s ${TOPDIR}/tmp-agl-xen-domd/deploy/images/${MACHINE}/${XEN_DTB_NAME} xen.dtb
    ln -s ${DEPLOY_DIR_IMAGE}/bl31-${MACHINE}.bin bl31.bin
    ln -s ${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.cpio.gz uInitramfs
    for f in fit-image.its ${FITIMAGE_CONF_EXTRA_FILES}; do
        cp -f ${DEPLOY_DIR_IMAGE}/${FITIMAGE_CONF}/$f .
    done
    mkimage -f fit-image.its fitImage
    cp -f fitImage ${DEPLOY_DIR_IMAGE}
}

addtask do_generate_fit_image after do_unpack do_image_cpio before do_image_wic

do_generate_fit_image[depends] += "${FITIMAGE_CONF}:do_deploy"


#
# User configurable portions below
#

require recipes-platform/images/agl-xen-dom0-minimal-initramfs.bb

IMAGE_INSTALL += " \
    kernel-modules \
    agl-v4h-domd-conf \
"

do_rootfs[mcdepends] += "mc::agl-xen-domd:initramfs-image:do_image_complete"

DOMD_IMAGE = "agl-xen-domd:agl-xen-domd-minimal"
DOM_LABEL_agl-xen-domd-minimal = "domd-rootfs"
# NOTE: The devicetree overlays are needed since they are potentially
#       used to create a final dtb for the domd in the dom0.
DOM_EXTRA_FILES_agl-xen-domd-minimal = " \
    r8a779g3-sparrow-hawk-domd.dtb \
    r8a779g3-sparrow-hawk-camera-j1-imx219.dtbo \
    r8a779g3-sparrow-hawk-camera-j2-imx219.dtbo \
    r8a779g3-sparrow-hawk-camera-j1-imx462.dtbo \
    r8a779g3-sparrow-hawk-camera-j2-imx462.dtbo \
    r8a779g3-sparrow-hawk-camera-j1-imx708.dtbo \
    r8a779g3-sparrow-hawk-camera-j2-imx708.dtbo \
    r8a779g3-sparrow-hawk-fan-pwm.dtbo \
    r8a779g3-sparrow-hawk-fan-argon40.dtbo \
    r8a779g3-sparrow-hawk-rpi-display-2-5in.dtbo \
    r8a779g3-sparrow-hawk-rpi-display-2-7in.dtbo \
    r8a779g3-sparrow-hawk-ws-display-13in.dtbo \
    r8a779g3-sparrow-hawk-olimex-dsi-hdmi.dtbo \
    initramfs-image-sparrow-hawk.rootfs.cpio.gz \
"

# DOMU_IMAGES = "" by default, can be set in derived images
