package info.nukepowered.nputils.jei;

import java.util.Map.Entry;
import org.lwjgl.opengl.GL11;
import org.apache.commons.lang3.ArrayUtils;
import com.google.common.math.DoubleMath;

import info.nukepowered.nputils.machines.TileEntityIIBF;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import static gregtech.api.GTValues.*;
import static net.minecraft.client.renderer.GlStateManager.*;

/**
 * @author TheDarkDnKTv
 *
 * Copyright (C) 2021 TheDarkDnKTv. All rights reversed.
 */
public final class IIBFCoolantsRecipeWrapper implements IRecipeWrapper {
	
	private static final int VOLTAGES_FROM = MV;
	private static final int VOLTAGES_TO = UV;
	private static final int graphSizeY = 100;
	private static final int graphSizeX = 150;
	private static final int voltageStep = 32;
	
	private final FluidStack fluid;
	private final int multiplier;
	private final double[][] graphPoints;
	private final double[] valuesY;
	
	/* For graph  */
	private static final int MIN_VOLTAGE_POW;
	private static final int MAX_VOLTAGE_POW;
	private static final double X_AXIS_STEP;
	
	static {
		MIN_VOLTAGE_POW = (int)DoubleMath.log2(V[VOLTAGES_FROM]);
		MAX_VOLTAGE_POW = (int)DoubleMath.log2(V[VOLTAGES_TO]);
		X_AXIS_STEP = graphSizeX * 1.0D / (MAX_VOLTAGE_POW - MIN_VOLTAGE_POW);
	}
	
	public IIBFCoolantsRecipeWrapper(Entry<Fluid, Integer> entry) {
		this(new FluidStack(entry.getKey(), 1), entry.getValue());
	}
	
	public IIBFCoolantsRecipeWrapper(FluidStack fluid, int coolantMultiplier) {
		this.fluid = fluid.copy();
		this.multiplier = coolantMultiplier;
		
		// same formula as in IIBF, but getting voltage insteadof heat
		double maxV = Math.pow(10.0D, 8.0D) / (2.0D * Math.pow(coolantMultiplier, 2.55D));
		int size = (int)Math.ceil(maxV / voltageStep);
		graphPoints = new double[size][2];
		valuesY = new double[size];
		this.initGraph(size);
	}
	
	public FluidStack getFluid() {
		return fluid.copy();
	}
	
	public int getMultiplier() {
		return multiplier;
	}
	
	private void initGraph(int size) {
		double maxX = DoubleMath.log2(V[VOLTAGES_TO] - V[VOLTAGES_FROM]);
		double offset = DoubleMath.log2(V[VOLTAGES_FROM] - 1) / maxX;
		graphPoints[0][0] = 0;
		graphPoints[0][1] = 0;
		for (int i = (int)V[VOLTAGES_FROM], j = 1; j < size; i += voltageStep * 1.1D, j++) {
			graphPoints[j][0] = DoubleMath.log2(i) / maxX - offset;
			valuesY[j] = graphPoints[j][1] = TileEntityIIBF.calcualteHeat(1, multiplier, i);
		}
	}
	
	@Override
	public void getIngredients(IIngredients ingredients) {
		ingredients.setInput(VanillaTypes.FLUID, fluid.copy());
	}

