package cat.kittens.mods.controller.input;

import cat.kittens.mods.controller.ControllerSupport;
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
        if (MinecraftAccessor.instance().currentScreen != null || MinecraftAccessor.instance().player == null)
            return;
        ControllerSupport.support().manager().currentController().ifPresent(controller -> {
            processInput();
            externalize();
        });
    }

    public void processInput() {
        if (MinecraftAccessor.instance().currentScreen != null || MinecraftAccessor.instance().player == null)
            return;
        movementForward = ControllerSupport.support().mapping().getCurrentValue(MappingActions.WALK_FORWARD).orElse(0) -
                ControllerSupport.support().mapping().getCurrentValue(MappingActions.WALK_BACKWARD).orElse(0);
        movementLeftward = ControllerSupport.support().mapping().getCurrentValue(MappingActions.WALK_LEFTWARD).orElse(0) -
                ControllerSupport.support().mapping().getCurrentValue(MappingActions.WALK_RIGHTWARD).orElse(0);
        jumping = ControllerSupport.support().mapping().getCurrentValue(MappingActions.JUMP).isPresent();
        sneaking = ControllerSupport.support().mapping().getCurrentValue(MappingActions.SNEAK).isPresent();
    }

    public void externalize() {
        if (MinecraftAccessor.instance().currentScreen != null || MinecraftAccessor.instance().player == null)
            return;
        MinecraftAccessor.instance().player.field_161.field_2533 =
                (float) Math.min(1, MinecraftAccessor.instance().player.field_161.field_2533 + movementForward);
        MinecraftAccessor.instance().player.field_161.field_2532 =
                (float) Math.min(1, MinecraftAccessor.instance().player.field_161.field_2532 + movementLeftward);
        MinecraftAccessor.instance().player.field_161.field_2536 = sneaking ||
                MinecraftAccessor.instance().player.field_161.field_2536;
        MinecraftAccessor.instance().player.field_161.field_2535 = jumping ||
                MinecraftAccessor.instance().player.field_161.field_2535;
    }
}
