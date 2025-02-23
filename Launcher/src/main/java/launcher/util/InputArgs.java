package launcher.util;

import java.util.HashMap;
import java.util.Map;

public final class InputArgs {
    private Map<String, String> values;

    public InputArgs(String[] args) {
        this.values = new HashMap<>();
        this.decompose(args);
    }

    private void decompose(String[] args) {
        for (String s : args) {
            String[] strings1 = s.split("=");
            if (strings1.length == 2) {
                this.values.put(strings1[0], strings1[1]);
                continue;
            }
            this.values.put(strings1[0], null);
        }
    }

    public Integer getIntValue(String s) {
        try {
            return Integer.parseInt(this.values.get(s));
        } catch (NullPointerException | NumberFormatException e) {
            return null;
        }
    }

    public boolean getBoolValue(String s) {
        try {
            return Boolean.parseBoolean(this.values.get(s));
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean hasValue(String value) {
        return this.values.containsKey(value);
    }

    public String getStringValue(String s) {
        return this.values.get(s);
    }
}
