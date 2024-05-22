import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextHitInfo;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class TextEdit extends JPanel implements KeyListener {

    private final Font defaultFont = new Font("Verdana",0,24);


    private float rx,ry;
    private final GapBuffer content;
    private int cursorPosition;
    private Font font;

    public TextEdit() {
        this.content = new GapBuffer();
        this.cursorPosition = 0;
        this.setFocusable(true);
        this.requestFocusInWindow();
        this.font = defaultFont;
        addKeyListener(this);
    }

    private void clampCursorPosition(){
        if (cursorPosition < 0) {
            cursorPosition = 0;
        } else if (cursorPosition > this.content.size()) {
            cursorPosition = this.content.size();
        }
    }

    @Override
    public void update(Graphics g) {
        g.clearRect(0, 0, getWidth(), getHeight());
        paintComponent(g);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g.setFont(font);

        ry = (float) 3 * getHeight() / 4;
        if(!this.content.toString().isEmpty()) {

            TreeMap<Integer, TextLayout> layoutTreeMap = new TreeMap<>();
            AttributedCharacterIterator paragraph = new AttributedString(this.content.toString()).getIterator();
            int paragraphStart = paragraph.getBeginIndex();
            int paragraphEnd = paragraph.getEndIndex();
            FontRenderContext frc = g.getFontMetrics().getFontRenderContext();
            LineBreakMeasurer lineMeasurer = new LineBreakMeasurer(paragraph, frc);
            float breakWidth = (float) getSize().width;
            float drawPosY = 0;
            lineMeasurer.setPosition(paragraphStart);

            while (lineMeasurer.getPosition() < paragraphEnd) {
                int layoutStartIndex = lineMeasurer.getPosition();
                int next = lineMeasurer.nextOffset(breakWidth);
                int limit = next;
                if (limit <= this.content.toString().length()) {
                    for (int i = lineMeasurer.getPosition(); i < next; ++i) {
                        char c = this.content.toString().charAt(i);
                        if (c == '\n') {
                            limit = i + 1;
                            break;
                        }
                    }
                }

                TextLayout layout = lineMeasurer.nextLayout(breakWidth,limit,false);

                layoutTreeMap.put(layoutStartIndex, layout);

                float drawPosX = layout.isLeftToRight() ? 0 : breakWidth - layout.getAdvance();
                AffineTransform at = AffineTransform.getTranslateInstance(drawPosX, drawPosY);
                drawPosY += layout.getAscent();
                layout.draw((Graphics2D) g, drawPosX, drawPosY);
                g.setColor(getForeground());
                drawPosY += layout.getDescent() + layout.getLeading();
            }

            if(!layoutTreeMap.isEmpty()){
                Entry<Integer,TextLayout> currentLayoutEntry = layoutTreeMap.floorEntry(cursorPosition);
                TextLayout currentLayout = currentLayoutEntry.getValue();
                int cursorPositionOffset = cursorPosition - currentLayoutEntry.getKey();
                AffineTransform at = AffineTransform.getTranslateInstance(currentLayout.getBounds().getX(), currentLayout.getBounds().getY());

                Shape[] carets = currentLayout.getCaretShapes(cursorPositionOffset);
                Shape caret = at.createTransformedShape(carets[0]);
                g2.setColor(getForeground());
                g2.draw(caret);
            }


//            lineMeasurer.setPosition(cursorPosition == paragraphEnd ? cursorPosition - 1 : cursorPosition);
//            int next = lineMeasurer.nextOffset(breakWidth);
//            int limit = next;
//            if (limit <= this.content.toString().length()) {
//                for (int i = lineMeasurer.getPosition(); i < next; ++i) {
//                    char c = this.content.toString().charAt(i);
//                    if (c == '\n') {
//                        limit = i + 1;
//                        break;
//                    }
//                }
//            }
//            TextLayout layout = lineMeasurer.nextLayout(breakWidth,limit,false);
//            TextHitInfo nextHit = layout.getNextRightHit(cursorPosition == paragraphEnd ? cursorPosition - 1 : cursorPosition);
//            if (nextHit != null) {
//                // translate graphics to origin of layout on screen
//                g.translate((int) layout.getBounds().getX(), (int) layout.getBounds().getY());
//                Shape[] carets = layout.getCaretShapes(nextHit.getInsertionIndex());
//                ((Graphics2D) g).draw(carets[0]);
//                if (carets[1] != null) {
//                    ((Graphics2D) g).draw(carets[1]);
//                }
//            }
        }

    }

    @Override
    public void keyTyped(KeyEvent e) {
        clampCursorPosition();
        if (e.getKeyChar() != '\b') {
            this.content.add(cursorPosition, e.getKeyChar());
            cursorPosition++;
        } else {
            if (cursorPosition > 0) {
                this.content.remove(cursorPosition - 1);
            } else {
                this.content.remove(cursorPosition);
            }
            cursorPosition--;
        }
        this.repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.isActionKey()) {
            if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                cursorPosition--;
            }
            if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                cursorPosition++;
            }
        }
        clampCursorPosition();
        this.repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public Font getFont() {
        return font;
    }

    @Override
    public void setFont(Font font) {
        this.font = font;
    }
}
