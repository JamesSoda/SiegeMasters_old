package io.github.zaxarner.minecraft.castlesiege.utils;

import org.apache.commons.lang.WordUtils;

import java.text.DecimalFormat;

/**
 * Created by JamesCZ98 on 8/5/2019.
 */
public class StringUtils {

    public final static DecimalFormat df2 = new DecimalFormat("#.##");

    public static String makePretty(String string) {
        return WordUtils.capitalize(string.toLowerCase().replace("_", " ").replace("-", " "));
    }

    public static String formatTime(int seconds) {

        int h = seconds / 3600;
        int m = (seconds % 3600) / 60;
        int s = (seconds % 3600) % 60;

        if(h > 0) {
            return String.format("%dhr, %dmin, and %dsec", h, m, s);
        } else if(m > 0) {
            return String.format("%dmin, and %dsec", m, s);
        } else {
            return String.format("%dsec", s);
        }
    }

}
