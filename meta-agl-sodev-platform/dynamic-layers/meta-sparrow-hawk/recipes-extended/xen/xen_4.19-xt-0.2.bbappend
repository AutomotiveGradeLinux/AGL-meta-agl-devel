FILESEXTRAPATHS:prepend := "${THISDIR}/xen:"

SRC_URI += " \
    file://0001-iommu-ipmmu-vmsa-Add-Renesas-R8A779G0-R-Car-V4H-supp.patch \
    file://0001-iommu-ipmmu-vmsa-Skip-preinit-for-R8A779G0-as-well.patch \
    file://config.cfg \
"
