package api.scripting.coding.env.internal.game.init.events.ui;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingField;

@JSCodingClass(binding = "JSUiBehaviourTargets", description = "...")
public enum JSUiBehaviourTargets {
    @JSCodingField(description = "ON_CONSTRUCT") ON_CONSTRUCT,
    @JSCodingField(description = "ON_DESTRUCT") ON_DESTRUCT,
    @JSCodingField(description = "ON_WINDOW_RESIZED") ON_WINDOW_RESIZED,
    @JSCodingField(description = "ON_DRAW") ON_DRAW
}
