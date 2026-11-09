PV = "2.1.0+git${SRCPV}"
SRCREV = "9dc4dc93634c6340bf500c2141d8273ae40a99a8"
BRANCH ?= "main"
SRC_URI = " \
    git://github.com/unified-hmi/remote-virtio-gpu.git;protocol=https;branch=${BRANCH} \
"
