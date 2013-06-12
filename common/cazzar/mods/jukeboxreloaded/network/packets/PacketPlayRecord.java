package cazzar.mods.jukeboxreloaded.network.packets;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import cazzar.mods.jukeboxreloaded.blocks.TileJukeBox;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;

public class PacketPlayRecord extends PacketJukebox {
	int		x, y, z;
	String	record;
	
	public PacketPlayRecord() {}
	
	public PacketPlayRecord(String record, int x, int y, int z) {
		this.record = record;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	@Override
	public void execute(EntityPlayer player, Side side)
			throws ProtocolException {
		if (side.isServer()) {
			PacketDispatcher.sendPacketToAllPlayers(makePacket());
			return;
		}
		
		final TileEntity te = player.worldObj.getBlockTileEntity(x, y, z);
		if (te instanceof TileJukeBox) {
			TileJukeBox t = ((TileJukeBox) te);
			t.getSoundSystem().playRecord(record, t.worldObj, x, y, z, 0.5F);
		}
	}
	
	@Override
	public void read(ByteArrayDataInput in) {
		x = in.readInt();
		y = in.readInt();
		z = in.readInt();
		record = in.readUTF();
	}
	
	@Override
	public void write(ByteArrayDataOutput out) {
		out.writeInt(x);
		out.writeInt(y);
		out.writeInt(z);
		out.writeUTF(record);
	}
}
