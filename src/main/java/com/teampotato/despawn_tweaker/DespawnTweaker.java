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
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON_CONFIG);
        MinecraftForge.EVENT_BUS.register(SpawnChecker.class);
    }

    public static final ForgeConfigSpec COMMON_CONFIG;
    public static final ForgeConfigSpec.BooleanValue ALLOW_MOBS_SPAWNED_BY_SPAWNERS_TO_DESPAWN;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> STRUCTURES_MODS;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> STRUCTURES;
    public static final ForgeConfigSpec.BooleanValue ENABLE_LET_ME_DESPAWN_OPTIMIZATION;
    public static final ForgeConfigSpec.BooleanValue ALLOW_EQUIPMENT_DROPS;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("DespawnTweaker");
        ALLOW_MOBS_SPAWNED_BY_SPAWNERS_TO_DESPAWN = builder.comment("Turn this off to disable the despawn of mobs spawned by spawners").define("allowMobsSpawnedBySpawnersToDespawn", true);
        STRUCTURES_MODS = builder.comment("If you add modIDs to this list, only mobs in the structures of the mods will be affected by DespawnTweaker.").defineList("StructuresMods", new ObjectArrayList<>(), o -> o instanceof String);
        STRUCTURES = builder.comment("If you add sturctures registry names to list, only mobs in the structures will be affected by DespawnTweaker.", "This can be combined with StructuresMods").defineList("Structures", new ObjectArrayList<>(), o -> o instanceof String);
        builder.pop();
        builder.push("Optimization");
        ENABLE_LET_ME_DESPAWN_OPTIMIZATION = builder.comment("DespawnTweaker does contain the optimization of Let Me Despawn mod and resolves its potenial performance issue on equipments drop of despawning.", "Turn this off to disable the optimization").define("enableLetMeDespawnOptimization", true);
        ALLOW_EQUIPMENT_DROPS = builder.comment("Turn this off to disable the equipments drop on mobs despawn").define("allowEquipmentDrops", true);
        builder.pop();
        COMMON_CONFIG = builder.build();
    }
}
