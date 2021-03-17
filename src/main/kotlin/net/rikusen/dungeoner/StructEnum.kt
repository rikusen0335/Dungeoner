package net.rikusen.dungeoner

enum class StructEnum(val pathList: TilePaths) {
    I_0(TilePaths(true, false, true, false)),
    I_90(TilePaths(false, true, false, true)),

    L_0(TilePaths(true, false, false, true)),
    L_90(TilePaths(false, false, true, true)),
    L_180(TilePaths(false, true, true, false)),
    L_270(TilePaths(true, true, false, false)),

    T_0(TilePaths(false, true, true, true)),
    T_90(TilePaths(true, true, true, false)),
    T_180(TilePaths(true, true, false, true)),
    T_270(TilePaths(true, false, true, true)),

    P(TilePaths(true, true, true, true));

    companion object {
        private val map = values().associateBy(StructEnum::pathList)

        fun getKeyByValue(pathList: TilePaths): String {
            var structName = map[pathList]
            if (structName == null) return "P"
            return structName.toString()
        }
    }
}