package info.nukepowered.nputils;

import net.minecraftforge.common.config.Config;

@Config(modid = NPUtils.MODID, name = "NPUtils")
public class NPUConfig {
	
	@Config.Comment({"Set to false to disable machine recycling redcipes", "This will also disable registration of Disassembling machine"})
	public static boolean enableDissabembling = true;
	@Config.Comment({"Set to false to disable steam machines ARC furnance recipes", "This working without any calculation, just written recipes, so if you are changed it - disable this property"})
	public static boolean enableSteamMachineRecycling = true;
	@Config.Comment({"Set to false to disable replacing of standalone GT worldgen", "This function is testing, there is no any veins yet"})
	public static boolean replaceGTWorldGen = false;

	
	@Config.Comment("Config options of mod integration features")
	public static final Integration integration = new Integration();
	public static class Integration {
		@Config.Comment({"Set this to false to disable the Forestry Integration", "Changing some stuff, like recipes of electrone tubes"})
        public boolean ForestryIntegration = true;
        @Config.Comment("Set this to false to disable the Tinkers' Construct Integration")
        public boolean TiCIntegration = true;
        @Config.Comment("Set this to false to disable whole AE2 integration module")
        public boolean AE2Integration = true;
        
        @Config.Comment("AE2 integration module setting")
        public AE ae = new AE();
        public static class AE {
        	@Config.Comment("Set this to false to disable inscriber...etc recipes removal")
    		public boolean recipeRemoval = true;
    		@Config.Comment("Set this to false to disable processor's assembler recipes")
    		public boolean processorAssemlerRecipes = true;
    		@Config.Comment("Set this to false to disable processor prints forming recipes")
    		public boolean formingProcessorsPrintRecipes = true;
        }
	}
	
	@Config.Comment("Client side settings")
	public static final Client client = new Client();
	public static class Client {
		
		@Config.Comment("Set this to false to disable HUD rendering")
		public boolean enableHUD = true;
		@Config.Comment("Set this to false to disable sounds effects")
        public boolean enableSounds = true;
		@Config.Comment("Set this to false to disable activating boost on QuartTech Suite while sprinting")
        public boolean enableBoostWithSprint = true;
		
		@Config.Comment("Settings of HUD")
		public HUD hud = new HUD(1, 0, 0);
		
		public static class HUD {
			
			public HUD(final int mode, final int xOffset, final int yOffset) {
				this.mode = (byte)mode;
				this.offsetX = (byte)xOffset;
				this.offsetY = (byte)yOffset;
			}
			
			@Config.Comment({"Sets HUD location", "1 - left-upper conrer", "2 - right-upper corner", "3 - left-bottom corner", "4 - right-bottom corner"})
			public byte mode;
			@Config.Comment("Horizontal offset of HUD [0 ~ 100)")
			public byte offsetX;
			@Config.Comment("Vertical ooffset of HUD [0 ~ 100)")
			public byte offsetY;
		}
	}
	
	@Config.Comment("Config options for stuff from GT6")
	public static GT6 GT6 = new GT6();
	public static class GT6 {
		@Config.Comment({"Bending Recipes (disabling Bending Cylinders' recipes disables all of them)", "Bending - Bending Cylinders' recipes"})
		public boolean BendingCylinders = true;
		@Config.Comment("Bending - Curved Plates' recipes")
		public boolean BendingCurvedPlates = true;
		@Config.Comment("Bending - Rotors require Curved Plates")
		public boolean BendingRotors = true;
		@Config.Comment("Bending - Rings are crafted with Bending Cyliders")
		public boolean BendingRings = true;
		@Config.Comment("Bending - Foils are made with Bending Cylinders")
		public boolean BendingFoils = true;
		@Config.Comment("Bending - Foils are automated in the Cluster Mill instead of the Bending Machine")
		public boolean BendingFoilsAutomatic = true;
		@Config.Comment("Bending - Pipes are crafted with Curved Plates")
		public boolean BendingPipes = true;

		@Config.Comment("Set this to false to disable Plates being crafted from Double Ingots")
		public boolean PlateDoubleIngot = true;
	}
	
