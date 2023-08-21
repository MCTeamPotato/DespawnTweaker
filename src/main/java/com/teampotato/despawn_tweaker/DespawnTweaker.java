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
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> structuresMods;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> structures;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("DespawnTweaker");
        structuresMods = builder.comment("If you add modIDs to this list, only mobs in the structures of the mods will be affected by DespawnTweaker.").defineList("StructuresMods", Lists.newArrayList(), o -> o instanceof String);
        structures = builder.comment("If you add sturctures registry names to list, only mobs in the structures will be affected by DespawnTweaker.", "However, this can be combined with StructuresMods").defineList("Structures", Lists.newArrayList(), o -> o instanceof String);
        builder.pop();
        configSpec = builder.build();
    }
}

