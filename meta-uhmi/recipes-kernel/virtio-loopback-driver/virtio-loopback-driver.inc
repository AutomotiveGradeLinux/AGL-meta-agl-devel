PV = "1.2.0+git${SRCPV}"
SRCREV = "44a31cd0bc2048f4ad1e4b036153e4fc81d1706c"
BRANCH ?= "main"
SRC_URI = " \
   git://github.com/unified-hmi/virtio-loopback-driver.git;protocol=https;branch=${BRANCH} \
"
