package de.jagenka

import it.unimi.dsi.fastutil.ints.IntList
import net.fabricmc.api.ModInitializer
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.FireworkExplosionComponent
import net.minecraft.component.type.FireworksComponent
import net.minecraft.entity.EntityStatuses
import net.minecraft.entity.projectile.FireworkRocketEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.DyeColor
import net.minecraft.world.event.GameEvent
import org.slf4j.LoggerFactory

object CreeperSindDochKeinVerbrechen : ModInitializer
{
    private val logger = LoggerFactory.getLogger("creeper-sind-doch-kein-verbrechen")

    override fun onInitialize()
    {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.
        logger.info("Creeper sind doch kein Verbrechen Mod loaded!")
    }

    fun createFireworkExplosionEffect(serverWorld: ServerWorld, x: Double, y: Double, z: Double)
    {
        val fireworkItemStack = ItemStack(Items.FIREWORK_ROCKET)
        fireworkItemStack.set<FireworksComponent>(
            DataComponentTypes.FIREWORKS,
            FireworksComponent(
                0,
                listOf(
                    FireworkExplosionComponent(
                        FireworkExplosionComponent.Type.CREEPER,
                        IntList.of(DyeColor.LIME.fireworkColor),
                        IntList.of(), // no trail
                        false,
                        false
                    ),
                    FireworkExplosionComponent(
                        FireworkExplosionComponent.Type.CREEPER,
                        IntList.of(DyeColor.GREEN.fireworkColor),
                        IntList.of(), // no trail
                        false,
                        false
                    )
                )
            )
        )

        val fireworkRocket = FireworkRocketEntity(serverWorld, x, y + 3, z, fireworkItemStack)
        serverWorld.spawnEntity(fireworkRocket)

        // fireworkRocket.explodeAndRemove(serverWorld) -- now replaced with the following:
        serverWorld.sendEntityStatus(fireworkRocket, EntityStatuses.EXPLODE_FIREWORK_CLIENT)
        fireworkRocket.emitGameEvent(GameEvent.EXPLODE, fireworkRocket.owner)
        // no exploding to prevent damage
        fireworkRocket.discard()
    }
}