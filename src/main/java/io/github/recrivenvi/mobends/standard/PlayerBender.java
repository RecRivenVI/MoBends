package io.github.recrivenvi.mobends.standard;

import io.github.recrivenvi.mobends.core.bender.EntityBender;
import io.github.recrivenvi.mobends.core.data.IEntityDataFactory;
import io.github.recrivenvi.mobends.core.mutators.IMutatorFactory;
import io.github.recrivenvi.mobends.standard.client.renderer.entity.mutated.PlayerRenderer;
import io.github.recrivenvi.mobends.standard.data.PlayerData;
import io.github.recrivenvi.mobends.standard.main.ModStatics;
import io.github.recrivenvi.mobends.standard.mutators.PlayerMutator;
import net.minecraft.client.player.AbstractClientPlayer;

public class PlayerBender extends EntityBender<AbstractClientPlayer> {

    public PlayerBender() {
        super(ModStatics.MODID, "player", AbstractClientPlayer.class, new PlayerRenderer());
    }

    @Override
    public IEntityDataFactory<AbstractClientPlayer> getDataFactory() {
        return PlayerData::new;
    }

    @Override
    public IMutatorFactory<AbstractClientPlayer> getMutatorFactory() {
        return PlayerMutator::new;
    }
}
