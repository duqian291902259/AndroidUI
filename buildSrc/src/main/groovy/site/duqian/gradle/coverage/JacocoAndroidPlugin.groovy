package site.duqian.gradle.coverage;

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.tasks.TaskContainer
import org.gradle.testing.jacoco.plugins.JacocoPlugin
import org.gradle.testing.jacoco.tasks.JacocoReport

/**
 * Description:Jacoco for cc,覆盖率插件
 * @author  Created by 杜小菜 on 2021/9/10 - 10:04 .
 * E-mail: duqian2010@gmail.com
 */
class JacocoAndroidPlugin implements Plugin<ProjectInternal> {

    private static String TAG = "dq-jacoco-JacocoAndroidPlugin"
    private static String GROUP = "cc-jacoco"
    private static String TASK_JACOCO_ALL = "jacocoAllTaskLauncher"
    private static String TASK_JACOCO_DOWNLOAD_EC = "jacocoDownloadEcData"
    private static String TASK_JACOCO_UPLOAD_BUILD_FILES = "jacocoUploadBuildFiles"
    static String TASK_JACOCO_BRANCH_DIFF_CLASS = "jacocoBranchDiffClass"
    private static String TASK_JACOCO_CC_REPORT = "jacocoCCReport"
    @Override
    void apply(ProjectInternal project) {
        JacocoReportExtension jacocoReportExtension = project.extensions.create("jacocoReportConfig", JacocoReportExtension)
        boolean isJacocoEnable = project.rootProject.ext.coverageEnabled //jacocoReportExtension.isJacocoEnable
        jacocoReportExtension.isJacocoEnable = isJacocoEnable
        def jacocoHost = jacocoReportExtension.jacocoHost
        println("$TAG isJacocoEnable =" + isJacocoEnable+",jacocoHost=${jacocoHost}")
        //配置BuildConfig
        updateBuildConfigField(project,jacocoHost)

        if (!isJacocoEnable || "cc-start" != project.getName()) {
            println("$TAG isJacocoEnable = false")
            return
        }

        project.plugins.apply(JacocoPlugin)

        /*def android = project.extensions.android
        if (android instanceof AppExtension) {
            JacocoTransform jacocoTransform = new JacocoTransform(project, jacocoReportExtension)
            android.registerTransform(jacocoTransform)
            println("$TAG,registerJacocoTransform $android")
        }*/

        project.afterEvaluate {
            //android.applicationVariants.all { variant ->
            //目前只在cc-start里面处理，不用各个moudle里面搞task
            Task jacocoReportEntryTask = findOrCreateJacocoReportTask(project.tasks)

            JacocoDownloadTask jacocoDownloadTask = project.tasks.findByName(TASK_JACOCO_DOWNLOAD_EC)
            if (jacocoDownloadTask == null) {//download .ec
                jacocoDownloadTask = project.tasks.create(TASK_JACOCO_DOWNLOAD_EC, JacocoDownloadTask)
                jacocoDownloadTask.setExtension(jacocoReportExtension)
                jacocoDownloadTask.setGroup(GROUP)
                println("$TAG,$TASK_JACOCO_DOWNLOAD_EC")
            }

            BranchDiffClassTask branchDiffClassTask = project.tasks.findByName(TASK_JACOCO_BRANCH_DIFF_CLASS)
            if (branchDiffClassTask == null) {//pull copy diff class
                branchDiffClassTask = project.tasks.create(TASK_JACOCO_BRANCH_DIFF_CLASS, BranchDiffClassTask)
                branchDiffClassTask.setGroup(GROUP)
                println("$TAG,$TASK_JACOCO_BRANCH_DIFF_CLASS")
                branchDiffClassTask.jacocoExtension = project.jacocoReportConfig
            }

            JacocoUploadBuildFileTask jacocoUploadTask = project.tasks.findByName(TASK_JACOCO_UPLOAD_BUILD_FILES)
            if (jacocoUploadTask == null) {//upload build classes
                jacocoUploadTask = project.tasks.create(TASK_JACOCO_UPLOAD_BUILD_FILES, JacocoUploadBuildFileTask)
                jacocoUploadTask.setExtension(jacocoReportExtension)
                jacocoUploadTask.setGroup(GROUP)
                println("$TAG,$TASK_JACOCO_UPLOAD_BUILD_FILES")
                jacocoUploadTask.dependsOn(branchDiffClassTask)
            }

            JacocoReport jacocoReportTask = project.tasks.findByName(TASK_JACOCO_CC_REPORT)
            if (jacocoReportTask == null) {//cc coverage report
                jacocoReportTask = createReportTask(project, null)//variant
                println("$TAG,$TASK_JACOCO_CC_REPORT " + jacocoReportTask)
                //先执行jacocoDownloadTask
                jacocoReportTask.dependsOn(jacocoDownloadTask)
            }

            //自由组合、控制一些逻辑的执行
            jacocoReportEntryTask.doFirst {
                jacocoDownloadTask.downloadJacocoEcFile()
                if (jacocoReportExtension.isDiffJacoco) {
                    branchDiffClassTask.getDiffClass()
                }
                jacocoUploadTask.uploadBuildFile()
                println("$TAG jacocoReportEntryTask " + it)
            }

            jacocoReportEntryTask.doLast {
                jacocoReportTask.generate()
            }

            //压缩并上传class/apk文件
            def uploadTask = project.tasks.findByName(TASK_JACOCO_UPLOAD_BUILD_FILES)
            Task buildTask = project.tasks.findByName("assembleDebug")
            buildTask.doFirst {
                updateBuildConfigField(project,jacocoHost)
            }
            jacocoDownloadTask.doFirst {
                updateBuildConfigField(project,jacocoHost)
            }
            println("$TAG uploadTask $uploadTask,buildTask=$buildTask")
            if (buildTask != null && uploadTask != null) {
                buildTask.finalizedBy(uploadTask)
            }
        }
    }