	@Config.Comment({"Config options all gameplay related features", "There is all options about hard crafting etc..."})
	public static GAMEPLAY gameplay = new GAMEPLAY();
	public static class GAMEPLAY {
		@Config.Comment("Set this to false to enable the GT5 Wrench recipes")
		public boolean ExpensiveWrenches = true;
		@Config.Comment("Set to false to enable Log -> Charcoal smelting recipes")
		public boolean DisableLogToCharcoalSmelting = true;
		@Config.Comment({"Set to false to disable generated wood sawing recipes", "A saw is required to get 4 Planks per Log"})
		public boolean GeneratedSawingRecipes = true;
        @Config.Comment("Set this to false to disable naquadah reactors")
        public boolean enableNaquadahReactors = true;
        
        // TODO Exceptions
		@Config.Comment({"Set these to flase to disable the generated Packager and Unpackaker recipes", "Packaging - 1x1 recipes with 9 outputs can be automated with the Unpackaker"})
        public boolean Unpackager3x3Recipes = true;
        @Config.Comment("Packaging - 3x3 recipes can automated with the Packagers")
        public boolean Packager3x3Recipes = true;
        @Config.Comment("Packaging - 2x2 recipes can automated with the Packagers")
        public boolean Packager2x2Recipes = true;
        
		@Config.Comment({"Set these to false to disable the generated Compressor recipes for blocks", "Compression - Generate Compressor recipes for blocks"})
		public boolean GenerateCompressorRecipes = true;
		@Config.Comment("Compression - Remove 3x3 crafting recipes for blocks")
		public boolean Remove3x3BlockRecipes = true;
		@Config.Comment("Compression - Remove crafting recipes for uncompressing blocks")
		public boolean RemoveBlockUncraftingRecipes = true;
		@Config.Comment({"Set these to false to disable certain Batteries.", "Batteries - Enable an extra ZPM and UV Battery (this also makes the Ultimate Battery harder to make)"})
		public boolean enableZPMandUVBats = true;
		@Config.Comment("Batteries - Replace the Ultimate Battery with a MAX Battery")
		public boolean replaceUVwithMAXBat = true;
		@Config.Comment({"Set to false to disable GT5U Cable isolation recipes", "Cables can be isolated with different combinations of Rubbers and Dusts with varying efficiencies"})
		public boolean CablesGT5U = true;
	}
	
	@Config.Comment("Config options for higher tiers machine")
	public static HighTiers tiers = new HighTiers();
	public static class HighTiers {
		@Config.Comment("Set this to true to enable ZPM and UV tiers of machines")
		public boolean uselessTiers = false;
		
