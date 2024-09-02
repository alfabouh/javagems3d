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

package jgems_api.test;

import javagems3d.JGems3D;
import javagems3d.graphics.opengl.rendering.fabric.objects.render.RenderEntity;
import javagems3d.graphics.opengl.rendering.items.objects.EntityObject;
import javagems3d.system.map.loaders.tbox.placers.TDefaultRenderContainer;
import javagems3d.system.resources.assets.models.mesh.data.render.MeshRenderAttributes;
import javagems3d.system.resources.manager.JGemsResourceManager;
import javagems3d.system.service.path.JGemsPath;
import api.app.main.JGemsTBoxApplication;
import api.app.main.JGemsTBoxEntry;
import api.app.main.tbox.ITBoxEntitiesObjectData;
import api.app.main.tbox.TBoxEntitiesUserData;
import api.app.main.tbox.containers.TObjectData;
import api.app.main.tbox.containers.TUserData;
import javagems3d.temp.map_sys.save.objects.object_attributes.Attribute;
import javagems3d.temp.map_sys.save.objects.object_attributes.AttributeID;
import javagems3d.temp.map_sys.save.objects.object_attributes.AttributeTarget;
import javagems3d.temp.map_sys.save.objects.object_attributes.AttributesContainer;
import toolbox.map_table.object.ModeledObjectData;
import toolbox.map_table.object.ObjectCategory;
import toolbox.resources.TBoxResourceManager;
import org.joml.Vector3f;

@JGemsTBoxEntry
public class TestTBoxApp implements JGemsTBoxApplication {
    public static ObjectCategory PHYSICS_OBJECT = new ObjectCategory("Entities");
    public static ObjectCategory PROP_OBJECT = new ObjectCategory("Props");
    public static ObjectCategory MARKER_OBJECT = new ObjectCategory("Markers");
    public static ObjectCategory LIQUID_OBJECT = new ObjectCategory("Liquids");

    @Override
    public void initEntitiesObjectData(TBoxResourceManager tBoxResourceManager, ITBoxEntitiesObjectData tBoxEntitiesObjectData) {
        Attribute<Vector3f> transformPosXYZ = new Attribute<>(AttributeTarget.POSITION_XYZ, AttributeID.POSITION_XYZ, new Vector3f(0.0f));
        Attribute<Vector3f> transformRotXYZ = new Attribute<>(AttributeTarget.ROTATION_XYZ, AttributeID.ROTATION_XYZ, new Vector3f(0.0f));
        Attribute<Vector3f> transformScaleXYZ = new Attribute<>(AttributeTarget.SCALING_XYZ, AttributeID.SCALING_XYZ, new Vector3f(1.0f));
        tBoxEntitiesObjectData.add("stat", new TObjectData(new ModeledObjectData(new AttributesContainer(transformPosXYZ, transformRotXYZ, transformScaleXYZ), tBoxResourceManager.getShaderAssets().world_object, tBoxResourceManager.createModel(new JGemsPath("/assets/jgems/models/cube/cube.obj")), TestTBoxApp.PHYSICS_OBJECT)));
    }

    @Override
    public void initEntitiesUserData(JGemsResourceManager jGemsResourceManager, TBoxEntitiesUserData tBoxEntitiesUserData) {
        tBoxEntitiesUserData.add("stat", new TUserData(new TDefaultRenderContainer(new RenderEntity(), EntityObject.class, new JGemsPath("/assets/jgems/models/cube/cube.obj"), new JGemsPath(JGems3D.Paths.SHADERS, "world/world_gbuffer"), new MeshRenderAttributes())));
    }
}