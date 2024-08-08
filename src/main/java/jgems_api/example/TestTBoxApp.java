package jgems_api.example;

import org.joml.Vector3f;
import ru.jgems3d.engine.graphics.opengl.rendering.fabric.objects.render.RenderEntity;
import ru.jgems3d.engine.graphics.opengl.rendering.items.objects.EntityObject;
import ru.jgems3d.engine.system.service.misc.JGPath;
import ru.jgems3d.engine.system.resources.assets.models.mesh.data.render.MeshRenderAttributes;
import ru.jgems3d.engine_api.app.JGemsTBoxApplication;
import ru.jgems3d.engine_api.app.JGemsTBoxEntry;
import ru.jgems3d.engine_api.app.tbox.IAppTBoxObjectsContainer;
import ru.jgems3d.engine_api.app.tbox.containers.TEntityContainer;
import ru.jgems3d.engine_api.app.tbox.containers.TRenderContainer;
import ru.jgems3d.toolbox.map_sys.save.objects.object_attributes.Attribute;
import ru.jgems3d.toolbox.map_sys.save.objects.object_attributes.AttributeContainer;
import ru.jgems3d.toolbox.map_sys.save.objects.object_attributes.AttributeTarget;
import ru.jgems3d.toolbox.map_sys.save.objects.object_attributes.AttributeID;
import ru.jgems3d.toolbox.map_table.object.ModeledObjectData;
import ru.jgems3d.toolbox.map_table.object.ObjectCategory;

@JGemsTBoxEntry
public class TestTBoxApp implements JGemsTBoxApplication {
    public static ObjectCategory PHYSICS_OBJECT = new ObjectCategory("Entities");
    public static ObjectCategory PROP_OBJECT = new ObjectCategory("Props");
    public static ObjectCategory MARKER_OBJECT = new ObjectCategory("Markers");
    public static ObjectCategory LIQUID_OBJECT = new ObjectCategory("Liquids");

    @Override
    public void fillTBoxObjectsContainer(IAppTBoxObjectsContainer tBoxObjectsContainer) {
        Attribute<Vector3f> transformPosXYZ = new Attribute<>(AttributeTarget.POSITION_XYZ, AttributeID.POSITION_XYZ, new Vector3f(0.0f));
        Attribute<Vector3f> transformRotXYZ = new Attribute<>(AttributeTarget.ROTATION_XYZ, AttributeID.ROTATION_XYZ, new Vector3f(0.0f));
        Attribute<Vector3f> transformScaleXYZ = new Attribute<>(AttributeTarget.SCALING_XYZ, AttributeID.SCALING_XYZ, new Vector3f(1.0f));

        tBoxObjectsContainer.addObject("test",
                new TEntityContainer(ModeledObjectData.class, new JGPath("/assets/jgems/models/cube/cube.obj"), new AttributeContainer(transformPosXYZ, transformRotXYZ, transformScaleXYZ), PHYSICS_OBJECT),
                new TRenderContainer(RenderEntity.class, EntityObject.class, new JGPath("/assets/jgems/models/cube/cube.obj"), new MeshRenderAttributes()));
    }
}
