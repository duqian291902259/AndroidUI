package site.duqian.gradle.coverage;

import com.android.utils.FileUtils
import site.duqian.gradle.util.FileUtil
import okhttp3.*
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction

import java.util.concurrent.TimeUnit
/**
 * Description:上传build后的产物，这里上传classes
 * @author  Created by 杜小菜 on 2021/9/22 - 16:25 .
 * E-mail: duqian2010@gmail.com
 */
class JacocoUploadBuildFileTask extends DefaultTask {
    private static final String TAG = "dq-JacocoUploadClassTask"
    public static final String TYPE_FILE_EC = ".ec"
    public static final String TYPE_FILE_ZIP = ".zip"
    public static final String TYPE_FILE_APK = ".apk"
    public static final String TYPE_FILE_TXT = ".txt"
    private JacocoReportExtension extension

    void setExtension(JacocoReportExtension extension) {
        this.extension = extension
    }

    @TaskAction
    def uploadBuildFile() {
        try {
            long start = System.currentTimeMillis()
            println "$TAG uploadBuildFile start"
            //后续反编译apk获取class的话，可以使用uploadApk()
            //本地diff,是取分支的差异，后续服务器处理，拉取remote代码对比提交点
            uploadDiffFiles()
            long uploadDiffTime = System.currentTimeMillis() - start
            println "$TAG uploadDiffTime $uploadDiffTime"

            //全量copy class，本地不做class差异上报，后端根据commitId做diff
            copyClassesAndZip()
            long copyClassesAndZipTime = System.currentTimeMillis() - start
            println "$TAG copyClassesAndZipTime $copyClassesAndZipTime"

            uploadClassFiles()
            long uploadClassFilesTime = System.currentTimeMillis() - start
            println "$TAG uploadClassFilesTime $uploadClassFilesTime"

            uploadSourceFiles()
            long uploadSourceFilesTime = System.currentTimeMillis() - start
            println "$TAG uploadSourceFilesTime $uploadSourceFilesTime"
        } catch (Exception e) {
            println "$TAG uploadBuildFile error=$e"
        }
    }

    private def uploadSourceFiles() {
        try {
            //1,copy src,只处理自己包名下的class：com.netease.cc
            String rootDir = getSrcSavedDir()
            def packagePath = "com" + File.separator + "netease" + File.separator + "cc"
            String targetDir = rootDir + File.separator + packagePath
            File targetFile = new File(targetDir)
            FileUtil.deleteDirectory(targetDir)
            targetFile.mkdirs()
            println "$TAG copySourceFiles saved to =${targetFile.getAbsolutePath()}"
            Set<Project> projects = project.rootProject.subprojects
            int count = 0
            projects.forEach {
                def currentSrcDir = it.projectDir.getAbsolutePath() + File.separator + "src/main/java/com/netease/cc"
                //if (currentSrcDir.contains("component-") && !currentSrcDir.contains(File.separator + "api" + File.separator)) {
                if (!currentSrcDir.contains(File.separator + "api" + File.separator)) {
                    //println "$TAG copySourceFiles currentSrcDir=${currentSrcDir}"
                    File srcFile = new File(currentSrcDir)
                    if (srcFile.isDirectory() && srcFile.listFiles() != null) {
                        FileUtils.copyDirectory(srcFile, targetFile)
                        count++
                    }
                }
            }
            println "$TAG copy src count=${count}"

            //2,dir -->zip
            String srcDir = getSrcSavedDir()
            String zipFilePath = getSrcZipSavedDir()
            boolean hasZip = compressToZip(srcDir, zipFilePath)
            println "$TAG hasZip=$hasZip,zipFilePath=$zipFilePath"
            if (hasZip) {
                FileUtil.deleteDirectory(srcDir)
            }
            println "$TAG srcDir=$srcDir,zipFilePath=$zipFilePath"

            //3,upload src
            File file = new File(zipFilePath)
            if (file != null && file.exists() && file.isFile() && file.length() > 0) {
                println "$TAG uploadSourceFiles $file,size=${file.size()}"
                syncUploadFiles(file, TYPE_FILE_ZIP)
            } else {
                println "$TAG uploadSourceFiles failed,file not exists: ${file}"
            }
        } catch (Exception ignored) {
            println "$TAG uploadSourceFiles failed:$ignored"
        }
    }

