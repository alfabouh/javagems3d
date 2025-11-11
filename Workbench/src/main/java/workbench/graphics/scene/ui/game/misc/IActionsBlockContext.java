package workbench.graphics.scene.ui.game.misc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import workbench.graphics.scene.ui.game.GameEditorInterface;

public interface IActionsBlockContext<T> {
    void onRender(@Nullable T t, @NotNull GameEditorInterface gameEditorInterface);
}
