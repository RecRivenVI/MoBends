package io.github.recrivenvi.mobends.core.pack;

import com.google.gson.JsonParseException;
import com.google.gson.stream.JsonReader;
import io.github.recrivenvi.mobends.core.animation.keyframe.AnimationLoader;
import io.github.recrivenvi.mobends.core.kumo.KumoSerializer;
import io.github.recrivenvi.mobends.core.kumo.state.template.AnimatorTemplate;
import io.github.recrivenvi.mobends.core.kumo.state.template.IKumoValidationContext;
import io.github.recrivenvi.mobends.core.kumo.state.template.LayerTemplate;
import io.github.recrivenvi.mobends.core.kumo.state.template.MalformedKumoTemplateException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class PackDataProvider {

    public static final PackDataProvider INSTANCE = new PackDataProvider();

    private Map<IBendsPack, BendsPackData> dataMap = new HashMap<>();

    private BendsPackData appliedData;

    private PackDataProvider() {}

    public void createBendsPackData(Collection<IBendsPack> packs)
            throws InvalidPackFormatException {
        final List<BendsPackData> dataList = new LinkedList<>();
        for (IBendsPack pack : packs) {
            final BendsPackData data = getDataForPack(pack);
            if (data != null) {
                dataList.add(data);
            }
        }

        if (dataList.isEmpty()) {
            appliedData = null;
            return;
        }

        appliedData = PackCombiner.combineData(dataList);
    }

    public void clearCache() {
        this.dataMap.clear();
    }

    public BendsPackData getDataForPack(IBendsPack bendsPack) throws InvalidPackFormatException {
        if (dataMap.containsKey(bendsPack)) {
            return dataMap.get(bendsPack);
        }

        if (bendsPack instanceof LocalBendsPack) {
            LocalBendsPack localPack = (LocalBendsPack) bendsPack;
            if (localPack.isLegacyFormat()) {
                BendsPackData data = new BendsPackData();
                data.legacyTargets.putAll(localPack.getLegacyDefinition().targets);
                dataMap.put(bendsPack, data);
                return data;
            }

            try {
                File file = localPack.getDataFile();
                if (file == null) {
                    file = PackManager.INSTANCE.getDataFileForPack(bendsPack.getKey());
                }
                BendsPackData data;
                try (JsonReader fileReader = new JsonReader(new FileReader(file))) {
                    data = KumoSerializer.INSTANCE.gson.fromJson(fileReader, BendsPackData.class);
                }

                validatePackData(data);

                dataMap.put(bendsPack, data);
                return data;
            } catch (IOException ex) {
                ex.printStackTrace();
                throw new InvalidPackFormatException(
                        bendsPack.getDisplayName(),
                        String.format(
                                "Data for pack '%s' couldn't be fetched", bendsPack.getKey()));
            } catch (JsonParseException | MalformedKumoTemplateException ex) {
                ex.printStackTrace();
                throw new InvalidPackFormatException(
                        bendsPack.getDisplayName(),
                        String.format(
                                "The '%s' pack isn't in a correct format.", bendsPack.getKey()));
            }
        }

        return null;
    }

    public void validatePackData(BendsPackData data) throws MalformedKumoTemplateException {
        if (data == null) {
            throw new MalformedKumoTemplateException(
                    "A BendsPack data file must contain a JSON object.");
        }
        if (data.targets == null) {
            data.targets = new HashMap<>();
        }
        if (data.keyframeAnimations == null) {
            data.keyframeAnimations = new HashMap<>();
        }
        if (data.legacyTargets == null) {
            data.legacyTargets = new LinkedHashMap<>();
        }
        IKumoValidationContext context =
                new IKumoValidationContext() {
                    @Override
                    public boolean doesAnimationExist(String animationKey) {
                        if (data.keyframeAnimations.containsKey(animationKey)) {
                            return true;
                        }

                        try {
                            return AnimationLoader.loadFromPath(animationKey) != null;
                        } catch (IOException e) {
                            e.printStackTrace();
                            return false;
                        }
                    }
                };

        for (Map.Entry<String, AnimatorTemplate> entry : data.targets.entrySet()) {
            if (entry.getKey() == null || entry.getKey().isBlank()) {
                throw new MalformedKumoTemplateException("A BendsPack target key cannot be empty.");
            }
            AnimatorTemplate animator = entry.getValue();
            if (animator == null || animator.layers == null || animator.layers.isEmpty()) {
                throw new MalformedKumoTemplateException(
                        String.format(
                                "Target '%s' must define at least one animation layer.",
                                entry.getKey()));
            }
            for (LayerTemplate layer : animator.layers) {
                if (layer == null) {
                    throw new MalformedKumoTemplateException(
                            String.format(
                                    "Target '%s' contains a null animation layer.",
                                    entry.getKey()));
                }
                layer.validate(context);
            }
        }
    }

    public BendsPackData getAppliedData() {
        return appliedData;
    }
}
