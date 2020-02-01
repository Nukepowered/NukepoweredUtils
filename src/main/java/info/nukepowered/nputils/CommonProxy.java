package info.nukepowered.nputils;

import java.util.function.Function;

import gregtech.api.GTValues;
import gregtech.common.blocks.VariantItemBlock;
import info.nukepowered.nputils.api.NPULib;
import info.nukepowered.nputils.input.Keybinds;
import info.nukepowered.nputils.item.NPUMetaBlocks;
import info.nukepowered.nputils.item.NPUMetaItems;
import info.nukepowered.nputils.machines.NPUTileEntities;
import info.nukepowered.nputils.network.NetworkHandler;
import info.nukepowered.nputils.recipes.AEIntegration;
import info.nukepowered.nputils.recipes.DisassemblyRecipes;
import info.nukepowered.nputils.recipes.ForestryIntegration;
import info.nukepowered.nputils.recipes.GeneratorFuels;
import info.nukepowered.nputils.recipes.MachineCraftingRecipes;
import info.nukepowered.nputils.recipes.MatterReplication;
import info.nukepowered.nputils.recipes.NPUMachineRecipeRemoval;
import info.nukepowered.nputils.recipes.NPURecipeAddition;
import info.nukepowered.nputils.recipes.TinkersIntegration;
import info.nukepowered.nputils.worldgen.WorldGenRegister;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(modid = NPUtils.MODID)
public class CommonProxy {
	
	public void preInit() {
		NPULog.init();
		new NPULib();
		new NetworkHandler();
		NPUMaterials.init();
		NPUMetaItems.init();
		NPUMetaBlocks.init();
		NPUTileEntities.init();
		Keybinds.register();
		MinecraftForge.EVENT_BUS.register(new NPUEventHandler());
	}
	
	public void init() {
		if (Loader.isModLoaded(GTValues.MODID_FR) && NPUConfig.Integration.ForestryIntegration) {
			ForestryIntegration.removeFabricatorRecipes();
			ForestryIntegration.parseCentrifugeRecipes();
		}
		WorldGenRegister.init();
	}
	
	public void postInit() {
		NPURecipeAddition.generatedRecipes();
		if (Loader.isModLoaded("tconstruct") && NPUConfig.Integration.TiCIntegration)
			TinkersIntegration.preInit();
	}
	
	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> e) {
		IForgeRegistry<Block> registry = e.getRegistry();
		registry.register(NPUMetaBlocks.MULTIBLOCK_CASING);
		registry.register(NPUMetaBlocks.TRANSPARENT_CASING);
	}
	
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> e) {
		IForgeRegistry<Item> registry = e.getRegistry();
		registry.register(createItemBlock(NPUMetaBlocks.MULTIBLOCK_CASING, VariantItemBlock::new));
		registry.register(createItemBlock(NPUMetaBlocks.TRANSPARENT_CASING, VariantItemBlock::new));
	}
	
	private static <T extends Block> ItemBlock createItemBlock(T block, Function<T, ItemBlock> producer) {
		ItemBlock itemBlock = producer.apply(block);
		itemBlock.setRegistryName(block.getRegistryName());
		return itemBlock;
	}
	
	@SubscribeEvent(priority = EventPriority.LOW)
	public static void registerRecipes(RegistryEvent.Register<IRecipe> e) {
		NPUMachineRecipeRemoval.init();
		NPURecipeAddition.init();
		NPURecipeAddition.init1();
		if (Loader.isModLoaded("appliedenergistics2") && NPUConfig.Integration.AE2Integration)
			AEIntegration.init();
		if (Loader.isModLoaded(GTValues.MODID_FR) && NPUConfig.Integration.ForestryIntegration)
			ForestryIntegration.init();
		if (Loader.isModLoaded("tconstruct") && NPUConfig.Integration.TiCIntegration) 
			TinkersIntegration.init();
		MatterReplication.init();
		MachineCraftingRecipes.init();
		GeneratorFuels.init();
		if (NPUConfig.enableDissabembling) 
			DisassemblyRecipes.init();
		
		NPUMetaItems.registerOreDict();
		NPUMetaItems.registerRecipes();
	}
}
