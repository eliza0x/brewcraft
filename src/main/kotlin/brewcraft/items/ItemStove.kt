package brewcraft.items

import brewcraft.BrewCraft
import brewcraft.items.Stove.itemID
import brewcraft.items.utils.BlockContainerBase
import brewcraft.items.utils.ItemBlockBase
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




object Stove {
    val itemID: String = "stove"

    @SidedProxy(clientSide = "brewcraft.items.StoveClientProxy", serverSide = "brewcraft.items.StoveCommonProxy")
    val proxy = StoveCommonProxy()

    fun register() {
        proxy.registerTileEntity(TileStove())
        BlockStove.register()
        ItemBlockStove.register()
    }
}

object BlockStove: BlockContainerBase(
        itemID = itemID,
        tileEntity = TileStove()
) {
    override fun onBlockClicked(world: World, pos: BlockPos, state: EntityPlayer) {
        val tile: TileEntity? = world.getTileEntity(pos)
        if (tile is TileStove) {
            tile.incrementCnt()
            tile.markDirty()
            if (!world.isRemote) {
                BrewCraft.logger!!.info("----------")
                BrewCraft.logger!!.info("${tile.cnt}")
                BrewCraft.logger!!.info("${tile.cntCpy}")
            }
        }
        super.onBlockClicked(world, pos, state)
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
        print("rrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr\n")
        super.readFromNBT(tagCompound)
        this.cnt = tagCompound.getInteger("cnt")
        this.cntCpy = tagCompound.getInteger("cnt")
    }

    override fun writeToNBT(tagCompound: NBTTagCompound): NBTTagCompound {
        print("wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww\n")
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

open class StoveCommonProxy() {
    open fun getClientWorld(): World? {
        return null
    }

    open fun registerTileEntity(tileEntity: TileEntity) {
        GameRegistry.registerTileEntity(tileEntity::class.java,
                ResourceLocation(BrewCraft.MOD_ID + itemID + "_tile"))
    }
}

class StoveClientProxy: StoveCommonProxy() {
    override fun getClientWorld(): World? {
        return FMLClientHandler.instance().worldClient
    }


    override fun registerTileEntity(tileEntity: TileEntity) {
        GameRegistry.registerTileEntity(tileEntity::class.java,
                ResourceLocation(BrewCraft.MOD_ID + itemID + "_tile"))
    }
}
