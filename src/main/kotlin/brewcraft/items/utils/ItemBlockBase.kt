package brewcraft.items.utils

import brewcraft.BrewCraft
import brewcraft.util.RegisterableItem
import brewcraft.util.RegisterableModel
import net.minecraft.block.Block
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.ItemBlock
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fml.common.registry.ForgeRegistries

open class ItemBlockBase (
        val itemID: String,
        block: Block
): ItemBlock(block), RegisterableItem, RegisterableModel {
    init {
        this.setRegistryName(BrewCraft.MOD_ID, itemID)
    }
    override fun registerModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0,
                ModelResourceLocation(BrewCraft.MOD_ID + ":" + itemID, "inventory"))
    }

    override fun registerItem() {
        ForgeRegistries.ITEMS.register(this)
    }
}
