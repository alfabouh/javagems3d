package ru.alfabouh.jgems3d.engine.system.resources.yml_loaders;

import ru.alfabouh.jgems3d.engine.system.exception.JGemsException;

import java.io.InputStream;

public interface YMLReader<T> {
    T loadYAMLObject(InputStream inputStream) throws JGemsException;
}
