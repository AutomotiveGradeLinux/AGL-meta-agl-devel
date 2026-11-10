#
# AGL partitioned Xen image - Linux dom0 with embeddeded domd/domu boot files
#
# This class creates a disk image that pulls together Xen and required
# guest domain images.  The expectation is it will be inherited by a
# dom0 image recipe.  The other domain images may either be build
# dependencies or sourced externally.  The resulting disk image has a
# partition layout of:
#
# - boot partition
#   - Xen and any bootloader files, dom0 files will be installed here.
# - dom0 image partition
# - domd image partition
#   - optional
# - any number of domu image partitions
#   - optional
#
# See agl-xen-image.bbclass for the configuration options for domain
# images.
#

inherit agl-xen-image-base

# Installing dom0 files into the boot partition.
DOM0_FILES_IN_BOOT = "1"

# Install the domain boot files under /boot/dom in the dom0 root filesystem
# NOTE: This should not have a leading '/'.
DOM_FILES_DIR = "boot/dom"

# There is no separate dom0 image since this bbclass assumes it will be
# inherited by an existing image intended to be dom0.
DOM0_IMAGE = ""

# Add guest artifacts to rootfs
fakeroot python do_rootfs_populate_guest_artifacts () {
    populate_guest_artifacts(d)
}

addtask do_rootfs_populate_guest_artifacts after do_rootfs before do_image
