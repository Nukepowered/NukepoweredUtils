package info.nukepowered.nputils.api;

import java.util.List;

import javax.annotation.Nullable;

import gregtech.api.GTValues;
import gregtech.api.capability.impl.RecipeLogicEnergy;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.metatileentity.SimpleMachineMetaTileEntity;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.render.OrientedOverlayRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class AmpereMachineMetaTileEntity extends SimpleMachineMetaTileEntity {

    private final int AMPERAGE_IN = 8;

    public AmpereMachineMetaTileEntity(ResourceLocation metaTileEntityId, RecipeMap<?> recipeMap, OrientedOverlayRenderer renderer, int tier) {
        super(metaTileEntityId, recipeMap, renderer, tier, true);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder holder)  {
        return new AmpereMachineMetaTileEntity(this.metaTileEntityId, this.workable.recipeMap, this.renderer, this.getTier());
    }

    @Override
    protected RecipeLogicEnergy createWorkable(RecipeMap<?> recipeMap) {
        return new AmperageRecipeLogic(this, recipeMap);
    }
    
    @Override
    protected long getMaxInputOutputAmperage() {
    	return this.AMPERAGE_IN;
    }
    
    @Override
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(I18n.format("gregtech.universal.tooltip.amperage_in_till", 8));
    }

    private static class AmperageRecipeLogic extends RecipeLogicEnergy {

        public AmperageRecipeLogic(AmpereMachineMetaTileEntity mte, RecipeMap<?> recipeMap) {
            super(mte, recipeMap, () -> mte.energyContainer);
        }

        @Override
        protected int[] calculateOverclock(int EUt, long voltage, int duration) {
            boolean negativeEU = EUt < 0;
            EUt *= getAmperage();
            int tier = getOverclockingTier(voltage);
            if (negativeEU)
                EUt = -EUt;
            if(!allowOverclocking) {
                return new int[] {EUt, duration};
            } else {
                int resultEUt = EUt;
                double resultDuration = duration;
                double durationMultiplier = negativeEU ? 3.80 : 2.0;
                while (resultDuration >= 3 && resultEUt <= GTValues.V[tier - 1] * getAmperage()) {
                    resultEUt *= 4;
                    resultDuration /= durationMultiplier;
                }
                return new int[]{negativeEU ? -resultEUt : resultEUt, (int) Math.floor(resultDuration)};
            }
        }

        private long getAmperage() {
            return ((AmpereMachineMetaTileEntity) metaTileEntity).AMPERAGE_IN;
        }
    }
}
