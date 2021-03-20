package item

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import org.bukkit.Material

enum class ItemEnum(val material: Material, val displayName: Component, val lore: Component) {
    BASIC_STEAK(Material.COOKED_BEEF, Component.text("焼かれたステーキ"), Component.text("スタミナ回復"));
}