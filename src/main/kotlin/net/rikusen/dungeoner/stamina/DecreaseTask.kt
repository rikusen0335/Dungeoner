package net.rikusen.dungeoner.stamina

import net.rikusen.dungeoner.CustomPlayer
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

class DecreaseTask(val player: Player) : BukkitRunnable() {
    override fun run() {
        if (!player.isSprinting) {
            cancel()
            return
        }

        // プレイヤーのFoodLevelを0.5減らす
        val cPlayer = CustomPlayer.getPlayer(player)
        cPlayer.consumeStamina()
    }
}