    private def copyClassesAndZip() {
        //1,copy all cc-class to outer dir
        copyBuildClassDirs()
        //2,zip
        String classesDir = getClassSavedDir()
        String zipFilePath = getClassZipSavedDir()
        boolean hasZip = compressToZip(classesDir, zipFilePath)
        println "$TAG classesDir=$classesDir,zipFilePath=$zipFilePath"
        if (hasZip) {
            FileUtil.deleteDirectory(classesDir)
        }
    }

    private def uploadClassFiles() {
        //3,upload zip.上面的可能失败，没成功是没有zip文件的
        String classZipFilePath = getClassZipSavedDir()
        File file = new File(classZipFilePath)
        if (file != null && file.exists() && file.isFile() && file.length() > 0) {
            println "$TAG uploadClass zip $file,size=${file.size()}"
            syncUploadFiles(file, TYPE_FILE_ZIP)
        } else {
            println "$TAG uploadClassFiles failed,file not exists: ${file}"
        }
    }

    private String getUploadRootDir() {
        File parentFile = project.projectDir.getParentFile().getParentFile()
        String rootDir = parentFile.getAbsolutePath() + File.separator + "jacoco_upload"
        return rootDir
    }

    private String getClassSavedDir() {
        return getUploadRootDir() + File.separator + "classes"
    }

    private String getClassZipSavedDir() {
        String rootDir = getUploadRootDir()
        return "${rootDir}/classes.zip"
    }

    private String getSrcSavedDir() {
        return getUploadRootDir() + File.separator + "src"
    }

    private String getSrcZipSavedDir() {
        String rootDir = getUploadRootDir()
        return "${rootDir}/src.zip"
    }

    public static String CC_PACKAGE_FILE_NAME = "com" + File.separator + "netease" + File.separator + "cc"

    /**
     * copy指定目录下面的class
     * @return 保存文件的根目录
     */
    private String copyBuildClassDirs() {
        //过滤不需要的文件
        try {
            String rootDir = getClassSavedDir()
            //只处理自己包名下的class：com.netease.cc
            String targetDir = rootDir + File.separator + CC_PACKAGE_FILE_NAME
            File targetFile = new File(targetDir)
            FileUtil.deleteDirectory(targetDir)
            targetFile.mkdirs()
            println "$TAG copyBuildClassDirs to =${targetFile.getAbsolutePath()}"
            Set<Project> projects = project.rootProject.subprojects
            int count = 0
            projects.forEach {
                //D:\NetEase\cc-projects\CC-Android-D\base\build 除外
                def currentBuildDir = it.buildDir.getAbsolutePath()
                //if (currentBuildDir.contains("component-") && !currentBuildDir.contains(File.separator + "api" + File.separator + "build")) {
                if (!currentBuildDir.contains(File.separator + "api" + File.separator + "build")) {

                    String classesDir = "$currentBuildDir\\intermediates\\javac\\debug\\classes\\$CC_PACKAGE_FILE_NAME"
                    def kotlin = JacocoUtils.hasKotlin(it.plugins)
                    //println "$TAG copyBuildClassDirs classesDir:$classesDir,kotlin=$kotlin"
                    if (kotlin) {
                        classesDir = "$currentBuildDir\\tmp\\kotlin-classes\\debug\\$CC_PACKAGE_FILE_NAME"
                    }

                    // 过滤不需要统计的class文件
                    def project = it
                    def finalClassDir = project.files(project.files(classesDir).files.collect {
                        project.fileTree(dir: it,
                                excludes: JacocoReportExtension.defaultExcludes)
                    })
                    for (String path : finalClassDir) {
                        //println "$TAG $CC_PACKAGE_FILE_NAME -->path=$path"
                        int index = path.indexOf(CC_PACKAGE_FILE_NAME)
                        if (index >= 0) {
                            String suffix = path.substring(index + CC_PACKAGE_FILE_NAME.length())
                            boolean copied = FileUtil.copyFile(new File(path), new File(targetDir + suffix))
                            if (copied) {
                                count++
                            }
                            //println "$TAG copy to -->path=${targetDir + suffix},copied=$copied"
                        }
                    }
                    /*File classFile = new File(classesDir)
                    if (classFile.isDirectory() && classFile.listFiles() != null) {
                        FileUtils.copyDirectory(classFile, targetFile)
                    }*/
                }
            }
            println "$TAG copy class count=${count}"

            return rootDir
        } catch (Exception ignored) {
            println "$TAG copyBuildClassDirs failed:$ignored"
        }
        return ""
    }

