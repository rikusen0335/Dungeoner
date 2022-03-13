package net.rikusen.dungeoner.dungeon_generator.geometry

import net.rikusen.dungeoner.dungeon_generator.Direction

// Reference: https://github.com/OndrejNepozitek/Edgar-DotNet/blob/dev/src/Edgar/Geometry/OrthogonalLineGrid2D.cs
class OrthogonalLineGrid2D(val from: Vector2Int, val to: Vector2Int) {

    var degeneratedDirection: Direction = Direction.Undefined
    val length = Vector2Int.manhattanDistance(Vector2Int(0, 0), from - to)

    init {
        if (from.x != to.x && from.y != to.y) {
            throw IllegalArgumentException("The line is not orthogonal")
        }
    }

    constructor(from: Vector2Int, to: Vector2Int, degeneratedDirection: Direction) : this(from, to) {
        if (from.x != to.x && from.y != to.y) {
            throw IllegalArgumentException("The line is not orthogonal")
        }

        if (from != to && degeneratedDirection != Direction.Undefined && degeneratedDirection != getDirection(
                from,
                to
            )
        ) {
            throw IllegalArgumentException("Given direction is wrong")
        }

        this.degeneratedDirection = degeneratedDirection
    }

    companion object {
        private val orderedDirections: List<Direction> =
            arrayListOf(Direction.Right, Direction.Bottom, Direction.Left, Direction.Top)

        fun rotateDirection(direction: Direction, degrees: Int): Direction {
            if (direction == Direction.Undefined) throw IllegalArgumentException("Direction must be specified if it should be rotated.")

            val degreesMod = degrees.mod(360)

            if (degreesMod % 90 != 0) throw IllegalArgumentException()

            val shift = degreesMod / 90
            val index = orderedDirections.indexOf(direction)
            val newIndex = (index + shift).mod(4)

            return orderedDirections[newIndex]
        }
    }

    /**
     * Returns a direction of the line.
     *
     * If the line is degenerated, returns the direction that was set
     * in constructor (or Undefined if none was set).
     *
     * @return
     */
    fun getDirection(): Direction {
        return if (from == to) {
            degeneratedDirection
        } else getDirection(from, to)
    }

    /**
     * Gets a direction of an orthogonal lined formed by given points.
     *
     * @param from
     * @param to
     * @throws IllegalArgumentException
     * @return
     */
    fun getDirection(from: Vector2Int, to: Vector2Int): Direction {
        if (from == to) {
            return Direction.Undefined
        }
        if (from.x == to.x) {
            return if (from.y > to.y) Direction.Bottom else Direction.Top
        }
        if (from.y == to.y) {
            return if (from.x > to.x) Direction.Left else Direction.Right
        }
        throw IllegalArgumentException("Given points do not form an orthogonal line")
    }

    /**
     * Rotate the line.
     *
     * Positive degrees mean clockwise rotation.
     *
     * @param degrees
     * @param checkDirection
     * @exception IllegalArgumentException
     * @return
     */
    fun rotate(degrees: Int, checkDirection: Boolean = true): OrthogonalLineGrid2D {
        if (degrees % 90 != 0)
            throw IllegalArgumentException("Degrees must be a multiple of 90.")

        if (checkDirection) {
            return OrthogonalLineGrid2D(
                from.rotateAroundCenter(degrees),
                to.rotateAroundCenter(degrees),
                rotateDirection(getDirection(), degrees)
            )
        }

        return OrthogonalLineGrid2D(from.rotateAroundCenter(degrees), to.rotateAroundCenter(degrees))
    }

    fun switchOrientation(): OrthogonalLineGrid2D {
        return OrthogonalLineGrid2D(to, from, getOppositeDirection(getDirection()))
    }

    fun shrink(from: Int, to: Int): OrthogonalLineGrid2D {
        if (length - from - to < 0) throw IllegalArgumentException("There must be at least one point left after shrinking.")
        val rotation = computeRotation()
        val rotated = rotate(rotation)

        val movedFrom = Vector2Int(rotated.from.x + from, rotated.from.y)
        val movedTo = Vector2Int(rotated.to.x - to, rotated.to.y)

        return OrthogonalLineGrid2D(movedFrom, movedTo, rotateDirection(getDirection(), rotation)).rotate(-rotation)
    }

    fun shrink(length: Int): OrthogonalLineGrid2D {
        return shrink(length, length)
    }

    fun computeRotation(): Int {
        return when (getDirection()) {
            Direction.Right -> 0
            Direction.Bottom -> 270
            Direction.Left -> 180
            Direction.Top -> 90
            else -> throw IllegalArgumentException("The range specified is out of range")
        }
    }

    fun getOppositeDirection(direction: Direction): Direction {
        return when (direction) {
            Direction.Bottom -> Direction.Top
            Direction.Top -> Direction.Bottom
            Direction.Right -> Direction.Left
            Direction.Left -> Direction.Right
            Direction.Undefined -> Direction.Undefined
        }
    }

    fun getPoints(): List<Vector2Int> {
        val points = ArrayList<Vector2Int>()

        if (from == to) {
            points.add(from)
            return points
        }

        when (getDirection()) {
            Direction.Top, Direction.Bottom -> (from.y..to.y).forEach { points.add(Vector2Int(from.x, it)) }
            Direction.Right, Direction.Left -> (from.x..to.x).forEach { points.add(Vector2Int(it, from.y)) }
            else -> throw IllegalArgumentException("The range specified is out of range")
        }

        return points
    }

    /**
     * Returns a line that has the same endpoints and From is smaller than To.
     */
    fun getNormalized(): OrthogonalLineGrid2D {
        if (from == to) return this
        return if (from < to) this else OrthogonalLineGrid2D(to, from)
    }

    fun getNthPoint(n: Int): Vector2Int {
        if (n > length) throw IllegalArgumentException("$n is greater than the length of the line.")

        return when (getDirection()) {
            Direction.Top -> from + Vector2Int(0, n)
            Direction.Right -> from + Vector2Int(n, 0)
            Direction.Bottom -> from - Vector2Int(0, n)
            Direction.Left -> from - Vector2Int(n, 0)
            Direction.Undefined -> {
                if (n > 0) throw IllegalArgumentException()
                from
            }
        }
    }

    fun contains(point: Vector2Int): Int {
        when (getDirection()) {
            Direction.Top -> if (point.x == from.x && point.y <= to.y && point.y >= from.y) return point.y - from.y
            Direction.Right -> if (point.y == from.y && point.x <= to.x && point.x >= from.x) return point.x - from.x
            Direction.Bottom -> if (point.x == from.x && point.y >= to.y && point.y <= from.y) return from.y - point.y
            Direction.Left -> if (point.y == from.y && point.x >= to.x && point.x <= from.x) return from.x - point.x
            Direction.Undefined -> if (point == from) return 0
        }

        return -1
    }

    fun getDirectionVector(): Vector2Int {
        return when (getDirection()) {
            Direction.Top -> Vector2Int(0, 1)
            Direction.Right -> Vector2Int(1, 0)
            Direction.Bottom -> Vector2Int(0, -1)
            Direction.Left -> Vector2Int(-1, 0)
            Direction.Undefined -> throw IllegalArgumentException("Degenerated lines without a direction set do not have a direction vector.")
        }
    }

    operator fun plus(point: Vector2Int): OrthogonalLineGrid2D {
        return OrthogonalLineGrid2D(this.from + point, this.to + point, this.getDirection())
    }
}
