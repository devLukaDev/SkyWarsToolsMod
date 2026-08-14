package org.devlukadev.skywarstoolsmod.features.tablevels;

import org.devlukadev.skywarstoolsmod.SkyWarsToolsMod;
import org.devlukadev.skywarstoolsmod.utils.fetchutils.responses.SkyWarsResponse;

import java.text.DecimalFormat;

/**
 * Turns a SkyWarsResponse + the user's format string (config.tabLevelText)
 * into the final string that replaces a player's tab-list name.
 */
public class TabStringConstructor {

    private static final DecimalFormat RATIO_FORMAT = new DecimalFormat("0.00");
    private static final DecimalFormat NUMBER_FORMAT = new DecimalFormat("#,###");

    private static final String UNKNOWN_LEVEL = "§c[?]";
    private static final String LOADING_LEVEL = "§7[1✯]";
    private static final String MISSING_STAT = "-";

    private TabStringConstructor() {}

    /**
     * Builds the full replacement tab name.
     *
     * @param response       stats for this player, or null if we don't have them (yet, or ever)
     * @param originalName   whatever GuiPlayerTabOverlay#getPlayerName originally returned
     *                       (Hypixel rank prefix, colors, nickname, etc.) - used for %default%
     * @param confirmedNicked true if we know this player is nicked and will never resolve to stats
     *                        (skip the "loading" state and show the permanent unknown marker instead)
     */
    public static String build(SkyWarsResponse response, String originalName, boolean confirmedNicked) {
        String format = SkyWarsToolsMod.config.tabLevelText;
        if (format == null || format.isEmpty()) {
            return originalName;
        }

        String levelToken = resolveLevelToken(response, confirmedNicked);

        String result = format;
        result = result.replace("%default%", safe(originalName));
        result = result.replace("%level%", levelToken);
        result = result.replace("%wl%", ratio(response, "wins", "losses"));
        result = result.replace("%kd%", ratio(response, "kills", "deaths"));
        result = result.replace("%kills%", stat(response, "kills"));
        result = result.replace("%wins%", stat(response, "wins"));
        result = result.replace("%deaths%", stat(response, "deaths"));
        result = result.replace("%losses%", stat(response, "losses"));
        result = result.replace("%exp%", expStat(response));

        return translateColorCodes(result);
    }

    private static String resolveLevelToken(SkyWarsResponse response, boolean confirmedNicked) {
        if (confirmedNicked) {
            return UNKNOWN_LEVEL;
        }
        if (response == null || response.display == null || response.display.levelFormattedWithBrackets == null) {
            return LOADING_LEVEL;
        }
        return response.display.levelFormattedWithBrackets.trim();
    }

    private static String ratio(SkyWarsResponse response, String numeratorField, String denominatorField) {
        if (response == null || response.stats == null) {
            return MISSING_STAT;
        }
        long numerator = statValue(response, numeratorField);
        long denominator = statValue(response, denominatorField);
        if (denominator == 0) {
            return RATIO_FORMAT.format(numerator);
        }
        return RATIO_FORMAT.format((double) numerator / (double) denominator);
    }

    private static String stat(SkyWarsResponse response, String field) {
        if (response == null || response.stats == null) {
            return MISSING_STAT;
        }
        return NUMBER_FORMAT.format(statValue(response, field));
    }

    private static long statValue(SkyWarsResponse response, String field) {
        switch (field) {
            case "wins": return response.stats.wins;
            case "losses": return response.stats.losses;
            case "kills": return response.stats.kills;
            case "deaths": return response.stats.deaths;
            default: throw new IllegalArgumentException("Unknown stat field: " + field);
        }
    }

    private static String expStat(SkyWarsResponse response) {
        if (response == null) {
            return MISSING_STAT;
        }
        return NUMBER_FORMAT.format((long) response.stats.skywars_experience);
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }

    /**
     * Converts &-codes typed by the user in the config into real §-codes.
     * Untouched §-codes already embedded in %default%/%level% pass through fine.
     */
    private static String translateColorCodes(String text) {
        char[] chars = text.toCharArray();
        for (int i = 0; i < chars.length - 1; i++) {
            if (chars[i] == '&' && "0123456789abcdefklmnorABCDEFKLMNOR".indexOf(chars[i + 1]) > -1) {
                chars[i] = '§';
                chars[i + 1] = Character.toLowerCase(chars[i + 1]);
            }
        }
        return new String(chars);
    }
}