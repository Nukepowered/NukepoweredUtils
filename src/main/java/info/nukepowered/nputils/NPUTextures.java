package info.nukepowered.nputils;

import gregtech.api.gui.resources.TextureArea;
import gregtech.api.render.OrientedOverlayRenderer;
import gregtech.api.render.SimpleCubeRenderer;
import gregtech.api.render.SimpleOverlayRenderer;
import gregtech.api.render.OrientedOverlayRenderer.OverlayFace;
import info.nukepowered.nputils.render.SimpleAtlasHolder;
import info.nukepowered.nputils.render.SolarPanelRenderer;

public class NPUTextures {
    public static SimpleCubeRenderer FUSION_TEXTURE;

    public static SimpleCubeRenderer ACTIVE_FUSION_TEXTURE;

    public static OrientedOverlayRenderer NAQADAH_OVERLAY;

    public static OrientedOverlayRenderer REPLICATOR_OVERLAY;

    public static OrientedOverlayRenderer MASS_FAB_OVERLAY;

    public static OrientedOverlayRenderer FUSION_REACTOR_OVERLAY;

    public static OrientedOverlayRenderer STEAM_MIXER_OVERLAY;
    
    public static final OrientedOverlayRenderer VENDING_MACHINE;
    
    public static final SolarPanelRenderer SOLAR_PANEL_BASIC;
    public static final SolarPanelRenderer SOLAR_PANEL_POLYCRYSTALLNE;
    public static final SolarPanelRenderer SOLAR_PANEL_MONOCRYSTALLINE;
    public static final SimpleAtlasHolder SOLAR_PANEL_BORDER;
    public static final SimpleAtlasHolder SOLAR_PANEL_SIDES;
    public static final SimpleAtlasHolder SOLAR_PANEL_BOTTOM;
	
    public static final SimpleOverlayRenderer ENERGY_IN_MULTI_16;
    public static final SimpleOverlayRenderer ENERGY_IN_PANELMODE;
    
//  GUI TEXTURES
    public static final TextureArea BRONZE_FLUID_SLOT = TextureArea.fullImage("textures/gui/steam/fluid_slot.png");
    public static final TextureArea COAL_OVERLAY = TextureArea.fullImage("textures/gui/steam/bronze/overlay_bronze_coal.png");
    public static final TextureArea VENDING_MACHINE_LINE = TextureArea.fullImage("textures/gui/vending_machine_line_overlay.png");
    public static final TextureArea WALLET_OVERLAY = TextureArea.fullImage("textures/gui/overlay/waller_overlay.png");
    public static final TextureArea COIN_OVERLAY = TextureArea.fullImage("textures/gui/overlay/coin_overlay.png");
    public static final TextureArea BUY_OVERLAY = TextureArea.fullImage("textures/gui/overlay/buy_overlay.png");
    public static final TextureArea SELL_OVERLAY = TextureArea.fullImage("textures/gui/overlay/sell_overlay.png");
    
    
    static {
    	VENDING_MACHINE = new OrientedOverlayRenderer("machines/vending_machine", OverlayFace.FRONT);
    	
    	SOLAR_PANEL_BASIC = new SolarPanelRenderer("machines/solar_panel/solar_panel.basic");
    	SOLAR_PANEL_POLYCRYSTALLNE = new SolarPanelRenderer("machines/solar_panel/solar_panel.polycrystalline");
    	SOLAR_PANEL_MONOCRYSTALLINE = new SolarPanelRenderer("machines/solar_panel/solar_panel.monocrystalline");
    	SOLAR_PANEL_BORDER = new SimpleAtlasHolder("machines/solar_panel/solar_panel.border");
    	SOLAR_PANEL_SIDES = new SimpleAtlasHolder("machines/solar_panel/solar_panel.sides");
    	SOLAR_PANEL_BOTTOM = new SimpleAtlasHolder("machines/solar_panel/solar_panel.bottom");
    	
    	ENERGY_IN_MULTI_16 = new SimpleOverlayRenderer("overlay/machine/overlay_energy_in_16a");
    	ENERGY_IN_PANELMODE = new SimpleOverlayRenderer("overlay/machine/overlay_energy_in_panelmode");
    	
        FUSION_TEXTURE = new SimpleCubeRenderer("casings/fusion/machine_casing_fusion_glass");

        ACTIVE_FUSION_TEXTURE = new SimpleCubeRenderer("casings/fusion/machine_casing_fusion_glass_yellow");

        NAQADAH_OVERLAY = new OrientedOverlayRenderer("machines/naquadah_reactor", new OverlayFace[]{OverlayFace.FRONT, OverlayFace.BACK, OverlayFace.TOP});

        REPLICATOR_OVERLAY = new OrientedOverlayRenderer("machines/replicator", new OverlayFace[]{OverlayFace.FRONT});

        MASS_FAB_OVERLAY = new OrientedOverlayRenderer("machines/mass_fab", new OverlayFace[]{OverlayFace.FRONT});

        FUSION_REACTOR_OVERLAY = new OrientedOverlayRenderer("machines/fusion_reactor", new OverlayFace[]{OverlayFace.FRONT});

        STEAM_MIXER_OVERLAY = new OrientedOverlayRenderer("machines/steam_mixer", new OverlayFace[]{OverlayFace.FRONT, OverlayFace.SIDE, OverlayFace.TOP});
    }
}
