package api.scripting.coding.env.internal.util.events;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingField;
import api.scripting.coding.env.def.JSHideFromDoc;
import javagems3d.system.external.mapping.tags.base.ColorMode;

@JSCodingClass(binding = "JSEventState", description = "Event state enum.")
public enum JSEventState {
    @JSCodingField(description = "START") START,
    @JSCodingField(description = "END") END;
}