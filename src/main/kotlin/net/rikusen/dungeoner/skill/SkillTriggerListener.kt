package net.rikusen.dungeoner.skill

import net.rikusen.dungeoner.CustomPlayer
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

object SkillTriggerListener : Listener {

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        if (event.action == Action.RIGHT_CLICK_AIR || event.action == Action.RIGHT_CLICK_BLOCK) {
            if (event.player.inventory.itemInMainHand.type == Material.IRON_SWORD) {
                CustomPlayer.getPlayer(event.player).consumeMana(1.0)
            }
        }
    }
}