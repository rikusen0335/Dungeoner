package net.rikusen.dungeoner

import java.util.*

class TilePaths(val north: Boolean, val east: Boolean, val south: Boolean, val west: Boolean) {
    override fun equals(other: Any?): Boolean {
        return other is TilePaths
                && other.north == north
                && other.east == east
                && other.south == south
                && other.west == west
    }

    override fun hashCode(): Int {
        var result = north.hashCode()
        result = 31 * result + east.hashCode()
        result = 31 * result + south.hashCode()
        result = 31 * result + west.hashCode()
        return result
    }
}