package com.xiaopo.flying.stickerview.util;

import android.content.res.Resources;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FontProvider {
    private static final String DEFAULT_FONT_NAME = "Helvetica";

    private final Map<String, Typeface> typefaces;
    private final Map<String, String> fontNameToTypefaceFile;
    private final Resources resources;
    private final List<String> fontNames;

    public FontProvider(Resources resources) {
        this.resources = resources;

        typefaces = new HashMap<>();

        // populate fonts
        fontNameToTypefaceFile = new HashMap<>();
        fontNameToTypefaceFile.put("Arial", "Arial.ttf");
        fontNameToTypefaceFile.put("Eutemia", "Eutemia.ttf");
        fontNameToTypefaceFile.put("GREENPIL", "GREENPIL.ttf");
        fontNameToTypefaceFile.put("Grinched", "Grinched.ttf");
        fontNameToTypefaceFile.put("Helvetica", "Helvetica.ttf");
        fontNameToTypefaceFile.put("Libertango", "Libertango.ttf");
        fontNameToTypefaceFile.put("Metal Macabre", "MetalMacabre.ttf");
        fontNameToTypefaceFile.put("Parry Hotter", "ParryHotter.ttf");
        fontNameToTypefaceFile.put("SCRIPTIN", "SCRIPTIN.ttf");
        fontNameToTypefaceFile.put("The Godfather v2", "TheGodfather_v2.ttf");
        fontNameToTypefaceFile.put("Aka Dora", "akaDora.ttf");
        fontNameToTypefaceFile.put("Waltograph", "waltograph42.ttf");
        fontNameToTypefaceFile.put("SEASRN", "SEASRN.ttf");
        fontNameToTypefaceFile.put("Chunk Five Pritn", "ChunkFivePrint.ttf");
        fontNameToTypefaceFile.put("Chunk Five Regular", "ChunkFive-Regular.otf");
        fontNameToTypefaceFile.put("FFF_Tusj", "FFF_Tusj.ttf");

        fontNames = new ArrayList<>(fontNameToTypefaceFile.keySet());
    }


    public Typeface getTypeface(@Nullable String typefaceName) {
        if (TextUtils.isEmpty(typefaceName)) {
            return Typeface.DEFAULT;
        } else {

            if (typefaces.get(typefaceName) == null) {
                typefaces.put(typefaceName,
                        Typeface.createFromAsset(resources.getAssets(), fontNameToTypefaceFile.get(typefaceName)));
            }
            return typefaces.get(typefaceName);
        }
    }

    public List<String> getFontNames() {
        return fontNames;
    }


    public String getDefaultFontName() {
        return DEFAULT_FONT_NAME;
    }
}
