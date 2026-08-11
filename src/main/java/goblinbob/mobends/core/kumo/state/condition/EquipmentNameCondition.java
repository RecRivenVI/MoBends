package goblinbob.mobends.core.kumo.state.condition;

import goblinbob.mobends.core.kumo.state.template.MalformedKumoTemplateException;
import goblinbob.mobends.core.kumo.state.template.TriggerConditionTemplate;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/** */
public class EquipmentNameCondition implements ITriggerCondition {

    private final Pattern namePattern;
    private final EquipmentSlot slot;

    public EquipmentNameCondition(Template template) throws MalformedKumoTemplateException {
        if (template.namePattern == null || template.namePattern.isBlank()) {
            throw new MalformedKumoTemplateException(
                    "An equipment_name condition must specify a namePattern.");
        }
        if (template.slot == null) {
            throw new MalformedKumoTemplateException(
                    "An equipment_name condition must specify an equipment slot.");
        }
        try {
            this.namePattern = Pattern.compile(template.namePattern);
        } catch (PatternSyntaxException exception) {
            throw new MalformedKumoTemplateException(
                    "Invalid equipment name pattern: " + exception.getMessage(), exception);
        }
        this.slot = template.slot;
    }

    @Override
    public boolean isConditionMet(ITriggerConditionContext context) {
        Entity entity = context.getEntityData().getEntity();

        if (entity instanceof Player) {
            Player player = (Player) entity;
            ItemStack itemStack = player.getItemBySlot(this.slot);
            return namePattern.matcher(itemStack.getHoverName().getString()).matches();
        }

        return false;
    }

    public static class Template extends TriggerConditionTemplate {

        public String namePattern;
        public EquipmentSlot slot;
    }
}
