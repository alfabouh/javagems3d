package ru.jgems3d.toolbox.map_sys;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import ru.jgems3d.exceptions.JGemsException;

import java.io.*;

public final class SerializeHelper {
    private static String convert(InputStream inputStream) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        String line;

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        }

        return stringBuilder.toString();
    }

    public static <T> T readFromJSON(InputStream inputStream, Class<T> tClass) throws IOException {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        return gson.fromJson(SerializeHelper.convert(inputStream), tClass);
    }

    public static <T> T readFromBytes(InputStream inputStream, Class<T> tClass) throws IOException, ClassNotFoundException {
        try (ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {
            Object obj = objectInputStream.readObject();
            if (tClass.isInstance(obj)) {
                @SuppressWarnings("unchecked")
                T castedObj = (T) obj;
                return castedObj;
            } else {
                throw new JGemsException("Deserialized object has another type: " + tClass.getName());
            }
        }
    }

    public static <T> T readFromJSON(File file, String fileName, Class<T> tClass) throws IOException {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        try (FileReader reader = new FileReader(file + "//" + fileName)) {
            return gson.fromJson(reader, tClass);
        }
    }

    public static <T> T readFromJSON(File file, String fileName, TypeToken<T> tTypeToken) throws IOException {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        try (FileReader reader = new FileReader(file + "//" + fileName)) {
            return gson.fromJson(reader, tTypeToken);
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
                    throw new JGemsException("Deserialized object has another type: " + tClass.getName());
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
        try (FileOutputStream fileOutputStream = new FileOutputStream(path + "//" + fileName)) {
            try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
                objectOutputStream.writeObject(object);
            }
        }
    }
}
