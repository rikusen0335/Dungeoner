package net.rikusen.dungeoner

import com.ryandw11.structure.api.CustomStructuresAPI
import com.ryandw11.structure.structure.StructureHandler
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

import net.rikusen.dungeoner.command.CommandFunctions
import net.rikusen.dungeoner.command.CommandHandler
import net.rikusen.dungeoner.config.Configuration
import net.rikusen.dungeoner.mana.ManaManager
import net.rikusen.dungeoner.skill.SkillTriggerListener
import net.rikusen.dungeoner.stamina.StaminaManager
import org.bukkit.Material
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.TransactionManager
import java.nio.file.Path
import java.nio.file.Paths
import java.sql.Connection


class Dungeoner : JavaPlugin(), Listener, CommandExecutor {
    private lateinit var db: Database
    private val command = CommandHandler()
    lateinit var customStructuresAPI: CustomStructuresAPI

    override fun onEnable() {
        server.pluginManager.registerEvents(this, this)
        server.pluginManager.registerEvents(EventListener, this)
        server.pluginManager.registerEvents(PlayerStatusChangeListener(this), this)
        server.pluginManager.registerEvents(StaminaManager(this), this)
        server.pluginManager.registerEvents(ManaManager(this), this)
        server.pluginManager.registerEvents(SkillTriggerListener, this)
        // TODO: If any error then try-catch them
        // TODO: Check if dependencies are installed

        customStructuresAPI = CustomStructuresAPI()

        val itemConfig = Configuration(this, "item").open()
        itemConfig.write("Test.Material", Material.IRON_SWORD.toString())
        itemConfig.write("Test.Display", "Test Iron Sword")
        itemConfig.write("Test.Lore", listOf("This is line 1", "This is line 2"))
        itemConfig.save()

        connectDatabase()

        command.addCommand(CommandFunctions::generateDungeon, "dungeon", "generate")
        command.addCommand(CommandFunctions::setHealth, "rpg", "health", "set", Int)
        command.addCommand(CommandFunctions::setMaxHealth, "rpg", "maxhealth", "set", Int)
        command.addCommand(CommandFunctions::showStatus, "rpg", "status")
        command.addCommand(CommandFunctions::getItem, "item", "get", String)
        command.addCommand(CommandFunctions::getItemAmount, "item", "get", String, Int)
        command.addCommand(CommandFunctions::testStructure, "dungeon", "test")
    }

    override fun onDisable() {
        disconnectDatabase()
    }

    private fun connectDatabase() {
        val basePath: Path = Paths.get(System.getProperty("user.dir")).toRealPath()
        val sqliteFile: Path = Paths.get(basePath.toString(), "player.db")

        println(sqliteFile) // Show the paths
        db = Database.connect("jdbc:sqlite:file:$sqliteFile", "org.sqlite.JDBC")
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
    }

    private fun disconnectDatabase() {
        TransactionManager.closeAndUnregister(db)
    }

    override fun onCommand(sender: CommandSender, cmd: Command, label: String, params: Array<String>): Boolean {
        val cmdName = cmd.name.toLowerCase()
        val result = command.exec(sender, cmdName, *params)
        if (result.message != "") sender.sendMessage(result.message)
        return result.success
    }

    private fun setByPlayerData(player: Player) {
        val cPlayer: CustomPlayer = CustomPlayer.getPlayer(player)
        player.sendMessage("Health: ${cPlayer.maxHealth} / ${cPlayer.health}")
        player.sendMessage("Exp: ${cPlayer.experience}")
        cPlayer.updateClientHealthDisplay()
        player.level = cPlayer.level
    }
}