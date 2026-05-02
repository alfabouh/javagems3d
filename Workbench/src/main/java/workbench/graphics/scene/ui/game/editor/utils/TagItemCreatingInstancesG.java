package workbench.graphics.scene.ui.game.editor.utils;

import imgui.ImGui;
import imgui.type.ImInt;
import imgui.type.ImString;
import javagems3d.system.external.mapping.tags.base.ColorMode;
import javagems3d.system.external.mapping.tags.base.ResourceType;
import javagems3d.system.external.mapping.tags.base.VectorMode;
import javagems3d.system.external.mapping.tags.items.*;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector4f;
import workbench.graphics.scene.ui.game.editor.ResourcesInterfaceComponentG;

import java.util.function.Supplier;
import java.util.*;

public class TagItemCreatingInstancesG {
    public static Supplier<TagItemCreatingInstancesG.TagItemClassResolver<TagCheckBoolean>> booleanCheckTagItemResolver = () -> new TagItemCreatingInstancesG.TagItemClassResolver<TagCheckBoolean>() {
        private boolean flagDefaultValue;

        @Override
        public void renderUI(@NotNull ResourcesInterfaceComponentG resourcesInterfaceComponentG) {
            ImGui.bullet();
            ImGui.text("DefaultPhysTest value:");
            ImGui.indent();
            if (ImGui.selectable("TRUE", this.flagDefaultValue)) {
                this.flagDefaultValue = true;
            }
            if (ImGui.selectable("FALSE", !this.flagDefaultValue)) {
                this.flagDefaultValue = false;
            }
            ImGui.unindent();
        }

        @Override
        public @NotNull TagCheckBoolean create() {
            return new TagCheckBoolean(this.flagDefaultValue);
        }
    };

    public static Supplier<TagItemCreatingInstancesG.TagItemClassResolver<TagRadioBoolean>> radioBooleanTagItemResolver = () -> new TagItemCreatingInstancesG.TagItemClassResolver<TagRadioBoolean>() {
        private final List<TagRadioBoolean.Info> infos = new ArrayList<>();
        private int selectedIndex = -1;
        private final ImString nameBuffer = new ImString();

        @Override
        public void renderUI(@NotNull ResourcesInterfaceComponentG resourcesInterfaceComponentG) {
            ImGui.text("Radio values:");
            ImGui.indent();
            for (int i = 0; i < this.infos.size(); i++) {
                final int index = i;
                boolean selected = this.selectedIndex == index;
                if (ImGui.radioButton(this.infos.get(i).getName(), selected)) {
                    this.selectedIndex = index;
                }
                ImGui.sameLine();
                ImGui.pushID(i);
                if (ImGui.button("- delete")) {
                    this.selectedIndex = 0;
                    this.infos.remove(i);
                }
                ImGui.popID();
            }
            ImGui.separator();
            ImGui.text("Add value:");
            {
                ImGui.indent();
                ImGui.inputText("Name", this.nameBuffer);
                if (ImGui.button("Add")) {
                    boolean isFirst = this.infos.isEmpty();
                    if (!this.nameBuffer.get().isEmpty()) {
                        this.infos.add(new TagRadioBoolean.Info(this.nameBuffer.get(), isFirst));
                        if (isFirst) {
                            this.selectedIndex = 0;
                        }
                        this.nameBuffer.clear();
                    }
                }
                ImGui.unindent();
            }
            ImGui.unindent();
        }

        @Override
        public @NotNull TagRadioBoolean create() {
            if (this.infos.isEmpty()) {
                throw new IllegalStateException("TagRadioBoolean requires at least one value");
            }
            for (int i = 0; i < infos.size(); i++) {
                this.infos.get(i).setFlag(i == this.selectedIndex);
            }
            return new TagRadioBoolean(this.infos.toArray(new TagRadioBoolean.Info[0]));
        }
    };

