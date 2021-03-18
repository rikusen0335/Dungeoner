package net.rikusen.dungeoner.command

import com.ryandw11.structure.api.CustomStructuresAPI
import com.ryandw11.structure.structure.Structure
import com.ryandw11.structure.structure.StructureHandler
import net.rikusen.dungeoner.*
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object CommandFunctions {
    private var structureHandler: StructureHandler = CustomStructuresAPI().structureHandler
    private val PART_GROUP = "stone_2"

    fun generateDungeon(sender: Player): CommandResult {
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

        return CommandResult(true, "ダンジョンを生成しました") // Generated a dungeon.
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
        return structureHandler.getStructure(getPartName(structName))
    }
}