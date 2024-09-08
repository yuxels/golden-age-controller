package cat.kittens.mods.controller.input;

import cat.kittens.mods.controller.lib.IGamepadDevice;
import cat.kittens.mods.controller.lib.IGamepadDeviceId;
import com.google.common.collect.ImmutableList;

import java.util.Optional;
import java.util.function.Function;

public interface ControllerMapping {
    static ControllerMapping create(
            MappingAction action,
            IGamepadDevice.Input.Button... buttons
    ) {
        return create(action, ImmutableList.copyOf(buttons), ImmutableList.of());
    }

    static ControllerMapping create(
            MappingAction action,
            IGamepadDevice.Input.Axis... axes
    ) {
        return create(action, ImmutableList.of(), ImmutableList.copyOf(axes));
    }

    static Function<IGamepadDevice<IGamepadDeviceId>, Optional<MappingExecutionContext>> createContextSupplier(
            ImmutableList<IGamepadDevice.Input.Button> buttons,
            ImmutableList<IGamepadDevice.Input.Axis> axes
    ) {
        return (gamepad) -> {
            if (buttons.isEmpty() && axes.isEmpty())
                return Optional.empty();
            boolean buttonChordActive = buttons.stream()
                    .allMatch(x -> gamepad.input().getPressState(x).current().orElse(false));
            boolean axisChordActive = axes.stream()
                    .allMatch(x -> gamepad.input().getAxisValue(x).current().orElse(Float.MIN_VALUE) > 0);
            if (buttonChordActive && axisChordActive) {
                var held = true;
                var value = 1.0;
                for (var button : buttons) {
                    var v = gamepad.input().getPressState(button);
                    if (!v.previous().orElse(false))
                        held = false;
                }
                for (var axis : axes) {
                    var v = gamepad.input().getAxisValue(axis);
                    if (v.previous().orElse(0f) == 0f)
                        held = false;
                    var curr = gamepad.input().getAxisValue(axis).current().orElse(1f);
                    if (value > curr)
                        value = curr;
                }
                return Optional.of(new MappingExecutionContext(value, held));
            }
            return Optional.empty();
        };
    }

    static ControllerMapping create(
            MappingAction action,
            ImmutableList<IGamepadDevice.Input.Button> buttons,
            ImmutableList<IGamepadDevice.Input.Axis> axes
    ) {
        var supplier = createContextSupplier(buttons, axes);
        return new ControllerMapping() {
            @Override
            public MappingAction action() {
                return action;
            }

            @Override
            public Optional<MappingExecutionContext> getContextFor(IGamepadDevice<IGamepadDeviceId> gamepad) {
                return supplier.apply(gamepad);
            }
        };
    }

    MappingAction action();

    Optional<MappingExecutionContext> getContextFor(IGamepadDevice<IGamepadDeviceId> gamepad);

    default ControllerMapping withContextSupplier(
            Function<IGamepadDevice<IGamepadDeviceId>, Optional<MappingExecutionContext>> supplier
    ) {
        var outer = this;
        return new ControllerMapping() {
            @Override
            public MappingAction action() {
                return outer.action();
            }

            @Override
            public Optional<MappingExecutionContext> getContextFor(IGamepadDevice<IGamepadDeviceId> gamepad) {
                return supplier.apply(gamepad);
            }
        };
    }
}
