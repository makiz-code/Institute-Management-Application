import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;

public class Student extends JFrame{
    private JPanel StudentPanel;
    private JTextField txtStudentID;
    private JTextField txtFirstName;
    private JTextField txtLastName;
    private JButton insertButton;
    private JButton updateButton;
    private JTable StudentTable;
    private JComboBox <String> SectionBox;
    private JButton UndoButton;
    private JButton deleteButton;
    private JButton emptyButton;
    private JButton resetButton;
    private JComboBox <String> SubjectBox;
    private JTextField txtDSNote;
    private JTextField txtExamNote;
    private JButton modifyButton;
    private JTable MeanTable;
    private JButton addButton;
    private JButton removeButton;
    private JLabel Moy;
    private JButton resetButton1;
    private JLabel meanLabel;
    private JLabel meanLabel1;
    private static final String url = "jdbc:mysql://localhost:3306/projet";
    private static final String user = "root";
    private static final String passwd = "7102";
    public Student() {
        setTitle("UTM");
        setContentPane(StudentPanel);
        setBounds(120, 80, 1300, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("bank.png")));
        sectionbox();
        createTable();
        updateDatabase();
        createMeanTable();
        setVisible(true);
        setDefaultCloseOperation(this.DISPOSE_ON_CLOSE);
        txtStudentID.getDocument().addDocumentListener(new DocumentListener() {
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
                if (txtStudentID.getText().equals("") | txtFirstName.getText().equals("") | txtLastName.getText().equals("")){
                    JFrame frame = new JFrame();
                    JOptionPane.showMessageDialog(frame,"Enter all fields","Error", JOptionPane.ERROR_MESSAGE);
                }else{
                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Connection conn = DriverManager.getConnection(url, user, passwd);
                        PreparedStatement prepare = conn.prepareStatement("insert into student values (?,?,?,?)");
                        Statement state1 = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                        ResultSet res1 = state1.executeQuery("select count(*) from student where student_id="+txtStudentID.getText());
                        res1.first();
                        if (res1.getInt(1)==0){
                            Statement state = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                            ResultSet res = state.executeQuery("select count(*) from student where first_name='"+
                                    txtFirstName.getText().toLowerCase()+"' and last_name='"+txtLastName.getText().toLowerCase()+"'");
                            res.first();
                            if(res.getInt(1)==0){
                                prepare.setInt(1, Integer.parseInt(txtStudentID.getText()));
                                prepare.setString(2, txtFirstName.getText().toLowerCase());
                                prepare.setString(3, txtLastName.getText().toLowerCase());
                                Statement state2 = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                                ResultSet res2 = state2.executeQuery("select section_id from section where name='" +
                                        SectionBox.getSelectedItem().toString()+"'");
                                res2.first();
                                prepare.setInt(4,res2.getInt("section_id"));
                                prepare.executeUpdate();
                                JFrame frame = new JFrame("Message");
                                JOptionPane.showMessageDialog(frame,"Student Record Added");
                                updateDatabase();
                                warn();
                            }
                            else{
                                JFrame frame = new JFrame();
                                JOptionPane.showMessageDialog(frame, "Existing Student Name",
                                        "Warning", JOptionPane.WARNING_MESSAGE);
                            }
                        }else{
                            JFrame frame = new JFrame();
                            JOptionPane.showMessageDialog(frame, "Existing Student ID",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (ClassNotFoundException exp1) {
                        java.util.logging.Logger.getLogger(Student.class.getName()).log(Level.SEVERE,null,exp1);
                    } catch (SQLException | NumberFormatException exp2){
                        JFrame frame = new JFrame();
                        JOptionPane.showMessageDialog(frame,"Invalid Inputs","Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (txtStudentID.getText().equals("") | txtFirstName.getText().equals("") | txtLastName.getText().equals("")){
                    JFrame frame = new JFrame();
                    JOptionPane.showMessageDialog(frame,"Enter all fields","Error", JOptionPane.ERROR_MESSAGE);
                }else {
                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Connection conn = DriverManager.getConnection(url, user, passwd);
                        PreparedStatement prepare = conn.prepareStatement("update student set first_name=?, last_name=?," +
                                " section_id=? where student_id=?");
                        Statement state = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                        ResultSet res = state.executeQuery("select count(*) from student where student_id="+
                                txtStudentID.getText());
                        res.first();
                        if(res.getInt(1)!=0){
                            Statement state1 = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                            ResultSet res1 = state1.executeQuery("select count(*) from student where first_name='"+
                                    txtFirstName.getText().toLowerCase()+"' and last_name='"+txtLastName.getText().toLowerCase()
                                    +"' and student_id!="+txtStudentID.getText());
                            res1.first();
                            if(res1.getInt(1)==0){
                                prepare.setString(1, txtFirstName.getText().toLowerCase());
                                prepare.setString(2, txtLastName.getText().toLowerCase());
                                Statement state2 = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                                ResultSet res2 = state2.executeQuery("select section_id from section where name='" +
                                        SectionBox.getSelectedItem().toString()+"'");
                                res2.first();
                                prepare.setInt(3,res2.getInt("section_id"));
                                prepare.setInt(4, Integer.parseInt(txtStudentID.getText()));
                                if (delete()==1){
                                    prepare.executeUpdate();
                                    JFrame frame = new JFrame("Message");
                                    JOptionPane.showMessageDialog(frame,"Student Record Updated");
                                    updateDatabase();
                                    warn();
                                    modifie_csv(SectionBox.getSelectedItem().toString());
                                }
                            }else{
                                JFrame frame = new JFrame();
                                JOptionPane.showMessageDialog(frame, "Existing Student Name",
                                        "Warning", JOptionPane.WARNING_MESSAGE);
                            }
                        }else{
                            JFrame frame = new JFrame();
                            JOptionPane.showMessageDialog(frame,"Inexisting Student ID","Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (ClassNotFoundException exp1) {
                        java.util.logging.Logger.getLogger(Student.class.getName()).log(Level.SEVERE,null,exp1);
                    } catch (SQLException | NumberFormatException exp2){
                        JFrame frame = new JFrame();
                        JOptionPane.showMessageDialog(frame,"Invalid Inputs","Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (txtStudentID.getText().equals("") | txtFirstName.getText().equals("") | txtLastName.getText().equals("")){
                    JFrame frame = new JFrame();
                    JOptionPane.showMessageDialog(frame,"Enter all fields","Error", JOptionPane.ERROR_MESSAGE);
                }else{
                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Connection conn = DriverManager.getConnection(url, user, passwd);
                        Statement state = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                        ResultSet res = state.executeQuery("select count(*) from student where student_id="+ txtStudentID.getText()
                                +" and first_name='"+ txtFirstName.getText().toLowerCase()+"' and last_name='"+txtLastName.getText().toLowerCase()+"'");
                        res.first();
                        if(res.getInt(1)==0){
                            JFrame frame1 = new JFrame();
                            JOptionPane.showMessageDialog(frame1,"Invalid Inputs","Error", JOptionPane.ERROR_MESSAGE);
                        }else{
                            JFrame frame = new JFrame();
                            if (JOptionPane.showConfirmDialog(frame,"Confirm if you want to delete",
                                    "Click a button",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
                                PreparedStatement prepare = conn.prepareStatement("delete from student where student_id=? and first_name=?" +
                                        " and last_name=? and section_id=?");
                                prepare.setInt(1, Integer.parseInt(txtStudentID.getText()));
                                prepare.setString(2, txtFirstName.getText().toLowerCase());
                                prepare.setString(3, txtLastName.getText().toLowerCase());
                                Statement state2 = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                                ResultSet res2 = state2.executeQuery("select section_id from section where name='" +
                                        SectionBox.getSelectedItem().toString()+"'");
                                res2.first();
                                prepare.setInt(4,res2.getInt("section_id"));
                                prepare.executeUpdate();
                                JFrame frame2 = new JFrame("Message");
                                JOptionPane.showMessageDialog(frame2,"Student Record Deleted");
                                updateDatabase();
                                supprime_csv(SectionBox.getSelectedItem().toString());
                                txtStudentID.setText("");
                                txtFirstName.setText("");
                                txtLastName.setText("");
                                warn();
                            }
                        }
                    }catch (ClassNotFoundException exp1) {
                        java.util.logging.Logger.getLogger(Student.class.getName()).log(Level.SEVERE,null,exp1);
                    }catch (SQLException | NumberFormatException exp2){
                        JFrame frame = new JFrame();
                        JOptionPane.showMessageDialog(frame,"Invalid Inputs","Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                txtStudentID.setText("");
                txtFirstName.setText("");
                txtLastName.setText("");
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
                        PreparedStatement prepare = conn.prepareStatement("delete from student");
                        Statement state1 = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                        ResultSet res2 = state1.executeQuery("select * from section");
                        while(res2.next()){
                            vide_csv(res2.getString("name"));
                        }
                        prepare.executeUpdate();
                        JFrame frame2 = new JFrame("Message");
                        JOptionPane.showMessageDialog(frame2,"Student Table Empty");
                        updateDatabase();
                        txtStudentID.setText("");
                        txtFirstName.setText("");
                        txtLastName.setText("");
                    }
                }catch (ClassNotFoundException | SQLException exp1) {
                    java.util.logging.Logger.getLogger(Student.class.getName()).log(Level.SEVERE, null, exp1);
                }
            }
        });
        StudentTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                DefaultTableModel recordtable = (DefaultTableModel)StudentTable.getModel();
                int selectedrow = StudentTable.getSelectedRow();
                txtStudentID.setText(recordtable.getValueAt(selectedrow,0).toString());
            }
        });
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (txtStudentID.getText().equals("")){
                    JFrame frame = new JFrame();
                    JOptionPane.showMessageDialog(frame,"Enter Subject and Student ID","Error", JOptionPane.ERROR_MESSAGE);
                }else{
                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Connection conn = DriverManager.getConnection(url, user, passwd);
                        Statement state = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                        ResultSet res = state.executeQuery("select count(*) from student where student_id="+ txtStudentID.getText());
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
                            ResultSet res1 = state1.executeQuery("select count(*) from note where subject_id="
                                    + res2.getInt("subject_id")+" and student_id="+txtStudentID.getText());
                            res1.first();
                            if(res1.getInt(1)!=0) {
                                JFrame frame1 = new JFrame();
                                JOptionPane.showMessageDialog(frame1, "Existing Row", "Error", JOptionPane.ERROR_MESSAGE);
                            }else{
                                PreparedStatement prepare = conn.prepareStatement("insert into note values (?,?,?,?,?)");
                                prepare.setInt(1, Integer.parseInt(txtStudentID.getText()));
                                prepare.setInt(2,res2.getInt("subject_id"));
                                if (calculate()==-1){
                                    JFrame frame1 = new JFrame();
                                    JOptionPane.showMessageDialog(frame1, "Invalid Inputs", "Error", JOptionPane.ERROR_MESSAGE);
                                }
                                else{
                                    float ds_note=Float.parseFloat(txtDSNote.getText());
                                    float exam_note=Float.parseFloat(txtExamNote.getText());
                                    if (0<=ds_note && ds_note<=20 && 0<=exam_note && exam_note<=20) {
                                        prepare.setFloat(3, ds_note);
                                        prepare.setFloat(4, exam_note);
                                        prepare.setFloat(5, calculate());
                                        prepare.executeUpdate();
                                        JFrame frame = new JFrame("Message");
                                        JOptionPane.showMessageDialog(frame, "Mean record Added");
                                        reveal();
                                        mean();
                                        if (count() == limit()){
                                            ajout_csv(SectionBox.getSelectedItem().toString());
                                        }
                                    }
                                    else {
                                        JFrame frame2 = new JFrame();
                                        JOptionPane.showMessageDialog(frame2, "Invalid Inputs", "Error", JOptionPane.ERROR_MESSAGE);
                                    }
                                }
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
                if (txtStudentID.getText().equals("")){
                    JFrame frame = new JFrame();
                    JOptionPane.showMessageDialog(frame,"Enter Subject and Student ID","Error", JOptionPane.ERROR_MESSAGE);
                }else{
                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Connection conn = DriverManager.getConnection(url, user, passwd);
                        Statement state = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                        ResultSet res = state.executeQuery("select count(*) from student where student_id="+ txtStudentID.getText());
                        res.first();
                        if(res.getInt(1)==0) {
                            JFrame frame1 = new JFrame();
                            JOptionPane.showMessageDialog(frame1, "Invalid Inputs", "Error", JOptionPane.ERROR_MESSAGE);
                        }else{
                            JFrame frame = new JFrame();
                            if (JOptionPane.showConfirmDialog(frame,"Confirm if you want to remove",
                                    "Click a button",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
                                PreparedStatement prepare = conn.prepareStatement("delete from note where student_id=? and subject_id=?");
                                prepare.setInt(1, Integer.parseInt(txtStudentID.getText()));
                                Statement state2 = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                                ResultSet res2 = state2.executeQuery("select subject_id from subject where name='" +
                                        SubjectBox.getSelectedItem().toString()+"'");
                                res2.first();
                                prepare.setInt(2,res2.getInt("subject_id"));
                                prepare.executeUpdate();
                                JFrame frame2 = new JFrame("Message");
                                JOptionPane.showMessageDialog(frame2,"Mean Record Removed");
                                reveal();
                                mean();
                                supprime_csv(SectionBox.getSelectedItem().toString());
                            }
                        }
                    }catch (ClassNotFoundException | SQLException exp1) {
                        java.util.logging.Logger.getLogger(Professor.class.getName()).log(Level.SEVERE,null,exp1);
                    }
                }
            }
        });
        modifyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (txtStudentID.getText().equals("")){
                    JFrame frame = new JFrame();
                    JOptionPane.showMessageDialog(frame,"Enter Subject and Student ID","Error", JOptionPane.ERROR_MESSAGE);
                }else{
                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Connection conn = DriverManager.getConnection(url, user, passwd);
                        Statement state = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                        ResultSet res = state.executeQuery("select count(*) from student where student_id="+ txtStudentID.getText());
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
                            ResultSet res1 = state1.executeQuery("select count(*) from note where subject_id="
                                    + res2.getInt("subject_id")+" and student_id="+txtStudentID.getText());
                            res1.first();
                            if(res1.getInt(1)==0) {
                                JFrame frame1 = new JFrame();
                                JOptionPane.showMessageDialog(frame1, "Inexisting Row", "Error", JOptionPane.ERROR_MESSAGE);
                            }else{
                                PreparedStatement prepare = conn.prepareStatement("update note set ds_note=?, exam_note=?, mean=? " +
                                        "where student_id="+txtStudentID.getText() +" and subject_id="+res2.getInt("subject_id"));
                                if (calculate()==-1){
                                    JFrame frame1 = new JFrame();
                                    JOptionPane.showMessageDialog(frame1, "Invalid Inputs", "Error", JOptionPane.ERROR_MESSAGE);
                                }
                                else{
                                    float ds_note=Float.parseFloat(txtDSNote.getText());
                                    float exam_note=Float.parseFloat(txtExamNote.getText());
                                    if (0<=ds_note && ds_note<=20 && 0<=exam_note && exam_note<=20) {
                                        prepare.setFloat(1, ds_note);
                                        prepare.setFloat(2, exam_note);
                                        prepare.setFloat(3, calculate());
                                        prepare.executeUpdate();
                                        JFrame frame = new JFrame("Message");
                                        JOptionPane.showMessageDialog(frame,"Mean record updated");
                                        reveal();
                                        mean();
                                        modifie_csv(SectionBox.getSelectedItem().toString());
                                    }else{JFrame frame2 = new JFrame();
                                        JOptionPane.showMessageDialog(frame2, "Invalid Inputs", "Error", JOptionPane.ERROR_MESSAGE);
                                    }
                                }
                            }
                        }
                    } catch (ClassNotFoundException | SQLException exp1) {
                        java.util.logging.Logger.getLogger(Professor.class.getName()).log(Level.SEVERE,null,exp1);
                    }
                }
            }
        });
        resetButton1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                txtDSNote.setText("");
                txtExamNote.setText("");
            }
        });
        MeanTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                DefaultTableModel recordtable = (DefaultTableModel)MeanTable.getModel();
                int selectedrow = MeanTable.getSelectedRow();
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection conn = DriverManager.getConnection(url, user, passwd);
                    Statement state = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                    ResultSet result = state.executeQuery("select subject_id from subject where name='"+recordtable.getValueAt(selectedrow,0).toString()
                    +"'");
                    result.first();
                    ResultSet result1 = state.executeQuery("select ds_note, exam_note from note where student_id="+txtStudentID.getText()+
                    " and subject_id="+ result.getInt("subject_id"));
                    result1.first();
                    txtDSNote.setText(String.valueOf(result1.getFloat("ds_note")));
                    txtExamNote.setText(String.valueOf(result1.getFloat("exam_note")));
                    SubjectBox.setSelectedItem(recordtable.getValueAt(selectedrow,0).toString());
                    result1.close();
                    result.close();
                } catch (Exception exp) {
                    exp.printStackTrace();
                }

            }
        });
    }
    public float calculate(){
        try{
            return (float) (Float.parseFloat(txtDSNote.getText())*0.3+Float.parseFloat(txtExamNote.getText())*0.7);
        }
        catch (NumberFormatException e){
            return -1;
        }
    }
    private void vide_csv(String name) {
        ArrayList<String> columnname = new ArrayList<>();
        columnname.add("Student_id");
        columnname.add("First_name");
        columnname.add("Last_name");
        columnname.add("mean");
        try (FileWriter fw = new FileWriter(name+".csv"))  {
            fw.append(String.join(",", columnname)).append("\n");
        } catch (IOException exp) {
            exp.printStackTrace();
        }
    }
    public void ajout_csv(String name){
        String filename = name+".csv";
        ArrayList <String> newLine = new ArrayList<>();
        newLine.add(txtStudentID.getText());
        newLine.add(txtFirstName.getText());
        newLine.add(txtLastName.getText());
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, user, passwd);
            Statement state = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet res = state.executeQuery("select * from note where student_id="+txtStudentID.getText()+" order by subject_id");
            while (res.next()){
                newLine.add(res.getString("mean"));
            }
        }
        catch (Exception exp) {
            exp.printStackTrace();
        }
        newLine.add(Moy.getText().replace(",", "."));
        try (FileWriter fw = new FileWriter(filename, true)) {
            fw.append(String.join(",", newLine)).append("\n");
        } catch (IOException exp) {
            exp.printStackTrace();
        }
    }
    private void modifie_csv(String name){
        List<String[]> rows = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(name+".csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                rows.add(values);
            }
        } catch (Exception exp) {
            exp.printStackTrace();
        }
        for (String[] row : rows) {
            if (row[0].equals(txtStudentID.getText())) {
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection conn = DriverManager.getConnection(url, user, passwd);
                    Statement state = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    ResultSet res = state.executeQuery("select * from note where student_id=" + txtStudentID.getText() + " order by subject_id");
                    row[1] = txtFirstName.getText();
                    row[2] = txtLastName.getText();
                    int i=3;
                    while (res.next()) {
                        row[i] = res.getString("mean");
                        i += 1;
                    }
                    row[i] = Moy.getText();
                }catch (Exception exp) {
                    exp.printStackTrace();
                }
            }
        }
        try (FileWriter fw = new FileWriter(name+".csv")) {
            for (String[] row : rows) {
                fw.append(String.join(",", row)).append("\n");
            }
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }
    private void supprime_csv(String name) {
        java.util.List<String[]> rows = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(name+".csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                rows.add(values);
            }
        } catch (Exception exp) {
            exp.printStackTrace();
        }

        List<String[]> updatedRows = new ArrayList<>();
        for (String[] row : rows) {
            if (!(row[0].equals(txtStudentID.getText()))) {
                updatedRows.add(row);
            }
        }
        try (FileWriter fw = new FileWriter(name+".csv")) {
            for (String[] row : updatedRows) {
                fw.append(String.join(",", row)).append("\n");
            }
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    public int count(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, user, passwd);
            Statement state = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet res = state.executeQuery("select count(*) from note where student_id=" + txtStudentID.getText());
            res.first();
            return res.getInt(1);
        } catch (Exception exp){
            exp.printStackTrace();
        }
        return 0;
    }
    private int limit() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, user, passwd);
            Statement state = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet result = state.executeQuery("select section_id from section where name='"+SectionBox.getSelectedItem().toString()+"'");
            result.first();
            ResultSet res = state.executeQuery("select count(*) from sec_sub where section_id=" + result.getInt("section_id"));
            res.first();
            return res.getInt(1);
        } catch (Exception exp){
            exp.printStackTrace();
        }
        return 0;
    }
    public void mean(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, user, passwd);
            Statement state = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet res = state.executeQuery("select count(*) from student where student_id=" + txtStudentID.getText());
            res.first();
            if (res.getInt(1) != 0) {
                Statement state2 = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                ResultSet res2 = state2.executeQuery("select note.subject_id, mean, coef" +
                        " from note, subject where note.subject_id=subject.subject_id and student_id=" + txtStudentID.getText());
                float s,i;
                s=i=0;
                while (res2.next()) {
                    i+=res2.getFloat("coef");
                    s+=res2.getFloat("mean")*res2.getFloat("coef");
                }
                DecimalFormat df = new DecimalFormat("0.00");
                if (i==0||s==0){
                    Moy.setText("null");
                }else{
                    Moy.setText(df.format(s/i));
                }
            }
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }
    public void createTable() {
        Object[][] data = null;
        String[] columnNames = new String[]{"Student_id","First_Name","Last_name","Section"};
        StudentTable.setModel(new DefaultTableModel(data, columnNames));
        StudentTable.getTableHeader().setBackground(Color.GRAY);
        StudentTable.getTableHeader().setForeground(Color.white);
    }
    public void createMeanTable() {
        Object[][] data = null;
        String[] columnNames = new String[]{"Subject","Mean"};
        MeanTable.setModel(new DefaultTableModel(data, columnNames));
        MeanTable.getTableHeader().setBackground(Color.GRAY);
        MeanTable.getTableHeader().setForeground(Color.white);
    }
    public void updateDatabase(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, user, passwd);
            PreparedStatement prepare = conn.prepareStatement("select * from student order by student_id");
            ResultSet result = prepare.executeQuery();
            ResultSetMetaData resultMeta = result.getMetaData();
            DefaultTableModel recordtable = (DefaultTableModel)StudentTable.getModel();
            recordtable.setRowCount(0);
            while(result.next()){
                Vector rowdata =new Vector();
                for(int i = 1; i <= resultMeta.getColumnCount()-1; i++){
                    rowdata.add(result.getObject(i));
                }
                Statement state2 = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                ResultSet res2 = state2.executeQuery("select name from section where section_id=" +
                        result.getInt("section_id"));
                res2.first();
                rowdata.add(res2.getString("name"));
                recordtable.addRow(rowdata);
            }
            result.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void close(){
        WindowEvent closeWindow = new WindowEvent(this,WindowEvent.WINDOW_CLOSING);
        Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(closeWindow);
    }
    public void sectionbox(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, user, passwd);
            Statement state = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
            ResultSet result = state.executeQuery("select * from section");
            while(result.next()){
                SectionBox.addItem(result.getString("name"));
            }
            result.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public int delete(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, user, passwd);
            Statement state = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
            ResultSet result = state.executeQuery("select section_id from student where student_id="+txtStudentID.getText());
            result.first();
            ResultSet result1 = state.executeQuery("select name from section where section_id="+result.getInt("section_id"));
            result1.first();
            if (!((result1.getString("name")).equals(SectionBox.getSelectedItem().toString()))){
                JFrame frame1 = new JFrame();
                if (JOptionPane.showConfirmDialog(frame1,"Confirm if you want to change section",
                        "Click a button",JOptionPane.YES_NO_OPTION) ==JOptionPane.YES_OPTION){
                    PreparedStatement prepare = conn.prepareStatement("delete from note where student_id=" + txtStudentID.getText());
                    supprime_csv(result1.getString("name"));
                    prepare.executeUpdate();
                    return 1;
                }else{
                    return 0;
                }
            }else{
                return 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    public void fill(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, user, passwd);
            Statement state1 = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet res1 = state1.executeQuery("select count(*) from student where student_id=" + txtStudentID.getText());
            res1.first();
            if (res1.getInt(1) != 0) {
                Statement state = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                ResultSet result = state.executeQuery("select * from student where student_id="+txtStudentID.getText());
                result.first();
                txtFirstName.setText(result.getString("first_name"));
                txtLastName.setText(result.getString("last_name"));
                Statement state2 = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                ResultSet res2 = state2.executeQuery("select name from section where section_id=" +
                        result.getInt("section_id"));
                res2.first();
                SectionBox.setSelectedItem(res2.getString("name"));
                result.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void subjectbox(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, user, passwd);
            Statement state = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
            ResultSet res = state.executeQuery("select count(*) from student where student_id="+ txtStudentID.getText());
            res.first();
            if(res.getInt(1)!=0) {
                Statement state1 = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                ResultSet result1 = state1.executeQuery("select section_id from student where student_id="+txtStudentID.getText());
                result1.first();
                Statement state2 = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                ResultSet result2 = state2.executeQuery("select subject_id from sec_sub where section_id="+result1.getInt("section_id")
                        +" order by subject_id");
                while(result2.next()){
                    Statement state3 = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                    ResultSet result3 = state3.executeQuery("select name from subject where subject_id="+result2.getInt("subject_id"));
                    result3.first();
                    SubjectBox.addItem(result3.getString("name"));
                }
            }
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }
    public void reveal(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, user, passwd);
            Statement state = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
            ResultSet res = state.executeQuery("select count(*) from student where student_id="+ txtStudentID.getText());
            res.first();
            if(res.getInt(1)!=0) {
                PreparedStatement prepare1 = conn.prepareStatement("select subject_id, mean from note where student_id="
                        +txtStudentID.getText()+" order by subject_id");
                ResultSet result1 = prepare1.executeQuery();
                DefaultTableModel recordtable1 = (DefaultTableModel)MeanTable.getModel();
                recordtable1.setRowCount(0);
                while(result1.next()){
                    Vector rowdata2 =new Vector();
                    Statement state2 = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                    ResultSet res2 = state2.executeQuery("select name from subject where subject_id=" +
                            result1.getInt("subject_id"));
                    res2.first();
                    rowdata2.add(res2.getString("name"));
                    rowdata2.add(result1.getFloat("mean"));
                    recordtable1.addRow(rowdata2);
                }
                result1.close();
            }
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }
    public void warn(){
        txtFirstName.setText("");
        txtLastName.setText("");
        txtDSNote.setText("");
        txtExamNote.setText("");
        SectionBox.setSelectedItem("");
        SubjectBox.removeAllItems();
        DefaultTableModel recordtable1 = (DefaultTableModel)MeanTable.getModel();
        recordtable1.setRowCount(0);
        Moy.setText("null");
        if (!txtStudentID.getText().equals("")){
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection conn = DriverManager.getConnection(url, user, passwd);
                Statement state = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                ResultSet res = state.executeQuery("select count(*) from student where student_id=" + txtStudentID.getText());
                res.first();
                if (res.getInt(1) != 0) {
                    fill();
                    subjectbox();
                    reveal();
                    mean();
                }
            }
            catch (Exception exp) {
                exp.printStackTrace();
            }
        }
    }
    public static void main(String[] args){
        new Student();
    }
}
