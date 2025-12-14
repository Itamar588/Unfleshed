package com.unfleshed.items;

import com.unfleshed.damage.ModDamageTypes;
import com.unfleshed.particle.ModParticles;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class HemoclastItem extends SwordItem {

    // Keep track of active blood sprays
    private static final List<BloodSpray> activeSprays = new ArrayList<>();

    static {
        // Tick handler to update all active sprays
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            Iterator<BloodSpray> it = activeSprays.iterator();
            while (it.hasNext()) {
                BloodSpray spray = it.next();
                if (!spray.tick()) {
                    it.remove();
                }
            }
        });
    }

    public HemoclastItem(ToolMaterial material, int attackDamage, float attackSpeed, Settings settings) {
        super(material, attackDamage, attackSpeed, settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {

            if (user.getItemCooldownManager().isCoolingDown(this)) {
                return TypedActionResult.fail(user.getStackInHand(hand));
            }

            // Damage player
            user.damage(ModDamageTypes.dismemberment(world), 5.0F);
            com.unfleshed.events.BloodlettingEvent.EVENT.invoker().onBloodletting(user);

            // Add a new blood spray
            if (world instanceof ServerWorld serverWorld) {
                activeSprays.add(new BloodSpray(user, serverWorld, 3.0));
            }

            user.getItemCooldownManager().set(this, 100); // 5 seconds cooldown
        }

        return TypedActionResult.success(user.getStackInHand(hand));
    }

    // Helper class to manage a forward blood spray
    private static class BloodSpray {
        private final PlayerEntity player;
        private final ServerWorld world;
        private final double length;
        private final Random random;
        private int ticksRemaining = 20; // 1 second at 20 ticks/sec

        public BloodSpray(PlayerEntity player, ServerWorld world, double length) {
            this.player = player;
            this.world = world;
            this.length = length;
            this.random = new Random(world.getSeed() + player.getId());
        }

        public boolean tick() {
            if (ticksRemaining-- <= 0 || player.isRemoved() || player.getWorld() != world) {
                return false;
            }

            // Recalculate positions each tick so the spray follows the player
            Vec3d startPos = player.getPos().add(0, player.getStandingEyeHeight() * 0.7, 0);
            Vec3d look = player.getRotationVector().normalize();
            Vec3d endPos = startPos.add(look.multiply(length));

            int particlesThisTick = 5 + random.nextInt(3);
            for (int i = 0; i < particlesThisTick; i++) {
                double t = random.nextDouble();
                double x = startPos.x + (endPos.x - startPos.x) * t + (random.nextDouble() - 0.5) * 0.2;
                double y = startPos.y + (endPos.y - startPos.y) * t + (random.nextDouble() - 0.5) * 0.2;
                double z = startPos.z + (endPos.z - startPos.z) * t + (random.nextDouble() - 0.5) * 0.2;

                world.spawnParticles(ModParticles.BLOOD, x, y, z, 1, 0, 0, 0, 0.05);
            }

            return true; // still active
        }
    }
}
