package info.nukepowered.nputils.gui;

import java.util.function.Supplier;

import gregtech.api.gui.Widget;
import gregtech.api.util.Position;
import gregtech.api.util.Size;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CenterDynamicLabel extends Widget {

	 protected Supplier<String> textSupplier;
	    private String lastTextValue = "";
	    private int color;

	    public CenterDynamicLabel(int xPosition, int yPosition, Supplier<String> text) {
	        this(xPosition, yPosition, text, 0x404040);
	    }

	    public CenterDynamicLabel(int xPosition, int yPosition, Supplier<String> text, int color) {
	        super(new Position(xPosition, yPosition), Size.ZERO);
	        this.textSupplier = text;
	        this.color = color;
	    }

	    private void updateSize() {
	        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
	        String resultText = lastTextValue;
	        setSize(new Size(fontRenderer.getStringWidth(resultText), fontRenderer.FONT_HEIGHT));
	        if (uiAccess != null) {
	            uiAccess.notifySizeChange();
	        }
	    }

	    @Override
	    @SideOnly(Side.CLIENT)
	    public void drawInForeground(int mouseX, int mouseY) {
	        String suppliedText = textSupplier.get();
	        if (!suppliedText.equals(lastTextValue)) {
	            this.lastTextValue = suppliedText;
	            updateSize();
	        }
	        String[] split = textSupplier.get().split("\n");
	        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
	        Position position = getPosition();
	        for (int i = 0; i < split.length; i++) {
	            fontRenderer.drawString(split[i], position.x - (fontRenderer.getStringWidth(suppliedText) / 2), position.y + (i * (fontRenderer.FONT_HEIGHT + 2)), color);
	        }
	    }
	
}
