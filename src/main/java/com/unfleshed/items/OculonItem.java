package com.unfleshed.items;

import com.unfleshed.damage.ModDamageTypes;
import com.unfleshed.effect.ModEffects;
import com.unfleshed.network.ModPackets;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.util.math.Box;

import java.util.*;

public class OculonItem extends SwordItem {
    private static final Map<UUID, Set<Integer>> glowingEntitiesPerPlayer = new HashMap<>();
    private static final double GLOW_RADIUS = 30.0;
    private static final int GLOW_DURATION_TICKS = 2; // short, will refresh every tick

    // Track which players are holding the Oculon
    private static final Set<UUID> playersHoldingOculon = new HashSet<>();
    private static final Set<Integer> glowingEntities = new HashSet<>();

    public OculonItem(ToolMaterial material, int attackDamage, float attackSpeed, Settings settings) {
        super(material, attackDamage, attackSpeed, settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (world.isClient) return; // server-side only
        if (!(entity instanceof ServerPlayerEntity player)) return;

        boolean isHolding = isHoldingOculon(player);
        UUID playerId = player.getUuid();

        if (isHolding) {
            playersHoldingOculon.add(playerId);
            updateGlowingEntities(player); // dynamically adds/removes glow each tick
        } else if (playersHoldingOculon.remove(playerId)) {
            // Player stopped holding the Oculon: clear everything for them
            Set<Integer> glowing = glowingEntitiesPerPlayer.get(playerId);
            if (glowing != null) {
                for (int entityId : glowing) {
                    Entity target = player.getWorld().getEntityById(entityId);
                    if (target != null) {
                        target.setGlowing(false);
                        target.setCustomNameVisible(false);
                    }
                }
            }
            ModPackets.sendClearGlowPacket(player); // clear on client
            glowingEntitiesPerPlayer.remove(playerId);
        }
    }

    private void updateGlowingEntities(ServerPlayerEntity player) {
        World world = player.getWorld();
        Box box = player.getBoundingBox().expand(GLOW_RADIUS);

        // Currently glowing entities for this player
        Set<Integer> currentlyGlowing = glowingEntitiesPerPlayer.computeIfAbsent(player.getUuid(), k -> new HashSet<>());
        Set<Integer> newGlowing = new HashSet<>();

        // Entities that should glow
        world.getEntitiesByClass(LivingEntity.class, box, e -> e != player && e.isAlive())
                .forEach(entity -> {
                    int id = entity.getId();
                    newGlowing.add(id);

                    // If not already glowing for this player, send glow packet
                    if (!currentlyGlowing.contains(id)) {
                        ModPackets.sendGlowPacket(player, entity, GLOW_DURATION_TICKS);
                    }
                });

        // Entities that should stop glowing
        for (int id : currentlyGlowing) {
            if (!newGlowing.contains(id)) {
                Entity entity = world.getEntityById(id);
                if (entity != null) {
                    entity.setGlowing(false);
                    entity.setCustomNameVisible(false);

                    // Send clear packet for this entity to the client
                    ModPackets.sendClearGlowPacket(player); // <-- could also make a packet for individual entity if you want finer control
                }
            }
        }

        // Update the map for this player
        glowingEntitiesPerPlayer.put(player.getUuid(), newGlowing);
    }




    //private void clearGlowingEntities(ServerPlayerEntity player) {
        //World world = player.getWorld();
        //for (int entityId : glowingEntities) {
            //Entity entity = world.getEntityById(entityId);
            //if (entity != null) {
                //entity.setGlowing(false);
                //entity.setCustomNameVisible(false);
            //}
        //}
        //glowingEntities.clear();
    //}

    private boolean isHoldingOculon(PlayerEntity player) {
        return player.getMainHandStack().getItem() == this ||
                player.getOffHandStack().getItem() == this;
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker.getWorld().isClient) return super.postHit(stack, target, attacker);
        if (!(attacker instanceof PlayerEntity player)) return super.postHit(stack, target, attacker);


        StatusEffectInstance current = target.getStatusEffect(ModEffects.GAZE);
        int newAmplifier = current != null ? Math.min(current.getAmplifier() + 1, 9) : 0;

        if (current != null) target.removeStatusEffect(ModEffects.GAZE);

        target.addStatusEffect(new StatusEffectInstance(
                ModEffects.GAZE,
                300,
                newAmplifier,
                false, // ambient
                false, // hide particles
                true   // show icon
        ));


        return super.postHit(stack, target, attacker);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);

