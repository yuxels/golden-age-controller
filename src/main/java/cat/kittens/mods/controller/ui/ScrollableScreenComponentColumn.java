package cat.kittens.mods.controller.ui;

import cat.kittens.mods.controller.util.GLUtils;

import java.awt.*;
import java.util.List;

public class ScrollableScreenComponentColumn implements ScrollableScreenComponent {
    private final List<ScrollableScreenComponent> top;
    private final List<ScrollableScreenComponent> bottom;
    private final ScrollableScreenLayout layout;
    private final int itemSpacing;

    private ScrollableScreen screen;
    private ScrollableScreenRenderContext ctx;
    private int x;
    private int y;
    private int x2;
    private int y2;
    private ScrollBarWidget scrollBar;
    private final boolean hasBackground;

    public static final Color BG_COLOR = new Color(0f, 0f, 0f, 0.5f);

    public ScrollableScreenComponentColumn(
            List<ScrollableScreenComponent> top,
            List<ScrollableScreenComponent> bottom,
            ScrollableScreenLayout layout,
            int itemSpacing,
            boolean hasBackground
    ) {
        this.top = top;
        this.bottom = bottom;
        this.layout = layout;
        this.itemSpacing = itemSpacing;
        this.hasBackground = hasBackground;
    }

    @Override
    public void init(
            ScrollableScreen screen, ScrollableScreenRenderContext ctx,
            int x, int y, int x2, int y2
    ) {
        this.screen = screen;
        this.ctx = ctx;
        this.x = x;
        this.y = y;
        this.x2 = x2;
        this.y2 = y2;
        int top2 = this.y;
        int bottom2 = screen.height - top2;
        this.scrollBar = new ScrollBarWidget(
                this.x2, top2, scrollBarWidth(), bottom2 - top2, height(ctx),
                top2
        );
        recalculate();
    }

    public void recalculate() {
        int minY = y, maxY = minY + height(ctx);
        int currentX = x, currentY = (int) Math.ceil(y + (scrollBar.progress() * (minY - maxY)));
        for (var component : this.top) {
            int height = component.height(ctx);
            component.init(
                    screen, ctx, currentX + layout.leftPadding(ctx),
                    currentY + layout.topPadding(ctx), x2 - layout.leftPadding(ctx), currentY + height
            );
            currentY += height + itemSpacing;
        }
        currentY += computeDistanceBetweenSections(
                ctx, currentY - y - itemSpacing, getHeight(bottom, ctx)
        );
        for (var component : this.bottom) {
            int height = component.height(ctx);
            component.init(
                    screen, ctx, currentX + layout.leftPadding(ctx), currentY + layout.topPadding(ctx),
                    x2 - layout.rightPadding(ctx), currentY + height
            );
            currentY += height + itemSpacing;
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        recalculate();
        if (hasBackground) {
            fillGradient(x, y, x2, y2, BG_COLOR.getRGB(), BG_COLOR.getRGB());
        }
        GLUtils.scissor(
                x + layout.leftPadding(ctx), x2 - layout.rightPadding(ctx),
                y + layout.topPadding(ctx), y2 - layout.bottomPadding(ctx), () -> {
            for (var component : this.top) {
                component.render(mouseX, mouseY, delta);
            }
            for (var component : this.bottom) {
                component.render(mouseX, mouseY, delta);
            }
        });
        scrollBar.render(delta);
    }

    public int scrollBarWidth() {
        return 6;
    }

    public boolean isMouseOver(int mouseX, int mouseY) {
        return (mouseX >= x && mouseX < x2 && mouseY >= y && mouseY <= y2) || scrollBar.isMouseOver(mouseX, mouseY);
    }

    private int computeDistanceBetweenSections(ScrollableScreenRenderContext ctx, int topHeight, int bottomHeight) {
        int overallHeight = y2 - y - layout.topPadding(ctx) - layout.bottomPadding(ctx);
        return Math.max(itemSpacing, overallHeight - Math.abs(topHeight - bottomHeight));
    }

    private int getHeight(List<ScrollableScreenComponent> components, ScrollableScreenRenderContext ctx) {
        int totalHeight = 0;
        for (ScrollableScreenComponent component : components) {
            totalHeight += component.height(ctx);
        }
        return totalHeight + (itemSpacing * (components.size() - 1));
    }

    @Override
    public int width(ScrollableScreenRenderContext ctx) {
        int max = 0;
        for (var component : top) {
            if (component.width(ctx) > max) max = component.width(ctx);
        }
        for (var component : bottom) {
            if (component.width(ctx) > max) max = component.width(ctx);
        }
        return max + layout.leftPadding(ctx) + layout.rightPadding(ctx);
    }

    @Override
    public int height(ScrollableScreenRenderContext ctx) {
        int top = getHeight(this.top, ctx), bottom = getHeight(this.bottom, ctx);
        return top + bottom + computeDistanceBetweenSections(ctx, top, bottom);
    }

    @Override
    public boolean handleClick(ScrollableScreen screen, ScrollableScreenRenderContext ctx, int x, int y, int button) {
        for (var c : top) {
            if (!c.bypassMouseOver(x, y) && !c.isMouseOver(x, y))
                continue;
            if(c.handleClick(screen, ctx, x, y, button))
                return true;
        }
        for (var c : bottom) {
            if (!c.bypassMouseOver(x, y) && !c.isMouseOver(x, y))
                continue;
            if(c.handleClick(screen, ctx, x, y, button))
                return true;
        }
        if (isMouseOver(x, y)) {
            scrollBar.onClick(x, y, button);
            return true;
        }
        return false;
    }

    @Override
    public boolean handleScroll(ScrollableScreen screen, ScrollableScreenRenderContext ctx, int x, int y, int amount) {
        for (var c : top) {
            if (!c.isMouseOver(x, y))
                continue;
            if (c.handleScroll(screen, ctx, x, y, amount))
                return true;
        }
        for (var c : bottom) {
            if (!c.isMouseOver(x, y))
                continue;
            if (c.handleScroll(screen, ctx, x, y, amount))
                return true;
        }
        if (isMouseOver(x, y)) {
            scrollBar.onScroll(amount);
            return true;
        }
        return false;
    }
}
