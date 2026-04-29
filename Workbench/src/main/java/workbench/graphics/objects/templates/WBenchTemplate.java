package workbench.graphics.objects.templates;

import org.jetbrains.annotations.NotNull;
import workbench.graphics.objects.WBenchObject;
import javagems3d.system.service.files.VirtualObjectsFolder;

public abstract class WBenchTemplate implements VirtualObjectsFolder.ObjectWithName {
    protected final WBenchObject.ID objectId;

    public WBenchTemplate(@NotNull WBenchObject.ID objectId) {
        this.objectId = objectId;
    }

    public WBenchObject.ID getObjectId() {
        return this.objectId;
    }

    @Override
    public String name() {
        return this.getObjectId().nameId();
    }
}
