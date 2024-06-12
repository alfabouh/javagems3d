package ru.alfabouh.jgems3d.engine.system.resources.assets.shaders.loader;

public interface IShaderLoader {
    void reloadShaders();
    void loadShaders();
    void destroyShaders();
    void startShaders();
}
