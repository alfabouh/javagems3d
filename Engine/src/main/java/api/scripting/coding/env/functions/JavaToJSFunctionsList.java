package api.scripting.coding.env.functions;

public abstract class JavaToJSFunctionsList {
    public static final String ENTRY_POINT_FUNCTION = "JsInit";
    public static final String ENTRY_POINT_FUNCTION_DESC =
            "Entry point of the script. Called once when the script is initialized. " +
                    "If this function is present, the file is treated as a script entry point.";
}
