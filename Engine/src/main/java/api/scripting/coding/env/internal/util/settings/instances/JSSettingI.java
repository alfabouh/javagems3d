package api.scripting.coding.env.internal.util.settings.instances;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSHideFromDoc;
import javagems3d.system.settings.objects.SettingObject;

@JSCodingClass(binding = "JSSettingI", description = "Interface representing a generic game setting.")
public interface JSSettingI {
    @JSHideFromDoc
    SettingObject<?> getJavaSetting();
}