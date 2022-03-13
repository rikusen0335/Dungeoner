package net.rikusen.dungeoner.dungeon_generator.legacy.core.layout_converters.interfaces

// Reference: https://github.com/OndrejNepozitek/Edgar-DotNet/blob/dev/src/Edgar/Legacy/Core/LayoutConverters/Interfaces/ILayoutConverter.cs
interface ILayoutConverters<in TLayoutFrom, out TLayoutTo> {
    fun convert(layout: TLayoutFrom, addDoors: Boolean): TLayoutTo
}