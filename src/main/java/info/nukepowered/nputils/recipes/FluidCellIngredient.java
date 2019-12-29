package info.nukepowered.nputils.recipes;

import javax.annotation.Nullable;

import gregtech.api.recipes.CountableIngredient;
import gregtech.api.unification.material.type.FluidMaterial;
import gregtech.common.items.MetaItems;
import info.nukepowered.nputils.NPULog;
import info.nukepowered.nputils.item.NPUMetaItems;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStackSimple;
import net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper;

public class FluidCellIngredient  extends Ingredient{
	Fluid fluid;
	
	/**
     * @param fluid
     * @param count Set to 0 for non consumable
     * @return
     */
	
	public static CountableIngredient getIngredient(Fluid fluid, int count) {
        return new CountableIngredient(new FluidCellIngredient(fluid), count);
    }

    public static CountableIngredient getIngredient(FluidMaterial fluidMaterial, int count) {
        return new CountableIngredient(new FluidCellIngredient(fluidMaterial.getMaterialFluid()), count);
    }

    public FluidCellIngredient(Fluid fluid) {
        super(NPUMetaItems.getFilledCell(fluid, 1));
        this.fluid = fluid;
    }

    @Override
    public boolean apply(@Nullable ItemStack itemStack) {
        IFluidHandlerItem stackFluid = itemStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
        FluidStack drained;
        try {
        	drained = stackFluid == null ? null : ((FluidHandlerItemStackSimple) stackFluid).getFluid();
        } catch (Exception e) {
        	NPULog.error("Trying get fluid from container getting an exception: " + e);
        	NPULog.error("Trying to use FluidBucketWrapper");
        	try {
        		drained = stackFluid == null ? null : ((FluidBucketWrapper) stackFluid).getFluid();
        	} catch (Exception e1) {
        		drained = null;
        		NPULog.error("Trying get fluid from container getting an exception second time: " + e1);
        		NPULog.error("Please report about this!");
        	}
        }
        return itemStack != null && MetaItems.FLUID_CELL.isItemEqual(itemStack) && drained != null && drained.getFluid() == fluid && drained.amount == 1000;
    }

    @Override
    public boolean isSimple() {
        return false;
    }
}
