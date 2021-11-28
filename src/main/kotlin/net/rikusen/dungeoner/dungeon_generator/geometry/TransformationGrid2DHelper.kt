package net.rikusen.dungeoner.dungeon_generator.geometry

class TransformationGrid2DHelper {
    companion object {
        fun getAll(): List<TransformationGrid2D> {
            return TransformationGrid2D.values().toList()
        }

        fun getRotations(includeEntity: Boolean = true): List<TransformationGrid2D> {
            return arrayListOf(
                TransformationGrid2D.Identity,
                TransformationGrid2D.Rotate90,
                TransformationGrid2D.Rotate180,
                TransformationGrid2D.Rotate270
            )
        }
    }
}