	@Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
		this.drawGraph(minecraft, mouseX, mouseY);
    }
	
	private void drawGraph(Minecraft minecraft, int mouseX, int mouseY) {
		pushAttrib();
		pushMatrix();
		{
			FontRenderer fontRenderer = minecraft.fontRenderer;
			Tessellator tess = Tessellator.getInstance();
			BufferBuilder buf = tess.getBuffer();
			translate(20F, 150F, 0);
			mouseX -= 20;
			mouseY = -mouseY + 150;
			
			
			int fontColor = 0x606060;
			int fontColorNoAccent = 0x888888;
			float higlightColor = 0.35f;
			int voltageIdx = -1;
			double heatAmount = -1;
			
			fontRenderer.drawString(I18n.format("jei.induction_blast_furnance_coolant.axis_y_name"), -20, -graphSizeY - 22, fontColorNoAccent);
			
			// Draw X axis values
			double stepX = graphSizeX / (VOLTAGES_TO - VOLTAGES_FROM);
			for (int i = VOLTAGES_FROM; i <= VOLTAGES_TO; i++) {
				int x = (int)((fontRenderer.getStringWidth(VN[i]) / -2.0D) + (stepX * (i - VOLTAGES_FROM)));
				fontRenderer.drawString(VN[i], x, 4, fontColor);
			}
			
			// Draw Y axis values
			int stepY = 10;
			for (int i = 0; i < stepY; i++)
				fontRenderer.drawString(Integer.toString(graphSizeY - (stepY * i)), -20, -graphSizeY - 3 + (stepY * i), fontColor);
			
			
			disableLighting();
			disableTexture2D();
			
			GL11.glLineWidth(2.0F);
			drawAxes(tess, buf, 0.5F, 2);
			
			GL11.glLineWidth(0.5F);
			drawAxesValuesLines(tess, buf, 0.65F, 0, stepY);
			
			// Draw selection line
			if (mouseX > 0 && mouseX <= graphSizeX && mouseY > 0 && mouseY <= graphSizeY) {
				buf.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
				buf.pos(mouseX + 1.2D, 0, 1)
					.color(higlightColor, higlightColor, higlightColor, 1.0F)
					.endVertex();
				buf.pos(mouseX + 1.2D, -graphSizeY, 1)
					.color(higlightColor, higlightColor, higlightColor, 1.0F)
					.endVertex();
				tess.draw();
				buf.reset();
			}
			
			
			// Curve
			double[] selectedPoint = null;
			GL11.glLineWidth(1.5F);
			buf.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
			for (double[] point : graphPoints) {
				double y = point[1];
				double x = point[0] * graphSizeX;
				if (mouseX >= 0 && mouseX <= graphSizeX &&
						mouseY >= 0 && mouseY <= graphSizeY &&
						mouseX == (int)x) {
					heatAmount = y;
					voltageIdx = (int)(Math.pow(2, MIN_VOLTAGE_POW + (point[0] * (MAX_VOLTAGE_POW - MIN_VOLTAGE_POW))));
					selectedPoint = new double[2];
					selectedPoint[0] = x;
					selectedPoint[1] = y;
				}
				if (y > graphSizeY)
					break;
				buf.pos(x, -y, 3.0F)
					.color(0.0F, 0.0F, 0.0F, 1.0F)
					.endVertex();
			}
			tess.draw();
			
			
			if (selectedPoint != null) {
				GL11.glPointSize(5.0F);
				buf.begin(GL11.GL_POINTS, DefaultVertexFormats.POSITION_COLOR);
				buf.pos(selectedPoint[0], -selectedPoint[1], 3.0F)
					.color(1.0F, 1.0F, 1.0F, 1.0F)
					.endVertex();
				tess.draw();
			}
			
			
			enableTexture2D();
			
			if (selectedPoint != null) {
				int x = (int)selectedPoint[0];
				if (voltageIdx >= 0) {
					String str = String.format("%,d", voltageIdx);
					int width = fontRenderer.getStringWidth(str) / 2;
					fontRenderer.drawString(str, x - width, -graphSizeY - 12, fontColorNoAccent);
				}
				
				if (heatAmount >= 0) {
					int y = (int)selectedPoint[1];
					String str = String.format("%,.2f", heatAmount);
					int width = fontRenderer.getStringWidth(str);
					int x1 = graphSizeX - width;
					fontRenderer.drawString(String.format("%,.2f", heatAmount), x1 - 10 < x ? 3 : x1, -y - 5, fontColorNoAccent);
				}
			}
		}
		popMatrix();
		popAttrib();
	}
	
	private void drawAxes(Tessellator tess, BufferBuilder buf, float color, float zIndex) {
		buf.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
		buf.pos(0, -graphSizeY, zIndex)
			.color(color, color, color, 1.0F)
			.endVertex();
		buf.pos(0, 0, zIndex)
			.color(color, color, color, 1.0F)
			.endVertex();
		buf.pos(graphSizeX, 0, zIndex)
			.color(color, color, color, 1.0F)
			.endVertex();
		buf.pos(graphSizeX, -graphSizeY, zIndex)
			.color(color, color, color, 1.0F)
			.endVertex();
		buf.pos(0, -graphSizeY, zIndex)
			.color(color, color, color, 1.0F)
			.endVertex();
		tess.draw();
		buf.reset();
	}
	
	private void drawAxesValuesLines(Tessellator tess, BufferBuilder buf, float color, double zIndex, double stepY) {
		buf.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
		// X axis
		for (int i = MIN_VOLTAGE_POW; i < MAX_VOLTAGE_POW; i++) {
			double x = (i - MIN_VOLTAGE_POW) * X_AXIS_STEP;
			if (ArrayUtils.contains(V, (int) Math.pow(2, i))) {
				buf.pos(x, 0, zIndex)
					.color(color, color, color, 1.0F)
					.endVertex();
				buf.pos(x, -graphSizeY, zIndex)
					.color(color, color, color, 1.0F)
					.endVertex();
			}
		}
		
		// Y Axis
		for (int i = 1; i <= graphSizeY / stepY; i++) {
			buf.pos(0, i * -stepY, zIndex)
				.color(color, color, color, 1.0F)
				.endVertex();
			buf.pos(graphSizeX, i * -stepY, zIndex)
				.color(color, color, color, 1.0F)
				.endVertex();
		}
		
		tess.draw();
		buf.reset();
	}
}
