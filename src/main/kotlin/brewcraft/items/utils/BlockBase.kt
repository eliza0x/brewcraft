package brewcraft.items.utils

import brewcraft.BrewCraft
import brewcraft.util.RegisterableItem
import brewcraft.util.RegisterableModel
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fml.common.registry.ForgeRegistries

open class BlockBase (
        val itemID: String
) : Block(Material.IRON), RegisterableModel, RegisterableItem {
        init {
                this.setRegistryName(BrewCraft.MOD_ID, itemID)
                this.setUnlocalizedName(itemID)
                this.setCreativeTab(CreativeTabs.MATERIALS)
                this.setHardness(0.5F);
                this.setResistance(0.5F);
        }
    override fun registerModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0,
                ModelResourceLocation(BrewCraft.MOD_ID + ":" + itemID, "inventory"))
    }
    override fun registerItem() {
        ForgeRegistries.BLOCKS.register(this)
    }
}