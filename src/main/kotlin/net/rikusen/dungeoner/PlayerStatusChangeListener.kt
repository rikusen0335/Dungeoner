package net.rikusen.dungeoner

import com.destroystokyo.paper.event.entity.EntityJumpEvent
import com.destroystokyo.paper.event.player.PlayerJumpEvent
import net.rikusen.dungeoner.stamina.DecreaseTask
import net.rikusen.dungeoner.stamina.RecoveryTask
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntityRegainHealthEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerToggleSprintEvent

// TODO 不正なイベント処理があったら、それをスタッフやDiscord等に通知するようにする

/* This object listener only accepts a player */
class PlayerStatusChangeListener(private val plugin: Dungeoner) : Listener {
    @EventHandler
    fun onEntityDamage(event: EntityDamageEvent) {
        if (event.entity !is Player) return

        val tempDamage: Double = event.damage // This have to be at the top
        event.damage = 0.0 // This have to be at the top

        val customPlayer = CustomPlayer.getPlayer(event.entity as Player)

        customPlayer.updateHealth(tempDamage)

        /*
        体力が0になったら違うイベントで処理するのでキャンセル
        Cancel the event if health is less than 0
         */
        if (customPlayer.health <= 0) {
            customPlayer.player.health = 0.0
            return
        }
    }

    @EventHandler
    fun onDeath(event: EntityDeathEvent) {
        if (event.entity is Player) {
            val customPlayer = CustomPlayer.getPlayer(event.entity as Player)
            customPlayer.health = customPlayer.maxHealth // 死亡時に体力リセット
        }

//        if (event.entity is Zombie) {
//            val player = event.entity.killer
//
//            if (player is Player) {
//                player.sendMessage("You killed a zombie")
//                // TODO 経験値取得処理
//            }
//        }
    }

    /*
    食べ物ゲージから回復したときは、回復しないようにする
    Cancel the event when player regain health from food eating
     */
    @EventHandler
    fun onEntityRegainHealth(event: EntityRegainHealthEvent) {
        if (event.regainReason == EntityRegainHealthEvent.RegainReason.SATIATED) event.isCancelled = true
    }
}