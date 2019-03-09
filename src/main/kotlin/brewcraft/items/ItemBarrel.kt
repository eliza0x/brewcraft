package brewcraft.items

import brewcraft.items.utils.ItemBlockBase
import brewcraft.util.RegisterableItem
import brewcraft.util.RegisterableModel
import brewcraft.BrewCraft
import brewcraft.items.utils.BlockContainerBase
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
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
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import java.lang.Exception

object Barrel: RegisterableItem, RegisterableModel {
    val itemID: String = "barrel"

    @SidedProxy(clientSide = "brewcraft.items.ClientProxyBarrel", serverSide = "brewcraft.items.CommonProxyBarrel")
    var proxy = CommonProxyBarrel()

    override fun registerItem() {
        proxy.registerTileEntity()
        BlockBarrel.registerItem()
        ItemBlockBarrel.registerItem()
    }
    override fun registerModel() {
        BlockBarrel.registerModel()
        ItemBlockBarrel.registerModel()
    }
}

object BlockBarrel: BlockContainerBase(
        itemID = Barrel.itemID
) {
    override fun onBlockActivated(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer, hand: EnumHand, facing: EnumFacing, x: Float, y: Float, z: Float): Boolean {
        val tile: TileEntity? = world.getTileEntity(pos)
        val heldItem: Item = player.getHeldItem(EnumHand.MAIN_HAND).item
        if (tile is TileBarrel && !world.isRemote && tile.container.canIntoItem(heldItem)) {
            tile.container.itemIntoBarrel(heldItem)
            declPlayerInventryCurrenyItem(player)
            tile.markDirty()

            for (i in tile.container.items.toList()) {
                println("${i.first.unlocalizedName} - ${i.second}")
            }
        }
        return super.onBlockActivated(world, pos, state, player, hand, facing, x, y, z)
    }

    fun declPlayerInventryCurrenyItem(player: EntityPlayer) {
        val heldItemStack: ItemStack = player.getHeldItem(EnumHand.MAIN_HAND)
        // プレイヤーの手持ちを減少
        heldItemStack.count--
        if (!player.isCreative && heldItemStack.count <= 0) {
            player.inventory.setInventorySlotContents(player.inventory.currentItem, ItemStack.EMPTY)
        }
        player.inventory.markDirty()
    }

    override fun createNewTileEntity(p0: World, p1: Int): TileEntity? {
        return TileBarrel()
    }
}

object ItemBlockBarrel: ItemBlockBase(
        itemID = Barrel.itemID,
        block = BlockBarrel
)

class TileBarrel: TileEntity() {
    val container: BarrelContainer = BarrelContainer()

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
        val items: MutableMap<Item, Int> = mutableMapOf(),
        private var internalLiquidCapacity: Int = 0,
        private val maxInternalLiquidCapacity: Int = 1,
        private var internalItemCapacity: Int = 0,
        private val maxInternalItemCapacity: Int = 32
) {
    fun canIntoItem(item: Item): Boolean {
        return canIntoItemVearias(item) && canIntoItemCapacity(item)
    }

    private fun canIntoItemVearias(item: Item): Boolean {
        val list: List<Item> = listOf(
                Item.getByNameOrId("minecraft:wheat")!!,
                Item.getByNameOrId("minecraft:water_bucket")!!,
                Item.getByNameOrId("brewcraft:new_make_spirit")!!
        )
        return list.contains(item)
    }
    private fun canIntoItemCapacity(item: Item): Boolean {
        if (isLiquid(item)) {
            return internalLiquidCapacity < maxInternalLiquidCapacity
        } else {
            return internalItemCapacity < maxInternalItemCapacity
        }
    }
    // TODO: Liquid判定をItemに付けられるようにする, LiquidInterfaceかなにか？
    fun isLiquid(item: Item): Boolean {
         val list: List<Item> = listOf(
                Item.getByNameOrId("minecraft:water_bucket")!!,
                Item.getByNameOrId("brewcraft:new_make_spirit")!!
        )
        return list.contains(item)
    }
    fun itemIntoBarrel(item: Item) {
        items[item] = 1 + (items[item] ?: 0)
        if (isLiquid(item)) { internalLiquidCapacity++ } else { internalItemCapacity++ }
    }
}

data class BarrelMaterial(
       val neccesaryTick: Int,
       val material: Map<Item, IntRange>
)

object BarrelRecipe {
    private val recipes: MutableMap<Item, BarrelMaterial> = mutableMapOf()

    // TODO: 時間も設定できるように, 樽の材質で結果を変更させても良い
    fun register(result: Item, neccesaryTick: Int, vararg newRecipe: Pair<Item, IntRange>) {
        // すでに同じレシピが登録されていた場合例外
        if (recipes[result]?.material == newRecipe) {
            throw Exception("This recipe is already registered.")
        }
        recipes[result] = BarrelMaterial(neccesaryTick, newRecipe.toMap())
    }

    fun matchOf(barrelContainer: BarrelContainer): Item? {
        // O(N^2), 木を作れば高速化できるがそこまでやる必要があるか不明
        val barrelItems: List<Pair<Item, Int>> = barrelContainer.items.toList()
        recipes.forEach { recipe ->
            val result: Item = recipe.key
            val recipe: Map<Item, IntRange> = recipe.value.material
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
