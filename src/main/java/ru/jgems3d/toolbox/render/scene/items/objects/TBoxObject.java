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

package ru.jgems3d.toolbox.render.scene.items.objects;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import ru.jgems3d.engine.system.resources.assets.models.Model;
import ru.jgems3d.engine.system.resources.assets.models.formats.Format3D;
import ru.jgems3d.logger.SystemLogging;
import ru.jgems3d.logger.managers.LoggingManager;
import ru.jgems3d.toolbox.map_sys.save.objects.object_attributes.AttributesContainer;
import ru.jgems3d.toolbox.render.scene.items.objects.base.TBoxAbstractObject;
import ru.jgems3d.toolbox.render.scene.items.renderers.data.TBoxObjectRenderData;

public final class TBoxObject extends TBoxAbstractObject {
    public TBoxObject(@NotNull String name, @NotNull TBoxObjectRenderData renderData, @NotNull Model<Format3D> model) {
        super(name, renderData, model);
    }

    @Override
    public TBoxObject copy() {
        SystemLogging.get().getLogManager().log("Copied " + this);

        TBoxObject tBoxObject = new TBoxObject(this.objectId(), this.getRenderData(), new Model<>(this.getModel()));
        tBoxObject.setAttributeContainer(new AttributesContainer(this.getAttributeContainer()));
        return tBoxObject;
    }
}