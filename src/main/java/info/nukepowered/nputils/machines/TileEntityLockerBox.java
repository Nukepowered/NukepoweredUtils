package info.nukepowered.nputils.machines;

import java.util.List;
import codechicken.lib.raytracer.IndexedCuboid6;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Matrix4;
import gregtech.api.gui.ModularUI;
import gregtech.api.metatileentity.IFastRenderMetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityLockerBox extends MetaTileEntity implements IFastRenderMetaTileEntity {
	
	private static IndexedCuboid6 COLLISION = new IndexedCuboid6(null, new Cuboid6(0, 0, 0, 1, 0.5, 1));
	
	
	public TileEntityLockerBox(ResourceLocation metaTileEntityId) {
		super(metaTileEntityId);
	}

	@Override
	public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder holder) {
		return new TileEntityLockerBox(metaTileEntityId);
	}

	@Override
	protected ModularUI createUI(EntityPlayer player) {
//		player.sendStatusMessage(new TextComponentString("123"), true); // TODO USE THIS!
		// TODO
		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderMetaTileEntityFast(CCRenderState renderState, Matrix4 translation, float partialTicks) {
		// TODO Auto-generated method stub
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {}
	
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		// TODO
		return new AxisAlignedBB(0, 0, 0, 1, 0.1 ,1);
	}
	
	@Override
    public void addCollisionBoundingBox(List<IndexedCuboid6> collisionList) {
        collisionList.add(COLLISION);
    }
	
	@Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public int getLightOpacity() {
        return 1;
    }
}
