package brewcraft.items.utils

import brewcraft.BrewCraft
import brewcraft.util.RegisterableItem
import brewcraft.util.RegisterableModel
import net.minecraft.block.BlockContainer
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fml.common.registry.ForgeRegistries

open class BlockContainerBase (
        val itemID: String
): BlockContainer(Material.SAND), RegisterableItem, RegisterableModel {
    override fun createNewTileEntity(p0: World, p1: Int): TileEntity? {
        throw NotImplementedError("This is a dummy method")
    }
    init {
        val isBlockContainer: Boolean = true
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