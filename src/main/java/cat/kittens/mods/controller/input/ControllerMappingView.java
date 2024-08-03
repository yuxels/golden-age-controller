package cat.kittens.mods.controller.input;

import cat.kittens.mods.controller.ControllerSupport;
import com.google.common.collect.ImmutableSet;

import java.util.Optional;
import java.util.OptionalDouble;
import java.util.function.Function;

public interface ControllerMappingView {
    ImmutableSet<ControllerMapping> mappings();

    Optional<ControllerMapping> find(MappingAction.Id id);

    default Optional<ControllerMapping> find(MappingAction action) {
        return find(action.id());
    }

    default OptionalDouble getCurrentValue(MappingAction.Id action) {
        var controller = ControllerSupport.support().manager().currentController().orElse(null);
        if (controller == null)
            return OptionalDouble.empty();
        var value = find(action).flatMap(m -> m.getContextFor(controller)).map(MappingExecutionContext::value);
        return value.map(OptionalDouble::of).orElseGet(OptionalDouble::empty);
    }

    default OptionalDouble getCurrentValue(MappingAction action) {
        return getCurrentValue(action.id());
    }

    void overrideMapping(MappingAction.Id id, Function<Optional<ControllerMapping>, ControllerMapping> supplier);
}
