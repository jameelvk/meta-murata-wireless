SUMMARY = "Cypress Backport tool"
LICENSE = "GPLv2.0"
LIC_FILES_CHKSUM = "file://${WORKDIR}/LICENSE;md5=b234ee4d69f5fce4486a80fdaf4a4263"


inherit systemd
inherit native

SYSTEMD_AUTO_ENABLE = "disable"

SRC_URI =  "https://github.com/murata-wireless/cyw-fmac/raw/imx-krogoth-battra/imx-krogoth-battra_r${PV}.tar.gz;name=archive1"
SRC_URI += "https://github.com/murata-wireless/meta-murata-wireless/raw/imx-krogoth-battra/LICENSE;name=archive99"

SRC_URI[archive1.md5sum] = "781dfa0e2a969f0685376235bcc7100e"
SRC_URI[archive1.sha256sum] = "80c6ca2ab3d57ecc8f46a4eff1ee67f4a9d8faf687ee5e5b8672f130f3b79f18"

#LICENSE
SRC_URI[archive99.md5sum] = "b234ee4d69f5fce4486a80fdaf4a4263"
SRC_URI[archive99.sha256sum] = "8177f97513213526df2cf6184d8ff986c675afb514d4e68a404010521b880643"

S = "${WORKDIR}/backporttool-native-${PV}"
B = "${WORKDIR}/backporttool-native-${PV}/"

DEPENDS = "linux-imx"

do_configure () {
	echo "Configuring"
        echo "KLIB:       ${KLIB}"
        echo "KLIB_BUILD: ${KLIB_BUILD} "
}

export EXTRA_CFLAGS = "${CFLAGS}"
export BINDIR = "${sbindir}"

do_compile () {
	echo "Compiling: "
        #echo "Testing Make Display:: ${MAKE}"
        pwd

        echo "KLIB:       ${KLIB}"
        echo "KLIB_BUILD: ${KLIB_BUILD} "
        echo "KBUILD_OUTPUT: ${KBUILD_OUTPUT}"

	rm -rf .git
	cp ${STAGING_KERNEL_BUILDDIR}/.config ${STAGING_KERNEL_DIR}/.config
	cp ${STAGING_KERNEL_BUILDDIR}/kernel-abiversion ${STAGING_KERNEL_DIR}/kernel-abiversion

        cp -a ${TMPDIR}/work/x86_64-linux/backporttool-native/1.0-r0/imx-krogoth-battra_r1.0/. .

        oe_runmake KLIB="${STAGING_KERNEL_DIR}" KLIB_BUILD="${STAGING_KERNEL_DIR}" defconfig-brcmfmac
}

do_install () {
	echo "Installing: "

        install -d ${D}${sbindir}
}

FILES_${PN} += "${sbindir}"
PACKAGES += "FILES-${PN}"