        if (player.getItemCooldownManager().isCoolingDown(this)) {
            return TypedActionResult.fail(stack);
        }

        if (!world.isClient) {
            shootLaser(world, player);
            player.getItemCooldownManager().set(this, 15 * 20); // 15s cooldown
        }

        return TypedActionResult.success(stack);
    }


    private void shootLaser(World world, PlayerEntity player) {
        // Start position at player's chest level
        Vec3d start = player.getPos().add(0, player.getStandingEyeHeight() * 0.7, 0);

        // Calculate end position (30 blocks in the direction player is looking)
        float reachDistance = 40.0f;  // 30 blocks max distance
        Vec3d look = player.getRotationVec(1.0F);
        Vec3d maxEnd = start.add(look.multiply(reachDistance));

        // Raycast to find hit position
        HitResult hit = world.raycast(new RaycastContext(
                start,
                maxEnd,  // Use maxEnd as the end point for the raycast
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                player
        ));

        // Use the hit position if we hit something, otherwise use maxEnd
        Vec3d end = hit.getType() != HitResult.Type.MISS ? hit.getPos() : maxEnd;

        // Send packet to show laser on all clients
        if (!world.isClient) {
            ModPackets.sendLaserLine(world, start, end);
        }

        // Rest of your effect application code...
        if (!world.isClient) {
            // Create a box that encompasses the laser's path
            Box laserBox = new Box(
                    Math.min(start.x, end.x) - 1.0,
                    Math.min(start.y, end.y) - 1.0,
                    Math.min(start.z, end.z) - 1.0,
                    Math.max(start.x, end.x) + 1.0,
                    Math.max(start.y, end.y) + 1.0,
                    Math.max(start.z, end.z) + 1.0
            );

            // Check for entities in the laser's path
            for (Entity entity : world.getOtherEntities(player, laserBox)) {
                if (!(entity instanceof LivingEntity livingEntity)) continue;

                Vec3d entityPos = livingEntity.getPos();
                Vec3d beamDir = end.subtract(start).normalize();
                Vec3d toEntity = entityPos.subtract(start);
                double projection = toEntity.dotProduct(beamDir);

                projection = Math.max(0, Math.min(projection, start.distanceTo(end)));
                Vec3d closestPoint = start.add(beamDir.multiply(projection));
                double distanceToBeam = entityPos.distanceTo(closestPoint);

                if (distanceToBeam <= 2.0) {
                    StatusEffectInstance current = livingEntity.getStatusEffect(ModEffects.GAZE);
                    int stacks = current != null ? current.getAmplifier() + 1 : 0;

                    float damage = 5f + stacks * 3f;
                    livingEntity.damage(ModDamageTypes.dismemberment(world), damage);

                    if (current != null) livingEntity.removeStatusEffect(ModEffects.GAZE);
                }
            }
        }
    }



    private boolean isEntityInLaserPath(Entity entity, Vec3d start, Vec3d end) {
        // Simple distance check - for more accuracy, you might want to implement
        // a ray-entity intersection test
        Vec3d entityPos = entity.getPos();
        double distance = entityPos.distanceTo(start);
        double totalDistance = start.distanceTo(end);

        // If entity is beyond the laser's range, skip
        if (distance > totalDistance) return false;

        // Check if entity is close enough to the line
        Vec3d lineDir = end.subtract(start).normalize();
        Vec3d toEntity = entityPos.subtract(start);
        double projection = toEntity.dotProduct(lineDir);
        Vec3d closestPoint = start.add(lineDir.multiply(projection));
        double distanceToLine = entityPos.distanceTo(closestPoint);

        return distanceToLine < 1.0; // 1 block radius around the laser
    }
}




