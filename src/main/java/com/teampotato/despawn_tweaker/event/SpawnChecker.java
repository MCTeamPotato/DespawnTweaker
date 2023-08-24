package com.teampotato.despawn_tweaker.event;

import com.teampotato.despawn_tweaker.DespawnTweaker;
import com.teampotato.despawn_tweaker.api.IMob;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;

public class SpawnChecker {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onSpawn(LivingSpawnEvent.CheckSpawn event) {
        if (DespawnTweaker.allowMobsSpawnedBySpawnersToDespawn.get()) return;
        LivingEntity entity = event.getEntityLiving();
        if (event.isSpawner() && !entity.level.isClientSide && !event.getResult().equals(Event.Result.DENY) && entity instanceof Mob mob) {
            ((IMob) mob).despawnTweaker$setSpawnStructures(entity.level.getChunkAt(entity.blockPosition()).getAllReferences().keySet());
            entity.addTag(DespawnTweaker.MOD_ID + ".shouldNotDespawn");
        }
    }

    @SubscribeEvent
    public static void onDespwan(LivingSpawnEvent.AllowDespawn event) {
        if (DespawnTweaker.allowMobsSpawnedBySpawnersToDespawn.get()) return;
        LivingEntity entity = event.getEntityLiving();
        if (!entity.level.isClientSide && entity.getTags().contains(DespawnTweaker.MOD_ID + ".shouldNotDespawn") && entity instanceof Mob mob) {
            List<? extends String> structuresMods = DespawnTweaker.structuresMods.get();
            List<? extends String> structures = DespawnTweaker.structures.get();
            if (structuresMods.isEmpty() && structures.isEmpty()) {
                event.setResult(Event.Result.DENY);
            } else {
                for (ConfiguredStructureFeature<?, ?> structure : ((IMob) mob).despawnTweaker$getSpawnStructures()) {
                    ResourceLocation registryName = BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.getKey(structure);
                    if (registryName == null) continue;
                    if (structuresMods.contains(registryName.getNamespace()) || structures.contains(registryName.toString())) {
                        event.setResult(Event.Result.DENY);
                        break;
                    }
                }
            }
        }
    }
}
