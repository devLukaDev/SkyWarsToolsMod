package org.devlukadev.skywarstoolsmod.utils.fetchutils.responses;

public class SkyWarsResponse {
    public long took;
    public String player;
    public Display display;
    public long queried;
    public double exp;

    public static class Display {
        public String levelFormattedWithBrackets;
        public String levelFormatted;
        public String newPackageRank;
        public String monthlyPackageRank;
        public String rankPlusColor;
        public String monthlyRankColor;
        public String active_scheme;
    }
}