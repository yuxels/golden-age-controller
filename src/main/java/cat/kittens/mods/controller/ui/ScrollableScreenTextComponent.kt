package cat.kittens.mods.controller.ui;

import cat.kittens.mods.controller.mixin.accessor.MinecraftAccessor;
import net.minecraft.client.font.TextRenderer;
import org.apache.logging.log4j.util.TriConsumer;
import org.lwjgl.opengl.GL11;

public class ScrollableScreenTextComponent implements ScrollableScreenComponent {
    private String message;
    private float scale;
    private ScrollableScreenLayout layout;
    private int backgroundColor;
    private int textColor;
    private int hoverColor;
    private TriConsumer<ScrollableScreenRenderContext, Integer, Integer> onClick;

    public static int FONT_SIZE = 7;

    public ScrollableScreenTextComponent() {
        this.message = "Hello, world!";
        this.scale = 1.f;
        this.layout = ScrollableScreenLayout.create();
        this.backgroundColor = -1073741824;
        this.textColor = 14737632;
        this.hoverColor = 16777120;
        this.onClick = (ctx, mouseX, mouseY) -> {};
    }

    public ScrollableScreenLayout layout() {
        return layout;
    }

    public ScrollableScreenTextComponent layout(ScrollableScreenLayout layout) {
        this.layout = layout;
        return this;
    }

    public String message() {
        return message;
    }

    public ScrollableScreenTextComponent message(String message) {
        this.message = message;
        return this;
    }

    public float scale() {
        return scale;
    }

    public ScrollableScreenTextComponent scale(float scale) {
        this.scale = scale;
        return this;
    }

    public int backgroundColor() {
        return backgroundColor;
    }

    public ScrollableScreenTextComponent backgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public int textColor() {
        return textColor;
    }

    public ScrollableScreenTextComponent textColor(int textColor) {
        this.textColor = textColor;
        return this;
    }

    public ScrollableScreenTextComponent onClick(TriConsumer<ScrollableScreenRenderContext, Integer, Integer> onClick) {
        this.onClick = onClick;
        return this;
    }

    public int hoverColor() {
        return hoverColor;
    }

    public ScrollableScreenTextComponent hoverColor(int hoverColor) {
        this.hoverColor = hoverColor;
        return this;
    }

    private ScrollableScreenRenderContext ctx;
    private int x, y;

    @Override
    public void init(
            ScrollableScreen screen, ScrollableScreenRenderContext ctx,
            int x, int y, int x2, int y2
    ) {
        this.ctx = ctx;
        this.x = x;
        this.y = y;
    }

    private TextRenderer renderer() {
        return MinecraftAccessor.instance().textRenderer;
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        int width = width(ctx), height = height(ctx);
        if (backgroundColor != -1) {
            fillGradient(x, y, x + width, y + height, backgroundColor, backgroundColor);
        }
        int color = isMouseOver(mouseX, mouseY) ? hoverColor : textColor;
        GL11.glPushMatrix();
        GL11.glTranslatef(0, 0, 300);
        renderer().drawWithShadow(message, x + layout.leftPadding(ctx), y + layout.topPadding(ctx), color);
        GL11.glPopMatrix();
    }

    @Override
    public boolean isMouseOver(int mouseX, int mouseY) {
        return mouseX >= x && mouseY >= y && mouseX <= x + width(ctx) && mouseY <= y + height(ctx);
    }

    @Override
    public boolean handleClick(ScrollableScreen screen, ScrollableScreenRenderContext ctx, int x, int y, int button) {
        if (button == 0) {
            onClick.accept(ctx, x, y);
            return true;
        }
        return false;
    }

    @Override
    public int width(ScrollableScreenRenderContext ctx) {
        return (int) Math.ceil((renderer().getWidth(message) * scale) + layout.leftPadding(ctx) + layout.rightPadding(ctx));
    }

    @Override
    public int height(ScrollableScreenRenderContext ctx) {
        return (int) Math.ceil((FONT_SIZE * scale) + layout.topPadding(ctx) + layout.bottomPadding(ctx));
    }
}
