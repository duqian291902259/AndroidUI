package site.duqian.android_ui.utils

import android.content.Context
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

/**
 * Description:Jacoco工具类
 *
 * @author  Created by 杜小菜 on 2021/9/8 - 11:55 . E-mail: duqian2010@gmail.com
 */
object CCJacocoHelper {
    private const val TAG = "JacocoHelper"

    //ec文件的路径
    private val DEFAULT_COVERAGE_ROOT_DIR =
        Environment.getExternalStorageDirectory().absolutePath + "/Android/data/site.duqian.test/cache/connected/"
    private var mRootDir = DEFAULT_COVERAGE_ROOT_DIR
    private const val localHost = "http://127.0.0.1:8090"
    private const val URL_HOST = localHost

    /**
     * 生成ec文件
     *
     * @param isNew 是否重新创建ec文件
     */
    fun generateEcFile(context: Context?, isNew: Boolean) {
        Thread().run {
            var out: OutputStream? = null
            //todo-dq 按照时间戳命名?
            val fileName = "dq_jacoco_${System.currentTimeMillis()}.ec"
            //val fileName = "coverage.ec"
            val rootDir = getJacocoEcFileSaveDir(context)
            val path = rootDir + fileName
            Log.d(TAG, "generateEcFile path is $path")
            val mCoverageFile = File(path)
            try {
                File(rootDir).mkdirs()
                if (isNew && mCoverageFile.exists()) {
                    Log.d(TAG, "delete old ec file")
                    mCoverageFile.delete()
                }
                if (!mCoverageFile.exists()) {
                    mCoverageFile.createNewFile()
                }
                out = FileOutputStream(mCoverageFile.path, true)
                val agent = Class.forName("org.jacoco.agent.rt.RT")
                    .getMethod("getAgent")
                    .invoke(null)
                if (agent != null) {
                    out.write(
                        agent.javaClass.getMethod(
                            "getExecutionData",
                            Boolean::class.javaPrimitiveType
                        )
                            .invoke(agent, false) as ByteArray
                    )
                } else {
                    Log.e(TAG, "generateEcFile agent is null")
                }
            } catch (e: Exception) {
                Log.e(TAG, "generateEcFile error=$e")
            } finally {
                try {
                    out?.close()
                } catch (e: Exception) {
                }
            }
        }
    }


    private fun getJacocoEcFileSaveDir(context: Context?): String {
        val root = context?.externalCacheDir?.absolutePath?.toString()
        if (!TextUtils.isEmpty(root)) {
            mRootDir = "$root/connected/"
        }
        return mRootDir
    }

}