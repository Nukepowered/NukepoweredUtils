package info.nukepowered.nputils.machines;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import com.google.common.collect.ImmutableList;
import codechicken.lib.raytracer.IndexedCuboid6;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.ColourMultiplier;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Matrix4;
import gregtech.api.GTValues;
import gregtech.api.block.machines.BlockMachine;
import gregtech.api.capability.GregtechCapabilities;
import gregtech.api.capability.IEnergyContainer;
import gregtech.api.capability.impl.EnergyContainerHandler;
import gregtech.api.capability.impl.EnergyContainerHandler.IEnergyChangeListener;
import gregtech.api.gui.ModularUI;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.util.ByteBufUtils;
import gregtech.api.util.GTUtility;
import info.nukepowered.nputils.NPUTextures;
import info.nukepowered.nputils.api.NPULib;
import info.nukepowered.nputils.render.SolarPanelRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntitySolarPanel extends MetaTileEntity implements IEnergyChangeListener {
	private final SolarPanelType TYPE;
	private static IndexedCuboid6 COLLISION_BOX = new IndexedCuboid6(null, new Cuboid6(0, 0, 0, 1, 0.125D, 1));
	
	private boolean addedToMultiblock = false;
	private boolean isRemoved = false;
	private byte connectionMask = 0;
	private boolean isDrainer = false;
	private boolean needResync = false;
	
	private EnergyContainerHandler energyContainer;
	private List<BlockPos> connectedPanels = new ArrayList<>();

	private BlockPos controllerPos = null;
	private WeakReference<TileEntitySolarPanel> controllerCache = new WeakReference<>(null);
	private PanelStatus panelStatus;
	
	
	public TileEntitySolarPanel(ResourceLocation metaTileEntityId, SolarPanelType type) {
		super(metaTileEntityId);
		this.TYPE = type;
		initEnergyContainer();
	}
	
	@Override
	public void update() {
		super.update();
		if (!this.getWorld().isRemote) {
			// Building multiblock
			if (!addedToMultiblock) {
				this.addedToMultiblock = true;
				rescanPanelsMultiblocks(Collections.emptySet());
			}
			if (panelStatus == PanelStatus.ATTACHED) {
                writeCustomData(1, buf -> buf.writeBlockPos(controllerPos));
                this.panelStatus = null;
                this.needResync = false;
            }
            if (panelStatus == PanelStatus.DETACHED) {
                writeCustomData(2, buf -> {});
                this.panelStatus = null;
            }
			if (isPanelController() && needResync) {
				writeCustomData(3, buf -> ByteBufUtils.writeRelativeBlockList(buf, getPos(), connectedPanels));
				this.needResync = false;
			}
			
			// Energy generatrion
			if (getActualOutput(false) > 0) {
	            TileEntitySolarPanel tesp;
				if (getControllerEntity() == null) {
					tesp = this;
				} else {
					tesp = getControllerEntity();
				}
				if (TYPE.getEUt() < 16) {
					if (getWorld().getWorldTime() % 20 == 0) tesp.energyContainer.addEnergy(getActualOutputL(true));
				} else {
					tesp.energyContainer.addEnergy(getActualOutputL(false));
				}
			}
			
			// Transport energy from controller to consumer
			if (!isPanelController()) updateDrainContainers();
			if (isDrainer) {
				TileEntitySolarPanel controller = getControllerEntity();
				if (controller != null) {
					long energyToDraw = getMaxPanelOutput();
					if (controller.energyContainer.getEnergyStored() < energyToDraw) energyToDraw = controller.energyContainer.getEnergyStored();
					if (energyContainer.getEnergyCanBeInserted() >= energyToDraw) {
						energyContainer.addEnergy(energyToDraw);
						controller.energyContainer.removeEnergy(energyToDraw);
					}
				}
			}
		}
		if (getWorld().isRemote) {
			byte newConMask = getConnectionMask(getPos());
			if (connectionMask != newConMask) {
				connectionMask = newConMask;
				getHolder().scheduleChunkForRenderUpdate();
			}
		}
	}
	
	@Override
	public void updateInputRedstoneSignals() {
		if (getControllerEntity() != null) {
			checkDrainers();
		}
	}
	
	private void updateDrainContainers() {
		if (isDrainer) {
			if (energyContainer.getEnergyCapacity() != getMaxPanelOutput() * 5L) {
				long energy = energyContainer.getEnergyStored();
				energyContainer = EnergyContainerHandler.emitterContainer(this, getMaxPanelOutput() * 5L, GTValues.V[TYPE.getTier()], getOutputAmperage());
				energyContainer.setSideOutputCondition(side -> side == EnumFacing.DOWN);
				energyContainer.setEnergyStored(energy);
			}
		} else {
			long energy = energyContainer.getEnergyStored();
			getControllerEntity().energyContainer.addEnergy(energy);
			energyContainer = EnergyContainerHandler.emitterContainer(this, 0L, GTValues.V[TYPE.getTier()], 0L);
			energyContainer.setSideOutputCondition(side -> side == EnumFacing.DOWN);
		}
	}
	
	private void checkDrainers() {
		if (!isPanelController()) {
			if (getControllerEntity() != null) {
				TileEntity te = getWorld().getTileEntity(getPos().down());
				if (te != null) {
					if (te.hasCapability(GregtechCapabilities.CAPABILITY_ENERGY_CONTAINER, EnumFacing.UP)) {
						IEnergyContainer capability = te.getCapability(GregtechCapabilities.CAPABILITY_ENERGY_CONTAINER, EnumFacing.UP);
						if (capability != null) {
							isDrainer = true;
						} else {
							isDrainer = false;
						}
					} else {
						isDrainer = false;
					}
				} else {
					isDrainer = false;
				}
			}
		} else {
			if (isDrainer) isDrainer = false;
		}
	}
	
	protected boolean getPowerFromStack(long energyAmount) {
		EnergyContainerHandler container;
		if (getControllerEntity() != null)
			container = getControllerEntity().energyContainer;
		else
			container = this.energyContainer;
		if (container.getEnergyStored() >= energyAmount) {
			container.removeEnergy(energyAmount);
			return true;
		}
		return false;
	}
	
	public long getActualOutputL(boolean forSecond) {
		return MathHelper.lfloor(getActualOutput(forSecond));
	}
	
	public double getActualOutput(boolean forSecond) {
		if (getWorld().canBlockSeeSky(getPos().up())) {
			int i = 0;
			double toGen = 0.0D;
			long currentTime = getWorld().getWorldTime() % 24000L;
			double rain = (getWorld().isRaining() && getWorld().isThundering()) ? 0.65D : 1.0D;
			do {
				long modifiedTime = currentTime + i;
				if (modifiedTime > 3600 && modifiedTime < 8400) {
					toGen += TYPE.getEUt() * 1.0D;
				} else if (modifiedTime <= 3600) {
					toGen += TYPE.getEUt() * (modifiedTime * 1.0D / 3600.0D);
				} else if (modifiedTime >= 8400 && modifiedTime < 12000) {
					toGen += TYPE.getEUt() * Math.abs(modifiedTime - 12000) / 3600.0D;
				}
				i++;
			} while (forSecond && i < 20);
			return toGen * rain;
		} else {
			return 0.0D;
		}
	}
	
	public long getMaxGeneration() {
		if (isPanelController()) {
			if (connectedPanels.size() < 1) return TYPE.getEUt();
				else return TYPE.getEUt() * getOutputAmperage();
		} else {
			TileEntitySolarPanel controller = getControllerEntity();
			if (controller == null) return 0L;
			return controller.getOutputAmperage() * TYPE.getEUt();
		}
	}
	
	public long getMaxPanelOutput() {
		if (isPanelController()) {
			if (connectedPanels.size() > 0) {
				return TYPE.getVoltage() * getOutputAmperage();
			} else {
				return TYPE.getVoltage();
			}
		} else {
			TileEntitySolarPanel controller = getControllerEntity();
			if (controller == null) {
				return 0;
			}
			return controller.getOutputAmperage() * TYPE.getVoltage();
		}
	}
	
	public long getOutputAmperage() {
		long amperage = 0L;
		TileEntitySolarPanel panel;
		if (isPanelController()) panel = this;
		else {
			panel = getControllerEntity();
			if (panel == null) return 0L;
		}
		amperage = TYPE.getOutputAmperage(panel.connectedPanels.size() + 1);
		return amperage;
	}
	
	@Override
    public void addDebugInfo(List<String> debugInfo) {
		debugInfo.add("Type: " + TYPE);
		debugInfo.add("ActualOut: " + String.format("%.4f", getActualOutput(false)) + " EU/t");
		debugInfo.add("OutputCurrent: " + this.energyContainer.getOutputAmperage() + "A");
		debugInfo.add("IsController: " + isPanelController());
		debugInfo.add("ControllerPos: " + NPULib.posToStringC(controllerPos));
		debugInfo.add("Drainer: " + isDrainer);
		debugInfo.add("CanSeeSky: " + getWorld().canBlockSeeSky(getPos().up()));
		debugInfo.add("WorldTime: " + getWorld().getWorldTime() % 24000L);
		debugInfo.add("MultiblockSize: " + (getControllerEntity() != null ? (getControllerEntity().connectedPanels.size() + 1) : null));
		if (!connectedPanels.isEmpty()) {
			List<String> connectedInfo = new ArrayList<>();
			Iterator<BlockPos> iter = connectedPanels.iterator();
			while (iter.hasNext()) {
				connectedInfo.add(NPULib.posToStringC(iter.next()));
			}
			debugInfo.add("ConnectedPanels: " + connectedInfo);
		}
	}
	
	@Override
	public void onAttached() {
		super.onAttached();
		checkDrainers();
		recomputePanelSize();
	}
	
	@Override
	public void onRemoval() {
		super.onRemoval();
		this.isRemoved = true;
		setPanelController(null);
		recomputeNearbyMultiblocks();
	}
	
	private Set<BlockPos> recomputeNearbyMultiblocks() {
		HashSet<BlockPos> handledSet = new HashSet<>();
		for (EnumFacing side : EnumFacing.HORIZONTALS) {
			TileEntitySolarPanel panel = getSolarPanelTile(getPos().offset(side));
			if (panel == null) continue;
			handledSet.addAll(panel.rescanPanelsMultiblocks(handledSet));
		}
		return handledSet;
	}

	protected void initEnergyContainer() {
		this.energyContainer = EnergyContainerHandler.emitterContainer(this, getMaxPanelOutput() * 20L , GTValues.V[TYPE.getTier()], 1L);
		energyContainer.setSideOutputCondition(side -> side == EnumFacing.DOWN);
	}
	
	@Override
	public int getActualComparatorValue() {
		// Comparing current efficiency of solar panel
		double f = this.TYPE.baseEUt == 0 ? 0.0D : (this.getActualOutput(false) / (this.TYPE.baseEUt * 1.0D));
		return MathHelper.floor(f * 14.0D) + (this.getActualOutput(false) > 0 ? 1 : 0);
	}
	
	@Override
	public void onEnergyChanged(IEnergyContainer container, boolean isInitialChange) {
	}
	
	private static Comparator<BlockPos> buildRootComparator() {
		//we compare on all axis to ensure stable order and independence from the tank who started
        //the tank multiblock update sequence
		return Comparator.comparing(BlockPos::getX).thenComparing(BlockPos::getZ).reversed();
	}

	private Set<BlockPos> rescanPanelsMultiblocks(Set<BlockPos> excludedBlocks) {
		Map<BlockPos, TileEntitySolarPanel> allPanels = findConnectedPanelBlocks(getPos());
		allPanels.keySet().removeAll(excludedBlocks);
		TreeSet<BlockPos> sortedPanels = new TreeSet<>(buildRootComparator());
		sortedPanels.addAll(allPanels.keySet());		
		while (!sortedPanels.isEmpty()) {
			BlockPos rootPanelPos = sortedPanels.pollLast();
			sortedPanels.remove(rootPanelPos);
			Set<BlockPos> structureBlocks = findStructureBlocks(rootPanelPos, allPanels);
			sortedPanels.removeAll(structureBlocks);
			buildPanelStructure(structureBlocks, allPanels);
		}
		return allPanels.keySet();
	}
	
	private Set<BlockPos> findStructureBlocks(BlockPos startPos, Map<BlockPos, TileEntitySolarPanel> allPanels) {
		return findAllConnectedBlocks(startPos, EnumFacing.HORIZONTALS, allPanels::get, this.TYPE.getStackAmount()).keySet();
	}
	
	private Map<BlockPos, TileEntitySolarPanel> findConnectedPanelBlocks(BlockPos startPos) {
		return findAllConnectedBlocks(startPos, EnumFacing.HORIZONTALS, this::getSolarPanelTile, (this.TYPE.getStackAmount() - 1));
	}
	
	private Map<BlockPos, TileEntitySolarPanel> findAllConnectedBlocks(BlockPos startPos, EnumFacing[] directions, Function<BlockPos, TileEntitySolarPanel> blockProvider, int maxAmount) {
		HashMap<BlockPos, TileEntitySolarPanel> observedSet = new HashMap<>();
		observedSet.put(startPos, blockProvider.apply(startPos));
		TileEntitySolarPanel firstNode = observedSet.get(startPos);
		MutableBlockPos currentPos = new MutableBlockPos(startPos);
		Stack<EnumFacing> moveStack = new Stack<>();
		int currentAmount = 0;
		main:
		while (true) {
			if (currentAmount >= maxAmount) break;
			for (EnumFacing facing : directions) {
				currentPos.move(facing);
				TileEntitySolarPanel tesp;
				if (!observedSet.containsKey(currentPos) && (tesp = blockProvider.apply(currentPos)) != null && (firstNode.getType() == tesp.getType())) {
					observedSet.put(tesp.getPos(), tesp);
					firstNode = tesp;
					moveStack.push(facing.getOpposite());
					currentAmount++;
					continue main;
				} else currentPos.move(facing.getOpposite());
			}
			if (!moveStack.isEmpty()) {
				currentPos.move(moveStack.pop());
				firstNode = observedSet.get(currentPos);
			} else break;
			
		}
		return observedSet;
	}
	
	private void buildPanelStructure(Set<BlockPos> structureBlocks, Map<BlockPos, TileEntitySolarPanel> allPanels) {
		// Index all connected blocks
		List<TileEntitySolarPanel> connectedPanels = structureBlocks.stream()
				.sorted(buildRootComparator())
				.map(allPanels::get)
				.collect(Collectors.toList());
		// First, attempt to search for an existing master block
		TileEntitySolarPanel firstController = connectedPanels.stream()
				.filter(TileEntitySolarPanel::isPanelController)
				.findFirst().orElse(null);
		if (firstController == null) {
			firstController = connectedPanels.get(connectedPanels.size() - 1);
			firstController.setPanelController(null);
		}
		// Add all new panels to the controller
		connectedPanels.remove(firstController);
		firstController.isDrainer = false;
		TileEntitySolarPanel finalFirstController = firstController;
		connectedPanels.forEach(panel -> {  // Set other panels controller pos and energyContainer
			panel.setPanelController(finalFirstController);
			long energy = panel.energyContainer.getEnergyStored();
			finalFirstController.energyContainer.addEnergy(energy);
			panel.energyContainer = EnergyContainerHandler.emitterContainer(panel, 0L, GTValues.V[TYPE.getTier()], 0L);
			panel.energyContainer.setSideOutputCondition(side -> side == EnumFacing.DOWN);
			panel.checkDrainers();
		});
	}
	
	private boolean isPanelController() {
		return controllerPos == null;
	}
	
	private void setPanelController(TileEntitySolarPanel controller) {
		TileEntitySolarPanel oldController = getControllerEntity();
		if (oldController == controller) return; // Do not pointlessly update controller
		if (oldController != null) oldController.removePanelFromMultiblock(this);
		if (controller != null) controller.addPanelToMultiblock(this);
			else setPanelControllerInternal(null);
		for (BlockPos panelPos : ImmutableList.copyOf(connectedPanels)) {
			TileEntitySolarPanel tesp = getSolarPanelTile(panelPos);
			if (tesp == null) continue;
			removePanelFromMultiblock(tesp);
		}
		this.connectedPanels.clear();
	}
	
	private void setPanelControllerInternal(TileEntitySolarPanel controller) {
		this.controllerPos = controller == null ? null : controller.getPos();
		this.controllerCache = new WeakReference<>(controller);
		getHolder().markAsDirty();
		if (controller == null) this.panelStatus = PanelStatus.DETACHED;
			else this.panelStatus = PanelStatus.ATTACHED;
	}
	
	private void removePanelFromMultiblock(TileEntitySolarPanel removedPanel) {
		this.connectedPanels.remove(removedPanel.getPos());
		this.needResync = true;
		removedPanel.setPanelControllerInternal(null);
		recomputePanelSize();
	}
	
	private void addPanelToMultiblock(TileEntitySolarPanel addedPanel) {
		this.connectedPanels.add(addedPanel.getPos());
		this.needResync = true;
		addedPanel.setPanelControllerInternal(this);
		recomputePanelSize();
	}
	
	@Nullable
	private TileEntitySolarPanel getControllerEntity() {
		if (controllerPos == null) {
			return null;
		}
		TileEntitySolarPanel cachedController = controllerCache.get();
		if (cachedController != null) {
			if (cachedController.isValid()) {
				return cachedController;
			} else {
				controllerCache.clear();
			}
		}
		MetaTileEntity mte = BlockMachine.getMetaTileEntity(getWorld(), controllerPos);
		if (mte instanceof TileEntitySolarPanel) {
			this.controllerCache = new WeakReference<>((TileEntitySolarPanel) mte);
			return (TileEntitySolarPanel) mte;
		}
		return null;
	}
	
	private void recomputePanelSize() {
		List<BlockPos> connectedPanels = new ArrayList<>(this.connectedPanels);
		connectedPanels.add(getPos());
//		if (energyContainer.getOutputAmperage() != getOutputAmperage() && isPanelController()) {
		if (energyContainer.getEnergyCapacity() != (getMaxPanelOutput() * 20L) && isPanelController()) {
//			checkDrainers();
			long energyStored = energyContainer.getEnergyStored();
			energyContainer = EnergyContainerHandler.emitterContainer(this, getMaxPanelOutput() * 20L, GTValues.V[TYPE.getTier()], getOutputAmperage());
			energyContainer.setSideOutputCondition(side -> side == EnumFacing.DOWN);
			energyContainer.addEnergy(energyStored);
		} 
	}
	
	@Nullable
	private TileEntitySolarPanel getSolarPanelTile(BlockPos pos) {
		MetaTileEntity mte = BlockMachine.getMetaTileEntity(getWorld(), pos);
		if (!(mte instanceof TileEntitySolarPanel)) {
			return null;
		}
		TileEntitySolarPanel tesp = (TileEntitySolarPanel) mte;
		if (tesp.isRemoved || tesp.TYPE != TYPE) return null;
		return tesp;
	}
	
	private byte getConnectionMask(BlockPos pos) {
		if (!isPanelController()) {
			TileEntitySolarPanel controller = getControllerEntity();
			return controller == null ? 0 : controller.getConnectionMask(pos);
		}
		byte resultMask = 0;
		for (EnumFacing facing : EnumFacing.HORIZONTALS) {
			BlockPos offsetPos = pos.offset(facing);
			if (!connectedPanels.contains(offsetPos) && !getPos().equals(offsetPos)) continue;
			resultMask |= 1 << (facing.getIndex() - 2);
		}
		return resultMask;
	}
	
	public SolarPanelType getType() {
		return this.TYPE;
	}
	
	@Override
	public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
		IVertexOperation[] colouredPipeline = ArrayUtils.add(pipeline, new ColourMultiplier(GTUtility.convertRGBtoOpaqueRGBA_CL(getPaintingColorForRendering())));
		TYPE.getRendererHolder().get().render(renderState, translation, colouredPipeline, connectionMask);
	}
	
	@Override
	public void addCollisionBoundingBox(List<IndexedCuboid6> collissionList) {
		collissionList.add(COLLISION_BOX);
	}
	
	@Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public int getLightOpacity() {
        return 1;
    }
	
    @Override
    protected boolean openGUIOnRightClick() {
        return false;
    }
    
    @Override
    public boolean canPlaceCoverOnSide(EnumFacing side) {
    	return false;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public Pair<TextureAtlasSprite, Integer> getParticleTexture() {
    	return Pair.of(TYPE.getRendererHolder().get().getParticleTexture(), 0xFFFFFF);
    }
    
	@Override
	public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder holder) {
		return new TileEntitySolarPanel(metaTileEntityId, TYPE);
	}

	@Override
	protected ModularUI createUI(EntityPlayer entityPlayer) {
		return null;
	}
	
	@Override
	public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, boolean advanced) {
		super.addInformation(stack, player, tooltip, advanced);
//		tooltip.add(I18n.format("nputils.work_in_progress"));
		tooltip.add(I18n.format(String.format("nputils.machine.solar_panel.%s.description", this.TYPE.toString().toLowerCase())));
		tooltip.add(I18n.format("nputils.machine.solar_panel.multiblock"));
		tooltip.add(I18n.format("nputils.machine.solar_panel.maxstack", NPULib.format(this.TYPE.getStackAmount())));
		tooltip.add(I18n.format("nputils.machine.solar_panel.generation", NPULib.format(this.TYPE.getEUt())));
		tooltip.add(I18n.format("gregtech.universal.tooltip.voltage_out", NPULib.format(this.TYPE.getVoltage()), GTValues.VN[this.TYPE.getTier()]));
		tooltip.add(I18n.format("gregtech.universal.tooltip.amperage_out_till", NPULib.format(this.TYPE.getOutputAmperage(this.TYPE.getStackAmount()))));
	}
	
	@Override
	public boolean hasFrontFacing() {
		return false;
	}
	
	@Override
	public void writeInitialSyncData(PacketBuffer buf) {
		super.writeInitialSyncData(buf);
		if (isPanelController()) {
			buf.writeBoolean(true);
			ByteBufUtils.writeRelativeBlockList(buf, getPos(), connectedPanels);
			recomputePanelSize();
			long energy = this.energyContainer.getEnergyStored();
			buf.writeLong(energy);
		} else {
			buf.writeBoolean(false);
			buf.writeBoolean(isDrainer);
			buf.writeBlockPos(controllerPos);
		}
	}
	
	@Override
	public void receiveInitialSyncData(PacketBuffer buf) {
		super.receiveInitialSyncData(buf);
		if (buf.readBoolean()) {
			this.connectedPanels = ByteBufUtils.readRelativeBlockList(buf, getPos());
			recomputePanelSize();
			this.energyContainer.setEnergyStored(buf.readLong());
		} else {
			this.isDrainer = buf.readBoolean();
			this.controllerPos = buf.readBlockPos();
			checkDrainers();
		}
	}
	
	@Override
	public void receiveCustomData(int dataId, PacketBuffer buf) {
		super.receiveCustomData(dataId, buf);
		if (dataId == 1) {
			this.controllerPos = buf.readBlockPos();
			this.controllerCache = new WeakReference<>(null);
		} else if (dataId == 2) {
			this.controllerPos = null;
			this.controllerCache = new WeakReference<>(null);
			this.connectedPanels.clear();
		} else if (dataId == 3) {
			if (controllerPos == null) {
				this.connectedPanels = ByteBufUtils.readRelativeBlockList(buf, getPos());
				recomputePanelSize();
			}
		}
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound data) {
		super.writeToNBT(data);
		if (controllerPos != null) {
			data.setTag("ControllerPos", NBTUtil.createPosTag(controllerPos));
			data.setBoolean("Drainer", isDrainer);
		} else {
			NBTTagList connectedPanels = new NBTTagList();
			this.connectedPanels.forEach(pos -> connectedPanels.appendTag(NBTUtil.createPosTag(pos)));
			data.setTag("ConnectedPanels", connectedPanels);
		}
		return data;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);
		if (data.hasKey("ControllerPos")) {
			this.controllerPos = NBTUtil.getPosFromTag(data.getCompoundTag("ControllerPos"));
			this.isDrainer = data.getBoolean("Drainer");
		} else {
			NBTTagList connectedPanels = data.getTagList("ConnectedPanels", NBT.TAG_COMPOUND);
			connectedPanels.forEach(pos -> this.connectedPanels.add(NBTUtil.getPosFromTag((NBTTagCompound) pos)));
		}
	}
	
	@Override
	public float getBlockResistance() {
		return 1.5f;
	}
	
	@Override
	public float getBlockHardness() {
		return 1.5f;
	}
	
	public static enum SolarPanelType {
		// ���������� �������� � enum �������� ����� ��������� ����� ������
		BASIC(NPUTextures.SOLAR_PANEL_BASIC, GTValues.ULV, 2, 64),
		POLYCRYSTALLINE(NPUTextures.SOLAR_PANEL_POLYCRYSTALLNE, GTValues.LV, 16, 32),
		MONOCRYSTALLINE(NPUTextures.SOLAR_PANEL_MONOCRYSTALLINE, GTValues.MV, 64, 32);

		private NPULib.Holder<? extends SolarPanelRenderer> renderer;
		private int baseEUt;
		private int maxStack;
		private int tier;
		
		<T extends SolarPanelRenderer> SolarPanelType(T renderer, int voltageTier, int baseOut, int maxStack) {
			this.tier = voltageTier;
			this.baseEUt = baseOut;
			this.maxStack = maxStack;
			this.renderer = new NPULib.Holder<T>(renderer);
		}
		
		// ������� ��������� ���� �� ���-�� ������� � �����������
		public long getOutputAmperage(int amount) {
			return MathHelper.ceil(amount / (getVoltage() * 1.0D / baseEUt));
		}
		
		@SideOnly(Side.CLIENT)
		public NPULib.Holder<? extends SolarPanelRenderer> getRendererHolder() {
			return this.renderer;
		}
		
		// ������������ ���-�� ������� �� ���� ����������
		public int getStackAmount() {
			return this.maxStack;
		}
		
		// ������� ��������� ������
		public int getEUt() {
			return this.baseEUt;
		}
		
		public long getVoltage() {
			return GTValues.V[tier];
		}
		
		public int getTier() {
			return this.tier;
		}
	}
	
	private enum PanelStatus {
		DETACHED,
		ATTACHED;
	}
}
