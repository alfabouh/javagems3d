package api.system.scripting;

public abstract class JavaToJsFunctionsList {
    public static final String ENTRY_POINT_FUNCTION = "JsInit";
    public static final String ENTRY_POINT_FUNCTION_DESC =
            "Entry point of the script. Called once when the script is initialized. " +
                    "If this function is present, the file is treated as a script entry point.";

    public static final String SUBSCRIBE_EVENTS_FUNCTION = "JsSubscribeEvents";
    public static final String SUBSCRIBE_EVENTS_FUNCTION_DESC = "...";
}
