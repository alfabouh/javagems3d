package api.scripting.coding.env.internal.util.settings;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingField;
import api.scripting.coding.env.def.JSHideFromDoc;
import javagems3d.system.service.stat.PerformanceStat;

@JSCodingClass(binding = "JSPerfTestResult", description = "Performance test result levels.")
public enum JSPerfTestResult {
    @JSCodingField(description = "Very low performance") POTATO,
    @JSCodingField(description = "Low performance") LOW,
    @JSCodingField(description = "Medium performance") MEDIUM,
    @JSCodingField(description = "High performance") HIGH,
    @JSCodingField(description = "Excellent performance") GREAT;

    @JSHideFromDoc
    public static JSPerfTestResult choose(PerformanceStat.Result result) {
        return switch (result) {
            case POTATO -> POTATO;
            case LOW -> LOW;
            case MEDIUM -> MEDIUM;
            case HIGH -> HIGH;
            case GREAT -> GREAT;
        };
    }
}