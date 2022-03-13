package net.rikusen.dungeoner.dungeon_generator.geometry

import kotlin.math.*

// Reference: https://github.com/OndrejNepozitek/Edgar-DotNet/blob/dev/src/Edgar/Geometry/Vector2Int.cs
data class Vector2Int(val x: Int, val y: Int): Comparable<Vector2Int> {
    companion object {
        /**
         * Computes a manhattan distance of two vectors.
         *
         * @param a
         * @param b
         * @return
         */
        fun manhattanDistance(a: Vector2Int, b: Vector2Int): Int {
            return abs(a.x - b.x) + abs(a.y - b.y)
        }

        /**
         * Compute an euclidean distance of two vectors.
         *
         * @param a
         * @param b
         * @return
         */
        fun euclideanDistance(a: Vector2Int, b: Vector2Int): Double {
            return sqrt((a.x - b.x).toDouble().pow(2) - (a.y - b.y).toDouble().pow(2))
        }

        /**
         * Computes a maximum distance between corresponding components of two vectors.
         *
         * @param a
         * @param b
         * @return
         */
        fun maxDistance(a: Vector2Int, b: Vector2Int): Int {
            return max(abs(a.x - b.x), abs(a.y - b.y))
        }
    }

//    override fun hashCode(): Int {
//        return (x * 397).toDouble().pow(y).toInt()
//    }

    /**
     * Rotate the point around the center.
     *
     * Positive degrees mean clockwise rotation.
     *
     * @param degrees
     * @return
     */
    fun rotateAroundCenter(degrees: Int): Vector2Int {
        val x = x * cos(degrees.toDouble()).toInt()
        val y = y * cos(degrees.toDouble()).toInt()

        return Vector2Int(x, y)
    }

    /**
     * Transforms a given vector.
     *
     * @param transformation
     * @return
     */
    fun transform(transformation: TransformationGrid2D): Vector2Int {
        return when (transformation) {
            TransformationGrid2D.Identity -> this
            TransformationGrid2D.Rotate90 -> rotateAroundCenter(90)
            TransformationGrid2D.Rotate180 -> rotateAroundCenter(180)
            TransformationGrid2D.Rotate270 -> rotateAroundCenter(270)
            TransformationGrid2D.MirrorX -> Vector2Int(x, -y)
            TransformationGrid2D.MirrorY -> Vector2Int(-x, y)
            TransformationGrid2D.Diagonal13 -> Vector2Int(y, x)
            TransformationGrid2D.Diagonal24 -> Vector2Int(-y, -x)
            else -> throw IllegalArgumentException("Given polygon transformation is not implemented")
        }
    }

    /**
     * Computes a dot product of two vectors.
     *
     * @param other
     * @return
     */
    fun dotProduct(other: Vector2Int): Int {
        return x * other.x + y * other.y
    }

    /**
     * Computes element-wise product of two vectors.
     *
     * @param other
     * @return
     */
    fun elementWiseProduct(other: Vector2Int): Vector2Int {
        return Vector2Int(x * other.x, y * other.y)
    }

    /**
     * Gets all vectors that are adjacent to this one.
     * That means vector that are different by 1 in exactly one of its components.
     *
     * @return
     */
    fun getAdjacentVectors(): List<Vector2Int> {
        return arrayListOf(
            Vector2Int(x + 1, y),
            Vector2Int(x - 1, y),
            Vector2Int(x, y + 1),
            Vector2Int(x, y - 1)
        )
    }

    /**
     * Gets all vectors that are adjacent and diagonal to this one.
     * That means vector that are different by 1 both of its components.
     *
     * @return
     */
    fun getAdjacentAndDiagonalVectors(): List<Vector2Int> {
        val positions = getAdjacentVectors()

        positions.plus(Vector2Int(x + 1, y + 1))
        positions.plus(Vector2Int(x - 1, y - 1))
        positions.plus(Vector2Int(x - 1, y + 1))
        positions.plus(Vector2Int(x + 1, y - 1))

        return positions
    }

    operator fun plus(other: Vector2Int): Vector2Int {
        return Vector2Int(x + other.x, y + other.y)
    }

    operator fun plus(line: OrthogonalLineGrid2D): OrthogonalLineGrid2D {
        return this + line
    }

    operator fun minus(other: Vector2Int): Vector2Int {
        return Vector2Int(x - other.x, y - other.y)
    }

    operator fun times(other: Vector2Int): Vector2Int {
        return Vector2Int(x * other.x, y * other.y)
    }

    override operator fun compareTo(other: Vector2Int): Int {
        if (this == other) return 0
        return (this.x shl 1 + this.y shl 1) - (other.x shl 1 + other.y shl 1)
    }
}