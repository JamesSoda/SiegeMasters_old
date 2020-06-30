package io.github.zaxarner.minecraft.castlesiege.utils;

import java.util.Random;

/**
 * Created by JamesCZ98 on 7/25/2019.
 */
public class MathUtils {


    public static int ranNumber(int min, int max) {

        Random r = new Random();
        return r.nextInt(max - min + 1) + min;
    }

    public static double ranDouble(double min, double max) {

        Random r = new Random();
        return min + (max - min) * r.nextDouble();
    }

    public static int clamp(int value, int min, int max) {
        if(value < min)
            value = min;

        if(value > max)
            value = max;

        return value;
    }

    public static float clamp(float value, float min, float max) {
        if(value < min)
            value = min;

        if(value > max)
            value = max;

        return value;
    }

}
