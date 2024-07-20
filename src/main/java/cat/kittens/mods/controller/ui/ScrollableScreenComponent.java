package cat.kittens.mods.controller.ui;

import net.minecraft.client.render.Tessellator;
import org.lwjgl.opengl.GL11;

public interface ScrollableScreenComponent {
    void init(
            ScrollableScreen screen, ScrollableScreenRenderContext ctx,
            int x, int y, int x2, int y2
    );

    default boolean bypassMouseOver(int mouseX, int mouseY) {
        return false;
    }

    default void fillGradient(int startX, int startY, int endX, int endY, int colorStart, int colorEnd) {
        float var7 = (float)(colorStart >> 24 & 255) / 255.0F;
        float var8 = (float)(colorStart >> 16 & 255) / 255.0F;
        float var9 = (float)(colorStart >> 8 & 255) / 255.0F;
        float var10 = (float)(colorStart & 255) / 255.0F;
        float var11 = (float)(colorEnd >> 24 & 255) / 255.0F;
        float var12 = (float)(colorEnd >> 16 & 255) / 255.0F;
        float var13 = (float)(colorEnd >> 8 & 255) / 255.0F;
        float var14 = (float)(colorEnd & 255) / 255.0F;
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glDisable(3008);
        GL11.glBlendFunc(770, 771);
        GL11.glShadeModel(7425);
        Tessellator var15 = Tessellator.INSTANCE;
        var15.startQuads();
        var15.color(var8, var9, var10, var7);
        var15.vertex(endX, startY, 0.0);
        var15.vertex(startX, startY, 0.0);
        var15.color(var12, var13, var14, var11);
        var15.vertex(startX, endY, 0.0);
        var15.vertex(endX, endY, 0.0);
        var15.draw();
        GL11.glShadeModel(7424);
        GL11.glDisable(3042);
        GL11.glEnable(3008);
        GL11.glEnable(3553);
    }

    default void doRender(int mouseX, int mouseY, float delta) {
        GL11.glPushMatrix();
        render(mouseX, mouseY, delta);
        GL11.glPopMatrix();
    }

    void render(int mouseX, int mouseY, float delta);

    int width(ScrollableScreenRenderContext ctx);

    int height(ScrollableScreenRenderContext ctx);

    default boolean handleClick(ScrollableScreen screen, ScrollableScreenRenderContext ctx, int x, int y, int button) {
        return false;
    }

    default boolean handleScroll(ScrollableScreen screen, ScrollableScreenRenderContext ctx, int x, int y, int amount) {
        return false;
    }

    boolean isMouseOver(int mouseX, int mouseY);

    static ScrollableScreenTextComponent text() {
        return new ScrollableScreenTextComponent();
    }

    static ScrollableScreenButtonComponent button() {
        return new ScrollableScreenButtonComponent();
    }

    static ScrollableScreenComponent gap() {
        return new ScrollableScreenGapComponent();
    }

    static ScrollableScreenDropdownComponent dropdown() {
        return new ScrollableScreenDropdownComponent();
    }
}
