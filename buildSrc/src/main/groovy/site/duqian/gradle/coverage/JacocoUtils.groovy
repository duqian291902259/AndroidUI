package site.duqian.gradle.coverage;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.plugins.PluginContainer;

/**
 * Description:覆盖率插件相关的工具方法
 * @author  Created by 杜小菜 on 2021/9/27 - 11:26 .
 * E-mail: duqian2010@gmail.com
 */
class JacocoUtils {

    private static String TAG = "dq-jacoco-utils"
    //static String sCurrentBranchName = "" //当前分支名称

    static String getCurrentBranchName() {
        def currentBranchName = "git name-rev --name-only HEAD".execute().text.replaceAll("\n", "")
        if (currentBranchName.contains("/")) {
            currentBranchName = currentBranchName.substring(currentBranchName.lastIndexOf("/") + 1)
        }
        println "$TAG currentName:$currentBranchName"
        return currentBranchName
    }

    //$ git rev-parse HEAD
    //$ git rev-parse --short HEAD
    static String getCurrentCommitId() {
        def commitId = "git rev-parse --short HEAD".execute().text.replaceAll("\n", "")
        println "$TAG commitId:$commitId"
        //由于打包的名字里面的长度为ce1f7f00，8位
        if (commitId!=null&&commitId.length()>=8){
            commitId = commitId.substring(0,8)
        }
        return commitId
    }

    //获取所有module 的源码路径
    static ArrayList<String> getAllJavaDir(ProjectInternal project) {
        Set<Project> projects = project.rootProject.subprojects
        List<String> javaDir = new ArrayList<>(projects.size())
        projects.forEach {
            String srcPath = "$it.projectDir\\src\\main\\java"
            //println("$TAG java src= $srcPath")
            javaDir.add(srcPath)
        }
        println("$TAG java src size= ${javaDir.size()}")
        return javaDir
    }

    //获取所有module 的class路径
    static Object getAllClassDir(ProjectInternal project) {
        Set<Project> projects = project.rootProject.subprojects
        List<String> classDir = new ArrayList<>(projects.size())
        projects.forEach {
            String classesDir = "$it.buildDir\\intermediates\\javac\\debug\\classes"
            classDir.add(classesDir)
            def kotlin = hasKotlin(it.plugins)
            if (kotlin) {
                classDir.add("$it.buildDir\\tmp\\kotlin-classes\\debug")
            }
        }

        return classDir
    }

    static boolean hasKotlin(PluginContainer plugins) {
        plugins.findPlugin('kotlin-android')
    }

    static String getUploadRootDir(ProjectInternal project) {
        File parentFile = project.projectDir.getParentFile().getParentFile()
        String rootDir = parentFile.getAbsolutePath() + File.separator + "jacoco_upload"
        return rootDir
    }

    static def getBuildType(ProjectInternal project) {
        def taskNames = project.gradle.startParameter.taskNames
        for (tn in taskNames) {
            if (tn.startsWith("assemble")) {
                return tn.replaceAll("assemble", "").toLowerCase()
            }
        }
        return ""
    }

    static def sourceDirs(variant) {
        variant.sourceSets.java.srcDirs.collect { it.path }.flatten()
    }

    static def classesDirs(variant) {
        if (variant.hasProperty('javaCompileProvider')) {
            variant.javaCompileProvider.get().destinationDir
        } else {
            variant.javaCompile.destinationDir
        }
    }

    static def executionDataFile(ProjectInternal project, Task testTask, variant) {
        //testTask.jacoco.destinationFile.path
        def unitTestsData = "$project.buildDir/jacoco/${testTask}.exec"
        def androidTestsData = fileTree(dir: "${project.buildDir}/outputs/code_coverage/${variant.name.capitalize()}AndroidTest/connected/",
                includes: ["**/*.ec"])
        files([unitTestsData, androidTestsData])
    }
}
