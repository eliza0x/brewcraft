package brewcraft.recipe

import net.minecraft.item.ItemStack
import net.minecraftforge.fml.common.registry.GameRegistry
import brewcraft.BrewCraft

object SmeltingRecipes {
    init {
        // roast coffee bean
        GameRegistry.addSmelting(
                ItemStack(BrewCraft.CoffeeBean),
                ItemStack(BrewCraft.RoastedCoffeeBean, 1),
                0.5f)
    }
}