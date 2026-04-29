package api.application.workbench.manager;

import api.application.workbench.resources.APIResource;
import javagems3d.system.service.files.VirtualObjectsFolder;
import org.jetbrains.annotations.NotNull;

public class ApiResourceObjectsFolder<A, B, T extends APIResource<A, B>> extends VirtualObjectsFolder<T> {
    public ApiResourceObjectsFolder(@NotNull String name) {
        super(name);
    }
}
