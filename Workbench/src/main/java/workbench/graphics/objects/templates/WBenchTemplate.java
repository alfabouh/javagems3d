package workbench.graphics.objects.templates;

import org.jetbrains.annotations.NotNull;
import workbench.graphics.objects.WBenchObject;
import javagems3d.system.service.collections.AbstractObjectsFolder;

public abstract class WBenchTemplate implements AbstractObjectsFolder.ObjectWithName {
    protected final WBenchObject.ID objectId;

    public WBenchTemplate(@NotNull WBenchObject.ID objectId) {
        this.objectId = objectId;
    }

    public WBenchObject.ID getObjectId() {
        return this.objectId;
    }

    @Override
    public String getName() {
        return this.getObjectId().getNameId();
    }
}
