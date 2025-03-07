package workbench.graphics.objects;

import javagems3d.graphics.objects.entities.SceneProp;
import javagems3d.graphics.objects.rendering.configuration.RenderAttributes;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.system.resources.assets.models.Model3D;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import workbench.graphics.scene.world.WBenchWorld;

public class WBenchObject extends SceneProp {
    public WBenchObject(@NotNull WBenchWorld wBenchWorld, @Nullable Model3D model, @NotNull RenderAttributes objectRenderingConfiguration) {
        super(wBenchWorld, model, objectRenderingConfiguration);
    }
}
