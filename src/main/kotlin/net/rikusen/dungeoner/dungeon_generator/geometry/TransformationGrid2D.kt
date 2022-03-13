package net.rikusen.dungeoner.dungeon_generator.geometry

enum class TransformationGrid2D {
    Identity,
    Rotate90, Rotate180, Rotate270,
    MirrorX, MirrorY,
    Diagonal13, Diagonal24
}