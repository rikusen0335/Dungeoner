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
                    val structureSize = 5.0
                    val location = sender.location.add(0.0, -3.0, 0.0)

                    val mazeGenerator = MazeGeneratorRevision(mazeSize, mazeSize)
                    val maze = mazeGenerator.generate()

                    for (row in maze.indices) {
                        for (col in maze[row].indices) {
                            if (maze[row][col] == MazeGenerator.PATH) {
                                decisionPart(maze, Cell(row, col)).spawn(location.clone().add(row * structureSize, 0.0, col * structureSize))
                            }
                        }
                    }
                }
            }
        }

        return false
    }

    /*
    迷路の現在のセルから、どの方向に道が伸びているかによって使うパーツを決める
     */
    private fun decisionPart(maze: Array<Array<Int>>, currentCell: Cell): Structure {
        val directions = getPathDirections(maze, currentCell)

        when (directions.count { it }) {
            2 -> {
                // I字型
                if (directions[0] && directions[2] || directions[1] && directions[3]) {
                    return if (directions[0]) {
                        structureHandler.getStructure("stone_I_0")
                    } else {
                        structureHandler.getStructure("stone_I_90")
                    }
                }

                // L字型
                else {
                    return if (directions[0] && directions[1]) {
                        structureHandler.getStructure("stone_L_270")
                    } else if (directions[1] && directions[2]) {
                        structureHandler.getStructure("stone_L_180")
                    } else if (directions[2] && directions[3]) {
                        structureHandler.getStructure("stone_L_90")
                    } else {
                        structureHandler.getStructure("stone_L_0")
                    }
                }
            }
            3 -> {
                return if (directions[1] && directions[2] && directions[3]) {
                    structureHandler.getStructure("stone_T_0")
                } else if (directions[0] && directions[2] && directions[3]) {
                    structureHandler.getStructure("stone_T_270")
                } else if (directions[0] && directions[1] && directions[3]) {
                    structureHandler.getStructure("stone_T_180")
                } else {
                    structureHandler.getStructure("stone_T_90")
                }
            }
            4 -> return structureHandler.getStructure("stone_P")
        }

        logger.warning("I couldn't find specified stuff")
        logger.warning("The result is ${directions.count { it }}")
        return structureHandler.getStructure("stone_P")
    }

    private fun getPathDirections(maze: Array<Array<Int>>, currentCell: Cell): BooleanArray {
        val boolList = BooleanArray(4)

        boolList[0] = (maze[currentCell.x][currentCell.y + 1] == MazeGeneratorRevision.PATH)
        boolList[1] = (maze[currentCell.x + 1][currentCell.y] == MazeGeneratorRevision.PATH)
        boolList[2] = (maze[currentCell.x][currentCell.y - 1] == MazeGeneratorRevision.PATH)
        boolList[3] = (maze[currentCell.x - 1][currentCell.y] == MazeGeneratorRevision.PATH)

        return boolList
    }
}