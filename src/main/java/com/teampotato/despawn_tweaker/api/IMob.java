package com.teampotato.despawn_tweaker.api;

import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.Set;

public interface IMob {
    Set<Structure> despawnTweaker$getSpawnStructures();
    void despawnTweaker$setSpawnStructures(Set<Structure> structureFeature);
}
