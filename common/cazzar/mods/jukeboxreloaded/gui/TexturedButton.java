package cazzar.mods.jukeboxreloaded.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class TexturedButton extends GuiButton {
	
	private final String	textureFile;
	private final int		xOffset, yOffset, yOffsetForDisabled,
			xOffsetForDisabled, xOffsetForHovered, yOffsetForHovered;
	
	public TexturedButton(int id, int xPosition, int yPosition, int width,
			int height, String textureFile, int xOffset, int yOffset,
			int xOffsetForDisabled, int yOffsetForDisabled,
			int xOffsetForHovered, int yOffsetForHovered) {
		
		super(id, xPosition, yPosition, width, height, "");
		
		this.textureFile = textureFile;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.xOffsetForDisabled = xOffsetForDisabled;
		this.yOffsetForDisabled = yOffsetForDisabled;
		this.xOffsetForHovered = xOffsetForHovered;
		this.yOffsetForHovered = yOffsetForHovered;
	}
	
	@Override
	public void drawButton(Minecraft mc, int par2, int par3) {
		if (drawButton) {
			//mc.renderEngine.bindTexture(textureFile);
			mc.renderEngine.func_110577_a(new ResourceLocation(textureFile));
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			field_82253_i = par2 >= xPosition && par3 >= yPosition
					&& par2 < xPosition + width && par3 < yPosition + height;
			
			// int hoverStatus = this.getHoverState(this.field_82253_i);
			// Args: x, y, xOffset, yOffset, Width, Height
			
			switch (getHoverState(field_82253_i)) {
				case 0:
					// Disabled
					drawTexturedModalRect(xPosition, yPosition,
							xOffsetForDisabled, yOffsetForDisabled, width,
							height);
					break;
				case 1:
					// not hovering
					drawTexturedModalRect(xPosition, yPosition, xOffset,
							yOffset, width, height);
					break;
				case 2:
					// hovering
					drawTexturedModalRect(xPosition, yPosition,
							xOffsetForHovered, yOffsetForHovered, width, height);
					break;
			}
			
			// this.drawTexturedModalRect(this.xPosition, this.yPosition,
			// this.xOffset, this.yOffset, this.width, this.height);
		}
	}
}
