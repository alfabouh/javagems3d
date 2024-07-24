package ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.items;

import ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.fabric.objects.IRenderObjectFabric;

public interface IRenderObject {
    IRenderObjectFabric renderFabric();

    default boolean hasRender() {
        return this.renderFabric() != null;
    }
}
