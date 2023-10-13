package com.teampotato.despawn_tweaker;

import com.teampotato.despawn_tweaker.event.SpawnChecker;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
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
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> structuresMods;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> structures;
    public static final ForgeConfigSpec.BooleanValue enableLetMeDespawnOptimization;

    public static final ForgeConfigSpec.BooleanValue allowEquipmentDrops;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("DespawnTweaker");
        allowMobsSpawnedBySpawnersToDespawn = builder.comment("Turn this off to disable the despawn of mobs spawned by spawners").define("allowMobsSpawnedBySpawnersToDespawn", true);
        structuresMods = builder.comment("If you add modIDs to this list, only mobs in the structures of the mods will be affected by DespawnTweaker.").defineList("StructuresMods", new ObjectArrayList<>(), o -> o instanceof String);
        structures = builder.comment("If you add sturctures registry names to list, only mobs in the structures will be affected by DespawnTweaker.", "This can be combined with StructuresMods").defineList("Structures", new ObjectArrayList<>(), o -> o instanceof String);
        builder.pop();
        builder.push("Optimization");
        enableLetMeDespawnOptimization = builder.comment("DespawnTweaker does contain the optimization of Let Me Despawn mod and resolves its potenial performance issue on equipments drop of despawning.", "Turn this off to disable the optimization").define("enableLetMeDespawnOptimization", true);
        allowEquipmentDrops = builder.comment("Turn this off to disable the equipments drop on mobs despawn").define("allowEquipmentDrops", true);
        builder.pop();
        configSpec = builder.build();
    }
}
