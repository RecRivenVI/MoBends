package goblinbob.mobends.core.client.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/** 页面内暂存的本地 BendsPack 选择，提交边界与原版资源包页面一致。 */
final class LocalPackSelectionModel {
    private final List<String> selectedKeys = new ArrayList<>();

    LocalPackSelectionModel(Collection<String> selectedKeys) {
        replaceWith(selectedKeys);
    }

    boolean select(String key) {
        if (selectedKeys.contains(key)) {
            return false;
        }
        selectedKeys.add(key);
        return true;
    }

    boolean unselect(String key) {
        return selectedKeys.remove(key);
    }

    boolean move(String key, int offset) {
        int index = selectedKeys.indexOf(key);
        int destination = index + offset;
        if (index < 0 || destination < 0 || destination >= selectedKeys.size()) {
            return false;
        }

        selectedKeys.remove(index);
        selectedKeys.add(destination, key);
        return true;
    }

    void retainAvailable(Collection<String> availableKeys) {
        Set<String> available = new LinkedHashSet<>(availableKeys);
        selectedKeys.removeIf(key -> !available.contains(key));
    }

    void replaceWith(Collection<String> keys) {
        selectedKeys.clear();
        selectedKeys.addAll(new LinkedHashSet<>(keys));
    }

    boolean isSelected(String key) {
        return selectedKeys.contains(key);
    }

    int indexOf(String key) {
        return selectedKeys.indexOf(key);
    }

    int size() {
        return selectedKeys.size();
    }

    List<String> selectedKeys() {
        return List.copyOf(selectedKeys);
    }
}
