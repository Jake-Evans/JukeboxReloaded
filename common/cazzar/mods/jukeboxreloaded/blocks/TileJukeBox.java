package cazzar.mods.jukeboxreloaded.blocks;

import java.util.Random;

import net.minecraft.client.audio.SoundManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import cazzar.mods.jukeboxreloaded.lib.InventoryUtils;
import cazzar.mods.jukeboxreloaded.network.packets.PacketJukeboxDescription;
import cazzar.mods.jukeboxreloaded.network.packets.PacketShuffleDisk;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileJukeBox extends TileEntity implements IInventory {
	int					metadata;
	public ItemStack[]	items;
	int					recordNumber		= 0;
	boolean				playingRecord		= false;
	String				lastPlayingRecord	= "";
	boolean				repeat				= true;
	boolean				repeatAll			= false;
	boolean				shuffle				= false;
	int					tick				= 0;
	private short		facing;
	
	public TileJukeBox() {
		items = new ItemStack[getSizeInventory()];
	}
	
	public TileJukeBox(int metadata) {
		this.metadata = metadata;
		items = new ItemStack[getSizeInventory()];
	}
	
	@Override
	public void closeChest() {}
	
	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		items[slot].stackSize -= amount;
		return items[slot];
	}
	
	public int getCurrentRecordNumer() {
		return recordNumber;
	}
	
	@Override
	public Packet getDescriptionPacket() {
		// PacketCustom pkt = new PacketCustom(CHANNEL_NAME,
		// Packets.CLIENT_TILEJUKEBOX_DATA);
		// pkt.writeCoord(getCoord());
		// pkt.writeBoolean(isPlayingRecord());
		// pkt.writeInt(getCurrentRecordNumer());
		// pkt.writeInt(getReplayMode());
		// pkt.writeBoolean(shuffleEnabled());
		// return pkt.toPacket();
		return (new PacketJukeboxDescription(this)).makePacket();
	}
	
	public short getFacing() {
		return facing;
	}
	
	@Override
	public int getInventoryStackLimit() {
		return 1;
	}
	
	@Override
	public String getInvName() {
		return "JukeBox";
	}
	
	public String getLastPlayedRecord() {
		return lastPlayingRecord;
	}
	
	public int getLastSlotWithItem() {
		int i = 0;
		for (final ItemStack itemStack : items) {
			if (itemStack == null) break;
			i++;
		}
		return i - 1;
	}
	
	/**
	 * @return 0: none <br/>
	 *         1: all <br/>
	 *         2: one
	 */
	public int getReplayMode() {
		if (repeat) return 2;
		else if (repeatAll) return 1;
		else return 0;
	}
	
	@Override
	public int getSizeInventory() {
		return 12;
	}
	
	@Override
	public ItemStack getStackInSlot(int slot) {
		return items[slot];
	}
	
	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		final ItemStack stack = getStackInSlot(slot);
		if (stack != null) setInventorySlotContents(slot, null);
		return stack;
	}
	
	@Override
	public boolean isInvNameLocalized() {
		return false;
	}
	
	public boolean isPlayingRecord() {
		return playingRecord;
	}
	
	@Override
	public boolean isStackValidForSlot(int i, ItemStack itemstack) {
		return (itemstack.getItem() instanceof ItemRecord) || itemstack == null;
	}
	
	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return true;
	}
	
	public void markForUpdate() {
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}
	
	public void nextRecord() {
		if (recordNumber++ >= getSizeInventory() - 1) recordNumber = 0;
	}
	
	@Override
	public void openChest() {}
	
	public void playSelectedRecord() {
		for (int i = recordNumber; i < getSizeInventory(); i++)
			if (getStackInSlot(i) != null) {
				if (!(getStackInSlot(recordNumber).getItem() instanceof ItemRecord))
					return; // no
							// I
							// will
							// not
							// play.
				recordNumber = i;
				break;
			}
		if (getStackInSlot(recordNumber) == null) return;
		
		// worldObj.playAuxSFXAtEntity((EntityPlayer) null, 1005, xCoord,
		// yCoord,
		// zCoord, getStackInSlot(recordNumber).itemID);
		worldObj.playRecord(((ItemRecord) getStackInSlot(recordNumber)
				.getItem()).recordName, xCoord, yCoord, zCoord);
		playingRecord = true;
		lastPlayingRecord = ((ItemRecord) getStackInSlot(recordNumber)
				.getItem()).recordName;
	}
	
	public void previousRecord() {
		if (recordNumber == 0) recordNumber = getSizeInventory() - 1;
		else recordNumber--;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		recordNumber = tag.getInteger("recordNumber");
		facing = tag.getShort("facing");
		shuffle = tag.getBoolean("shuffle");
		setRepeatMode(tag.getInteger("rptMode"));
		
		InventoryUtils
				.readItemStacksFromTag(items, tag.getTagList("inventory"));
	}
	
	public void resetPlayingRecord() {
		recordNumber = 0;
	}
	
	public void setFacing(short direction) {
		facing = direction;
	}
	
	@Override
	public void setInventorySlotContents(int slot, ItemStack itemstack) {
		items[slot] = itemstack;
		// if ( itemstack != null
		// && itemstack.stackSize > getInventoryStackLimit() )
		// {
		// itemstack.stackSize = getInventoryStackLimit();
		// }
		
		if (recordNumber == slot && playingRecord && itemstack == null)
			playSelectedRecord();
	}
	
	public void setPlaying(boolean playing) {
		if (!isPlayingRecord() && playing) playSelectedRecord();
		else if (isPlayingRecord() && !playing) stopPlayingRecord();
	}
	
	public void setRecordPlaying(int recordNumber) {
		final int oldRecord = this.recordNumber;
		this.recordNumber = recordNumber;
		if (playingRecord && oldRecord != recordNumber) playSelectedRecord();
	}
	
	/**
	 * @param mode
	 *            0: none <br/>
	 *            1: all <br/>
	 *            2: one
	 */
	public void setRepeatMode(int mode) {
		switch (mode) {
			case 0:
				repeat = repeatAll = false;
				break;
			case 1:
				repeatAll = true;
				repeat = false;
				break;
			case 2:
				repeatAll = false;
				repeat = true;
				break;
			default:
				repeat = repeatAll = false;
				break;
		}
	}
	
	public void setShuffle(boolean shuffle) {
		this.shuffle = shuffle;
	}
	
	public boolean shuffleEnabled() {
		return shuffle;
	}
	
	public void stopPlayingRecord() {
		playingRecord = false;
		
		worldObj.playAuxSFXAtEntity((EntityPlayer) null, 1005, xCoord, yCoord,
				zCoord, 0);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void updateEntity() {
		tick++;
		final Random random = new Random();
		
		if (tick % 5 == 0 && random.nextBoolean())
			if (playingRecord)
				worldObj.spawnParticle("note", xCoord + random.nextDouble(),
						yCoord + 1.2D, zCoord + random.nextDouble(),
						random.nextDouble(), random.nextDouble(),
						random.nextDouble());
		// worldObj.spawnParticle("note", xCoord + random.nextDouble(),
		// yCoord +1, zCoord + random.nextDouble(), 0, 100, 0);
		
		if (tick % 10 != 0) return;
		
		if (SoundManager.sndSystem == null) return; // Thanks to alex
		// streaming is only used on the client for playing in the jukebox..
		final boolean playing = SoundManager.sndSystem.playing("streaming");
		if (!playing) {
			final boolean wasPlaying = isPlayingRecord();
			if (!wasPlaying) return;
			// if repeating
			if (repeat) playSelectedRecord();
			else if (repeatAll) {
				nextRecord();
				if (recordNumber == getLastSlotWithItem() + 1)
					resetPlayingRecord();
				playSelectedRecord();
			}
			else {
				stopPlayingRecord();
				resetPlayingRecord();
			}
			// send tile information to the server to update the other clients
			if (shuffle && !repeat)
				PacketDispatcher
						.sendPacketToServer((new PacketShuffleDisk(this))
								.makePacket());
			// PacketCustom packet = new PacketCustom(CHANNEL_NAME,
			// Packets.SERVER_NEXT_SHUFFLEDDISK);
			// packet.writeCoord(getCoord());
			// packet.writeString(lastPlayingRecord);
			// packet.writeBoolean(shuffle);
			// packet.sendToServer();
			// final PacketCustom packet = new PacketCustom(
			// CHANNEL_NAME, Packets.CLIENT_UPDATE_TILEJUKEBOX);
			// packet.writeCoord(this.getCoord());
			// packet.writeBoolean(this.isPlayingRecord());
			// packet.writeInt(this.getCurrentRecordNumer());
			// packet.sendToServer();
			PacketDispatcher.sendPacketToServer((new PacketJukeboxDescription(
					this)).makePacket());
		}
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("recordNumber", recordNumber);
		tag.setShort("facing", facing);
		tag.setInteger("rptMode", getReplayMode());
		tag.setBoolean("shuffle", shuffle);
		tag.setTag("inventory", InventoryUtils.writeItemStacksToTag(items));
	}
}
