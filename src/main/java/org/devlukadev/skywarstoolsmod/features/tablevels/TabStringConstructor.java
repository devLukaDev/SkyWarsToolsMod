package org.devlukadev.skywarstoolsmod.features.tablevels;

import net.minecraft.client.gui.FontRenderer;
import org.devlukadev.skywarstoolsmod.SkyWarsToolsMod;
import org.devlukadev.skywarstoolsmod.utils.TextAlignUtil;
import org.devlukadev.skywarstoolsmod.utils.fetchutils.responses.SkyWarsResponse;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Turns a SkyWarsResponse + the user's format string (config.tabLevelText)
 * into the final string that replaces a player's tab-list name.
 */
public class TabStringConstructor {

    private static final Pattern TOKEN_PATTERN = Pattern.compile("%\\w+%");
    private static final DecimalFormat RATIO_FORMAT = new DecimalFormat("0.00");
    private static final DecimalFormat NUMBER_FORMAT = new DecimalFormat("#,###");
    private static String cachedFormat = null;
    private static List<String> cachedTemplate = null;

    private static final String UNKNOWN_LEVEL = "§c[?]";
    private static final String LOADING_LEVEL = "§7[1✯]";
    private static final String MISSING_STAT = "-";

    /** Splits the format string into literal/token pieces, in order. Cached until the format changes. */
    private static List<String> template(String format) {
        if (format.equals(cachedFormat)) return cachedTemplate;

        List<String> parts = new ArrayList<>();
        Matcher m = TOKEN_PATTERN.matcher(format);
        int last = 0;
        while (m.find()) {
            if (m.start() > last) parts.add(format.substring(last, m.start()));
            parts.add(m.group()); // e.g. "%level%"
            last = m.end();
        }
        if (last < format.length()) parts.add(format.substring(last));

        cachedFormat = format;
        cachedTemplate = parts;
        return parts;
    }

    private static boolean isToken(String part) {
        return part.length() > 1 && part.charAt(0) == '%' && part.charAt(part.length() - 1) == '%';
    }

    /** Resolves one token to its display value (no padding, colors translated). */
    private static String resolveToken(String token, SkyWarsResponse response, String originalName, boolean confirmedNicked) {
        switch (token) {
            case "%default%": return translateColorCodes(safe(originalName));
            case "%level%":   return translateColorCodes(resolveLevelToken(response, confirmedNicked));
            case "%wl%":      return translateColorCodes(ratio(response, "wins", "losses"));
            case "%kd%":      return translateColorCodes(ratio(response, "kills", "deaths"));
            case "%kills%":   return translateColorCodes(stat(response, "kills"));
            case "%wins%":    return translateColorCodes(stat(response, "wins"));
            case "%deaths%":  return translateColorCodes(stat(response, "deaths"));
            case "%losses%":  return translateColorCodes(stat(response, "losses"));
            case "%exp%":     return translateColorCodes(expStat(response));
            default:          return token; // unknown token, leave as-is
        }
    }

    /** Returns the ordered, resolved-but-unpadded pieces for one player. Used both for width scanning and final build. */
    public static List<String> resolveSegments(SkyWarsResponse response, String originalName, boolean confirmedNicked) {
        String format = SkyWarsToolsMod.config.levelsText;
        if (format == null || format.isEmpty()) {
            return Collections.singletonList(originalName);
        }
        List<String> pieces = new ArrayList<>();
        for (String part : template(format)) {
            pieces.add(isToken(part)
                    ? resolveToken(part, response, originalName, confirmedNicked)
                    : translateColorCodes(part));
        }
        return pieces;
    }

    /** Builds the final aligned string using precomputed per-index column widths (index into resolveSegments()). */
    public static String buildAligned(SkyWarsResponse response, String originalName, boolean confirmedNicked,
                                      FontRenderer fr, int[] colWidths) {
        String format = SkyWarsToolsMod.config.levelsText;
        if (format == null || format.isEmpty()) return originalName;

        List<String> template = template(format);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < template.size(); i++) {
            String part = template.get(i);
            String value = isToken(part)
                    ? resolveToken(part, response, originalName, confirmedNicked)
                    : translateColorCodes(part);

            if (isToken(part) && colWidths != null && i < colWidths.length) {
                value = TextAlignUtil.padToWidth(fr, value, colWidths[i]);
            }
            sb.append(value);
        }
        return sb.toString();
    }

    private static String resolveLevelToken(SkyWarsResponse response, boolean confirmedNicked) {
        if (confirmedNicked) {
            return UNKNOWN_LEVEL;
        }
        if (response == null) {
            return UNKNOWN_LEVEL;
        }
        if(response.display == null || response.display.levelFormattedWithBrackets == null) {
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