    private boolean compressToZip(String classesDir, String zipFilePath) {
        try {
            FileUtil.deleteFile(zipFilePath)
            FileUtil.zipFolder(classesDir, zipFilePath)
            return true
        } catch (Exception e) {
            e.printStackTrace()
            println "$TAG compressToZip failed:$e"
        }
        return false
    }

    private def uploadDiffFiles() {
        def diffFilePath = "${project.buildDir}/outputs/diff/diffFiles.txt"
        File diffFile = new File(diffFilePath)
        if (diffFile != null && diffFile.exists() && diffFile.isFile() && diffFile.length() > 0) {
            println "$TAG uploadDiffFiles ${diffFile.getAbsolutePath()}"
            syncUploadFiles(diffFile, TYPE_FILE_TXT)
        } else {
            println "$TAG uploadDiffFiles failed:diffFile not exists}"
        }
    }

    private def uploadApk() {
        def apkDir = "${project.buildDir}/outputs/apk/debug/"
        File rootFile = new File(apkDir)
        File apkFile = null
        if (rootFile.exists() && rootFile.listFiles() != null) {
            for (File file : rootFile.listFiles()) {
                if (file.getName().endsWith(".apk")) {
                    apkFile = new File(apkDir + file.getName())
                    break
                }
            }
        }
        if (apkFile != null && apkFile.exists() && apkFile.isFile() && apkFile.length() > 0) {
            println "$TAG uploadApk ${apkFile.getAbsolutePath()}"
            syncUploadFiles(apkFile, TYPE_FILE_APK)
        } else {
            println "$TAG uploadApk failed:apk not exists}"
        }
    }

    /**
     * 文件上传，如果是本地server，要确保测试设备与server在同一个局域网
     * */
    private void syncUploadFiles(File file, String type) {
        String currentBranchName = JacocoUtils.getCurrentBranchName()
        String currentCommitId = JacocoUtils.getCurrentCommitId()
        println("$TAG currentBranchName:$currentBranchName,currentCommitId=$currentCommitId")
        OkHttpClient client = buildHttpClient()
        RequestBody fileBody = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        String fileName = file.getName()
        // 处理上传的参数：用户名，uid，版本信息,分支信息等
        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", fileName, fileBody)
                .addFormDataPart("appName", extension.appName)
                .addFormDataPart("branch", currentBranchName)
                .addFormDataPart("commitId", currentCommitId)
                .addFormDataPart("type", "$type")
                .build()
        String url = "${extension.jacocoHost}/coverage/upload"
        println("$TAG syncUploadFiles,appName=${extension.appName},file=${file.absolutePath},commitId=$currentCommitId,size=${file.length()},currentBranchName=$currentBranchName")
        println("$TAG upload url =$url")
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build()
        Call call = client.newCall(request)
        Response response = call.execute()
        handleResponse(response, file)
    }

    private void handleResponse(Response response, File file) {
        ResponseBody responseBody = null
        try {
            responseBody = response.body()
            def str = responseBody.string()
            println("$TAG syncUploadFiles str =$str")
        } catch (Exception e) {
            println("$TAG syncUploadFiles error =$e")
        } finally {
            responseBody.close()
        }
    }

    private OkHttpClient buildHttpClient() {
        return new OkHttpClient.Builder()
                .callTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(100L, TimeUnit.SECONDS)
                .build()
    }
}