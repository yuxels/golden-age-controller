package cat.kittens.mods.controller.input;

import cat.kittens.mods.controller.ControllerSupport;
import cat.kittens.mods.controller.lib.IGamepadDevice;
import cat.kittens.mods.controller.lib.IGamepadDeviceId;
import cat.kittens.mods.controller.mixin.accessor.MinecraftAccessor;

public class ControllerMovementHandler {
    private double movementForward, movementLeftward;
    private boolean jumping, sneaking;

    public ControllerMovementHandler() {
        movementForward = 0;
        movementLeftward = 0;
        jumping = false;
        sneaking = false;
    }

    public void tick() {
        if (MinecraftAccessor.instance().player == null)
            return;
        ControllerSupport.support().manager().currentController().ifPresent(controller -> {
            processInput(controller);
            externalize();
        });
    }

    public void processInput(IGamepadDevice<IGamepadDeviceId> gamepad) {
        movementForward = gamepad.chord().getAxisValue(ActionIds.WALK_FORWARD).orElse(0) -
                gamepad.chord().getAxisValue(ActionIds.WALK_BACKWARD).orElse(0);
        movementLeftward = gamepad.chord().getAxisValue(ActionIds.WALK_LEFTWARD).orElse(0) -
                gamepad.chord().getAxisValue(ActionIds.WALK_RIGHTWARD).orElse(0);
    }

    public void externalize() {
        MinecraftAccessor.instance().player.field_161.field_2533 =
                (float) Math.min(1, MinecraftAccessor.instance().player.field_161.field_2533 + movementForward);
        MinecraftAccessor.instance().player.field_161.field_2532 =
                (float) Math.min(1, MinecraftAccessor.instance().player.field_161.field_2533 + movementLeftward);
        MinecraftAccessor.instance().player.field_161.field_2536 = sneaking ||
                MinecraftAccessor.instance().player.field_161.field_2536;
        MinecraftAccessor.instance().player.field_161.field_2535 = jumping ||
                MinecraftAccessor.instance().player.field_161.field_2535;
    }
}
