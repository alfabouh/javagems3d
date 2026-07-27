/*
 *
 *  * javagems3d
 *  * Copyright (C) 2026 gltexture
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package javagems3d.system.resources.assets.initialization;

import javagems3d.graphics.objects.rendering.attributes.JGemsRenderProperties;
import javagems3d.graphics.objects.rendering.attributes.RenderAttributes;
import javagems3d.graphics.objects.rendering.data.PropRenderData;
import javagems3d.graphics.objects.rendering.pipeline.RenderTable;
import javagems3d.system.resources.assets.initialization.base.IAssetsInitializer;
import javagems3d.system.resources.assets.materials.Material;
import javagems3d.system.resources.assets.models.mesh.RenderMesh;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode3D;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import org.joml.Vector3f;

import javagems3d.graphics.objects.rendering.data.EntityRenderData;
import javagems3d.graphics.objects.rendering.data.LiquidRenderData;
import javagems3d.physics.world.basic.WorldItem;
import javagems3d.system.resources.assets.models.helper.MeshHelper;
import javagems3d.graphics.objects.rendering.constructors.IModelConstructor;
import javagems3d.system.resources.managing.resources.SystemResources;
import javagems3d.system.resources.managing.JGemsResourceManager;

public class RenderDataInitializer implements IAssetsInitializer {
    public PropRenderData defaultPropIndirect;
    public PropRenderData defaultPropDirect;

    public EntityRenderData defaultEntityIndirect;
    public EntityRenderData defaultEntityDirect;

    public EntityRenderData entityCube;
    public EntityRenderData defaultPlayer;
    public EntityRenderData ground;
    public LiquidRenderData water;

    public RenderDataInitializer() {
    }

    public static void setDefaultRenderTableValues() {
        JGemsShaderManager DEFAULT_SCENE_SHADER = JGemsResourceManager.globalShaderAssets.world_gbuffer;
        JGemsShaderManager DEFAULT_BACKGROUND_SHADER = JGemsResourceManager.globalShaderAssets.background;
        JGemsShaderManager DEFAULT_SUN_L_SHADOW_MAP_SHADER = JGemsResourceManager.globalShaderAssets.depth_sun;
        JGemsShaderManager DEFAULT_POINT_L_SHADOW_MAP_SHADER = JGemsResourceManager.globalShaderAssets.depth_plight;
        JGemsShaderManager DEFAULT_SPOT_L_SHADOW_MAP_SHADER = JGemsResourceManager.globalShaderAssets.depth_slight;
        JGemsShaderManager DEFAULT_TRANSPARENCY_SHADER = JGemsResourceManager.globalShaderAssets.weighted_oit;

        JGemsShaderManager DEFAULT_SCENE_SHADER_IND = JGemsResourceManager.globalShaderAssets.world_gbuffer_indirect;
        JGemsShaderManager DEFAULT_BACKGROUND_SHADER_IND = JGemsResourceManager.globalShaderAssets.background_indirect;
        JGemsShaderManager DEFAULT_SUN_L_SHADOW_MAP_SHADER_IND = JGemsResourceManager.globalShaderAssets.depth_sun_indirect;
        JGemsShaderManager DEFAULT_POINT_L_SHADOW_MAP_SHADER_IND = JGemsResourceManager.globalShaderAssets.depth_plight_indirect;
        JGemsShaderManager DEFAULT_SPOT_L_SHADOW_MAP_SHADER_IND = JGemsResourceManager.globalShaderAssets.depth_slight_indirect;
        JGemsShaderManager DEFAULT_TRANSPARENCY_SHADER_IND = JGemsResourceManager.globalShaderAssets.weighted_oit_indirect;

        RenderTable.SET_DEFAULT_SHADERS_DIRECT(DEFAULT_SCENE_SHADER, DEFAULT_BACKGROUND_SHADER, DEFAULT_SUN_L_SHADOW_MAP_SHADER, DEFAULT_POINT_L_SHADOW_MAP_SHADER, DEFAULT_SPOT_L_SHADOW_MAP_SHADER, DEFAULT_TRANSPARENCY_SHADER);
        RenderTable.SET_DEFAULT_SHADERS_INDIRECT(DEFAULT_SCENE_SHADER_IND, DEFAULT_BACKGROUND_SHADER_IND, DEFAULT_SUN_L_SHADOW_MAP_SHADER_IND, DEFAULT_POINT_L_SHADOW_MAP_SHADER_IND, DEFAULT_SPOT_L_SHADOW_MAP_SHADER_IND, DEFAULT_TRANSPARENCY_SHADER_IND);
    }

    @Override
    public void load(SystemResources systemResources) {
        IModelConstructor<WorldItem, RenderMesh> itemPickUpModelConstructor = e -> {
            MeshGroup meshGroup = new MeshGroup(new MeshNode3D<>(MeshHelper.generateSimplePlane3DMesh(null, new Vector3f(-0.5f, -0.5f, 0.0f), new Vector3f(0.5f, -0.5f, 0.0f), new Vector3f(-0.5f, 0.5f, 0.0f), new Vector3f(0.5f, 0.5f, 0.0f))));
            return meshGroup;
        };

        //this.zippo_world.getObjectRenderSettings().setOverlappingMaterial(zwMat);

        final Material waterMat = new Material.Builder().diffuseMap(JGemsResourceManager.globalTextureAssets.waterTexture).normalsMap(JGemsResourceManager.globalTextureAssets.waterNormals).roughnessFactor(1.0f).metallicFactor(1.0f).build();
        this.water = new LiquidRenderData(waterMat.setOpacity(0.5f), JGemsResourceManager.globalShaderAssets.weighted_liquid_oit);
        this.entityCube = new EntityRenderData(EntityRenderData.defaultObjectConstructor(), new RenderAttributes(RenderTable.getIndirect(), JGemsRenderProperties.getDefault())).setMeshStructure(JGemsResourceManager.globalModelAssets.grassCube); //TODO
        this.defaultPlayer = new EntityRenderData(EntityRenderData.defaultObjectConstructor(), null);
        this.ground = new EntityRenderData(EntityRenderData.defaultObjectConstructor(), new RenderAttributes(RenderTable.getIndirect(), JGemsRenderProperties.getDefault()));

        this.defaultEntityIndirect = new EntityRenderData(RenderAttributes.getDefaultIndirect());
        this.defaultEntityDirect = new EntityRenderData(RenderAttributes.getDefaultDirect());
        this.defaultPropIndirect = new PropRenderData(RenderAttributes.getDefaultIndirect());
        this.defaultPropDirect = new PropRenderData(RenderAttributes.getDefaultDirect());
    }

    @Override
    public LaunchMode loadMode() {
        return LaunchMode.REGULAR;
    }

    @Override
    public LoadPriority loadPriority() {
        return LoadPriority.LOW;
    }
}
