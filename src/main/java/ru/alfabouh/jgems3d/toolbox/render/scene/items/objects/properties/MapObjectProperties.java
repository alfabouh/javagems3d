package ru.alfabouh.jgems3d.toolbox.render.scene.items.objects.properties;

import java.io.Serializable;

public class MapObjectProperties implements Serializable {
    private static final long serialVersionUID = -228L;
    private boolean isPhysicsObject;
    private boolean isStatic;
    private boolean generateMeshCollision;

    public MapObjectProperties() {
        this.isPhysicsObject = false;
        this.isStatic = true;
        this.generateMeshCollision = false;
    }

    public void setStatic(boolean aStatic) {
        isStatic = aStatic;
    }

    public void setPhysicsObject(boolean physicsObject) {
        isPhysicsObject = physicsObject;
    }

    public void setGenerateMeshCollision(boolean generateMeshCollision) {
        this.generateMeshCollision = generateMeshCollision;
    }

    public boolean isPhysicsObject() {
        return this.isPhysicsObject;
    }

    public boolean isGenerateMeshCollision() {
        return this.generateMeshCollision;
    }

    public boolean isStatic() {
        return this.isStatic;
    }
}
