package brewcraft.items

import brewcraft.items.utils.ItemBase
import net.minecraft.item.ItemStack

object ItemHandMill : ItemBase(
        itemID = "hand_mill"
) {
    init {
        this.setMaxDamage(8)
    }

    override fun hasContainerItem(stack: ItemStack): Boolean {
        return true
    }

    override fun getContainerItem(itemStack: ItemStack): ItemStack {
        return if (itemStack.maxDamage == itemStack.itemDamage) {
            ItemStack.EMPTY
        } else {
            val newItemStack = itemStack.copy()
            newItemStack.itemDamage = itemStack.itemDamage + 1
            newItemStack
        }
    }
}