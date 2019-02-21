package brewcraft.recipe

import brewcraft.BrewCraft
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.init.PotionTypes
import net.minecraft.potion.PotionUtils
import net.minecraftforge.common.brewing.BrewingRecipeRegistry

object BrewingRecipes {
    init {
        // Awkward Coffee
        BrewingRecipeRegistry.addRecipe(
                PotionUtils.addPotionToItemStack(ItemStack(Items.POTIONITEM), PotionTypes.AWKWARD),
                ItemStack(BrewCraft.RoastedCoffeePowder),
                ItemStack(BrewCraft.AwkwardCoffee)
        )
    }
}