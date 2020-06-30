package io.github.zaxarner.minecraft.castlesiege.utils;

/**
 * Created by JamesCZ98 on 11/27/2019.
 */
public class CollectionUtils {

    public static boolean arrayContains(Object[] array, Object object) {
        for(int i=0; i < array.length; i++) {
            Object o = array[i];
            if(o == object || o.equals(object)) {
                return true;
            }
        }

        return false;
    }

}
