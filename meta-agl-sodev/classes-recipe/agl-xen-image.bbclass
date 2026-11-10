#
# AGL partitioned Xen image
#
# This class creates a disk image that pulls together Xen and
# required guest domain images.  The domain images may either be build
# dependencies or sourced externally.  The resulting disk image has a
# partition layout of:
#
# - boot partition
#   - Xen and any bootloader files.  dom0 files may optionally
#     be installed here to support certain bootloaders.
# - dom boot partition
#   - Contains dom kernels and any other extra files in
#     subdirectories named for the domain (name can be individually
#     overridden as desired).  If a dom0 partition is configured,
#     but the dom0 files have been configured to go into the boot
#     partition, they will not be installed.  The expectation is
#     that IMAGE_BOOT_FILES will be used to install the dom0 files
#     in this case.
# - dom0 image partition
#   - optional
# - domd image partition
#   - optional
# - any number of domu image partitions
#   - optional
#
# See agl-xen-image.bbclass for the configuration options for domain
# images.
#

# Reuse the image bbclass, but clear out anything that would install
# files, as we only want deploy artifacts installed for Xen to use.
inherit image
ROOTFS_BOOTSTRAP_INSTALL = ""
ROOTFS_PREPROCESS_COMMAND = ""
IMAGE_FEATURES = ""
IMGCLASSES = "image_types image_types_wic"

inherit agl-xen-image-base

# Override to avoid any package installation.
# This is a less expensive way to get an empty rootfs than letting
# the default version run and then erasing the result.
fakeroot python do_rootfs () {
    populate_guest_artifacts(d)
}
