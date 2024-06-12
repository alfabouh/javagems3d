package ru.alfabouh.jgems3d.engine.render.opengl.scene.objects;

import ru.alfabouh.jgems3d.engine.render.opengl.scene.fabric.render.base.IRenderFabric;

public interface IRenderObject {
    IRenderFabric renderFabric();

    boolean hasRender();
}
