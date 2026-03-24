package api.scripting.coding.env.internal.util.settings;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingField;
import api.scripting.coding.env.def.JSHideFromDoc;
import javagems3d.system.service.stat.PerformanceStat;

@JSCodingClass(binding = "JSPerfTestResult", description = "...")
public enum JSPerfTestResult {
        @JSCodingField(description = "POTATO") POTATO,
        @JSCodingField(description = "LOW") LOW,
        @JSCodingField(description = "MEDIUM") MEDIUM,
        @JSCodingField(description = "HIGH") HIGH,
        @JSCodingField(description = "GREAT") GREAT;

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