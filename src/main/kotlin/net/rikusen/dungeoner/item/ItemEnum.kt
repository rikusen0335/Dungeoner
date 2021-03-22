package net.rikusen.dungeoner.item

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.util.HSVLike
import org.bukkit.Material

enum class ItemEnum(val material: Material, val displayName: Component, val lore: ArrayList<Component>) {
    BASIC_STEAK(
        Material.COOKED_BEEF,
        Component.text("焼かれたステーキ")
            .color(TextColor.color(NamedTextColor.WHITE))
            .decoration(TextDecoration.ITALIC, false),
        arrayListOf(
            Component.text("スタミナ回復")
                .color(TextColor.color(NamedTextColor.GRAY))
                .decoration(TextDecoration.ITALIC, false)
        )
    ),
    IRON_SWORD(
        Material.IRON_SWORD,
        Component.text("鉄の剣")
            .color(TextColor.color(NamedTextColor.WHITE))
            .decoration(TextDecoration.ITALIC, false),
        arrayListOf(
            Component.text("ごくありふれた鉄の剣。")
                .color(TextColor.color(NamedTextColor.GRAY))
                .decoration(TextDecoration.ITALIC, false),
            Component.text("金型があるので、誰が作っても同じような形になる。")
                .color(TextColor.color(NamedTextColor.GRAY))
                .decoration(TextDecoration.ITALIC, false),
            Component.text(""),
            Component.text("[性能]")
                .color(TextColor.color(NamedTextColor.GREEN))
                .decoration(TextDecoration.ITALIC, false),
            Component.text("  斬撃 ")
                .color(TextColor.color(NamedTextColor.GRAY))
                .decoration(TextDecoration.ITALIC, false)
                .append(
                    Component.text("8")
                        .color(TextColor.color(NamedTextColor.GOLD))
                        .decoration(TextDecoration.ITALIC, false)
                        .decoration(TextDecoration.BOLD, true)
                ),
            Component.text("  打撃 ")
                .color(TextColor.color(NamedTextColor.GRAY))
                .decoration(TextDecoration.ITALIC, false)
                .append(
                    Component.text("1")
                        .color(TextColor.color(NamedTextColor.GOLD))
                        .decoration(TextDecoration.ITALIC, false)
                        .decoration(TextDecoration.BOLD, true)
                ),
            Component.text(""),
        )
    );
}