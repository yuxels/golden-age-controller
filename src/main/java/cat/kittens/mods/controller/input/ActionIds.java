package cat.kittens.mods.controller.input;

public class ActionIds {
    private static IControllerMapping.ActionId action(String name) {
        return IControllerMapping.ActionId.fromString("minecraft:" + name);
    }

    public static final IControllerMapping.ActionId JUMP = action("jump");
    public static final IControllerMapping.ActionId OPEN_INVENTORY = action("open_inventory");
    public static final IControllerMapping.ActionId WALK_FORWARD = action("walk_forward");
    public static final IControllerMapping.ActionId WALK_BACKWARD = action("walk_backward");
    public static final IControllerMapping.ActionId WALK_LEFTWARD = action("walk_leftward");
    public static final IControllerMapping.ActionId WALK_RIGHTWARD = action("walk_rightward");
    public static final IControllerMapping.ActionId BREAK = action("break");
    public static final IControllerMapping.ActionId INTERACT = action("interact");
    public static final IControllerMapping.ActionId DROP_ITEM = action("drop_item");
    public static final IControllerMapping.ActionId SNEAK = action("sneak");
    public static final IControllerMapping.ActionId OPEN_CHAT = action("open_chat");
    public static final IControllerMapping.ActionId PICK_BLOCK = action("pick_block");
    public static final IControllerMapping.ActionId AIM_UP = action("aim_up");
    public static final IControllerMapping.ActionId AIM_DOWN = action("aim_down");
    public static final IControllerMapping.ActionId AIM_LEFT = action("aim_left");
    public static final IControllerMapping.ActionId AIM_RIGHT = action("aim_right");
}
