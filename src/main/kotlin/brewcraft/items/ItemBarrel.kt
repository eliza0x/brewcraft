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
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraft.network.play.server.SPacketUpdateTileEntity
import net.minecraft.network.NetworkManager
import net.minecraft.util.*
import java.lang.Exception

object Barrel: RegisterableItem, RegisterableModel {
    const val itemID: String = "barrel"

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
    // アイテムの投入
    override fun onBlockActivated(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer, hand: EnumHand, facing: EnumFacing, x: Float, y: Float, z: Float): Boolean {
        val tile: TileEntity? = world.getTileEntity(pos)
        val heldItem: Item = player.getHeldItem(EnumHand.MAIN_HAND).item
        if (tile is TileBarrel && !world.isRemote && tile.container.canIntoItem(heldItem)) {
            tile.container.itemIntoBarrel(heldItem)
            declPlayerInventryCurrenyItem(player)
            tile.markDirty()

            for (i in tile.container.getItems().toList()) {
                println("${i.first.unlocalizedName} - ${i.second}")
            }
        }
        return super.onBlockActivated(world, pos, state, player, hand, facing, x, y, z)
    }

    // 投入されたアイテムをプレイヤーから引く
    private fun declPlayerInventryCurrenyItem(player: EntityPlayer) {
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

class TileBarrel: TileEntity(), ITickable {
    val container: BarrelContainer = BarrelContainer()

    override fun update() {
        container.updateTick()
    }

    /*
    override fun readFromNBT(tagCompound: NBTTagCompound) {
        super.readFromNBT(tagCompound)
    }
    */

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
        // val tag = pkt!!.nbtCompound
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
        private val items: MutableMap<Item, Int> = mutableMapOf(),
        private val maxInternalLiquidCapacity: Int = 1,
        private val maxInternalItemCapacity: Int = 32,
        private var resultRecipe: Recipe? = null, // バレルの中身が変化するのに必要な制約と材料、結果
        private var sinceLastUpdatedAt: Int = 0 // バレルの内容が更新されてから何tick経ったか
) {
    fun getItems(): Map<Item, Int> = items

    fun updateTick() {
        sinceLastUpdatedAt += 1
        val recipe = resultRecipe
        if (recipe != null && recipe.neccesaryTick <= sinceLastUpdatedAt) {
            sinceLastUpdatedAt = 0
            items.clear()
            itemIntoBarrel(recipe.result)
        }
    }

    fun canIntoItem(item: Item): Boolean = isPutAbleItem(item) && isSafeCapacity(item)

    fun itemIntoBarrel(item: Item) {
        items[item] = 1 + (items[item] ?: 0)
        // 毎tick計算すると重いので、アイテム追加時に計算
        resultRecipe = BarrelRecipe.matchOf(items.toList())
        println("recipe: " + (resultRecipe?.result?.unlocalizedName?:"null"))
    }

    private fun isSafeCapacity(item: Item): Boolean {
        return if (isLiquid(item)) {
            val internalLiquidCapacity: Int = items.filter { isLiquid(it.key) }.map { it.value }.sum()
            internalLiquidCapacity < maxInternalLiquidCapacity
        } else {
            val internalItemCapacity: Int = items.filter { !isLiquid(it.key) }.map { it.value }.sum()
            internalItemCapacity < maxInternalItemCapacity
        }
    }

    private fun isPutAbleItem(item: Item): Boolean {
        return setOf(
                Item.getByNameOrId("minecraft:wheat")!!,
                Item.getByNameOrId("minecraft:water_bucket")!!,
                Item.getByNameOrId("brewcraft:new_make_spirit")!!,
                Item.getByNameOrId("brewcraft:whiskey")!!
        ).contains(item)
    }

    // TODO: Liquid判定をItemに付けられるようにする, LiquidInterfaceかなにか？
    private fun isLiquid(item: Item): Boolean {
         return setOf(
                Item.getByNameOrId("minecraft:water_bucket")!!,
                Item.getByNameOrId("brewcraft:new_make_spirit")!!,
                Item.getByNameOrId("brewcraft:whiskey")!!
        ).contains(item)
    }
}

data class Recipe(
       val result: Item,
       val neccesaryTick: Int,
       val material: Map<Item, IntRange>
)

object BarrelRecipe {
    private val recipes: MutableMap<Item, Recipe> = mutableMapOf()

    // TODO: 時間も設定できるように, 樽の材質で結果を変更させても良い
    fun register(result: Item, neccesaryTick: Int, vararg newRecipe: Pair<Item, IntRange>) {
        // すでに同じレシピが登録されていた場合例外
        // 同じアイテムを作るレシピはok
        if (recipes[result]?.material == newRecipe) {
            throw Exception("This recipe is already registered.")
        }
        recipes[result] = Recipe(result, neccesaryTick, newRecipe.toMap())
    }

    fun matchOf(materials: List<Pair<Item, Int>>): Recipe? {
        // O(N^3), 木を作れば高速化できるがそこまでやる必要があるか不明
        recipes.forEach { recipe ->
            val recipeRequiredMaterials: Map<Item, IntRange> = recipe.value.material
            if (recipeRequiredMaterials.size == materials.size) {
                if (materials.all { item ->
                    recipeRequiredMaterials[item.first] ?. contains(item.second) ?: false
                }) {
                    return recipe.value
                }
            }
        }
        return null
    }
}
