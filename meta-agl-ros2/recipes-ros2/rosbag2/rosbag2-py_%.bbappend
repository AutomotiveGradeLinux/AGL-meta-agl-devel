# meta-ros cross-builds the rosbag2_py pybind11 extensions with the build host
# Python SOABI (BUILD_ARCH) rather than the target one, so they are installed
# as *.cpython-<ver>-${BUILD_ARCH}-linux-gnu.so and the target Python cannot
# import them. "ros2 bag play" then fails with
#   No module named 'rosbag2_py._reader'
# and any backend that replays a bag on the target is broken. Rename the
# mis-tagged extensions to the target architecture SOABI. This is a no-op on
# native builds where BUILD_ARCH == TARGET_ARCH.
do_install:append() {
    sitedir="${D}${PYTHON_SITEPACKAGES_DIR}/rosbag2_py"
    if [ -d "$sitedir" ]; then
        for so in "$sitedir"/*-${BUILD_ARCH}-linux-gnu.so; do
            [ -e "$so" ] || continue
            newso=$(echo "$so" | sed 's/-${BUILD_ARCH}-linux-gnu\.so$/-${TARGET_ARCH}-linux-gnu.so/')
            [ "$so" = "$newso" ] || mv "$so" "$newso"
        done
    fi
}
