package net.rikusen.dungeoner.skill

import net.rikusen.dungeoner.CustomPlayer
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class SkillCastEvent(
    val cPlayer: CustomPlayer,
    val comsumeManaAmount: Double
): Event() {
    var isCancelled: Boolean = false

    companion object {
        private val HANDLERS = HandlerList()
        @JvmStatic private fun getHandlerList(): HandlerList = HANDLERS
    }

    override fun getHandlers(): HandlerList {
        return HANDLERS
    }
}