package eu.shadehost.mixin;

import net.minecraft.world.item.component.BundleContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BundleContents.class)
public class BundleContentsMixin {

    @Inject(method = "getNumberOfItemsToShow", at = @At("HEAD"), cancellable = true)
    private void showAllItemsInBundle(CallbackInfoReturnable<Integer> cir) {
        BundleContents contents = (BundleContents) (Object) this;
        cir.setReturnValue(contents.size());
    }
}
