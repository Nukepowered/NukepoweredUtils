package info.nukepowered.nputils;

import info.nukepowered.nputils.blocks.NPUMetaBlocks;
import info.nukepowered.nputils.input.Keybinds;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy {
	
	public void preInit() {
		Keybinds.initBinds();
		super.preInit();
		Keybinds.registerCleint();
		new NPUTextures();
	}
	
	public void init() {
		super.init();
	}
	
	public void postInit() {
		super.postInit();
	}
	
	@SubscribeEvent
	public static void registertModels(ModelRegistryEvent e) {
		NPUMetaBlocks.registerItemModels();
	}
}
