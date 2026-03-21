package api.scripting.coding.env.internal.util.resources.init;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingField;
import api.scripting.coding.env.def.JSHideFromDoc;
import javagems3d.system.resources.assets.initialization.base.IAssetsInitializer;

import java.awt.*;

@JSCodingClass(binding = "JSAssetsLoadPriority", description = "...")
public enum JSAssetsLoadPriority {
    @JSCodingField(description = "LOW", paramName = "LOW") LOW(IAssetsInitializer.LoadPriority.LOW.getPriority()),
    @JSCodingField(description = "NORMAL", paramName = "NORMAL") NORMAL(IAssetsInitializer.LoadPriority.NORMAL.getPriority()),
    @JSCodingField(description = "HIGH", paramName = "HIGH") HIGH(IAssetsInitializer.LoadPriority.HIGH.getPriority());

    private final int value;
    JSAssetsLoadPriority(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    @JSHideFromDoc
    @Override
    public String toString() {
        return super.toString();
    }
}
