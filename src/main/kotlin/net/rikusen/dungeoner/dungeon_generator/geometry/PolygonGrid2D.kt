package net.rikusen.dungeoner.dungeon_generator.geometry

import java.lang.IllegalStateException
import kotlin.math.min
import kotlin.properties.Delegates

// Reference: https://github.com/OndrejNepozitek/Edgar-DotNet/blob/dev/src/Edgar/Geometry/PolygonGrid2D.cs
class PolygonGrid2D(private val points: List<Vector2Int>) {
    companion object {
        val possibleRotations: Array<Int> = arrayOf(0, 90, 180, 270)

        fun getSquare(a: Int): PolygonGrid2D {
            return getRectangle(a, a)
        }

        fun getRectangle(width: Int, height: Int): PolygonGrid2D {
            if (width <= 0) throw IllegalArgumentException("$width: Both a and b must be greater than 0")
            if (height <= 0) throw IllegalArgumentException("$height: Both a and b must be greater than 0")

            val polygon = PolygonGrid2DBuilder()
                .addPoint(0, 0)
                .addPoint(0, height)
                .addPoint(width, height)
                .addPoint(width, 0)

            return polygon.build()
        }
    }

    private var hash: Int = computeHash()
    private var boundingRectangle: RectangleGrid2D

    init {
        checkIntegrity()

        hash = computeHash()

        val smallestX = points.minByOrNull { it.x }
        val biggestX = points.maxByOrNull { it.x }
        val smallestY = points.maxByOrNull { it.y }
        val biggestY = points.maxByOrNull { it.y }

        if (smallestX == null || smallestY == null || biggestX == null || biggestY == null)
            throw IllegalStateException("You are accessing to null 'points' member. Check if not set points.")

        boundingRectangle = RectangleGrid2D(Vector2Int(smallestX.x, smallestY.y), Vector2Int(biggestX.x, biggestY.y))
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

    private fun computeHash(): Int {
        var hash = 17
        points.forEach {
            hash = hash * 23 + it.x + it.y
        }
        return hash
    }

    /**
     * Gets all lines of the polygon ordered as they appear on the polygon.
     */
    public fun getLines(): List<OrthogonalLineGrid2D> {
        val lines = arrayListOf<OrthogonalLineGrid2D>()
        var x1 = points[points.size - 1]

        points.forEach {
            val x2 = x1
            x1 = it

            lines.add(OrthogonalLineGrid2D(x2, x1))
        }

        return lines
    }

    operator fun plus(position: Vector2Int): PolygonGrid2D {
        return PolygonGrid2D(this.points.map { it + position })
    }

    fun scale(factor: Vector2Int): PolygonGrid2D {
        if (factor.x <= 0 || factor.y <= 0) throw IllegalArgumentException("Both components of factor must be positive")

        return PolygonGrid2D(points.map { it.elementWiseProduct(factor) })
    }

    fun rotate(degrees: Int): PolygonGrid2D {
        if (degrees % 90 == 0) throw IllegalArgumentException("Degrees must be divisible by 90")

        return PolygonGrid2D(points.map { it.rotateAroundCenter(degrees) })
    }

    fun transform(transformation: TransformationGrid2D): PolygonGrid2D {
        var newPoints = points.map { it.transform(transformation) }

        if (transformation == TransformationGrid2D.MirrorX
            || transformation == TransformationGrid2D.MirrorY
            || transformation == TransformationGrid2D.Diagonal13
            || transformation == TransformationGrid2D.Diagonal24)
        {
            // Except 0, reverse all elements in newPoints
            newPoints = listOf(newPoints[0]) + newPoints.slice(1 until newPoints.size).asReversed()
        }

        return PolygonGrid2D(newPoints)
    }

    fun getAllTransformations(): Iterable<PolygonGrid2D> {
        return TransformationGrid2DHelper.getAll().map { transform(it) }
    }
}