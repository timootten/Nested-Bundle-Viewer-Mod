package eu.shadehost.mixin;

import eu.shadehost.BundleLayout;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientBundleTooltip;
import net.minecraft.world.item.component.BundleContents;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Makes the bundle grid wider instead of only taller (26.x method names).
 * Replaces the hardcoded vanilla 4 columns / 96px with {@link BundleLayout} values.
 */
@Mixin(ClientBundleTooltip.class)
public class BundleLayoutMixin {

    @Final
    @Shadow
    private BundleContents contents;

    @Inject(method = "extractImage", at = @At("HEAD"))
    private void nestedBundleViewer$beginRender(CallbackInfo ci) {
        BundleLayout.beginRender(this.contents.size());
    }

    @ModifyConstant(method = "getWidth", constant = @Constant(intValue = 96))
    private int nestedBundleViewer$width(int original) {
        return BundleLayout.widthFor(this.contents.size());
    }

    @ModifyConstant(method = "gridSizeY", constant = @Constant(intValue = 4))
    private int nestedBundleViewer$gridColumns(int original) {
        return BundleLayout.columnsFor(this.contents.size());
    }

    @ModifyConstant(method = "getContentXOffset", constant = @Constant(intValue = 96))
    private static int nestedBundleViewer$contentWidth(int original) {
        return BundleLayout.renderWidth();
    }

    @ModifyConstant(method = "extractBundleWithItemsTooltip", constant = @Constant(intValue = 4, ordinal = 0))
    private int nestedBundleViewer$renderColumns(int original) {
        return BundleLayout.renderColumns();
    }

    @ModifyConstant(method = "extractBundleWithItemsTooltip", constant = @Constant(intValue = 96))
    private int nestedBundleViewer$renderWidth(int original) {
        return BundleLayout.renderWidth();
    }

    @ModifyConstant(method = "extractProgressbar", constant = @Constant(intValue = 96))
    private static int nestedBundleViewer$barWidth(int original) {
        return BundleLayout.renderWidth();
    }

    @ModifyConstant(method = "extractProgressbar", constant = @Constant(intValue = 48))
    private static int nestedBundleViewer$barTextCenter(int original) {
        return BundleLayout.renderWidth() / 2;
    }

    @ModifyConstant(method = "getProgressBarFill", constant = @Constant(intValue = 94))
    private static int nestedBundleViewer$barFillMax(int original) {
        return BundleLayout.renderWidth() - 2;
    }
}
