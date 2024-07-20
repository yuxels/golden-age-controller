package cat.kittens.mods.controller.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;

public record ScrollableScreenRenderContext(Minecraft minecraft, int width, int height, Screen previousScreen) {
}
