package workbench.graphics.scene.ui.game.editor.utils;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImInt;
import imgui.type.ImString;
import javagems3d.system.service.collections.Pair;
import logger.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import workbench.graphics.scene.ui.game.editor.instances.IPreviewWrapperObject;
import workbench.project.managing.instances.IAsset;
import javagems3d.system.service.collections.AbstractObjectsFolder;
import workbench.project.managing.instances.group.GameResourceAssetsFolder;

import java.util.ArrayList;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class CreatableResourcesTreeDrawerG<T extends IAsset, E extends IPreviewWrapperObject<T>> {
    private final ImInt getSelectedGroup;
    private E previewWrapperObject;

    private final String tab;
    private final PopupContext popupCreateObjectContext;
    private final PopupContext popupCreateGroupContext;
    private final List<PopupConstructorData> popupConstructorData;
    private final Predicate<Pair<AbstractObjectsFolder<T>, PopupContext>> existenceCheck;
    private final Supplier<AbstractObjectsFolder<T>> groupSupplier;
    private final Function<Pair<AbstractObjectsFolder<T>, PopupContext>, T> assetCreation;

    private @Nullable Consumer<Pair<AbstractObjectsFolder<T>, T>> afterAssetCreated;
    private @Nullable Consumer<Pair<AbstractObjectsFolder<T>, T>> afterAssetDeleted;
    private @Nullable Consumer<AbstractObjectsFolder<T>> afterFolderCreated;
    private @Nullable Consumer<AbstractObjectsFolder<T>> afterFolderDeleted;
    private @Nullable Consumer<Void> onRefreshButton;
    private @Nullable Consumer<T> onContextOnItem;
    private final Function<T, E> previewInstanceFactory;

    public CreatableResourcesTreeDrawerG(@NotNull String tab, @NotNull Supplier<AbstractObjectsFolder<T>> groupSupplier, @NotNull List<PopupConstructorData> popupConstructorData, @NotNull Predicate<Pair<AbstractObjectsFolder<T>, PopupContext>> existenceCheck, @NotNull Function<Pair<AbstractObjectsFolder<T>, PopupContext>, T> assetCreation, @NotNull Function<T, E> previewInstanceFactory) {
        this.tab = tab;
        this.popupConstructorData = popupConstructorData;
        this.popupCreateObjectContext = new PopupContext(popupConstructorData.size());
        this.popupCreateGroupContext = new PopupContext(1);
        this.existenceCheck = existenceCheck;
        this.groupSupplier = groupSupplier;
        this.assetCreation = assetCreation;
        this.getSelectedGroup = new ImInt(-1);
        this.previewWrapperObject = null;
        this.onRefreshButton = null;
        this.previewInstanceFactory = previewInstanceFactory;
        this.onContextOnItem = null;
    }

    private void getFoldersToChoose(Map<String, AbstractObjectsFolder<T>> init, AbstractObjectsFolder<T> root) {
        for (AbstractObjectsFolder<T> inside : root.getFoldersThere()) {
            init.put(inside.getHierarchy(), inside);
        }
        for (AbstractObjectsFolder<T> inside : root.getFoldersThere()) {
            this.getFoldersToChoose(init, inside);
        }
    }

    private void popUpObject(@NotNull String tag, @Nullable String initFolder) {
        if (ImGui.beginPopup(tag + "_popupDataCreation_" + this.tab)) {
            int i = 0;
            for (PopupConstructorData popupConstructorData1 : this.popupConstructorData) {
                ImGui.text(popupConstructorData1.getFieldName());
                ImGui.inputText("##" + popupConstructorData1.getFieldName(), this.popupCreateObjectContext.getInputStrings().get(i++));
            }
            final Map<String, AbstractObjectsFolder<T>> groupsMap = new LinkedHashMap<>();
            this.getFoldersToChoose(groupsMap, this.getGroupSupplier().get());
            String[] groups = new String[groupsMap.size()];
            int j = 0;
            for (String key : groupsMap.keySet()) {
                groups[j] = key;
                if (key.equals(initFolder)) {
                    this.getSelectedGroup.set(j);
                }
                j += 1;
            }
            ImGui.combo("Folder", this.getSelectedGroup, groups);
            i = 0;
            if (ImGui.button("Create")) {
                this.popupCreateObjectContext.resetErr();
                final AbstractObjectsFolder<T> group1 = this.getSelectedGroup.get() < 0 || this.getSelectedGroup.get() > groups.length ? this.getGroupSupplier().get() : groupsMap.get(groups[this.getSelectedGroup.get()]);

                for (ImString input : this.popupCreateObjectContext.getInputStrings()) {
                    final PopupConstructorData context = this.popupConstructorData.get(i++);
                    if (input.isEmpty()) {
                        this.popupCreateObjectContext.setErrorText("Field " + context.getFieldName() + " is empty!");
                    } else if (!input.get().matches(context.getRegex())) {
                        this.popupCreateObjectContext.setErrorText("Field " + context.getFieldName() + ": " + context.getNonMatchError());
                    } else if (this.getExistenceCheck().test(new Pair<>(group1, this.popupCreateObjectContext))) {
                        this.popupCreateObjectContext.setErrorText("Field " + context.getFieldName() + ": Couldn't be created. Already exists!");
                    }
                }
                if (this.popupCreateObjectContext.getErrorText() == null) {
                    final T t = this.getAssetCreation().apply(new Pair<>(group1, this.popupCreateObjectContext));
                    if (this.getAfterAssetCreated() != null) {
                        this.getAfterAssetCreated().accept(new Pair<>(group1, t));
                    }
                    Log.get().debug(this.tab + ": New Asset (folder) = " + group1.getHierarchy() + " (asset) = " + this.popupCreateObjectContext.getInputStrings().get(0));
                }
                this.popupCreateObjectContext.reset();
                ImGui.closeCurrentPopup();
            }
            ImGui.sameLine();
            if (ImGui.button("Cancel")) {
                this.popupCreateObjectContext.reset();
                ImGui.closeCurrentPopup();
            }
            if (this.popupCreateObjectContext.getErrorText() != null) {
                ImGui.pushStyleColor(ImGuiCol.Text, 0xff0000ff);
                ImGui.text(this.popupCreateObjectContext.getErrorText());
                ImGui.popStyleColor();
            }
            ImGui.endPopup();
        } else {
            this.getSelectedGroup.set(-1);
        }
    }

    private void popUpFolder(AbstractObjectsFolder<T> placeIn) {
        if (ImGui.beginPopup("popupGroupCreation_" + this.tab)) {
            ImGui.text("Group:");
            ImGui.inputText("##objectsGroup_" + this.tab, this.popupCreateGroupContext.inputStrings.get(0));
            String group = this.popupCreateGroupContext.inputStrings.get(0).get();
            ImGui.beginDisabled(group.isEmpty());
            if (ImGui.button("Create")) {
                this.popupCreateGroupContext.setErrorText(null);
                if (!group.matches("[a-zA-Z\\d]+")) {
                    this.popupCreateGroupContext.setErrorText("Digits, spec. symbols and spaces are not allowed in folder's name!");
                }
                if (placeIn.getFoldersThereMap().containsKey(group)) {
                    this.popupCreateGroupContext.setErrorText("This folder already exists!");
                }
                if (this.popupCreateGroupContext.getErrorText() == null) {
                    Log.get().debug(this.tab + ": New Folder " + group);
                    final GameResourceAssetsFolder<T> gameResourceAssetsFolder = new GameResourceAssetsFolder<>(group);
                    placeIn.putFolderThere(gameResourceAssetsFolder);
                    if (this.getAfterFolderCreated() != null) {
                        this.getAfterFolderCreated().accept(gameResourceAssetsFolder);
                    }
                    ImGui.closeCurrentPopup();
                    this.popupCreateGroupContext.reset();
                }
            }
            ImGui.endDisabled();
            ImGui.sameLine();
            if (ImGui.button("Cancel")) {
                ImGui.closeCurrentPopup();
            }
            if (this.popupCreateGroupContext.getErrorText() != null) {
                ImGui.pushStyleColor(ImGuiCol.Text, 0xff0000ff);
                ImGui.text(this.popupCreateGroupContext.getErrorText());
                ImGui.popStyleColor();
            }
            ImGui.endPopup();
        }
    }

    private void drawObjectsTree(boolean wantsToDeleteCurrentSelected, AbstractObjectsFolder<T> groupParent, AbstractObjectsFolder<T> group, boolean root) {
        String folderName = group.getName();
        if (root) {
            folderName = "View";
        }
        ImGui.pushStyleColor(ImGuiCol.Text, 0xffffb0b0);
        if (ImGui.treeNodeEx(folderName + "##" + group.getName(), ImGuiTreeNodeFlags.OpenOnArrow | ImGuiTreeNodeFlags.DefaultOpen)) {
            ImGui.popStyleColor();
            boolean openGrCreate = false;
            boolean openObjCreate = false;
            if (ImGui.beginPopupContextItem("ctx_folder" + "##" + group.getName())) {
                ImGui.pushStyleColor(ImGuiCol.Text, 0xffafffaf);
                if (ImGui.menuItem("+ Add Object")) {
                    openObjCreate = true;
                }
                ImGui.popStyleColor();
                ImGui.separator();
                ImGui.pushStyleColor(ImGuiCol.Text, 0xffffafaf);
                if (ImGui.menuItem("+ Add Folder")) {
                    openGrCreate = true;
                }
                if (!root) {
                    final boolean flag = !group.getObjectsThere().isEmpty() || !group.getFoldersThere().isEmpty();
                    ImGui.beginDisabled(flag);
                    if (ImGui.menuItem("- Delete Folder")) {
                        groupParent.removeFolderFromThere(group.getName());
                        if (this.getAfterFolderDeleted() != null) {
                            this.getAfterFolderDeleted().accept(group);
                        }
                    }
                    ImGui.endDisabled();
                    //if (ImGui.isItemHovered() && flag) {
                    //    ImGui.beginTooltip();
                    //    ImGui.setTooltip("The folder should be empty!");
                    //    ImGui.endTooltip();
                    //}
                }
                ImGui.popStyleColor();
                ImGui.endPopup();
            }
            this.popUpFolder(group);
            if (openGrCreate) {
                ImGui.openPopup("popupGroupCreation_" + this.tab);
            }
            this.popUpObject("short", group.getHierarchy());
            if (openObjCreate) {
                ImGui.openPopup("short_popupDataCreation_" + this.tab);
            }
            for (T asset : group.getObjectsThere()) {
                String label = asset.getName();
                if (label == null) {
                    continue;
                }
                ImGui.pushID(this.tab + "_" + label);
                final boolean selected = this.getPreviewWrapperObject() != null && this.getPreviewWrapperObject().getAsset().equals(asset);
                ImGui.bullet();
                if (ImGui.selectable("./" + label, selected)) {
                    if (!selected) {
                        this.setPreviewWrapperObject(this.getPreviewInstanceFactory().apply(asset));
                    } else {
                        this.setPreviewWrapperObject(null);
                    }
                }
                if (ImGui.beginPopupContextItem("ctx_item" + "##" + group.getName())) {
                    ImGui.pushStyleColor(ImGuiCol.Text, 0xffffafaf);
                    if (ImGui.menuItem("* Select")) {
                        this.setPreviewWrapperObject(this.getPreviewInstanceFactory().apply(asset));
                    }
                    ImGui.popStyleColor();

                    ImGui.pushStyleColor(ImGuiCol.Text, 0xff6868ff);
                    if (ImGui.menuItem("- Delete")) {
                        wantsToDeleteCurrentSelected = true;
                    }
                    ImGui.popStyleColor();

                    ImGui.pushStyleColor(ImGuiCol.Text, 0xffffafaf);
                    if (this.getOnContextOnItem() != null) {
                        ImGui.separator();
                        this.getOnContextOnItem().accept(asset);
                    }
                    ImGui.popStyleColor();
                    ImGui.endPopup();
                }
                if (selected && wantsToDeleteCurrentSelected) {
                    group.removeObjectFromThere(asset.getName());
                    if (this.getAfterAssetDeleted() != null) {
                        this.getAfterAssetDeleted().accept(new Pair<>(group, asset));
                    }
                    if (this.getCurrentSelectedAsset() != null && asset.equals(this.getCurrentSelectedAsset())) {
                        this.setPreviewWrapperObject(null);
                    }
                    Log.get().debug(this.tab + ": Removed Asset (folder) = " + group.getName() + " (asset) = " + label);
                }
                ImGui.popID();
            }
            for (AbstractObjectsFolder<T> child : new ArrayList<>(group.getFoldersThere())) {
                this.drawObjectsTree(wantsToDeleteCurrentSelected, group, child, false);
            }
            ImGui.treePop();
        } else {
            ImGui.popStyleColor();
        }
    }

    public void render() {
        if (ImGui.collapsingHeader(this.getTab(), ImGuiTreeNodeFlags.DefaultOpen)) {
            ImGui.beginChild("##ObjChild_" + this.tab, ImGui.getColumnWidth(), ImGui.getWindowHeight() * 0.5f, true, ImGuiWindowFlags.HorizontalScrollbar);
            ImGui.pushID("##IDC" + this.tab);
            this.popUpObject("button", null);
            if (this.getOnRefreshButton() != null) {
                ImGui.pushStyleColor(ImGuiCol.Text, 0xff8c8cff);
                if (ImGui.button("Refresh")) {
                    this.getOnRefreshButton().accept(null);
                    this.setPreviewWrapperObject(null);
                }
                ImGui.popStyleColor();
            }
            ImGui.pushStyleColor(ImGuiCol.Text, 0xff00ff00);
            if (ImGui.button("+ Create")) {
                ImGui.openPopup("button_popupDataCreation_" + this.tab);
            }
            ImGui.popStyleColor();
            boolean flag = this.getPreviewWrapperObject() != null;
            ImGui.beginDisabled(!flag);
            ImGui.sameLine();
            ImGui.pushStyleColor(ImGuiCol.Text, 0xff0000ff);
            boolean wantsToDelete = ImGui.button("- Delete");
            ImGui.popStyleColor();
            ImGui.endDisabled();
            ImGui.spacing();
            this.drawObjectsTree(wantsToDelete, null, this.groupSupplier.get(), true);
            ImGui.popID();
            ImGui.endChild();
        }
    }

    public @Nullable Consumer<Void> getOnRefreshButton() {
        return this.onRefreshButton;
    }

    public CreatableResourcesTreeDrawerG<T, E> setOnRefreshButton(@Nullable Consumer<Void> onRefreshButton) {
        this.onRefreshButton = onRefreshButton;
        return this;
    }

    public @Nullable Consumer<T> getOnContextOnItem() {
        return this.onContextOnItem;
    }

    public CreatableResourcesTreeDrawerG<T, E> setOnContextOnItem(@Nullable Consumer<T> onContextOnItem) {
        this.onContextOnItem = onContextOnItem;
        return this;
    }

    public @Nullable Consumer<Pair<AbstractObjectsFolder<T>, T>> getAfterAssetCreated() {
        return this.afterAssetCreated;
    }

    public CreatableResourcesTreeDrawerG<T, E> setAfterAssetCreated(@Nullable Consumer<Pair<AbstractObjectsFolder<T>, T>> afterAssetCreated) {
        this.afterAssetCreated = afterAssetCreated;
        return this;
    }

    public @Nullable Consumer<Pair<AbstractObjectsFolder<T>, T>> getAfterAssetDeleted() {
        return this.afterAssetDeleted;
    }

    public CreatableResourcesTreeDrawerG<T, E> setAfterAssetDeleted(@Nullable Consumer<Pair<AbstractObjectsFolder<T>, T>> afterAssetDeleted) {
        this.afterAssetDeleted = afterAssetDeleted;
        return this;
    }

    public @Nullable Consumer<AbstractObjectsFolder<T>> getAfterFolderCreated() {
        return this.afterFolderCreated;
    }

    public CreatableResourcesTreeDrawerG<T, E> setAfterFolderCreated(@Nullable Consumer<AbstractObjectsFolder<T>> afterFolderCreated) {
        this.afterFolderCreated = afterFolderCreated;
        return this;
    }

    public @Nullable Consumer<AbstractObjectsFolder<T>> getAfterFolderDeleted() {
        return this.afterFolderDeleted;
    }

    public CreatableResourcesTreeDrawerG<T, E> setAfterFolderDeleted(@Nullable Consumer<AbstractObjectsFolder<T>> afterFolderDeleted) {
        this.afterFolderDeleted = afterFolderDeleted;
        return this;
    }

    public Function<T, E> getPreviewInstanceFactory() {
        return this.previewInstanceFactory;
    }

    public Function<Pair<AbstractObjectsFolder<T>, PopupContext>, T> getAssetCreation() {
        return this.assetCreation;
    }

    public Supplier<AbstractObjectsFolder<T>> getGroupSupplier() {
        return this.groupSupplier;
    }

    public Predicate<Pair<AbstractObjectsFolder<T>, PopupContext>> getExistenceCheck() {
        return this.existenceCheck;
    }

    public E getPreviewWrapperObject() {
        return this.previewWrapperObject;
    }

    public T getCurrentSelectedAsset() {
        return this.previewWrapperObject == null ? null : this.previewWrapperObject.getAsset();
    }

    public void setPreviewWrapperObject(E previewWrapperObject) {
        this.previewWrapperObject = previewWrapperObject;
    }

    public String getTab() {
        return this.tab;
    }

    public static class PopupConstructorData {
        private final String fieldName;
        private final String regex;
        private final String nonMatchError;

        public PopupConstructorData(String fieldName, String regex, String nonMatchError) {
            this.fieldName = fieldName;
            this.regex = regex;
            this.nonMatchError = nonMatchError;
        }

        public String getFieldName() {
            return this.fieldName;
        }

        public String getRegex() {
            return this.regex;
        }

        public String getNonMatchError() {
            return this.nonMatchError;
        }
    }

    public static class PopupContext {
        private String errorText;
        private final List<ImString> inputStrings;

        public PopupContext(int inputStrings) {
            this.inputStrings = new ArrayList<>();
            for (int i = 0; i < inputStrings; i++) {
                this.inputStrings.add(new ImString());
            }
        }

        public void resetErr() {
            this.errorText = null;
        }

        public void reset() {
            this.errorText = null;
            this.getInputStrings().forEach(ImString::clear);
        }

        public PopupContext setErrorText(String errorText) {
            this.errorText = errorText;
            return this;
        }

        public String getErrorText() {
            return this.errorText;
        }

        public List<ImString> getInputStrings() {
            return this.inputStrings;
        }
    }
}
