package ru.alfabouh.jgems3d.engine.graphics.opengl.scene.objects;

import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.fabric.render.base.IRenderFabric;

public interface IRenderObject {
    IRenderFabric renderFabric();

    boolean hasRender();
}
