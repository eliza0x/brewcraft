package brewcraft.items.utils

import brewcraft.BrewCraft
import brewcraft.items.BlockStove
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fml.common.registry.ForgeRegistries

open class BlockBase (
        val itemID: String
) : Block(Material.IRON), Registerable {
        init {
                this.setRegistryName(BrewCraft.MOD_ID, itemID)
                this.setUnlocalizedName(itemID)
                this.setCreativeTab(CreativeTabs.MATERIALS)
                this.setHardness(0.5F);
                this.setResistance(0.5F);
        }
    override fun register() {
        ForgeRegistries.BLOCKS.register(this)
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0,
                ModelResourceLocation(BrewCraft.MOD_ID + ":" + itemID, "inventory"))
    }
}