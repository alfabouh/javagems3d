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

package javagems3d.toolbox.resources.shaders;

import javagems3d.engine.system.resources.assets.loaders.base.ShadersLoader;
import javagems3d.engine.system.resources.assets.shaders.RenderPass;
import javagems3d.engine.system.resources.assets.shaders.ShaderContainer;
import javagems3d.engine.system.resources.cache.ResourceCache;
import javagems3d.engine.system.service.path.JGemsPath;
import javagems3d.toolbox.resources.shaders.manager.TBoxShaderManager;

public final class ShaderResources extends ShadersLoader<TBoxShaderManager> {
    public TBoxShaderManager world_transparent_color;
    public TBoxShaderManager world_isometric_object;
    public TBoxShaderManager world_lines;
    public TBoxShaderManager world_xyz;
    public TBoxShaderManager world_object;
    public TBoxShaderManager world_object_nolight;
    public TBoxShaderManager imgui;
    public TBoxShaderManager scene_gluing;

    protected void initObjects(ResourceCache resourceCache) {
        this.world_transparent_color = this.createShaderManager(resourceCache, new JGemsPath("/assets/toolbox/shaders/world_transparent_color")).setShaderRenderPass(RenderPass.TRANSPARENCY);
        this.world_isometric_object = this.createShaderManager(resourceCache, new JGemsPath("/assets/toolbox/shaders/world_isometric_object"));
        this.world_object = this.createShaderManager(resourceCache, new JGemsPath("/assets/toolbox/shaders/world_object"));
        this.world_object_nolight = this.createShaderManager(resourceCache, new JGemsPath("/assets/toolbox/shaders/world_object_nolight"));
        this.world_lines = this.createShaderManager(resourceCache, new JGemsPath("/assets/toolbox/shaders/world_lines"));
        this.world_xyz = this.createShaderManager(resourceCache, new JGemsPath("/assets/toolbox/shaders/world_xyz"));
        this.imgui = this.createShaderManager(resourceCache, new JGemsPath("/assets/toolbox/shaders/imgui"));
        this.scene_gluing = this.createShaderManager(resourceCache, new JGemsPath("/assets/toolbox/shaders/scene_gluing"));
    }

    @Override
    public TBoxShaderManager createShaderObject(JGemsPath shaderPath) {
        return new TBoxShaderManager(new ShaderContainer(shaderPath));
    }
}