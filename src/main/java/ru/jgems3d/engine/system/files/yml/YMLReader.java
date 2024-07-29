package ru.jgems3d.engine.system.files.yml;

import ru.jgems3d.exceptions.JGemsException;

import java.io.InputStream;

public interface YMLReader<T> {
    T loadYAMLObject(InputStream inputStream) throws JGemsException;
}
