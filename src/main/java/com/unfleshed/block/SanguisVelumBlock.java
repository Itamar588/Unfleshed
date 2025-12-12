package com.unfleshed.block;

import com.unfleshed.items.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class SanguisVelumBlock extends Block implements BlockEntityProvider {

    public static final Set<Item> SPECIAL_ITEMS = new HashSet<>();

    static {
        SPECIAL_ITEMS.add(ModItems.HUMAN_EYES);
        SPECIAL_ITEMS.add(Items.AMETHYST_SHARD);
    }

    private static final VoxelShape SHAPE = VoxelShapes.union(
            Block.createCuboidShape(2, 0, 2, 14, 12, 14)
    );

    public SanguisVelumBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public SanguisVelumBlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SanguisVelumBlockEntity(pos, state);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {

        if (hand != Hand.MAIN_HAND) return ActionResult.PASS;

        ItemStack held = player.getStackInHand(hand);
        SanguisVelumBlockEntity entity = (SanguisVelumBlockEntity) world.getBlockEntity(pos);
        if (entity == null) return ActionResult.PASS;

        ItemStack current = entity.getStoredItem();

        // Empty hand -> take stored item
        if (held.isEmpty() && !current.isEmpty()) {
            if (!world.isClient) {
                player.setStackInHand(hand, current.copy());
                entity.setStoredItem(new ItemStack(Items.AIR));
            }
            return ActionResult.SUCCESS;
        }

        if (SPECIAL_ITEMS.contains(held.getItem())) {
            if (!world.isClient) {
                ItemStack previous = current.copy();
                entity.setStoredItem(held.copy());

                if (!previous.isEmpty()) player.setStackInHand(hand, previous);
                else player.setStackInHand(hand, new ItemStack(Items.AIR));

                player.sendMessage(Text.literal("The velum reacts to your offering!"), true);
            }
            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (world.isClient) return null;
        if (type != ModBlockEntities.SANGUIS_VELUM) return null;

        return (w, pos, st, be) -> {
            if (be instanceof SanguisVelumBlockEntity sv) sv.tickServer();
        };
    }
}
