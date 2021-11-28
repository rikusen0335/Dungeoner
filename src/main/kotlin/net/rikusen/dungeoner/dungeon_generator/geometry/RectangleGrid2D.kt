package net.rikusen.dungeoner.dungeon_generator.geometry

import kotlin.math.abs

// Reference: https://github.com/OndrejNepozitek/Edgar-DotNet/blob/dev/src/Edgar/Geometry/RectangleGrid2D.cs
data class RectangleGrid2D(val a: Vector2Int, val b: Vector2Int) {
    var center: Vector2Int = Vector2Int((a.x + b.y) / 2, (a.y + b.y) / 2)
    var width: Int = abs(a.x - b.x)
    var height: Int = abs(a.y - b.y)
    var area: Int = width * height

    /**
     * Rotates the rectangle.
     *
     * @param degrees Degrees divisible by 90.
     * @return
     */
    fun rotate(degrees: Int): RectangleGrid2D {
        if (degrees % 90 != 0) {
            throw IllegalArgumentException("Degrees must be divisible by 90")
        }

        return RectangleGrid2D(a.rotateAroundCenter(degrees), b.rotateAroundCenter(degrees))
    }

    /**
     * Adds a given offset to both reference points of the rectangle itself.
     *
     * @param offset
     * @return
     */
    operator fun plus(offset: Vector2Int): RectangleGrid2D {
        return RectangleGrid2D(a + offset, b + offset)
    }
}