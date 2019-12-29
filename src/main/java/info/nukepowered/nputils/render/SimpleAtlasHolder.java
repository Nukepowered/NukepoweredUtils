package info.nukepowered.nputils.render;

import codechicken.lib.texture.TextureUtils.IIconRegister;
import gregtech.api.render.Textures;
import info.nukepowered.nputils.NPUtils;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SimpleAtlasHolder implements IIconRegister {
	
	private final String basePath;
	
	@SideOnly(Side.CLIENT)
	private TextureAtlasSprite sprite;
	
	public SimpleAtlasHolder(String path) {
		this.basePath = path;
		Textures.iconRegisters.add(this);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(TextureMap textureMap) {
		this.sprite = textureMap.registerSprite(new ResourceLocation(NPUtils.MODID, "blocks/" + basePath));
	}
	
	public TextureAtlasSprite get() {
		return this.sprite;
	}
}
