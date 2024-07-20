package cat.kittens.mods.controller.ui;

import cat.kittens.mods.controller.mixin.accessor.MinecraftAccessor;
import net.minecraft.client.gui.screen.Screen;
import org.lwjgl.input.Mouse;

import java.util.function.Supplier;

public class ScrollableScreen extends Screen {
    private final ScrollableScreenLayout layout;

    private ScrollableScreenComponentColumn left;
    private ScrollableScreenComponentColumn right;
    private Supplier<ScrollableScreenComponentColumn> leftSupplier;
    private Supplier<ScrollableScreenComponentColumn> rightSupplier;
    private ScrollableScreenComponent title;
    private ScrollableScreenRenderContext ctx;
    private Screen lastScreen;

    public ScrollableScreen(
            ScrollableScreenLayout layout,
            String title,
            Supplier<ScrollableScreenComponentColumn> left,
            Supplier<ScrollableScreenComponentColumn> right
    ) {
        this.layout = layout;
        this.leftSupplier = left;
        this.rightSupplier = right;
        this.title = ScrollableScreenComponent.text().message(title);
        this.lastScreen = MinecraftAccessor.instance().currentScreen;
    }

    @Override
    public void init() {
        left = leftSupplier.get();
        right = rightSupplier.get();
        ctx = new ScrollableScreenRenderContext(minecraft, width, height, lastScreen);
        int titleX = (width / 2) - (title.width(ctx) / 2), titleY = layout.topPadding(ctx);
        int titleX2 = width / 2 + (title.width(ctx) / 2), titleY2 = titleY + title.height(ctx);
        title.init(this, ctx, titleX, titleY, titleX2, titleY2);
        int leftX = layout.leftPadding(ctx), leftY = titleY + (2 * layout.topPadding(ctx));
        int leftX2 = leftX + left.width(ctx), leftY2 = height - layout.bottomPadding(ctx);
        if (right != null) {
            int rightX = leftX2 + layout.leftPadding(ctx) + left.scrollBarWidth(), rightY = leftY;
            int rightX2 = width - layout.rightPadding(ctx) - right.scrollBarWidth(), rightY2 = leftY2;
            right.init(this, ctx, rightX, rightY, rightX2, rightY2);
        } else {
            leftX2 = width - leftX;
        }
        left.init(this, ctx, leftX, leftY, leftX2, leftY2);
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        renderBackground();
        title.doRender(mouseX, mouseY, delta);
        left.doRender(mouseX, mouseY, delta);
        if (right != null)
            right.doRender(mouseX, mouseY, delta);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        left.handleClick(this, ctx, mouseX, mouseY, button);
        if (right != null)
            right.handleClick(this, ctx, mouseX, mouseY, button);
    }

    public void handleScroll(int x, int y) {
        int wheel = Mouse.getEventDWheel();
        if (wheel == 0)
            return;
        int amount = (int) -Math.signum(wheel);
        left.handleScroll(this, ctx, x, y, amount);
        if (right != null)
            right.handleScroll(this, ctx, x, y, amount);
    }

    public ScrollableScreen left(ScrollableScreenComponentColumn left) {
        this.left = left;
        init();
        return this;
    }

    public ScrollableScreen right(ScrollableScreenComponentColumn right) {
        this.right = right;
        init();
        return this;
    }

    public ScrollableScreen leftSupplier(Supplier<ScrollableScreenComponentColumn> leftSupplier) {
        this.leftSupplier = leftSupplier;
        init();
        return this;
    }

    public ScrollableScreen rightSupplier(Supplier<ScrollableScreenComponentColumn> rightSupplier) {
        this.rightSupplier = rightSupplier;
        init();
        return this;
    }
}
