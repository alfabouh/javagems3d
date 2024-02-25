package ru.BouH.engine.physics.world.timer;

import org.bytedeco.bullet.BulletCollision.btBroadphaseProxy;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.annotation.Cast;
import org.bytedeco.javacpp.annotation.Properties;
import org.bytedeco.javacpp.annotation.Virtual;

@Properties(inherit = org.bytedeco.bullet.presets.BulletCollision.class)
public class CollisionFilter extends Pointer {
    static {
        Loader.load();
    }

    public CollisionFilter(Pointer p) {
        super(p);
    }

    @Virtual
    public native @Cast("bool") boolean needBroadphaseCollision(btBroadphaseProxy proxy0, btBroadphaseProxy proxy1);
}
