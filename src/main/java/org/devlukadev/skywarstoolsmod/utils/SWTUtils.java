package org.devlukadev.skywarstoolsmod.utils;

public class SWTUtils {


    public static double calcLevel(double xp) {
        double[] perLevelXp = {
                10, 25, 50, 75, 100, 250, 500, 750, 1000,
                1250, 1500, 1750, 2000, 2500, 3000, 3500,
                4000, 4500, 5000
        };

        int level = 1;

        for (double requiredXp : perLevelXp) {
            if (xp < requiredXp) {
                return level + xp / requiredXp;
            }

            xp -= requiredXp;
            level++;
        }

        level += (int) Math.floor(xp / 5000);
        double remainder = xp % 5000;

        return level + remainder / 5000;
    }


}
