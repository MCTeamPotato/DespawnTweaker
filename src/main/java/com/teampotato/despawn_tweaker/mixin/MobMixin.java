package com.teampotato.despawn_tweaker.mixin;

import com.teampotato.despawn_tweaker.DespawnTweaker;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Mob.class)
public abstract class MobMixin extends LivingEntity {
    protected MobMixin(EntityType<? extends LivingEntity> arg, Level arg2) {
        super(arg, arg2);
    }

    @Inject(method = "removeWhenFarAway", at = @At("HEAD"), cancellable = true)
    private void onDespawn(double dist, CallbackInfoReturnable<Boolean> cir) {
        if (this.level.isClientSide) return;
        ChunkAccess chunkAccess = this.level.getChunk(this.blockPosition());
        if (chunkAccess instanceof LevelChunk) {
            LevelChunk chunk = (LevelChunk) chunkAccess;
            if (this.getTags().contains(DespawnTweaker.MOD_ID + ".shouldNotDespawn")) {
                List<? extends String> structuresMods = DespawnTweaker.structuresMods.get();
                List<? extends String> structures = DespawnTweaker.structures.get();
                if (structuresMods.isEmpty() && structures.isEmpty()) {
                    cir.setReturnValue(false);
                    cir.cancel();
                } else {
                    for (StructureFeature<?> structureFeature : chunk.getAllReferences().keySet()) {
                        ResourceLocation registryName = structureFeature.getRegistryName();
                        if (registryName == null) continue;
                        if (structuresMods.contains(registryName.getNamespace()) || structures.contains(registryName.toString())) {
                            cir.setReturnValue(false);
                            cir.cancel();
                        }
                    }
                }
            }
        }
    }
}
