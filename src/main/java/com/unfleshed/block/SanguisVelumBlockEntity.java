package com.unfleshed.block;

import com.unfleshed.items.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.particle.ParticleGroup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
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
    private int ritualTicks = 0;
    private static final int RITUAL_DURATION = 100; // longer floating duration
    private int ritualPhase = 0; // 0 = idle, 1 = rising, 2 = lowering
    private transient PlayerEntity ritualOwner = null;

    public SanguisVelumBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SANGUIS_VELUM, pos, state);
    }

    public static final Set<SanguisVelumBlockEntity> ACTIVE_ALTARS =
            Collections.newSetFromMap(new WeakHashMap<>());

    public void setStoredItem(ItemStack stack) {
        this.storedItem = stack != null ? stack.copy() : new ItemStack(Items.AIR);
        markDirty();
        if (world != null && !world.isClient) {
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
            BlockEntityUpdateS2CPacket pkt = BlockEntityUpdateS2CPacket.create(this);
            ((ServerWorld) world).getPlayers(p -> true).forEach(p -> p.networkHandler.sendPacket(pkt));
        }

        if (storedItem != null && !storedItem.isEmpty() && storedItem.getItem() != Items.AIR) {
            ACTIVE_ALTARS.add(this);
        } else {
            ACTIVE_ALTARS.remove(this);
        }
    }

    public ItemStack getStoredItem() {
        return storedItem != null ? storedItem : new ItemStack(Items.AIR);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.put("StoredItem", storedItem.writeNbt(new NbtCompound()));
        nbt.putInt("RitualTicks", ritualTicks);
        nbt.putInt("RitualPhase", ritualPhase);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        storedItem = nbt.contains("StoredItem") ?
                ItemStack.fromNbt(nbt.getCompound("StoredItem")) : new ItemStack(Items.AIR);
        ritualTicks = nbt.getInt("RitualTicks");
        ritualPhase = nbt.getInt("RitualPhase");
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
        ACTIVE_ALTARS.remove(this);
    }

    public void beginRitual(PlayerEntity player) {
        if (world == null || world.isClient) return;
        if (ritualPhase != 0) return;

        ItemStack current = getStoredItem();
        if (current == null || current.isEmpty() || current.getItem() == Items.AIR) return;

        ritualPhase = 1; // rising
        ritualTicks = 0;
        ritualOwner = player;
        markDirty();
        world.updateListeners(pos, getCachedState(), getCachedState(), 3);
    }

    public void tickServer() {
        if (world == null || world.isClient) return;
        if (ritualPhase == 0) return;

        ritualTicks++;

        // Rising
        if (ritualPhase == 1 && ritualTicks >= RITUAL_DURATION) {
            ritualPhase = 2; // switch to lowering
            ritualTicks = 0;

            // Particle explosion at top
            if (world instanceof ServerWorld serverWorld) {
                serverWorld.spawnParticles(ParticleTypes.SOUL_FIRE_FLAME,
                        pos.getX() + 0.5, pos.getY() + 2.0, pos.getZ() + 0.5,
                        20, 0.3, 0.3, 0.3, 0.1);
            }

            // Swap the item to the result
            setStoredItem(getRitualResultFor(storedItem));
        }

        // Lowering
        if (ritualPhase == 2 && ritualTicks >= RITUAL_DURATION / 2) { // 2x speed down
            ritualPhase = 0;
            ritualTicks = 0;
            ritualOwner = null;
        }

        markDirty();
        world.updateListeners(pos, getCachedState(), getCachedState(), 3);
    }

    private ItemStack getRitualResultFor(ItemStack input) {
        if (input == null || input.isEmpty()) return new ItemStack(Items.AIR);
        Item in = input.getItem();
        if (in == ModItems.HUMAN_EYES) return new ItemStack(ModItems.ARCANE_EYES);
        if (in == Items.AMETHYST_SHARD) return new ItemStack(ModItems.HEMOCRYSTAL);
        return new ItemStack(Items.AIR);
    }

    public int getRitualTicks() {
        return ritualTicks;
    }

    public int getRitualPhase() {
        return ritualPhase;
    }

    public int getRitualDuration() {
        return RITUAL_DURATION;
    }
}
