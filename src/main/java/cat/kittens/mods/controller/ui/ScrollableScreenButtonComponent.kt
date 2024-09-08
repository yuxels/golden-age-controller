package cat.kittens.mods.controller.ui;

import cat.kittens.mods.controller.mixin.accessor.MinecraftAccessor;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.apache.logging.log4j.util.TriConsumer;

public class ScrollableScreenButtonComponent implements ScrollableScreenComponent {
    private String message;
    private int textColor;
    private int width, height;
    private TriConsumer<ScrollableScreenRenderContext, Integer, Integer> onClick;
    private ButtonWidget widget;

    public ScrollableScreenButtonComponent() {
        this.message = "Hello, world!";
        this.textColor = 14737632;
        this.onClick = (ctx, mouseX, mouseY) -> {};
        dimensions(300, 20);
    }

    public ScrollableScreenButtonComponent dimensions(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    public ScrollableScreenButtonComponent fitDimensions(int height) {
        this.width = MinecraftAccessor.instance().textRenderer.getWidth(message) + 20;
        this.height = height;
        return this;
    }

    public ScrollableScreenButtonComponent message(String message) {
        this.message = message;
        return this;
    }

    public ScrollableScreenButtonComponent scale(float scale) {
        return this;
    }

    public int textColor() {
        return textColor;
    }

    public ScrollableScreenButtonComponent textColor(int textColor) {
        this.textColor = textColor;
        return this;
    }

    public ScrollableScreenButtonComponent onClick(TriConsumer<ScrollableScreenRenderContext, Integer, Integer> onClick) {
        this.onClick = onClick;
        return this;
    }

    private ScrollableScreenRenderContext ctx;;

    @Override
    public void init(
            ScrollableScreen screen, ScrollableScreenRenderContext ctx,
            int x, int y, int x2, int y2
    ) {
        this.ctx = ctx;
        this.widget = new ButtonWidget(10000, x, y, width, height, message);
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        widget.render(ctx.minecraft(), mouseX, mouseY);
    }

    @Override
    public boolean isMouseOver(int mouseX, int mouseY) {
        if (widget == null)
            return false;
        return widget.isMouseOver(MinecraftAccessor.instance(), mouseX, mouseY);
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
        return width;
    }

    @Override
    public int height(ScrollableScreenRenderContext ctx) {
        return height;
    }
}
