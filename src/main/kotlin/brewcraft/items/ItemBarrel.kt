package brewcraft.items

import brewcraft.items.utils.BlockBase
import brewcraft.items.utils.ItemBlockBase
import brewcraft.util.RegisterableItem
import brewcraft.util.RegisterableModel

object Barrel: RegisterableItem, RegisterableModel {
    val itemID: String = "barrel"
    override fun registerModel() {
        BlockBarrel.registerModel()
        ItemBlockBarrel.registerModel()
    }
    override fun registerItem() {
        BlockBarrel.registerItem()
        ItemBlockBarrel.registerItem()

    }
}

object BlockBarrel: BlockBase(
        itemID = Barrel.itemID
)

object ItemBlockBarrel: ItemBlockBase(
        itemID = Barrel.itemID,
        block = BlockBarrel
)