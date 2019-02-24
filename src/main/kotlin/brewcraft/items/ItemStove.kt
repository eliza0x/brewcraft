package brewcraft.items

import brewcraft.BrewCraft
import brewcraft.items.Stove.itemID
import brewcraft.items.utils.BlockContainerBase
import brewcraft.items.utils.ItemBlockBase
import net.minecraft.block.state.IBlockState
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
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
import net.minecraft.util.text.translation.I18n
import net.minecraftforge.fml.common.network.IGuiHandler
import net.minecraftforge.fml.common.network.NetworkRegistry
import org.lwjgl.opengl.GL11
import net.minecraft.util.text.translation.I18n.translateToLocal

object Stove {
    val GUI_ID = 1
    val itemID: String = "stove"

    @SidedProxy(clientSide = "brewcraft.items.StoveClientProxy", serverSide = "brewcraft.items.StoveCommonProxy")
    var proxy = StoveCommonProxy()

    val itemBlockStove = ItemBlockStove()
    fun register() {
        proxy.registerTileEntity()
        BlockStove.register()
        itemBlockStove.register()
        NetworkRegistry.INSTANCE.registerGuiHandler(BrewCraft.instance, StoveGuiHandler())
    }
}

object BlockStove: BlockContainerBase(
        itemID = itemID
) {
    override fun onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        playerIn.openGui(BrewCraft.instance, Stove.GUI_ID, worldIn, pos.x, pos.y, pos.z)
        return true
        // return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ)
    }

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

class ItemBlockStove: ItemBlockBase(
        itemID = Stove.itemID,
        block = BlockStove
)

class TileStove: TileEntity() {
    var cnt = 0
    var cntCpy = 0

    fun incrementCnt() {
        cnt = cnt + 1
    }

    override fun readFromNBT(tagCompound: NBTTagCompound?) {
        if (tagCompound != null) {
            super.readFromNBT(tagCompound)
            this.cnt = tagCompound.getInteger("cnt")
            this.cntCpy = tagCompound.getInteger("cnt")
        }
    }

    override fun writeToNBT(tagCompound: NBTTagCompound?): NBTTagCompound? {
        if (tagCompound != null) {
            super.writeToNBT(tagCompound)
            tagCompound.setInteger("cnt", this.cnt)
        }
        return tagCompound
    }

    override fun getUpdatePacket(): SPacketUpdateTileEntity? {
        val tagCompound = NBTTagCompound()
        //Write your data into the nbtTag
        this.writeToNBT(tagCompound)
        return SPacketUpdateTileEntity(getPos(), 1, tagCompound)
    }

    override fun onDataPacket(net: NetworkManager?, pkt: SPacketUpdateTileEntity?) {
        pkt?.let { readFromNBT(it.nbtCompound) }
    }
}

open class StoveCommonProxy {
    open fun getClientWorld(): World? {
        return null
    }

    open fun registerTileEntity() {
        GameRegistry.registerTileEntity(TileStove::class.java,
                ResourceLocation(BrewCraft.MOD_ID + itemID + "_tile"))
    }
}

class StoveClientProxy: StoveCommonProxy() {
    override fun getClientWorld(): World? {
        return FMLClientHandler.instance().worldClient
    }


    override fun registerTileEntity() {
        GameRegistry.registerTileEntity(TileStove::class.java,
                ResourceLocation(BrewCraft.MOD_ID + itemID + "_tile"))
    }
}

class StoveGuiHandler: IGuiHandler {
    override fun getServerGuiElement(ID: Int, player: EntityPlayer?, world: World?, x: Int, y: Int, z: Int): Any? {
        val tile: TileEntity? = world?.getTileEntity(BlockPos(x,y,z))
        if (tile is TileStove && player != null && tile is TileStove) {
            return ContainerStove(player, tile)
        }
        return null
    }

    override fun getClientGuiElement(ID: Int, player: EntityPlayer?, world: World?, x: Int, y: Int, z: Int): Any? {
        val tile: TileEntity? = world?.getTileEntity(BlockPos(x,y,z))
        if (ID == Stove.GUI_ID && player != null && tile is TileStove) {
            return StoveGuiContainer(player, tile)
        }
        return null
    }
}

class ContainerStove(
        val player: EntityPlayer,
        val tile: TileStove
): Container() {
    override fun canInteractWith(playerIn: EntityPlayer): Boolean {
        return true
    }
}

class StoveGuiContainer(
        player: EntityPlayer,
        val entity: TileStove
): GuiContainer(ContainerStove(player, entity)) {
    val GUI_TEXTURE = ResourceLocation(BrewCraft.MOD_ID + ":" + itemID, "textures/gui/stove_gui.png")
    // val tileEntity: TileStove = entity
    // val ysize = 222

    override fun drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY)
    }

    override fun drawGuiContainerBackgroundLayer(tick: Float, mouseX: Int, mouseY: Int) {
        this.mc.renderEngine.bindTexture(GUI_TEXTURE)
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, xSize, ySize)
    }
}
