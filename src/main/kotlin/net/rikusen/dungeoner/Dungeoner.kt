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
import org.bukkit.Location


class Dungeoner : JavaPlugin(), Listener, CommandExecutor {
    lateinit var structureHandler: StructureHandler

    val PART_GROUP = "stone_2"

    override fun onEnable() {
        server.pluginManager.registerEvents(this, this)
        // TODO: If any error then try-catch them
        // TODO: Check if dependencies are installed

        structureHandler = CustomStructuresAPI().structureHandler
    }

    override fun onDisable() {
    }

    override fun onCommand(sender: CommandSender, cmd: Command, label: String, params: Array<String>): Boolean {
        val cmdName = cmd.name.toLowerCase()

        if (sender !is Player) return false

        if (cmdName == "dungeon") {
            if (params.isEmpty()) {
                sender.sendMessage("HI!")
                return true
            }

            when (params[0]) {
                "create" -> {
                    val result = structureHandler.getStructure("test_dungeon")
                    result.spawn(sender.location)
                    return true
                }

                // Generates the labyrinth and let structures covers it
                "generate" -> {
                    val mazeSize = 31
                    val structureSize = 9.0
                    val location = sender.location.add(0.0, -3.0, 0.0)

                    val mazeGenerator = MazeGeneratorRevision(mazeSize, mazeSize)
                    val maze = mazeGenerator.generate()

                    for (row in maze.indices) {
                        for (col in maze[row].indices) {
                            if (maze[row][col] == MazeGenerator.PATH) {
                                getStructure(maze, Cell(row, col)).spawn(location.clone().add(row * structureSize, 0.0, col * structureSize))
                            }
                        }
                    }
                }
            }
        }

        return false
    }

    // これEnumとか作って、getPartName(type: Part.Enum, rotate: Int)とかにしたほうがいいかもね～～～
    private fun getPartName(partName: String): String {
        return PART_GROUP + "_" + partName
    }

    /*
    迷路の現在のセルから、どの方向に道が伸びているかによって使うパーツを決める
     */
    private fun getStructure(maze: Array<Array<Int>>, currentCell: Cell): Structure {
        val pathList = TilePaths(
            maze[currentCell.x][currentCell.y + 1] == MazeGeneratorRevision.PATH,
            maze[currentCell.x + 1][currentCell.y] == MazeGeneratorRevision.PATH,
            maze[currentCell.x][currentCell.y - 1] == MazeGeneratorRevision.PATH,
            maze[currentCell.x - 1][currentCell.y] == MazeGeneratorRevision.PATH
        )

        val structName = StructEnum.getKeyByValue(pathList)
        logger.info("Making: $structName")
        return structureHandler.getStructure(getPartName(structName))
    }
}