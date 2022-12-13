package www.ings.vip.utils;

/**
 * @author ljr
 * @date 2022-09-23 17:15
 */
public class TableClassUtils {

    /**
     * getMethodName
     * @param str
     * @return
     */
    protected static String getMethodName(String str) {
        char[] cs = str.toCharArray();
        cs[0] -= 32;
        return String.valueOf(cs);
    }

}
