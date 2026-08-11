package goblinbob.mobends.core.pack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

public class PackCombiner {

    /**
     * Combining given packs into one data. The resulting data will contain animations and targets
     * from each pack. If packs share a target, the ones that appear first in the array will take
     * precedence.
     *
     * @param packs
     * @return
     */
    public static BendsPackData combineData(List<BendsPackData> packs) {
        BendsPackData combinedData = new BendsPackData();
        combinedData.targets = new HashMap<>();
        combinedData.keyframeAnimations = new HashMap<>();
        combinedData.legacyTargets = new LinkedHashMap<>();

        for (int i = packs.size() - 1; i >= 0; --i) {
            BendsPackData data = packs.get(i);
            combinedData.keyframeAnimations.putAll(data.keyframeAnimations);
        }

        Set<String> claimedTargets = new HashSet<>();
        for (BendsPackData data : packs) {
            for (var entry : data.targets.entrySet()) {
                if (claimedTargets.add(entry.getKey())) {
                    combinedData.targets.put(entry.getKey(), entry.getValue());
                }
            }
            for (var entry : data.legacyTargets.entrySet()) {
                if (claimedTargets.add(entry.getKey())) {
                    combinedData.legacyTargets.put(entry.getKey(), entry.getValue());
                }
            }
        }

        return combinedData;
    }
}
