package brewcraft.items

import brewcraft.items.utils.BlockBase
import brewcraft.items.utils.ItemBlockBase
import brewcraft.items.utils.Registerable

object Percolator: Registerable {
        val itemID: String = "percolator"
        override fun register() {
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