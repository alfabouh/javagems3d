package workbench.graphics.objects.templates;

import org.jetbrains.annotations.NotNull;
import workbench.graphics.objects.WBenchObject;

public abstract class WBenchTemplate {
    protected final WBenchObject.ID objectId;

    public WBenchTemplate(@NotNull WBenchObject.ID objectId) {
        this.objectId = objectId;
    }

    public WBenchObject.ID getObjectId() {
        return this.objectId;
    }
}
