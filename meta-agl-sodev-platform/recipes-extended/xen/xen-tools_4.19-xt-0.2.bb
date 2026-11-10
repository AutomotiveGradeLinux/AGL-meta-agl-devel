require xen-source-4.19-xt-0.2.inc

DEFAULT_PREFERENCE ??= "-1"

require recipes-extended/xen/xen.inc
require recipes-extended/xen/xen-tools.inc

# Override defaults
SUMMARY = "Xen hypervisor tools - Xen Troops fork"
DESCRIPTION = "Xen Troops fork of tools and utility software for the Xen hypervisor"
LIC_FILES_CHKSUM = "file://COPYING;md5=d1a1e216f80b6d8da95fec897d0dbec9"
