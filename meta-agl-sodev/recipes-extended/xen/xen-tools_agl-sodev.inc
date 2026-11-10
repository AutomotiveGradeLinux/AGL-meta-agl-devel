# Disable default RDEPENDS addition, as it brings in a couple hundred
# megabytes of firmware we are unlikely to need.
QEMU_SYSTEM_RDEPENDS = ""

# Disable recommendation to reduce dom0/domd sizes.
# Any required qemu components can be pulled in explicitly.
RRECOMMENDS:${PN}:remove = "qemu"
