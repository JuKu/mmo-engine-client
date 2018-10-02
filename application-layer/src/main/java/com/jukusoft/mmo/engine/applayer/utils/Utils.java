package com.jukusoft.mmo.engine.applayer.utils;

public class Utils {

    protected Utils() {
        //
    }

    public static String getSection (final String section) {
        if (section == null) {
            throw new NullPointerException("section cannot be null.");
        }

        if (section.isEmpty()) {
            throw new IllegalArgumentException("section cannot be empty.");
        }

        StringBuilder sb = new StringBuilder();

        sb.append("===={ " + section + " }");

        while (sb.length() < 80) {
            sb.append("-");
        }

        return sb.toString();
    }

    public static void printSection (final String section) {
        String s = getSection(section);

        //TODO: replace this with logger, e.q. LocalLogger.print("\n" + s);
        System.out.println("\n" + s);
    }

}
