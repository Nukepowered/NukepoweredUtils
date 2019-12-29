package info.nukepowered.nputils.render;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.texture.TextureUtils.IIconRegister;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Rotation;
import gregtech.api.render.Textures;
import info.nukepowered.nputils.NPUTextures;
import info.nukepowered.nputils.NPUtils;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SolarPanelRenderer implements IIconRegister {
	@SideOnly(Side.CLIENT)
	private TextureAtlasSprite panelTop;
	private final String basePath;
	
	public SolarPanelRenderer(String path) {
		this.basePath = path;
		Textures.iconRegisters.add(this);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(TextureMap textureMap) {
		this.panelTop = textureMap.registerSprite(new ResourceLocation(NPUtils.MODID, "blocks/" + basePath));
	}
	
	@SideOnly(Side.CLIENT)
	public void render(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline, byte connectionMask) {
		Cuboid6 size = new Cuboid6(0, 0, 0, 1, 0.125D, 1);
		boolean[] mask = getBorderedSides(connectionMask);
		Matrix4 mat = translation.copy();
		Textures.renderFace(renderState, translation, null, EnumFacing.UP, size, panelTop);
		for (int i = 0; i < 4; i++) {
			if (mask[i]) Textures.renderFace(renderState, mat, null, EnumFacing.UP, size, NPUTextures.SOLAR_PANEL_BORDER.get());
			mat.rotate(Math.toRadians(90), Rotation.axes[1]).translate(-1, 0, 0);
		}
		Textures.renderFace(renderState, translation, null, EnumFacing.DOWN, size, NPUTextures.SOLAR_PANEL_BOTTOM.get());
		for (EnumFacing facing : EnumFacing.HORIZONTALS) {
			Textures.renderFace(renderState, translation, null, facing, size, NPUTextures.SOLAR_PANEL_SIDES.get());
		}
		
//		CCModel model = CCModel.quadModel(24).generateBlock(0, size);
//		model.render(renderState, pipeline);
	}
	
	private static boolean[] getBorderedSides(int connectionMask) {
		boolean[] result = new boolean[4];
		EnumFacing[] faces = {EnumFacing.NORTH, EnumFacing.WEST, EnumFacing.SOUTH, EnumFacing.EAST};
		for (int i = 0; i < 4; i++) {
			if (!hasFaceBit(connectionMask, faces[i])) {
				result[i] = true;
			}
		}
		return result;
	}
	
	private static boolean hasFaceBit(int mask, EnumFacing face) {
		return (mask & 1 << (face.getIndex() - 2)) > 0;
	}
	
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite getParticleTexture() {
		return this.panelTop;
	}
}
