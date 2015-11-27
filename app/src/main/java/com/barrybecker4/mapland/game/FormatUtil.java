package com.barrybecker4.mapland.game;

import java.text.DecimalFormat;

/**
 * @author Barry Becker
 */
public class FormatUtil {

    private static final DecimalFormat FORMAT = new DecimalFormat("###,###.##");
    private static final DecimalFormat INT_FORMAT = new DecimalFormat("#,###");

    private FormatUtil() {
    }


    /**
     * Show a reasonable number of significant digits.
     * @param num the number to format.
     * @return a nicely formatted string representation of the number.
     */
    public static String formatNumber(double num) {
        double absnum = Math.abs(num);

        if (absnum == 0)  {
            return "0";
        }

        if (absnum > 10000000000000.0) {
            return FORMAT.format(num / 1000000000000.0) + "T";
        }
        else if (absnum > 10000000000.0) {
            return FORMAT.format(num / 1000000000.0) + "B";
        }
        else if (absnum > 10000000.0) {
            return FORMAT.format(num / 1000000.0) + "M";
        }
        else if (absnum > 1000.0) {
            FORMAT.setMinimumFractionDigits(0);
            FORMAT.setMaximumFractionDigits(0);
        }
        else if (absnum > 100.0) {
            FORMAT.setMinimumFractionDigits(1);
            FORMAT.setMaximumFractionDigits(1);
        }
        else if (absnum > 1.0) {
            FORMAT.setMinimumFractionDigits(1);
            FORMAT.setMaximumFractionDigits(3);
        }
        else if (absnum > 0.0001) {
            FORMAT.setMinimumFractionDigits(2);
            FORMAT.setMaximumFractionDigits(5);
        }
        else if (absnum>0.000001) {
            FORMAT.setMinimumFractionDigits(3);
            FORMAT.setMaximumFractionDigits(8);
        }
        else {
            FORMAT.setMinimumFractionDigits(6);
            FORMAT.setMaximumFractionDigits(11);
        }

        return FORMAT.format(num);
    }


    /**
     * @param num the number to format.
     * @return a nicely formatted string representation of the number.
     */
    public static String formatNumber(int num) {
        return INT_FORMAT.format(num);
    }
    /** Just show the last few digits for brevity */
    public static String formatId(Long id) {
        return "..." + id.toString().substring(10);
    }
}
