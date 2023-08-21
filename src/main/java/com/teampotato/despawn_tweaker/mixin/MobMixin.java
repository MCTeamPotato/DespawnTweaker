package com.teampotato.despawn_tweaker.mixin;

import com.google.common.collect.Sets;
import com.teampotato.despawn_tweaker.api.IMob;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Set;

@Mixin(Mob.class)
public abstract class MobMixin implements IMob {
    @Unique
    private Set<Structure> despawnTweaker$spawnStructures = Sets.newHashSet();

    @Override
    public Set<Structure> despawnTweaker$getSpawnStructures() {
        return this.despawnTweaker$spawnStructures;
    }

    @Override
    public void despawnTweaker$setSpawnStructures(Set<Structure> structureFeature) {
        this.despawnTweaker$spawnStructures = structureFeature;
    }
}
