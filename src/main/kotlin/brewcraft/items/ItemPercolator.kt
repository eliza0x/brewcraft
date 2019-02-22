package brewcraft.items

import brewcraft.items.utils.BlockBase
import brewcraft.items.utils.ItemBlockBase
import brewcraft.util.RegisterableItem
import brewcraft.util.RegisterableModel

object Percolator: RegisterableItem, RegisterableModel {
        val itemID: String = "percolator"
        override fun registerModel() {
                BlockPercolator.registerModel()
                ItemBlockPercolator.registerModel()
        }
        override fun registerItem() {
                BlockPercolator.registerItem()
                ItemBlockPercolator.registerItem()

        }
}

object BlockPercolator: BlockBase(
        itemID = Percolator.itemID
)

object ItemBlockPercolator: ItemBlockBase(
        itemID = Percolator.itemID,
        block = BlockPercolator
)