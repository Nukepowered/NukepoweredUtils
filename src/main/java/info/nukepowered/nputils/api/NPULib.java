package info.nukepowered.nputils.api;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import gregtech.api.capability.GregtechCapabilities;
import gregtech.api.capability.IElectricItem;
import gregtech.api.items.armor.ArmorMetaItem;
import gregtech.api.items.metaitem.MetaItem;
import gregtech.api.items.metaitem.stats.IItemBehaviour;
import gregtech.api.items.toolitem.ToolMetaItem;
import gregtech.api.recipes.CountableIngredient;
import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.util.ItemStackKey;
import info.nukepowered.nputils.NPUConfig;
import info.nukepowered.nputils.NPULog;
import info.nukepowered.nputils.input.EnumKey;
import info.nukepowered.nputils.input.Key;
import info.nukepowered.nputils.input.Keybinds;
import info.nukepowered.nputils.item.CoinBehaviour;
import info.nukepowered.nputils.item.WalletBehavior;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author The_DnK / TheDarkDnKTv / iDnK
 */
public class NPULib {
	
	public static final Side SIDE = FMLCommonHandler.instance().getSide();
	public static final SoundEvent JET_ENGINE = new SoundEvent(new ResourceLocation("nputils:jet_engine"));
	
	
	@Nullable
	public static WalletBehavior getWalletBehaviour(ItemStack stack) {
		if (stack.getItem() instanceof MetaItem) {
			List<IItemBehaviour> behaviours = ((MetaItem<?>) stack.getItem()).getBehaviours(stack);
			for (IItemBehaviour behaviour : behaviours) {
				if (behaviour instanceof WalletBehavior) {
					return (WalletBehavior) behaviour;
				}
			}
		}
		
		return null;
	}
	
	@Nullable
	public static CoinBehaviour getCoinBehaviour(ItemStack stack) {
		if (stack.getItem() instanceof MetaItem) {
			List<IItemBehaviour> behaviours = ((MetaItem<?>) stack.getItem()).getBehaviours(stack);
			for (IItemBehaviour behaviour : behaviours) {
				if (behaviour instanceof CoinBehaviour) {
					return (CoinBehaviour) behaviour;
				}
			}
		}
		
		return null;
	}
	
	public static List<ItemStack> getStackedList(ItemStack sample, int totalAmount) {
		List<ItemStack> result = new ArrayList<>();
		if (totalAmount > sample.getMaxStackSize()) {
			double totalStacks = (totalAmount * 1.0D) / sample.getMaxStackSize();
			if (totalStacks == MathHelper.floor(totalStacks)) {
				for (int i = 0; i < totalStacks; i++) {
					ItemStack curr = sample.copy();
					curr.setCount(curr.getMaxStackSize());
					result.add(curr);
				}
			} else {
				totalAmount -= MathHelper.floor(totalStacks) * sample.getMaxStackSize();
				for (int i = 0; i < MathHelper.floor(totalStacks); i++) {
					ItemStack curr = sample.copy();
					curr.setCount(curr.getMaxStackSize());
					result.add(curr);
				}
				ItemStack curr = sample.copy();
				curr.setCount(totalAmount);
				result.add(curr);
			}
		} else {
			ItemStack res = sample.copy();
			res.setCount(totalAmount);
			result.add(res);
		}
		
		return result;
	}
	
	public static List<ItemStack> copyStackList(List<ItemStack> itemStacks) {
        List<ItemStack> stacks = new ArrayList<>();
        for (int i = 0; i < itemStacks.size(); i++) {
        	ItemStack stack = itemStacks.get(i);
        	if (!stack.isEmpty()) {
        		stacks.add(stack.copy());
        	}
        }
        
        return stacks;
    }
	
