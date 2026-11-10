DESCRIPTION = "Xen dom0 minimal initramfs image"
LICENSE = "MIT"

inherit core-image

IMAGE_FSTYPES = "${INITRAMFS_FSTYPES}"

IMAGE_INSTALL = " \
    packagegroup-core-boot \
    xen-tools \
    ${CORE_IMAGE_EXTRA_INSTALL} \
"

IMAGE_LINGUAS = ""

INITRAMFS_MAXSIZE ?= "256000"

PACKAGE_EXCLUDE = "kernel-image-*"

# There is no hardware visible, so udev-hwdb can be left out to reduce
# image size.
PACKAGE_EXCLUDE += "udev-hwdb"

#
# Remove some things added globally by AGL features, this needs
# further investigation into a handling things more cleanly.
#

IMAGE_FEATURES:remove = "package-management post-install-logging tools-debug tools-profile ssh-server-openssh"

IMAGE_INSTALL:remove = "packagegroup-agl-core-devel libdrm-tests"