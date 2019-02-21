package brewcraft.items.utils

import brewcraft.BrewCraft
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fml.common.registry.ForgeRegistries

open class ItemBase(
        itemID: String,
        maxStackSize: Int = 64
) : Item() {
    init {
        this.setRegistryName(BrewCraft.MOD_ID, itemID)
        this.setUnlocalizedName(itemID)
        this.setCreativeTab(CreativeTabs.MATERIALS)
        this.setMaxStackSize(maxStackSize)

        ModelLoader.setCustomModelResourceLocation(this, 0,
                ModelResourceLocation(BrewCraft.MOD_ID + ":" + itemID, "inventory"))
    }
    fun register() = ForgeRegistries.ITEMS.register(this)
}
