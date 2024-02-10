package com.teampotato.despawn_tweaker.api;

import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;

import java.util.Set;

public interface IMob {
    Set<ConfiguredStructureFeature<?, ?>> despawnTweaker$getSpawnStructures();
    void despawnTweaker$setSpawnStructures(Set<ConfiguredStructureFeature<?, ?>> structureFeature);
}
