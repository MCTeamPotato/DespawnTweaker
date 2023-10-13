package com.teampotato.despawn_tweaker.event;

import com.teampotato.despawn_tweaker.DespawnTweaker;
import com.teampotato.despawn_tweaker.api.IMob;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;

@SuppressWarnings("resource")
public class SpawnChecker {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onSpawn(MobSpawnEvent.FinalizeSpawn event) {
        if (DespawnTweaker.allowMobsSpawnedBySpawnersToDespawn.get()) return;
        Mob entity = event.getEntity();
        if (event.getSpawnType().equals(MobSpawnType.SPAWNER) && !entity.level().isClientSide && !event.getResult().equals(Event.Result.DENY)) {
            ((IMob) entity).despawnTweaker$setSpawnStructures(entity.level().getChunkAt(entity.blockPosition()).getAllReferences().keySet());
            entity.addTag(DespawnTweaker.MOD_ID + ".shouldNotDespawn");
        }
    }

    @SubscribeEvent
    public static void onDespwan(MobSpawnEvent.AllowDespawn event) {
        if (DespawnTweaker.allowMobsSpawnedBySpawnersToDespawn.get()) return;
        Mob entity = event.getEntity();
        if (!entity.level().isClientSide && entity.getTags().contains(DespawnTweaker.MOD_ID + ".shouldNotDespawn")) {
            List<? extends String> structuresMods = DespawnTweaker.structuresMods.get();
            List<? extends String> structures = DespawnTweaker.structures.get();
            if (structuresMods.isEmpty() && structures.isEmpty()) {
                event.setResult(Event.Result.DENY);
            } else {
                for (Structure structure : ((IMob) entity).despawnTweaker$getSpawnStructures()) {
                    ResourceLocation registryName = entity.level().registryAccess().registryOrThrow(Registries.STRUCTURE).getKey(structure);
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
