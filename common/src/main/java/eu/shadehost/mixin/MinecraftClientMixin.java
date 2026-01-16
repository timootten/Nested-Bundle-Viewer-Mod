package eu.shadehost.mixin;

import eu.shadehost.state.BundleNavigationState;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftClientMixin {

    private boolean upPressed = false;
    private boolean downPressed = false;
    private boolean leftPressed = false;
    private boolean rightPressed = false;

    @Inject(method = "tick", at = @At("HEAD"))
    private void handleBundleNavigation(CallbackInfo ci) {
        Minecraft mc = (Minecraft)(Object)this;

        if (BundleNavigationState.isHovering()) {
            long window = mc.getWindow().getWindow();

            boolean upNow = GLFW.glfwGetKey(window, GLFW.GLFW_KEY_UP) == GLFW.GLFW_PRESS;
            boolean downNow = GLFW.glfwGetKey(window, GLFW.GLFW_KEY_DOWN) == GLFW.GLFW_PRESS;
            boolean leftNow = GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT) == GLFW.GLFW_PRESS;
            boolean rightNow = GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT) == GLFW.GLFW_PRESS;

            if (upNow && !upPressed) {
                BundleNavigationState.moveUp();
            }
            if (downNow && !downPressed) {
                BundleNavigationState.moveDown();
            }
            if (leftNow && !leftPressed) {
                BundleNavigationState.moveLeft();
            }
            if (rightNow && !rightPressed) {
                BundleNavigationState.moveRight();
            }

            upPressed = upNow;
            downPressed = downNow;
            leftPressed = leftNow;
            rightPressed = rightNow;
        } else {
            upPressed = false;
            downPressed = false;
            leftPressed = false;
            rightPressed = false;
        }
    }
}
