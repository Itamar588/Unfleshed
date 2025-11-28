package com.unfleshed.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class SanguisVelumBlockEntity extends BlockEntity {

    private ItemStack storedItem = new ItemStack(Items.AIR);

    public SanguisVelumBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SANGUIS_VELUM, pos, state);
    }

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
}
