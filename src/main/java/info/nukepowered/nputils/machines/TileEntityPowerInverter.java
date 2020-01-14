package info.nukepowered.nputils.machines;

import java.lang.ref.WeakReference;
import java.util.List;
import javax.annotation.Nullable;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;

import codechicken.lib.raytracer.CuboidRayTraceResult;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.ColourMultiplier;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.GTValues;
import gregtech.api.block.machines.BlockMachine;
import gregtech.api.capability.GregtechCapabilities;
import gregtech.api.capability.impl.EnergyContainerHandler;
import gregtech.api.capability.tool.ISoftHammerItem;
import gregtech.api.gui.ModularUI;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.render.SimpleSidedCubeRenderer;
import gregtech.api.render.Textures;
import gregtech.api.util.GTUtility;
import gregtech.api.util.PipelineUtil;
import gregtech.common.tools.DamageValues;
import info.nukepowered.nputils.NPUTextures;
import info.nukepowered.nputils.api.NPULib;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityPowerInverter extends MetaTileEntity {
	
	private EnergyContainerHandler energyContainer;
	private WeakReference<TileEntitySolarPanel> panel = new WeakReference<>(null);
	private boolean panelMode = true;
	private boolean isPanelCheckedOnce = false;
	private int tier = 0;

	
	public TileEntityPowerInverter(ResourceLocation metaTileEntityId, int tier) {
		super(metaTileEntityId);
		this.tier = tier;
		reinitializeEnergyContainer();
	}
	
	@Override
	public void update() {
		super.update();
		if (!this.getWorld().isRemote) {
			if (!isPanelCheckedOnce) updatePanel();
			if (panelMode && panel != null) {
				if (this.getPanel().getType().getVoltage() <= energyContainer.getInputVoltage()) {
					long packet = energyContainer.getInputVoltage() * energyContainer.getInputAmperage();
					if (this.getPanel().getPowerFromStack(packet)) {
						energyContainer.addEnergy(packet);
					}
				} else {
					GTUtility.doOvervoltageExplosion(this, this.getPanel().getType().getVoltage());
				}
			}
		}
	}
	
	@Override
	public void addDebugInfo(List<String> list) {
		list.add("PanelMode: " + this.panelMode);
		list.add("Panel: " + (this.panel == null ? "§cnull" : NPULib.posToStringC(this.getPanel().getPos())));
    }
	
	@Override
	public void updateInputRedstoneSignals() {
		if (this.panelMode) this.updatePanel();
	}
	
	private void updatePanel() {
		this.isPanelCheckedOnce = true;
		BlockPos panelPos = getPos().offset(getFrontFacing().getOpposite());
		MetaTileEntity mte = BlockMachine.getMetaTileEntity(getWorld(), panelPos);
		if (mte != null) {
			if (mte instanceof TileEntitySolarPanel) {
				this.panel = new WeakReference<>((TileEntitySolarPanel) mte);
				return;
			} 
		}
		this.panel = null;
	}
	
	@Nullable
	private TileEntitySolarPanel getPanel() {
		TileEntitySolarPanel panel = this.panel.get();
		if (panel != null) {
			if (panel.isValid()) {
				return panel;
			}
		}
		
		this.panel.clear();
		return null;
	}
	
	private void reinitializeEnergyContainer() {
		this.energyContainer = new EnergyContainerHandler(this, GTValues.V[tier] * 32L, GTValues.V[tier - 1], 16L, GTValues.V[tier], 4L);
		this.energyContainer.setSideInputCondition(face -> face == getFrontFacing().getOpposite() && !this.panelMode);
		this.energyContainer.setSideOutputCondition(face -> face == getFrontFacing());
	}
	
	@Override
	public boolean onRightClick(EntityPlayer playerIn, EnumHand hand, EnumFacing facing, CuboidRayTraceResult hitResult) {
		ItemStack heldItem = playerIn.getHeldItem(hand);
		if (!heldItem.isEmpty() && heldItem.hasCapability(GregtechCapabilities.CAPABILITY_MALLET, null)) {
			ISoftHammerItem shi = heldItem.getCapability(GregtechCapabilities.CAPABILITY_MALLET, null);
			if (getWorld().isRemote) return true;
			if (!shi.damageItem(DamageValues.DAMAGE_FOR_SOFT_HAMMER, false)) return false;
			if (this.panelMode)
				this.panelMode = false;
			else
				this.panelMode = true;
			playerIn.sendMessage(new TextComponentTranslation("nputils.machine.power_inverter.message_panelmode", this.panelMode));
			writeCustomData(1, buf -> buf.writeBoolean(this.panelMode));
		}
		return false;
	}
	
	@Override
	public void receiveCustomData(int dataId, PacketBuffer buf) {
		super.receiveCustomData(dataId, buf);
		if (dataId == 1) {
			this.panelMode = buf.readBoolean();
			getHolder().scheduleChunkForRenderUpdate();
		}
	}
	
	@Override
	public void writeInitialSyncData(PacketBuffer buf) {
		super.writeInitialSyncData(buf);
		buf.writeBoolean(panelMode);
	}
	
	@Override
	public void receiveInitialSyncData(PacketBuffer buf) {
		super.receiveInitialSyncData(buf);
		this.panelMode = buf.readBoolean();
		if (this.panelMode) updatePanel();
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound data) {
		super.writeToNBT(data);
		data.setBoolean("PanelMode", this.panelMode);
		return data;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);
		this.panelMode = data.getBoolean("PanelMode");
		reinitializeEnergyContainer();
//		if (this.panelMode) updatePanel(); // NullPointerException from gregtech.api.block.machines.BlockMachine.getMetaTileEntity
	}
	
	private SimpleSidedCubeRenderer getBaseRenderer() {
		return Textures.VOLTAGE_CASINGS[tier];
	}
	
	@Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
		IVertexOperation[] colouredPipeline = ArrayUtils.add(pipeline, new ColourMultiplier(GTUtility.convertRGBtoOpaqueRGBA_CL(getPaintingColorForRendering())));
		getBaseRenderer().render(renderState, translation, colouredPipeline);
        Textures.ENERGY_OUT_MULTI.renderSided(getFrontFacing(), renderState, translation, PipelineUtil.color(pipeline, GTValues.VC[tier]));
        if (!this.panelMode) {
        	NPUTextures.ENERGY_IN_MULTI_16.renderSided(getFrontFacing().getOpposite(), renderState, translation, PipelineUtil.color(pipeline, GTValues.VC[tier - 1]));
        } else {
        	NPUTextures.ENERGY_IN_PANELMODE.renderSided(getFrontFacing().getOpposite(), renderState, translation, PipelineUtil.color(pipeline, GTValues.VC[tier - 1]));
        }
	}

	@Override
	public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder holder) {
		return new TileEntityPowerInverter(metaTileEntityId, tier);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public Pair<TextureAtlasSprite, Integer> getParticleTexture() {
		return Pair.of(getBaseRenderer().getParticleSprite(), 0xFFFFFF);
	}
	
	@Override
    protected boolean openGUIOnRightClick() {
        return false;
    }
	
	@Override
	protected ModularUI createUI(EntityPlayer entityPlayer) {
		return null;
	}
	
	@Override
	public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, boolean advanced) {
		long inputV = energyContainer.getInputVoltage();
		long outputV = energyContainer.getOutputVoltage();
		long inputA = energyContainer.getInputAmperage();
		long outputA = energyContainer.getOutputAmperage();
		tooltip.add(I18n.format("nputils.machine.power_inverter.description"));
		tooltip.add(I18n.format("nputils.machine.power_inverter." + GTValues.VN[tier].toLowerCase() + ".description"));
		tooltip.add(I18n.format("gregtech.universal.tooltip.voltage_in", NPULib.format(inputV), GTValues.VN[tier - 1]));
		tooltip.add(I18n.format("gregtech.universal.tooltip.voltage_out", NPULib.format(outputV), GTValues.VN[tier]));
		tooltip.add(I18n.format("gregtech.universal.tooltip.amperage_in", NPULib.format(inputA)));
		tooltip.add(I18n.format("gregtech.universal.tooltip.amperage_out", NPULib.format(outputA)));
	}
	
}
