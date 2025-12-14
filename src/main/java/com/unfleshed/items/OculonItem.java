package com.unfleshed.items;

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
import net.minecraft.world.World;
import net.minecraft.util.math.Box;

import java.util.*;

public class OculonItem extends SwordItem {
    private static final Map<UUID, Set<Integer>> glowingEntitiesPerPlayer = new HashMap<>();
    private static final double GLOW_RADIUS = 15.0;
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

}
