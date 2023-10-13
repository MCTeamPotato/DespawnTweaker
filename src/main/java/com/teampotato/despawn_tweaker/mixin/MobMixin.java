package com.teampotato.despawn_tweaker.mixin;

import com.teampotato.despawn_tweaker.DespawnTweaker;
import com.teampotato.despawn_tweaker.api.IMob;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(Mob.class)
public abstract class MobMixin extends LivingEntity implements IMob {
    @Shadow private boolean persistenceRequired;
    @Unique
    private boolean despawnTweaker$pickedItems = false;

    @Inject(method = "setItemSlotAndDropWhenKilled", at = @At("TAIL"))
    private void onSet(EquipmentSlot arg, ItemStack arg2, CallbackInfo ci) {
        if (DespawnTweaker.enableLetMeDespawnOptimization.get()) {
            this.despawnTweaker$pickedItems = true;
            this.persistenceRequired = this.hasCustomName();
        }
    }

    @Inject(method = "checkDespawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;discard()V", shift = At.Shift.AFTER))
    private void onDespawn(CallbackInfo ci) {
        if (DespawnTweaker.enableLetMeDespawnOptimization.get() && this.despawnTweaker$pickedItems) this.despawnTweaker$dropEquipmentOnDespawn();
    }

    @Unique
    private static final EquipmentSlot[] despawnTweaker$slots = EquipmentSlot.values();

    @Unique
    private void despawnTweaker$dropEquipmentOnDespawn() {
        if (!DespawnTweaker.allowEquipmentDrops.get()) return;
        for (EquipmentSlot equipmentSlot : despawnTweaker$slots) {
            ItemStack itemStack = this.getItemBySlot(equipmentSlot);
            CompoundTag tag = itemStack.getTag();
            if (!itemStack.isEmpty() && !(tag != null && tag.toString().contains("vanishing_curse"))) {
                this.spawnAtLocation(itemStack);
                this.setItemSlot(equipmentSlot, ItemStack.EMPTY);
            }
        }
    }

    @Unique
    private Set<Structure> despawnTweaker$spawnStructures = new ObjectOpenHashSet<>();

    @Override
    public Set<Structure> despawnTweaker$getSpawnStructures() {
        return this.despawnTweaker$spawnStructures;
    }

    @Override
    public void despawnTweaker$setSpawnStructures(Set<Structure> structureFeature) {
        this.despawnTweaker$spawnStructures = structureFeature;
    }

    protected MobMixin(EntityType<? extends LivingEntity> arg, Level arg2) {
        super(arg, arg2);
    }
}
