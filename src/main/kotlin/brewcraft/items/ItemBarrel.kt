package brewcraft.items

import brewcraft.items.utils.ItemBlockBase
import brewcraft.util.RegisterableItem
import brewcraft.util.RegisterableModel
import brewcraft.BrewCraft
import brewcraft.items.utils.BlockContainerBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraft.network.play.server.SPacketUpdateTileEntity
import net.minecraft.network.NetworkManager
import java.lang.Exception


object Barrel: RegisterableItem, RegisterableModel {
    val itemID: String = "barrel"

    @SidedProxy(clientSide = "brewcraft.items.ClientProxyBarrel", serverSide = "brewcraft.items.CommonProxyBarrel")
    var proxy = CommonProxyBarrel()

    val itemBlockBarrel = ItemBlockBarrel()
    override fun registerItem() {
        proxy.registerTileEntity()
        BlockBarrel.registerItem()
        itemBlockBarrel.registerItem()
    }
    override fun registerModel() {
        BlockBarrel.registerModel()
        itemBlockBarrel.registerModel()
    }
}

object BlockBarrel: BlockContainerBase(
        itemID = Barrel.itemID
) {
    override fun onBlockClicked(world: World, pos: BlockPos, state: EntityPlayer) {
        val tile: TileEntity? = world.getTileEntity(pos)
        if (tile is TileBarrel && !world.isRemote) {
          tile.markDirty()
        }
        super.onBlockClicked(world, pos, state)
    }
    override fun createNewTileEntity(p0: World, p1: Int): TileEntity? {
        return TileBarrel()
    }
}

class ItemBlockBarrel: ItemBlockBase(
        itemID = Stove.itemID,
        block = BlockBarrel
)

class TileBarrel: TileEntity() {
    private val storedContainer: BarrelContainer = BarrelContainer()

    override fun readFromNBT(tagCompound: NBTTagCompound) {
        super.readFromNBT(tagCompound)
    }

    override fun writeToNBT(tagCompound: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(tagCompound)
        return tagCompound
    }

    override fun getUpdatePacket(): SPacketUpdateTileEntity? {
        val nbtTag = NBTTagCompound()
        //Write your data into the nbtTag
        return SPacketUpdateTileEntity(getPos(), 1, nbtTag)
    }

    override fun onDataPacket(net: NetworkManager?, pkt: SPacketUpdateTileEntity?) {
        val tag = pkt!!.nbtCompound
        //Handle your Data
    }
}

open class CommonProxyBarrel {
    open fun getClientWorld(): World? {
        return null
    }

    open fun registerTileEntity() {
        GameRegistry.registerTileEntity(TileBarrel::class.java,
                ResourceLocation(BrewCraft.MOD_ID + Barrel.itemID + "_tile"))
    }
}

class ClientProxyBarrel: CommonProxyBarrel() {
    override fun getClientWorld(): World? {
        return FMLClientHandler.instance().worldClient
    }


    override fun registerTileEntity() {
        GameRegistry.registerTileEntity(TileBarrel::class.java,
                ResourceLocation(BrewCraft.MOD_ID + Barrel.itemID + "_tile"))
    }
}

data class BarrelContainer(
        val items: MutableMap<Item, Int> = mutableMapOf()
) {
    fun itemIntoBarrel(item: Item) {
        items[item] = 1 + (items[item] ?: 0)
    }
}

object BarrelRecipe {
    private val recipes: MutableMap<Item, Map<Item, ClosedRange<Int>>> = mutableMapOf()

    fun register(result: Item, vararg recipe: Pair<Item, ClosedRange<Int>>) {
        // すでにレシピが登録されていた場合例外
        if (recipes[result] != null) {
            throw Exception("This recipe is already registered.")
        }
        recipes[result] = recipe.toMap()
    }

    fun matchOf(barrelContainer: BarrelContainer): Item? {
        // O(N^2), 木を作れば高速化できるがそこまでやる必要があるか不明
        val barrelItems: List<Pair<Item, Int>> = barrelContainer.items.toList()
        recipes.forEach { recipe ->
            val result: Item = recipe.key
            val recipe: Map<Item, ClosedRange<Int>> = recipe.value
            if (recipe.size == barrelItems.size) {
                if (barrelItems.all { item ->
                    recipe[item.first] ?. contains(item.second) ?: false
                }) {
                    return result
                }
            }
        }
        return null
    }
}
