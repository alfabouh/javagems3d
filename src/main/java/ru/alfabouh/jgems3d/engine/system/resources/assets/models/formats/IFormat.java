package ru.alfabouh.jgems3d.engine.system.resources.assets.models.formats;

public interface IFormat {
    IFormat copy();

    boolean isOrientedToViewMatrix();
}