    public static Supplier<TagItemCreatingInstancesG.TagItemClassResolver<TagFloat>> floatTagItemResolver = () -> new TagItemCreatingInstancesG.TagItemClassResolver<TagFloat>() {
        private float value;
        private float min;
        private float max;

        @Override
        public void renderUI(@NotNull ResourcesInterfaceComponentG resourcesInterfaceComponentG) {
            ImGui.bulletText("DefaultPhysTest value:");
            ImGui.indent();
            float[] value = new float[] {this.value};
            if (ImGui.dragFloat("##value", value, 0.01f, this.min, this.max)) {
                this.value = value[0];
            }
            ImGui.unindent();
            ImGui.bulletText("Min value:");
            ImGui.indent();
            float[] min = new float[] {this.min};
            if (ImGui.dragFloat("##min", min, 0.01f)) {
                this.min = min[0];
            }
            ImGui.unindent();
            ImGui.bulletText("Max value:");
            ImGui.indent();
            float[] max = new float[] {this.max};
            if (ImGui.dragFloat("##max", max, 0.01f)) {
                this.max = max[0];
            }
            ImGui.unindent();
        }

        @Override
        public @NotNull TagFloat create() {
            return new TagFloat(this.value, this.min, this.max);
        }
    };

    public static Supplier<TagItemCreatingInstancesG.TagItemClassResolver<TagInt>> intTagItemResolver = () -> new TagItemCreatingInstancesG.TagItemClassResolver<TagInt>() {
        private int value;
        private int min;
        private int max;

        @Override
        public void renderUI(@NotNull ResourcesInterfaceComponentG resourcesInterfaceComponentG) {
            ImGui.bulletText("DefaultPhysTest value:");
            ImGui.indent();
            int[] value = new int[] {this.value};
            if (ImGui.dragInt("##value", value, 0.01f, this.min, this.max)) {
                this.value = value[0];
            }
            ImGui.unindent();
            ImGui.bulletText("Min value:");
            ImGui.indent();
            int[] min = new int[] {this.min};
            if (ImGui.dragInt("##min", min, 0.01f)) {
                this.min = min[0];
            }
            ImGui.unindent();
            ImGui.bulletText("Max value:");
            ImGui.indent();
            int[] max = new int[] {this.max};
            if (ImGui.dragInt("##max", max, 0.01f)) {
                this.max = max[0];
            }
            ImGui.unindent();
        }

        @Override
        public @NotNull TagInt create() {
            return new TagInt(this.value, this.min, this.max);
        }
    };

    public static Supplier<TagItemCreatingInstancesG.TagItemClassResolver<TagString>> stringTagItemResolver = () -> new TagItemCreatingInstancesG.TagItemClassResolver<TagString>() {
        private String value = "";

        @Override
        public void renderUI(@NotNull ResourcesInterfaceComponentG resourcesInterfaceComponentG) {
            ImGui.bulletText("DefaultPhysTest text:");
            ImGui.indent();
            ImString string = new ImString(this.value);
            if (ImGui.inputText("##defaultText", string)) {
                this.value = string.get();
            }
            ImGui.unindent();
        }

        @Override
        public @NotNull TagString create() {
            return new TagString(this.value);
        }
    };

    public static Supplier<TagItemCreatingInstancesG.TagItemClassResolver<TagGameResourcesList>> gameResourcesListTagItemResolver = () -> new TagItemCreatingInstancesG.TagItemClassResolver<TagGameResourcesList>() {
                private final String[] resourceTypes = Arrays.stream(ResourceType.values()).map(Enum::name).toArray(String[]::new);
                private final ImInt selectedType = new ImInt(0);
                @Override
                public void renderUI(@NotNull ResourcesInterfaceComponentG resourcesInterfaceComponentG) {
                    ImGui.indent();
                    ImGui.text("Resource Type");
                    ImGui.combo("##resource_type", this.selectedType, this.resourceTypes, this.resourceTypes.length);
                    ImGui.unindent();
                }
                @Override
                public @NotNull TagGameResourcesList create() {
                    ResourceType resourceType = ResourceType.values()[this.selectedType.get()];
                    return new TagGameResourcesList("", resourceType);
                }
            };

    public static Supplier<TagItemCreatingInstancesG.TagItemClassResolver<TagObjectsList>> objectListTagItemResolver = () -> new TagItemCreatingInstancesG.TagItemClassResolver<TagObjectsList>() {
        @Override
        public void renderUI(@NotNull ResourcesInterfaceComponentG resourcesInterfaceComponentG) {
            ImGui.indent();
            ImGui.bulletText("<Nothing to adjust...>");
            ImGui.unindent();
        }

        @Override
        public @NotNull TagObjectsList create() {
            return new TagObjectsList();
        }
    };

