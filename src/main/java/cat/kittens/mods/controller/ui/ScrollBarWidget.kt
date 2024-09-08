package cat.kittens.mods.controller.ui;

import net.minecraft.client.render.Tessellator;
import org.lwjgl.opengl.GL11;

public class ScrollBarWidget {
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final int pageHeight;
    private float targetScrollPercent;
    private float scrollPercent;
    private final int top;
    private final boolean render;

    private static final float SMOOTH_SCROLLING_FACTOR = 0.1f;

    public ScrollBarWidget(int x, int y, int width, int height, int pageHeight, int top) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.pageHeight = pageHeight;
        this.scrollPercent = 0;
        this.targetScrollPercent = 0;
        this.top = top;
        this.render = pageHeight > height;
    }

    public void drawRectangle(Tessellator t, int x, int y, int width, int height) {
        t.vertex(x, y, 0.0);
        t.vertex(x, (y + height), 0.0);
        t.vertex((x + width), (y + height), 0.0);
        t.vertex((x + width), y, 0.0);
    }

    public int gripY() {
        float availableTrackY = height - gripHeight();
        return (int) Math.ceil(top + (availableTrackY * scrollPercent));
    }

    public int gripHeight() {
        int scrollableHeight = Math.max(pageHeight - height, 0);
        int idealGripHeight = (int) Math.ceil((double) height * (height / (float) scrollableHeight));
        return Math.min(idealGripHeight, height);
    }

    public float progress() {
        return render ? scrollPercent : 0.f;
    }

    public void render(float delta) {
        if (!render)
            return;
        update(delta);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        Tessellator tessellator = Tessellator.INSTANCE;
        tessellator.startQuads();
        tessellator.color(8421504, 255);
        drawRectangle(tessellator, x, y, width, height);
        tessellator.color(12632256, 255);
        drawRectangle(tessellator, x, gripY(), width, gripHeight());
        tessellator.draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    private void update(float delta) {
        scrollPercent += (targetScrollPercent - scrollPercent) * SMOOTH_SCROLLING_FACTOR * delta;
        scrollPercent = Math.min(1.0f, Math.max(0.0f, scrollPercent));
    }

    public boolean isMouseOver(int mouseX, int mouseY) {
        return mouseX >= x && mouseX < (x + width) && mouseY >= y && mouseY <= (y + height);
    }

    public void onClick(int mouseX, int mouseY, int button) {
        if (!render || button != 0 || !isMouseOver(mouseX, mouseY))
            return;
        float y = Math.max(0, mouseY - this.y);
        float ratio = y / height;
        this.targetScrollPercent = (float) Math.max(0, Math.min(1.0, ratio));
    }

    public void onScroll(int amount) {
        this.targetScrollPercent = (float) Math.max(0, Math.min(1.0, targetScrollPercent + (double) amount / 8));
    }
}
