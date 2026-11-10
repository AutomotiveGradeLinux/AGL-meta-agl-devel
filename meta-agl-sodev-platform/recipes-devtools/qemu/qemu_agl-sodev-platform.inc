# NOTE:
# The patch 0001-virtio-input-hid-send-BTN_TOUCH-event-for-virtio-tab.patch
# has purposely not been ported from meta-rcar-demo and applied here, as it
# seems to break input with the ivi-homescreen/flutter-auto v3 Flutter
# embedder.  A different solution will be needed to support Android guests.

PACKAGECONFIG:append = " virtfs vhost ${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'gtk+', '', d)}"
PACKAGECONFIG:remove = "kvm"

# Prune supported targets down since we only need the host arch for the QEMU backend
QEMU_TARGETS:aarch64 = "aarch64"
