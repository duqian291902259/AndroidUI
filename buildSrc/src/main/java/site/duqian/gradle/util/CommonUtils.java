package site.duqian.gradle.util;

/**
 * Description:通用工具类
 * @author  Created by 杜小菜 on 2021/9/16 - 19:40 .
 * E-mail: duqian2010@gmail.com
 */
public class CommonUtils {
    public static boolean isWindowsOS() {
        return System.getProperties().getProperty("os.name").toLowerCase().contains("windows");
    }
}
