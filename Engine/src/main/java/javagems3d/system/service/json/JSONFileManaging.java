package javagems3d.system.service.json;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import javagems3d.mapping.tags.TagsContainer;
import javagems3d.system.service.args.ArbitraryArguments;
import javagems3d.system.service.collections.Pair;
import javagems3d.system.service.exceptions.JGemsIOException;
import logger.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class JSONFileManaging {
    public static final Set<Pair<Class<?>, SerializationRules<?>>> DEFAULT_SERIALIZATION_RULES = new HashSet<>();

    static {
        JSONFileManaging.DEFAULT_SERIALIZATION_RULES.add(new Pair<>(Vector3f.class, JSONFileManaging.createSerializationRules(
                (object, context) -> {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("x", object.x);
                    jsonObject.addProperty("y", object.y);
                    jsonObject.addProperty("z", object.z);
                    return jsonObject;
                },
                (json, context) -> {
                    JsonObject jsonObject = json.getAsJsonObject();
                    float x = jsonObject.get("x").getAsFloat();
                    float y = jsonObject.get("y").getAsFloat();
                    float z = jsonObject.get("z").getAsFloat();
                    return new Vector3f(x, y, z);
                }
        )));

        JSONFileManaging.DEFAULT_SERIALIZATION_RULES.add(new Pair<>(Vector4f.class, JSONFileManaging.createSerializationRules(
                (object, context) -> {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("x", object.x);
                    jsonObject.addProperty("y", object.y);
                    jsonObject.addProperty("z", object.z);
                    jsonObject.addProperty("w", object.w);
                    return jsonObject;
                },
                (json, context) -> {
                    JsonObject jsonObject = json.getAsJsonObject();
                    float x = jsonObject.get("x").getAsFloat();
                    float y = jsonObject.get("y").getAsFloat();
                    float z = jsonObject.get("z").getAsFloat();
                    float w = jsonObject.get("w").getAsFloat();
                    return new Vector4f(x, y, z, w);
                }
        )));
    }

    public void addDefaultSerializationRule(Pair<Class<?>, SerializationRules<?>> pair) {
        JSONFileManaging.DEFAULT_SERIALIZATION_RULES.add(pair);
    }

    private final Map<Class<?>, SerializationRules<?>> rulesMap;

    protected JSONFileManaging() {
        this.rulesMap = new HashMap<>();
        for (Pair<Class<?>, SerializationRules<?>> pair : JSONFileManaging.DEFAULT_SERIALIZATION_RULES) {
            this.getRulesMap().put(pair.getFirst(), pair.getSecond());
        }
    }

    @SafeVarargs
    public static JSONFileManaging createSerializationRules(Pair<Class<?>, SerializationRules<?>>... pairs) {
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
        return gsonBuilder.setPrettyPrinting().create();
    }

    public String write(@NotNull Object object, @Nullable ArbitraryArguments metaData) {
        Gson gson = this.createGson(metaData);
        return gson.toJson(object);
    }

    public JsonElement read(String jsonString) throws JsonSyntaxException {
        return JsonParser.parseString(jsonString);
    }

    public JsonElement read(InputStream stream) throws JsonSyntaxException {
        try (Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            String string = new BufferedReader(reader).lines().collect(Collectors.joining("\n"));
            return JsonParser.parseString(string);
        } catch (IOException | JsonSyntaxException e) {
            throw new JGemsIOException(e);
        }
    }

    public <T> T read(String jsonString, TypeToken<T> typeToken, @Nullable ArbitraryArguments metaData) throws JsonSyntaxException {
        Gson gson = this.createGson(metaData);
        return gson.fromJson(jsonString, typeToken);
    }

    public <T> T readFromInputStream(InputStream stream, TypeToken<T> typeToken, @Nullable ArbitraryArguments metaData) throws JsonSyntaxException {
        try (Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            String string = new BufferedReader(reader).lines().collect(Collectors.joining("\n"));
            return this.read(string, typeToken, metaData);
        } catch (IOException | JsonSyntaxException e) {
            throw new JGemsIOException(e);
        }
    }

    public <T> T readFromFile(File file, TypeToken<T> typeToken, @Nullable ArbitraryArguments metaData) throws JGemsIOException, JsonSyntaxException {
        try {
            return this.readFromInputStream(Files.newInputStream(file.toPath()), typeToken, metaData);
        } catch (IOException e) {
            throw new JGemsIOException(e);
        }
    }

    public String getString(Object object, @Nullable ArbitraryArguments metaData) {
        return this.write(object, metaData);
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
            Log.get().debug("Wrote file: " + file.getAbsolutePath());
        } catch (IOException e) {
            throw new JGemsIOException(e);
        }
    }

    public void setMatchUnsafe(@NotNull Class<?> clazz, @NotNull SerializationRules<?> serializationRules) {
        this.getRulesMap().put(clazz, serializationRules);
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
        JsonElement write(T toWrite, Type typeOfSrc, JsonSerializationContext context, @Nullable ArbitraryArguments metaData) throws JGemsIOException;
        T read(JsonElement jsonObject, Type typeOfT, JsonDeserializationContext context, @Nullable ArbitraryArguments metaData) throws JGemsIOException;

        default JsonSerializer<T> getSerializer(@Nullable ArbitraryArguments metaData) {
            return (src, typeOfSrc, context) -> context.serialize(this.write(src, typeOfSrc, context, metaData));
        }

        default JsonDeserializer<T> getDeserializer(@Nullable ArbitraryArguments metaData) {
            return (json, typeOfT, context) -> this.read(json, typeOfT, context, metaData);
        }
    }

    public static <E> SerializationRules<E> createSerializationRules(Serializer<E> serializer, Deserializer<E> deserializer) {
        return new SerializationRules<E>() {

            @Override
            public JsonElement write(E toWrite, Type typeOfSrc, JsonSerializationContext context, ArbitraryArguments metaData) throws JGemsIOException {
                return serializer.serialize(toWrite, context);
            }

            @Override
            public E read(JsonElement jsonObject, Type typeOfT, JsonDeserializationContext context, ArbitraryArguments metaData) throws JGemsIOException {
                return deserializer.deserialize(jsonObject, context);
            }

            @Override
            public JsonSerializer<E> getSerializer(ArbitraryArguments metaData) {
                return (src, typeOfSrc, context) -> context.serialize(write(src, typeOfSrc, context, metaData));
            }

            @Override
            public JsonDeserializer<E> getDeserializer(ArbitraryArguments metaData) {
                return (json, typeOfT, context) -> this.read(json, typeOfT, context, metaData);
            }
        };
    }

    public interface Serializer<E> {
        JsonElement serialize(E object, JsonSerializationContext context);
    }

    public interface Deserializer<E> {
        E deserialize(JsonElement json, JsonDeserializationContext context);
    }
}
