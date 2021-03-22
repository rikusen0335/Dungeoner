package net.rikusen.dungeoner.maze_generator

enum class StructEnum(val pathList: TilePaths, val rotate: Int) {
    I_0(TilePaths(true, false, true, false), 0),
    I_90(TilePaths(false, true, false, true), 90),

    L_0(TilePaths(true, false, false, true), 0),
    L_90(TilePaths(false, false, true, true), 90),
    L_180(TilePaths(false, true, true, false), 180),
    L_270(TilePaths(true, true, false, false), 270),

    T_0(TilePaths(false, true, true, true), 0),
    T_90(TilePaths(true, true, true, false), 90),
    T_180(TilePaths(true, true, false, true), 180),
    T_270(TilePaths(true, false, true, true), 270),

    U_0(TilePaths(true, false, false, false), 0),
    U_90(TilePaths(false, false, false, true), 90),
    U_180(TilePaths(false, false, true, false), 180),
    U_270(TilePaths(false, true, false, false), 270),


    P(TilePaths(true, true, true, true), 0);

    companion object {
        private val map = values().associateBy(StructEnum::pathList)

        fun getKeyByValue(pathList: TilePaths): String = map.getOrDefault(pathList, P).toString()
    }
}