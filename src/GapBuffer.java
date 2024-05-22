import java.util.Arrays;

public class GapBuffer {

    private static final int DEFAULT_SIZE = 10;
    private static final char EMPTY_CHAR = '\u0000';

    private char[] data;
    private int size;
    private final int gapLength;
    private int gapStart;
    private int gapEnd;

    public GapBuffer() {
        this(DEFAULT_SIZE);
    }

    public GapBuffer(int gapLenght) {
        this.gapLength = gapLenght;
        this.data = new char[this.gapLength];
        this.gapStart = 0;
        this.gapEnd = this.data.length - 1;
        Arrays.fill(data, gapStart, gapEnd, EMPTY_CHAR);
    }


    public void add(char c) {
        add(size(), c);
    }

    public void add(int index, char c) {
        checkCapacity();
        moveGap(index);
        this.data[gapStart] = c;
        this.gapStart++;
        this.size++;
    }

    public void remove(int index) {
        if(size == 0){
            return;
        }
        moveGap(index);
        this.data[++gapEnd] = EMPTY_CHAR;
        this.size--;
    }

    private void checkCapacity() {
        int left = data.length - size();
        if (left <= 1) {
            char[] newArray = new char[data.length + gapLength];
            moveGap(size());
            System.arraycopy(data, 0, newArray, 0, data.length);
            data = newArray;
            gapStart = gapEnd;
            gapEnd = newArray.length - 1;
        }
    }

    private void moveGap(int index) {
        if (index > gapStart) {
            int count = index - gapStart;
            System.arraycopy(data, gapEnd + 1, data, gapStart, count);
            gapStart = index;
            gapEnd = gapEnd + count;
            Arrays.fill(data, gapStart, gapEnd, EMPTY_CHAR);
        } else if (index < gapStart) {
            int count = gapStart - index;
            System.arraycopy(data, index, data, gapEnd - count + 1, count);
            gapStart = index;
            gapEnd = gapEnd - count;
            Arrays.fill(data, gapStart, gapEnd, EMPTY_CHAR);
        }
    }

    public int size() {
        return this.size;
    }

    @Override
    public String toString() {
        String result = new String(data);
        result = result.replace("\u0000", "");
        return result;
    }

}
