package cat.kittens.mods.controller.input;

import cat.kittens.mods.controller.mixin.accessor.MinecraftAccessor;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;

public enum MappingActionContext {
    IN_GAME,
    INVENTORY,
    CONTAINER,
    MAIN_MENU,
    PAUSE,
    CHAT,
    UNKNOWN;

    public static ImmutableList<MappingActionContext> listOf(MappingActionContext... ctx) {
        return ImmutableList.copyOf(ctx);
    }

    public static MappingActionContext current() {
        var screen = MinecraftAccessor.instance() != null ? MinecraftAccessor.instance().currentScreen : null;
        if (screen == null)
            return IN_GAME;
        if (screen instanceof InventoryScreen)
            return INVENTORY;
        if (screen instanceof HandledScreen)
            return CONTAINER;
        if (screen instanceof GameMenuScreen)
            return PAUSE;
        if (screen instanceof ChatScreen)
            return CHAT;
        if (screen instanceof TitleScreen)
            return MAIN_MENU;
        return UNKNOWN;
    }
}
