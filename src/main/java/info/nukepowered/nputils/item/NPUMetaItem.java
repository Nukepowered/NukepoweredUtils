package info.nukepowered.nputils.item;

import java.util.Comparator;
import gregtech.api.items.materialitem.MaterialMetaItem;
import gregtech.api.items.metaitem.ElectricStats;
import gregtech.api.items.metaitem.MetaItem;
import gregtech.api.items.metaitem.stats.IItemBehaviour;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.material.MarkerMaterials.Tier;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.common.items.MetaItems;
import info.nukepowered.nputils.NPUConfig;
import net.minecraftforge.fml.common.Loader;


public class NPUMetaItem extends MaterialMetaItem {
	public NPUMetaItem() {
		super(OrePrefix.valueOf("plateCurved"), OrePrefix.valueOf("ingotDouble"), OrePrefix.valueOf("round"), null,
				null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
				null, null, null, null, null, null, null, null, null, null, null);
	}
	
	public void registerSubItems() {
		NPUMetaItems.COKE_BRICK = addItem(0, "npu_brick.coke");
		NPUMetaItems.FIRECLAY_BRICK = addItem(1, "npu_brick.fireclay").setUnificationData(OrePrefix.ingot, Materials.Fireclay);
		NPUMetaItems.ADVANCED_CIRCUIT = addItem(2, "circuit.advanced.regular").setUnificationData(OrePrefix.circuit, Tier.Advanced);
		NPUMetaItems.GOOD_CIRCUIT = addItem(3, "circuit.good.regular").setUnificationData(OrePrefix.circuit, Tier.Good);
		NPUMetaItems.PETRI_DISH = addItem(4, "component.petri.dish");
		NPUMetaItems.COMPRESSED_CLAY = addItem(5, "npu_compressed.clay");
		NPUMetaItems.COMPRESSED_COKE_CLAY = addItem(6, "npu_compressed.coke.clay");
		NPUMetaItems.COMPRESSED_FIRECLAY = addItem(7, "npu_compressed.fireclay");
		NPUMetaItems.CRYSTAL_COMPUTER = addItem(8, "computer.crystal").setUnificationData(OrePrefix.circuit, Tier.Ultimate);
		NPUMetaItems.NANO_COMPUTER = addItem(9, "computer.nano").setUnificationData(OrePrefix.circuit, Tier.Elite);
		NPUMetaItems.QUANTUM_COMPUTER = addItem(10, "computer.quantum").setUnificationData(OrePrefix.circuit, Tier.Master);
		NPUMetaItems.CRYSTAL_MAINFRAME = addItem(11, "mainframe.crystal").setUnificationData(OrePrefix.circuit, Tier.Superconductor);
		NPUMetaItems.NANO_MAINFRAME = addItem(12, "mainframe.nano").setUnificationData(OrePrefix.circuit, Tier.Master);
		NPUMetaItems.INTEGRATED_MAINFRAME = addItem(13, "mainframe.normal").setUnificationData(OrePrefix.circuit, Tier.Elite);
		NPUMetaItems.QUANTUM_MAINFRAME = addItem(14, "mainframe.quantum").setUnificationData(OrePrefix.circuit, Tier.Ultimate);
		NPUMetaItems.NEURO_PROCESSOR = addItem(15, "processor.neuro");
		NPUMetaItems.INTEGRATED_COMPUTER = addItem(16, "computer.normal").setUnificationData(OrePrefix.circuit, Tier.Extreme);
		NPUMetaItems.RAW_CRYSTAL_CHIP = addItem(17, "crystal.raw");
		NPUMetaItems.STEMCELLS = addItem(18, "stemcells");
		NPUMetaItems.MICA_SHEET = addItem(26, "mica_sheet");
		NPUMetaItems.MICA_INSULATOR_SHEET = addItem(27, "mica_insulator_sheet");
        NPUMetaItems.MICA_INSULATOR_FOIL = addItem(28, "mica_insulator_foil");
        NPUMetaItems.BASIC_BOARD = addItem(29, "board.basic");
        NPUMetaItems.GOOD_PHENOLIC_BOARD = addItem(30, "board.good.phenolic");
        NPUMetaItems.GOOD_PLASTIC_BOARD = addItem(31, "board.good.plastic");
        NPUMetaItems.ADVANCED_BOARD = addItem(32, "board.advanced");
        NPUMetaItems.EXTREME_BOARD = addItem(33, "board.extreme");
        NPUMetaItems.ELITE_BOARD = addItem(34, "board.elite");
        NPUMetaItems.MASTER_BOARD = addItem(35, "board.master");
		
		if (Loader.isModLoaded("forestry") && NPUConfig.Integration.ForestryIntegration) {
			NPUMetaItems.ELECTRODE_APATITE = addItem(100, "electrode.apatite");
			NPUMetaItems.ELECTRODE_BLAZE = addItem(101, "electrode.blaze");
			NPUMetaItems.ELECTRODE_BRONZE = addItem(102, "electrode.bronze");
			NPUMetaItems.ELECTRODE_COPPER = addItem(103, "electrode.copper");
			NPUMetaItems.ELECTRODE_DIAMOND = addItem(104, "electrode.diamond");
			NPUMetaItems.ELECTRODE_EMERALD = addItem(105, "electrode.emerald");
			NPUMetaItems.ELECTRODE_ENDER = addItem(106, "electrode.ender");
			NPUMetaItems.ELECTRODE_GOLD = addItem(107, "electrode.gold");
			if (Loader.isModLoaded("ic2") || Loader.isModLoaded("binniecore"))
				NPUMetaItems.ELECTRODE_IRON = addItem(108, "electrode.iron");
			NPUMetaItems.ELECTRODE_LAPIS = addItem(109, "electrode.lapis");
			NPUMetaItems.ELECTRODE_OBSIDIAN = addItem(110, "electrode.obsidian");
			if (Loader.isModLoaded("extrautils2"))
				NPUMetaItems.ELECTRODE_ORCHID = addItem(111, "electrode.orchid");
			if (Loader.isModLoaded("ic2") || Loader.isModLoaded("techreborn") || Loader.isModLoaded("binniecore"))
				NPUMetaItems.ELECTRODE_RUBBER = addItem(112, "electrode.rubber");
			NPUMetaItems.ELECTRODE_TIN = addItem(113, "electrode.tin");
		}
		
		if (NPUConfig.GT5U.enableZPMandUVBats) {
			NPUMetaItems.ENERGY_MODULE = addItem(19, "energy.module").addComponents(ElectricStats.createRechargeableBattery(10000000000L, 7)).setModelAmount(8);
			NPUMetaItems.ENERGY_CLUSTER = addItem(20, "energy.cluster").addComponents(ElectricStats.createRechargeableBattery(100000000000L, 8)).setModelAmount(8);
        }
		
		if (NPUConfig.GT5U.replaceUVwithMAXBat) {
			NPUMetaItems.MAX_BATTERY = addItem(21, "max.battery").addComponents(ElectricStats.createRechargeableBattery(9223372036854775807L, 9)).setModelAmount(8);
            MetaItems.ZPM2.setInvisible();
        }
		
		if (Loader.isModLoaded("tconstruct") && NPUConfig.Integration.TiCIntegration) {
			NPUMetaItems.COMPRESSED_GROUT = addItem(22, "npu_compressed.grout");
			NPUMetaItems.MOLD_FORM_ANVIL = addItem(36, "mold.form.anvil");
			NPUMetaItems.MOLD_FORM_BALL = addItem(37, "mold.form.ball");
			NPUMetaItems.MOLD_FORM_BLOCK = addItem(38, "mold.form.block");
			NPUMetaItems.MOLD_FORM_BOTTLE = addItem(39, "mold.form.bottle");
			NPUMetaItems.MOLD_FORM_COINAGE = addItem(40, "mold.form.coinage");
			NPUMetaItems.MOLD_FORM_CYLINDER = addItem(23, "mold.form.cylinder");
			NPUMetaItems.MOLD_FORM_GEAR = addItem(41, "mold.form.gear");
	        NPUMetaItems.MOLD_FORM_INGOT = addItem(42, "mold.form.ingot");
	        NPUMetaItems.MOLD_FORM_NAME = addItem(43, "mold.form.name");
	        NPUMetaItems.MOLD_FORM_NUGGETS = addItem(44, "mold.form.nuggets");
	        NPUMetaItems.MOLD_FORM_PLATE = addItem(45, "mold.form.plate");
	        NPUMetaItems.MOLD_FORM_SMALL_GEAR = addItem(46, "mold.form.small_gear");
	        NPUMetaItems.SHAPE_AXE_HEAD = addItem(47, "shape.axe_head");
	        NPUMetaItems.SHAPE_BLOCK = addItem(48, "shape.block");
	        NPUMetaItems.SHAPE_BOLT = addItem(49, "shape.bolt");
	        NPUMetaItems.SHAPE_BOTTLE = addItem(50, "shape.bottle");
	        NPUMetaItems.SHAPE_CELL = addItem(51, "shape.cell");
	        NPUMetaItems.SHAPE_FILE_HEAD = addItem(52, "shape.file_head");
	        NPUMetaItems.SHAPE_GEAR = addItem(53, "shape.gear");
	        NPUMetaItems.SHAPE_HAMMER_HEAD = addItem(54, "shape.hammer_head");
	        NPUMetaItems.SHAPE_HOE_HEAD = addItem(55, "shape.hoe_head");
	        NPUMetaItems.SHAPE_INGOT = addItem(56, "shape.ingot");
	        NPUMetaItems.SHAPE_LARGE_PIPE = addItem(57, "shape.large_pipe");
	        NPUMetaItems.SHAPE_NORMAL_PIPE = addItem(58, "shape.normal_pipe");
	        NPUMetaItems.SHAPE_PICKAXE_HEAD = addItem(59, "shape.pickaxe_head");
	        NPUMetaItems.SHAPE_PLATE = addItem(60, "shape.plate");
	        NPUMetaItems.SHAPE_RING = addItem(61, "shape.ring");
	        NPUMetaItems.SHAPE_ROD = addItem(62, "shape.rod");
	        NPUMetaItems.SHAPE_SAW_BLADE = addItem(63, "shape.saw_blade");
	        NPUMetaItems.SHAPE_SHOVEL_HEAD = addItem(64, "shape.shovel_head");
	        NPUMetaItems.SHAPE_SMALL_PIPE = addItem(65, "shape.small_pipe");
	        NPUMetaItems.SHAPE_SWORD_BLADE = addItem(66, "shape.sword_blade");
	        NPUMetaItems.SHAPE_TINY_PIPE = addItem(67, "shape.tiny_pipe");
	        NPUMetaItems.SHAPE_WIRE = addItem(68, "shape.wire");
	        
	        MetaItems.COMPRESSED_CLAY.setInvisible();
	        MetaItems.COMPRESSED_FIRECLAY.setInvisible();
	        MetaItems.COKE_OVEN_BRICK.setInvisible();
	        MetaItems.FIRECLAY_BRICK.setInvisible();
	    }
		
        if (NPUConfig.enableRealisticMotorCraft) {
	        NPUMetaItems.STATOR_LV = addItem(69, "stator.lv");
	        NPUMetaItems.STATOR_MV = addItem(70, "stator.mv");
	        NPUMetaItems.STATOR_HV = addItem(71, "stator.hv");
	        NPUMetaItems.STATOR_EV = addItem(72, "stator.ev");
	        NPUMetaItems.STATOR_IV = addItem(73, "stator.iv");
	        NPUMetaItems.STATOR_LuV = addItem(74, "stator.luv");
	        NPUMetaItems.STATOR_ZPM = addItem(75, "stator.zpm");
	        NPUMetaItems.STATOR_UV = addItem(76, "stator.uv");
	        
	        NPUMetaItems.ROTOR_LV = addItem(77, "rotor.lv");
	        NPUMetaItems.ROTOR_MV = addItem(78, "rotor.mv");
	        NPUMetaItems.ROTOR_HV = addItem(79, "rotor.hv");
	        NPUMetaItems.ROTOR_EV = addItem(80, "rotor.ev");
	        NPUMetaItems.ROTOR_IV = addItem(81, "rotor.iv");
	        NPUMetaItems.ROTOR_LuV = addItem(82, "rotor.luv");
	        NPUMetaItems.ROTOR_ZPM = addItem(83, "rotor.zpm");
	        NPUMetaItems.ROTOR_UV = addItem(84, "rotor.uv");
	        
	        NPUMetaItems.MOTOR_HULL_LV = addItem(85, "motor_hull.lv");
	        NPUMetaItems.MOTOR_HULL_MV = addItem(86, "motor_hull.mv");
	        NPUMetaItems.MOTOR_HULL_HV = addItem(87, "motor_hull.hv");
	        NPUMetaItems.MOTOR_HULL_EV = addItem(88, "motor_hull.ev");
	        NPUMetaItems.MOTOR_HULL_IV = addItem(89, "motor_hull.iv");
	        NPUMetaItems.MOTOR_HULL_LuV = addItem(90, "motor_hull.luv");
	        NPUMetaItems.MOTOR_HULL_ZPM = addItem(91, "motor_hull.zpm");
	        NPUMetaItems.MOTOR_HULL_UV = addItem(92, "motor_hull.uv");
        }
        
        NPUMetaItems.INSULATING_TAPE = addItem(93, "insulating_tape");
        NPUMetaItems.MAGNETICALLY_PERMEABLE_PLATE_SET = addItem(94, "magnetic_plates");
        NPUMetaItems.FERRITE_PLATE_SET = addItem(95, "ferrite_plates");
        
        NPUMetaItems.POLYCRYSTALLINE_SOLAR_CELL = addItem(96, "solar_cell.polycrystalline");
        NPUMetaItems.MONOCRYSTALLINE_SOLAR_CELL = addItem(97, "solar_cell.monocrystalline");
        NPUMetaItems.MONOCRYSTALL_SILICON_PLATE = addItem(98, "plate.monocrystall_silicone");
		
        NPUMetaItems.IMPELLER_MV = addItem(120, "impeller.mv");
        NPUMetaItems.IMPELLER_HV = addItem(121, "impeller.hv");
        NPUMetaItems.GRAVITATION_ENGINE = addItem(122, "gravitation_engine");
        
        NPUMetaItems.COIN_WALLET = addItem(123, "coin_wallet").addComponents(new WalletBehavior()).setMaxStackSize(1);
        
        NPUMetaItems.COIN_BRONZE = addItem(124, "credit.bronze").addComponents(new CoinBehaviour(10), new CoinInfoBehaviour(Materials.Bronze));
        NPUMetaItems.COIN_IRON = addItem(125, "credit.iron").addComponents(new CoinBehaviour(20), new CoinInfoBehaviour(Materials.Iron));

        MetaItems.CREDIT_COPPER.addComponents(new CoinBehaviour(1));
        MetaItems.CREDIT_CUPRONICKEL.addComponents(new CoinBehaviour(5));
        MetaItems.CREDIT_SILVER.addComponents(new CoinBehaviour(50));
        MetaItems.CREDIT_GOLD.addComponents(new CoinBehaviour(100));
        MetaItems.CREDIT_PLATINUM.addComponents(new CoinBehaviour(200));
        MetaItems.CREDIT_NAQUADAH.addComponents(new CoinBehaviour(500));
        MetaItems.CREDIT_OSMIUM.addComponents(new CoinBehaviour(1000));
        MetaItems.CREDIT_DARMSTADTIUM.addComponents(new CoinBehaviour(10000));
        
        // Add all of coins in list
        for (MetaItem<?> item : MetaItem.getMetaItems()) {
        	for (MetaItem<?>.MetaValueItem metaValue : item.getAllItems()) {
        		for (IItemBehaviour beh : metaValue.getBehaviours()) {
        			if (beh instanceof CoinBehaviour) {
        				NPUMetaItems.COINS.add(metaValue);
        			}
        		}
        	}
        }
        
        // Sort coins for proper display in wallet's UI
        NPUMetaItems.COINS.sort(Comparator.comparing(coin -> CoinBehaviour.getCoinValue(coin)));
        
	}
}
