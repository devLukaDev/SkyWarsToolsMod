package org.devlukadev.skywarstoolsmod.features.tablevels;

import java.util.List;

public class TabRowRenderContext {
    public static List<String> lastSegments;   // resolved, unpadded pieces for the row about to be drawn
    public static int[] lastColWidths;          // TabColumnWidths.get() snapshot
    public static String lastBuiltString;       // exact string getPlayerName returned, for verification
    public static int max;

}
