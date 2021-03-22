package net.rikusen.dungeoner.item

import net.kyori.adventure.text.Component
import org.bukkit.entity.Item
import org.bukkit.inventory.ItemStack
import org.yaml.snakeyaml.Yaml
import org.bukkit.configuration.InvalidConfigurationException

import java.io.IOException

import org.bukkit.configuration.file.YamlConfiguration
import java.io.File


object ItemManager {

    fun get(itemName: String): ItemStack {
        val itemEnum = ItemEnum.valueOf(itemName)
        val item = ItemStack(itemEnum.material)
        val newItemMeta = item.itemMeta

        /*
        アイテムの名前を設定する
        Set the item name
         */
        newItemMeta.displayName(itemEnum.displayName)

        /*
        アイテムに説明を追加する
        Add lore to item
         */
        val loreList: ArrayList<Component> = itemEnum.lore
        newItemMeta.lore(loreList)

        item.itemMeta = newItemMeta

        return item
    }
}