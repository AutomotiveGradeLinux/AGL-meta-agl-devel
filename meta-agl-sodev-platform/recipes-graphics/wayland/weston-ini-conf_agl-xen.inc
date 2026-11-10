FILESEXTRAPATHS:prepend := "${THISDIR}/weston-ini-conf:"

#SRC_URI += "file://remote-output.cfg.in"

WESTON_FRAGMENTS_BASE = "core"

# Reset default to one non-rotated display.
# Platform/Demo specific bbappends need to change as appropriate
WESTON_DISPLAYS ?= "hdmi-a-1-0"
