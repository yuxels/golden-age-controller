package cat.kittens.mods.controller.input;

import cat.kittens.mods.controller.lib.IGamepadDevice;
import com.google.common.collect.ImmutableSet;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public class ControllerMappingViewImpl implements ControllerMappingView {
    private final Map<MappingAction.Id, ControllerMapping> mappings;

    public ControllerMappingViewImpl(Set<ControllerMapping> mappings) {
        this.mappings = mappings != null ? toMap(mappings) : toMap(defaultSet());
    }

    public ControllerMappingViewImpl() {
        this(null);
    }

    private static Set<ControllerMapping> defaultSet() {
        return Set.of(
                ControllerMapping.create(MappingActions.BREAK, IGamepadDevice.Input.Axis.RIGHT_TRIGGER),
                ControllerMapping.create(MappingActions.INTERACT, IGamepadDevice.Input.Axis.LEFT_TRIGGER),
                ControllerMapping.create(MappingActions.PICK_BLOCK, IGamepadDevice.Input.Button.DPAD_LEFT),
                ControllerMapping.create(MappingActions.JUMP, IGamepadDevice.Input.Button.SOUTH),
                ControllerMapping.create(MappingActions.OPEN_INVENTORY, IGamepadDevice.Input.Button.NORTH),
                ControllerMapping.create(MappingActions.DROP_ITEM, IGamepadDevice.Input.Button.EAST),
                ControllerMapping.create(MappingActions.SNEAK, IGamepadDevice.Input.Button.WEST),
                ControllerMapping.create(MappingActions.OPEN_CHAT, IGamepadDevice.Input.Button.DPAD_RIGHT),
                ControllerMapping.create(MappingActions.WALK_FORWARD, IGamepadDevice.Input.Axis.LEFT_STICK_UP),
                ControllerMapping.create(MappingActions.WALK_BACKWARD, IGamepadDevice.Input.Axis.LEFT_STICK_DOWN),
                ControllerMapping.create(MappingActions.WALK_LEFTWARD, IGamepadDevice.Input.Axis.LEFT_STICK_LEFT),
                ControllerMapping.create(MappingActions.WALK_RIGHTWARD, IGamepadDevice.Input.Axis.LEFT_STICK_RIGHT),
                ControllerMapping.create(MappingActions.AIM_UP, IGamepadDevice.Input.Axis.RIGHT_STICK_UP),
                ControllerMapping.create(MappingActions.AIM_DOWN, IGamepadDevice.Input.Axis.RIGHT_STICK_DOWN),
                ControllerMapping.create(MappingActions.AIM_LEFT, IGamepadDevice.Input.Axis.RIGHT_STICK_LEFT),
                ControllerMapping.create(MappingActions.AIM_RIGHT, IGamepadDevice.Input.Axis.RIGHT_STICK_RIGHT),
                ControllerMapping.create(MappingActions.BACK_TO_GAME, IGamepadDevice.Input.Button.WEST)
        );
    }

    private static Map<MappingAction.Id, ControllerMapping> toMap(Set<ControllerMapping> set) {
        var buf = new HashMap<MappingAction.Id, ControllerMapping>();
        for (var value : set) {
            if (MappingActions.getById(value.action().id()).isEmpty())
                throw new RuntimeException("Cannot convert a non-registered mapping.");
            buf.put(value.action().id(), value);
        }
        return buf;
    }

    @Override
    public ImmutableSet<ControllerMapping> mappings() {
        return ImmutableSet.copyOf(mappings.values());
    }

    @Override
    public void overrideMapping(MappingAction.Id id, Function<Optional<ControllerMapping>, ControllerMapping> supplier) {
        mappings.compute(id, (k, v) -> supplier.apply(Optional.ofNullable(v)));
    }

    @Override
    public Optional<ControllerMapping> find(MappingAction.Id id) {
        return Optional.ofNullable(mappings.get(id));
    }
}
