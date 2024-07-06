package ru.alfabouh.jgems3d.engine.system.resources.yml_loaders;

import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.fabric.render.base.IRenderFabric;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.fabric.render.data.RenderObjectData;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.objects.items.AbstractSceneItemObject;
import ru.alfabouh.jgems3d.engine.system.exception.JGemsException;
import ru.alfabouh.jgems3d.engine.system.resources.assets.shaders.manager.JGemsShaderManager;
import ru.alfabouh.jgems3d.engine.system.resources.manager.JGemsResourceManager;
import ru.alfabouh.jgems3d.engine.system.resources.yml_loaders.containers.YMLRenderEntityDataContainer;
import ru.alfabouh.jgems3d.engine.system.yaml.YamlReader;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class YMLRenderEntityData implements YMLReader<YMLRenderEntityDataContainer> {

    @SuppressWarnings("unchecked")
    @Override
    public YMLRenderEntityDataContainer loadYAMLObject(InputStream inputStream) throws JGemsException {
        YMLRenderEntityDataContainer ymlRenderDataContainer = new YMLRenderEntityDataContainer();
        YamlReader yamlReader = new YamlReader(inputStream);
        if (yamlReader.isValid()) {
            try {
                List<Map<String, Object>> list = yamlReader.getObjects();
                for (Map<String, Object> obj : list) {
                    String name = (String) obj.get("name");
                    if (name == null || name.isEmpty()) {
                        continue;
                    }
                    String shader_name = (String) obj.get("shader_name");
                    if (shader_name == null || shader_name.isEmpty()) {
                        throw new JGemsException("YAML data: " + name + " - NULL SHADER");
                    }
                    String render_fabric = (String) obj.get("render_fabric");
                    if (render_fabric == null || render_fabric.isEmpty()) {
                        throw new JGemsException("YAML data: " + name + " - NULL RENDER FABRIC");
                    }
                    String render_object = (String) obj.get("render_object");
                    if (render_object == null || render_object.isEmpty()) {
                        throw new JGemsException("YAML data: " + name + " - NULL RENDER OBJECT");
                    }
                    Class<? extends IRenderFabric> clazz1;
                    Class<? extends IRenderFabric> clazz2;
                    try {
                        clazz1 = (Class<? extends IRenderFabric>) Class.forName("ru.alfabouh.jgems3d.engine.graphics.opengl.scene.fabric.render.objects." + render_fabric);
                        clazz2 = (Class<? extends IRenderFabric>) Class.forName("ru.alfabouh.jgems3d.engine.graphics.opengl.scene.objects.items." + render_object);
                    } catch (ClassNotFoundException e) {
                        throw new JGemsException(e);
                    }
                    IRenderFabric renderFabric = null;
                    try {
                        renderFabric = clazz1.newInstance();
                    } catch (InstantiationException | IllegalAccessException e) {
                        throw new JGemsException(e);
                    }
                    RenderObjectData renderObjectData = new RenderObjectData(renderFabric, (Class<? extends AbstractSceneItemObject>) clazz2, (JGemsShaderManager) JGemsResourceManager.getGlobalGameResources().getResource(shader_name));

                    Map<String, Object> mesh_render_params = (Map<String, Object>) obj.get("mesh_render_params");

                    if (mesh_render_params != null) {
                        Boolean shadow_caster = (Boolean) mesh_render_params.get("shadow_caster");
                        if (shadow_caster != null) {
                            renderObjectData.getModelRenderParams().setShadowCaster(shadow_caster);
                        }

                        Boolean has_transparency = (Boolean) mesh_render_params.get("has_transparency");
                        if (has_transparency != null) {
                            renderObjectData.getModelRenderParams().setHasTransparency(has_transparency);
                        }

                        Boolean light_opaque = (Boolean) mesh_render_params.get("light_opaque");
                        if (light_opaque != null) {
                            renderObjectData.getModelRenderParams().setLightOpaque(light_opaque);
                        }

                        Boolean is_bright = (Boolean) mesh_render_params.get("is_bright");
                        if (is_bright != null) {
                            renderObjectData.getModelRenderParams().setBright(is_bright);
                        }

                        String render_distance = (String) mesh_render_params.get("render_distance");
                        if (render_distance != null) {
                            renderObjectData.getModelRenderParams().setRenderDistance(Float.parseFloat(render_distance));
                        }

                        String alpha_discard = (String) mesh_render_params.get("alpha_discard");
                        if (alpha_discard != null) {
                            renderObjectData.getModelRenderParams().setAlphaDiscard(Float.parseFloat(alpha_discard));
                        }

                        Boolean interpolate_movement = (Boolean) mesh_render_params.get("interpolate_movement");
                        if (interpolate_movement != null) {
                            renderObjectData.getModelRenderParams().setShouldInterpolateMovement(interpolate_movement);
                        }
                    }

                    ymlRenderDataContainer.addObject(name, renderObjectData);
                }
            } catch (ClassCastException | NumberFormatException e) {
                throw new JGemsException(e);
            }
        }
        return ymlRenderDataContainer;
    }
}