    /**
     * 初始化构建的module到BuildConfig
     */
    private void updateBuildConfigField(Project project,String jacocoHost) {
        boolean isJacocoEnable = project.rootProject.ext.coverageEnabled
        def currentBranchName = JacocoUtils.getCurrentBranchName()
        String commitId = JacocoUtils.getCurrentCommitId()
        Set<Project> set = project.getRootProject().getAllprojects()
        println("$TAG currentBranchName ${currentBranchName},commitId ${commitId},isJacocoEnable ${isJacocoEnable},")
        for (Project s : set) {
            try {
                def moduleName = s.getName()
                if (moduleName == "api") continue
                //println("$TAG moduleName= ${s.getName()}")
                if ("basiclib-common".equalsIgnoreCase(moduleName) || "cc-start".equalsIgnoreCase(moduleName)) {
                    println("$TAG updateBuildConfigField ${s.getName()}")
                    def defaultConfig = s.android.defaultConfig
                    defaultConfig.buildConfigField "String", "CURRENT_BRANCH_NAME", "\"" + currentBranchName + "\""
                    defaultConfig.buildConfigField "String", "CURRENT_COMMIT_ID", "\"" + commitId + "\""
                    defaultConfig.buildConfigField "boolean", "IS_JACOCO_ENABLE", "" + isJacocoEnable + ""
                    defaultConfig.buildConfigField "String", "JACOCO_HOST","\"" + jacocoHost + "\""
                    println("$TAG jacocoHost= ${jacocoHost}")
                }
            } catch (Exception e) {//cc的工程根目录也能获取到，作为模块名获取android就有异常
                println("$TAG updateBuildConfigField error $e")
            }
        }
    }

    private static Task findOrCreateJacocoReportTask(TaskContainer tasks) {
        Task jacocoTestReportTask = tasks.findByName(TASK_JACOCO_ALL)
        if (!jacocoTestReportTask) {
            jacocoTestReportTask = tasks.create(TASK_JACOCO_ALL)
            jacocoTestReportTask.group = GROUP
            jacocoTestReportTask.description = "Try to generate coverage report for cc!!"
        }
        jacocoTestReportTask
    }

    private static JacocoReport createReportTask(ProjectInternal project, variant) {
        def sourceDirs = JacocoUtils.getAllJavaDir(project) //sourceDirs(variant)
        def classDir
        /*if (project.jacocoReportConfig.isDiffJacoco) {//增量覆盖率
            classDir = new ArrayList<>()
            classDir.add("$project.projectDir/classes")
        } else {
            classDir = JacocoUtils.getAllClassDir(project)
        }*/
        classDir = JacocoUtils.getAllClassDir(project)
        // 过滤不需要统计的class文件
        def finalClassDir = project.files(project.files(classDir).files.collect {
            project.fileTree(dir: it,
                    excludes: JacocoReportExtension.defaultExcludes)
        })
        println("$TAG java classDir size= ${classDir.size()},finalClassDir.size=${finalClassDir.size()}")

        //指定ec文件
        def executionDataDir = "${project.buildDir}/outputs/code_coverage/connected/"
        def executionDataPaths = project.files(project.files(executionDataDir).files.collect {
            project.fileTree(dir: it,
                    includes: ["*.ec"])
        })

        JacocoReport reportTask = project.tasks.create(TASK_JACOCO_CC_REPORT, JacocoReport)
        reportTask.doFirst {
            println("$TAG,createReportTask :do first.executionDataDir=$executionDataDir")
        }
        reportTask.group = GROUP
        reportTask.description = "Generates Jacoco coverage reports for cc whole project."
        reportTask.executionData.setFrom(executionDataPaths)

        reportTask.sourceDirectories.setFrom(sourceDirs)
        reportTask.classDirectories.setFrom(finalClassDir)

        reportTask.reports {
            def destination = project.jacocoReportConfig.destination

            csv.enabled project.jacocoReportConfig.csv.enabled
            html.enabled project.jacocoReportConfig.html.enabled
            xml.enabled project.jacocoReportConfig.xml.enabled

            if (csv.enabled) {
                csv.destination new File((destination == null) ? "${project.buildDir}/jacoco/jacoco.csv" : "${destination.trim()}/jacoco.csv")
            }

            println("$TAG html.enabled = ${html.enabled},destination: $reportTask.reports.html.destination")
            if (html.enabled) {
                html.destination new File((destination == null) ? "${project.buildDir}/jacoco/jacocoHtml" : "${destination.trim()}/jacocoHtml")
            }

            if (xml.enabled) {
                xml.destination new File((destination == null) ? "${project.buildDir}/jacoco/jacoco.xml" : "${destination.trim()}/jacoco.xml")
            }
        }
        reportTask
    }
}
