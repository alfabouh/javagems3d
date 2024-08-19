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

package ru.jgems3d.toolbox.map_table;

import org.joml.Vector3f;
import ru.jgems3d.engine.JGems3D;
import ru.jgems3d.engine.api_bridge.APIContainer;
import ru.jgems3d.engine.system.service.collections.Pair;
import ru.jgems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;
import ru.jgems3d.engine_api.app.tbox.AppTBoxObjectsContainer;
import ru.jgems3d.engine_api.app.tbox.containers.TEntityContainer;
import ru.jgems3d.engine_api.app.tbox.containers.TRenderContainer;
import ru.jgems3d.toolbox.map_table.object.AbstractObjectData;
import ru.jgems3d.toolbox.map_table.object.MarkerObjectData;
import ru.jgems3d.toolbox.map_table.object.ObjectCategory;
import ru.jgems3d.toolbox.map_sys.save.objects.object_attributes.Attribute;
import ru.jgems3d.toolbox.map_sys.save.objects.object_attributes.AttributesContainer;
import ru.jgems3d.toolbox.map_sys.save.objects.object_attributes.AttributeTarget;
import ru.jgems3d.toolbox.map_sys.save.objects.object_attributes.AttributeID;
import ru.jgems3d.toolbox.ToolBox;
import ru.jgems3d.toolbox.resources.TBoxResourceManager;
import ru.jgems3d.toolbox.resources.shaders.manager.TBoxShaderManager;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ObjectsTable {
    private final Map<String, AbstractObjectData> objects;

    public ObjectsTable() {
        this.objects = new HashMap<>();
    }

    public void init(TBoxResourceManager tBoxResourceManager) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        APIContainer.get().getApiTBoxInfo().getAppInstance().fillTBoxObjectsContainer(APIContainer.get().getAppTBoxObjectsContainer());

        AppTBoxObjectsContainer appTBoxObjectsContainer = APIContainer.get().getAppTBoxObjectsContainer();
        Attribute<Float> soundVolume = new Attribute<>(AttributeTarget.FLOAT_0_50, AttributeID.SOUND_VOL, 1.0f);
        Attribute<Float> soundPitch = new Attribute<>(AttributeTarget.FLOAT_0_50, AttributeID.SOUND_PITCH, 1.0f);
        Attribute<Float> soundRollOff = new Attribute<>(AttributeTarget.FLOAT_0_50, AttributeID.SOUND_ROLL_OFF, 1.0f);
        Attribute<String> soundAttribute = new Attribute<>(AttributeTarget.STRING, AttributeID.SOUND, JGems3D.Paths.SOUNDS);
        Attribute<Vector3f> transformPosXYZ = new Attribute<>(AttributeTarget.POSITION_XYZ, AttributeID.POSITION_XYZ, new Vector3f(0.0f));
        Attribute<Vector3f> rotationPlayerAttribute = new Attribute<>(AttributeTarget.ROTATION_Y, AttributeID.ROTATION_XYZ, new Vector3f(0.0f, (float) Math.toRadians(90.0f), 0.0f));
        Attribute<Vector3f> colorPlayerStaticAttribute = new Attribute<>(AttributeTarget.STATIC_NO_EDIT, AttributeID.COLOR, new Vector3f(1.0f, 0.0f, 0.0f));
        Attribute<Vector3f> colorAttributeSound = new Attribute<>(AttributeTarget.STATIC_NO_EDIT, AttributeID.COLOR, new Vector3f(0.0f, 1.0f, 0.0f));
        Attribute<Vector3f> colorAttribute = new Attribute<>(AttributeTarget.COLOR3, AttributeID.COLOR, new Vector3f(1.0f));

        Set<Map.Entry<String, Pair<TEntityContainer, TRenderContainer>>> pairEntry = appTBoxObjectsContainer.getMap().entrySet();
        for (Map.Entry<String, Pair<TEntityContainer, TRenderContainer>> entry : pairEntry) {
            TEntityContainer tEntityContainer = entry.getValue().getFirst();
            ObjectCategory objectCategory = tEntityContainer.getObjectCategory();
            TBoxShaderManager shaderManager = (tEntityContainer.getPathToTBoxShader() == null) ? (TBoxResourceManager.shaderAssets.world_object) : tBoxResourceManager.createShaderManager(tEntityContainer.getPathToTBoxShader());
            AttributesContainer attributesContainer = tEntityContainer.getAttributeContainer();
            MeshDataGroup meshDataGroup = TBoxResourceManager.createModel(tEntityContainer.getPathToTBoxModel());

            AbstractObjectData abstractObjectData = tEntityContainer.getAbstractObjectDataClass().getConstructor(AttributesContainer.class, TBoxShaderManager.class, MeshDataGroup.class, ObjectCategory.class).newInstance(attributesContainer, shaderManager, meshDataGroup, objectCategory);
            this.addObject(entry.getKey(), abstractObjectData);
        }

        this.addObject("player_start", new MarkerObjectData(new AttributesContainer(transformPosXYZ, rotationPlayerAttribute, colorPlayerStaticAttribute), TBoxResourceManager.shaderAssets.world_object, ToolBox.get().getResourceManager().getModelResources().player, ObjectCategory.GENERIC));
        this.addObject("generic_marker", new MarkerObjectData(new AttributesContainer(transformPosXYZ, colorAttribute), TBoxResourceManager.shaderAssets.world_object, ToolBox.get().getResourceManager().getModelResources().pointer, ObjectCategory.GENERIC));
        this.addObject("ambient_sound", new MarkerObjectData(new AttributesContainer(transformPosXYZ, soundVolume, soundPitch, soundRollOff, soundAttribute, colorAttributeSound), TBoxResourceManager.shaderAssets.world_object, ToolBox.get().getResourceManager().getModelResources().cubic, ObjectCategory.GENERIC));
    }

    public void addObject(String key, AbstractObjectData mapObject) {
        this.getObjects().put(key, mapObject);
    }

    public Map<String, AbstractObjectData> getObjects() {
        return this.objects;
    }
}