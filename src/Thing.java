import javax.swing.*;
import java.io.IOException;
import java.util.Scanner;

public class Thing {

    JFrame f;

    public Thing(){
        f = new JFrame("Thing");

        f.setSize(500,400);
        TextEdit editor = new TextEdit();

        editor.setBounds(0,0,f.getWidth(),f.getHeight());



        f.add(editor);

        f.setLayout(null);
        f.setVisible(true);
    }

    public static void main(String[] args) throws IOException {
        new Thing();
    }


}