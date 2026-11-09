SUMMARY = "Unified AGL HMI Package Groups"
LICENSE = "Apache-2.0"

inherit packagegroup

RDEPENDS:${PN} += " \
    unified-hmi \
    ula-tools-agl \
    uhmi-agl-wm \
    agl-compositor \
"
