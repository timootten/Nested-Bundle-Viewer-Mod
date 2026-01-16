package eu.shadehost.mixin;

import net.minecraft.client.gui.screens.inventory.tooltip.ClientBundleTooltip;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.world.item.component.BundleContents;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ClientBundleTooltip.class)
public class ClientBundleTooltipMixin {

    @Shadow
    private BundleContents contents;

    @Inject(method = "gridSizeX", at = @At("RETURN"), cancellable = true)
    private void squareGridX(CallbackInfoReturnable<Integer> cir) {
        int gridSize = (int) Math.ceil(Math.sqrt(this.contents.size() + 1));
        cir.setReturnValue(Math.min(8, gridSize));
    }

    @Inject(method = "gridSizeY", at = @At("RETURN"), cancellable = true)
    private void squareGridY(CallbackInfoReturnable<Integer> cir) {
        int gridSize = (int) Math.ceil(Math.sqrt(this.contents.size() + 1));
        cir.setReturnValue(Math.min(8, gridSize));
    }
}

