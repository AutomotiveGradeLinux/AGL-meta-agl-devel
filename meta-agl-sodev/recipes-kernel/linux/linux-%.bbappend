require ${@bb.utils.contains('AGL_FEATURES', 'agl-sodev', 'linux_agl-sodev.inc', '', d) if bb.data.inherits_class('kernel', d) else ''}