		@Config.Comment("Set this to false to disable the high tier Air Collectors")
        public boolean highTierCollector = true;
		@Config.Comment({"Set these to false to disable the higher tier versions of machines", "Should higher tier Alloy Smelters be registered?"})
		public boolean highTierAlloySmelter = true;
		@Config.Comment("Should higher tier Arc Furnaces be registered?")
		public boolean highTierArcFurnaces = true;
		@Config.Comment("Should higher tier Assembling Machines be registered?")
		public boolean highTierAssemblers = true;
		@Config.Comment("Should higher tier Autoclaves be registered?")
		public boolean highTierAutoclaves = true;
		@Config.Comment("Should higher tier Bending Machines be registered?")
		public boolean highTierBenders = true;
		@Config.Comment("Should higher tier Breweries be registered?")
		public boolean highTierBreweries = true;
		@Config.Comment("Should higher tier Canning Machines be registered?")
		public boolean highTierCanners = true;
		@Config.Comment("Should higher tier Centrifuges be registered?")
		public boolean highTierCentrifuges = true;
		@Config.Comment("Should higher tier Chemical Baths be registered?")
		public boolean highTierChemicalBaths = true;
		@Config.Comment("Should higher tier Chemical Reactors be registered?")
		public boolean highTierChemicalReactors = true;
		@Config.Comment("Should higher tier Circuit Assembling Machines be registered?")
		public boolean highTierCircuitAssemblers = true;
		@Config.Comment("Should higher tier Compressors be registered?")
		public boolean highTierCompressors = true;
		@Config.Comment("Should higher tier Cutting Machines be registered?")
		public boolean highTierCutters = true;
		@Config.Comment("Should higher tier Cluster Mills be registered?")
		public boolean highTierClusterMills = true;
		@Config.Comment("Should higher tier Distilleries be registered?")
		public boolean highTierDistilleries = true;
		@Config.Comment("Should higher tier Electric Furnaces be registered?")
		public boolean highTierElectricFurnace = true;
		@Config.Comment("Should higher tier Electrolyzers be registered?")
		public boolean highTierElectrolyzers = true;
		@Config.Comment("Should higher tier Electromagnetic Separators be registered?")
		public boolean highTierElectromagneticSeparators = true;
		@Config.Comment("Should higher tier Extractors be registered?")
		public boolean highTierExtractors = true;
		@Config.Comment("Should higher tier Extruders be registered?")
		public boolean highTierExtruders = true;
		@Config.Comment("Should higher tier Fermenters be registered?")
		public boolean highTierFermenters = true;
		@Config.Comment("Should higher tier Fishers be registered?")
		public boolean highTierFishers = true;
		@Config.Comment("Should higher tier Eluid Canners be registered?")
		public boolean highTierFluidCanners = true;
		@Config.Comment("Should higher tier Fluid Extractors be registered?")
		public boolean highTierFluidExtractors = true;
		@Config.Comment("Should higher tier Fluid Heaters be registered?")
		public boolean highTierFluidHeaters = true;
		@Config.Comment("Should higher tier Fluid Heaters be registered?")
		public boolean highTierFluidSolidifiers = true;
		@Config.Comment("Should higher tier Forge Hammers be registered?")
		public boolean highTierForgeHammers = true;
		@Config.Comment("Should higher tier Forming Presses be registered?")
		public boolean highTierFormingPresses = true;
		@Config.Comment("Should higher tier Lathes be registered?")
		public boolean highTierLathes = true;
		@Config.Comment("Should higher tier Microwaves be registered?")
		public boolean highTierMicrowaves = true;
		@Config.Comment("Should higher tier Mixers be registered?")
		public boolean highTierMixers = true;
		@Config.Comment("Should higher tier Ore Washers be registered?")
		public boolean highTierOreWashers = true;
		@Config.Comment("Should higher tier Packagers be registered?")
		public boolean highTierPackers = true;
		@Config.Comment("Should higher tier Plasma Arc Furnaces be registered?")
		public boolean highTierPlasmaArcFurnaces = true;
		@Config.Comment("Should higher tier Polarizers be registered?")
		public boolean highTierPolarizers = true;
		@Config.Comment("Should higher tier Precision Laser Engravers be registered?")
		public boolean highTierLaserEngravers = true;
		@Config.Comment("Should higher tier Pumps be registered?")
		public boolean highTierPumps = true;
		@Config.Comment("Should higher tier Rock Breakers be registered?")
		public boolean highTierRockBreakers = true;
		@Config.Comment("Should higher tier Replicators be registered?")
		public boolean highTierReplicators = true;
		@Config.Comment("Should higher tier Sifting Machines be registered?")
		public boolean highTierSifters = true;
		@Config.Comment("Should higher tier Thermal Centrifuges be registered?")
		public boolean highTierThermalCentrifuges = true;
		@Config.Comment("Should higher tier Macerators be registered?")
		public boolean highTierMacerators = true;
		@Config.Comment("Should higher tier Mass Fabricators be registered?")
		public boolean highTierMassFabs = true;
		@Config.Comment("Should higher tier Unpackagers be registered?")
		public boolean highTierUnpackers = true;
		@Config.Comment("Should higher tier Wiremills be registered?")
		public boolean highTierWiremills = true;
        @Config.Comment("Should higher tier Disassembling machines be registered?")
        public boolean highTierDisassemblers = true;
	}
	
}
