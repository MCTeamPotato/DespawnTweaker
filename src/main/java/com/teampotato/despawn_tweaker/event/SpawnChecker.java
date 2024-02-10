package com.teampotato.despawn_tweaker.event;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.teampotato.despawn_tweaker.DespawnTweaker;
import com.teampotato.despawn_tweaker.api.IMob;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashSet;
import java.util.Set;

public class SpawnChecker {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onSpawn(LivingSpawnEvent.CheckSpawn event) {
        LivingEntity entity = event.getEntityLiving();
        if (event.isSpawner() && !DespawnTweaker.allowMobsSpawnedBySpawnersToDespawn.get() && !entity.level.isClientSide && !event.getResult().equals(Event.Result.DENY) && entity instanceof Mob) {
            Mob mob = (Mob) entity;
            ((IMob)mob).despawnTweaker$setSpawnStructures(entity.level.getChunkAt(entity.blockPosition()).getAllReferences().keySet());
            mob.addTag(DespawnTweaker.MOD_ID + ".shouldNotDespawn");
        }
    }

    private static final Supplier<Set<? extends String>> STRUCTURE_MODS = Suppliers.memoize(() -> new HashSet<String>(DespawnTweaker.structuresMods.get()));
    private static final Supplier<Set<? extends String>> STRUCTURES = Suppliers.memoize(() -> new HashSet<String>(DespawnTweaker.structures.get()));

    @SubscribeEvent
    public static void onDespwan(LivingSpawnEvent.AllowDespawn event) {
        LivingEntity entity = event.getEntityLiving();
        if (!entity.level.isClientSide && entity.getTags().contains(DespawnTweaker.MOD_ID + ".shouldNotDespawn") && entity instanceof Mob) {
            Mob mob = (Mob) entity;
            LevelChunk levelChunk = mob.level.getChunkAt(mob.blockPosition());
            if (levelChunk != null) {
                if (STRUCTURE_MODS.get().isEmpty() && STRUCTURES.get().isEmpty()) {
                    event.setResult(Event.Result.DENY);
                } else {
                    for (StructureFeature<?> structureFeature : ((IMob)mob).despawnTweaker$getSpawnStructures()) {
                        ResourceLocation registryName = structureFeature.getRegistryName();
                        if (registryName == null) continue;
                        if (STRUCTURE_MODS.get().contains(registryName.getNamespace()) || STRUCTURES.get().contains(registryName.toString())) {
                            event.setResult(Event.Result.DENY);
                            break;
                        }
                    }
                }
            }
        }
    }
}
