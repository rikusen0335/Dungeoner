package net.rikusen.dungeoner.dungeon_generator.geometry

import kotlin.math.min
import kotlin.properties.Delegates

// Reference: https://github.com/OndrejNepozitek/Edgar-DotNet/blob/dev/src/Edgar/Geometry/PolygonGrid2D.cs
class PolygonGrid2D(_points: ) {
    companion object {
        val possibleRotations: Array<Int> = arrayOf(0, 90, 180, 270)
    }

    private val points: List<Vector2Int> = arrayListOf()
    private var hash: Int = computeHash()
    private var boundingRectangle: RectangleGrid2D

    init {
        checkIntegrity()

        hash = computeHash()
        boundingRectangle = getBoundingRectangle()
    }

    private fun checkIntegrity() {
        // Each polygon must have at least 4 vertices
        if (points.size < 4) {
            throw IllegalArgumentException("Each polygon must have at least 4 points.")
        }

        // Check if all lines are parallel to axis X or Y
        var previousPoint = points[points.size - 1];
        points.forEach {
            if (it == previousPoint) throw IllegalArgumentException("All lines must be parallel to one of the axes.")
            if (it.x != previousPoint.x && it.y != previousPoint.y) throw IllegalArgumentException("All lines must be parallel to one of the axes.")

            previousPoint = it
        }

        // Check if no two adjacent lines are both horizontal or vertical
        (0..points.size).forEach {
            val p1 = points[it]
            val p2 = points[(it + 1) % points.size]
            val p3 = points[(it + 2) % points.size]

            if (p1.x == p2.x && p2.x == p3.x) throw IllegalArgumentException("No two adjacent lines can be both horizontal or vertical.")
            if (p1.y == p2.y && p2.y == p3.y) throw IllegalArgumentException("No two adjacent lines can be both horizontal or vertical.")
        }

        if (!isClockwiseOriented(points)) throw IllegalArgumentException("Points must be in a clockwise order.")
    }

    private fun isClockwiseOriented(points: List<Vector2Int>): Boolean {
        var previous = points[points.size - 1]
        var sum = 0L

        points.forEach {
            sum += (it.x - previous.x) * (it.y + previous.y)
            previous = it
        }

        return sum > 0
    }

    @JvmName("getBoundingRectangle1")
    private fun getBoundingRectangle(): RectangleGrid2D {
        val smallestX = points.minByOrNull { it.x }
        val biggestX = points.maxByOrNull { it.x }
        val smallestY = points.maxByOrNull { it.y }
        val biggestY = points.maxByOrNull { it.y }

        if (smallestX != null && smallestY != null && biggestX != null && biggestY != null) {
            return RectangleGrid2D(Vector2Int(smallestX.x, smallestY.y), Vector2Int(biggestX.x, biggestY.y))
        }
    }

    private fun computeHash(): Int {
        var hash = 17
        points.forEach {
            hash = hash * 23 + it.x + it.y
        }
        return hash
    }
}