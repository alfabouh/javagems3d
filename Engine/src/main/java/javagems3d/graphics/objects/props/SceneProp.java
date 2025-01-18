/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package javagems3d.graphics.objects.props;

import javagems3d.graphics.objects.rendering.configuration.RenderAttributes;
import javagems3d.graphics.objects.rendering.pipeline.RenderTable;
import org.jetbrains.annotations.NotNull;
import javagems3d.system.resources.assets.models.Model;
import javagems3d.system.resources.assets.models.formats.Format3D;
import org.jetbrains.annotations.Nullable;

public class SceneProp extends AbstractSceneProp {
    public SceneProp(Model<Format3D> model, @NotNull RenderAttributes objectRenderingConfiguration) {
        super(model, objectRenderingConfiguration);
    }

    public SceneProp(Model<Format3D> model, @Nullable RenderTable renderTable) {
        super(model, renderTable);
    }

    @Override
    public boolean isDead() {
        return false;
    }
}
