package cat.kittens.mods.controller.input

/**
 * Provides action execution and information about a mapping.
 */
public interface MappingAction {
    /**
     * Unique identification for this action.
     */
    public val id: Id

    /**
     * Contexts where this mapping action could be executed.
     */
    public val contexts: Set<MappingActionContext>

    /**
     * Whether this mapping has an executor that is made for internal-use only.
     */
    public val isExecutorInternalUseOnly: Boolean

    public val executor: MappingActionExecutor

    public interface Id {
        override fun toString(): String

        override fun equals(other: Any?): Boolean

        override fun hashCode(): Int

        public companion object {
            public fun fromString(value: String): Id {
                return object : Id {
                    override fun toString(): String {
                        return value
                    }

                    override fun equals(other: Any?): Boolean {
                        if (other is Id) {
                            return other.toString() == toString()
                        }
                        return false
                    }

                    override fun hashCode(): Int {
                        return value.hashCode()
                    }
                }
            }
        }
    }

    public companion object {
        public fun inGame(id: Id, executor: MappingActionExecutor): MappingAction =
            invoke(id, setOf(MappingActionContext.InGame), false, executor)

        public fun inGame(id: String, executor: MappingActionExecutor): MappingAction =
            inGame(Id.fromString(id), executor)

        public fun inGame(id: String, internal: Boolean, executor: MappingActionExecutor): MappingAction =
            invoke(Id.fromString(id), setOf(MappingActionContext.InGame), internal, executor)

        public fun inGame(id: Id, internal: Boolean, executor: MappingActionExecutor): MappingAction =
            invoke(id, setOf(MappingActionContext.InGame), internal, executor)

        @JvmName("create")
        public operator fun invoke(
            id: Id, contexts: Set<MappingActionContext>, executor: MappingActionExecutor
        ): MappingAction = invoke(id, contexts, false, executor)

        @JvmName("create")
        public operator fun invoke(
            id: String, contexts: Set<MappingActionContext>, executor: MappingActionExecutor
        ): MappingAction = invoke(Id.fromString(id), contexts, false, executor)

        @JvmName("create")
        public operator fun invoke(
            id: String,
            contexts: Set<MappingActionContext>,
            internal: Boolean,
            executor: MappingActionExecutor
        ): MappingAction = invoke(Id.fromString(id), contexts, internal, executor)

        @JvmName("create")
        public operator fun invoke(
            id: Id, contexts: Set<MappingActionContext>,
            internal: Boolean, executor: MappingActionExecutor
        ): MappingAction = object : MappingAction {
            override val id: Id = id
            override val contexts: Set<MappingActionContext> = contexts
            override val executor: MappingActionExecutor = executor
            override val isExecutorInternalUseOnly: Boolean = internal
        }
    }
}
