package cat.kittens.mods.controller.ui;

public interface ScrollableScreenLayout {
    int topPadding(ScrollableScreenRenderContext ctx);

    int leftPadding(ScrollableScreenRenderContext ctx);

    int bottomPadding(ScrollableScreenRenderContext ctx);

    int rightPadding(ScrollableScreenRenderContext ctx);

    record Static(
            int top, int left, int bottom, int right
    ) implements ScrollableScreenLayout {

        public Static withXPadding(int pad) {
            return new Static(top, pad, bottom, pad);
        }

        public Static withYPadding(int pad) {
            return new Static(pad, left, pad, right);
        }

        @Override
        public int topPadding(ScrollableScreenRenderContext ctx) {
            return top;
        }

        @Override
        public int leftPadding(ScrollableScreenRenderContext ctx) {
            return left;
        }

        @Override
        public int bottomPadding(ScrollableScreenRenderContext ctx) {
            return bottom;
        }

        @Override
        public int rightPadding(ScrollableScreenRenderContext ctx) {
            return right;
        }
    }

    static Static create() {
        return new Static(0, 0, 0, 0);
    }

    static Static padding(int padding) {
        return new Static(padding, padding, padding, padding);
    }

    static Static padding(int top, int left, int bottom, int right) {
        return new Static(top, left, bottom, right);
    }
}