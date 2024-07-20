package cat.kittens.mods.controller.util;

public class MathHelper {
    public static float lerp(float delta, float start, float end) {
        return start + delta * (end - start);
    }

    public static float clampedLerp(float start, float end, float delta) {
        if (delta < 0.0F) {
            return start;
        } else {
            return delta > 1.0F ? end : lerp(delta, start, end);
        }
    }

    public static float getLerpProgress(float value, float start, float end) {
        return (value - start) / (end - start);
    }

    public static float clampedLerpFromProgress(float lerpValue, float lerpStart, float lerpEnd, float start,
                                                float end) {
        return clampedLerp(start, end, getLerpProgress(lerpValue, lerpStart, lerpEnd));
    }
}
