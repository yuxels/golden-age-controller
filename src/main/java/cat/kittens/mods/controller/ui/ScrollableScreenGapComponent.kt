package cat.kittens.mods.controller.ui;

public class ScrollableScreenGapComponent implements ScrollableScreenComponent {
    public ScrollableScreenGapComponent() {
    }

    @Override
    public void init(
            ScrollableScreen screen, ScrollableScreenRenderContext ctx,
            int x, int y, int x2, int y2
    ) {
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
    }

    @Override
    public boolean isMouseOver(int mouseX, int mouseY) {
        return false;
    }

    @Override
    public boolean handleClick(ScrollableScreen screen, ScrollableScreenRenderContext ctx, int x, int y, int button) {
        return false;
    }

    @Override
    public int width(ScrollableScreenRenderContext ctx) {
        return 16;
    }

    @Override
    public int height(ScrollableScreenRenderContext ctx) {
        return 16;
    }
}
