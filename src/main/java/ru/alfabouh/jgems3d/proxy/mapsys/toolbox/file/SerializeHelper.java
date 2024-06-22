package ru.alfabouh.jgems3d.proxy.mapsys.toolbox.file;

import com.google.gson.*;
import org.joml.Vector3d;
import ru.alfabouh.jgems3d.proxy.mapsys.toolbox.file.save.objects.SaveObject;
import ru.alfabouh.jgems3d.proxy.mapsys.toolbox.table.object.attributes.AttributeContainer;

import java.io.*;
import java.lang.reflect.Type;

public final class SerializeHelper {
    public static <T> T readFromJSON(File file, String fileName, Class<T> tClass) throws IOException {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(SaveObject.class, new SaveObjectDeserializer());
        Gson gson = gsonBuilder.create();
        try (FileReader reader = new FileReader(file + "//" + fileName)) {
            return (T) gson.fromJson(reader, tClass);
        }
    }

    public static <T> T readFromBytes(File file, String fileName, Class<T> tClass) throws IOException, ClassNotFoundException {
        try (FileInputStream fileInputStream = new FileInputStream(file + "//" + fileName)) {
            try (ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
                Object obj = objectInputStream.readObject();
                if (tClass.isInstance(obj)) {
                    @SuppressWarnings("unchecked")
                    T castedObj = (T) obj;
                    return castedObj;
                } else {
                    throw new ClassCastException("Deserialized object has another type: " + tClass.getName());
                }
            }
        }
    }

    public static void saveToJSON(File path, String fileName, Object object) throws IOException {
        Gson gson = new Gson();
        String jsonString = gson.toJson(object);
        try (FileWriter fileWriter = new FileWriter(path + "//" + fileName)) {
            fileWriter.write(jsonString);
        }
    }

    public static void saveToBytes(File path, String fileName, Object object) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(path + "//" + fileName)){
            try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
                objectOutputStream.writeObject(object);
            }
        }
    }

    private static class SaveObjectDeserializer implements JsonDeserializer<SaveObject> {
        @Override
        public SaveObject deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();

            String objectId = jsonObject.get("objectId").getAsString();
            Vector3d position = context.deserialize(jsonObject.get("position"), Vector3d.class);
            Vector3d rotation = context.deserialize(jsonObject.get("rotation"), Vector3d.class);
            Vector3d scaling = context.deserialize(jsonObject.get("scaling"), Vector3d.class);
            AttributeContainer attributeContainer = context.deserialize(jsonObject.get("attributeContainer"), AttributeContainer.class);

            return new SaveObject(attributeContainer, objectId, position, rotation, scaling);
        }
    }
}
