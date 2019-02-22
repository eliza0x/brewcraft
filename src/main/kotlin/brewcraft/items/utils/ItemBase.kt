package brewcraft.items.utils

import brewcraft.BrewCraft
import brewcraft.util.RegisterableItem
import brewcraft.util.RegisterableModel
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fml.common.registry.ForgeRegistries

open class ItemBase(
        val itemID: String,
        maxStackSize: Int = 64
) : Item(), RegisterableItem, RegisterableModel {
    init {
        this.setRegistryName(BrewCraft.MOD_ID, itemID)
        this.setUnlocalizedName(itemID)
        this.setCreativeTab(CreativeTabs.MATERIALS)
    }
    override fun registerModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0,
            ModelResourceLocation(BrewCraft.MOD_ID + ":" + itemID, "inventory"))
    }

    override fun registerItem() {
        ForgeRegistries.ITEMS.register(this)
    }
}
