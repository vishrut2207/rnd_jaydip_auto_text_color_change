package com.xiaopo.flying.stickerview.util;


import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ColorUtils {
    private final static String TAG = "ColorUtils";

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
            Log.i(TAG, "getDominantColor1: val::-" + val);
            Log.i(TAG, "getDominantColor1: max::-" + max);
        }
        int dominantColor = 0;
        int colors = Color.rgb(rgb[0], rgb[1], rgb[2]);
        String hexColor1 = String.format("#%06X", (0xFFFFFF & colors));
        Log.i(TAG, "getDominantColor1: color:::="+hexColor1);

        if (action_code == 1)
            dominantColor = Color.rgb(255 - rgb[0], 255 - rgb[1], 255 - rgb[2]);
        else {
            double a = 1 - (0.299 * rgb[0] + 0.587 * rgb[1] + 0.114 * rgb[2]) / 255;
            dominantColor = a < 0.5 ? Color.BLACK : Color.WHITE;
        }
      /*  int cc = Color.rgb(rgb[0], rgb[1], rgb[2]);
        String hexColor1 = String.format("#%06X", (0xFFFFFF & cc));
        Log.i(TAG, "getDominantColor1: image_color::-" + hexColor1);*/

        return dominantColor;
    }


    public static int getAverageColor(Drawable image) {
//Setup initial variables
        int hSamples = 40; //Number of pixels to sample on horizontal axis
        int vSamples = 40; //Number of pixels to sample on vertical axis
        int sampleSize = hSamples * vSamples; //Total number of pixels to sample
        float[] sampleTotals = {0, 0, 0}; //Holds temporary sum of HSV values
//If white pixels are included in sample, the average color will
// often have an unexpected shade. For this reason, we set a
// minimum saturation for pixels to be included in the sample set.
// Saturation < 0.1 is very close to white (see http://mkweb.bcgsc.ca/color_summarizer/?faq)
        float minimumSaturation = 0.1f; //Saturation range is 0...1
//By the same token, we should ignore transparent pixels
// (pixels with low alpha value)
        int minimumAlpha = 200; //Alpha range is 0...255
//Get bitmap
        Bitmap b = ((BitmapDrawable) image).getBitmap();
        int width = b.getWidth();
        int height = b.getHeight();
//Loop through pixels horizontally
        float[] hsv = new float[3];
        int sample;
        for (int i = 0; i < width; i += (width / hSamples)) {
//Loop through pixels vertically
            for (int j = 0; j < height; j += (height / vSamples)) {
//Get pixel & convert to HSV format
                sample = b.getPixel(i, j);
                Color.colorToHSV(sample, hsv);
//Check pixel matches criteria to be included in sample
                if ((Color.alpha(sample) > minimumAlpha) && (hsv[1] >= minimumSaturation)) {
//Add sample values to total
                    sampleTotals[0] += hsv[0]; //H
                    sampleTotals[1] += hsv[1]; //S
                    sampleTotals[2] += hsv[2]; //V
                } else {
                    Log.v(TAG, "Pixel rejected: Alpha " + Color.alpha(sample) + ", H: " + hsv[0] + ", S:" + hsv[1] + ", V:" + hsv[1]);
                }
            }
        }
//Divide total by number of samples to get average HSV values
        float[] average = new float[3];
        average[0] = sampleTotals[0] / sampleSize;
        average[1] = sampleTotals[1] / sampleSize;
        average[2] = sampleTotals[2] / sampleSize;
//Return average tuplet as RGB color
        return Color.HSVToColor(average);
    }

}
