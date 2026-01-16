package eu.shadehost.mixin;

import eu.shadehost.state.BundleNavigationState;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientBundleTooltip;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BundleContents;
import org.apache.commons.lang3.math.Fraction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientBundleTooltip.class)
public class ClientBundleTooltipMixin {

    @Shadow
    private BundleContents contents;

    private static final ResourceLocation BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("container/bundle/background");
    private static final ResourceLocation SLOT_SPRITE = ResourceLocation.withDefaultNamespace("container/bundle/slot");
    private static final ResourceLocation BLOCKED_SLOT_SPRITE = ResourceLocation.withDefaultNamespace("container/bundle/blocked_slot");

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

    @Inject(method = "renderImage", at = @At("HEAD"))
    private void setActiveBundle(Font font, int x, int y, GuiGraphics guiGraphics, CallbackInfo ci) {
        BundleNavigationState.setActive(this.contents);
    }

    @Inject(method = "renderImage", at = @At("HEAD"), cancellable = true)
    private void renderCustomBundle(Font font, int x, int y, GuiGraphics guiGraphics, CallbackInfo ci) {
        int gridSize = (int) Math.ceil(Math.sqrt(this.contents.size() + 1));
        int gridX = Math.min(8, gridSize);
        int gridY = Math.min(8, gridSize);

        int backgroundWidth = gridX * 18 + 2;
        int backgroundHeight = gridY * 20 + 2;

        guiGraphics.blitSprite(BACKGROUND_SPRITE, x, y, backgroundWidth, backgroundHeight);

        boolean isFull = this.contents.weight().compareTo(Fraction.ONE) >= 0;
        int index = 0;

        for (int row = 0; row < gridY; row++) {
            for (int col = 0; col < gridX; col++) {
                int posX = x + col * 18 + 1;
                int posY = y + row * 20 + 1;

                this.renderSlotCustom(posX, posY, index, isFull, guiGraphics, font);
                index++;
            }
        }

        ci.cancel();
    }

    private void renderSlotCustom(int x, int y, int index, boolean bl, GuiGraphics guiGraphics, Font font) {
        if (index >= this.contents.size()) {
            guiGraphics.blitSprite(bl ? BLOCKED_SLOT_SPRITE : SLOT_SPRITE, x, y, 0, 18, 20);
        } else {
            ItemStack itemStack = this.contents.getItemUnsafe(index);
            guiGraphics.blitSprite(SLOT_SPRITE, x, y, 0, 18, 20);
            guiGraphics.renderItem(itemStack, x + 1, y + 1, index);
            guiGraphics.renderItemDecorations(font, itemStack, x + 1, y + 1);

            // Highlight das ausgewählte Item (nur wenn es ein echtes Item ist)
            if (index == BundleNavigationState.getSelectedIndex() && index < this.contents.size()) {
                AbstractContainerScreen.renderSlotHighlight(guiGraphics, x + 1, y + 1, 0);
            }
        }
    }

    @Inject(method = "renderImage", at = @At("RETURN"))
    private void renderNestedBundleTooltip(Font font, int x, int y, GuiGraphics guiGraphics, CallbackInfo ci) {
        int selectedIndex = BundleNavigationState.getSelectedIndex();

        // Wichtig: Prüfe ob selectedIndex im gültigen Bereich ist
        if (selectedIndex >= 0 && selectedIndex < this.contents.size()) {
            ItemStack selectedItem = this.contents.getItemUnsafe(selectedIndex);

            if (selectedItem.is(Items.BUNDLE)) {
                BundleContents nestedContents = selectedItem.get(DataComponents.BUNDLE_CONTENTS);

                if (nestedContents != null && nestedContents.size() > 0) {
                    int gridSize = (int) Math.ceil(Math.sqrt(this.contents.size() + 1));
                    int mainWidth = Math.min(8, gridSize) * 18 + 2;
                    int nestedX = x + mainWidth + 8;

                    int nestedGridSize = (int) Math.ceil(Math.sqrt(nestedContents.size() + 1));
                    nestedGridSize = Math.min(8, nestedGridSize);

                    int nestedWidth = nestedGridSize * 18 + 2;
                    int nestedHeight = nestedGridSize * 20 + 2;

                    Component bundleName = selectedItem.getHoverName();
                    int textWidth = font.width(bundleName);
                    int boxWidth = Math.max(nestedWidth, textWidth + 8);

                    guiGraphics.fill(nestedX - 2, y - 14, nestedX + boxWidth + 2, y - 2, 0xF0100010);
                    guiGraphics.renderOutline(nestedX - 2, y - 14, boxWidth + 4, 12, 0xFF8B4513);
                    guiGraphics.drawString(font, bundleName, nestedX + 2, y - 10, 0xFFFFFF, true);
                    guiGraphics.renderOutline(nestedX - 1, y - 1, nestedWidth + 2, nestedHeight + 2, 0xFF8B4513);
                    guiGraphics.blitSprite(BACKGROUND_SPRITE, nestedX, y, nestedWidth, nestedHeight);

                    boolean isFull = nestedContents.weight().compareTo(Fraction.ONE) >= 0;
                    int index = 0;

                    for (int row = 0; row < nestedGridSize; row++) {
                        for (int col = 0; col < nestedGridSize; col++) {
                            int slotX = nestedX + col * 18 + 1;
                            int slotY = y + row * 20 + 1;

                            if (index >= nestedContents.size()) {
                                guiGraphics.blitSprite(isFull ? BLOCKED_SLOT_SPRITE : SLOT_SPRITE, slotX, slotY, 0, 18, 20);
                            } else {
                                ItemStack stack = nestedContents.getItemUnsafe(index);
                                guiGraphics.blitSprite(SLOT_SPRITE, slotX, slotY, 0, 18, 20);

                                if (!stack.isEmpty()) {
                                    guiGraphics.renderItem(stack, slotX + 1, slotY + 1, index);
                                    guiGraphics.renderItemDecorations(font, stack, slotX + 1, slotY + 1);
                                }
                            }
                            index++;
                        }
                    }
                }
            }
        }

        BundleNavigationState.setInactive();
    }

    @Shadow
    private int gridSizeX() {
        throw new AssertionError();
    }
}
