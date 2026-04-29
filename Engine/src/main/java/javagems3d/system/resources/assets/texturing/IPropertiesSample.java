package javagems3d.system.resources.assets.texturing;

import org.jetbrains.annotations.Nullable;

public interface IPropertiesSample {
    void reload(@Nullable ISample.IProperties properties, boolean update);
    void setProperties(ISample.IProperties properties, boolean update);
    ISample.IProperties getProperties();
}
