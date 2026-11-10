# AGL SoDeV Platform support layer

This OpenEmbedded layer adds the feature 'agl-sodev-platform'

'agl-sodev-platform' is used to enable SoDeV image building support via:
- Non-upstream xen, xen-tools, and qemu recipes required to enable
  hardware support for Xen.  This includes Xen-Troops derived changes
  for VirtIO support.
- Systemd units and configuration for domd and domu synchronization
  derived from Xen-Troops scheme in meta-xt-common.
- Platform specific kernel and image changes, handled with dynamic
  layers.

## Setup

Enable the `agl-sodev-platform` AGL feature when setting up your build
environment with aglsetup.sh.  Usually that will not be done directly,
as the feature will be a dependency of another, e.g. agl-sodev-demo.
