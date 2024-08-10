package ru.jgems3d.engine.system.graph;

import org.joml.Vector3f;

import java.io.Serializable;

public class GraphVertex implements Serializable {
    private static final long serialVersionUID = -228L;
    private final float x;
    private final float y;
    private final float z;
    private float g;
    private float h;
    private float f;
    private GraphVertex parent;

    public GraphVertex(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.g = 0;
        this.f = 0;
    }

    public double distanceTo(GraphVertex vertex) {
        return new Vector3f(this.getX(), this.getY(), this.getZ()).distance(vertex.getX(), vertex.getY(), vertex.getZ());
    }

    public double distanceTo(Vector3f vector3f) {
        return new Vector3f(this.getX(), this.getY(), this.getZ()).distance(vector3f);
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

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public float getZ() {
        return this.z;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Double.hashCode(this.getX());
        result = prime * result + Double.hashCode(this.getY());
        result = prime * result + Double.hashCode(this.getZ());
        return result;
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
        return Double.doubleToLongBits(this.x) == Double.doubleToLongBits(vertex.x) && Double.doubleToLongBits(this.y) == Double.doubleToLongBits(vertex.y) && Double.doubleToLongBits(this.z) == Double.doubleToLongBits(vertex.z);
    }

    public String toString() {
        return this.getX() + " " + this.getY() + " " + this.getZ();
    }
}
