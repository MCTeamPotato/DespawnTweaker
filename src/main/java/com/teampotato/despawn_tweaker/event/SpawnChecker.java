package com.teampotato.despawn_tweaker.event;

import com.teampotato.despawn_tweaker.DespawnTweaker;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class SpawnChecker {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onSpawn(LivingSpawnEvent.CheckSpawn event) {
        LivingEntity entity = event.getEntityLiving();
        if (event.isSpawner() && !entity.level.isClientSide && !event.getResult().equals(Event.Result.DENY)) {
            entity.addTag(DespawnTweaker.MOD_ID + ".shouldNotDespawn");
        }
    }

    @SubscribeEvent
    public static void onDespwan(LivingSpawnEvent.AllowDespawn event) {
        LivingEntity entity = event.getEntityLiving();
        if (!entity.level.isClientSide && entity.getTags().contains(DespawnTweaker.MOD_ID + ".shouldNotDespawn")) {
            event.setResult(Event.Result.DENY);
        }
    }
}
