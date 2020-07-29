package com.xiaopo.flying.stickerview.util;


import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ColorUtils {
    private final static String TAG = "ColorUtils";
      static int dominantColor;
     static int dominate;
    /*Only black and white text color*/
    @ColorInt
    public static int getBlackAndWhiteColor(@ColorInt int color) {
        double a = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        return a < 0.5 ? Color.BLACK : Color.WHITE;
    }

    /* for all color contrast text color */
    public static int getTextColor(int color) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        int alpha = Color.alpha(color);
        return Color.argb(255, 255 - red, 255 - green, 255 - blue);
    }

    /* Image/pattern image/Gradient color contrast text color */
    public static int getDominantColor1(Bitmap bitmap, int action_code) {
        if (bitmap == null)
            throw new NullPointerException();
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int size = width * height;
        int[] pixels = new int[size];
        int pixel = bitmap.getPixel(0, 0);
        Bitmap bitmap2 = bitmap.copy(Bitmap.Config.ARGB_4444, false);
        bitmap2.getPixels(pixels, 0, width, 0, 0, width, height);
        final List<HashMap<Integer, Integer>> colorMap = new ArrayList<HashMap<Integer, Integer>>();
        colorMap.add(new HashMap<Integer, Integer>());
        colorMap.add(new HashMap<Integer, Integer>());
        colorMap.add(new HashMap<Integer, Integer>());
        int color = 0;
        int r = 0;
        int g = 0;
        int b = 0;
        Integer rC, gC, bC;
        Log.i(TAG, "getDominantColor1: pixel::-" + pixels.length);
        for (int i = 0; i < pixels.length; i++) {
            color = pixels[i];
            r = Color.red(color);
            g = Color.green(color);
            b = Color.blue(color);
            rC = colorMap.get(0).get(r);
            if (rC == null)
                rC = 0;
            colorMap.get(0).put(r, ++rC);
            gC = colorMap.get(1).get(g);
            if (gC == null)
                gC = 0;
            colorMap.get(1).put(g, ++gC);
            bC = colorMap.get(2).get(b);
            if (bC == null)
                bC = 0;
            colorMap.get(2).put(b, ++bC);
        }

        int[] rgb = new int[3];
        for (int i = 0; i < 3; i++) {
            int max = 0;
            int val = 0;
            for (Map.Entry<Integer, Integer> entry : colorMap.get(i).entrySet()) {
                if (entry.getValue() > max) {
                    max = entry.getValue();
                    val = entry.getKey();
                }
            }
            rgb[i] = val;

        }
        int dominantColor = 0;

        int colors = Color.rgb(rgb[0], rgb[1], rgb[2]);
        String hexColor1 = String.format("#%06X", (colors));
        Log.i(TAG, "getDominantColor1: Dominate color:::=" + hexColor1);


        if (action_code == 1)
            dominantColor = Color.rgb(255 - rgb[0], 255 - rgb[1], 255 - rgb[2]);
        else {
            double a = 1 - (0.299 * rgb[0] + 0.587 * rgb[1] + 0.114 * rgb[2]) / 255;
            dominantColor = a < 0.5 ? Color.BLACK : Color.WHITE;
        }
        String hexColor11 = String.format("#%06X", (dominantColor));
        Log.i(TAG, "getDominantColor1: contrast Color "+hexColor11);
        return dominantColor;
    }

}
