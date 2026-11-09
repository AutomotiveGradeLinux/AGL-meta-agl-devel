SUMMARY = "Unified HMI Distributed Display Framework"
SECTION = "graphics"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=a1426c5527163cae09cb37edc5ccf958"

SRCREV = "c22c14e0c57098ea71d46c070ea10f570d1c3061"
BRANCH ?= "main"
SRC_URI = " \
    git://github.com/unified-hmi/unified-hmi.git;protocol=https;branch=${BRANCH} \
"
PV = "3.0.0+git${SRCPV}"

export GO111MODULE = "auto"
export GOFLAGS = "-modcacherw"

GO_IMPORT = "unified-hmi"
GO_INSTALL = " \
    ${GO_IMPORT}/cmd/uhmi-master-node \
    ${GO_IMPORT}/cmd/uhmi-worker-node \
    ${GO_IMPORT}/cmd/uhmi-comm \
    ${GO_IMPORT}/cmd/uhmi-virtio-gpu-wl-send \
    ${GO_IMPORT}/cmd/uhmi-virtio-gpu-recv \
"

inherit go

DEPENDS += "bc-native"

inherit systemd features_check

SRC_URI += " \
    file://uhmi-master-node.service \
    file://uhmi-worker-node.service \
    file://virtual-screen-def.json \
    file://app-list-def.json \
"

REQUIRED_DISTRO_FEATURES = "systemd"
SYSTEMD_SERVICE:${PN} = "uhmi-master-node.service uhmi-worker-node.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"

do_compile[network] = "1"
do_compile:prepend() {
    export http_proxy=${http_proxy}
    export https_proxy=${https_proxy}
    cd ${S}
    oe_runmake mod
}

do_install() {
    if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
        install -d ${D}${systemd_system_unitdir}
        install -m 0644 ${UNPACKDIR}/*.service ${D}/${systemd_system_unitdir}
    fi

    install -d ${D}/etc/uhmi-framework
    install -m 0644 ${UNPACKDIR}/virtual-screen-def.json ${D}/etc/uhmi-framework
    install -m 0644 ${UNPACKDIR}/app-list-def.json ${D}/etc/uhmi-framework

    install -d ${D}${bindir}
    install -m 0755 ${B}/${GO_BUILD_BINDIR}/* ${D}${bindir}
}

FILES:${PN} += " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', '${systemd_system_unitdir}/${SYSTEMD_SERVICE}', '', d)} \
    /etc/uhmi-framework/virtual-screen-def.json \
    /etc/uhmi-framework/app-list-def.json \
"
