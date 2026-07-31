package org.devlukadev.skywarstoolsmod.hud;

import cc.polyfrost.oneconfig.config.core.OneColor;
import cc.polyfrost.oneconfig.renderer.NanoVGHelper;
import cc.polyfrost.oneconfig.renderer.font.Fonts;
import cc.polyfrost.oneconfig.utils.InputHandler;
import cc.polyfrost.oneconfig.utils.gui.GuiUtils;
import cc.polyfrost.oneconfig.utils.gui.OneUIScreen;
import net.minecraft.client.Minecraft;
import org.devlukadev.skywarstoolsmod.autododge.AutododgeStorage;
import org.devlukadev.skywarstoolsmod.utils.ChatLib;

import java.util.ArrayList;
import java.util.List;

import cc.polyfrost.oneconfig.libs.universal.UKeyboard;

public class AutododgeScreen extends OneUIScreen {
    private final List<String> dodgeMaps = new ArrayList<>();
    private final StringBuilder input = new StringBuilder();
    private boolean inputFocused = true;

    public AutododgeScreen() {
        super();
        dodgeMaps.addAll(AutododgeStorage.load());
    }

    @Override
    public void initScreen(int width, int height) {
        this.width = Minecraft.getMinecraft().displayWidth;
        this.height = Minecraft.getMinecraft().displayHeight;
        super.initScreen(Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
    }

    private static final float INPUT_H = 80f; // matches your draw() height
    private static final float MAP_ITEM_H = 50f;
    private static final float MAP_ITEM_GAP = 60f;
    private static final float DELETE_BTN_SIZE = 32f;

    private float panelX() {
        return (float) this.width / 3;
    }

    private float panelW() {
        return (float) this.width / 3;
    }

    private float inputX() {
        return panelX() + 15;
    }

    private float inputY() {
        return 100;
    }

    private float inputW() {
        return panelW() - 30;
    }

    private float todoY(int index) {
        return 200 + index * MAP_ITEM_GAP;
    }

    @Override
    public void draw(long vg, float partialTicks, InputHandler inputHandler) {
        NanoVGHelper.INSTANCE.drawRoundedRect(vg, panelX(), 0, panelW(),
                this.height, new OneColor(60, 60, 70).getRGB(), 2);
        NanoVGHelper.INSTANCE.drawText(vg, "Autododge Maps", panelX() + 20, 40,
                new OneColor(220, 220, 220).getRGB(), 40, Fonts.MINECRAFT_REGULAR);
        NanoVGHelper.INSTANCE.drawText(vg, "Press enter to add a map", panelX() + 20, 70,
                new OneColor(220, 220, 220).getRGB(), 20, Fonts.MINECRAFT_REGULAR);

        int boxColor = inputFocused
                ? new OneColor(80, 80, 80).getRGB()
                : new OneColor(50, 50, 45).getRGB();
        NanoVGHelper.INSTANCE.drawRoundedRect(vg, inputX(), inputY(), inputW(), INPUT_H, boxColor, 6f);

        String display = input.toString()
                + (inputFocused && (System.currentTimeMillis() / 500 % 2 == 0) ? "|" : "");
        NanoVGHelper.INSTANCE.drawText(vg, display, inputX() + 5, inputY() + 40, -1, 50, Fonts.REGULAR);

        for (int i = 0; i < dodgeMaps.size(); i++) {
            float y = todoY(i);
            NanoVGHelper.INSTANCE.drawRoundedRect(vg, panelX() + 15, y, inputW(), MAP_ITEM_H, new OneColor(30, 30, 34).getRGB(), 4f);
            NanoVGHelper.INSTANCE.drawText(vg, dodgeMaps.get(i), panelX() + 20, y + 30, -1, 40, Fonts.REGULAR);

            // Delete (X) button, right-aligned within the item row
            float btnX = panelX() + 15 + inputW() - DELETE_BTN_SIZE - 4;
            float btnY = y + (MAP_ITEM_H - DELETE_BTN_SIZE) / 2f;
            NanoVGHelper.INSTANCE.drawRoundedRect(vg, btnX, btnY, DELETE_BTN_SIZE, DELETE_BTN_SIZE, new OneColor(140, 40, 40).getRGB(), 3f);
            NanoVGHelper.INSTANCE.drawText(vg, "X", btnX + 6, btnY + 18, -1, 30, Fonts.REGULAR);
        }
    }

    @Override
    public void onMouseClicked(double mouseX, double mouseY, int mouseButton) {
        super.onMouseClicked(mouseX, mouseY, mouseButton);

        inputFocused = mouseX >= inputX() && mouseX <= inputX() + inputW()
                && mouseY >= inputY() && mouseY <= inputY() + INPUT_H;

        if (mouseButton != 0) return; // only left click deletes

        for (int i = 0; i < dodgeMaps.size(); i++) {
            float y = todoY(i);
            float btnX = panelX() + 15 + inputW() - DELETE_BTN_SIZE - 4;
            float btnY = y + (MAP_ITEM_H - DELETE_BTN_SIZE) / 2f;

            if (mouseX >= btnX && mouseX <= btnX + DELETE_BTN_SIZE
                    && mouseY >= btnY && mouseY <= btnY + DELETE_BTN_SIZE) {
                dodgeMaps.remove(i);
                AutododgeStorage.save(dodgeMaps);
                break; // list mutated, stop iterating
            }
        }
    }

    @Override
    public void onKeyPressed(int keyCode, char typedChar, UKeyboard.Modifiers modifiers) {
        if (keyCode == UKeyboard.KEY_ESCAPE) GuiUtils.closeScreen();

        if (!inputFocused) return;

        // On MC >=1.15.2 this fires twice per keystroke: once with keyCode (typedChar=0),
        // once with typedChar (keyCode=0). On older versions it fires once with both set.
        if (keyCode == UKeyboard.KEY_ENTER) {
            if (input.length() > 0) {
                dodgeMaps.add(input.toString());
                input.setLength(0);
                AutododgeStorage.save(dodgeMaps);
                input.setLength(0);
            }
        } else if (keyCode == UKeyboard.KEY_BACKSPACE) {
            if (input.length() > 0) input.deleteCharAt(input.length() - 1);
        } else if (typedChar != 0 && !Character.isISOControl(typedChar)) {
            input.append(typedChar);
        }
    }

    @Override
    public void onScreenClose() {
        ChatLib.chat("Autododge config saved. You will automatically dodge: " + dodgeMaps);
    }
}
