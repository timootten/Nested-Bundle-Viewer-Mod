package eu.shadehost.mixin;

import net.minecraft.client.gui.screens.inventory.tooltip.ClientBundleTooltip;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.math.Fraction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Shows the bundle fullness as "used/64" on the progress bar (26.x: static method taking the weight).
 * Vanilla only renders text when the bundle is empty or full; those texts are kept.
 */
@Mixin(ClientBundleTooltip.class)
public class BundleProgressTextMixin {

    private static final int BUNDLE_CAPACITY = 64;

    @Inject(method = "getProgressBarFillText", at = @At("RETURN"), cancellable = true)
    private static void nestedBundleViewer$showFullness(Fraction weight, CallbackInfoReturnable<Component> cir) {
        if (cir.getReturnValue() != null) {
            return;
        }
        int used = weight.multiplyBy(Fraction.getFraction(BUNDLE_CAPACITY, 1)).intValue();
        cir.setReturnValue(Component.literal(used + "/" + BUNDLE_CAPACITY));
    }
}
