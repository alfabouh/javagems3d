package javagems3d.system.service.files.json;

import javagems3d.system.service.annotations.RequireEmptyConstructor;
import org.jetbrains.annotations.NotNull;

@RequireEmptyConstructor
public interface IJSONSerializable<T> {
    @NotNull JSONFileManaging.SerializationRules<T> getSerializationRules();
}