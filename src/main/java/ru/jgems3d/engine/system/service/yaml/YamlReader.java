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

package ru.jgems3d.engine.system.service.yaml;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import ru.jgems3d.engine.JGemsHelper;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class YamlReader {
    private final Map<String, Object> data;

    public YamlReader(InputStream inputStream) {
        if (inputStream != null) {
            LoaderOptions loaderOptions = new LoaderOptions();
            loaderOptions.setAllowDuplicateKeys(false);
            Yaml yaml = new Yaml(new Constructor(Map.class, loaderOptions));
            this.data = yaml.load(inputStream);
        } else {
            JGemsHelper.getLogger().warn("Couldn't read NULL yaml bytes");
            this.data = null;
        }
    }

    public boolean isValid() {
        return this.data != null;
    }

    public Object getValue(String key) {
        return this.getData().get(key);
    }

    @SuppressWarnings("all")
    public Map<String, Object> getMap(String key) {
        return (Map<String, Object>) data.get(key);
    }

    @SuppressWarnings("all")
    public List<Map<String, Object>> getObjects() {
        return (List<Map<String, Object>>) data.get("objects");
    }

    public Map<String, Object> getData() {
        return this.data;
    }
}
