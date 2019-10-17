package joshie.harvest.api.cooking;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.LinkedHashMap;
import java.util.Map;

public class Utensil {
    public static final Map<ResourceLocation, Utensil> REGISTRY = new LinkedHashMap<>();
    private final ResourceLocation resource;
    private final String unlocalized;
    private final String burntName;
    private ItemStack burntItem;

    public Utensil(ResourceLocation resource) {
        this.resource = resource;
        this.unlocalized = resource.getResourceDomain() + ".cookware." + resource.getResourcePath().replace("_", ".");
        this.burntName = resource.getResourceDomain() + ".meal.burnt." + resource.getResourcePath().replace("_", ".");
        REGISTRY.put(resource, this);
    }

    public Utensil setBurntItem(@Nonnull ItemStack burntItem) {
        this.burntItem = burntItem;
        return this;
    }

    @Nonnull
    public ItemStack getBurntItem() {
        return burntItem;
    }

    public ResourceLocation getResource() {
        return resource;
    }

    /** Return the resource location to use for this utensil
     *  when a recipe is burnt */
    @SideOnly(Side.CLIENT)
    public ModelResourceLocation getModelForMeal() {
        return new ModelResourceLocation(getResource().getResourceDomain() + ":meal", "burnt_" + getResource().getResourcePath());
    }

    @SuppressWarnings("deprecation")
    public String getLocalizedName() {
        return I18n.translateToLocal(unlocalized);
    }

    /** Return the unlocalized name for this utensil when it's burnt **/
    @SuppressWarnings("deprecation")
    public String getBurntName() {
        return I18n.translateToLocal(burntName);
    }
}