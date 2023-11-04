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
import java.io.File;

public class Section extends JFrame{
    private JPanel SectionPanel;
    private JButton UndoButton;
    private JTextField txtSectionID;
    private JTextField txtName;
    private JButton insertButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton emptyButton;
    private JButton resetButton;
    private JTable SectionTable;
    private JComboBox <String> SubjectBox;
    private JButton addButton;
    private JButton removeButton;
    private JTable SubjectsTable;
    private JTable StudentsTable;
    private static final String url = "jdbc:mysql://localhost:3306/projet";
    private static final String user = "root";
    private static final String passwd = "7102";
    public Section() {
        setTitle("UTM");
        setContentPane(SectionPanel);
        setBounds(200, 80, 1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("img/bank.png")));
        createTable();
        updateDatabase();
        subjectBox();
        createSubjectTable();
        createStudentsTable();
        setVisible(true);
        setDefaultCloseOperation(this.DISPOSE_ON_CLOSE);
        txtSectionID.getDocument().addDocumentListener(new DocumentListener() {
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
                if (txtSectionID.getText().equals("") | txtName.getText().equals("")){
                    JFrame frame = new JFrame();
                    JOptionPane.showMessageDialog(frame,"Enter all fields","Error", JOptionPane.ERROR_MESSAGE);
                }else{
                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Connection conn = DriverManager.getConnection(url, user, passwd);
                        PreparedStatement prepare = conn.prepareStatement("insert into section values (?,?)");
                        Statement state1 = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                        ResultSet res1 = state1.executeQuery("select count(*) from section where section_id="+txtSectionID.getText());
                        res1.first();
                        if (res1.getInt(1)==0){
                            Statement state = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                            ResultSet res = state.executeQuery("select count(*) from section where name='"+
                                    txtName.getText().toUpperCase()+"'");
                            res.first();
                            if(res.getInt(1)==0){
                                prepare.setInt(1, Integer.parseInt(txtSectionID.getText()));
                                prepare.setString(2, txtName.getText().toUpperCase());
                                prepare.executeUpdate();
                                JFrame frame = new JFrame("Message");
                                JOptionPane.showMessageDialog(frame,"Section Record Added");
                                updateDatabase();
                                warn();
                                ajout_csv(txtName.getText());
                            }
                            else{
                                JFrame frame = new JFrame();
                                JOptionPane.showMessageDialog(frame, "Existing Section Name",
                                        "Warning", JOptionPane.WARNING_MESSAGE);
                            }
                        }else{
                            JFrame frame = new JFrame();
                            JOptionPane.showMessageDialog(frame, "Existing Section ID",
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
                if (txtSectionID.getText().equals("") | txtName.getText().equals("")){
                    JFrame frame = new JFrame();
                    JOptionPane.showMessageDialog(frame,"Enter all fields","Error", JOptionPane.ERROR_MESSAGE);
                }else {
                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Connection conn = DriverManager.getConnection(url, user, passwd);
                        PreparedStatement prepare = conn.prepareStatement("update section set name=? where section_id=?");
                        Statement state = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                        ResultSet res = state.executeQuery("select count(*) from section where section_id="+
                                txtSectionID.getText());
                        res.first();
                        if(res.getInt(1)!=0){
                            Statement state1 = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                            ResultSet res1 = state1.executeQuery("select count(*) from section where name='"+
                                    txtName.getText().toUpperCase()+"' and section_id!="+txtSectionID.getText());
                            res1.first();
                            if(res1.getInt(1)==0){
                                prepare.setString(1, txtName.getText().toUpperCase());
                                prepare.setInt(2, Integer.parseInt(txtSectionID.getText()));
                                File oldFile = new File(old()+".csv");
                                prepare.executeUpdate();
                                File newFile = new File(txtName.getText().toUpperCase()+".csv");
                                JFrame frame = new JFrame("Message");
                                JOptionPane.showMessageDialog(frame,"Section Record Updated");
                                updateDatabase();
                                warn();
                                oldFile.renameTo(newFile);
                            }else{
                                JFrame frame = new JFrame();
                                JOptionPane.showMessageDialog(frame, "Existing Section Name",
                                        "Warning", JOptionPane.WARNING_MESSAGE);
                            }
                        }else{
                            JFrame frame = new JFrame();
                            JOptionPane.showMessageDialog(frame,"Inexisting Section ID","Error", JOptionPane.ERROR_MESSAGE);
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
                if (txtSectionID.getText().equals("") | txtName.getText().equals("")){
                    JFrame frame = new JFrame();
                    JOptionPane.showMessageDialog(frame,"Enter all fields","Error", JOptionPane.ERROR_MESSAGE);
                }else{
                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Connection conn = DriverManager.getConnection(url, user, passwd);
                        Statement state = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                        ResultSet res = state.executeQuery("select count(*) from section where section_id="+ txtSectionID.getText()
                                +" and name='"+ txtName.getText().toUpperCase()+"'");
                        res.first();
                        if(res.getInt(1)==0){
                            JFrame frame1 = new JFrame();
                            JOptionPane.showMessageDialog(frame1,"Invalid Inputs","Error", JOptionPane.ERROR_MESSAGE);
                        }else{
                            Statement state1 = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                            ResultSet res1 = state1.executeQuery("select count(*) from student where section_id="
                                    +txtSectionID.getText());
                            res1.first();
                            if(res1.getInt(1)!=0){
                                JFrame frame2 = new JFrame();
                                JOptionPane.showMessageDialog(frame2,"Existing Students in this Section","Error", JOptionPane.ERROR_MESSAGE);
                            }else{
                                JFrame frame = new JFrame();
                                if (JOptionPane.showConfirmDialog(frame,"Confirm if you want to delete",
                                        "Click a button",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
                                    PreparedStatement prepare = conn.prepareStatement("delete from section where section_id=? and name=?");
                                    prepare.setInt(1, Integer.parseInt(txtSectionID.getText()));
                                    prepare.setString(2, txtName.getText().toUpperCase());
                                    File file = new File(txtName.getText()+".csv");
                                    prepare.executeUpdate();
                                    JFrame frame2 = new JFrame("Message");
                                    JOptionPane.showMessageDialog(frame2,"Section Record Deleted");
                                    updateDatabase();
                                    file.delete();
                                    txtSectionID.setText("");
                                    txtName.setText("");
                                    warn();
                                }
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
                txtSectionID.setText("");
                txtName.setText("");
            }
        });
        SectionTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                DefaultTableModel recordtable = (DefaultTableModel)SectionTable.getModel();
                int selectedrow = SectionTable.getSelectedRow();
                txtSectionID.setText(recordtable.getValueAt(selectedrow,0).toString());
            }
        });
        emptyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection conn = DriverManager.getConnection(url, user, passwd);
                    Statement state1 = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                    ResultSet res1 = state1.executeQuery("select count(*) from student where section_id is not null");
                    res1.first();
                    if(res1.getInt(1)!=0){
                        JFrame frame1 = new JFrame();
                        JOptionPane.showMessageDialog(frame1,"Existing Students in these Sections","Error", JOptionPane.ERROR_MESSAGE);
                    }else{
                        JFrame frame = new JFrame();
                        if (JOptionPane.showConfirmDialog(frame,"Confirm if you want to empty",
                                "Click a button",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
                            PreparedStatement prepare = conn.prepareStatement("delete from section");
                            ResultSet res2 = state1.executeQuery("select * from section");
                            while(res2.next()){
                                File file = new File(res2.getString("name")+".csv");
                                file.delete();
                            }
                            prepare.executeUpdate();
                            JFrame frame2 = new JFrame("Message");
                            JOptionPane.showMessageDialog(frame2,"Section Table Empty");
                            updateDatabase();
                            txtSectionID.setText("");
                            txtName.setText("");
                        }
                    }
                }catch (ClassNotFoundException | SQLException exp1) {
                    java.util.logging.Logger.getLogger(Professor.class.getName()).log(Level.SEVERE, null, exp1);
                }
            }
        });
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (txtSectionID.getText().equals("") | SubjectBox.getSelectedItem().toString().equals("")){
                    JFrame frame = new JFrame();
                    JOptionPane.showMessageDialog(frame,"Enter Subject and Section ID","Error", JOptionPane.ERROR_MESSAGE);
                }else{
                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Connection conn = DriverManager.getConnection(url, user, passwd);
                        Statement state = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                        ResultSet res = state.executeQuery("select count(*) from section where section_id="+ txtSectionID.getText());
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
                            ResultSet res1 = state1.executeQuery("select count(*) from sec_sub where subject_id="
                                    + res2.getInt("subject_id")+" and section_id="+txtSectionID.getText());
                            res1.first();
                            if(res1.getInt(1)!=0) {
                                JFrame frame1 = new JFrame();
                                JOptionPane.showMessageDialog(frame1, "Existing Row Affected", "Error", JOptionPane.ERROR_MESSAGE);
                            }else{
                                PreparedStatement prepare = conn.prepareStatement("insert into sec_sub values (?,?)");
                                prepare.setInt(1, Integer.parseInt(txtSectionID.getText()));
                                prepare.setInt(2,res2.getInt("subject_id"));
                                prepare.executeUpdate();
                                JFrame frame = new JFrame("Message");
                                JOptionPane.showMessageDialog(frame,"Subject Record Affected");
                                reveal();
                                ajout_csv(txtName.getText());
                                mean(txtName.getText());
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
                if (txtSectionID.getText().equals("") | SubjectBox.getSelectedItem().toString().equals("")){
                    JFrame frame = new JFrame();
                    JOptionPane.showMessageDialog(frame,"Enter Subject and Section ID","Error", JOptionPane.ERROR_MESSAGE);
                }else{
                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Connection conn = DriverManager.getConnection(url, user, passwd);
                        Statement state = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                        ResultSet res = state.executeQuery("select count(*) from section where section_id="+ txtSectionID.getText());
                        res.first();
                        if(res.getInt(1)==0) {
                            JFrame frame1 = new JFrame();
                            JOptionPane.showMessageDialog(frame1, "Invalid Inputs", "Error", JOptionPane.ERROR_MESSAGE);
                        }else{
                            JFrame frame = new JFrame();
                            if (JOptionPane.showConfirmDialog(frame,"Confirm if you want to remove",
                                    "Click a button",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
                                PreparedStatement prepare = conn.prepareStatement("delete from sec_sub where section_id=? and subject_id=?");
                                prepare.setInt(1, Integer.parseInt(txtSectionID.getText()));
                                Statement state2 = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                                ResultSet res2 = state2.executeQuery("select subject_id from subject where name='" +
                                        SubjectBox.getSelectedItem().toString()+"'");
                                res2.first();
                                prepare.setInt(2,res2.getInt("subject_id"));
                                supprime_csv(txtName.getText());
                                prepare.executeUpdate();
                                JFrame frame2 = new JFrame("Message");
                                JOptionPane.showMessageDialog(frame2,"Subject Record Unaffected");
                                reveal();
                                trigger(res2.getInt("subject_id"));
                                mean(txtName.getText());
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
        SubjectsTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                DefaultTableModel recordtable = (DefaultTableModel)SubjectsTable.getModel();
                int selectedrow = SubjectsTable.getSelectedRow();
                SubjectBox.setSelectedItem(recordtable.getValueAt(selectedrow,0).toString());
            }
        });
    }

    private String old() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, user, passwd);
            Statement state = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet res = state.executeQuery("select name from section where section_id="+txtSectionID.getText());
            res.first();
            return res.getString("name");
        } catch(Exception exp){
            exp.printStackTrace();
        }
        return null;
    }

    private void trigger(int id) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, user, passwd);
            PreparedStatement prepare = conn.prepareStatement("delete from note where subject_id="+id);
            prepare.executeUpdate();
        } catch(Exception exp){
            exp.printStackTrace();
        }
    }
    private void ajout_csv(String name) {
        ArrayList<String> columnname = new ArrayList<>();
        columnname.add("Student_id");
        columnname.add("First_name");
        columnname.add("Last_name");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, user, passwd);
            Statement state = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet res = state.executeQuery("select sec_sub.subject_id,name from sec_sub,subject where sec_sub.section_id="+txtSectionID.getText()+
                    " and sec_sub.subject_id = subject.subject_id order by subject_id");
            while (res.next()){
                columnname.add(res.getString("name"));
            }
        }
        catch (Exception exp) {
            exp.printStackTrace();
        }
        columnname.add("mean");
        try (FileWriter fw = new FileWriter(name+".csv"))  {
            fw.append(String.join(",", columnname)).append("\n");
        } catch (IOException exp) {
            exp.printStackTrace();
        }
    }
    private void supprime_csv(String name) {
        ArrayList<String[]> rows = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(name+".csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                rows.add(values);
            }
        } catch (Exception exp) {
            exp.printStackTrace();
        }

        ArrayList<ArrayList<String>> updatedRows = new ArrayList<>();
        for (String[] row : rows) {
            ArrayList <String> newline = new ArrayList<>();
            for (int i=0; i<limit()+4 ; i++){
                if (i!=2+count()){
                    newline.add(row[i]);
                }
            }
            updatedRows.add(newline);
        }
        try (FileWriter fw = new FileWriter(name+".csv")) {
            for (ArrayList<String> row : updatedRows) {
                fw.append(String.join(",", row)).append("\n");
            }
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    private int count() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, user, passwd);
            Statement state = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet res = state.executeQuery("select sec_sub.subject_id from sec_sub,subject where name='"
                    +SubjectBox.getSelectedItem().toString() +"' and sec_sub.subject_id=subject.subject_id");
            res.first();
            String value = String.valueOf(res.getInt("subject_id"));
            ResultSet res2 = state.executeQuery("select subject_id from sec_sub where section_id="+txtSectionID.getText()+" order by subject_id");
            int nb=1;
            while (res2.next()){
                if (!value.equals(String.valueOf(res2.getInt("subject_id")))){
                    nb++;
                }
                else{
                    return nb;
                }
            }
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
            ResultSet res = state.executeQuery("select count(*) from sec_sub where section_id=" + txtSectionID.getText());
            res.first();
            return res.getInt(1);
        } catch (Exception exp){
            exp.printStackTrace();
        }
        return 0;
    }
    public void mean(String name){
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
        int nb=0;
        for (String[] row : rows) {
            if (nb==0){
                nb++;
            } else{
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection conn = DriverManager.getConnection(url, user, passwd);
                    Statement state = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    ResultSet res2 = state.executeQuery("select note.subject_id, mean, coef" +
                            " from note, subject where note.subject_id=subject.subject_id and student_id=" + row[0]);
                    float s,i;
                    s=i=0;
                    while (res2.next()) {
                        i+=res2.getFloat("coef");
                        s+=res2.getFloat("mean")*res2.getFloat("coef");
                    }
                    DecimalFormat df = new DecimalFormat("0.00");
                    ResultSet res = state.executeQuery("select count(*) from sec_sub where section_id="+txtSectionID.getText());
                    res.first();
                    row[res.getInt(1)+2] = df.format(s/i);

                }catch (Exception exp){
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
    public void createTable() {
        Object[][] data = null;
        String[] columnNames = new String[]{"Section_id","Name"};
        SectionTable.setModel(new DefaultTableModel(data, columnNames));
        SectionTable.getTableHeader().setBackground(Color.GRAY);
        SectionTable.getTableHeader().setForeground(Color.white);
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
        String[] columnNames = new String[]{"Student","Mean"};
        StudentsTable.setModel(new DefaultTableModel(data, columnNames));
        StudentsTable.getTableHeader().setBackground(Color.GRAY);
        StudentsTable.getTableHeader().setForeground(Color.white);
    }
    public void updateDatabase(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, user, passwd);
            PreparedStatement prepare = conn.prepareStatement("select * from section order by section_id");
            ResultSet result = prepare.executeQuery();
            ResultSetMetaData resultMeta = result.getMetaData();
            DefaultTableModel recordtable = (DefaultTableModel)SectionTable.getModel();
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
    public void close(){
        WindowEvent closeWindow = new WindowEvent(this,WindowEvent.WINDOW_CLOSING);
        Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(closeWindow);
    }
    public void subjectBox(){
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
            ResultSet res1 = state1.executeQuery("select count(*) from section where section_id=" + txtSectionID.getText());
            res1.first();
            if (res1.getInt(1) != 0) {
                Statement state = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                ResultSet result = state.executeQuery("select * from section where section_id="+txtSectionID.getText());
                result.first();
                txtName.setText(result.getString("name"));
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
            ResultSet res = state.executeQuery("select count(*) from section where section_id="+ txtSectionID.getText());
            res.first();
            if(res.getInt(1)!=0) {
                PreparedStatement prepare = conn.prepareStatement("select subject_id from sec_sub where section_id="
                        +txtSectionID.getText()+" order by subject_id");
                ResultSet result = prepare.executeQuery();
                DefaultTableModel recordtable = (DefaultTableModel) SubjectsTable.getModel();
                recordtable.setRowCount(0);
                while(result.next()){
                    Vector rowdata2 =new Vector();
                    Statement state2 = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                    ResultSet res2 = state2.executeQuery("select name from subject where subject_id=" +
                            result.getInt("subject_id"));
                    res2.first();
                    rowdata2.add(res2.getString("name"));
                    recordtable.addRow(rowdata2);
                }
                result.close();
                reveal2(txtName.getText());
            }
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }
    public void reveal2(String name){
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
        int nb=0;
        DefaultTableModel recordtable1 = (DefaultTableModel)StudentsTable.getModel();
        recordtable1.setRowCount(0);
        for (String[] row : rows) {
            if (nb==0){
                nb++;
            } else{

                Vector rowdata2 =new Vector();
                rowdata2.add(row[1]+" "+row[2]);
                rowdata2.add(row[row.length-1]);
                recordtable1.addRow(rowdata2);
            }
        }
    }
    public void warn(){
        txtName.setText("");
        DefaultTableModel recordtable1 = (DefaultTableModel) SubjectsTable.getModel();
        recordtable1.setRowCount(0);
        recordtable1 = (DefaultTableModel)StudentsTable.getModel();
        recordtable1.setRowCount(0);
        if (!txtSectionID.getText().equals("")){
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection conn = DriverManager.getConnection(url, user, passwd);
                Statement state = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                ResultSet res = state.executeQuery("select count(*) from section where section_id=" + txtSectionID.getText());
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
        new Section();
    }
}
