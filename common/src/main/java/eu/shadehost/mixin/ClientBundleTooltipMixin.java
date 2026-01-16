package eu.shadehost.mixin;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientBundleTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
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
            method = "renderBundleWithItemsTooltip",
            at = @At("STORE"),
            ordinal = 0)
    private boolean disableSurplusText(boolean bl) {
        return false;
    }

    @Inject(
            method = "renderImage",
            at = @At("RETURN")
    )
    private void renderNestedBundleTooltip(Font font, int x, int y, int width, int height, GuiGraphics guiGraphics, CallbackInfo ci) {
        if (!this.contents.hasSelectedItem()) {
            return;
        }

        ItemStack selectedItem = this.contents.getItemUnsafe(this.contents.getSelectedItem());
        BundleContents nestedBundle = selectedItem.get(DataComponents.BUNDLE_CONTENTS);

        if (nestedBundle != null && !nestedBundle.isEmpty()) {
            ClientBundleTooltip nestedTooltip = new ClientBundleTooltip(nestedBundle);

            int nestedX = x + width;
            int nestedY = y;

            Identifier tooltipStyle = selectedItem.get(DataComponents.TOOLTIP_STYLE);

            guiGraphics.renderTooltip(
                    font,
                    List.of(nestedTooltip),
                    nestedX,
                    nestedY,
                    DefaultTooltipPositioner.INSTANCE,
                    tooltipStyle
            );
        }
    }
}
