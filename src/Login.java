import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.logging.Level;

import static java.lang.Thread.sleep;

public class Login extends JFrame{
    private JTextField txtUserName;
    private JPasswordField txtPassword;
    private JPanel RegisterPanel;
    private JButton ConnectButton;
    private JButton cancelButton;
    private static final String url = "jdbc:mysql://localhost:3306/projet";
    private static final String user = "root";
    private static final String passwd = "7102";
    private int i=0;
    private int count=5000;
    public Login() {
        setTitle("UTM");
        setContentPane(RegisterPanel);
        setBounds(550, 240, 420, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("img/bank.png")));
        setVisible(true);
        setDefaultCloseOperation(this.DISPOSE_ON_CLOSE);
        ConnectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection conn = DriverManager.getConnection(url, user, passwd);
                    Statement state = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                    ResultSet res = state.executeQuery("select * from connection");
                    res.first();
                    if (!(txtUserName.getText()).equals(res.getString("user_name")) |
                             !(String.valueOf(txtPassword.getPassword())).equals(res.getString("password"))){
                        i++;
                        if(i<3) {
                            JFrame frame = new JFrame();
                            JOptionPane.showMessageDialog(frame, "Access Denied " + i + "/3",
                                 "Error", JOptionPane.ERROR_MESSAGE);
                        }
                        else {
                            ConnectButton.setEnabled(false);
                            JFrame frame = new JFrame();
                            JOptionPane.showMessageDialog(frame, "Access Denied for " + count/1000 + " seconds",
                                 "Error", JOptionPane.ERROR_MESSAGE);
                            try {
                                sleep(count);
                            } catch (InterruptedException ex) {
                                throw new RuntimeException(ex);
                            }
                            ConnectButton.setEnabled(true);
                            count*=2;
                        }
                    }else{
                        JFrame frame = new JFrame("Connected");
                        JOptionPane.showMessageDialog(frame, "Access Accepted");
                        close();
                        new Home();
                    }
                } catch (ClassNotFoundException | SQLException exp) {
                    java.util.logging.Logger.getLogger(Login.class.getName()).log(Level.SEVERE,null,exp);
                }
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }
    public void close(){
        WindowEvent closeWindow = new WindowEvent(this,WindowEvent.WINDOW_CLOSING);
        Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(closeWindow);
    }
    public static void main(String[] args){
        new Login();
    }
}
