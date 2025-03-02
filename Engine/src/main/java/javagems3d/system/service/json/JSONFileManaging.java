package javagems3d.system.service.json;

import com.google.gson.*;
import javagems3d.system.service.args.ArbitraryArguments;
import javagems3d.system.service.collections.Pair;
import javagems3d.system.service.exceptions.JGemsIOException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class JSONFileManaging {
    private final Map<Class<?>, SerializationRules<?>> rulesMap;

    protected JSONFileManaging() {
        this.rulesMap = new HashMap<>();
    }

    @SafeVarargs
    public static JSONFileManaging create(Pair<Class<?>, SerializationRules<?>>... pairs) {
        JSONFileManaging jsonFileManaging = new JSONFileManaging();
        for (Pair<Class<?>, SerializationRules<?>> pair : pairs) {
            jsonFileManaging.getRulesMap().put(pair.getFirst(), pair.getSecond());
        }
        return jsonFileManaging;
    }

    private Gson createGson(@Nullable ArbitraryArguments metaData) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        this.rulesMap.forEach((clazz, rules) -> {
            gsonBuilder.registerTypeAdapter(clazz, rules.getSerializer(metaData));
            gsonBuilder.registerTypeAdapter(clazz, rules.getDeserializer(metaData));
        });
        gsonBuilder.setPrettyPrinting();
        return gsonBuilder.create();
    }

    protected String write(@NotNull Object object, @Nullable ArbitraryArguments metaData) {
        Gson gson = this.createGson(metaData);
        return gson.toJson(object);
    }

    protected <T> T read(String jsonString, Class<T> clazz, @Nullable ArbitraryArguments metaData) throws JsonSyntaxException {
        Gson gson = createGson(metaData);
        return gson.fromJson(jsonString, clazz);
    }

    @SuppressWarnings("all")
    public void writeToFile(Object object, File file, @Nullable ArbitraryArguments metaData) throws JGemsIOException {
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
        } catch (IOException e) {
            throw new JGemsIOException(e);
        }
        try (Writer writer = new OutputStreamWriter(Files.newOutputStream(file.toPath()), StandardCharsets.UTF_8)) {
            writer.write(this.write(object, metaData));
        } catch (IOException e) {
            throw new JGemsIOException(e);
        }
    }

    public <T> T readFromFile(File file, Class<T> clazz, @Nullable ArbitraryArguments metaData) throws JGemsIOException, JsonSyntaxException {
        try (Reader reader = new InputStreamReader(Files.newInputStream(file.toPath()), StandardCharsets.UTF_8)) {
            String string = new BufferedReader(reader).lines().collect(Collectors.joining("\n"));
            return this.read(string, clazz, metaData);
        } catch (IOException | JsonSyntaxException e) {
            throw new JGemsIOException(e);
        }
    }

    public <T> void setMatch(@NotNull Class<T> clazz, @NotNull SerializationRules<T> serializationRules) {
        this.getRulesMap().put(clazz, serializationRules);
    }

    public void clear() {
        this.getRulesMap().clear();
    }

    public Map<Class<?>, SerializationRules<?>> getRulesMap() {
        return this.rulesMap;
    }

    public interface SerializationRules<T> {
        String write(T toWrite, Type typeOfSrc, JsonSerializationContext context, @Nullable ArbitraryArguments metaData) throws JGemsIOException;
        T read(String readString, Type typeOfT, JsonDeserializationContext context, @Nullable ArbitraryArguments metaData) throws JGemsIOException;

        default JsonSerializer<T> getSerializer(@Nullable ArbitraryArguments metaData) {
            return (src, typeOfSrc, context) -> context.serialize(write(src, typeOfSrc, context, metaData));
        }

        default JsonDeserializer<T> getDeserializer(@Nullable ArbitraryArguments metaData) {
            return (json, typeOfT, context) -> read(json.getAsString(), typeOfT, context, metaData);
        }
    }
}
