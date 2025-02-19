package com.jakting.shareclean.utils

import android.content.Intent
import android.content.pm.PackageManager
import com.jakting.shareclean.utils.MyApplication.Companion.appContext
import com.topjohnwu.superuser.Shell
import java.io.File

fun moduleApplyAvailable(): Boolean {
    //检查模块是否已经生效，无论是 Riru 版还是 Zygisk 版
    val selfIfwFile = File(ifw_app_self_path)
    if (!selfIfwFile.exists()) {
        if (runShell("touch $ifw_app_self_path").isSuccess)
            runShell("echo '$ifw_app_self' > $ifw_app_self_path")
    }
    val resolveInfoList = appContext.packageManager!!.queryIntentActivities(
        Intent(Intent.ACTION_PROCESS_TEXT).setType("text/tigerbeanst"),
        PackageManager.MATCH_ALL
    )
    for (resolveInfo in resolveInfoList) {
        if (resolveInfo.activityInfo.packageName == appContext.packageName) {
            return false
        }
    }
    return true
}

fun moduleInfo(): Array<String> {
    // 检查 Riru 版
    val riruShell = runShell("cat /data/adb/modules/riru_ifw_enhance_tiw/module.prop")
    if(riruShell.isSuccess){
        return arrayOf("Riru", moduleVersion(riruShell)[0]!!, moduleVersion(riruShell)[1]!!)
    }
    val zygiskShell = runShell("cat /data/adb/modules/zygisk_ifw_enhance_tiw/module.prop")
    if(zygiskShell.isSuccess){
        return arrayOf("Zygisk", moduleVersion(zygiskShell)[0]!!, moduleVersion(zygiskShell)[1]!!)
    }
    return arrayOf("", "", "")
}

fun moduleVersion(sr: Shell.Result): Array<String?> {
    val shellResult = sr.getPureCat()
    logd(shellResult)
    if (shellResult.isEmpty()) return arrayOf("", "")
    val version = Regex(".*?version=(.*?),.*?versionCode=(.*?),").find(shellResult)?.groupValues
    return arrayOf(version?.get(1), version?.get(2))
}

