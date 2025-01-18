package javagems3d.graphics.objects.rendering.pipeline.fabric;

import javagems3d.graphics.objects.IRendered;
import javagems3d.graphics.objects.rendering.pipeline.enums.Stage;
import javagems3d.graphics.objects.rendering.pipeline.enums.Type;
import org.jetbrains.annotations.NotNull;

public interface IRenderFabric {
    void createResources(IRendered renderedItem);
    void destroyResources(IRendered renderedItem);

    @NotNull Stage getRenderingStage();

    default Type getRenderingType() {
        return this.getRenderingStage().getType();
    }
}