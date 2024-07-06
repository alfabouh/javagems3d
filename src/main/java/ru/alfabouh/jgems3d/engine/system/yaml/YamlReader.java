package ru.alfabouh.jgems3d.engine.system.yaml;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import ru.alfabouh.jgems3d.logger.SystemLogging;

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
            SystemLogging.get().getLogManager().warn("Couldn't read NULL yaml bytes");
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
