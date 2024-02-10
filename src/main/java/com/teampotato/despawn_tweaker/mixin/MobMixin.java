package com.teampotato.despawn_tweaker.mixin;

import com.teampotato.despawn_tweaker.DespawnTweaker;
import com.teampotato.despawn_tweaker.api.IMob;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.Set;

@Mixin(Mob.class)
public abstract class MobMixin extends LivingEntity implements IMob {
    @Shadow private boolean persistenceRequired;

    @Shadow public abstract ItemStack getItemBySlot(EquipmentSlot arg);

    protected MobMixin(EntityType<? extends LivingEntity> arg, Level arg2) {
        super(arg, arg2);
    }

    @Inject(method = "setItemSlotAndDropWhenKilled", at = @At("TAIL"))
    private void onSetItemSlotAndDropWhenKilled(EquipmentSlot arg, ItemStack itemStack, CallbackInfo ci) {
        if (DespawnTweaker.ENABLE_LET_ME_DESPAWN_OPTIMIZATION.get()) {
            EquipmentSlot equipmentSlot = Mob.getEquipmentSlotForItem(itemStack);
            ItemStack stack = this.getItemBySlot(equipmentSlot);
            stack.getOrCreateTag().putBoolean("DespawnTweakerPicked", true);
            this.addTag("despawnTweaker.pickedItems");
            this.persistenceRequired = this.hasCustomName();
        }
    }

    @Inject(method = "checkDespawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;remove()V", shift = At.Shift.AFTER))
    private void onDespawn(CallbackInfo ci) {
        if (DespawnTweaker.ENABLE_LET_ME_DESPAWN_OPTIMIZATION.get() && this.getTags().contains("despawnTweaker.pickedItems")) {
            this.despawnTweaker$dropEquipmentOnDespawn();
        }
    }

    @Unique
    private static final EquipmentSlot[] EQUIPMENT_SLOTS = EquipmentSlot.values();

    @Unique
    private void despawnTweaker$dropEquipmentOnDespawn() {
        if (!DespawnTweaker.ALLOW_EQUIPMENT_DROPS.get()) return;
        for (EquipmentSlot equipmentSlot : EQUIPMENT_SLOTS) {
            ItemStack itemStack = this.getItemBySlot(equipmentSlot);
            CompoundTag tag = itemStack.getTag();
            boolean tagPresent = tag != null;
            if (!itemStack.isEmpty() && !(tagPresent && tag.toString().contains("vanishing_curse"))) {
                if (tagPresent && tag.getBoolean("DespawnTweakerPicked")) itemStack.removeTagKey("Picked");
                this.spawnAtLocation(itemStack);
                this.setItemSlot(equipmentSlot, ItemStack.EMPTY);
            }
        }
    }

    @Unique
    private void despawnTweaker$removeTagOnDeath() {
        for (EquipmentSlot equipmentSlot : EQUIPMENT_SLOTS) {
            ItemStack itemStack = this.getItemBySlot(equipmentSlot);
            CompoundTag tag = itemStack.getTag();
            boolean tagPresent = tag != null;
            if (tagPresent && !itemStack.isEmpty() && !tag.toString().contains("vanishing_curse") && tag.getBoolean("DespawnTweakerPicked")) {
                itemStack.removeTagKey("DespawnTweakerPicked");
            }
        }
    }

    @Inject(method = {"dropFromLootTable", "dropCustomDeathLoot"}, at = @At("HEAD"))
    private void onDropFromLootTable(CallbackInfo ci) {
        if (this.getTags().contains("despawnTweaker.pickedItems")) this.despawnTweaker$removeTagOnDeath();
    }

    @Unique private @Nullable Set<StructureFeature<?>> despawnTweaker$spawnStructures = null;

    @Override
    public @NotNull Set<StructureFeature<?>> despawnTweaker$getSpawnStructures() {
        return this.despawnTweaker$spawnStructures == null ? Collections.emptySet() : this.despawnTweaker$spawnStructures;
    }

    @Override
    public void despawnTweaker$setSpawnStructures(Set<StructureFeature<?>> structureFeature) {
        this.despawnTweaker$spawnStructures = structureFeature;
    }
}
