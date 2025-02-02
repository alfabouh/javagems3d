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

package javagems3d.system.map.loaders.tbox.placers;

import org.joml.Vector3f;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.physics.world.PhysicsWorld;
import javagems3d.physics.world.triggers.Zone;
import javagems3d.physics.world.triggers.zones.SimpleTriggerZone;
import javagems3d.physics.world.triggers.zones.base.AbstractTriggerZone;
import javagems3d.system.resources.managing.resources.GameResources;
import api.app.main.tbox.containers.TUserData;
import javagems3d.temp.map_sys.save.objects.object_attributes.AttributesContainer;

public abstract class TBoxMapDefaultObjectsPlacer {
    public static void placeTBoxEntityOnMap(SceneWorld sceneWorld, PhysicsWorld physicsWorld, GameResources globalGameResources, GameResources localGameResources, String id, AttributesContainer attributesContainer, TUserData userData) {
       // TDefaultRenderContainer renderContainer = userData.tryCastObject(TDefaultRenderContainer.class);
       // if (renderContainer == null) {
       //     return;
       // }
       // Vector3f pos = attributesContainer.getValueFromAttributeByID(AttributeID.POSITION_XYZ, Vector3f.class);
       // Vector3f rot = attributesContainer.getValueFromAttributeByID(AttributeID.ROTATION_XYZ, Vector3f.class);
       // Vector3f scale = attributesContainer.getValueFromAttributeByID(AttributeID.SCALING_XYZ, Vector3f.class);
       // Boolean isProp = attributesContainer.getValueFromAttributeByID(AttributeID.IS_PROP, Boolean.class);
//
       // MeshGroup meshGroup = localGameResources.createMeshGroup(renderContainer.getPathToRenderModel(), ModelMeshLoader.FLAGS.DEFAULT);
       // JGemsShaderManager shaderManager = globalGameResources.getResource(renderContainer.getPathToJGemsShader());
//
       // if (isProp != null && (isProp)) {
       //     RenderAttributes modelRenderData = renderContainer.getObjectRenderSettings().copy();
       //     //IRenderObjectFabric renderFabric = renderContainer.getRenderFabric();
//
       //     Model3D model = new Model<>(new Pose3D(), meshGroup);
       //     model.getPose().setPosition(pos);
       //     model.getPose().setRotation(rot);
       //     model.getPose().setScaling(scale);
       //     JGemsHelper.WORLD.addPropInScene(new SceneProp(null, model, modelRenderData));
       // } else {
       //     EntityRenderData entityRenderData = new EntityRenderData(null, renderContainer.getSceneEntityClass(), renderContainer.getObjectRenderSettings().copy());
//
       //     Boolean isStatic = attributesContainer.getValueFromAttributeByID(AttributeID.IS_STATIC, Boolean.class);
       //     if (isStatic == null || isStatic) {
       //         JGemsStaticBody worldModeledBrush = new JGemsStaticBody(MeshCollider.getStatic(meshGroup), physicsWorld, pos, id);
       //         JGemsHelper.WORLD.addItemInWorld(worldModeledBrush, new EntityRenderData(entityRenderData, meshGroup));
       //         worldModeledBrush.setCanBeDestroyed(false);
       //         worldModeledBrush.setRotation(new Vector3f(rot).negate());
       //         worldModeledBrush.setScaling(scale);
       //     } else {
       //         JGemsDynamicBody worldModeledBrush = new JGemsDynamicBody(MeshCollider.getDynamic(meshGroup), physicsWorld, pos, id);
       //         JGemsHelper.WORLD.addItemInWorld(worldModeledBrush, new EntityRenderData(entityRenderData, meshGroup));
       //         worldModeledBrush.setCanBeDestroyed(false);
       //         worldModeledBrush.setRotation(new Vector3f(rot).negate());
       //         worldModeledBrush.setScaling(scale);
       //     }
       // }
    }

    public static AbstractTriggerZone placeTBoxTriggerZoneOnMap(PhysicsWorld physicsWorld, Vector3f position, Vector3f size, String id, AttributesContainer attributesContainer, TUserData renderContainer) {
        TDefaultTriggerZoneInfo defaultTriggerZoneInfo = renderContainer.tryCastObject(TDefaultTriggerZoneInfo.class);
        if (defaultTriggerZoneInfo != null) {
            SimpleTriggerZone simpleTriggerZone = new SimpleTriggerZone(new Zone(position, size));
            simpleTriggerZone.setTriggerAction(defaultTriggerZoneInfo.getTriggerAction());
            physicsWorld.addItem(simpleTriggerZone);
            return simpleTriggerZone;
        }
        return null;
    }

    public static void handleTBoxMarker(SceneWorld sceneWorld, PhysicsWorld physicsWorld, GameResources globalGameResources, GameResources localGameResources, String id, AttributesContainer attributesContainer, TUserData renderContainer) {
    }

    public static void mapPostLoad(PhysicsWorld physicsWorld, SceneWorld sceneWorld) {
    }

    public static void mapPreLoad(PhysicsWorld physicsWorld, SceneWorld sceneWorld) {
    }
}
