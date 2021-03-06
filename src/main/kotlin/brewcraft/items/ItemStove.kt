package brewcraft.items

import brewcraft.BrewCraft
import brewcraft.items.utils.BlockContainerBase
import brewcraft.items.utils.ItemBlockBase
import brewcraft.util.RegisterableItem
import brewcraft.util.RegisterableModel
import net.minecraft.entity.player.EntityPlayer
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

object Stove: RegisterableItem, RegisterableModel {
    val itemID: String = "stove"

    @SidedProxy(clientSide = "brewcraft.items.ClientProxyStove", serverSide = "brewcraft.items.CommonProxyStove")
    var proxy = CommonProxyStove()

    override fun registerItem() {
        proxy.registerTileEntity()
        BlockStove.registerItem()
        ItemBlockStove.registerItem()
    }
    override fun registerModel() {
        BlockStove.registerModel()
        ItemBlockStove.registerModel()
    }
}

object BlockStove: BlockContainerBase(
        itemID = Stove.itemID
) {
    override fun onBlockClicked(world: World, pos: BlockPos, state: EntityPlayer) {
        val tile: TileEntity? = world.getTileEntity(pos)
        if (tile is TileStove && !world.isRemote) {
          tile.incrementCnt()
          tile.markDirty()
          BrewCraft.logger!!.info("----------")
          BrewCraft.logger!!.info("${tile.hashCode()}")
          BrewCraft.logger!!.info("${pos}")
          BrewCraft.logger!!.info("${tile.cnt}")
          BrewCraft.logger!!.info("${tile.cntCpy}")
        }
        super.onBlockClicked(world, pos, state)
    }
    override fun createNewTileEntity(p0: World, p1: Int): TileEntity? {
        return TileStove()
    }
}

object ItemBlockStove: ItemBlockBase(
        itemID = Stove.itemID,
        block = BlockStove
)

class TileStove: TileEntity() {
    var cnt = 0
    var cntCpy = 0

    fun incrementCnt() {
        cnt = cnt + 1
    }

    override fun readFromNBT(tagCompound: NBTTagCompound) {
        super.readFromNBT(tagCompound)
        this.cnt = tagCompound.getInteger("cnt")
        this.cntCpy = tagCompound.getInteger("cnt")
    }

    override fun writeToNBT(tagCompound: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(tagCompound)
        tagCompound.setInteger("cnt", this.cnt)
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

open class CommonProxyStove {
    open fun getClientWorld(): World? {
        return null
    }

    open fun registerTileEntity() {
        GameRegistry.registerTileEntity(TileStove::class.java,
                ResourceLocation(BrewCraft.MOD_ID + Stove.itemID + "_tile"))
    }
}

class ClientProxyStove: CommonProxyStove() {
    override fun getClientWorld(): World? {
        return FMLClientHandler.instance().worldClient
    }

    override fun registerTileEntity() {
        GameRegistry.registerTileEntity(TileStove::class.java,
                ResourceLocation(BrewCraft.MOD_ID + Stove.itemID + "_tile"))
    }
}

