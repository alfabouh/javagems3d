package ru.alfabouh.jgems3d.engine.system.resources.yml_loaders;

import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.fabric.render.data.RenderObjectData;
import ru.alfabouh.jgems3d.engine.system.exception.JGemsException;
import ru.alfabouh.jgems3d.engine.system.resources.yml_loaders.containers.YMLRenderEntityDataContainer;
import ru.alfabouh.jgems3d.engine.system.resources.yml_loaders.containers.YMLRenderObjectsContainer;
import ru.alfabouh.jgems3d.engine.system.yaml.YamlReader;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class YMLRenderObjects implements YMLReader<YMLRenderObjectsContainer> {
    @Override
    public YMLRenderObjectsContainer loadYAMLObject(InputStream inputStream) throws JGemsException {
        YMLRenderObjectsContainer ymlRenderDataContainer = new YMLRenderObjectsContainer();
        YamlReader yamlReader = new YamlReader(inputStream);
        if (yamlReader.isValid()) {
            try {
                List<Map<String, Object>> list = yamlReader.getObjects();
                for (Map<String, Object> obj : list) {
                    String name = (String) obj.get("name");
                    if (name == null || name.isEmpty()) {
                        continue;
                    }
                    String render_object_data = (String) obj.get("render_object_data");
                    if (render_object_data == null) {
                        throw new JGemsException("YAML data: " + name + " - NULL RENDER OBJECT DATA");
                    }
                    String mesh_data_group = (String) obj.get("mesh_data_group");
                    if (mesh_data_group == null) {
                        throw new JGemsException("YAML data: " + name + " - NULL MESH DATA GROUP");
                    }
                    String type = (String) obj.get("type");
                    if (type == null || type.isEmpty()) {
                        type = "entity";
                    }
                    ymlRenderDataContainer.addObject(name, render_object_data, mesh_data_group, type);
                }
            } catch (ClassCastException | NumberFormatException e) {
                throw new JGemsException(e);
            }
        }
        return ymlRenderDataContainer;
    }
}
