package info.nukepowered.nputils.item;

import gregtech.api.GTValues;
import gregtech.api.items.armor.ArmorMetaItem;
import info.nukepowered.nputils.armor.AdvancedImpellerJetpack;
import info.nukepowered.nputils.armor.AdvancedNanoMuscleSuite;
import info.nukepowered.nputils.armor.AdvancedQurakTechSuite;
import info.nukepowered.nputils.armor.BatteryPack;
import info.nukepowered.nputils.armor.ImpellerJetpack;
import info.nukepowered.nputils.armor.NanoMuscleSuite;
import info.nukepowered.nputils.armor.PowerlessJetpack;
import info.nukepowered.nputils.armor.QuarkTechSuite;
import net.minecraft.inventory.EntityEquipmentSlot;

public class NPUMetaArmor extends ArmorMetaItem<ArmorMetaItem<?>.ArmorMetaValueItem> {
	@Override
	public void registerSubItems() {
		NPUMetaItems.NANO_MUSCLE_SUITE_CHESTPLATE = addItem(0, "nms.chestplate").setArmorLogic(new NanoMuscleSuite(EntityEquipmentSlot.CHEST, 5000, 1600000));
		NPUMetaItems.NANO_MUSCLE_SUITE_LEGGINS = addItem(1, "nms.leggins").setArmorLogic(new NanoMuscleSuite(EntityEquipmentSlot.LEGS, 5000, 1600000));
		NPUMetaItems.NANO_MUSCLE_SUITE_HELMET = addItem(2, "nms.helmet").setArmorLogic(new NanoMuscleSuite(EntityEquipmentSlot.HEAD, 5000, 1600000));
		NPUMetaItems.NANO_MUSCLE_SUITE_BOOTS = addItem(3, "nms.boots").setArmorLogic(new NanoMuscleSuite(EntityEquipmentSlot.FEET, 5000, 1600000));
		
		NPUMetaItems.QUARK_TECH_SUITE_CHESTPLATE = addItem(4, "qts.chestplate").setArmorLogic(new QuarkTechSuite(EntityEquipmentSlot.CHEST, 10000, 8000000, GTValues.EV));
		NPUMetaItems.QUARK_TECH_SUITE_LEGGINS = addItem(5, "qts.leggins").setArmorLogic(new QuarkTechSuite(EntityEquipmentSlot.LEGS, 10000, 8000000, GTValues.EV));
		NPUMetaItems.QUARK_TECH_SUITE_HELMET = addItem(6, "qts.helmet").setArmorLogic(new QuarkTechSuite(EntityEquipmentSlot.HEAD, 10000, 8000000, GTValues.EV));
		NPUMetaItems.QUARK_TECH_SUITE_BOOTS = addItem(7, "qts.boots").setArmorLogic(new QuarkTechSuite(EntityEquipmentSlot.FEET, 10000, 8000000, GTValues.EV));
		
		NPUMetaItems.SEMIFLUID_JETPACK = addItem(8, "liquid_fuel_jetpack").setArmorLogic(new PowerlessJetpack(12000, GTValues.MV));
		NPUMetaItems.IMPELLER_JETPACK = addItem(9, "impeller_jetpack").setArmorLogic(new ImpellerJetpack(128, 2520000, GTValues.MV));
		
		NPUMetaItems.BATPACK_LV = addItem(10, "battery_pack.lv").setArmorLogic(new BatteryPack(0, 600000, GTValues.LV));
		NPUMetaItems.BATPACK_MV = addItem(11, "battery_pack.mv").setArmorLogic(new BatteryPack(0, 2400000, GTValues.MV));
		NPUMetaItems.BATPACK_HV = addItem(12, "battery_pack.hv").setArmorLogic(new BatteryPack(0, 9600000, GTValues.HV));
		
		NPUMetaItems.ADVANCED_QAURK_TECH_SUITE_CHESTPLATE = addItem(13, "qts.advanced_chestplate").setArmorLogic(new AdvancedQurakTechSuite());
		NPUMetaItems.ADVANCED_NANO_MUSCLE_CHESTPLATE = addItem(14, "nms.advanced_chestplate").setArmorLogic(new AdvancedNanoMuscleSuite());
		NPUMetaItems.ADVANCED_IMPELLER_JETPACK = addItem(15, "advanced_impeller_jetpack").setArmorLogic(new AdvancedImpellerJetpack(512, 11400000, GTValues.HV));
		
		NPUMetaItems.IMPELLER_JETPACK.setModelAmount(8);
		NPUMetaItems.BATPACK_LV.setModelAmount(8);
		NPUMetaItems.BATPACK_MV.setModelAmount(8);
		NPUMetaItems.BATPACK_HV.setModelAmount(8);
	}
}
