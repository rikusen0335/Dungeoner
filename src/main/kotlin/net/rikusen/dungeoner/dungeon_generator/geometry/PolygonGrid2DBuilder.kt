package net.rikusen.dungeoner.dungeon_generator.geometry

class PolygonGrid2DBuilder {
    private val points = arrayListOf<Vector2Int>();
    
    fun addPoint(x: Int, y: Int): PolygonGrid2DBuilder {
        points.add(Vector2Int(x, y))
        return this;
    }

    fun build(): PolygonGrid2D {
        return PolygonGrid2D(points);
    }
}