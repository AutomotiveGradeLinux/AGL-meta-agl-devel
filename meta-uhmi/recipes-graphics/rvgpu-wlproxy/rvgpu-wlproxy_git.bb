DESCRIPTION = "Panasonic UHMI RVGPU Wayland Proxy"
DEPENDS = " virtual/libgbm libinput libxkbcommon"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=a8a0cf4075753e7d7e8c948171357af0"

SRCREV = "8e3b7939ec7c1aefa0f6a95e28737315f6dc2af6"
BRANCH ?= "main"

SRC_URI = " \
        git://github.com/unified-hmi/rvgpu-wlproxy.git;protocol=https;branch=${BRANCH} \
"
PV = "1.1.0+git${SRCPV}"

inherit pkgconfig cmake

CFLAGS += "-Wno-error=incompatible-pointer-types"

FILES:${PN} += "/usr/bin/*"
