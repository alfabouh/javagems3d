package ru.jgems3d.engine_api.app;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface JGemsGameEntry {
    @NotNull String gameTitle();
    @NotNull String gameVersion();
    @NotNull DevStage devStage();

    enum DevStage {
        PRE_ALPHA,
        ALPHA,
        PRE_BETA,
        BETA,
        PRE_RELEASE,
        RELEASE
    }
}
