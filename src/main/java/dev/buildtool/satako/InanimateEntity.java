package dev.buildtool.satako;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HandSide;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Collections;

public class InanimateEntity extends LivingEntity {
    public InanimateEntity(EntityType<? extends LivingEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return Collections.emptyList();
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlotType slotIn) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(EquipmentSlotType slotIn, ItemStack stack) {

    }

    @Override
    public HandSide getMainArm() {
        return HandSide.RIGHT;
    }


    @Override
    public boolean attackable() {
        return false;
    }

    @Override
    public boolean isAttackable() {
        return false;
    }
}
