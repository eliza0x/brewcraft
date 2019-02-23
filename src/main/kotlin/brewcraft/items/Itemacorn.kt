package brewcraft.items

import brewcraft.items.utils.ItemBase
import brewcraft.util.RegisterableItem
import brewcraft.util.RegisterableModel

object ItemAcorn: RegisterableItem, RegisterableModel {
    override fun registerModel() {
            ItemAcornCastanopsis.registerModel()
            ItemAcornSawtoothOak.registerModel()
    }

    override fun registerItem() {
            ItemAcornCastanopsis.registerItem()
            ItemAcornSawtoothOak.registerItem()
    }
}

object ItemAcornCastanopsis: ItemBase(
        itemID = "acorn_castanopsis"
)

object ItemAcornSawtoothOak: ItemBase(
        itemID = "acorn_sawtooth_oak"
)
