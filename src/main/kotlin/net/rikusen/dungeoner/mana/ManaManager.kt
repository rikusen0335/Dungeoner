package net.rikusen.dungeoner.mana

import net.rikusen.dungeoner.CustomPlayer
import net.rikusen.dungeoner.Dungeoner
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class ManaManager(private val plugin: Dungeoner) : Listener {
    init { start5LTask() }

    @EventHandler
    fun onManaConsume(event: ManaConsumeEvent) {
    }

    private fun start5LTask() {
        plugin.server.scheduler.scheduleSyncRepeatingTask(plugin, {
            manaRegenerationTask()
        }, 0L, 5L)
    }

    private fun manaRegenerationTask() {
        Bukkit.getOnlinePlayers().forEach {
            /*
            0.25秒ごとにカスタムプレイヤーのマナを回復させる
            Let regenerate customPlayer's mana per 0.25 seconds
             */
            val cPlayer = CustomPlayer.getPlayer(it)
            cPlayer.regenerateMana()
        }
    }
}