package com.teampotato.despawn_tweaker.mixin;

import com.google.common.collect.Sets;
import com.teampotato.despawn_tweaker.api.IMob;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Set;

@Mixin(Mob.class)
public abstract class MobMixin implements IMob {
    @Unique
    private Set<StructureFeature<?>> despawnTweaker$spawnStructures = Sets.newHashSet();

    @Override
    public Set<StructureFeature<?>> despawnTweaker$getSpawnStructures() {
        return this.despawnTweaker$spawnStructures;
    }

    @Override
    public void despawnTweaker$setSpawnStructures(Set<StructureFeature<?>> structureFeature) {
        this.despawnTweaker$spawnStructures = structureFeature;
    }
}
