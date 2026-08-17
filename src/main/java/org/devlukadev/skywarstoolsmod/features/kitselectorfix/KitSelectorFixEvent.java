package org.devlukadev.skywarstoolsmod.features.kitselectorfix;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.devlukadev.skywarstoolsmod.SkyWarsToolsMod;
import org.devlukadev.skywarstoolsmod.utils.ChatLib;
import org.lwjgl.input.Mouse;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KitSelectorFixEvent {


    /**
     * Handles the "Kit select" GUI:
     * - Detects when the inventory is open
     * - Detects clicks on items in that inventory
     * - Waits for a "You selected X!" chat confirmation (no fixed tick delay,
     * so it adapts naturally to any ping)
     * - Re-clicks the same slot if the confirmed item name doesn't match what
     * was clicked
     * - Warns in chat if the inventory closes unexpectedly, or if no
     * confirmation ever arrives (timeout safety net)
     * <p>
     * Register an instance of this class on the Forge event bus, e.g. in your
     * mod's init:
     * MinecraftForge.EVENT_BUS.register(new KitSelectHandler());
     */

    private static final String TARGET_INVENTORY_TITLE = "Kit Selector";
    private static final Pattern SELECT_PATTERN = Pattern.compile("^§r§eYou've selected the (.+) kit!§r$");
    private static final int TIMEOUT_TICKS = 20; // ~1 seconds at 20 tps, safety net only

    // Inventory state
    private boolean kitSelectOpen = false;

    // Click tracking
    private ItemStack lastClickedStack = null;
    private int lastSlotClicked = -1;
    private boolean waitingForConfirmation = false;
    private int clickTick = -1;

    // Global tick counter
    private int totalTicks = 0;

    Pattern pattern = Pattern.compile("^§[a5](.+?)([§&]6 ✯)*$");

    // ------------------------------------------------------------------
    // Inventory open/close detection
    // ------------------------------------------------------------------

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        if (!SkyWarsToolsMod.config.kitSelectFix) return;
        if (event.gui instanceof GuiChest) {
            GuiChest chest = (GuiChest) event.gui;
            ContainerChest container = (ContainerChest) chest.inventorySlots;
            String title = container.getLowerChestInventory().getDisplayName().getUnformattedText();
            kitSelectOpen = TARGET_INVENTORY_TITLE.equals(title);
        } else {
            // GUI closed or switched to something else entirely
            kitSelectOpen = false;
            waitingForConfirmation = false;
        }
    }

    // ------------------------------------------------------------------
    // Click detection
    // ------------------------------------------------------------------

    @SubscribeEvent
    public void onMouseInput(GuiScreenEvent.MouseInputEvent.Pre event) {
        if (!SkyWarsToolsMod.config.kitSelectFix) return;
        if (!kitSelectOpen) return;
        if (!(event.gui instanceof GuiContainer)) return;
        if (!Mouse.getEventButtonState()) return; // only react on press, not release

        GuiContainer gui = (GuiContainer) event.gui;
        Slot slot = gui.getSlotUnderMouse();

        if (slot != null && slot.getHasStack()) {

            lastClickedStack = slot.getStack().copy();

            Matcher kitNameMatcher = pattern.matcher(lastClickedStack.getDisplayName());
            if (!kitNameMatcher.find()) {
                return;
            }
            lastSlotClicked = slot.slotNumber;
            waitingForConfirmation = true;
            clickTick = totalTicks;
        }
    }

    // ------------------------------------------------------------------
    // Chat confirmation handling
    // ------------------------------------------------------------------

    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {
        if (!SkyWarsToolsMod.config.kitSelectFix) return;
        if (!waitingForConfirmation) return;
        String msg = event.message.getFormattedText();
        Matcher matcher = SELECT_PATTERN.matcher(msg);
        // Not the confirmation message we're looking for; keep waiting
        // and let other chat messages pass through untouched.
        if (!matcher.matches()) return;

        String selectedName = matcher.group(1);

        waitingForConfirmation = false; // consumed, this is our real confirmation

        if (lastClickedStack == null) return;

        Matcher kitNameMatcher = pattern.matcher(lastClickedStack.getDisplayName());
//        ChatLib.chat(lastClickedStack.getDisplayName());
        if (!kitNameMatcher.find()) {
            return;
        }


        String kitName = kitNameMatcher.group(1); // e.g. "Pig Rider"
//        ChatLib.chat(kitName + " - " + selectedName);
        if (!namesMatch(kitName, selectedName)) {

            Minecraft.getMinecraft().thePlayer.playSound("mob.villager.no", 1F, 1F);
            ChatLib.chat("&cKit did not select correctly!");
//            reclick();
        }

    }

    private boolean namesMatch(String itemName, String selectedName) {
        return itemName.trim().equalsIgnoreCase(selectedName.trim());
    }

// ------------------------------------------------------------------
// Re-click logic
// ------------------------------------------------------------------

    private void reclick() {
        if (lastSlotClicked == -1) return;

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.thePlayer.openContainer == null) return;

        mc.playerController.windowClick(
                mc.thePlayer.openContainer.windowId,
                lastSlotClicked,
                0, // mouse button, 0 = left click
                0, // click type, 0 = normal click
                mc.thePlayer
        );

        // Re-arm confirmation waiting since we just clicked again
        waitingForConfirmation = true;
        clickTick = totalTicks;
    }

// ------------------------------------------------------------------
// Tick handler: inventory-closed check + timeout safety net
    // Might not be necessary, have to see
// ------------------------------------------------------------------

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (!SkyWarsToolsMod.config.kitSelectFix) return;
        if (event.phase != TickEvent.Phase.END) return;
        totalTicks++;

        Minecraft mc = Minecraft.getMinecraft();

        // Inventory closed unexpectedly
        if (kitSelectOpen && !(mc.currentScreen instanceof GuiChest)) {
            if (mc.thePlayer != null) {
                mc.thePlayer.addChatMessage(
                        new ChatComponentText(EnumChatFormatting.RED +
                                "Warning: Kit select inventory closed unexpectedly!"));
            }
            kitSelectOpen = false;
            waitingForConfirmation = false;
        }

        // Timeout safety net: no confirmation ever arrived
        if (waitingForConfirmation && (totalTicks - clickTick) > TIMEOUT_TICKS) {
            waitingForConfirmation = false;
            if (mc.thePlayer != null) {
                mc.thePlayer.addChatMessage(
                        new ChatComponentText(EnumChatFormatting.RED +
                                "Warning: no confirmation message received for click!"));
            }
        }
    }


}
