package brewcraft.items.utils

import brewcraft.BrewCraft
import net.minecraft.block.Block
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.ItemBlock
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fml.common.registry.ForgeRegistries

open class ItemBlockBase (
        val itemID: String,
        block: Block
): ItemBlock(block) {
    init {
        this.setRegistryName(BrewCraft.MOD_ID, itemID)
    }
    fun register() {
        ForgeRegistries.ITEMS.register(this)
        ModelLoader.setCustomModelResourceLocation(this, 0,
                ModelResourceLocation(BrewCraft.MOD_ID + ":" + itemID, "inventory"))
    }
}
