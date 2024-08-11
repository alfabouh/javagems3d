package ru.jgems3d.engine.system.graph;

import org.joml.Vector3f;

import java.io.Serializable;

public class GraphVertex implements Serializable {
    private static final long serialVersionUID = -228L;
    private final Vector3f position;
    private float g;
    private float h;
    private float f;
    private GraphVertex parent;

    public GraphVertex(Vector3f position) {
        this.position = position;
        this.g = 0;
        this.f = 0;
    }

    public double distanceTo(GraphVertex vertex) {
        return this.getPosition().distance(vertex.getPosition());
    }

    public double distanceTo(Vector3f vector3f) {
        return this.getPosition().distance(vector3f);
    }

    public float getG() {
        return this.g;
    }

    public void setG(float g) {
        this.g = g;
    }

    public float getH() {
        return this.h;
    }

    public void setH(float h) {
        this.h = h;
    }

    public double getF() {
        return this.f;
    }

    public void setF(float f) {
        this.f = f;
    }

    public GraphVertex getParent() {
        return this.parent;
    }

    public void setParent(GraphVertex parent) {
        this.parent = parent;
    }

    public Vector3f getPosition() {
        return this.position;
    }

    @Override
    public int hashCode() {
        return this.getPosition().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GraphVertex)) {
            return false;
        }
        GraphVertex vertex = (GraphVertex) o;
        return vertex.getPosition().equals(this.getPosition());
    }

    public String toString() {
        return this.getPosition().toString();
    }
}
