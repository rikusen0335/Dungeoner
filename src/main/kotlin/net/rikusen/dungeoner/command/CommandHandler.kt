package net.rikusen.dungeoner.command

import net.rikusen.dungeoner.CustomPlayer

import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.lang.RuntimeException
import kotlin.reflect.KCallable

typealias Callback = KCallable<CommandResult>
typealias Command = Pair<Callback, Array<out Any>>

class CommandHandler {
    private val commands: ArrayList<Command> = ArrayList(10)

    fun addCommand(fn: Callback, vararg cmd: Any) {
        this.commands.add(Pair(fn, cmd))
    }

    fun exec(sender: CommandSender, vararg cmd: String): CommandResult {
        if (sender !is Player) return CommandResult(false, "${ChatColor.RED}このコマンドはプレイヤーのみ使用できます。") // Says: "The command only accepts a player."
        val player = CustomPlayer.getPlayer(sender)
        return when (val command = findCommand(*cmd)) {
            null -> CommandResult(false, "存在しないコマンドです。") // Says: "Unknown command."
            else -> command.first.call(player, *collectArgs(command.second, *cmd))
        }
    }

    private fun collectArgs(signature: Array<out Any>, vararg cmd: String): Array<Any> {
        val fnArgs = ArrayList<Any>()
        signature.forEachIndexed { i, v ->
            if (v !is String) {
                fnArgs.add(parseValue(cmd[i], v))
            }
        }
        return fnArgs.toTypedArray()
    }

    private fun findCommand(vararg cmd: String): Command? {
        return this.commands.find {
            it.second.size == cmd.size && !it.second.zip(cmd).map { (s, arg) ->
                when (s) {
                    is String -> s == arg
                    else -> canParse(arg, s)
                }
            }.contains(false)
        }
    }

//    // TODO TabCompletions
//    private fun findCommandCompletions(arg: String): Array<String>? {
//
//    }

    private fun canParse(value: String, asType: Any): Boolean {
        return when (asType) {
            Int -> value.toIntOrNull(10) !== null
            Float -> value.toFloatOrNull() !== null
            Double -> value.toDoubleOrNull() !== null
            String -> true
            else -> throw RuntimeException("Unhandled input type")
        }
    }

    private fun parseValue(value: String, asType: Any): Any {
        return when (asType) {
            Int -> value.toInt(10)
            Float -> value.toFloat()
            Double -> value.toDouble()
            String -> value
            else -> throw RuntimeException("Unhandled input type")
        }
    }
}