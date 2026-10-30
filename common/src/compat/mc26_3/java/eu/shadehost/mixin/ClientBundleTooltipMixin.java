package eu.shadehost.mixin;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientBundleTooltip;
import eu.shadehost.NestedTooltipPositioner;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.List;
/** Minecraft 26.3+ variant (tooltip() has an extra boolean) (GuiGraphicsExtractor, ItemStackTemplate). */
@Mixin(ClientBundleTooltip.class)
public class ClientBundleTooltipMixin {
    @Final
    @Shadow
    private BundleContents contents;
    @Inject(method = "slotCount", at = @At("HEAD"), cancellable = true)
    private void showAllItems(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(this.contents.size());
    }
    @ModifyVariable(
            method = "extractBundleWithItemsTooltip",
            at = @At("STORE"),
            ordinal = 0)
    private boolean disableSurplusText(boolean bl) {
        return false;
    }
    @Inject(
            method = "extractImage",
            at = @At("RETURN")
    )
    private void renderNestedBundleTooltip(Font font, int x, int y, int width, int height, GuiGraphicsExtractor graphics, CallbackInfo ci) {
        if (this.contents.getSelectedItemIndex() == BundleContents.NO_SELECTED_ITEM_INDEX) {
            return;
        }
        ItemStack selectedItem = this.contents.getSelectedItem().create();
        BundleContents nestedBundle = selectedItem.get(DataComponents.BUNDLE_CONTENTS);
        if (nestedBundle != null && !nestedBundle.isEmpty()) {
            ClientBundleTooltip nestedTooltip = new ClientBundleTooltip(nestedBundle);
            Identifier tooltipStyle = selectedItem.get(DataComponents.TOOLTIP_STYLE);
            graphics.tooltip(
                    font,
                    List.of(nestedTooltip),
                    x + width,
                    y,
                    new NestedTooltipPositioner(x, width),
                    tooltipStyle,
                    false
            );
        }
    }
}