package util.common;

import java.util.List;

public class ListUtil {
    /**
     * 获得集合元素个数
     */
    public static int getSize(List<?> list) {
        return list == null ? 0 : list.size();
    }

    /**
     * 获得数组元素个数
     */
    public static int getSize(Object[] array) {
        return array == null ? 0 : array.length;
    }

    /**
     * 判断集合是否为空
     */
    public static boolean isEmpty(List<?> list) {
        return getSize(list) == 0;
    }

    /**
     * 判断数组是否为空
     */
    public static boolean isEmpty(Object[] array) {
        return getSize(array) == 0;
    }
}
