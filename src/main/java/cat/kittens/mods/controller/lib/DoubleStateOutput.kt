package cat.kittens.mods.controller.lib

public data class DoubleStateOutput<T>(var current: T? = null, var previous: T? = null) {
    public fun set(value: T?) {
        this.previous = current
        this.current = value
    }
}