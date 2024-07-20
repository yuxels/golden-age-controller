package cat.kittens.mods.controller.input;

import cat.kittens.mods.controller.ControllerSupport;
import cat.kittens.mods.controller.lib.IGamepadDevice;
import cat.kittens.mods.controller.mixin.accessor.MinecraftAccessor;

import java.util.OptionalDouble;
import java.util.OptionalLong;
import java.util.Set;

public record ChordHelper(IGamepadDevice<?> controller, Set<IControllerMapping.ActionId> reversionQueue) {
    public boolean shouldRevert(IControllerMapping.ActionId id) {
        return reversionQueue.contains(id);
    }

    public boolean shouldPerform(IControllerMapping.Action action) {
        if (action == null)
            return false;
        var buttonChord = action.buttonChord();
        var axisChord = action.axisChord();
        boolean buttonChordActive = buttonChord.entrySet().stream()
                .allMatch(x -> controller.input().getPressDuration(x.getKey()).orElse(Long.MIN_VALUE) >= x.getValue());
        boolean axisChordActive = axisChord.entrySet().stream()
                .allMatch(x -> controller.input().getAxisValue(x.getKey()).orElse(Double.MIN_VALUE) >= x.getValue());
        return (axisChord.isEmpty() && buttonChordActive) ||
                (buttonChord.isEmpty() && axisChordActive) ||
                (buttonChordActive && axisChordActive);
    }

    public boolean shouldPerform(IControllerMapping.ActionId id) {
        return shouldPerform(ControllerSupport.support().mapping().getActions().get(id));
    }

    public OptionalDouble getAxisValue(IControllerMapping.Action action) {
        if (!shouldPerform(action))
            return OptionalDouble.empty();
        return action.axisChord().keySet().stream()
                .flatMapToDouble(b -> controller.input().getAxisValue(b).stream())
                .min();
    }

    public OptionalLong getButtonDuration(IControllerMapping.Action action) {
        if (!shouldPerform(action))
            return OptionalLong.empty();
        return OptionalLong.of(action.buttonChord().keySet().stream()
                .flatMapToLong(b -> controller.input().getPressDuration(b).stream())
                .min()
                .orElse(0));
    }

    public OptionalDouble getAxisValue(IControllerMapping.ActionId id) {
        return getAxisValue(ControllerSupport.support().mapping().getActions().get(id));
    }

    public OptionalLong getButtonDuration(IControllerMapping.ActionId id) {
        return getButtonDuration(ControllerSupport.support().mapping().getActions().get(id));
    }

    public boolean tryPerform(IControllerMapping.ActionId id, IControllerMapping.Action action) {
        if (shouldPerform(action)) {
            OptionalLong buttonDuration = getButtonDuration(action);
            OptionalDouble axisValue = getAxisValue(action);
            IControllerMapping.Context ctx = new IControllerMapping
                    .Context(MinecraftAccessor.instance(), buttonDuration.orElse(0), axisValue, shouldRevert(id));
            action.executor().perform(ctx);
            reversionQueue.add(id);
            return true;
        } else if (shouldRevert(id)) {
            IControllerMapping.Context ctx = new IControllerMapping
                    .Context(MinecraftAccessor.instance(), -1L, OptionalDouble.empty(), false);
            action.executor().perform(ctx);
            reversionQueue.remove(id);
        }
        return false;
    }

    public boolean performAllOffTick(IControllerMapping mapping) {
        boolean any = false;
        for (var action : mapping.getActions().entrySet()) {
            if (action.getValue().offTick() && tryPerform(action.getKey(), action.getValue()))
                any = true;
        }
        return any;
    }

    public boolean performAll(IControllerMapping mapping) {
        boolean any = false;
        for (var action : mapping.getActions().entrySet()) {
            if (!action.getValue().offTick() && tryPerform(action.getKey(), action.getValue()))
                any = true;
        }
        return any;
    }
}
