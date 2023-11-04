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

public class Subject extends JFrame{
    private JPanel SubjectPanel;
    private JTextField txtSubjectID;
    private JTextField txtName;
    private JButton insertButton;
    private JButton updateButton;
    private JTable SubjectTable;
    private JComboBox <String> SectionBox;
    private JButton addSectionButton;
    private JButton removeSectionButton;
    private JButton removeProfessorButton;
    private JButton UndoButton;
    private JButton addProfessorButton;
    private JComboBox <String> ProfessorBox;
    private JButton deleteButton;
    private JButton emptyButton;
    private JButton resetButton;
    private JButton showButton;
    private JComboBox <String> CoefBox;
    private JTable SectionsTable;
    private JTable ProfessorsTable;
    private static final String url = "jdbc:mysql://localhost:3306/projet";
    private static final String user = "root";
    private static final String passwd = "7102";
    public Subject() {
        setTitle("UTM");
        setContentPane(SubjectPanel);
        setBounds(200, 80, 1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("bank.png")));
        createTable();
        updateDatabase();
        sectionBox();
        professorBox();
        createSectionTable();
        createProfessorTable();
        setVisible(true);
        setDefaultCloseOperation(this.DISPOSE_ON_CLOSE);
        txtSubjectID.getDocument().addDocumentListener(new DocumentListener() {
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
                if (txtSubjectID.getText().equals("") | txtName.getText().equals("")){
                    JFrame frame = new JFrame();
                    JOptionPane.showMessageDialog(frame,"Enter all fields","Error", JOptionPane.ERROR_MESSAGE);
                }else{
                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Connection conn = DriverManager.getConnection(url, user, passwd);
                        PreparedStatement prepare = conn.prepareStatement("insert into subject values (?,?,?)");
                        Statement state1 = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                        ResultSet res1 = state1.executeQuery("select count(*) from subject where subject_id="+txtSubjectID.getText());
                        res1.first();
                        if (res1.getInt(1)==0){
                            Statement state = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                            ResultSet res = state.executeQuery("select count(*) from subject where name='"+
                                    txtName.getText().toUpperCase()+"'");
                            res.first();
                            if(res.getInt(1)==0){
                                prepare.setInt(1, Integer.parseInt(txtSubjectID.getText()));
                                prepare.setString(2, txtName.getText().toUpperCase());
                                prepare.setFloat(3, Float.parseFloat(CoefBox.getSelectedItem().toString()));
                                prepare.executeUpdate();
                                JFrame frame = new JFrame("Message");
                                JOptionPane.showMessageDialog(frame,"Subject Record Added");
                                updateDatabase();
                                warn();
                            }
                            else{
                                JFrame frame = new JFrame();
                                JOptionPane.showMessageDialog(frame, "Existing Subject Name",
                                        "Warning", JOptionPane.WARNING_MESSAGE);
                            }
                        }else{
                            JFrame frame = new JFrame();
                            JOptionPane.showMessageDialog(frame, "Existing Subject ID",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (ClassNotFoundException exp1) {
                        java.util.logging.Logger.getLogger(Subject.class.getName()).log(Level.SEVERE,null,exp1);
                    } catch (SQLException | NumberFormatException exp2){
                        JFrame frame = new JFrame();
                        JOptionPane.showMessageDialog(frame,"Invalid Inputs","Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (txtSubjectID.getText().equals("") | txtName.getText().equals("")){
                    JFrame frame = new JFrame();
                    JOptionPane.showMessageDialog(frame,"Enter all fields","Error", JOptionPane.ERROR_MESSAGE);
                }else {
                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Connection conn = DriverManager.getConnection(url, user, passwd);
                        PreparedStatement prepare = conn.prepareStatement("update subject set name=?, coef=? where subject_id=?");
                        Statement state = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                        ResultSet res = state.executeQuery("select count(*) from subject where subject_id="+
                                txtSubjectID.getText());
                        res.first();
                        if(res.getInt(1)!=0){
                            Statement state1 = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                            ResultSet res1 = state1.executeQuery("select count(*) from subject where name='"+
                                    txtName.getText().toUpperCase()+"' and subject_id!="+txtSubjectID.getText());
                            res1.first();
                            if(res1.getInt(1)==0){
                                prepare.setString(1, txtName.getText().toUpperCase());
                                prepare.setFloat(2, Float.parseFloat(CoefBox.getSelectedItem().toString()));
                                prepare.setInt(3, Integer.parseInt(txtSubjectID.getText()));
                                prepare.executeUpdate();
                                ResultSet res2 = state1.executeQuery("select * from section");
                                while(res2.next()){
                                    modifie_csv(res2.getString("name"));
                                }
                                JFrame frame = new JFrame("Message");
                                JOptionPane.showMessageDialog(frame,"Subject Record Updated");
                                updateDatabase();
                                warn();
                            }else{
                                JFrame frame = new JFrame();
                                JOptionPane.showMessageDialog(frame, "Existing Subject Name",
                                        "Warning", JOptionPane.WARNING_MESSAGE);
                            }
                        }else{
                            JFrame frame = new JFrame();
                            JOptionPane.showMessageDialog(frame,"Inexisting Subject ID","Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (ClassNotFoundException exp1) {
                        java.util.logging.Logger.getLogger(Subject.class.getName()).log(Level.SEVERE,null,exp1);
                    } catch (SQLException | NumberFormatException exp2){
                        JFrame frame = new JFrame();
                        JOptionPane.showMessageDialog(frame,"Invalid Inputs","Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (txtSubjectID.getText().equals("") | txtName.getText().equals("")){
                    JFrame frame = new JFrame();
                    JOptionPane.showMessageDialog(frame,"Enter all fields","Error", JOptionPane.ERROR_MESSAGE);
                }else{
                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Connection conn = DriverManager.getConnection(url, user, passwd);
                        Statement state = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                        ResultSet res = state.executeQuery("select count(*) from subject where subject_id="+ txtSubjectID.getText()
                                +" and name='"+ txtName.getText().toUpperCase()+"' and coef="+CoefBox.getSelectedItem().toString());
                        res.first();
                        if(res.getInt(1)==0){
                            JFrame frame1 = new JFrame();
                            JOptionPane.showMessageDialog(frame1,"Invalid Inputs","Error", JOptionPane.ERROR_MESSAGE);
                        }else{
                            JFrame frame = new JFrame();
                            if (JOptionPane.showConfirmDialog(frame,"Confirm if you want to delete",
                                    "Click a button",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
                                PreparedStatement prepare = conn.prepareStatement("delete from subject where subject_id=? and name=?");
                                prepare.setInt(1, Integer.parseInt(txtSubjectID.getText()));
                                prepare.setString(2, txtName.getText().toUpperCase());
                                supprime_csv(SectionBox.getSelectedItem().toString());
                                prepare.executeUpdate();
                                JFrame frame2 = new JFrame("Message");
                                JOptionPane.showMessageDialog(frame2,"Subject Record Deleted");
                                updateDatabase();
                                txtSubjectID.setText("");
                                txtName.setText("");
                                warn();
                                mean(SectionBox.getSelectedItem().toString());
                            }
                        }
                    }catch (ClassNotFoundException exp1) {
                        java.util.logging.Logger.getLogger(Subject.class.getName()).log(Level.SEVERE,null,exp1);
                    }catch (SQLException | NumberFormatException exp2){
                        JFrame frame = new JFrame();
                        JOptionPane.showMessageDialog(frame,"Invalid Inputs","Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                txtSubjectID.setText("");
                txtName.setText("");
            }
        });
        SubjectTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                DefaultTableModel recordtable = (DefaultTableModel) SubjectTable.getModel();
                int selectedrow = SubjectTable.getSelectedRow();
                txtSubjectID.setText(recordtable.getValueAt(selectedrow,0).toString());
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
                        PreparedStatement prepare = conn.prepareStatement("delete from subject");
                        Statement state1 = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                        ResultSet res2 = state1.executeQuery("select * from section");
                        while(res2.next()){
                            vide_csv(res2.getString("name"));
                        }
                        prepare.executeUpdate();
                        JFrame frame2 = new JFrame("Message");
                        JOptionPane.showMessageDialog(frame2,"Subject Table Empty");
                        updateDatabase();
                        txtSubjectID.setText("");
                        txtName.setText("");
                    }
                }catch (ClassNotFoundException | SQLException exp1) {
                    java.util.logging.Logger.getLogger(Subject.class.getName()).log(Level.SEVERE, null, exp1);
                }
            }
        });
        addSectionButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (txtSubjectID.getText().equals("") | SectionBox.getSelectedItem().toString().equals("")){
                    JFrame frame = new JFrame();
                    JOptionPane.showMessageDialog(frame,"Enter Section and Subject ID","Error", JOptionPane.ERROR_MESSAGE);
                }else{
                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Connection conn = DriverManager.getConnection(url, user, passwd);
                        Statement state = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                        ResultSet res = state.executeQuery("select count(*) from subject where subject_id="+ txtSubjectID.getText());
                        res.first();
                        if(res.getInt(1)==0) {
                            JFrame frame1 = new JFrame();
                            JOptionPane.showMessageDialog(frame1, "Invalid Inputs", "Error", JOptionPane.ERROR_MESSAGE);
                        }else{
                            Statement state2 = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                            ResultSet res2 = state2.executeQuery("select section_id from section where name='" +
                                    SectionBox.getSelectedItem().toString()+"'");
                            res2.first();
                            Statement state1 = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                            ResultSet res1 = state1.executeQuery("select count(*) from sec_sub where section_id="
                                    + res2.getInt("section_id")+" and subject_id="+txtSubjectID.getText());
                            res1.first();
                            if(res1.getInt(1)!=0) {
                                JFrame frame1 = new JFrame();
                                JOptionPane.showMessageDialog(frame1, "Existing Row Affected", "Error", JOptionPane.ERROR_MESSAGE);
                            }else{
                                PreparedStatement prepare = conn.prepareStatement("insert into sec_sub values (?,?)");
                                prepare.setInt(2, Integer.parseInt(txtSubjectID.getText()));
                                prepare.setInt(1,res2.getInt("section_id"));
                                prepare.executeUpdate();
                                JFrame frame = new JFrame("Message");
                                JOptionPane.showMessageDialog(frame,"Section Record Affected");
                                reveal();
                                ajout_csv(SectionBox.getSelectedItem().toString());
                                mean(SectionBox.getSelectedItem().toString());
                            }
                        }
                    } catch (ClassNotFoundException | SQLException exp1) {
                        java.util.logging.Logger.getLogger(Subject.class.getName()).log(Level.SEVERE,null,exp1);
                    }
                }
            }
        });
        removeSectionButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (txtSubjectID.getText().equals("") | SectionBox.getSelectedItem().toString().equals("")){
                    JFrame frame = new JFrame();
                    JOptionPane.showMessageDialog(frame,"Enter Section and Subject ID","Error", JOptionPane.ERROR_MESSAGE);
                }else{
                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Connection conn = DriverManager.getConnection(url, user, passwd);
                        Statement state = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                        ResultSet res = state.executeQuery("select count(*) from subject where subject_id="+ txtSubjectID.getText());
                        res.first();
                        if(res.getInt(1)==0) {
                            JFrame frame1 = new JFrame();
                            JOptionPane.showMessageDialog(frame1, "Invalid Inputs", "Error", JOptionPane.ERROR_MESSAGE);
                        }else{
                            JFrame frame = new JFrame();
                            if (JOptionPane.showConfirmDialog(frame,"Confirm if you want to remove",
                                    "Click a button",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
                                PreparedStatement prepare = conn.prepareStatement("delete from sec_sub where section_id=? and subject_id=?");
                                prepare.setInt(2, Integer.parseInt(txtSubjectID.getText()));
                                Statement state2 = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                                ResultSet res2 = state2.executeQuery("select section_id from section where name='" +
                                        SectionBox.getSelectedItem().toString()+"'");
                                res2.first();
                                prepare.setInt(1,res2.getInt("section_id"));
                                supprime_csv(SectionBox.getSelectedItem().toString());
                                prepare.executeUpdate();
                                JFrame frame2 = new JFrame("Message");
                                JOptionPane.showMessageDialog(frame2,"Section Record Unaffected");
                                reveal();
                                trigger(txtSubjectID.getText());
                                mean(SectionBox.getSelectedItem().toString());
                            }
                        }
                    }catch (ClassNotFoundException exp1) {
                        java.util.logging.Logger.getLogger(Subject.class.getName()).log(Level.SEVERE,null,exp1);
                    }catch (SQLException | NumberFormatException exp2){
                        JFrame frame = new JFrame();
                        JOptionPane.showMessageDialog(frame,"Invalid Inputs","Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        addProfessorButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (txtSubjectID.getText().equals("") | ProfessorBox.getSelectedItem().toString().equals("")){
                    JFrame frame = new JFrame();
                    JOptionPane.showMessageDialog(frame,"Enter Professor and Subject ID","Error", JOptionPane.ERROR_MESSAGE);
                }else{
                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Connection conn = DriverManager.getConnection(url, user, passwd);
                        Statement state = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                        ResultSet res = state.executeQuery("select count(*) from subject where subject_id="+ txtSubjectID.getText());
                        res.first();
                        if(res.getInt(1)==0) {
                            JFrame frame1 = new JFrame();
                            JOptionPane.showMessageDialog(frame1, "Invalid Inputs", "Error", JOptionPane.ERROR_MESSAGE);
                        }else{
                            Statement state2 = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                            ResultSet res2 = state2.executeQuery("select professor_id from professor where concat(first_name,' ',last_name)='" +
                                    ProfessorBox.getSelectedItem().toString()+"'");
                            res2.first();
                            Statement state1 = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                            ResultSet res1 = state1.executeQuery("select count(*) from prof_sub where professor_id="
                                    + res2.getInt("professor_id")+" and subject_id="+txtSubjectID.getText());
                            res1.first();
                            if(res1.getInt(1)!=0) {
                                JFrame frame1 = new JFrame();
                                JOptionPane.showMessageDialog(frame1, "Existing Row Affected", "Error", JOptionPane.ERROR_MESSAGE);
                            }else{
                                PreparedStatement prepare = conn.prepareStatement("insert into prof_sub values (?,?)");
                                prepare.setInt(2, Integer.parseInt(txtSubjectID.getText()));
                                prepare.setInt(1,res2.getInt("professor_id"));
                                prepare.executeUpdate();
                                JFrame frame = new JFrame("Message");
                                JOptionPane.showMessageDialog(frame,"Professor Record Affected");
                                reveal();
                            }
                        }
                    } catch (ClassNotFoundException | SQLException exp1) {
                        java.util.logging.Logger.getLogger(Subject.class.getName()).log(Level.SEVERE,null,exp1);
                    }
                }
            }
        });
        removeProfessorButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (txtSubjectID.getText().equals("") | ProfessorBox.getSelectedItem().toString().equals("")){
                    JFrame frame = new JFrame();
                    JOptionPane.showMessageDialog(frame,"Enter Professor and Subject ID","Error", JOptionPane.ERROR_MESSAGE);
                }else{
                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Connection conn = DriverManager.getConnection(url, user, passwd);
                        Statement state = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                        ResultSet res = state.executeQuery("select count(*) from subject where subject_id="+ txtSubjectID.getText());
                        res.first();
                        if(res.getInt(1)==0) {
                            JFrame frame1 = new JFrame();
                            JOptionPane.showMessageDialog(frame1, "Invalid Inputs", "Error", JOptionPane.ERROR_MESSAGE);
                        }else{
                            JFrame frame = new JFrame();
                            if (JOptionPane.showConfirmDialog(frame,"Confirm if you want to remove",
                                    "Click a button",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
                                PreparedStatement prepare = conn.prepareStatement("delete from prof_sub where professor_id=? and subject_id=?");
                                prepare.setInt(2, Integer.parseInt(txtSubjectID.getText()));
                                Statement state2 = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                                ResultSet res2 = state2.executeQuery("select professor_id from professor where concat(first_name,' ',last_name)='" +
                                        ProfessorBox.getSelectedItem().toString()+"'");
                                res2.first();
                                prepare.setInt(1,res2.getInt("professor_id"));
                                prepare.executeUpdate();
                                JFrame frame2 = new JFrame("Message");
                                JOptionPane.showMessageDialog(frame2,"Professor Record Unaffected");
                                reveal();
                            }
                        }
                    }catch (ClassNotFoundException | SQLException exp1) {
                        java.util.logging.Logger.getLogger(Subject.class.getName()).log(Level.SEVERE,null,exp1);
                    }
                }
            }
        });
        SectionsTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                DefaultTableModel recordtable = (DefaultTableModel)SectionsTable.getModel();
                int selectedrow = SectionsTable.getSelectedRow();
                SectionBox.setSelectedItem(recordtable.getValueAt(selectedrow,0).toString());
            }
        });
        ProfessorsTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                DefaultTableModel recordtable = (DefaultTableModel)ProfessorsTable.getModel();
                int selectedrow = ProfessorsTable.getSelectedRow();
                ProfessorBox.setSelectedItem(recordtable.getValueAt(selectedrow,0).toString());
            }
        });
    }
    private void trigger(String id) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, user, passwd);
            PreparedStatement prepare = conn.prepareStatement("delete from note where subject_id="+id);
            prepare.executeUpdate();
        } catch(Exception exp){
            exp.printStackTrace();
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
    private void ajout_csv(String name) {
        ArrayList<String> columnname = new ArrayList<>();
        columnname.add("Student_id");
        columnname.add("First_name");
        columnname.add("Last_name");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, user, passwd);
            Statement state = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet result = state.executeQuery("select section_id from section where name='"+SectionBox.getSelectedItem().toString()+"'");
            result.first();
            ResultSet res = state.executeQuery("select sec_sub.subject_id,name from sec_sub,subject where sec_sub.section_id="+
                    result.getInt("section_id")+ " and sec_sub.subject_id = subject.subject_id order by subject_id");
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
            for (int i=0; i<limit()+4 ; i++){
                if (i==2+count()){
                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Connection conn = DriverManager.getConnection(url, user, passwd);
                        Statement state = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                        ResultSet res = state.executeQuery("select name from subject where subject_id=" + txtSubjectID.getText() + " order by subject_id");
                        res.first();
                        row[i] = res.getString("name");
                    }catch (Exception exp) {
                        exp.printStackTrace();
                    }
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
                    +txtName.getText() +"' and sec_sub.subject_id=subject.subject_id");
            res.first();
            String value = String.valueOf(res.getInt("subject_id"));
            ResultSet result = state.executeQuery("select section_id from section where name='"+SectionBox.getSelectedItem().toString()
                    +"'");
            result.first();
            ResultSet res2 = state.executeQuery("select subject_id from sec_sub where section_id="+result.getInt("section_id")
                    +" order by subject_id");
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
            ResultSet result = state.executeQuery("select section_id from section where name='"+SectionBox.getSelectedItem().toString()
                    +"'");
            result.first();
            ResultSet res = state.executeQuery("select count(*) from sec_sub where section_id=" + result.getInt("section_id"));
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
                    ResultSet result = state.executeQuery("select section_id from section where name='"+SectionBox.getSelectedItem().toString()
                            +"'");
                    result.first();
                    ResultSet res = state.executeQuery("select count(*) from sec_sub where section_id="+result.getInt("section_id"));
                    res.first();
                    row[res.getInt(1)+3] = df.format(s/i);
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
        String[] columnNames = new String[]{"Subject_id","Name","Coef"};
        SubjectTable.setModel(new DefaultTableModel(data, columnNames));
        SubjectTable.getTableHeader().setBackground(Color.GRAY);
        SubjectTable.getTableHeader().setForeground(Color.white);
    }
    public void updateDatabase(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, user, passwd);
            PreparedStatement prepare = conn.prepareStatement("select * from subject order by subject_id");
            ResultSet result = prepare.executeQuery();
            ResultSetMetaData resultMeta = result.getMetaData();
            DefaultTableModel recordtable = (DefaultTableModel) SubjectTable.getModel();
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
    public void createSectionTable() {
        Object[][] data = null;
        String[] columnNames = new String[]{"Section"};
        SectionsTable.setModel(new DefaultTableModel(data, columnNames));
        SectionsTable.getTableHeader().setBackground(Color.GRAY);
        SectionsTable.getTableHeader().setForeground(Color.white);
    }
    public void createProfessorTable() {
        Object[][] data = null;
        String[] columnNames = new String[]{"Professor"};
        ProfessorsTable.setModel(new DefaultTableModel(data, columnNames));
        ProfessorsTable.getTableHeader().setBackground(Color.GRAY);
        ProfessorsTable.getTableHeader().setForeground(Color.white);
    }
    public void close(){
        WindowEvent closeWindow = new WindowEvent(this,WindowEvent.WINDOW_CLOSING);
        Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(closeWindow);
    }
    public void sectionBox(){
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
    public void professorBox(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, user, passwd);
            Statement state = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
            ResultSet result = state.executeQuery("select * from professor");
            while(result.next()){
                ProfessorBox.addItem(result.getString("first_name")+" "+result.getString(("last_name")));
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
            ResultSet res1 = state1.executeQuery("select count(*) from subject where subject_id=" + txtSubjectID.getText());
            res1.first();
            if (res1.getInt(1) != 0) {
                Statement state = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                ResultSet result = state.executeQuery("select * from subject where subject_id="+txtSubjectID.getText());
                result.first();
                txtName.setText(result.getString("name"));
                CoefBox.setSelectedItem(String.valueOf(result.getFloat("coef")));
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
            ResultSet res = state.executeQuery("select count(*) from subject where subject_id="+ txtSubjectID.getText());
            res.first();
            if(res.getInt(1)!=0) {
                PreparedStatement prepare = conn.prepareStatement("select section_id from sec_sub where subject_id="
                        +txtSubjectID.getText()+" order by section_id");
                ResultSet result = prepare.executeQuery();
                DefaultTableModel recordtable = (DefaultTableModel) SectionsTable.getModel();
                recordtable.setRowCount(0);
                while(result.next()){
                    Vector rowdata2 =new Vector();
                    Statement state2 = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                    ResultSet res2 = state2.executeQuery("select name from section where section_id=" +
                            result.getInt("section_id"));
                    res2.first();
                    rowdata2.add(res2.getString("name"));
                    recordtable.addRow(rowdata2);
                }
                result.close();
                PreparedStatement prepare1 = conn.prepareStatement("select professor_id from prof_sub where subject_id="
                        +txtSubjectID.getText()+" order by professor_id");
                ResultSet result1 = prepare1.executeQuery();
                DefaultTableModel recordtable1 = (DefaultTableModel)ProfessorsTable.getModel();
                recordtable1.setRowCount(0);
                while(result1.next()){
                    Vector rowdata2 =new Vector();
                    Statement state2 = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                    ResultSet res2 = state2.executeQuery("select first_name, last_name from professor where professor_id=" +
                            result1.getInt("professor_id"));
                    res2.first();
                    rowdata2.add(res2.getString("first_name")+" "+res2.getString("last_name"));
                    recordtable1.addRow(rowdata2);
                }
                result1.close();
            }
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }
    public void warn(){
        txtName.setText("");
        DefaultTableModel recordtable1 = (DefaultTableModel) SectionsTable.getModel();
        recordtable1.setRowCount(0);
        recordtable1 = (DefaultTableModel)ProfessorsTable.getModel();
        recordtable1.setRowCount(0);
        if (!txtSubjectID.getText().equals("")){
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection conn = DriverManager.getConnection(url, user, passwd);
                Statement state = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                ResultSet res = state.executeQuery("select count(*) from subject where subject_id=" + txtSubjectID.getText());
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
        new Subject();
    }
}
