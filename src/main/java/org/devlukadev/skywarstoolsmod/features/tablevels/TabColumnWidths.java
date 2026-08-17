package org.devlukadev.skywarstoolsmod.features.tablevels;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.network.NetworkPlayerInfo;
import org.devlukadev.skywarstoolsmod.SkyWarsToolsMod;
import org.devlukadev.skywarstoolsmod.utils.NickDetector;
import org.devlukadev.skywarstoolsmod.utils.fetchutils.responses.SkyWarsResponse;

import java.util.Collection;
import java.util.List;

public class TabColumnWidths {
    private static int[] widths = new int[0];

    public static int[] get() {
        return widths;
    }

    public static void recompute(FontRenderer fr, Collection<NetworkPlayerInfo> players,
                                 java.util.function.Function<NetworkPlayerInfo, SkyWarsResponse> statLookup,
                                 java.util.function.Predicate<NetworkPlayerInfo> nickedCheck) {
        List<String> templateShape = null;
        int[] max = null;

        for (NetworkPlayerInfo p : players) {
            String originalName =p.getGameProfile().getName();
            SkyWarsResponse resp;
            boolean nicked;

            if (NickDetector.isLikelyNicked(p)) {
                if (NickDetector.isMythical(p)) {
                    resp = SkyWarsRequestCache.getStats(p.getGameProfile().getName());
                    nicked = false;
                } else {
                    resp = null;
                    nicked = true;
                }
            } else {
                resp = SkyWarsRequestCache.getStats(p.getGameProfile().getId());
                nicked = false;
            }

            List<String> segs = TabStringConstructor.resolveSegments(resp, originalName, nicked);
            if (max == null) max = new int[segs.size()];

            for (int i = 0; i < segs.size(); i++) {
                max[i] = Math.max(max[i], fr.getStringWidth(segs.get(i)));
            }
        }

        if (max != null) {
            for (int i = 0; i < max.length; i++) max[i] += SkyWarsToolsMod.config.levelsGutter; // small gutter between columns
        }
        widths = max != null ? max : new int[0];
    }
}