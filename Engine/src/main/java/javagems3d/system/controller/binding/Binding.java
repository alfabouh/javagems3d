package javagems3d.system.controller.binding;

import javagems3d.system.controller.components.Key;

import java.util.HashMap;
import java.util.Map;

public class Binding {
    private static final Map<Integer, String> KEY_NAMES = new HashMap<>();
    static {
        KEY_NAMES.put(32, "Space");
        KEY_NAMES.put(39, "Apostrophe");
        KEY_NAMES.put(44, "Comma");
        KEY_NAMES.put(45, "Minus");
        KEY_NAMES.put(46, "Period");
        KEY_NAMES.put(47, "Slash");
        KEY_NAMES.put(48, "0");
        KEY_NAMES.put(49, "1");
        KEY_NAMES.put(50, "2");
        KEY_NAMES.put(51, "3");
        KEY_NAMES.put(52, "4");
        KEY_NAMES.put(53, "5");
        KEY_NAMES.put(54, "6");
        KEY_NAMES.put(55, "7");
        KEY_NAMES.put(56, "8");
        KEY_NAMES.put(57, "9");
        KEY_NAMES.put(59, "Semicolon");
        KEY_NAMES.put(61, "Equal");
        KEY_NAMES.put(65, "A");
        KEY_NAMES.put(66, "B");
        KEY_NAMES.put(67, "C");
        KEY_NAMES.put(68, "D");
        KEY_NAMES.put(69, "E");
        KEY_NAMES.put(70, "F");
        KEY_NAMES.put(71, "G");
        KEY_NAMES.put(72, "H");
        KEY_NAMES.put(73, "I");
        KEY_NAMES.put(74, "J");
        KEY_NAMES.put(75, "K");
        KEY_NAMES.put(76, "L");
        KEY_NAMES.put(77, "M");
        KEY_NAMES.put(78, "N");
        KEY_NAMES.put(79, "O");
        KEY_NAMES.put(80, "P");
        KEY_NAMES.put(81, "Q");
        KEY_NAMES.put(82, "R");
        KEY_NAMES.put(83, "S");
        KEY_NAMES.put(84, "T");
        KEY_NAMES.put(85, "U");
        KEY_NAMES.put(86, "V");
        KEY_NAMES.put(87, "W");
        KEY_NAMES.put(88, "X");
        KEY_NAMES.put(89, "Y");
        KEY_NAMES.put(90, "Z");
        KEY_NAMES.put(91, "Left Bracket");
        KEY_NAMES.put(92, "Backslash");
        KEY_NAMES.put(93, "Right Bracket");
        KEY_NAMES.put(96, "Grave Accent");
        KEY_NAMES.put(161, "World 1");
        KEY_NAMES.put(162, "World 2");

        KEY_NAMES.put(256, "Escape");
        KEY_NAMES.put(257, "Enter");
        KEY_NAMES.put(258, "Tab");
        KEY_NAMES.put(259, "Backspace");
        KEY_NAMES.put(260, "Insert");
        KEY_NAMES.put(261, "Delete");
        KEY_NAMES.put(262, "Right");
        KEY_NAMES.put(263, "Left");
        KEY_NAMES.put(264, "Down");
        KEY_NAMES.put(265, "Up");
        KEY_NAMES.put(266, "Page Up");
        KEY_NAMES.put(267, "Page Down");
        KEY_NAMES.put(268, "Home");
        KEY_NAMES.put(269, "End");
        KEY_NAMES.put(280, "Caps Lock");
        KEY_NAMES.put(281, "Scroll Lock");
        KEY_NAMES.put(282, "Num Lock");
        KEY_NAMES.put(283, "Print Screen");
        KEY_NAMES.put(284, "Pause");
        KEY_NAMES.put(290, "F1");
        KEY_NAMES.put(291, "F2");
        KEY_NAMES.put(292, "F3");
        KEY_NAMES.put(293, "F4");
        KEY_NAMES.put(294, "F5");
        KEY_NAMES.put(295, "F6");
        KEY_NAMES.put(296, "F7");
        KEY_NAMES.put(297, "F8");
        KEY_NAMES.put(298, "F9");
        KEY_NAMES.put(299, "F10");
        KEY_NAMES.put(300, "F11");
        KEY_NAMES.put(301, "F12");
        KEY_NAMES.put(302, "F13");
        KEY_NAMES.put(303, "F14");
        KEY_NAMES.put(304, "F15");
        KEY_NAMES.put(305, "F16");
        KEY_NAMES.put(306, "F17");
        KEY_NAMES.put(307, "F18");
        KEY_NAMES.put(308, "F19");
        KEY_NAMES.put(309, "F20");
        KEY_NAMES.put(310, "F21");
        KEY_NAMES.put(311, "F22");
        KEY_NAMES.put(312, "F23");
        KEY_NAMES.put(313, "F24");
        KEY_NAMES.put(314, "F25");

        KEY_NAMES.put(320, "Keypad 0");
        KEY_NAMES.put(321, "Keypad 1");
        KEY_NAMES.put(322, "Keypad 2");
        KEY_NAMES.put(323, "Keypad 3");
        KEY_NAMES.put(324, "Keypad 4");
        KEY_NAMES.put(325, "Keypad 5");
        KEY_NAMES.put(326, "Keypad 6");
        KEY_NAMES.put(327, "Keypad 7");
        KEY_NAMES.put(328, "Keypad 8");
        KEY_NAMES.put(329, "Keypad 9");
        KEY_NAMES.put(330, "Keypad Decimal");
        KEY_NAMES.put(331, "Keypad Divide");
        KEY_NAMES.put(332, "Keypad Multiply");
        KEY_NAMES.put(333, "Keypad Subtract");
        KEY_NAMES.put(334, "Keypad Add");
        KEY_NAMES.put(335, "Keypad Enter");
        KEY_NAMES.put(336, "Keypad Equal");

        KEY_NAMES.put(340, "Left Shift");
        KEY_NAMES.put(341, "Left Control");
        KEY_NAMES.put(342, "Left Alt");
        KEY_NAMES.put(343, "Left Super");
        KEY_NAMES.put(344, "Right Shift");
        KEY_NAMES.put(345, "Right Control");
        KEY_NAMES.put(346, "Right Alt");
        KEY_NAMES.put(347, "Right Super");
        KEY_NAMES.put(348, "Menu");
    }

    public static String getKeyName(int keyCode) {
        return Binding.KEY_NAMES.getOrDefault(keyCode, "UNKNOWN");
    }

    private final String description;
    private Key key;

    private Binding(Key key, String description) {
        this.key = key;
        this.description = description;
    }

    private Binding(String description) {
        this(null, description);
    }

    @SuppressWarnings("all")
    public static Binding createBinding(Key key, String description) {
        return new Binding(key, description);
    }

    public void setKeyToBinding(Key key) {
        this.key = key;
    }

    @Override
    public int hashCode() {
        return this.getKey().hashCode();
    }

    public String getDescription() {
        return this.description;
    }

    public Key getKey() {
        return this.key;
    }

    public String toString() {
        return Binding.getKeyName(this.getKey().getKeyCode()) + " - " + this.getDescription();
    }
}
