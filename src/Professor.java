import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;
import java.util.logging.Level;

public class Professor extends JFrame{
    private JTextField txtProfessorID;
    private JTextField txtFirstName;
    private JTextField txtLastName;
    private JTable ProfessorTable;
    private JButton insertButton;
    private JButton deleteButton;
    private JButton updateButton;
    private JButton emptyButton;
    private JButton resetButton;
    private JComboBox <String> SubjectBox;
    private JButton addButton;
    private JPanel ProfessorPanel;
    private JButton removeButton;
    private JButton UndoButton;
    private JTable SubjectsTable;
    private JTable StudentsTable;
    private static final String url = "jdbc:mysql://localhost:3306/UTM";
    private static final String user = "root";
    private static final String passwd = "7102";
    public Professor() {
        setTitle("UTM");
        setContentPane(ProfessorPanel);
        setBounds(200, 80, 1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("img/bank.png")));
        createTable();
        updateDatabase();
        subjectbox();
        createSubjectTable();
        createStudentsTable();
        setVisible(true);
        setDefaultCloseOperation(this.DISPOSE_ON_CLOSE);
        txtProfessorID.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                warn();
            }
            public void removeUpdate(DocumentEvent e) {
                warn();
            }
            public void changedUpdate(DocumentEvent e) {
                warn();
            }
        });
        UndoButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                close();
                new Home();
            }
        });
        insertButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (txtProfessorID.getText().equals("") | txtFirstName.getText().equals("") | txtLastName.getText().equals("")){
                    JFrame frame = new JFrame();
                    JOptionPane.showMessageDialog(frame,"Enter all fields","Error", JOptionPane.ERROR_MESSAGE);
                }else{
                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Connection conn = DriverManager.getConnection(url, user, passwd);
                        PreparedStatement prepare = conn.prepareStatement("insert into professor values (?,?,?)");
                        Statement state1 = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                        ResultSet res1 = state1.executeQuery("select count(*) from professor where professor_id="+txtProfessorID.getText());
                        res1.first();
                        if (res1.getInt(1)==0){
                            Statement state = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                            ResultSet res = state.executeQuery("select count(*) from professor where first_name='"+
                                    txtFirstName.getText().toLowerCase()+"' and last_name='"+txtLastName.getText().toLowerCase()+"'");
                            res.first();
                            if(res.getInt(1)==0){
                                prepare.setInt(1, Integer.parseInt(txtProfessorID.getText()));
                                prepare.setString(2, txtFirstName.getText().toLowerCase());
                                prepare.setString(3, txtLastName.getText().toLowerCase());
                                prepare.executeUpdate();
                                JFrame frame = new JFrame("Message");
                                JOptionPane.showMessageDialog(frame,"Professor Record Added");
                                updateDatabase();
                                warn();
                            }
                            else{
                                JFrame frame = new JFrame();
                                JOptionPane.showMessageDialog(frame, "Existing Professor Name",
                                        "Warning", JOptionPane.WARNING_MESSAGE);
                            }
                        }else{
                            JFrame frame = new JFrame();
                            JOptionPane.showMessageDialog(frame, "Existing Professor ID",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (ClassNotFoundException exp1) {
                        java.util.logging.Logger.getLogger(Professor.class.getName()).log(Level.SEVERE,null,exp1);
                    } catch (SQLException | NumberFormatException exp2){
                        JFrame frame = new JFrame();
                        JOptionPane.showMessageDialog(frame,"Invalid Inputs","Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (txtProfessorID.getText().equals("") | txtFirstName.getText().equals("") | txtLastName.getText().equals("")){
                    JFrame frame = new JFrame();
                    JOptionPane.showMessageDialog(frame,"Enter all fields","Error", JOptionPane.ERROR_MESSAGE);
                }else {
                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Connection conn = DriverManager.getConnection(url, user, passwd);
                        PreparedStatement prepare = conn.prepareStatement("update professor set first_name=?, last_name=? where professor_id=?");
                        Statement state = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                        ResultSet res = state.executeQuery("select count(*) from professor where professor_id="+
                                txtProfessorID.getText());
                        res.first();
                        if(res.getInt(1)!=0){
                            Statement state1 = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                            ResultSet res1 = state1.executeQuery("select count(*) from professor where first_name='"+
                                    txtFirstName.getText().toLowerCase()+"' and last_name='"+txtLastName.getText().toLowerCase()
                                    + "' and professor_id!="+txtProfessorID.getText());
                            res1.first();
                            if(res1.getInt(1)==0){
                                prepare.setString(1, txtFirstName.getText().toLowerCase());
                                prepare.setString(2, txtLastName.getText().toLowerCase());
                                prepare.setInt(3, Integer.parseInt(txtProfessorID.getText()));
                                prepare.executeUpdate();
                                JFrame frame = new JFrame("Message");
                                JOptionPane.showMessageDialog(frame,"Professor Record Updated");
                                updateDatabase();
                                warn();
                            }else{
                                JFrame frame = new JFrame();
                                JOptionPane.showMessageDialog(frame, "Existing Professor Name",
                                        "Warning", JOptionPane.WARNING_MESSAGE);
                            }
                        }else{
                            JFrame frame = new JFrame();
                            JOptionPane.showMessageDialog(frame,"Inexisting Professor ID","Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (ClassNotFoundException exp1) {
                        java.util.logging.Logger.getLogger(Professor.class.getName()).log(Level.SEVERE,null,exp1);
                    } catch (SQLException | NumberFormatException exp2){
                        JFrame frame = new JFrame();
                        JOptionPane.showMessageDialog(frame,"Invalid Inputs","Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (txtProfessorID.getText().equals("") | txtFirstName.getText().equals("") | txtLastName.getText().equals("")){
                    JFrame frame = new JFrame();
                    JOptionPane.showMessageDialog(frame,"Enter all fields","Error", JOptionPane.ERROR_MESSAGE);
                }else{
                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Connection conn = DriverManager.getConnection(url, user, passwd);
                        Statement state = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                        ResultSet res = state.executeQuery("select count(*) from professor where professor_id="+ txtProfessorID.getText()
                                +" and first_name='"+ txtFirstName.getText().toLowerCase()+"' and last_name='"+txtLastName.getText().toLowerCase()+"'");
                        res.first();
                        if(res.getInt(1)==0){
                            JFrame frame1 = new JFrame();
                            JOptionPane.showMessageDialog(frame1,"Invalid Inputs","Error", JOptionPane.ERROR_MESSAGE);
                        }else{
                            JFrame frame = new JFrame();
                            if (JOptionPane.showConfirmDialog(frame,"Confirm if you want to delete",
                                    "Click a button",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
                                PreparedStatement prepare = conn.prepareStatement("delete from professor where professor_id=? and first_name=?" +
                                        " and last_name=?");
                                prepare.setInt(1, Integer.parseInt(txtProfessorID.getText()));
                                prepare.setString(2, txtFirstName.getText().toLowerCase());
                                prepare.setString(3, txtLastName.getText().toLowerCase());
                                prepare.executeUpdate();
                                JFrame frame2 = new JFrame("Message");
                                JOptionPane.showMessageDialog(frame2,"professor Record Deleted");
                                updateDatabase();
                                txtProfessorID.setText("");
                                txtFirstName.setText("");
                                txtLastName.setText("");
                                warn();
                            }
                        }
                    }catch (ClassNotFoundException exp1) {
                        java.util.logging.Logger.getLogger(Professor.class.getName()).log(Level.SEVERE,null,exp1);
                    }catch (SQLException | NumberFormatException exp2){
                        JFrame frame = new JFrame();
                        JOptionPane.showMessageDialog(frame,"Invalid Inputs","Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                txtProfessorID.setText("");
                txtFirstName.setText("");
                txtLastName.setText("");
            }
        });
        ProfessorTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                DefaultTableModel recordtable = (DefaultTableModel)ProfessorTable.getModel();
                int selectedrow = ProfessorTable.getSelectedRow();
                txtProfessorID.setText(recordtable.getValueAt(selectedrow,0).toString());
            }
        });
        emptyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection conn = DriverManager.getConnection(url, user, passwd);
                    JFrame frame = new JFrame();
                    if (JOptionPane.showConfirmDialog(frame,"Confirm if you want to empty",
                            "Click a button",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
                        PreparedStatement prepare = conn.prepareStatement("delete from professor");
                        prepare.executeUpdate();
                        JFrame frame2 = new JFrame("Message");
                        JOptionPane.showMessageDialog(frame2,"Professor Table Empty");
                        updateDatabase();
                        txtProfessorID.setText("");
                        txtFirstName.setText("");
                        txtLastName.setText("");
                    }
                }catch (ClassNotFoundException | SQLException exp1) {
                    java.util.logging.Logger.getLogger(Professor.class.getName()).log(Level.SEVERE, null, exp1);
                }
            }
        });
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (txtProfessorID.getText().equals("") | SubjectBox.getSelectedItem().toString().equals("")){
                    JFrame frame = new JFrame();
                    JOptionPane.showMessageDialog(frame,"Enter Subject and Professor ID","Error", JOptionPane.ERROR_MESSAGE);
                }else{
                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Connection conn = DriverManager.getConnection(url, user, passwd);
                        Statement state = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                        ResultSet res = state.executeQuery("select count(*) from professor where professor_id="+ txtProfessorID.getText());
                        res.first();
                        if(res.getInt(1)==0) {
                            JFrame frame1 = new JFrame();
                            JOptionPane.showMessageDialog(frame1, "Invalid Inputs", "Error", JOptionPane.ERROR_MESSAGE);
                        }else{
                            Statement state2 = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                            ResultSet res2 = state2.executeQuery("select subject_id from subject where name='" +
                                    SubjectBox.getSelectedItem().toString()+"'");
                            res2.first();
                            Statement state1 = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                            ResultSet res1 = state1.executeQuery("select count(*) from prof_sub where subject_id="
                                    + res2.getInt("subject_id")+" and professor_id="+txtProfessorID.getText());
                            res1.first();
                            if(res1.getInt(1)!=0) {
                                JFrame frame1 = new JFrame();
                                JOptionPane.showMessageDialog(frame1, "Existing Row Affected", "Error", JOptionPane.ERROR_MESSAGE);
                            }else{
                                PreparedStatement prepare = conn.prepareStatement("insert into prof_sub values (?,?)");
                                prepare.setInt(1, Integer.parseInt(txtProfessorID.getText()));
                                prepare.setInt(2,res2.getInt("subject_id"));
                                prepare.executeUpdate();
                                JFrame frame = new JFrame("Message");
                                JOptionPane.showMessageDialog(frame,"Subject Record Affected");
                                reveal();
                            }
                        }
                    } catch (ClassNotFoundException | SQLException exp1) {
                        java.util.logging.Logger.getLogger(Professor.class.getName()).log(Level.SEVERE,null,exp1);
                    }
                }
            }
        });
        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (txtProfessorID.getText().equals("") | SubjectBox.getSelectedItem().toString().equals("")){
                    JFrame frame = new JFrame();
                    JOptionPane.showMessageDialog(frame,"Enter Subject and Professor ID","Error", JOptionPane.ERROR_MESSAGE);
                }else{
                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Connection conn = DriverManager.getConnection(url, user, passwd);
                        Statement state = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                        ResultSet res = state.executeQuery("select count(*) from professor where professor_id="+ txtProfessorID.getText());
                        res.first();
                        if(res.getInt(1)==0) {
                            JFrame frame1 = new JFrame();
                            JOptionPane.showMessageDialog(frame1, "Invalid Inputs", "Error", JOptionPane.ERROR_MESSAGE);
                        }else{
                            JFrame frame = new JFrame();
                            if (JOptionPane.showConfirmDialog(frame,"Confirm if you want to remove",
                                    "Click a button",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
                                PreparedStatement prepare = conn.prepareStatement("delete from prof_sub where professor_id=? and subject_id=?");
                                prepare.setInt(1, Integer.parseInt(txtProfessorID.getText()));
                                Statement state2 = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                                ResultSet res2 = state2.executeQuery("select subject_id from subject where name='" +
                                        SubjectBox.getSelectedItem().toString()+"'");
                                res2.first();
                                prepare.setInt(2,res2.getInt("subject_id"));
                                prepare.executeUpdate();
                                JFrame frame2 = new JFrame("Message");
                                JOptionPane.showMessageDialog(frame2,"Subject Record Unaffected");
                                reveal();
                            }
                        }
                    }catch (ClassNotFoundException | SQLException exp1) {
                        java.util.logging.Logger.getLogger(Professor.class.getName()).log(Level.SEVERE,null,exp1);
                    }
                }
            }
        });
        SubjectsTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                DefaultTableModel recordtable = (DefaultTableModel)SubjectsTable.getModel();
                int selectedrow = SubjectsTable.getSelectedRow();
                SubjectBox.setSelectedItem(recordtable.getValueAt(selectedrow,0).toString());
            }
        });
    }
    public void createTable() {
        Object[][] data = null;
        String[] columnNames = new String[]{"Professor_id","First_Name","Last_name"};
        ProfessorTable.setModel(new DefaultTableModel(data, columnNames));
        ProfessorTable.getTableHeader().setBackground(Color.GRAY);
        ProfessorTable.getTableHeader().setForeground(Color.white);
    }
    public void updateDatabase(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, user, passwd);
            PreparedStatement prepare = conn.prepareStatement("select * from professor order by professor_id");
            ResultSet result = prepare.executeQuery();
            ResultSetMetaData resultMeta = result.getMetaData();
            DefaultTableModel recordtable = (DefaultTableModel)ProfessorTable.getModel();
            recordtable.setRowCount(0);
            while(result.next()){
                Vector rowdata =new Vector();
                for(int i = 1; i <= resultMeta.getColumnCount(); i++){
                    rowdata.add(result.getObject(i));
                }
                recordtable.addRow(rowdata);
            }
            result.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void createSubjectTable() {
        Object[][] data = null;
        String[] columnNames = new String[]{"Subject"};
        SubjectsTable.setModel(new DefaultTableModel(data, columnNames));
        SubjectsTable.getTableHeader().setBackground(Color.GRAY);
        SubjectsTable.getTableHeader().setForeground(Color.white);
    }
    public void createStudentsTable() {
        Object[][] data = null;
        String[] columnNames = new String[]{"Student"};
        StudentsTable.setModel(new DefaultTableModel(data, columnNames));
        StudentsTable.getTableHeader().setBackground(Color.GRAY);
        StudentsTable.getTableHeader().setForeground(Color.white);
    }
    public void close(){
        WindowEvent closeWindow = new WindowEvent(this,WindowEvent.WINDOW_CLOSING);
        Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(closeWindow);
    }
    public void subjectbox(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, user, passwd);
            Statement state = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
            ResultSet result = state.executeQuery("select * from subject");
            while(result.next()){
                SubjectBox.addItem(result.getString("name"));
            }
            result.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void fill(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, user, passwd);
            Statement state1 = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet res1 = state1.executeQuery("select count(*) from professor where professor_id=" + txtProfessorID.getText());
            res1.first();
            if (res1.getInt(1) != 0) {
                Statement state = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                ResultSet result = state.executeQuery("select * from professor where professor_id="+txtProfessorID.getText());
                result.first();
                txtFirstName.setText(result.getString("first_name"));
                txtLastName.setText(result.getString("last_name"));
                result.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void reveal(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, user, passwd);
            Statement state = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
            ResultSet res = state.executeQuery("select count(*) from professor where professor_id="+ txtProfessorID.getText());
            res.first();
            if(res.getInt(1)!=0) {
                PreparedStatement prepare1 = conn.prepareStatement("select subject_id from prof_sub where professor_id="
                        +txtProfessorID.getText()+" order by subject_id");
                ResultSet result1 = prepare1.executeQuery();
                DefaultTableModel recordtable1 = (DefaultTableModel)SubjectsTable.getModel();
                recordtable1.setRowCount(0);
                while(result1.next()){
                    Vector rowdata2 =new Vector();
                    Statement state2 = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                    ResultSet res2 = state2.executeQuery("select name from subject where subject_id=" +
                            result1.getInt("subject_id"));
                    res2.first();
                    rowdata2.add(res2.getString("name"));
                    recordtable1.addRow(rowdata2);
                }
                result1.close();
                PreparedStatement prepare2 = conn.prepareStatement("select distinct concat(first_name,' ',last_name)" +
                        " from prof_sub,sec_sub,student where prof_sub.subject_id=sec_sub.subject_id and sec_sub.section_id=student.section_id"
                        + " and prof_sub.professor_id="+txtProfessorID.getText());
                ResultSet result2 = prepare2.executeQuery();
                DefaultTableModel recordtable2 = (DefaultTableModel)StudentsTable.getModel();
                recordtable2.setRowCount(0);
                while(result2.next()){
                    Vector rowdata2 =new Vector();
                    rowdata2.add(result2.getString(1));
                    recordtable2.addRow(rowdata2);
                }
                result2.close();
            }
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }
    public void warn(){
        txtFirstName.setText("");
        txtLastName.setText("");
        DefaultTableModel recordtable1 = (DefaultTableModel)SubjectsTable.getModel();
        recordtable1.setRowCount(0);
        if (!txtProfessorID.getText().equals("")){
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection conn = DriverManager.getConnection(url, user, passwd);
                Statement state = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                ResultSet res = state.executeQuery("select count(*) from professor where professor_id=" + txtProfessorID.getText());
                res.first();
                if (res.getInt(1) != 0) {
                    fill();
                    reveal();
                }
            }
            catch (Exception exp) {
                exp.printStackTrace();
            }
        }
    }
    public static void main(String[] args){
        new Professor();
    }
}
