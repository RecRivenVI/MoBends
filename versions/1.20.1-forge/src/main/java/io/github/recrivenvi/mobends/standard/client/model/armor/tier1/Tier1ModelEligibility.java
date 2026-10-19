package io.github.recrivenvi.mobends.standard.client.model.armor.tier1;

import javax.annotation.Nullable;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;

/** Selects models that can use Tier 1 transform injection. */
final class Tier1ModelEligibility {
    private Tier1ModelEligibility() {}

    @Nullable
    static HumanoidModel<?> asHumanoid(Model model) {
        return model instanceof HumanoidModel<?> humanoidModel ? humanoidModel : null;
    }
}
