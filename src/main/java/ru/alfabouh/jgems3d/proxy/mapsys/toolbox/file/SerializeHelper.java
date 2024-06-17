package ru.alfabouh.jgems3d.proxy.mapsys.toolbox.file;

import com.google.gson.Gson;
import ru.alfabouh.jgems3d.proxy.logger.managers.LoggingManager;

import java.io.*;

public final class SerializeHelper {
    public static <T> T readFromJSON(File file, String fileName, Class<T> tClass) throws IOException {
        Gson gson = new Gson();
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
}
