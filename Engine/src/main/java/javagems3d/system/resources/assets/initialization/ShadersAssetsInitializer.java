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

package javagems3d.system.resources.assets.initialization;

import javagems3d.JGems3D;
import javagems3d.global.JGemsGlobalConfiguration;
import javagems3d.graphics.environment.Environment;
import javagems3d.graphics.environment.lights.scene.LightsScene;
import javagems3d.graphics.rendering.programs.ssbo.ShaderStorageBufferProgram;
import javagems3d.system.resources.assets.initialization.base.ShadersInitializer;
import javagems3d.system.resources.assets.loading.models.utils.ModelLoadingUtils;
import javagems3d.system.resources.assets.shaders.base.ShadersContainer;
import javagems3d.system.resources.assets.shaders.buffers.ShaderStorageBufferObject;
import javagems3d.system.resources.assets.shaders.buffers.UniformBufferObject;
import javagems3d.system.resources.assets.shaders.libraries.ShaderLibrariesContainer;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.cache.ResourceCache;
import javagems3d.system.service.path.JGemsPath;

public final class ShadersAssetsInitializer extends ShadersInitializer<JGemsShaderManager> {
    public ShaderStorageBufferObject Bones;
    public ShaderStorageBufferObject IndirectBufferData;
    public ShaderStorageBufferObject BindlessTextures;
    public ShaderStorageBufferObject MaterialsData;
    public ShaderStorageBufferObject PropertiesData;

    public UniformBufferObject SunLight;
    public UniformBufferObject PointLights;
    public UniformBufferObject Misc;
    public UniformBufferObject Fog;

    public JGemsShaderManager world_pickable;
    public JGemsShaderManager menu;
    public JGemsShaderManager gameUbo;
    public JGemsShaderManager gui_text;
    public JGemsShaderManager gui_noised;
    public JGemsShaderManager gui_image;
    public JGemsShaderManager gui_button;
    public JGemsShaderManager gui_image_selectable;
    public JGemsShaderManager blur5;
    public JGemsShaderManager blur9;
    public JGemsShaderManager blur13;
    public JGemsShaderManager blur_box;
    public JGemsShaderManager blur_ssao;
    public JGemsShaderManager hdr;
    public JGemsShaderManager scene_gluing;
    public JGemsShaderManager fxaa;
    public JGemsShaderManager skybox;
    public JGemsShaderManager skybox_background;

    public JGemsShaderManager world_gbuffer;
    public JGemsShaderManager world_gbuffer_indirect;

    public JGemsShaderManager world_ssao;
    public JGemsShaderManager world_deferred;
    public JGemsShaderManager weighted_oit;
    public JGemsShaderManager weighted_particle_oit;
    public JGemsShaderManager weighted_liquid_oit;
    public JGemsShaderManager simple;

    public JGemsShaderManager depth_sun;
    public JGemsShaderManager depth_sun_indirect;

    public JGemsShaderManager depth_plight;

    public JGemsShaderManager debug;
    public JGemsShaderManager world_selected_gbuffer;
    public JGemsShaderManager inventory_common_item;
    public JGemsShaderManager imgui;

