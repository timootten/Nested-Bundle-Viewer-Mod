package eu.shadehost;

import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import org.joml.Vector2i;
import org.joml.Vector2ic;

/**
 * Places the nested bundle tooltip next to the main bundle tooltip.
 * <p>
 * Vanilla's default positioner flips around the anchor point, which makes the nested tooltip
 * cover the main one once that grew wider. This one prefers the right side and, if there is
 * no room, flips around the <em>main tooltip's left edge</em> so the two never overlap.
 */
public final class NestedTooltipPositioner implements ClientTooltipPositioner {

    /** Distance between the tooltip content and its frame, as used by vanilla's default positioner. */
    private static final int OFFSET = 12;
    private static final int SCREEN_MARGIN = 4;

    private final int mainLeft;
    private final int mainWidth;

    /**
     * @param mainLeft  x of the main tooltip's content area
     * @param mainWidth width of the main tooltip's content area
     */
    public NestedTooltipPositioner(int mainLeft, int mainWidth) {
        this.mainLeft = mainLeft;
        this.mainWidth = mainWidth;
    }

    @Override
    public Vector2ic positionTooltip(int screenWidth, int screenHeight, int x, int y, int width, int height) {
        int right = this.mainLeft + this.mainWidth + OFFSET;
        int posX = right;
        if (posX + width > screenWidth - SCREEN_MARGIN) {
            posX = this.mainLeft - OFFSET - width;
        }
        posX = Math.max(SCREEN_MARGIN, posX);

        int posY = y - OFFSET;
        posY = Math.max(SCREEN_MARGIN, Math.min(posY, screenHeight - height - SCREEN_MARGIN));

        return new Vector2i(posX, posY);
    }
}
