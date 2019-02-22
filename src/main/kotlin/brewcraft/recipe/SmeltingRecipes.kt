package brewcraft.recipe

import net.minecraft.item.ItemStack
import net.minecraftforge.fml.common.registry.GameRegistry
import brewcraft.BrewCraft
import brewcraft.util.RegisterableItem

object SmeltingRecipes: RegisterableItem {
    override fun registerItem() {
        // roast coffee bean
        GameRegistry.addSmelting(
                ItemStack(BrewCraft.CoffeeBean),
                ItemStack(BrewCraft.RoastedCoffeeBean, 1),
                0.5f)
    }
}