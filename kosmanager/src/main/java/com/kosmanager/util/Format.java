package com.kosmanager.util;

import java.text.NumberFormat;
import java.util.Locale;

public final class Format {
    private static final NumberFormat NF = NumberFormat.getIntegerInstance(Locale.forLanguageTag("id-ID"));

    private Format() { }

    public static String rupiah(double nilai) {
        return "Rp" + NF.format(Math.round(nilai));
    }
}
