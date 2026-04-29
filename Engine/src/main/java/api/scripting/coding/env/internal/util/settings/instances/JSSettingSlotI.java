package api.scripting.coding.env.internal.util.settings.instances;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSHideFromDoc;
import javagems3d.system.settings.objects.SettingObject;
import javagems3d.system.settings.objects.SettingSlot;

@JSCodingClass(binding = "JSSettingSlotI", description = "Interface for a setting slot.")
public interface JSSettingSlotI {
    @JSHideFromDoc
    SettingSlot getJavaSettingSlot();
}