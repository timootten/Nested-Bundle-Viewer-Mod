package eu.shadehost;

/**
 * Layout rules for the bundle tooltip grid.
 * <p>
 * Vanilla hardcodes 4 columns (96px). Because this mod shows every item, the grid grows
 * wider instead: more items means more columns, limited by the screen size.
 */
public final class BundleLayout {

    public static final int SLOT_SIZE = 24;
    public static final int MIN_COLUMNS = 4;
    public static final int MAX_COLUMNS = 12;

    /** Column count of the tooltip currently being rendered (render thread only). */
    private static int renderColumns = MIN_COLUMNS;

    private BundleLayout() {
    }

    public static int columnsFor(int itemCount) {
        int columns = (int) Math.ceil(Math.sqrt(Math.max(itemCount, 1)));
        return Math.max(MIN_COLUMNS, Math.min(MAX_COLUMNS, columns));
    }

    public static int widthFor(int itemCount) {
        return columnsFor(itemCount) * SLOT_SIZE;
    }

    /** Must be called at the start of every tooltip render so static layout helpers know the size. */
    public static void beginRender(int itemCount) {
        renderColumns = columnsFor(itemCount);
    }

    public static int renderColumns() {
        return renderColumns;
    }

    public static int renderWidth() {
        return renderColumns * SLOT_SIZE;
    }
}
