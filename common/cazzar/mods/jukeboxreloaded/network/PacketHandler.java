package cazzar.mods.jukeboxreloaded.network;

import java.util.logging.Level;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import cazzar.mods.jukeboxreloaded.lib.util.LogHelper;
import cazzar.mods.jukeboxreloaded.network.packets.PacketJukebox;
import cazzar.mods.jukeboxreloaded.network.packets.PacketJukebox.ProtocolException;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;

public class PacketHandler implements IPacketHandler {
	
	@Override
	public void onPacketData(INetworkManager manager,
			Packet250CustomPayload packet, Player player) {
		try {
			final EntityPlayer entityPlayer = (EntityPlayer) player;
			final ByteArrayDataInput in = ByteStreams.newDataInput(packet.data);
			final int packetId = in.readUnsignedByte(); // Assuming your
														// packetId is
			// between 0 (inclusive) and
			// 256 (exclusive). If you
			// need more you need to
			// change this
			final PacketJukebox demoPacket = PacketJukebox
					.constructPacket(packetId);
			demoPacket.read(in);
			demoPacket.execute(entityPlayer,
					entityPlayer.worldObj.isRemote ? Side.CLIENT : Side.SERVER);
		}
		catch (final ProtocolException e) {
			if (player instanceof EntityPlayerMP) {
				((EntityPlayerMP) player).playerNetServerHandler
						.kickPlayerFromServer("Protocol Exception!");
				LogHelper.log(Level.INFO, "Player "
						+ ((EntityPlayer) player).username
						+ " caused a Protocol Exception!");
			}
		}
		catch (final InstantiationException e) {
			throw new RuntimeException(
					"Unexpected Reflection exception during Packet construction!",
					e);
		}
		catch (final IllegalAccessException e) {
			throw new RuntimeException(
					"Unexpected Reflection exception during Packet construction!",
					e);
		}
	}
}
