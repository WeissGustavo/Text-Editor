import java.awt.font.TextLayout;

public class IndexedTextLayout {

    private int index;
    private TextLayout layout;

    public IndexedTextLayout(int index, TextLayout layout) {
        this.index = index;
        this.layout = layout;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public TextLayout getLayout() {
        return layout;
    }

    public void setLayout(TextLayout layout) {
        this.layout = layout;
    }
}
