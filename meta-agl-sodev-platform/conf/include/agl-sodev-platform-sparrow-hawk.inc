# ATM this Sparrow-Hawk specific until we have more platforms that use
# a similar Linux DomD setup
BBMULTICONFIG = "agl-xen-domd agl-xen-domu"

# ATM only the 6.12 kernel has the required Xen-Troops patches
PREFERRED_VERSION_linux-renesas:sparrow-hawk = "6.12%"

# dom0 is built for a generic machine, use our virtio-aarch64
MACHINE:agl-xen-dom0 = "virtio-aarch64"

# default/dom0 does not need anything
MACHINE_EXTRA_RRECOMMENDS:agl-xen-default = ""
MACHINE_EXTRA_RRECOMMENDS:agl-xen-dom0 = ""

# domd does not need FIT image or U-Boot
MACHINE_EXTRA_RRECOMMENDS:remove:agl-xen-domd = "linux-fitimage u-boot"
