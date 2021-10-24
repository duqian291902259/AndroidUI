package site.duqian.gradle.coverage;

/**
 * Description:相关配置、过滤规则
 * @author  Created by 杜小菜 on 2021/9/10 - 18:29 .
 * E-mail: duqian2010@gmail.com
 */
class JacocoReportExtension {

    ReportConfiguration csv
    ReportConfiguration html
    ReportConfiguration xml
    //生成报告的目录
    String destination

    JacocoReportExtension() {
        this.html = new ReportConfiguration(true)
        this.csv = new ReportConfiguration(false)
        this.xml = new ReportConfiguration(false)
        this.destination = null
    }

    public static final Collection<String> thirdLibExcludes =
            ['**/io/realm/**.class',
             '**/site/duqian/**.*',
             '**/*_Factory.class',
             '**SubApp.kt'
            ].asImmutable()

    public static final Collection<String> androidDataBindingExcludes =
            ['android/databinding/**/*.class',
             'androidx/databinding/**/*.class',
             '**/android/databinding/*Binding.class',
             '**/androidx/databinding/*Binding.class',
             '**/DataBindingInfo.class',
             '**/DataBinderMapperImpl**.class',
             '**/DataBinderMapperImpl**.class',
             '**/BR.*'].asImmutable()

    public static final Collection<String> androidExcludes =
            ['**/R.class',
             '**/R$*.class',
             '**/BuildConfig.*',
             '**/*JavascriptBridge.class',
             '**/Manifest*.*'].asImmutable()

    public static final Collection<String> butterKnifeExcludes =
            ['**/*$ViewInjector*.*',
             '**/*$InjectAdapter.class',
             '**/*$ModuleAdapter.class',
             '**/*$ViewBinder*.*'].asImmutable()

    public static final Collection<String> dagger2Excludes =
            ['**/*_MembersInjector.class',
             '**/Dagger**.class',
             '**/Dagger*Component.class',
             '**/Dagger*Component$Builder.class',
             '**/*Module_*Factory.class'].asImmutable()

    public static final Collection<String> defaultExcludes = (thirdLibExcludes + androidDataBindingExcludes + androidExcludes + dagger2Excludes).asImmutable()

    //static Closure<Collection<String>> defaultExcludesFactory = { defaultExcludes }

    //jacoco开关
    boolean isJacocoEnable = true
    boolean isDiffJacoco = false //是否差量更新

    //需要对比class的分支名
    String branchName
    //需要插桩的文件
    List<String> includes
    String gitPushShell
    String pullDiffClassShell
    //git-bash的路径，如果找不到，自行配置
    private String gitBashPath
    String appName = "cc-android"
    String jacocoHost //服务器地址
   // String reportHost //报告地址

    String getGitBashPath() {
        if (gitBashPath == null || gitBashPath.isEmpty()) {
            Process process = 'where git'.execute()
            String path = process.inputStream.text
            process.closeStreams()
            String[] paths = path.split('\n')
            String temp = ''
            paths.each {
                try {
                    File file = new File(it)
                    def parentFile = file.getParentFile()
                    if (parentFile != null) {
                        File gitBash = new File(parentFile.getParent() + File.separator + 'git-bash.exe')
                        println("dq-jacoco GitBashPath:$gitBash exist:${gitBash.exists()}")
                        if (gitBash.exists()) {
                            temp = gitBash.absolutePath
                            return temp
                        }
                    }
                } catch (Exception e) {
                    println("dq-jacoco GitBashPath:$e")
                }
            }
            return temp
        }
        return gitBashPath
    }
}

