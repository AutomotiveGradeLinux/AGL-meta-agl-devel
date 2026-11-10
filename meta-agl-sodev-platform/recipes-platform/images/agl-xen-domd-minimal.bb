require recipes-graphics/images/core-image-weston.bb

IMAGE_INSTALL += " \
    xen-tools-devd \
    xen-tools-scripts-network \
    xen-tools-scripts-block \
    xen-tools-xencommons \
    xen-tools-net-conf \
    qemu-system-aarch64 \
    dnsmasq \
    nftables \
    kernel-module-xt-masquerade \
    kernel-module-xt-nat \
    kernel-module-xt-tcpudp \
    virglrenderer \
    compositor-notification \
    bridge-notification \
"

PACKAGE_EXCLUDE = "kernel-image-*"
