package com.unfleshed.block;

import com.unfleshed.items.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

public class SanguisVelumBlockEntity extends BlockEntity {

    private ItemStack storedItem = new ItemStack(Items.AIR);

    // -------------------------
    // 🎇 RITUAL FIELDS
    // -------------------------
    private boolean ritualActive = false;
    private int ritualTicks = 0;
    private static final int RITUAL_DURATION = 60; // 3 seconds
    private transient PlayerEntity ritualOwner = null; // not saved in NBT

    public SanguisVelumBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SANGUIS_VELUM, pos, state);
    }






    /** Holds all altars that currently have a non-air item ready for ritual */
    public static final Set<SanguisVelumBlockEntity> ACTIVE_ALTARS =
            Collections.newSetFromMap(new WeakHashMap<>());

    public void setStoredItem(ItemStack stack) {
        this.storedItem = stack != null ? stack.copy() : new ItemStack(Items.AIR);
        markDirty();

        if (world != null && !world.isClient) {
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);

            // send update to all nearby players
            BlockEntityUpdateS2CPacket pkt = BlockEntityUpdateS2CPacket.create(this);
            ((ServerWorld) world).getPlayers(player -> true)
                    .forEach(p -> p.networkHandler.sendPacket(pkt));

            System.out.println("[SanguisVelumBlockEntity] Stored item set to: " + stack);
        }

        // 🔥 Update ACTIVE_ALTARS automatically
        if (storedItem != null && !storedItem.isEmpty() && storedItem.getItem() != Items.AIR) {
            if (!ACTIVE_ALTARS.contains(this)) {
                ACTIVE_ALTARS.add(this);
                System.out.println("[SanguisVelumBlockEntity] Added to ACTIVE_ALTARS: " + pos);
            }
        } else {
            if (ACTIVE_ALTARS.remove(this)) {
                System.out.println("[SanguisVelumBlockEntity] Removed from ACTIVE_ALTARS: " + pos);
            }
        }
    }

    public ItemStack getStoredItem() {
        return storedItem != null ? storedItem : new ItemStack(Items.AIR);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        storedItem = nbt.contains("StoredItem") ?
                ItemStack.fromNbt(nbt.getCompound("StoredItem")) : new ItemStack(Items.AIR);
        System.out.println("[SanguisVelumBlockEntity] readNbt: storedItem = " + storedItem);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.put("StoredItem", storedItem.writeNbt(new NbtCompound()));
        System.out.println("[SanguisVelumBlockEntity] writeNbt: storedItem = " + storedItem);
    }

    @Nullable
    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound nbt = new NbtCompound();
        writeNbt(nbt);
        return nbt;
    }

    @Override
    public void markRemoved() {
        super.markRemoved();
        if (ACTIVE_ALTARS.remove(this)) {
            System.out.println("[SanguisVelumBlockEntity] Removed from ACTIVE_ALTARS (chunk unload): " + pos);
        }
    }

    /** Called when bloodletting event picks THIS altar */
    public void beginRitual(PlayerEntity player) {
        if (world == null || world.isClient) return;
        if (ritualActive) return;

        ItemStack current = getStoredItem();
        if (current == null || current.isEmpty() || current.getItem() == Items.AIR) return;

        ritualActive = true;
        ritualTicks = 0;
        ritualOwner = player;

        markDirty();
        world.updateListeners(pos, getCachedState(), getCachedState(), 3);

        System.out.println("[SanguisVelum] Ritual started at " + pos + " by " + player.getName().getString());
    }

    /** Called every server tick via global tick handler */
    public void tickServer() {
        if (!ritualActive || world == null || world.isClient) return;

        ritualTicks++;

        if (ritualTicks % 5 == 0) {
            markDirty();
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }

        if (ritualTicks >= RITUAL_DURATION) {
            completeRitual();
        }
    }

    private void completeRitual() {
        ritualActive = false;
        ritualTicks = 0;

        ItemStack input = getStoredItem();
        ItemStack output = getRitualResultFor(input);

        setStoredItem(output);

        ritualOwner = null;

        System.out.println("[SanguisVelum] Ritual complete at " + pos + " -> " + output);
    }

    private ItemStack getRitualResultFor(ItemStack input) {
        if (input == null || input.isEmpty()) return new ItemStack(Items.AIR);
        Item in = input.getItem();

        if (in == ModItems.HUMAN_EYES) return new ItemStack(ModItems.ARCANE_EYES);
        if (in == Items.AMETHYST_SHARD) return new ItemStack(Items.DIAMOND);

        return new ItemStack(Items.AIR);
    }
}
