package site.duqian.gradle.coverage;

import com.android.utils.FileUtils
import site.duqian.gradle.util.FileUtil
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * Description:下载ec文件到指定的目录，后续做git diff和生成增量覆盖率报告等逻辑
 * @author Created by 杜小菜 on 2021/9/10 - 18:39 .
 * E-mail: duqian2010@gmail.com
 */
class JacocoDownloadTask extends DefaultTask {

    public JacocoReportExtension extension

    void setExtension(JacocoReportExtension extension) {
        this.extension = extension
    }

    @TaskAction
    def downloadJacocoEcFile() {
        try {
            def dataDir = "${project.buildDir}/outputs/code_coverage/connected/"
            File rootFile = new File(dataDir)
            //先清除缓存
            rootFile.deleteDir()
            FileUtil.deleteDirectory(dataDir)
            rootFile.mkdirs()

            //本地拉取
            pullLocalEcFiles(dataDir)

            //远程下载
            println "downloadJacocoEcFile start local dq"
            downloadEcData(dataDir)
            println "downloadJacocoEcFile end"

            //copy到外部目录备用，"ec-" + commitId
            File targetDir = new File(JacocoUtils.getUploadRootDir(project) + File.separator + "ec")
            targetDir.deleteDir()
            FileUtils.copyDirectory(rootFile, targetDir)
        } catch (Exception e) {
            println "downloadJacocoEcFile error=$e"
        }
    }

    private def pullLocalEcFiles(String rootDir) {
        def dataDir = new File(rootDir).getParentFile().getAbsolutePath()
        def ecDir = "/sdcard/Android/data/${extension.packageName}/cache/connected/"
        def pullEcFile = "adb pull $ecDir $dataDir".execute().text
        println "pullEcFile=${pullEcFile}"
    }

    //下载ec数据文件
    private def downloadEcData(String dataDir) {
        def host = extension.jacocoHost
        def appName = extension.appName
        def branch = JacocoUtils.getCurrentBranchName()
        branch = URLEncoder.encode(branch, "utf-8")

        String commitId = JacocoUtils.getCurrentCommitId()
        def downloadUrl = "${host}/coverage/queryFile?appName=${appName}&branch=${branch}&commitId=${commitId}&type=${JacocoUploadBuildFileTask.TYPE_FILE_EC}"
        def curl = "curl $downloadUrl"
        println "curl = ${curl}"
        def text = curl.execute().text
        println "queryFile = ${text}"
        println "paths=${text}"
        if ("" == text) {
            return
        }
        //todo-dq 优化后台返回json列表
        /*JsonObject jsonObject = new JsonObject(text)
        String files = jsonObject.get("files")
        List<String> filePathList = new Gson().fromJson(text,List<String>)*/
        text = text.substring(text.indexOf("[") + 1, text.lastIndexOf("]")).replace("]", "")
        if ("" == text) {
            return
        }
        String[] paths = text.split(',')
        println "download ec file, length=${paths.length}"
        if (paths != null && paths.size() > 0) {
            for (String path : paths) {
                path = path.replace("\"", '')
                def name = getOneParameter(path, "fileName")
                def file = new File(dataDir, name)
                if (file.exists() && file.length() > 0) { //存在
                    file.delete()
                }
                path = URLEncoder.encode(path, "utf-8")
                println "execute curl -o ${file.getAbsolutePath()} ${host}/download?path=${path}"
                "curl -o ${file.getAbsolutePath()} ${host}/download?path=${path}".execute().text
            }
        }
        println "downloadData 下载完成"
    }

    private static String getOneParameter(String url, String keyWord) {
        String retValue = "";
        try {
            final String charset = "utf-8"
            url = URLDecoder.decode(url, charset)
            String[] keyValues = url.split("&")
            for (int i = 0; i < keyValues.length; i++) {
                int index = keyValues[i].indexOf("=")
                if (index <= 0) {
                    continue
                }
                String key = keyValues[i].substring(0, index)
                if (key.equals(keyWord)) {
                    retValue = keyValues[i].substring(index + 1)
                }
            }
        } catch (Exception e) {

        }
        return retValue
    }

    private boolean deleteEmptyDir(File dir) {
        if (dir.isDirectory()) {
            boolean flag = true
            for (File f : dir.listFiles()) {
                if (deleteEmptyDir(f))
                    f.delete()
                else
                    flag = false
            }
            return flag
        }
        return false
    }
}