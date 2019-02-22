package brewcraft

import brewcraft.items.*
import brewcraft.items.Stove.itemID
import brewcraft.recipe.BrewingRecipes
import brewcraft.recipe.SmeltingRecipes
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.registry.GameRegistry
import org.apache.logging.log4j.Logger
import javax.annotation.Resource

@Mod(
        modid = BrewCraft.MOD_ID,
        name = BrewCraft.NAME,
        version = BrewCraft.VERSION,
        acceptedMinecraftVersions = BrewCraft.MOD_ACCEPTED_MV_VERSIONS,
        modLanguage = "kotlin"
)
class BrewCraft {
    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        logger = event.modLog
    }

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        logger!!.info("BrewCraft initialising...")

        // TofuCraftの大豆と出現確率を合わせた
        MinecraftForge.addGrassSeed(ItemStack(CoffeeBean), 2)
    }

    @SubscribeEvent
    fun registerBlocks(event: RegistryEvent.Register<Block>) {}

    companion object {
        const val MOD_ID = "brewcraft"
        const val NAME = "BrewCraft"
        const val VERSION = "1.0"
        const val MOD_ACCEPTED_MV_VERSIONS = "[1.12.2]"

        var logger: Logger? = null

        val AwkwardCoffee: Item = ItemAwkwardCoffee
        val CoffeeBean: Item = ItemCoffeeBean
        val RoastedCoffeeBean: Item = ItemRoastedCoffeeBean
        val RoastedCoffeePowder: Item = ItemRoastedCoffeePowder

        val AddSmeltingRecipes: SmeltingRecipes = SmeltingRecipes
        val AddBrewingRecipes: BrewingRecipes = BrewingRecipes
    }

    init {
        Percolator.register()
        Stove.register()
        ItemAwkwardCoffee.register()
        ItemCoffeeBean.register()
        ItemHandMill.register()
        ItemPlateIron.register()
        ItemRoastedCoffeeBean.register()
        ItemRoastedCoffeePowder.register()
        ItemTeaLeaf.register()
    }
}
