package brewcraft.items.utils

import brewcraft.BrewCraft
import brewcraft.util.RegisterableItem
import brewcraft.util.RegisterableModel
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemFood
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fml.common.registry.ForgeRegistries

open class ItemFoodBase(
        val itemID: String,
        amount: Int,
        saturation: Float,
        isWolfFood: Boolean = false,
        maxStackSize: Int = 64
) : ItemFood(amount, saturation, isWolfFood), RegisterableModel, RegisterableItem {
    init {
        this.setRegistryName(BrewCraft.MOD_ID, itemID)
        this.setUnlocalizedName(itemID)
        this.setCreativeTab(CreativeTabs.MATERIALS)
        this.setMaxStackSize(maxStackSize)
    }

    override fun registerModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0,
                ModelResourceLocation(BrewCraft.MOD_ID + ":" + itemID, "inventory"))
    }

    override fun registerItem() {
        ForgeRegistries.ITEMS.register(this)
    }
}