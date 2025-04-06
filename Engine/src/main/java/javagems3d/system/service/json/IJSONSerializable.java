package javagems3d.system.service.json;

import javagems3d.system.service.annotations.RequireEmptyConstructor;
import org.jetbrains.annotations.NotNull;

@RequireEmptyConstructor
public interface IJSONSerializable<T> {
    @NotNull JSONFileManaging.SerializationRules<T> getSerializationRules();
}