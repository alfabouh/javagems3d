package workbench.graphics.scene.ui.game.editor.utils;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiHoveredFlags;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImInt;
import imgui.type.ImString;
import javagems3d.help.JGemsHelper;
import javagems3d.system.service.collections.Pair;
import logger.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import workbench.graphics.scene.ui.game.editor.instances.IPreviewWrapperObject;
import javagems3d.system.external.gaming.def.IAsset;
import javagems3d.system.service.files.VirtualObjectsFolder;
import javagems3d.system.external.gaming.def.util.GameResourceAssetsFolder;

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
    private final Predicate<Pair<VirtualObjectsFolder<T>, PopupContext>> existenceCheck;
    private final Supplier<VirtualObjectsFolder<T>> groupSupplier;
    private final Function<Pair<VirtualObjectsFolder<T>, PopupContext>, T> assetCreation;

    private @Nullable Consumer<Pair<VirtualObjectsFolder<T>, T>> afterAssetCreated;
    private @Nullable Consumer<Pair<VirtualObjectsFolder<T>, T>> afterAssetDeleted;
    private @Nullable Consumer<VirtualObjectsFolder<T>> afterFolderCreated;
    private @Nullable Consumer<VirtualObjectsFolder<T>> afterFolderDeleted;
    private @Nullable Consumer<AssetMovedData<T>> afterAssetMoved;
    private @Nullable Consumer<Void> onRefreshButton;
    private @Nullable Consumer<Void> onOpenFolderButton;
    private @Nullable Consumer<T> onContextOnItem;
    private final Function<T, E> previewInstanceFactory;
    private Consumer<E> onItemSelection;

    public CreatableResourcesTreeDrawerG(@NotNull String tab, @NotNull Supplier<VirtualObjectsFolder<T>> groupSupplier, @NotNull List<PopupConstructorData> popupConstructorData, @NotNull Predicate<Pair<VirtualObjectsFolder<T>, PopupContext>> existenceCheck, @NotNull Function<Pair<VirtualObjectsFolder<T>, PopupContext>, T> assetCreation, @NotNull Function<T, E> previewInstanceFactory) {
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
        this.onOpenFolderButton = null;
        this.afterAssetMoved = null;
        this.previewInstanceFactory = previewInstanceFactory;
        this.onContextOnItem = null;
        this.onItemSelection = null;
    }

    private void getFoldersToChoose(Map<String, VirtualObjectsFolder<T>> init, VirtualObjectsFolder<T> root) {
        for (VirtualObjectsFolder<T> inside : root.getFoldersThere()) {
            init.put(inside.getHierarchy(), inside);
        }
        for (VirtualObjectsFolder<T> inside : root.getFoldersThere()) {
            this.getFoldersToChoose(init, inside);
        }
    }

    private void popUpObject(@NotNull String tag, @Nullable String initFolder) {
        if (ImGui.beginPopup(tag + "_popupDataCreation_" + this.tab)) {
            int i = 0;
            for (PopupConstructorData popupConstructorData1 : this.popupConstructorData) {
                ImGui.text(popupConstructorData1.fieldName());
                ImGui.inputText("##" + popupConstructorData1.fieldName(), this.popupCreateObjectContext.getInputStrings().get(i++));
            }
            final Map<String, VirtualObjectsFolder<T>> groupsMap = new LinkedHashMap<>();
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
                final VirtualObjectsFolder<T> group1 = this.getSelectedGroup.get() < 0 || this.getSelectedGroup.get() > groups.length ? this.getGroupSupplier().get() : groupsMap.get(groups[this.getSelectedGroup.get()]);

                for (ImString input : this.popupCreateObjectContext.getInputStrings()) {
                    final PopupConstructorData context = this.popupConstructorData.get(i++);
                    if (input.isEmpty()) {
                        this.popupCreateObjectContext.setErrorText("Field " + context.fieldName() + " is empty!");
                    } else if (!input.get().matches(context.regex())) {
                        this.popupCreateObjectContext.setErrorText("Field " + context.fieldName() + ": " + context.nonMatchError());
                    } else if (this.getExistenceCheck().test(new Pair<>(group1, this.popupCreateObjectContext))) {
                        this.popupCreateObjectContext.setErrorText("Field " + context.fieldName() + ": Couldn't be created. Already exists!");
                    }
                }
                if (this.popupCreateObjectContext.getErrorText() == null) {
                    final T t = this.getAssetCreation().apply(new Pair<>(group1, this.popupCreateObjectContext));
                    if (this.getAfterAssetCreated() != null) {
                        this.getAfterAssetCreated().accept(new Pair<>(group1, t));
                    }
                    Log.get().debug(this.tab + ": New Asset (folder) = " + group1.getHierarchy() + " (asset) = " + this.popupCreateObjectContext.getInputStrings().getFirst());
                    ImGui.closeCurrentPopup();
                    this.popupCreateObjectContext.reset();
                }
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

    private void popUpFolder(VirtualObjectsFolder<T> placeIn) {
        if (ImGui.beginPopup("popupGroupCreation_" + this.tab)) {
            ImGui.text("Folder Name:");
            ImGui.inputText("##objectsGroup_" + this.tab, this.popupCreateGroupContext.inputStrings.get(0));
            String group = this.popupCreateGroupContext.inputStrings.getFirst().get();
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

    private void drawObjectsTree(boolean wantsToDeleteCurrentSelected, VirtualObjectsFolder<T> groupParent, VirtualObjectsFolder<T> group, boolean root) {
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
                if (ImGui.menuItem("+ Create Object")) {
                    openObjCreate = true;
                }
                ImGui.popStyleColor();
                ImGui.separator();
                ImGui.pushStyleColor(ImGuiCol.Text, 0xffffafaf);
                if (ImGui.menuItem("+ Create Folder")) {
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
            for (T asset : new ArrayList<>(group.getObjectsThere())) {
                String label = asset.name();
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
                if (ImGui.isMouseClicked(1)) {
                    if (ImGui.isItemHovered(ImGuiHoveredFlags.AllowWhenBlockedByPopup)) {
                        this.setPreviewWrapperObject(this.getPreviewInstanceFactory().apply(asset));
                    }
                }
                if (ImGui.beginPopupContextItem("ctx_item" + "##" + group.getName())) {
                    ImGui.text("Obj: " + asset.name());
                    ImGui.spacing();
                    {
                        ImGui.pushStyleColor(ImGuiCol.Text, 0xff6868ff);
                        if (ImGui.menuItem("- Delete")) {
                            wantsToDeleteCurrentSelected = true;
                        }
                        ImGui.popStyleColor();
                    }

                    {
                        final Map<String, VirtualObjectsFolder<T>> groupsMap = new LinkedHashMap<>();
                        if (!root) {
                            groupsMap.put(VirtualObjectsFolder.DEF_PATH, this.getGroupSupplier().get());
                        }
                        this.getFoldersToChoose(groupsMap, this.getGroupSupplier().get());
                        String[] groups = new String[groupsMap.size()];
                        int j = 0;
                        for (String key : groupsMap.keySet()) {
                            groups[j] = key;
                            if (key.equals(group.getHierarchy())) {
                                this.getSelectedGroup.set(j);
                            }
                            j += 1;
                        }
                        if (ImGui.combo("Folder", this.getSelectedGroup, groups)) {
                            group.removeObjectFromThere(asset.name());
                            final VirtualObjectsFolder<T> group1 = this.getSelectedGroup.get() < 0 || this.getSelectedGroup.get() > groups.length ? this.getGroupSupplier().get() : groupsMap.get(groups[this.getSelectedGroup.get()]);
                            group1.putObjectThere(asset);
                            if (this.getAfterAssetMoved() != null) {
                                this.getAfterAssetMoved().accept(new AssetMovedData<>(asset, group, group1));
                            }
                        }
                    }

                    ImGui.pushStyleColor(ImGuiCol.Text, 0xffffafaf);
                    if (this.getOnContextOnItem() != null) {
                        ImGui.separator();
                        this.getOnContextOnItem().accept(asset);
                    }
                    ImGui.popStyleColor();
                    ImGui.endPopup();
                }
                if (selected && wantsToDeleteCurrentSelected) {
                    group.removeObjectFromThere(asset.name());
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
            for (VirtualObjectsFolder<T> child : new ArrayList<>(group.getFoldersThere())) {
                this.drawObjectsTree(wantsToDeleteCurrentSelected, group, child, false);
            }
            ImGui.treePop();
        } else {
            ImGui.popStyleColor();
        }
    }

    public void render() {
        if (ImGui.collapsingHeader(this.getTab(), ImGuiTreeNodeFlags.DefaultOpen)) {
            final float dynHeight = JGemsHelper.math().clamp(this.getGroupSupplier().get().totalObjectsAndFoldersThere((int) (ImGui.getWindowHeight() / 10.0f)) * 30.0f, 160.0f, ImGui.getWindowHeight() * 0.5f) + 10.0f;
            ImGui.beginChild("##ObjChild_" + this.tab, ImGui.getColumnWidth(), dynHeight, true, ImGuiWindowFlags.HorizontalScrollbar);
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
            if (this.getOnOpenFolderButton() != null) {
                if (this.getOnRefreshButton() != null) {
                    ImGui.sameLine();
                }
                ImGui.pushStyleColor(ImGuiCol.Text, 0xff8c8cff);
                if (ImGui.button("Open Explorer")) {
                    this.getOnOpenFolderButton().accept(null);
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

    public @Nullable Consumer<Pair<VirtualObjectsFolder<T>, T>> getAfterAssetCreated() {
        return this.afterAssetCreated;
    }

    public CreatableResourcesTreeDrawerG<T, E> setAfterAssetCreated(@Nullable Consumer<Pair<VirtualObjectsFolder<T>, T>> afterAssetCreated) {
        this.afterAssetCreated = afterAssetCreated;
        return this;
    }

    public @Nullable Consumer<Pair<VirtualObjectsFolder<T>, T>> getAfterAssetDeleted() {
        return this.afterAssetDeleted;
    }

    public CreatableResourcesTreeDrawerG<T, E> setAfterAssetDeleted(@Nullable Consumer<Pair<VirtualObjectsFolder<T>, T>> afterAssetDeleted) {
        this.afterAssetDeleted = afterAssetDeleted;
        return this;
    }

    public @Nullable Consumer<VirtualObjectsFolder<T>> getAfterFolderCreated() {
        return this.afterFolderCreated;
    }

    public CreatableResourcesTreeDrawerG<T, E> setAfterFolderCreated(@Nullable Consumer<VirtualObjectsFolder<T>> afterFolderCreated) {
        this.afterFolderCreated = afterFolderCreated;
        return this;
    }

    public @Nullable Consumer<VirtualObjectsFolder<T>> getAfterFolderDeleted() {
        return this.afterFolderDeleted;
    }

    public CreatableResourcesTreeDrawerG<T, E> setAfterFolderDeleted(@Nullable Consumer<VirtualObjectsFolder<T>> afterFolderDeleted) {
        this.afterFolderDeleted = afterFolderDeleted;
        return this;
    }

    public Consumer<E> getOnItemSelection() {
        return this.onItemSelection;
    }

    public CreatableResourcesTreeDrawerG<T, E> setOnItemSelection(Consumer<E> onItemSelection) {
        this.onItemSelection = onItemSelection;
        return this;
    }

    public Function<T, E> getPreviewInstanceFactory() {
        return this.previewInstanceFactory;
    }

    public Function<Pair<VirtualObjectsFolder<T>, PopupContext>, T> getAssetCreation() {
        return this.assetCreation;
    }

    public Supplier<VirtualObjectsFolder<T>> getGroupSupplier() {
        return this.groupSupplier;
    }

    public Predicate<Pair<VirtualObjectsFolder<T>, PopupContext>> getExistenceCheck() {
        return this.existenceCheck;
    }

    public @Nullable Consumer<AssetMovedData<T>> getAfterAssetMoved() {
        return this.afterAssetMoved;
    }

    public CreatableResourcesTreeDrawerG<T, E> setAfterAssetMoved(@Nullable Consumer<AssetMovedData<T>> afterAssetMoved) {
        this.afterAssetMoved = afterAssetMoved;
        return this;
    }

    public @Nullable Consumer<Void> getOnOpenFolderButton() {
        return this.onOpenFolderButton;
    }

    public CreatableResourcesTreeDrawerG<T, E> setOnOpenFolderButton(@Nullable Consumer<Void> onOpenFolderButton) {
        this.onOpenFolderButton = onOpenFolderButton;
        return this;
    }

    public E getPreviewWrapperObject() {
        return this.previewWrapperObject;
    }

    public T getCurrentSelectedAsset() {
        return this.previewWrapperObject == null ? null : this.previewWrapperObject.getAsset();
    }

    public void setPreviewWrapperObject(E previewWrapperObject) {
        this.previewWrapperObject = previewWrapperObject;
        if (this.getOnItemSelection() != null) {
            this.getOnItemSelection().accept(previewWrapperObject);
        }
    }

    public String getTab() {
        return this.tab;
    }

    public record AssetMovedData <S extends IAsset> (S asset, VirtualObjectsFolder<S> from, VirtualObjectsFolder<S> to) {}

    public record PopupConstructorData(String fieldName, String regex, String nonMatchError) {
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
