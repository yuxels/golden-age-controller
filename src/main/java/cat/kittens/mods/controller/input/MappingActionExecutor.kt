package cat.kittens.mods.controller.input

public fun interface MappingActionExecutor {
    public fun perform(ctx: MappingExecutionContext)

    public companion object {
        public fun internal(): MappingActionExecutor {
            return MappingActionExecutor {
                throw RuntimeException("This mapping is meant to be used internally by Mixin injections and cannot be executed directly.")
            }
        }

        public fun withCooldown(amountInMilliseconds: Long, callback: MappingActionExecutor): MappingActionExecutor {
            var lastExecution = -1L
            return MappingActionExecutor { ctx ->
                if (System.currentTimeMillis() > lastExecution + amountInMilliseconds) {
                    callback.perform(ctx)
                    lastExecution = System.currentTimeMillis()
                }
            }
        }
    }
}
