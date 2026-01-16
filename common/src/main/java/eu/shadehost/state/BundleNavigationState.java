package eu.shadehost.state;

import net.minecraft.world.item.component.BundleContents;

public class BundleNavigationState {
    private static int selectedIndex = 0;
    private static int gridSize = 0;
    private static int totalItems = 0;
    private static BundleContents currentBundle = null;
    private static boolean isHovering = false;

    public static void setActive(BundleContents contents) {
        currentBundle = contents;
        totalItems = contents.size(); // Ohne +1, nur echte Items
        gridSize = (int) Math.ceil(Math.sqrt(contents.size() + 1));
        gridSize = Math.min(8, gridSize);
        isHovering = true;

        // Stelle sicher dass selectedIndex im gültigen Bereich ist
        if (selectedIndex >= totalItems) {
            selectedIndex = Math.max(0, totalItems - 1);
        }
    }

    public static void setInactive() {
        isHovering = false;
    }

    public static boolean isHovering() {
        return isHovering;
    }

    public static int getSelectedIndex() {
        return selectedIndex;
    }

    public static BundleContents getCurrentBundle() {
        return currentBundle;
    }

    public static void moveUp() {
        int newIndex = selectedIndex - gridSize;
        if (newIndex >= 0) {
            selectedIndex = newIndex;
        }
    }

    public static void moveDown() {
        int newIndex = selectedIndex + gridSize;
        if (newIndex < totalItems) {
            selectedIndex = newIndex;
        }
    }

    public static void moveLeft() {
        if (selectedIndex > 0) {
            selectedIndex--;
        }
    }

    public static void moveRight() {
        if (selectedIndex < totalItems - 1) {
            selectedIndex++;
        }
    }

    public static void reset() {
        selectedIndex = 0;
    }
}
