package cat.kittens.mods.controller.util

public object MathHelper {
    public fun lerp(delta: Float, start: Float, end: Float): Float {
        return start + delta * (end - start)
    }

    public fun clampedLerp(start: Float, end: Float, delta: Float): Float {
        return if (delta < 0.0f) {
            start
        } else {
            if (delta > 1.0f) end else lerp(delta, start, end)
        }
    }

    public fun getLerpProgress(value: Float, start: Float, end: Float): Float {
        return (value - start) / (end - start)
    }

    public fun clampedLerpFromProgress(
        lerpValue: Float, lerpStart: Float, lerpEnd: Float, start: Float,
        end: Float
    ): Float {
        return clampedLerp(start, end, getLerpProgress(lerpValue, lerpStart, lerpEnd))
    }
}
