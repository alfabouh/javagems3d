package javagems3d.system.resources.assets.texturing.base;

import org.jetbrains.annotations.Nullable;

public interface IModifiableSample {
    void reload(@Nullable ISample.IProperties properties, boolean update);
    void setProperties(ISample.IProperties properties, boolean update);
    ISample.IProperties getProperties();
}
