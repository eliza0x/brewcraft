package brewcraft.items.utils

import brewcraft.BrewCraft
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fml.common.registry.ForgeRegistries

open class ItemBase(
        val itemID: String,
        maxStackSize: Int = 64
) : Item(), Registerable {
    init {
        this.setRegistryName(BrewCraft.MOD_ID, itemID)
        this.setUnlocalizedName(itemID)
        this.setCreativeTab(CreativeTabs.MATERIALS)
    }
    override fun register() {
        ForgeRegistries.ITEMS.register(this)
        ModelLoader.setCustomModelResourceLocation(this, 0,
            ModelResourceLocation(BrewCraft.MOD_ID + ":" + itemID, "inventory"))
    }
}
