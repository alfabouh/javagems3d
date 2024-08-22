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

package ru.jgems3d.engine.system.map.loaders.tbox.placers;

import org.joml.Vector3f;
import ru.jgems3d.engine.JGemsHelper;
import ru.jgems3d.engine.graphics.opengl.rendering.fabric.objects.IRenderObjectFabric;
import ru.jgems3d.engine.graphics.opengl.rendering.fabric.objects.data.RenderEntityData;
import ru.jgems3d.engine.graphics.opengl.rendering.items.props.SceneProp;
import ru.jgems3d.engine.graphics.opengl.world.SceneWorld;
import ru.jgems3d.engine.physics.entities.BtDynamicMeshBody;
import ru.jgems3d.engine.physics.entities.BtStaticMeshBody;
import ru.jgems3d.engine.physics.world.PhysicsWorld;
import ru.jgems3d.engine.physics.world.triggers.Zone;
import ru.jgems3d.engine.physics.world.triggers.zones.SimpleTriggerZone;
import ru.jgems3d.engine.system.resources.assets.models.Model;
import ru.jgems3d.engine.system.resources.assets.models.formats.Format3D;
import ru.jgems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;
import ru.jgems3d.engine.system.resources.assets.models.mesh.data.render.MeshRenderData;
import ru.jgems3d.engine.system.resources.assets.shaders.manager.JGemsShaderManager;
import ru.jgems3d.engine.system.resources.manager.GameResources;
import ru.jgems3d.engine_api.app.tbox.containers.TUserData;
import ru.jgems3d.toolbox.map_sys.save.objects.object_attributes.AttributeID;
import ru.jgems3d.toolbox.map_sys.save.objects.object_attributes.AttributesContainer;

public abstract class TBoxMapDefaultObjectsPlacer {
    public static void placeObjectOnMap(SceneWorld sceneWorld, PhysicsWorld physicsWorld, GameResources globalGameResources, GameResources localGameResources, String id, AttributesContainer attributesContainer, TUserData userData) {
        TDefaultRenderContainer renderContainer = userData.tryCastObject(TDefaultRenderContainer.class);
        if (renderContainer == null) {
            return;
        }
        Vector3f pos = attributesContainer.tryGetValueFromAttributeByID(AttributeID.POSITION_XYZ, Vector3f.class);
        Vector3f rot = attributesContainer.tryGetValueFromAttributeByID(AttributeID.ROTATION_XYZ, Vector3f.class);
        Vector3f scale = attributesContainer.tryGetValueFromAttributeByID(AttributeID.SCALING_XYZ, Vector3f.class);
        boolean isProp = attributesContainer.tryGetValueFromAttributeByID(AttributeID.IS_PROP, Boolean.class);

        MeshDataGroup meshDataGroup = localGameResources.createMesh(renderContainer.getPathToRenderModel());
        JGemsShaderManager shaderManager = localGameResources.getResource(renderContainer.getPathToJGemsShader());

        if (isProp) {
            MeshRenderData meshRenderData = new MeshRenderData(renderContainer.getMeshRenderAttributes(), shaderManager);
            IRenderObjectFabric renderFabric = renderContainer.getRenderFabric();

            Model<Format3D> model = new Model<>(new Format3D(), meshDataGroup);
            model.getFormat().setPosition(pos);
            model.getFormat().setRotation(rot);
            model.getFormat().setScaling(scale);
            JGemsHelper.WORLD.addPropInScene(new SceneProp(renderFabric, model, meshRenderData));
        } else {
            RenderEntityData renderEntityData = new RenderEntityData(renderContainer.getRenderFabric(), renderContainer.getSceneEntityClass(), new MeshRenderData(renderContainer.getMeshRenderAttributes(), shaderManager));

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
        }
    }

    public static void placeTBoxTriggerZoneOnMap(PhysicsWorld physicsWorld, Vector3f position, Vector3f size, String id, AttributesContainer attributesContainer, TUserData renderContainer) {
        TDefaultTriggerZoneInfo defaultTriggerZoneInfo = renderContainer.tryCastObject(TDefaultTriggerZoneInfo.class);
        if (defaultTriggerZoneInfo != null) {
            SimpleTriggerZone simpleTriggerZone = new SimpleTriggerZone(new Zone(position, size));
            simpleTriggerZone.setTriggerAction(defaultTriggerZoneInfo.getTriggerAction());
            physicsWorld.addItem(simpleTriggerZone);
        }
    }

    public static void handleMarkerOnMap(SceneWorld sceneWorld, PhysicsWorld physicsWorld, GameResources globalGameResources, GameResources localGameResources, String id, AttributesContainer attributesContainer, TUserData renderContainer) {
    }
}
