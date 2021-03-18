package net.rikusen.dungeoner

import com.ryandw11.structure.api.CustomStructuresAPI
import com.ryandw11.structure.structure.Structure
import com.ryandw11.structure.structure.StructureHandler
import com.sk89q.worldedit.extent.clipboard.Clipboard
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import java.io.FileInputStream

import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader

import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats

import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat
import net.rikusen.dungeoner.command.CommandFunctions
import net.rikusen.dungeoner.command.CommandHandler
import org.bukkit.Location
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.TransactionManager
import java.nio.file.Path
import java.nio.file.Paths
import java.sql.Connection


class Dungeoner : JavaPlugin(), Listener, CommandExecutor {
    private lateinit var structureHandler: StructureHandler

    private lateinit var db: Database
    private val command = CommandHandler()

    override fun onEnable() {
        server.pluginManager.registerEvents(this, this)
        // TODO: If any error then try-catch them
        // TODO: Check if dependencies are installed

        val basePath: Path = Paths.get(System.getProperty("user.dir")).toRealPath()
        val sqliteFile: Path = Paths.get(basePath.toString(), "player.db")

        println(sqliteFile) // Show the paths
        db = Database.connect("jdbc:sqlite:file:$sqliteFile", "org.sqlite.JDBC")
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE

        structureHandler = CustomStructuresAPI().structureHandler

        command.addCommand(CommandFunctions::generateDungeon, "dungeon", "generate")
    }

    override fun onDisable() {
        TransactionManager.closeAndUnregister(db) // Disconnect処理
    }

    override fun onCommand(sender: CommandSender, cmd: Command, label: String, params: Array<String>): Boolean {
        val cmdName = cmd.name.toLowerCase()
        val result = command.exec(sender, cmdName, *params)
        if (result.message != "") sender.sendMessage(result.message)
        return result.success
    }
}