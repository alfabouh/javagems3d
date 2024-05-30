package ru.alfabouh.engine.system.resources.assets.models.formats;

public interface IFormat {
    IFormat copy();

    boolean isOrientedToViewMatrix();
}
