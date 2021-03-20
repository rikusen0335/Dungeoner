package net.rikusen.dungeoner.stamina

import net.rikusen.dungeoner.CustomPlayer
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

class RecoveryTask(val player: Player) : BukkitRunnable() {
    /*
    1につき0.25秒遅延
    Delay 0.25 seconds per 1
     */
    private var delay = 4

    override fun run() {
        // プレイヤーがスプリント状態か、FoodLevelが全快している時はTaskをキャンセル
        if (player.isSprinting || player.foodLevel == 20) {
            cancel()
            return
        }

        if (delay <= 0) {
            val cPlayer = CustomPlayer.getPlayer(player)
            cPlayer.regenerateStamina()
            return
        }
        delay--
    }
}