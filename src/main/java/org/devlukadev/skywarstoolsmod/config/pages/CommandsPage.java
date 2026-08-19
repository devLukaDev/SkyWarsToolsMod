package org.devlukadev.skywarstoolsmod.config.pages;

import cc.polyfrost.oneconfig.gui.pages.Page;
import cc.polyfrost.oneconfig.renderer.NanoVGHelper;
import cc.polyfrost.oneconfig.renderer.font.Fonts;
import cc.polyfrost.oneconfig.utils.InputHandler;
import cc.polyfrost.oneconfig.utils.gui.GuiUtils;
import net.minecraft.client.Minecraft;
import org.devlukadev.skywarstoolsmod.SkyWarsToolsMod;
import org.devlukadev.skywarstoolsmod.features.autododge.AutododgeScreen;
import org.devlukadev.skywarstoolsmod.features.sessions.SessionManager;

public class CommandsPage extends Page {

    private static final CommandEntry[] COMMANDS = {
            new CommandEntry("/swt", "Opens the SkyWarsTools config GUI",
                    () -> SkyWarsToolsMod.config.openGui()),
            new CommandEntry("/swt sessions sync", "Re-syncs session stats against Hypixel API",
                    () -> SessionManager.getInstance().sync(Minecraft.getMinecraft().thePlayer.getName())),
            new CommandEntry("/swt sessions reset", "Resets the current session and starts a new one",
                    () -> SessionManager.getInstance().startSession(Minecraft.getMinecraft().thePlayer.getName())),
            new CommandEntry("/swt autododge", "Opens the Autododge screen",
                    () -> GuiUtils.displayScreen(new AutododgeScreen()))
    };

    private static final int ROW_HEIGHT = 32;
    private static final int CMD_COL_WIDTH = 200;
    private static final int BUTTON_WIDTH = 100;
    private static final int BUTTON_HEIGHT = 28;

    public CommandsPage() {
        super("Command Overview");
    }

    @Override
    public void draw(long vg, int x, int y, InputHandler inputHandler) {
        NanoVGHelper.INSTANCE.drawText(vg, "Command Overview",
                x + 20, y + 30, -1, 32, Fonts.BOLD);

        int tableTop = y + 90;

        for (int i = 0; i < COMMANDS.length; i++) {
            CommandEntry entry = COMMANDS[i];
            int rowY = tableTop + (i * ROW_HEIGHT);

            NanoVGHelper.INSTANCE.drawText(vg, entry.command,
                    x + 20, rowY, -1, 16, Fonts.REGULAR);

            NanoVGHelper.INSTANCE.drawText(vg, entry.description,
                    x + 20 + CMD_COL_WIDTH, rowY, -1, 16, Fonts.REGULAR);

            int buttonX = x + 20 + CMD_COL_WIDTH + 720;
            int buttonY = rowY - 12;

            boolean hovered = inputHandler.isAreaHovered(buttonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT);

            NanoVGHelper.INSTANCE.drawRoundedRect(vg, buttonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT,
                    hovered ? 0xFF5A5A5A : 0xFF3A3A3A, 4f);

            NanoVGHelper.INSTANCE.drawCenteredText(vg, "Run",
                    buttonX + (BUTTON_WIDTH / 2f), buttonY + (BUTTON_HEIGHT / 2f) + 2,
                    -1, 20, Fonts.REGULAR);

            if (hovered && inputHandler.isClicked()) {
                entry.action.run();
            }
        }
    }

    @Override
    public int getMaxScrollHeight() {
        return 60 + (COMMANDS.length * ROW_HEIGHT) + 20;
    }

    private static class CommandEntry {
        final String command;
        final String description;
        final Runnable action;

        CommandEntry(String command, String description, Runnable action) {
            this.command = command;
            this.description = description;
            this.action = action;
        }
    }
}