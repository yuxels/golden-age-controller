package cat.kittens.mods.controller.ui;

import cat.kittens.mods.controller.mixin.accessor.MinecraftAccessor;
import net.minecraft.client.font.TextRenderer;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;
import java.util.function.BiConsumer;

public class ScrollableScreenDropdownComponent implements ScrollableScreenComponent {
    private String selectedValue;
    private float scale;
    private ScrollableScreenLayout layout;
    private Color backgroundColor;
    private Color textColor;
    private Color hoverColor;
    private Color outlineColor;
    private List<String> options;
    private BiConsumer<ScrollableScreenRenderContext, String> onSelect;
    public boolean isExpanded;

    private static int FONT_SIZE = 7;
    private static int STROKE_SIZE = 1;

    @Override
    public boolean bypassMouseOver(int mouseX, int mouseY) {
        return true;
    }

    public ScrollableScreenDropdownComponent() {
        this.selectedValue = "Hello, world!";
        this.scale = 1.f;
        this.layout = ScrollableScreenLayout.padding(5, 10, 5, 10);
        this.backgroundColor = new Color(75, 75, 75, 128);
        this.outlineColor = new Color(75, 75, 75);
        this.textColor = new Color(224, 224, 224);
        this.hoverColor = new Color(255, 255, 160);
        this.onSelect = (ctx, option) -> {
        };
        this.isExpanded = false;
    }

    public List<String> options() {
        return options;
    }

    public ScrollableScreenDropdownComponent options(List<String> options) {
        this.options = options;
        return this;
    }

    public String selectedValue() {
        return selectedValue;
    }

    public ScrollableScreenDropdownComponent selectedValue(String defaultValue) {
        this.selectedValue = defaultValue;
        return this;
    }

    public ScrollableScreenLayout layout() {
        return layout;
    }

    public ScrollableScreenDropdownComponent layout(ScrollableScreenLayout layout) {
        this.layout = layout;
        return this;
    }

    public String message() {
        return selectedValue;
    }

    public ScrollableScreenDropdownComponent message(String message) {
        this.selectedValue = message;
        return this;
    }

    public float scale() {
        return scale;
    }

    public ScrollableScreenDropdownComponent scale(float scale) {
        this.scale = scale;
        return this;
    }

    public Color backgroundColor() {
        return backgroundColor;
    }

    public ScrollableScreenDropdownComponent backgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public Color textColor() {
        return textColor;
    }

    public ScrollableScreenDropdownComponent textColor(Color textColor) {
        this.textColor = textColor;
        return this;
    }

    public ScrollableScreenDropdownComponent onSelect(BiConsumer<ScrollableScreenRenderContext, String> onSelect) {
        this.onSelect = onSelect;
        return this;
    }

    public Color hoverColor() {
        return hoverColor;
    }

    public ScrollableScreenDropdownComponent hoverColor(Color hoverColor) {
        this.hoverColor = hoverColor;
        return this;
    }

    public Color outlineColor() {
        return outlineColor;
    }

    public ScrollableScreenDropdownComponent outlineColor(Color outlineColor) {
        this.outlineColor = outlineColor;
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
        int width = width(ctx) - (STROKE_SIZE * 2), height = baseHeight(ctx) - (STROKE_SIZE * 2);
        var outlineColor = (isMouseOverDropdownOnly(mouseX, mouseY) ? this.hoverColor : this.outlineColor).getRGB();
        var textColor = (isMouseOverDropdownOnly(mouseX, mouseY) ? this.hoverColor : this.textColor).getRGB();
        if (backgroundColor != null) {
            fillGradient(x, y, x + width, y + STROKE_SIZE, outlineColor, outlineColor);
            fillGradient(x, y + height - STROKE_SIZE, x + width, y + height, outlineColor, outlineColor);
            fillGradient(x, y + STROKE_SIZE, x + STROKE_SIZE, y + height - STROKE_SIZE, outlineColor, outlineColor);
            fillGradient(x + width - STROKE_SIZE, y + STROKE_SIZE, x + width, y + height - STROKE_SIZE, outlineColor, outlineColor);
            fillGradient(x + STROKE_SIZE, y + STROKE_SIZE, x + width - STROKE_SIZE, y + height - STROKE_SIZE, backgroundColor.getRGB(), backgroundColor.getRGB());
        }
        renderer().drawWithShadow(
                selectedValue + " ⌄", x + layout.leftPadding(ctx), y + layout.topPadding(ctx), textColor
        );
        if (isExpanded()) {
            int y2 = y + height + (STROKE_SIZE * 2);
            int x2 = x;
            for (var option : options) {
                if (backgroundColor != null) {
                    fillGradient(x2, y2, x + width, y2 + height, backgroundColor.getRGB(), backgroundColor.getRGB());
                }
                GL11.glPushMatrix();
                GL11.glTranslatef(0, 0, 300);
                renderer().drawWithShadow(option, x2 + layout.leftPadding(ctx), y2 + layout.topPadding(ctx), textColor);
                GL11.glPopMatrix();
                y2 += height;
            }
        }
    }

    public boolean isExpanded() {
        return this.isExpanded;
    }

    public boolean isMouseOverDropdownOnly(int mouseX, int mouseY) {
        return mouseX >= x && mouseY >= y && mouseX <= x + width(ctx) && mouseY <= y + baseHeight(ctx);
    }

    @Override
    public boolean isMouseOver(int mouseX, int mouseY) {
        return mouseX >= x && mouseY >= y && mouseX <= x + width(ctx) && mouseY <= y + height(ctx);
    }

    @Override
    public boolean handleClick(ScrollableScreen screen, ScrollableScreenRenderContext ctx, int x, int y, int button) {
        if (button != 0) {
            return false;
        }
        if (isMouseOverDropdownOnly(x, y)) {
            this.isExpanded = !isExpanded;
            return true;
        } else if (isExpanded && isMouseOver(x, y)) {
            int baseWidth = width(ctx), baseHeight = baseHeight(ctx) - (STROKE_SIZE * 2);
            int baseX = this.x, baseY = this.y + baseHeight + (STROKE_SIZE * 2);
            for (var option : options) {
                boolean isMouseOver =
                        x >= baseX && y >= baseY && x <= baseX + baseWidth && y <= baseY + baseHeight;
                if (isMouseOver) {
                    isExpanded = false;
                    selectedValue = option;
                    onSelect.accept(ctx, option);
                    return true;
                }
                baseY += baseHeight;
            }
        }
        return false;
    }

    @Override
    public int width(ScrollableScreenRenderContext ctx) {
        int baseWidth = Math.max(renderer().getWidth(selectedValue), options.stream().mapToInt(renderer()::getWidth)
                .max().orElse(0));
        return (STROKE_SIZE * 2) + (int) Math.ceil((baseWidth * scale) + layout.leftPadding(ctx)
                + layout.rightPadding(ctx));
    }

    public int baseHeight(ScrollableScreenRenderContext ctx) {
        return (STROKE_SIZE * 2) + (int) Math.ceil((FONT_SIZE * scale) + layout.topPadding(ctx) + layout.bottomPadding(ctx));
    }

    @Override
    public int height(ScrollableScreenRenderContext ctx) {
        int height = baseHeight(ctx);
        if (isExpanded) {
            for (var ignored : options) {
                height += baseHeight(ctx) - (STROKE_SIZE * 2);
            }
        }
        return height;
    }
}
