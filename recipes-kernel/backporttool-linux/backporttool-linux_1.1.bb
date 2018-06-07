SUMMARY = "Cypress FMAC backport"
DESCRIPTION = "Cypress FMAC backport"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${WORKDIR}/LICENSE;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI =  "https://github.com/murata-wireless/cyw-fmac/raw/imx8-morty-battra/imx8-morty-battra_r${PV}.tar.gz;name=archive1"
SRC_URI += "https://github.com/murata-wireless/meta-murata-wireless/raw/imx8-morty-battra/LICENSE;name=archive99"

SRC_URI[archive1.md5sum] = "5eb044d8628d58ae532cdaa33aa83511"
SRC_URI[archive1.sha256sum] = "83616dbc051bb0aeab6896718a43002c814cf69e0483b4212c77a5f1e67d644d"

#LICENSE
SRC_URI[archive99.md5sum] = "b234ee4d69f5fce4486a80fdaf4a4263"
SRC_URI[archive99.sha256sum] = "8177f97513213526df2cf6184d8ff986c675afb514d4e68a404010521b880643"

inherit linux-kernel-base kernel-arch

DEPENDS = " linux-imx"
DEPENDS += " backporttool-native"

inherit module-base
addtask make_scripts after do_patch before do_configure
do_make_scripts[lockfiles] = "${TMPDIR}/kernel-scripts.lock"
do_make_scripts[deptask] = "do_populate_sysroot"


S = "${WORKDIR}/backporttool-linux-${PV}"
B = "${WORKDIR}/backporttool-linux-${PV}/"

#You should set variable CROSS_COMPILE, not a CROSS-COMPILE
export CROSS_COMPILE = "${TARGET_PREFIX}"

KERNEL_VERSION = "${@base_read_file('${STAGING_KERNEL_BUILDDIR}/kernel-abiversion')}"

do_compile() {
	# Linux kernel build system is expected to do the right thing
	# unset CFLAGS
        echo "TEST_CROSS_COMPILE:: ${CROSS_COMPILE}"
        echo "TEST_CROSSCOMPILE:: ${CROSSCOMPILE}"          
        echo "TEST_TARGET_PREFIX:: ${TARGET_PREFIX}"      
        echo "TEST_ARCH:: ${ARCH}"
        echo "TEST_TARGET_ARCH:: ${TARGET_ARCH}"
        echo "STAGING_KERNEL_BUILDDIR: ${STAGING_KERNEL_BUILDDIR}"
        echo "TEST_LDFLAGS:: ${LDFLAGS}"
        echo "S DIR:  {S}"
        unset LDFLAGS
        
	cp ${STAGING_KERNEL_BUILDDIR}/.config ${STAGING_KERNEL_DIR}/.config
	cp ${STAGING_KERNEL_BUILDDIR}/kernel-abiversion ${STAGING_KERNEL_DIR}/kernel-abiversion

	rm -rf .git
        cp -a ${TMPDIR}/work/x86_64-linux/backporttool-native/${PV}-r0/backporttool-native-${PV}/. .

        oe_runmake KLIB="${STAGING_KERNEL_DIR}" KLIB_BUILD="${STAGING_KERNEL_BUILDDIR}" modules
}

do_install() {
	install -d ${D}/lib/modules/${KERNEL_VERSION}/kernel/drivers/net/wireless/broadcom/brcm80211/brcmfmac
	install -d ${D}/lib/modules/${KERNEL_VERSION}/kernel/drivers/net/wireless/broadcom/brcm80211/brcmutil
	install -d ${D}/lib/modules/${KERNEL_VERSION}/kernel/compat
	install -d ${D}/lib/modules/${KERNEL_VERSION}/kernel/net/wireless

	install -m 644 ${S}/drivers/net/wireless/broadcom/brcm80211/brcmfmac/brcmfmac.ko ${D}/lib/modules/${KERNEL_VERSION}/kernel/drivers/net/wireless/broadcom/brcm80211/brcmfmac/brcmfmac.ko
	install -m 644 ${S}/drivers/net/wireless/broadcom/brcm80211/brcmutil/brcmutil.ko ${D}/lib/modules/${KERNEL_VERSION}/kernel/drivers/net/wireless/broadcom/brcm80211/brcmutil/brcmutil.ko
	install -m 644 ${S}/compat/compat.ko ${D}/lib/modules/${KERNEL_VERSION}/kernel/compat/compat.ko
	install -m 644 ${S}/net/wireless/cfg80211.ko ${D}/lib/modules/${KERNEL_VERSION}/kernel/net/wireless/cfg80211.ko
}


PACKAGE_ARCH = "${MACHINE_ARCH}"

FILES_${PN} += " \
	/lib/modules/${KERNEL_VERSION}/kernel/drivers/net/wireless/broadcom/brcm80211/brcmfmac/brcmfmac.ko \	
	/lib/modules/${KERNEL_VERSION}/kernel/drivers/net/wireless/broadcom/brcm80211/brcmutil/brcmutil.ko \
	/lib/modules/${KERNEL_VERSION}/kernel/compat/compat.ko \
	/lib/modules/${KERNEL_VERSION}/kernel/net/wireless/cfg80211.ko \
"

PACKAGES += "FILES-${PN}"



