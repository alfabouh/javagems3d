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

package ru.jgems3d.engine.system.map.loaders.tbox;

import jgems_api.test.TestTBoxApp;
import org.joml.Vector3f;
import ru.jgems3d.engine.JGemsHelper;
import ru.jgems3d.engine.graphics.opengl.rendering.fabric.objects.IRenderObjectFabric;
import ru.jgems3d.engine.graphics.opengl.rendering.fabric.objects.data.RenderEntityData;
import ru.jgems3d.engine.graphics.opengl.rendering.items.props.SceneProp;
import ru.jgems3d.engine.graphics.opengl.world.SceneWorld;
import ru.jgems3d.engine.physics.entities.BtDynamicMeshBody;
import ru.jgems3d.engine.physics.entities.BtStaticMeshBody;
import ru.jgems3d.engine.physics.world.PhysicsWorld;
import ru.jgems3d.engine.system.resources.assets.models.Model;
import ru.jgems3d.engine.system.resources.assets.models.formats.Format3D;
import ru.jgems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;
import ru.jgems3d.engine.system.resources.assets.models.mesh.data.render.MeshRenderData;
import ru.jgems3d.engine.system.resources.assets.shaders.manager.JGemsShaderManager;
import ru.jgems3d.engine.system.resources.manager.GameResources;
import ru.jgems3d.engine.system.service.exceptions.JGemsRuntimeException;
import ru.jgems3d.engine_api.app.tbox.containers.TRenderContainer;
import ru.jgems3d.toolbox.map_sys.save.objects.object_attributes.AttributeID;
import ru.jgems3d.toolbox.map_sys.save.objects.object_attributes.AttributesContainer;
import ru.jgems3d.toolbox.map_table.object.ObjectCategory;

public abstract class TBoxMapDefaultObjectsPlacer {
    public static void placeObjectOnMap(SceneWorld sceneWorld, PhysicsWorld physicsWorld, GameResources globalGameResources, GameResources localGameResources, String id, ObjectCategory objectCategory, AttributesContainer attributesContainer, TRenderContainer renderContainer) {
        try {
            Vector3f pos = attributesContainer.tryGetValueFromAttributeByID(AttributeID.POSITION_XYZ, Vector3f.class);
            Vector3f rot = attributesContainer.tryGetValueFromAttributeByID(AttributeID.ROTATION_XYZ, Vector3f.class);
            Vector3f scale = attributesContainer.tryGetValueFromAttributeByID(AttributeID.SCALING_XYZ, Vector3f.class);

            MeshDataGroup meshDataGroup = localGameResources.createMesh(renderContainer.getPathToRenderModel());
            JGemsShaderManager shaderManager = localGameResources.getResource(renderContainer.getPathToJGemsShader());

            if (objectCategory.equals(TestTBoxApp.PHYSICS_OBJECT)) {
                if (renderContainer.getSceneEntityClass() == null) {
                    throw new JGemsRuntimeException("Null scene entity!");
                }
                RenderEntityData renderEntityData = new RenderEntityData(renderContainer.getRenderFabricClass().newInstance(), renderContainer.getSceneEntityClass(), new MeshRenderData(renderContainer.getMeshRenderAttributes(), shaderManager));

                boolean isStatic = attributesContainer.tryGetValueFromAttributeByID(AttributeID.IS_STATIC, Boolean.class);
                JGemsHelper.UTILS.createMeshCollisionData(meshDataGroup);
                if (isStatic) {
                    BtStaticMeshBody worldModeledBrush = new BtStaticMeshBody(meshDataGroup, physicsWorld, pos, id);
                    JGemsHelper.WORLD.addItemInWorld(worldModeledBrush, new RenderEntityData(renderEntityData, meshDataGroup));
                    worldModeledBrush.setCanBeDestroyed(false);
                    worldModeledBrush.setRotation(new Vector3f(rot).negate());
                    worldModeledBrush.setScaling(scale);
                } else {
                    BtDynamicMeshBody worldModeledBrush = new BtDynamicMeshBody(meshDataGroup, physicsWorld, pos, id);
                    JGemsHelper.WORLD.addItemInWorld(worldModeledBrush, new RenderEntityData(renderEntityData, meshDataGroup));
                    worldModeledBrush.setCanBeDestroyed(false);
                    worldModeledBrush.setRotation(new Vector3f(rot).negate());
                    worldModeledBrush.setScaling(scale);
                }
            } else if (objectCategory.equals(TestTBoxApp.PROP_OBJECT)) {
                MeshRenderData meshRenderData = new MeshRenderData(renderContainer.getMeshRenderAttributes(), shaderManager);
                IRenderObjectFabric renderFabric = renderContainer.getRenderFabricClass().newInstance();

                Model<Format3D> model = new Model<>(new Format3D(), meshDataGroup);
                model.getFormat().setPosition(pos);
                model.getFormat().setRotation(rot);
                model.getFormat().setScaling(scale);
                JGemsHelper.WORLD.addPropInScene(new SceneProp(renderFabric, model, meshRenderData));
            }
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}