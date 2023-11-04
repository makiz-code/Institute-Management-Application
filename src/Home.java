import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

public class Home extends JFrame{
    private JButton studentButton;
    private JButton professorButton;
    private JButton sectionButton;
    private JButton subjectButton;
    private JPanel HomePanel;
    private JButton exitButton;

    public Home() {
        setTitle("UTM");
        setContentPane(HomePanel);
        setBounds(510, 220, 500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setDefaultCloseOperation(this.DISPOSE_ON_CLOSE);
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("bank.png")));
        studentButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                close();
                new Student();
            }
        });
        professorButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                close();
                new Professor();
            }
        });
        sectionButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                close();
                new Section();
            }
        });
        subjectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                close();
                new Subject();
            }
        });
        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFrame frame = new JFrame();
                if (JOptionPane.showConfirmDialog(frame,"Confirm if you want to exit",
                        "Click a button",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
                    System.exit(0);
                }
            }
        });
    }
    public void close(){
        WindowEvent closeWindow = new WindowEvent(this,WindowEvent.WINDOW_CLOSING);
        Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(closeWindow);
    }
    public static void main(String[] args){
        new Home();
    }
}