	/** Check is possible to charge item
	 * 
	 * @param chargeable
	 * @return
	 */
	public static boolean isPossibleToCharge(ItemStack chargeable) {
		IElectricItem container = chargeable.getCapability(GregtechCapabilities.CAPABILITY_ELECTRIC_ITEM, null);
		if (container != null) {
			if (container.getCharge() < container.getMaxCharge() && (container.getCharge() + container.getTransferLimit()) <= container.getMaxCharge()) {
				return true;
			}
		}
		return false;
	}
	
	/** Check is item instance of GT armor or Tool
	 * P.S decided not to use this method
	 * @param stack
	 * @return
	 */
	public static boolean isItemToolOrArmor(ItemStack stack) {
		if (stack.getItem() instanceof MetaItem) {
			if (stack.getItem() instanceof ArmorMetaItem || stack.getItem() instanceof ToolMetaItem) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Searching in player's inventory for tool/armor item, w
	 * @param player
	 * @param tier of charger
	 * @return index of slot, where is item
	 */
	public static int getChargeableItem(EntityPlayer player, int tier) {
		int result = -1;
		for (int i = 0; i < player.inventory.mainInventory.size(); i++) {
			ItemStack current = player.inventory.mainInventory.get(i);
			IElectricItem item = current.getCapability(GregtechCapabilities.CAPABILITY_ELECTRIC_ITEM, null);
			if (item == null) continue;
			/* && NPULib.isItemToolOrArmor(current)
			 * Decided not to use this, becasue nano saber int's tool. Unfortunatly will also charge batteries in you inventory
			 */
			if (NPULib.isPossibleToCharge(current) && item.getTier() <= tier) {
				result = i;
				break;
			}	
		}
		return result;
	}
	
	/**
	 * Spawn particle behind player with speedY speed
	 * @param world
	 * @param player
	 * @param type
	 * @param speedY
	 */
	public static void spawnParticle(World world, EntityPlayer player, EnumParticleTypes type, double speedY) {
		if (SIDE.isClient()) {
			Vec3d forward = player.getForward();
			world.spawnParticle(type, player.posX - forward.x, player.posY + 0.5D, player.posZ - forward.z, 0.0D, speedY, 0.0D);
		}
	}
	
	public static void playJetpackSound(@Nonnull EntityPlayer player) {
		if (NPUConfig.Misc.enableSounds && SIDE.isClient()) {
			float cons = (float)player.motionY + player.moveForward;
			cons = MathHelper.clamp(cons, 0.6F, 1.0F);
			
			if (player.motionY > 0.05F) {
				cons += 0.1F;
			}
			
			if (player.motionY < -0.05F) {
				cons -= 0.4F;
			}
			
			player.playSound(JET_ENGINE, 0.3F, cons);
		}
	}
	
	/**
	 * Resets private field, amount of ticks player in the sky
	 * @param player
	 */
	@SuppressWarnings("deprecation")
	public static void resetPlayerFloatingTime(EntityPlayer player) {
		if (player instanceof EntityPlayerMP) { 
			ObfuscationReflectionHelper.setPrivateValue(NetHandlerPlayServer.class, ((EntityPlayerMP)player).connection, 0, new String[]{"field_147365_f", "floatingTickCount"});
		}
	}
	
	/**
	 * This method feeds player with food, if food heal amount more than
	 * empty food gaps, then reminder adds to saturation
	 * @param player
	 * @param food
	 * @return result of eating food
	 */
	public static ActionResult<ItemStack> canEat(EntityPlayer player, ItemStack food) {
		if (!(food.getItem() instanceof ItemFood)) return new ActionResult<ItemStack>(EnumActionResult.FAIL, food);
		ItemFood foodItem = (ItemFood) food.getItem();
		if (player.getFoodStats().needFood()) {
			food.setCount(food.getCount() - 1);
			float saturation = foodItem.getSaturationModifier(food);
			int hunger = 20 - player.getFoodStats().getFoodLevel();
			saturation += (hunger - foodItem.getHealAmount(food)) < 0 ? foodItem.getHealAmount(food) - hunger : 1.0F;
			player.getFoodStats().addStats(foodItem.getHealAmount(food), saturation);
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, food);
		} else {
			return new ActionResult<ItemStack>(EnumActionResult.FAIL, food);
		}
	}
	
	/**
	 * Check if current key being pressed right now on both sides
	 * @param player
	 * @param key
	 * @return true if key currently pressed
	 */
	public static boolean isKeyDown(EntityPlayer player, EnumKey type) {
		if (SIDE.isClient()) {
			return Keybinds.REGISTERY.get(type.getID()).state;
		} else {
			if (Keybinds.PLAYER_KEYS.get(player) == null) return false;
			List<Key> playerKeys = Keybinds.PLAYER_KEYS.get(player);
			if (playerKeys.isEmpty()) return false;
			return playerKeys.get(type.getID()).state;
		}
	}
	
	/**
	 * Equalize list of recipe outputs
	 * @throws StackOverflowException if there is a lot of lists
	 * @param recipes - List of recipes outputs
	 * @return List of items contains only common items from all recipes
	 */
	public static List<ItemStack> equalizeRecipes(List<List<ItemStack>> recipes) {
		List<ItemStack> result = new ArrayList<>();
		List<List<ItemStack>> recipes_compared = new ArrayList<>();
		int rec_size = recipes.size();
		for (int i = 0; i < (rec_size % 2 == 1 ? rec_size - 1 : rec_size); i += 2)
			recipes_compared.add(compareStackLists(recipes.get(i), recipes.get(i + 1)));
		if (rec_size % 2 == 1) recipes_compared.add(recipes.get(rec_size - 1));
		if (recipes_compared.size() > 1)
			result = equalizeRecipes(recipes_compared);
		else
			if (recipes_compared.size() != 0) result = recipes_compared.get(0);
		return result;
	}

	/**
     * This method comparing two lists of ItemStacks
     * @param sample1 - list to compare
     * @param sample2 - list to compare
     * @return List of common items from both lists with common amount
     */
	public static List<ItemStack> compareStackLists(List<ItemStack> sample1, List<ItemStack> sample2) {
		List<ItemStack> result = new ArrayList<>();
		if (sample1 == null || sample2 == null) {
			NPULog.warn("ERROR while comparing lists! list == null", new IllegalArgumentException());
			return new ArrayList<>();
		}
		if (sample1.isEmpty()) return sample2;
		if (sample2.isEmpty()) return sample1;
		for (ItemStack item1 : sample1) {
			for (ItemStack item2 : sample2) {
				if (item1.isItemEqual(item2)) {
					ItemStack resulting = item1.copy();
					resulting.setCount(Math.min(item1.getCount(), item2.getCount()));
					result.add(resulting);
				}
			}
		}
		return result;
	}
	
	/** 
	 * @debug Logging itemstack list in correct form
	 * [ 1x item.getDisplayName() ]
	 * @param List to print
	 */
	public static void printStackList(List<ItemStack> list) {
		String result = "[ ";
		int index = 1;
		for (ItemStack item : list) {
			result += item.getCount() + "x " + item.getDisplayName();
			if (index == list.size()) {
				result += " ]";
			} else {
				result += ", ";
			}
			++index;
		}
		NPULog.info(result);
	}
	
	public static void printFluidList(List<FluidStack> list) {
		String result = "[ ";
		int index = 1;
		for (FluidStack fluid : list) {
			result += fluid.amount + "x " + fluid.getLocalizedName();
			if (index == list.size()) {
				result += " ]";
			} else {
				result += ", ";
			}
			++index;
		}
		NPULog.info(result);
	}
	
	/**
	 *  Getting items for recipe input of map of item
	 * @param map - RecipeMap, where will be searching
	 * @param item - item, which recipes will be found
	 * @return List of Recipes
	 */
	public static List<Recipe> getGTRecipeByOutput(RecipeMap<?> map, ItemStack item) {
		Iterator<Recipe> recipes = map.getRecipeList().iterator();
		List<Recipe> result = new ArrayList<>();
		while (recipes.hasNext()) {
			Recipe current = recipes.next();
			NonNullList<ItemStack> outputs = current.getOutputs();
			Iterator<ItemStack> items = outputs.iterator();
			while (items.hasNext()) {
				ItemStack currentI = items.next();
				if (currentI.isItemEqual(item)) {
					result.add(current);
				}
			}
		}
		return result;
	}
	
	/**
	 * Converts GT Recipe to List of needed stacks
	 * @param recipe
	 * @return List of items of input of recipe
	 */
	public static List<ItemStack> parseGTRecipe(Recipe recipe) {
		List<ItemStack> result = new ArrayList<>();
		Iterator<CountableIngredient> inputs = recipe.getInputs().iterator();
		while (inputs.hasNext()) {
			CountableIngredient ingredient = inputs.next();
			int amount = ingredient.getCount();
			ItemStack item;
			if (ingredient.getIngredient().getMatchingStacks() == null) continue;
			if (ingredient.getIngredient().getMatchingStacks().length == 0) continue;
			item = ingredient.getIngredient().getMatchingStacks()[0].copy();
			item.setCount(amount);
			result.add(item);
		}
		return result;
	}
	
	/**
	 * Searching in REGISTRY recipe of stack item
	 * @param stack
	 * @return Recipe of stack as output
	 */
	public static List<IRecipe> getRecipesByOutput(ItemStack stack) {
		List<IRecipe> result = new ArrayList<>();
		for (IRecipe recipe : CraftingManager.REGISTRY) {
			if (recipe.getRecipeOutput().isItemEqual(stack)) {
				result.add(recipe);
			}
		}
		return result;
	}
	
	/**
	 * @param recipe
	 * @return List of items needed for craft
	 */
	public static List<ItemStack> getRecipeInputs(IRecipe recipe) {
		List<ItemStack> result = new ArrayList<>();
		NonNullList<Ingredient> ingredients = recipe.getIngredients();
		for (int i = 0; i < ingredients.size(); i++) {
			if (ingredients.get(i) == Ingredient.EMPTY) continue;
			if (ingredients.get(i).getMatchingStacks().length < 1) continue;
			if (!ingredients.get(i).getMatchingStacks()[0].isEmpty())
				result.add(ingredients.get(i).getMatchingStacks()[0]);
		}
		return result;
	}
	
	/**
	 * Format itemstacks list from [1xitem@1, 1xitem@1, 1xitem@2] to
	 * [2xitem@1, 1xitem@2]
	 * @param input
	 * @return Formated list
	 */
	public static List<ItemStack> format(List<ItemStack> input) {
		Map<ItemStackKey, Integer> items = new HashMap<>();
		List<ItemStack> output = new ArrayList<>();
		Iterator<ItemStack> iter = input.iterator();
		while (iter.hasNext()) {
			ItemStackKey current = new ItemStackKey(iter.next());
			if (items.containsKey(current)) {
				int amount = items.get(current);
				items.replace(current, ++amount);
			} else {
				items.put(current, 1);
			}
		}
		Iterator<Entry<ItemStackKey, Integer>> iter1 = items.entrySet().iterator();
		while (iter1.hasNext()) {
			Entry<ItemStackKey, Integer> entry = iter1.next();
			ItemStack stack = entry.getKey().getItemStack();
			stack.setCount(entry.getValue());
			output.add(stack);
		}
		return output;		
	}
	
	/**
	 * Format itemstack list from [2xitem@1, 1xitem@2] to
	 * [1xitem@1, 1xitem@1, 1xitem@2]
	 * @param recipe
	 * @return Formated list
	 */
	public static List<ItemStack> finalizeRecipe(List<ItemStack> recipe) {
		List<ItemStack> result = new ArrayList<>();
		Iterator<ItemStack> iter = recipe.iterator();
		while (iter.hasNext()) {
			ItemStack listItem = iter.next();
			if (listItem.getCount() == 1) {
				result.add(listItem);
			} else {
				ItemStack item = listItem.copy();
				item.setCount(1);
				for (int i = 0; i < listItem.getCount(); i++)
					result.add(item);
			}
		}
		return result;
	}
	
	/**
	 * Return comforatble format of pos.toString()
	 * @param pos - BlockPos
	 * @return String - (x=*, y=*, z=*)
	 */
	public static String posToString(BlockPos pos) {
		if (pos == null) return "null";
		return String.format("(x=%s, y=%s, z=%s)", pos.getX(), pos.getY(), pos.getZ());
	}
	
	/**
	 * Same like posToString() but with red color coordinated numbers
	 * @param pos - BlockPos
	 * @return String - (x=*, y=*, z=*)
	 */
	public static String posToStringC(BlockPos pos) {
		if (pos == null) return "§4null";
		return String.format("(x=§c%s§r, y=§c%s§r, z=§c%s§r)", pos.getX(), pos.getY(), pos.getZ());
	}
	
	public static String format(long value) {
		return new DecimalFormat("###,###.##").format(value);
	}

	public static String format(double value) {
		return new DecimalFormat("###,###.##").format(value);
	}
	
	/**
	 * Modular HUD class for armor
	 * now available only string rendering, if will be needed,
	 * may be will add some additional functions
	 */
	@SideOnly(Side.CLIENT)
	public static class ModularHUD {
		private byte stringAmount = 0;
		private List<String> stringList;
		private final static Minecraft mc = Minecraft.getMinecraft();
		
		public ModularHUD() {
			this.stringList = new ArrayList<String>();
		}
		
		public void newString(String string) {
			this.stringAmount++;
			this.stringList.add(string);
		}
		
		public void draw() {
			for (int i = 0; i < stringAmount; i++) {
				Pair<Integer, Integer> coords = this.getStringCoord(i);
				mc.ingameGUI.drawString(mc.fontRenderer, stringList.get(i), coords.getLeft(), coords.getRight(), 0xFFFFFF);
			}
		}
		
		private Pair<Integer, Integer> getStringCoord(int index) {
			int posX = 0;
			int posY = 0;
			int fontHeight = mc.fontRenderer.FONT_HEIGHT;
			int windowHeight = new ScaledResolution(mc).getScaledHeight();
			int windowWidth = new ScaledResolution(mc).getScaledWidth();
			int stringWidth = mc.fontRenderer.getStringWidth(stringList.get(index));
			switch(NPUConfig.Misc.hudLocation) {
			case 1:
				posX = 1 + NPUConfig.Misc.hudOffsetX;
				posY = 1 + NPUConfig.Misc.hudOffsetY + (fontHeight * index);
				break;
			case 2:
				posX = windowWidth - (1 + NPUConfig.Misc.hudOffsetX) - stringWidth;
				posY = 1 + NPUConfig.Misc.hudOffsetY + (fontHeight * index);
				break;
			case 3:
				posX = 1 + NPUConfig.Misc.hudOffsetX;
				posY = windowHeight - fontHeight * (stringAmount - index) - 1 - NPUConfig.Misc.hudOffsetY;
				break;
			case 4:
				posX = windowWidth - (1 + NPUConfig.Misc.hudOffsetX) - stringWidth;
				posY = windowHeight - fontHeight * (stringAmount - index) - 1 - NPUConfig.Misc.hudOffsetY;
				break;
			default:
				NPULog.error("Illegal argument for HUD position!");
				throw new IllegalArgumentException();
			}
			return Pair.of(posX, posY);
		}
		
		public void reset() {
			this.stringAmount = 0;
			this.stringList.clear();
		}
		
		public int getStringAmount() {
			return this.stringAmount;
		}
		
		public List<String> getStringList() {
			return this.stringList;
		}
	}
	
	/**
	 * Made to be generic object, which can store any objects
	 * and using generics ex. <? exteds ObjectName>
	 * @param <T>
	 */
	public static class Holder<T> {
		private T object;
		
		public Holder(@Nonnull T object) {
			this.object = object;
		}
		
		@Nonnull
		public T get() {
			return this.object;
		}
	}
}