package api.scripting.coding.env.internal.game.init.events.ui;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingField;

@JSCodingClass(binding = "JSUiBehaviourTargets", description = "Enumeration of possible UI behaviour targets for panel callbacks.")
public enum JSUiBehaviourTargets {
    @JSCodingField(description = "Triggered when the UI panel is constructed.") ON_CONSTRUCT,
    @JSCodingField(description = "Triggered when the UI panel is destructed.") ON_DESTRUCT,
    @JSCodingField(description = "Triggered when the window containing the UI panel is resized.") ON_WINDOW_RESIZED,
    @JSCodingField(description = "Triggered every frame to draw the UI panel.") ON_DRAW
}
