AGL_FEATURES:append = " agl-sodev-platform"

# Use the Xen-Troops forks of Xen and QEMU
PREFERRED_VERSION_xen = "4.19+xt-0.2"
PREFERRED_VERSION_xen-tools = "4.19+xt-0.2"
QEMUVERSION:agl-xen-domd = "7.0.0-xt"

include agl-sodev-platform-${MACHINE}.inc