    protected void initObjects(ResourceCache resourceCache) {
        this.addShaderLibraryContainerInGlobalList(new ShaderLibrariesContainer(new JGemsPath("/assets/jgems/shaders/libs/shadows")));

        this.Bones = new ShaderStorageBufferObject(0, 16 * Float.BYTES * ModelLoadingUtils.ANIM_MAX_BONES);
        ShaderStorageBufferProgram.createSSBO(this.Bones);

        this.IndirectBufferData = new ShaderStorageBufferObject(1, 2 * (JGemsGlobalConfiguration.MAX_INDIRECT_RENDERING_MESH_DATASETS * Integer.BYTES) + (JGemsGlobalConfiguration.MAX_INDIRECT_RENDERING_MESH_DATASETS * 16 * Float.BYTES));
        ShaderStorageBufferProgram.createSSBO(this.IndirectBufferData);

        this.BindlessTextures = new ShaderStorageBufferObject(2, Long.BYTES * JGemsGlobalConfiguration.MAX_BINDLESS_TEXTURES);
        ShaderStorageBufferProgram.createSSBO(this.BindlessTextures);

        this.MaterialsData = new ShaderStorageBufferObject(3, Integer.BYTES * JGemsGlobalConfiguration.INDIRECT_RENDERING_MATERIALS_PACK_SIZE * JGemsGlobalConfiguration.MAX_INDIRECT_RENDERING_MESH_MATERIALS);
        ShaderStorageBufferProgram.createSSBO(this.MaterialsData);

        this.PropertiesData = new ShaderStorageBufferObject(4, Integer.BYTES * JGemsGlobalConfiguration.INDIRECT_RENDERING_PROPERTIES_PACK_SIZE * JGemsGlobalConfiguration.MAX_INDIRECT_RENDERING_MESH_PROPERTIES);
        ShaderStorageBufferProgram.createSSBO(this.PropertiesData);

        this.SunLight = this.createUBO("SunLight", 0, LightsScene.SN_STRUCT_SIZE * Float.BYTES);
        this.PointLights = this.createUBO("PointLights", 1, ((LightsScene.PL_STRUCT_SIZE * Float.BYTES) * JGemsGlobalConfiguration.MAX_POINT_LIGHTS) + Integer.BYTES);
        this.Misc = this.createUBO("Misc", 2, Float.BYTES);
        this.Fog = this.createUBO("Fog", 3, Environment.FOG_STRUCT_SIZE * Float.BYTES);

        this.world_pickable = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.Paths.SHADERS, "world/world_pickable"));
        this.debug = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.Paths.SHADERS, "debug"));
        this.gui_text = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.Paths.SHADERS, "gui/gui_text"));
        this.gui_noised = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.Paths.SHADERS, "gui/gui_noised"));

        this.gui_button = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.Paths.SHADERS, "gui/gui_button"));
        this.gui_image = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.Paths.SHADERS, "gui/gui_image"));
        this.gui_image_selectable = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.Paths.SHADERS, "gui/gui_image_selectable"));

        this.blur_ssao = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.Paths.SHADERS, "post/blur_ssao"));
        this.blur5 = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.Paths.SHADERS, "post/blur5"));
        this.blur9 = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.Paths.SHADERS, "post/blur9"));
        this.blur13 = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.Paths.SHADERS, "post/blur13"));
        this.blur_box = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.Paths.SHADERS, "post/blur_box"));

        this.imgui = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.Paths.SHADERS, "gui/imgui"));

        this.fxaa = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.Paths.SHADERS, "post/fxaa"));
        this.hdr = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.Paths.SHADERS, "post/hdr"));
        this.scene_gluing = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.Paths.SHADERS, "post/scene_gluing"));

        this.skybox = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.Paths.SHADERS, "world/skybox")).attachUBOs(this.SunLight);
        this.skybox_background = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.Paths.SHADERS, "world/skybox_background")).attachUBOs(this.SunLight, this.Fog);

        this.world_ssao = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.Paths.SHADERS, "post/screen_ssao"));

        this.weighted_liquid_oit = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.Paths.SHADERS, "oit/weighted_liquid_oit"));
        this.weighted_oit = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.Paths.SHADERS, "oit/weighted_oit"));
        this.weighted_particle_oit = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.Paths.SHADERS, "oit/weighted_particle_oit"));

        this.world_gbuffer = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.Paths.SHADERS, "world/world_gbuffer"));
        this.world_gbuffer_indirect = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.Paths.SHADERS, "world/world_gbuffer_indirect"));

        this.world_deferred = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.Paths.SHADERS, "world/world_deferred")).attachUBOs(this.SunLight, this.PointLights, this.Fog);

        this.menu = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.Paths.SHADERS, "gui/menu"));

        this.inventory_common_item = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.Paths.SHADERS, "inventory/inventory_common_item"));

        this.simple = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.Paths.SHADERS, "world/simple"));
        this.depth_sun = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.Paths.SHADERS, "shadows/depth_sun"));
        this.depth_sun_indirect = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.Paths.SHADERS, "shadows/depth_sun_indirect"));

        this.world_selected_gbuffer = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.Paths.SHADERS, "world/world_selected_gbuffer"));
        this.depth_plight = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.Paths.SHADERS, "shadows/depth_plight"));

        this.gameUbo = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.Paths.SHADERS, "gameubo")).attachUBOs(this.SunLight, this.Misc, this.PointLights, this.Fog);
    }

    @Override
    protected JGemsShaderManager createShaderObject(JGemsPath shaderPath) {
        return new JGemsShaderManager(new ShadersContainer(this.getGlobalShaderLibrary(), shaderPath));
    }
}