    public static Supplier<TagItemCreatingInstancesG.TagItemClassResolver<TagColor>> colorTagItemResolver = () -> new TagItemCreatingInstancesG.TagItemClassResolver<TagColor>() {
        private final Vector4f colorVector = new Vector4f();
        private ColorMode colorMode;
        private final ImInt imInt = new ImInt(0);

        @Override
        public void renderUI(@NotNull ResourcesInterfaceComponentG resourcesInterfaceComponentG) {
            final Map<String, ColorMode> colorModeMap = new HashMap<>();
            for (ColorMode colorMode1 : ColorMode.values()) {
                colorModeMap.put(colorMode1.toString(), colorMode1);
            }
            final String[] comboGet = colorModeMap.keySet().toArray(new String[0]);
            ImGui.bulletText("Color mode:");
            ImGui.indent();
            if (ImGui.combo("##colorMode", this.imInt, comboGet)) {
                this.colorMode = colorModeMap.get(comboGet[this.imInt.get()]);
            }
            ImGui.unindent();
            ImGui.bulletText("DefaultPhysTest Color:");
            ImGui.indent();
            float[] colorArray = new float[]{this.colorVector.x, this.colorVector.y, this.colorVector.z, this.colorVector.w};
            if (ImGui.colorEdit4("##colorDefault", colorArray)) {
                this.colorVector.set(new Vector4f(colorArray[0], colorArray[1], colorArray[2], colorArray[3]));
            }
            ImGui.unindent();
        }

        @Override
        public @NotNull TagColor create() {
            return new TagColor(this.colorMode, this.colorVector);
        }
    };

    public static Supplier<TagItemCreatingInstancesG.TagItemClassResolver<TagVector>> vectorTagItemResolver = () -> new TagItemCreatingInstancesG.TagItemClassResolver<TagVector>() {
                private VectorMode vectorMode = VectorMode.VEC3F;
                private final Vector4f vector = new Vector4f(0, 0, 0, 0);
                private float min = -999999.0f;
                private float max = 999999.0f;
                private final ImInt modeIndex = new ImInt(0);

                @Override
                public void renderUI(@NotNull ResourcesInterfaceComponentG resourcesInterfaceComponentG) {
                    final VectorMode[] modes = VectorMode.values();
                    final String[] modeNames = new String[modes.length];
                    for (int i = 0; i < modes.length; i++) {
                        modeNames[i] = modes[i].name();
                    }

                    ImGui.bulletText("Vector mode:");
                    ImGui.indent();
                    if (ImGui.combo("##vectorMode", modeIndex, modeNames)) {
                        vectorMode = modes[modeIndex.get()];
                    }
                    ImGui.unindent();

                    ImGui.bulletText("Limits:");
                    ImGui.indent();
                    float[] min = new float[] {this.min};
                    if (ImGui.dragFloat("Min", min, 0.01f)) {
                        this.min = min[0];
                    }
                    float[] max = new float[] {this.max};
                    if (ImGui.dragFloat("Max", max, 0.01f)) {
                        this.max = max[0];
                    }
                    ImGui.unindent();

                    ImGui.bulletText("DefaultPhysTest value:");
                    ImGui.indent();

                    float[] arr = new float[]{vector.x, vector.y, vector.z, vector.w};
                    switch (vectorMode) {
                        case VEC2F: {
                            if (ImGui.dragFloat2("##vec2", arr, 0.01f, this.min, this.max)) {
                                vector.set(arr[0], arr[1], 0f, 0f);
                            }
                            break;
                        }
                        case VEC3F: {
                            if (ImGui.dragFloat3("##vec3", arr, 0.01f, this.min, this.max)) {
                                vector.set(arr[0], arr[1], arr[2], 0f);
                            }
                            break;
                        }
                        case VEC4F: {
                            if (ImGui.dragFloat4("##vec4", arr, 0.01f, this.min, this.max)) {
                                vector.set(arr[0], arr[1], arr[2], arr[3]);
                            }
                            break;
                        }
                    }
                    ImGui.unindent();
                }

                @Override
                public @NotNull TagVector create() {
                    return new TagVector(vectorMode, new Vector4f(vector), min, max);
                }
            };

    public interface TagItemClassResolver<T extends TagItem> {
        void renderUI(@NotNull ResourcesInterfaceComponentG resourcesInterfaceComponentG);
        @NotNull T create();
    }
}
