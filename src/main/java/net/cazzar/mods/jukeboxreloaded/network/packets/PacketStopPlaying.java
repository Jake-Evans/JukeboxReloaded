package net.cazzar.mods.jukeboxreloaded.network.packets;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import net.cazzar.corelib.lib.SoundSystemHelper;
import net.cazzar.mods.jukeboxreloaded.blocks.TileJukebox;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

/**
 * @author Cayde
 */
public class PacketStopPlaying extends PacketJukebox {

    public int x, y, z;

    public PacketStopPlaying() {
    }

    public PacketStopPlaying(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void execute(EntityPlayer player, Side side)
            throws ProtocolException {
        final TileEntity te = player.worldObj.getBlockTileEntity(x, y, z);
        if (te instanceof TileJukebox) {
            ((TileJukebox) te).setPlaying(false);
            ((TileJukebox) te).waitTicks = 20;
            ((TileJukebox) te).markForUpdate();
        }

        if (side.isServer()) {
            PacketDispatcher.sendPacketToAllPlayers(makePacket());
            return;
        }

        SoundSystemHelper.stop(x + ":" + y + ":" + z);
    }

    @Override
    public void read(ByteArrayDataInput in) {
        x = in.readInt();
        y = in.readInt();
        z = in.readInt();
    }

    @Override
    public void write(ByteArrayDataOutput out) {
        out.writeInt(x);
        out.writeInt(y);
        out.writeInt(z);
    }

}