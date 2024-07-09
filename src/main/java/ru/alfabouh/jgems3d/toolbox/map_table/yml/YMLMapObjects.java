package ru.alfabouh.jgems3d.toolbox.map_table.yml;

import ru.alfabouh.jgems3d.engine.system.exception.JGemsException;
import ru.alfabouh.jgems3d.engine.system.resources.yml_loaders.YMLReader;
import ru.alfabouh.jgems3d.engine.system.yaml.YamlReader;
import ru.alfabouh.jgems3d.logger.SystemLogging;
import ru.alfabouh.jgems3d.toolbox.map_table.object.ObjectType;
import ru.alfabouh.jgems3d.toolbox.map_table.yml.containers.YMLMapObjectsContainer;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class YMLMapObjects implements YMLReader<YMLMapObjectsContainer> {
    @Override
    public YMLMapObjectsContainer loadYAMLObject(InputStream inputStream) throws JGemsException {
        YMLMapObjectsContainer ymlRenderDataContainer = new YMLMapObjectsContainer();
        YamlReader yamlReader = new YamlReader(inputStream);
        if (yamlReader.isValid()) {
            try {
                List<Map<String, Object>> list = yamlReader.getObjects();
                for (Map<String, Object> obj : list) {
                    String name = (String) obj.get("name");
                    if (name == null || name.isEmpty()) {
                        continue;
                    }
                    String mesh_data_group = (String) obj.get("mesh_data_group");
                    if (mesh_data_group == null) {
                        SystemLogging.get().getLogManager().warn("YAML data: " + name + " - NULL MESH DATA GROUP");
                        continue;
                    }
                    String type = (String) obj.get("type");
                    if (type == null || type.isEmpty()) {
                        type = "";
                    }
                    ObjectType objectType;
                    switch (type) {
                        default:
                        case "entity": {
                            objectType = ObjectType.PHYSICS_OBJECT;
                            break;
                        }
                        case "model": {
                            objectType = ObjectType.PROP_OBJECT;
                            break;
                        }
                    }
                    ymlRenderDataContainer.addObject(name, mesh_data_group, objectType);
                }
            } catch (ClassCastException | NumberFormatException e) {
                throw new JGemsException(e);
            }
        }
        return ymlRenderDataContainer;
    }
}
