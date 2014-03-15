package net.cazzar.mods.jukeboxreloaded.blocks

import net.minecraft.block.{Block, BlockContainer}
import net.minecraft.block.material.Material
import net.minecraft.util.{MathHelper, IIcon}
import net.minecraft.world.{World, IBlockAccess}
import net.minecraftforge.common.util.ForgeDirection
import net.cazzar.mods.jukeboxreloaded.blocks.tileentity.TileJukebox
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.tileentity.TileEntity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.entity.player.EntityPlayer
import net.cazzar.mods.jukeboxreloaded.JukeboxReloaded
import net.cazzar.mods.jukeboxreloaded.common.gui.GuiHandler
import net.minecraft.creativetab.CreativeTabs

class BlockJukebox extends BlockContainer(Material.rock) {
    var iconBuffer: Array[IIcon] = new Array[IIcon](4)

    this.setBlockName("Jukebox")

    override def createNewTileEntity(world: World, meta: Int): TileEntity = new TileJukebox(meta)

    override def getIcon(world: IBlockAccess, x: Int, y: Int, z: Int, side: Int): IIcon = {
        if (side == ForgeDirection.UP.ordinal) return iconBuffer(2)
        if (side == ForgeDirection.DOWN.ordinal) return iconBuffer(0)
        val te: TileJukebox = world.getTileEntity(x, y, z).asInstanceOf[TileJukebox]

        var front, left, right: ForgeDirection = ForgeDirection.UNKNOWN
        front = ForgeDirection.getOrientation(te.facing)

        front.ordinal() match {
            case 2 | 3 =>
                left = ForgeDirection getOrientation 4
                right = left.getOpposite
            case 4 | 5 =>
                left = ForgeDirection getOrientation 2
                right = left.getOpposite
        }

        if (side == left.ordinal() || side == right.ordinal()) iconBuffer(1)
        else if (side == front.ordinal()) iconBuffer(3)
        else iconBuffer(0)
    }

    override def getIcon(side: Int, meta: Int): IIcon = ForgeDirection.getOrientation(side) match {
        case ForgeDirection.UP => iconBuffer(2)
        case ForgeDirection.DOWN => iconBuffer(0)
        case _ => iconBuffer(1)
    }

    override def hasTileEntity(meta: Int): Boolean = true

    override def onBlockPlacedBy(world: World, x: Int, y: Int, z: Int, player: EntityLivingBase, stack: ItemStack) = {
        super.onBlockPlacedBy(world, x, y, z, player, stack)
        val heading: Int = MathHelper.floor_double(player.rotationYaw * 4F / 360F + 0.5D) & 3
        val tile: TileJukebox = world.getTileEntity(x, y, z).asInstanceOf[TileJukebox]

        heading match {
            case 0 => tile.facing = 2
            case 1 => tile.facing = 5
            case 2 => tile.facing = 3
            case 3 => tile.facing = 4
        }
    }

    override def registerBlockIcons(register: IIconRegister) = {
        iconBuffer(0) = register.registerIcon("cazzar:jukeboxbottom")
        iconBuffer(1) = register.registerIcon("cazzar:jukeboxside")
        iconBuffer(2) = register.registerIcon("cazzar:jukeboxtop")
        iconBuffer(3) = register.registerIcon("cazzar:jukeboxfront")
        //ParticleIcons.CROTCHET = iconRegister.registerIcon("cazzar:crotchet");
        //ParticleIcons.QUAVER = iconRegister.registerIcon("cazzar:quaver");
        //ParticleIcons.DOUBLE_QUAVER = iconRegister.registerIcon("cazzar:double-quaver");
    }

    override def onBlockActivated(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, par6: Int, playerX: Float, playery: Float, playerZ: Float): Boolean = {
        world.playRecord("13", x, y, z)
        if (!player.isSneaking && !world.isRemote) {
            val tile: TileJukebox = world.getTileEntity(x, y, z).asInstanceOf[TileJukebox]
            if (tile != null) {
                player.openGui(JukeboxReloaded, GuiHandler.JUKEBOX, world, x, y, z)
                true
            }
            else false
        }
        else !player.isSneaking
    }
}
