package ru.BouH.engine.render.scene.objects;

import ru.BouH.engine.render.scene.fabric.physics.base.IRenderFabric;

public interface IRenderObject {
    IRenderFabric renderFabric();

    boolean hasRender();
}
