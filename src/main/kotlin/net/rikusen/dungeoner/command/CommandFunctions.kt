package net.rikusen.dungeoner.command

import com.ryandw11.structure.api.CustomStructuresAPI
import com.ryandw11.structure.structure.Structure
import com.ryandw11.structure.structure.StructureHandler
import net.rikusen.dungeoner.item.ItemManager
import net.kyori.adventure.text.Component
import net.rikusen.dungeoner.CustomPlayer
import net.rikusen.dungeoner.Dungeoner
import net.rikusen.dungeoner.maze_generator.*
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import kotlin.math.roundToInt

object CommandFunctions {
    private val PART_GROUP = "stone_2"

    fun generateDungeon(player: CustomPlayer): CommandResult {
        val mazeSize = 31
        val structureSize = 9.0
        val location = player.player.location.add(0.0, -7.0, 0.0)

        val mazeGenerator = MazeGeneratorRevision(mazeSize, mazeSize)
        val maze = mazeGenerator.generate()

        for (row in maze.indices) {
            for (col in maze[row].indices) {
                if (maze[row][col] == MazeGenerator.PATH) {
                    getStructure(maze, Cell(row, col)).spawn(location.clone().add(row * structureSize, 0.0, col * structureSize))
                }
            }
        }

        return CommandResult(true, "ダンジョンを生成しました") // Generated a dungeon.
    }

    fun getMaxHealth(player: CustomPlayer): CommandResult =
        CommandResult(true, "Your max health is ${player.maxHealth}")

    fun setMaxHealth(player: CustomPlayer, value: Int): CommandResult {
        if (value < 0) return CommandResult(false, "Invalid health: $value")
        player.maxHealth = value.toDouble()
        player.updateClientHealthDisplay()
        return CommandResult(true, "Your max health is now ${player.maxHealth}")
    }

    fun getHealth(player: CustomPlayer): CommandResult =
        CommandResult(true, "Your health is ${player.health}")

    fun setHealth(player: CustomPlayer, value: Int): CommandResult {
        if (value < 0 || value > player.maxHealth) {
            if (value <= 0) {
                player.player.health = 0.0
            }
            return CommandResult(true)
        }
        player.health = value.toDouble()
        player.updateClientHealthDisplay()
        return CommandResult(true, "Your health is now ${player.health}")
    }

    fun getLevel(player: CustomPlayer) =
        CommandResult(true, "Your level is ${player.level}")

    fun setLevel(player: CustomPlayer, value: Int): CommandResult {
        if (value < 0) return CommandResult(false, "Invalid level: $value")
        player.level = value
        return CommandResult(true, "Your level is now ${player.level}")
    }

    fun getStrength(player: CustomPlayer) =
        CommandResult(true, "Your strength is ${player.strength}")

    fun setStrength(player: CustomPlayer, value: Double): CommandResult {
        if (value < 0) return CommandResult(false, "Invalid strength: $value")
        player.strength = value
        return CommandResult(true, "Your Strength is now ${player.strength}")
    }

    fun testStructure(player: CustomPlayer): CommandResult {
        val customStructuresAPI = CustomStructuresAPI()
        val hoge = customStructuresAPI.structureHandler.getStructure("stone_2_T_0").baseRotation
        player.player.sendMessage(hoge.toString())

        return CommandResult(true)
    }

    fun showStatus(player: CustomPlayer): CommandResult {
        val maxHealth = player.maxHealth
        val currentHealth = player.health.roundToInt()
        val maxMana = player.maxMana
        val currentMana = player.mana

        player.player.sendMessage("${ChatColor.GRAY}----- ${ChatColor.WHITE}Your current status ${ChatColor.GRAY}-----")
        player.player.sendMessage("${player.name} - Level ${player.level}")
        player.player.sendMessage("Exp: ${player.experience} / ${player.requiredExperience}")
        player.player.sendMessage("HP: ${maxHealth.toInt()} / $currentHealth")
        player.player.sendMessage("Mana: ${maxMana.toInt()} / ${currentMana.toInt()}")
        player.player.sendMessage("Strength: ${player.strength}")
        player.player.sendMessage("Intelligence: ${player.intelligence}")

        return CommandResult(true)
    }

    /*
    TODO
    そのうち、アイテムリストを出して、そこから欲しいアイテムをクリックするだけで取得できるようにする
    Display the all item list, and click it then get the specified item in the future
     */
    fun getItem(player: CustomPlayer, name: String): CommandResult {
        val item = ItemManager.get(name)
        player.player.inventory.addItem(item)

        // TODO TextComponentImplからcontentを取得する方法を探す
        return CommandResult(true, "${ChatColor.GREEN}${item.itemMeta.displayName()}${ChatColor.WHITE}を1個与えました")
    }

    fun getItemAmount(player: CustomPlayer, name: String, amount: Int): CommandResult {
        val item = ItemManager.get(name)
        item.amount = amount
        player.player.inventory.addItem(item)

        // TODO TextComponentImplからcontentを取得する方法を探す
        return CommandResult(true, "${ChatColor.GREEN}${item.itemMeta.displayName()}${ChatColor.WHITE}を${amount}個与えました")
    }

    /*
    これEnumとか作って、getPartName(type: Part.Enum, rotate: Int)とかにしたほうがいいかもね～～～
     */
    private fun getPartName(partName: String): String {
        return PART_GROUP + "_" + partName
    }

    /*
    迷路の現在のセルから、どの方向に道が伸びているかによって使うパーツを決める
    Decide which part of maze would be used from where the PATH is connected to current cell
     */
    private fun getStructure(maze: Array<Array<Int>>, currentCell: Cell): Structure {
        val pathList = TilePaths(
            maze[currentCell.x][currentCell.y + 1] == MazeGeneratorRevision.PATH,
            maze[currentCell.x + 1][currentCell.y] == MazeGeneratorRevision.PATH,
            maze[currentCell.x][currentCell.y - 1] == MazeGeneratorRevision.PATH,
            maze[currentCell.x - 1][currentCell.y] == MazeGeneratorRevision.PATH
        )

        val structName = StructEnum.getKeyByValue(pathList)
        Bukkit.getLogger().info("Making: $structName")
        return Dungeoner().customStructuresAPI.structureHandler.getStructure(getPartName(structName))
    }
}