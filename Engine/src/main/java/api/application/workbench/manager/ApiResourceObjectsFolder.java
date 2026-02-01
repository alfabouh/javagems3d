package api.application.workbench.manager;

import api.application.workbench.resources.APIResource;
import javagems3d.system.service.collections.AbstractObjectsFolder;
import org.jetbrains.annotations.NotNull;

public class ApiResourceObjectsFolder<A, B, T extends APIResource<A, B>> extends AbstractObjectsFolder<T> {
    public ApiResourceObjectsFolder(@NotNull String name) {
        super(name);
    }
}
