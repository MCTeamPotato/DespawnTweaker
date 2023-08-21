package com.teampotato.despawn_tweaker.api;

import net.minecraft.world.level.levelgen.feature.StructureFeature;

import java.util.Set;

public interface IMob {
    Set<StructureFeature<?>> despawnTweaker$getSpawnStructures();
    void despawnTweaker$setSpawnStructures(Set<StructureFeature<?>> structureFeature);
}
