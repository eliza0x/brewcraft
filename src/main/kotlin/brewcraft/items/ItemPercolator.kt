package brewcraft.items

import brewcraft.items.utils.BlockBase
import brewcraft.items.utils.ItemBlockBase

object Percolator {
        val itemID: String = "percolator"
        fun register() {
                BlockPercolator.register()
                ItemBlockPercolator.register()
        }
}

object BlockPercolator: BlockBase(
        itemID = Percolator.itemID
)

object ItemBlockPercolator: ItemBlockBase(
        itemID = Percolator.itemID,
        block = BlockPercolator
)