package ru.alfabouh.engine.game.resources.assets.models.formats;

public interface IFormat {
    IFormat copy();

    boolean isOrientedToViewMatrix();
}