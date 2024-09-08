package cat.kittens.mods.controller.input

import cat.kittens.mods.controller.mixin.accessor.MinecraftAccessor
import net.minecraft.client.gui.screen.ChatScreen
import net.minecraft.client.gui.screen.GameMenuScreen
import net.minecraft.client.gui.screen.TitleScreen
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.gui.screen.ingame.InventoryScreen

public enum class MappingActionContext {
    InGame,
    Inventory,
    Container,
    MainMenu,
    Pause,
    Chat,
    Unknown;

    public companion object {
        public val current: MappingActionContext
            get() =
                when (if (MinecraftAccessor.instance() != null) MinecraftAccessor.instance().currentScreen else null) {
                    null -> InGame
                    is InventoryScreen -> Inventory
                    is HandledScreen -> Container
                    is GameMenuScreen -> Pause
                    is ChatScreen -> Chat
                    is TitleScreen -> MainMenu
                    else -> Unknown
                }
    }
}
