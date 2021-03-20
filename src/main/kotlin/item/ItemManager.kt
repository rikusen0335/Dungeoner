package item

import net.kyori.adventure.text.Component
import org.bukkit.inventory.ItemStack

/*
TODO
というか、アイテム系の管理はMythicMobsでやったほうがいいのでは？
 */
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
        val loreList: ArrayList<Component> = arrayListOf(itemEnum.lore)
        newItemMeta.lore(loreList)

        item.itemMeta = newItemMeta

        return item
    }
}