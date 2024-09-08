package cat.kittens.mods.controller.input

import net.minecraft.client.gui.screen.ChatScreen
import net.minecraft.client.gui.screen.ingame.InventoryScreen

public object MappingActions {
    private val MAPPINGS: MutableMap<MappingAction.Id, MappingAction> = mutableMapOf()

    @JvmField
    public val BREAK: MappingAction =
        register(MappingAction.inGame("minecraft:break", true, MappingActionExecutor.internal()))

    @JvmField
    public val INTERACT: MappingAction =
        register(MappingAction.inGame("minecraft:interact", true, MappingActionExecutor.internal()))

    @JvmField
    public val PICK_BLOCK: MappingAction = register(MappingAction.inGame("minecraft:pick_block") { ctx ->
        ctx.minecraft.asAccessor.pickBlock()
    })

    @JvmField
    public val JUMP: MappingAction =
        register(MappingAction.inGame("minecraft:jump", true, MappingActionExecutor.internal()))

    @JvmField
    public val OPEN_INVENTORY: MappingAction = register(
        MappingAction.inGame("minecraft:open_inventory") { ctx ->
            ctx.minecraft.setScreen(InventoryScreen(ctx.minecraft.player))
        }
    )

    @JvmField
    public val BACK_TO_GAME: MappingAction = register(MappingAction(
        "minecraft:back_to_game",
        setOf(
            MappingActionContext.Container,
            MappingActionContext.Inventory,
            MappingActionContext.Chat,
            MappingActionContext.Pause
        )
    ) { ctx -> ctx.minecraft.setScreen(null) })

    @JvmField
    public val WALK_FORWARD: MappingAction = register(
        MappingAction.inGame("minecraft:walk_forward", true, MappingActionExecutor.internal())
    )

    @JvmField
    public val WALK_BACKWARD: MappingAction = register(
        MappingAction.inGame("minecraft:walk_backward", true, MappingActionExecutor.internal())
    )

    @JvmField
    public val WALK_LEFTWARD: MappingAction = register(
        MappingAction.inGame("minecraft:walk_leftward", true, MappingActionExecutor.internal())
    )

    @JvmField
    public val WALK_RIGHTWARD: MappingAction = register(
        MappingAction.inGame("minecraft:walk_rightward", true, MappingActionExecutor.internal())
    )

    @JvmField
    public val DROP_ITEM: MappingAction = register(
        MappingAction.inGame("minecraft:drop_item") { ctx -> if (!ctx.held) ctx.minecraft.player.dropSelectedItem() }
    )

    @JvmField
    public val SNEAK: MappingAction =
        register(MappingAction.inGame("minecraft:sneak", true, MappingActionExecutor.internal()))

    @JvmField
    public val OPEN_CHAT: MappingAction = register(
        MappingAction.inGame("minecraft:open_chat") { ctx ->
            if (ctx.minecraft.isWorldRemote) ctx.minecraft.setScreen(ChatScreen())
        }
    )

    @JvmField
    public val AIM_LEFT: MappingAction =
        register(MappingAction.inGame("minecraft:aim_left", true, MappingActionExecutor.internal()))

    @JvmField
    public val AIM_RIGHT: MappingAction = register(
        MappingAction.inGame("minecraft:aim_right", true, MappingActionExecutor.internal())
    )

    @JvmField
    public val AIM_UP: MappingAction =
        register(MappingAction.inGame("minecraft:aim_up", true, MappingActionExecutor.internal()))

    @JvmField
    public val AIM_DOWN: MappingAction =
        register(MappingAction.inGame("minecraft:aim_down", true, MappingActionExecutor.internal()))

    public fun register(action: MappingAction): MappingAction {
        MAPPINGS[action.id] = action
        return action
    }

    @JvmName("getById")
    public fun get(id: MappingAction.Id): MappingAction? = MAPPINGS[id]

    public fun all(): MutableCollection<MappingAction> = MAPPINGS.values
}
