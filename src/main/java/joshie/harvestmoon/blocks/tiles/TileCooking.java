package joshie.harvestmoon.blocks.tiles;

import java.util.ArrayList;

import joshie.harvestmoon.cooking.FoodRegistry;
import joshie.harvestmoon.cooking.Utensil;
import joshie.harvestmoon.network.PacketHandler;
import joshie.harvestmoon.network.PacketSyncCooking;
import joshie.lib.helpers.StackHelper;
import joshie.lib.util.IFaceable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public abstract class TileCooking extends TileEntity implements IFaceable {
    public static short COOK_TIMER = 100;
    private boolean cooking;
    private short cookTimer = 0;
    private ArrayList<ItemStack> ingredients = new ArrayList();
    private ArrayList<ItemStack> seasonings = new ArrayList();
    private ItemStack result;
    private ForgeDirection orientation = ForgeDirection.NORTH;
    private float rotation;

    public TileCooking() {}

    public abstract Utensil getUtensil();

    public boolean hasPrerequisites() {
        return true;
    }

    public boolean canAddItems() {
        return result == null;
    }

    public ItemStack getStored() {
        return result;
    }

    public ArrayList<ItemStack> getIngredients() {
        return ingredients;
    }

    public ArrayList<ItemStack> getSeasonings() {
        return seasonings;
    }

    //reset everything ready for the next cooking batch
    public void clear() {
        result = null;
    }

    @Override
    public void setFacing(ForgeDirection dir) {
        orientation = dir;
    }

    @Override
    public ForgeDirection getFacing() {
        return orientation;
    }
    
    public float getRotation() {
        return rotation;
    }

    @Override
    public void updateEntity() {
        //If we are server side perform the actions
        if (!worldObj.isRemote) {
            if (cooking) {
                cookTimer++;
                if (cookTimer >= COOK_TIMER) {
                    result = FoodRegistry.getResult(getUtensil(), ingredients, seasonings);
                    cooking = false;
                    ingredients = new ArrayList();
                    seasonings = new ArrayList();
                    cookTimer = 0;
                    this.markDirty();
                }

                if (!hasPrerequisites()) {
                    cooking = false;
                    this.markDirty();
                }
            }
        } else if (cooking) { //If we are client side render some particles
            rotation += worldObj.rand.nextFloat();
            worldObj.spawnParticle("smoke", xCoord + 0.5D + +worldObj.rand.nextFloat() - worldObj.rand.nextFloat() / 2, yCoord + 0.5D + worldObj.rand.nextFloat() - worldObj.rand.nextFloat() / 2, zCoord + 0.5D + +worldObj.rand.nextFloat() - worldObj.rand.nextFloat() / 2, 0, 0, 0);
        }
    }

    //Returns true if this was a valid ingredient to add
    public boolean addIngredient(ItemStack stack) {
        if (ingredients.size() >= 9) return false;
        if (!hasPrerequisites()) return false;
        if (FoodRegistry.getIngredient(stack) == null) return false;
        else {
            if (worldObj.isRemote) return true;
            ItemStack clone = stack.copy();
            clone.stackSize = 1;
            this.ingredients.add(clone);
            this.cooking = true;
            this.cookTimer = 0;
            this.markDirty();
            return true;
        }
    }

    //Returns true if this was a valid seasoning to add
    public boolean addSeasoning(ItemStack stack) {
        if (!hasPrerequisites()) return false;
        if (FoodRegistry.getSeasoning(stack) == null) return false;
        else {
            if (worldObj.isRemote) return true;
            ItemStack clone = stack.copy();
            clone.stackSize = 1;
            this.seasonings.add(clone);
            this.cooking = true;
            this.cookTimer = Short.MIN_VALUE;
            this.markDirty();
            return true;
        }
    }

    //Called Clientside to update the client
    public void setFromPacket(boolean isCooking, ArrayList<ItemStack> ingredients, ArrayList<ItemStack> seasonings, ItemStack result) {
        this.cooking = isCooking;
        this.ingredients = ingredients;
        this.seasonings = seasonings;
        this.result = result;
    }

    public IMessage getPacket() {
        return new PacketSyncCooking(worldObj.provider.dimensionId, xCoord, yCoord, zCoord, orientation, cooking, ingredients, seasonings, result);
    }

    @Override
    public Packet getDescriptionPacket() {
        return PacketHandler.getPacket(getPacket());
    }

    @Override
    public void markDirty() {
        super.markDirty();

        if (!worldObj.isRemote) {
            PacketHandler.sendAround(getPacket(), this);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        orientation = ForgeDirection.getOrientation(nbt.getInteger("Orientation"));
        cooking = nbt.getBoolean("IsCooking");
        cookTimer = nbt.getShort("CookingTimer");
        if (nbt.hasKey("IngredientsInside")) {
            NBTTagList is = nbt.getTagList("IngredientsInside", 10);
            for (int i = 0; i < is.tagCount(); i++) {
                ingredients.add(StackHelper.getItemStackFromNBT(is.getCompoundTagAt(i)));
            }
        }

        if (nbt.hasKey("SeasoningsInside")) {
            NBTTagList ss = nbt.getTagList("SeasoningsInside", 10);
            for (int i = 0; i < ss.tagCount(); i++) {
                seasonings.add(StackHelper.getItemStackFromNBT(ss.getCompoundTagAt(i)));
            }
        }

        if (nbt.hasKey("Count")) {
            result = StackHelper.getItemStackFromNBT(nbt);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger("Orientation", orientation.ordinal());
        nbt.setBoolean("IsCooking", cooking);
        nbt.setShort("CookingTimer", cookTimer);
        //Write out the saved Ingredients
        if (ingredients.size() > 0) {
            NBTTagList is = new NBTTagList();
            for (ItemStack ingredient : ingredients) {
                is.appendTag(StackHelper.writeItemStackToNBT(new NBTTagCompound(), ingredient));
            }

            nbt.setTag("IngredientsInside", is);
        }

        //Write out the saved Seasonings
        if (seasonings.size() > 0) {
            NBTTagList ss = new NBTTagList();
            for (ItemStack seasoning : seasonings) {
                ss.appendTag(StackHelper.writeItemStackToNBT(new NBTTagCompound(), seasoning));
            }

            nbt.setTag("SeasoningsInside", ss);
        }

        if (result != null) {
            StackHelper.writeItemStackToNBT(nbt, result);
        }
    }
}