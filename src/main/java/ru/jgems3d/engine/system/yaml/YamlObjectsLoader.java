package ru.jgems3d.engine.system.yaml;

import ru.jgems3d.exceptions.JGemsException;

import java.io.InputStream;

public interface YamlObjectsLoader<T> {
    T loadYAMLObject(InputStream inputStream) throws JGemsException;
}