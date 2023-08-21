package com.teampotato.despawn_tweaker;

import com.google.common.collect.Lists;
import com.teampotato.despawn_tweaker.event.SpawnChecker;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

import java.util.List;

@Mod(DespawnTweaker.MOD_ID)
public class DespawnTweaker {
    public static final String MOD_ID = "despawn_tweaker";
    public DespawnTweaker() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, configSpec);
        MinecraftForge.EVENT_BUS.register(SpawnChecker.class);
    }
    public static ForgeConfigSpec configSpec;

    public static final ForgeConfigSpec.BooleanValue allowMobsSpawnedBySpawnersToDespawn;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> structuresMods;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> structures;

    public static ForgeConfigSpec.BooleanValue enableLetMeDespawnOptimization;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("DespawnTweaker");
        allowMobsSpawnedBySpawnersToDespawn = builder.comment("Turn this off to allow the despawn of mobs spawned by spawners").define("allowMobsSpawnedBySpawnersToDespawn", false);
        structuresMods = builder.comment("If you add modIDs to this list, only mobs in the structures of the mods will be affected by DespawnTweaker.").defineList("StructuresMods", Lists.newArrayList(), o -> o instanceof String);
        structures = builder.comment("If you add sturctures registry names to list, only mobs in the structures will be affected by DespawnTweaker.", "However, this can be combined with StructuresMods").defineList("Structures", Lists.newArrayList(), o -> o instanceof String);
        builder.pop();
        builder.push("Optimization");
        enableLetMeDespawnOptimization = builder.comment("DespawnTweaker does contain the optimization of Let Me Despawn mod and resolves its potenial performance issue on equipments drop of despawning.", "Turn this off to disable the optimization").define("enableLetMeDespawnOptimization", true);
        builder.pop();
        configSpec = builder.build();
    }
}