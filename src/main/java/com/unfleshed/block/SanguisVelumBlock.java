package com.unfleshed.block;

import com.unfleshed.items.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
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
        SPECIAL_ITEMS.add(net.minecraft.item.Items.AMETHYST_SHARD);
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

        // Only react to main hand
        if (hand != Hand.MAIN_HAND) return ActionResult.PASS;

        ItemStack held = player.getStackInHand(hand);
        SanguisVelumBlockEntity entity = (SanguisVelumBlockEntity) world.getBlockEntity(pos);
        if (entity == null) return ActionResult.PASS;

        ItemStack current = entity.getStoredItem();

        System.out.println("[SanguisVelumBlock] Player clicked. Held: " + held + ", Block holds: " + current);

        // Empty hand -> take stored item
        if (held.isEmpty() && !current.isEmpty()) {
            if (!world.isClient) {
                player.setStackInHand(hand, current.copy());
                entity.setStoredItem(new ItemStack(Items.AIR));
                System.out.println("[SanguisVelumBlock] Gave stored item to player and cleared block.");
            }
            return ActionResult.SUCCESS;
        }

        // Special item logic
        if (SPECIAL_ITEMS.contains(held.getItem())) {

            // Do nothing if same item
            if (ItemStack.areItemsEqual(held, current)) {
                System.out.println("[SanguisVelumBlock] Clicked with same item, doing nothing.");
                return ActionResult.PASS;
            }

            if (!world.isClient) {
                ItemStack previous = current.copy();
                entity.setStoredItem(held.copy());

                if (!previous.isEmpty()) {
                    player.setStackInHand(hand, previous);
                    System.out.println("[SanguisVelumBlock] Swapped block item with player item.");
                } else {
                    player.setStackInHand(hand, new ItemStack(Items.AIR));
                    System.out.println("[SanguisVelumBlock] No previous item, removing held item from player.");
                }

                player.sendMessage(Text.literal("The velum reacts to your offering!"), true);
            }

            return ActionResult.SUCCESS;
        }

        // Non-special items do nothing
        System.out.println("[SanguisVelumBlock] Player clicked with non-special item. Doing nothing.");
        return ActionResult.PASS;
    }
}
