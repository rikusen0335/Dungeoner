package net.rikusen.dungeoner.maze_generator

import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random

// https://algoful.com/Archive/Algorithm/MazeExtend

/* 壁伸ばし法 */
class MazeGeneratorRevision(width: Int, height: Int) {
    companion object {
        val PATH = 0
        val WALL = 1

        fun debugPrint(maze: Array<Array<Int>>) {
            println("Width: ${maze.size}")
            println("Height: ${maze[0].size}")

            for (y in maze[1].indices) {
                for (x in maze[0].indices) {
                    print(if (maze[x][y] == WALL) "■" else "□")
                }
                println()
            }
        }
    }

    val width = width
    val height = height

    // 迷路情報を初期化
    private var maze = Array(width) { Array(height) { PATH } }
    // 現在拡張中の壁情報を保持
    private val currentWallCells = Stack<Cell>()
    // 壁の拡張を行う開始セルの情報
    private var startCells = ArrayList<Cell>()

    init {
        // 5以下だと成立しないのでエラー
        if (width < 5 || height < 5) throw IllegalArgumentException("You need to set width and height are at least 5.")
    }

    fun generate(): Array<Array<Int>> {
        for (y in 0 until height) {
            for (x in 0 until width) {
                if (x == 0 || y == 0 || x == width - 1|| y == height - 1) {
                    maze[x][y] = WALL
                } else {
                    maze[x][y] = PATH
                    if (x % 2 == 0 && y % 2 == 0) {
                        startCells.add(Cell(x, y))
                    }
                }
            }
        }

        // 壁が拡張できなくなるまでループ
        while (startCells.size > 0) {
            // ランダムに開始セルを取得し、開始候補から削除
            var index = Random.nextInt(startCells.size)
            var cell = startCells[index]
            startCells.removeAt(index)
            var (x, y) = cell

            if (maze[x][y] == PATH) {
                currentWallCells.clear()
                extendWall(x, y)
            }
        }

        return maze
    }

    // 指定座標から壁を生成拡張する
    private fun extendWall(_x: Int, _y: Int) {
        var x = _x
        var y = _y
        // 伸ばすことができる方向(1マス先が通路で2マス先まで範囲内)
        // 2マス先が壁で自分自身の場合、伸ばせない
        var directions = ArrayList<Direction>()

        if (maze[x][y - 1] == PATH && !isCurrentWall(x, y - 2)) directions.add(Direction.Up)
        if (maze[x + 1][y] == PATH && !isCurrentWall(x + 2, y)) directions.add(Direction.Right)
        if (maze[x][y + 1] == PATH && !isCurrentWall(x, y + 2)) directions.add(Direction.Down)
        if (maze[x - 1][y] == PATH && !isCurrentWall(x - 2, y)) directions.add(Direction.Left)

        // ランダムに伸ばす(2マス)
        if (directions.size > 0) {
            // 壁を作成(この地点から壁を伸ばす)
            setWall(x, y)

            // 伸ばす先が通路の場合は拡張を続ける
            var isPath = false
            var dirIndex = Random.nextInt(directions.size)

            when (directions[dirIndex]) {
                Direction.Up -> {
                    isPath = maze[x][y - 2] == PATH
                    setWall(x, --y)
                    setWall(x, --y)
                }
                Direction.Right -> {
                    isPath = maze[x + 2][y] == PATH
                    setWall(++x, y)
                    setWall(++x, y)
                }
                Direction.Down -> {
                    isPath = maze[x][y + 2] == PATH
                    setWall(x, ++y)
                    setWall(x, ++y)
                }
                Direction.Left -> {
                    isPath = maze[x - 2][y] == PATH
                    setWall(--x, y)
                    setWall(--x, y)
                }
            }

            // 既存の壁に接続できていない場合は拡張続行
            if (isPath) extendWall(x, y)
        } else {
            // すべて現在拡張中の壁にぶつかる場合、バックして再開
            var beforeCell = currentWallCells.pop()
            extendWall(beforeCell.x, beforeCell.y)
        }
    }

    // 壁を拡張する
    private fun setWall(x: Int, y: Int) {
        maze[x][y] = WALL
        if (x % 2 == 0 && y % 2 == 0) {
            currentWallCells.push(Cell(x, y))
        }
    }

    // 拡張中の座標かどうか判定
    private fun isCurrentWall(x: Int, y: Int): Boolean {return currentWallCells.contains(Cell(x, y))}
}