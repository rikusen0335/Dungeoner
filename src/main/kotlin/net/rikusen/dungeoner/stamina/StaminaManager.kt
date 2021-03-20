package net.rikusen.dungeoner.stamina

import com.destroystokyo.paper.event.player.PlayerJumpEvent
import net.rikusen.dungeoner.CustomPlayer
import net.rikusen.dungeoner.Dungeoner
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerToggleSprintEvent

class StaminaManager(private val plugin: Dungeoner) : Listener {
    init { start5LTask() }

    /*
     スタミナコントロール
     Controls stamina
     */
    @EventHandler
    fun onPlayerToggleSprint(event: PlayerToggleSprintEvent) {
        if (event.isSprinting) DecreaseTask(event.player).runTaskTimer(plugin, 0L, 5L)
    }

    @EventHandler
    fun onPlayerJump(event: PlayerJumpEvent) {
        CustomPlayer.getPlayer(event.player).consumeJumpStamina()
    }

    private fun start5LTask() {
        plugin.server.scheduler.scheduleSyncRepeatingTask(plugin, {
            staminaRegenerationTask()
        }, 0L, 5L)
    }

    private fun staminaRegenerationTask() {
        Bukkit.getOnlinePlayers().forEach {
            /*
            0.25秒ごとにカスタムプレイヤーのスタミナを回復させる
            Let regenerate customPlayer's stamina per 0.25 seconds
             */
            val cPlayer = CustomPlayer.getPlayer(it)
            cPlayer.staminaDelay -= 0.25
            cPlayer.regenerateStamina()
        }
    }

    /*
    スタミナコントロール
    Controls stamina
     */
//    @EventHandler
//    fun onPlayerMove(event: PlayerMoveEvent) {
//        if (event.player.isSprinting) {
//            // プレイヤーのFoodLevelを0.5減らす
//            val cPlayer = CustomPlayer.getPlayer(event.player)
//            cPlayer.consumeStamina()
//        }
//    }
}