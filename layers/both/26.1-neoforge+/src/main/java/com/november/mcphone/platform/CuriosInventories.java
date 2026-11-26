package com.november.mcphone.platform;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.Optional;
import java.util.function.Predicate;

/** Curios inventory seam for 26.x NeoForge. */
public final class CuriosInventories {

    private CuriosInventories() {}

    public record Slot(String slotId, int index, ItemStack stack) {}

    public static boolean isEquipped(LivingEntity entity, Predicate<ItemStack> filter) {
        return of(entity).map(inventory -> inventory.isEquipped(filter)).orElse(false);
    }

    public static Optional<Slot> findFirst(LivingEntity entity, Predicate<ItemStack> filter) {
        return of(entity)
                .flatMap(inventory -> inventory.findFirstCurio(filter))
                .map(result -> new Slot(result.slotContext().identifier(),
                        result.slotContext().index(), result.stack()));
    }

    public static ItemStack stackAt(LivingEntity entity, String slotId, int index) {
        return of(entity)
                .flatMap(inventory -> inventory.findCurio(slotId, index))
                .map(result -> result.stack())
                .orElse(ItemStack.EMPTY);
    }

    public static void setEquipped(LivingEntity entity, String slotId, int index, ItemStack stack) {
        of(entity).ifPresent(inventory -> inventory.setEquippedCurio(slotId, index, stack));
    }

    private static Optional<ICuriosItemHandler> of(LivingEntity entity) {
        return CuriosApi.getCuriosInventory(entity);
    }
}
