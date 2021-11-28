package net.rikusen.dungeoner

import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

object EventListener : Listener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        /* Register CustomPlayer */
        val customPlayer = CustomPlayer.getPlayer(event.player)
        CustomPlayer.registerPlayer(customPlayer)

        setByPlayerData(event.player)

        /* Update join message */
        val message = Component.text("${ChatColor.GRAY}[${ChatColor.GREEN}+${ChatColor.GRAY}] ${ChatColor.WHITE}${event.player.name}")
        message.append(Component.newline())

        var sound = Sound.ENTITY_EXPERIENCE_ORB_PICKUP

        /*
        初参加であればメッセージを追加して音を変える
        Add message and change sound if player is first join
         */
        if (!event.player.hasPlayedBefore()) {
            message.append(
                Component.text("${ChatColor.GREEN}${event.player.name}が初参加しました！")
            )
            sound = Sound.ENTITY_PLAYER_LEVELUP
        }

        Bukkit.getOnlinePlayers().forEach { it.playSound(it.location, sound, 0.3F, 1F) }
        event.joinMessage(message)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        /* Unregister CustomPlayer */
        val customPlayer: CustomPlayer = CustomPlayer.getPlayer(event.player)
        CustomPlayer.unregisterPlayer(customPlayer)

        val message = Component.text("${ChatColor.GRAY}[${ChatColor.RED}-${ChatColor.GRAY}] ${ChatColor.WHITE}${event.player.name}")
        event.quitMessage(message)
    }

    private fun setByPlayerData(player: Player) {
        val cPlayer: CustomPlayer = CustomPlayer.getPlayer(player)
        player.sendMessage("Health: ${cPlayer.maxHealth} / ${cPlayer.health}")
        player.sendMessage("Exp: ${cPlayer.experience}")
        cPlayer.updateClientHealthDisplay()
        player.level = cPlayer.level
    }
}