package brewcraft.recipe

import brewcraft.items.BarrelRecipe
import brewcraft.items.ItemNewMakeSpirit
import brewcraft.items.ItemWhiskey
import brewcraft.util.RegisterableItem

object BarrelRecipes: RegisterableItem {
    override fun registerItem() {
        BarrelRecipe.register(
                ItemWhiskey,
                24000, // 1day
                ItemNewMakeSpirit to IntRange(1,1)
        )
    }
}