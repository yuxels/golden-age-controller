package cat.kittens.mods.controller.util;

import cat.kittens.mods.controller.mixin.accessor.MinecraftAccessor;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

public class GLUtils {
    public static void scissor(int x, int x2, int y, int y2, Runnable callback) {
        Minecraft mc = MinecraftAccessor.instance();
        var factor = ScaledResolution.create(mc.options, mc.displayWidth, mc.displayHeight);
        GL11.glScissor(
                x * factor.guiScale(), (int) ((factor.guiScaledHeight() - y2) * factor.guiScale()),
                (x2 - x) * factor.guiScale(), (y2 - y) * factor.guiScale()
        );
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        callback.run();
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }
}
