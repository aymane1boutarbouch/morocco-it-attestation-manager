package com.moroccoit.attestation.util;

public class NumberToWordsFR {

    private static final String[] UNITS = {
        "", "Un", "Deux", "Trois", "Quatre", "Cinq", "Six", "Sept", "Huit", "Neuf", "Dix",
        "Onze", "Douze", "Treize", "Quatorze", "Quinze", "Seize", "Dix-Sept", "Dix-Huit", "Dix-Neuf"
    };

    private static final String[] TENS = {
        "", "Dix", "Vingt", "Trente", "Quarante", "Cinquante", "Soixante", "Soixante-Dix", "Quatre-Vingts", "Quatre-Vingt-Dix"
    };

    public static String convertAmount(double amount) {
        long dirhams = (long) Math.floor(amount);
        int centimes = (int) Math.round((amount - dirhams) * 100);

        String result = convertLong(dirhams);
        if (result.isEmpty()) result = "Zéro";

        if (centimes > 0) {
            result += " Dirhams et " + convertLong(centimes) + " Centimes";
        } else {
            result += " Dirhams";
        }

        return result;
    }

    public static String convertLong(long n) {
        if (n == 0) return "";

        if (n < 20) {
            return UNITS[(int) n];
        }

        if (n < 100) {
            int ten = (int) (n / 10);
            int unit = (int) (n % 10);

            if (ten == 7 || ten == 9) {
                return TENS[ten - 1] + "-" + UNITS[unit + 10];
            } else if (unit == 1 && ten != 8) {
                return TENS[ten] + " et Un";
            } else if (unit > 0) {
                return TENS[ten] + "-" + UNITS[unit];
            } else {
                return TENS[ten];
            }
        }

        if (n < 1000) {
            int hundred = (int) (n / 100);
            int remainder = (int) (n % 100);

            String prefix = (hundred == 1) ? "Cent" : UNITS[hundred] + " Cents";
            if (remainder > 0) {
                if (hundred > 1) prefix = UNITS[hundred] + " Cent";
                return prefix + " " + convertLong(remainder);
            } else {
                return prefix;
            }
        }

        if (n < 1000000) {
            long thousand = n / 1000;
            long remainder = n % 1000;

            String prefix = (thousand == 1) ? "Mille" : convertLong(thousand) + " Mille";
            if (remainder > 0) {
                return prefix + " " + convertLong(remainder);
            } else {
                return prefix;
            }
        }

        if (n < 1000000000) {
            long million = n / 1000000;
            long remainder = n % 1000000;

            String prefix = (million == 1) ? "Un Million" : convertLong(million) + " Millions";
            if (remainder > 0) {
                return prefix + " " + convertLong(remainder);
            } else {
                return prefix;
            }
        }

        return String.valueOf(n);
    }
}
