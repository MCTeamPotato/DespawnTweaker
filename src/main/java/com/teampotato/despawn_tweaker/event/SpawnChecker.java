package com.teampotato.despawn_tweaker.event;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.teampotato.despawn_tweaker.DespawnTweaker;
import com.teampotato.despawn_tweaker.api.IMob;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("resource")
public class SpawnChecker {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onSpawn(MobSpawnEvent.FinalizeSpawn event) {
        if (event.isSpawnCancelled()) return;
        if (DespawnTweaker.ALLOW_MOBS_SPAWNED_BY_SPAWNERS_TO_DESPAWN.get()) return;
        Mob entity = event.getEntity();
        if (!event.getSpawnType().equals(MobSpawnType.SPAWNER)) return;
        if (entity.level().isClientSide) return;
        ((IMob) entity).despawnTweaker$setSpawnStructures(entity.level().getChunkAt(entity.blockPosition()).getAllReferences().keySet());
        entity.addTag(DespawnTweaker.MOD_ID + ".shouldNotDespawn");
    }

    private static final Supplier<Set<String>> STRUCTURE_MODS = Suppliers.memoize(() -> new HashSet<>(DespawnTweaker.STRUCTURES_MODS.get()));
    private static final Supplier<Set<String>> STRUCTURES = Suppliers.memoize(() -> new HashSet<>(DespawnTweaker.STRUCTURES.get()));
    private static final Supplier<Registry<Structure>> STRUCTURES_REGISTRY = Suppliers.memoize(() -> ServerLifecycleHooks.getCurrentServer().registryAccess().registryOrThrow(Registries.STRUCTURE));

    @SubscribeEvent
    public static void onDespwan(MobSpawnEvent.AllowDespawn event) {
        if (event.getResult().equals(Event.Result.DENY)) return;
        Mob entity = event.getEntity();
        if (entity.level().isClientSide) return;
        if (!entity.getTags().contains(DespawnTweaker.MOD_ID + ".shouldNotDespawn")) return;
        ChunkAccess levelChunk = entity.level().getChunk(entity.blockPosition().getX() >> 4, entity.blockPosition().getZ() >> 4, ChunkStatus.FULL, false);
        if (levelChunk == null) return;
        if (STRUCTURE_MODS.get().isEmpty() && STRUCTURES.get().isEmpty()) {
            event.setResult(Event.Result.DENY);
        } else {
            for (Structure structure : ((IMob) entity).despawnTweaker$getSpawnStructures()) {
                ResourceLocation registryName = STRUCTURES_REGISTRY.get().getKey(structure);
                if (registryName == null) continue;
                boolean canDeny = STRUCTURE_MODS.get().contains(registryName.getNamespace()) || STRUCTURES.get().contains(registryName.toString());
                if (!canDeny) continue;
                event.setResult(Event.Result.DENY);
                break;
            }
        }
    }
}
