package timetables;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.List;
import java.util.Map;

public class TimetableManagement {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            MainFrame.create().setVisible(true);
        });

    }
}

abstract class MainFrame extends JFrame {
    private JSON_FUNCTIONS jsonFunctions;

    public MainFrame() {
        setTitle("FAST NUCES Timetable Management System");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Color topColor = new Color(240, 240, 240);
                Color bottomColor = new Color(200, 220, 255);
                GradientPaint gradient = new GradientPaint(0, 0, topColor, 0, getHeight(), bottomColor);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        ImageIcon logoIcon = loadLogoImage();
        if (logoIcon != null) {
            JLabel logoLabel = new JLabel(logoIcon);
            logoLabel.setHorizontalAlignment(JLabel.CENTER);
            logoLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
            headerPanel.add(logoLabel, BorderLayout.NORTH);
        }

        JLabel titleLabel = new JLabel("FAST NUCES TIMETABLE APPLICATION", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 51, 102));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        mainPanel.add(headerPanel, BorderLayout.NORTH);


        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 15, 15));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(40, 150, 40, 150));

        JButton studentButton = createGradientButton("Student");
        JButton teacherButton = createGradientButton("Teacher");
        JButton adminButton = createGradientButton("Administrator");

        studentButton.addActionListener(e -> new StudentLoginFrame().setVisible(true));
        teacherButton.addActionListener(e -> new TeacherLoginFrame().setVisible(true));
        adminButton.addActionListener(e -> new AdminLoginFrame().setVisible(true));

        buttonPanel.add(studentButton);
        buttonPanel.add(teacherButton);
        buttonPanel.add(adminButton);

        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        JLabel footerLabel = new JLabel("© 2023 FAST NUCES - Timetable Management System", JLabel.CENTER);
        footerLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        footerLabel.setForeground(new Color(100, 100, 100));
        footerLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        mainPanel.add(footerLabel, BorderLayout.SOUTH);
        add(mainPanel);

        jsonFunctions = new JSON_FUNCTIONS();
    }

    public static MainFrame create() {
        return new MainFrame() {};
    }

    private ImageIcon loadLogoImage() {
        try {
            File imageFile = new File("src/timetables/image.png");
            ImageIcon icon = imageFile.exists()
                    ? new ImageIcon(imageFile.getPath())
                    : new ImageIcon(getClass().getResource("/timetables/image.png"));

            Image scaledImage = icon.getImage().getScaledInstance(300, -1, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } catch (Exception e) {
            System.err.println("Error loading logo: " + e.getMessage());
            return null;
        }
    }

    private JButton createGradientButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(220, 235, 255),
                        0, getHeight(), new Color(180, 210, 255));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                g2.setColor(new Color(0, 51, 102));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);

                super.paintComponent(g);
            }
        };

        button.setFont(new Font("Arial", Font.BOLD, 24));
        button.setForeground(new Color(0, 51, 102));
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
                button.repaint();
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                button.repaint();
            }
        });

        return button;
    }

    public JSON_FUNCTIONS getJsonFunctions() {
        return jsonFunctions;
    }
}
enum Day {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY;

    public static Day fromString(String dayStr) {
        try {
            return Day.valueOf(dayStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return MONDAY;
        }
    }

    @Override
    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }
}

abstract class person <T>{
    String name;
    String department;
    person(String name, String department) {
        this.name = name;
        this.department = department;
    }
    void displayTimetable(JTextArea textArea){};
}

class Student extends person {
    private String rollNo;
    private String section;
    private String batch;
    private JSON_FUNCTIONS jsonFunctions;

    public Student(String name, String rollNo, String section, String department, String batch) {
        super(name,department);
        this.rollNo = rollNo;
        this.section = section;
        this.batch = batch;
        this.jsonFunctions = new JSON_FUNCTIONS();
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getRollNo() {
        return rollNo;
    }
    public void setRollNo(String rollNo) {
        this.rollNo = rollNo;
    }
    public String getSection() {
        return section;
    }
    public void setSection(String section) {
        this.section = section;
    }
    public String getDepartment() {
        return department;
    }
    public void setDepartment(String department) {
        this.department = department;
    }
    public String getBatch() {
        return batch;
    }
    public void setBatch(String batch) {
        this.batch = batch;
    }

    private String generateFilename() {
        return batch + "_" + department.toUpperCase() + "_" + section + ".txt";
    }


    @Override
    void displayTimetable(JTextArea textArea) {
        String filename = generateFilename();

        String timetableText = "Timetable for " + name + " (" + batch+"-"+department + " - " + section + ")\n";
        textArea.append(timetableText);

        File file = new File("src\\timetables\\" + filename);

        if (!file.exists()) {
            textArea.append("\nNo timetable file found for the selected batch, department, and section.\n");
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                textArea.append(line + "\n");
            }
        } catch (IOException e) {
            textArea.append("\nError loading the timetable: " + e.getMessage());
        }
    }

    private void validateDepartmentAndSection() throws IllegalArgumentException {
        List<String> batches = jsonFunctions.getBatches();
        boolean isValid = false;

        for (String batch : batches) {
            List<String> departments = jsonFunctions.getDepartments(batch);
            if (departments.contains(department)) {
                List<String> sections = jsonFunctions.getSections(batch, department);
                if (sections.contains(section)) {
                    isValid = true;
                    break;
                }
            }
        }
        if (!isValid) {
            throw new IllegalArgumentException("Invalid department or section: " + department + " - " + section);
        }
    }
    public static Student createStudentFromLogin(String name, String rollNo, String department, String section, String batch) {
        return new Student(name, rollNo, section, department, batch);
    }
}

class Teacher extends person{
    private Map<Day, List<String>> timetable;
    private List<String> subjects;


    public Teacher(String name, String department) {
        super(name,department);
        this.department = department;
        this.timetable = new EnumMap<>(Day.class);
        this.subjects = new ArrayList<>();
    }
    public void loadTimetableFromFile() throws IOException {
        timetable.clear();
        String filename = "src\\timetables\\" +
                name.toLowerCase().replace(" ", "_") + "_timetable.txt";
        File file = new File(filename);


        if (!file.exists()) {
            throw new FileNotFoundException("Timetable file not found: " + file.getAbsolutePath());
        }


        boolean inConsolidatedSection = false;


        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            Day currentDay = null;


            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                if (line.startsWith("Consolidated Timetable")) {
                    inConsolidatedSection = true;
                    continue;
                }


                if (inConsolidatedSection) {
                    if (line.endsWith(":")) {
                        String dayStr = line.replace(":", "").trim();
                        currentDay = Day.fromString(dayStr);
                    } else if (currentDay != null && !line.isEmpty()) {
                        timetable.computeIfAbsent(currentDay, k -> new ArrayList<>()).add(line.trim());
                    }
                }
            }
        }
    }

    @Override
    public void displayTimetable(JTextArea textArea) {
        try {
            loadTimetableFromFile();
            textArea.setText("");
            textArea.append("TIMETABLE FOR " + name.toUpperCase() + "\n\n");
            if (department != null && !department.isEmpty()) {
                textArea.append("Department: " + department + "\n\n");
            }
            for (Day day : Day.values()) {
                List<String> slots = timetable.get(day);
                if (slots == null || slots.isEmpty()) continue;
                textArea.append(day.toString().toUpperCase() + ":\n");
                for (String slot : slots) {
                    textArea.append("  " + slot + "\n");
                }
                textArea.append("\n");
            }
            if (timetable.isEmpty()) {
                textArea.append("No timetable entries found.\n");
            }
        } catch (IOException e) {
            textArea.append("Error: " + e.getMessage() + "\n");
        }
    }
    public String getName() {
        return name;
    }
    public List<String> getSubjects() {
        return new ArrayList<>(subjects);
    }

}

class Timetable {
    private String batch;
    private String department;
    private String section;
    private String teacherName;
    private String subject;
    private Map<String, String[]> schedule = new HashMap<>();


    public void setBatch(String batch) { this.batch = batch; }
    public void setDepartment(String department) {this.department = department;    }
    public void setSection(String section) { this.section = section; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Batch: ").append(batch).append("\n");
        sb.append("Department: ").append(department).append("\n");
        sb.append("Section: ").append(section).append("\n\n");

        String[] orderedDays = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};

        for (String day : orderedDays) {
            String[] periods = schedule.get(day);
            if (periods != null) {
                sb.append(day).append(":\n");
                for (String period : periods) {
                    if (period != null && !period.isEmpty()) {
                        sb.append("  ").append(period).append("\n");
                    }
                }
            }
        }
        return sb.toString();
    }

    public void addDaySchedule(String day, String[] periods) {
        schedule.put(day, periods);
    }
}



class StudentLoginFrame extends JFrame {
    private JTextField nameField;
    private JTextField rollNoField;
    private JComboBox<String> deptCombo;
    private JComboBox<String> sectionCombo;
    private String currentBatch = null;
    private final JSON_FUNCTIONS json;

    private final Color PRIMARY_COLOR = new Color(0, 51, 102);
    private final Color SECONDARY_COLOR = new Color(200, 220, 255);
    private final Color BACKGROUND_COLOR = new Color(240, 245, 255);

    public StudentLoginFrame() {
        setTitle("Student Login");
        setSize(700, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        json = new JSON_FUNCTIONS("C:\\Users\\HP\\OneDrive\\Desktop\\baseclasstimetable.txt");

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        mainPanel.setBackground(BACKGROUND_COLOR);

        JLabel titleLabel = new JLabel("Student Login", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 15, 20));
        formPanel.setBackground(BACKGROUND_COLOR);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        nameField = createStyledTextField();
        rollNoField = createStyledTextField();
        deptCombo = createStyledComboBox();
        sectionCombo = createStyledComboBox();

        formPanel.add(createStyledLabel("Full Name:"));
        formPanel.add(nameField);
        formPanel.add(createStyledLabel("Roll Number (e.g. 24k-2558):"));
        formPanel.add(rollNoField);
        formPanel.add(createStyledLabel("Department:"));
        formPanel.add(deptCombo);
        formPanel.add(createStyledLabel("Section:"));
        formPanel.add(sectionCombo);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setBackground(BACKGROUND_COLOR);

        JButton loginButton = createStyledButton("Login", PRIMARY_COLOR, Color.WHITE);
        JButton clearButton = createStyledButton("Clear", SECONDARY_COLOR, PRIMARY_COLOR);

        buttonPanel.add(loginButton);
        buttonPanel.add(clearButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        rollNoField.addActionListener(e -> handleRollInput());

        deptCombo.addActionListener(e -> {
            if (currentBatch != null) {
                String dept = (String) deptCombo.getSelectedItem();
                updateSectionCombo(currentBatch, dept);
            }
        });

        loginButton.addActionListener(e -> handleLogin());

        clearButton.addActionListener(e -> {
            nameField.setText("");
            rollNoField.setText("");
            deptCombo.setModel(new DefaultComboBoxModel<>());
            sectionCombo.setModel(new DefaultComboBoxModel<>());
            currentBatch = null;
        });

        add(mainPanel);
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 18));
        label.setForeground(PRIMARY_COLOR);
        return label;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Arial", Font.PLAIN, 16));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        return field;
    }

    private JComboBox<String> createStyledComboBox() {
        JComboBox<String> combo = new JComboBox<>();
        combo.setFont(new Font("Arial", Font.PLAIN, 16));
        combo.setBackground(Color.WHITE);
        combo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return combo;
    }

    private JButton createStyledButton(String text, Color bgColor, Color textColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
            }
        };

        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(textColor);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
                button.setBackground(bgColor.darker());
                button.repaint();
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                button.setBackground(bgColor);
                button.repaint();
            }
        });

        return button;
    }

    private void handleRollInput() {
        String roll = rollNoField.getText().trim();
        if (!roll.matches("\\d{2}[kK]-\\d{4}")) {
            JOptionPane.showMessageDialog(this, "Roll number must be in format 24k-1234", "Invalid", JOptionPane.WARNING_MESSAGE);
            return;
        }

        currentBatch = roll.substring(0, 2);
        updateDeptCombo(currentBatch);
    }

    private void updateDeptCombo(String batch) {
        List<String> depts = json.getDepartments(batch);
        deptCombo.setModel(new DefaultComboBoxModel<>(depts.toArray(new String[0])));
        sectionCombo.setModel(new DefaultComboBoxModel<>());
    }

    private void updateSectionCombo(String batch, String dept) {
        List<String> sections = json.getSections(batch, dept);
        sectionCombo.setModel(new DefaultComboBoxModel<>(sections.toArray(new String[0])));
    }


    private void handleLogin() {
        String name = nameField.getText().trim();
        String rollNo = rollNoField.getText().trim();
        String department = (String) deptCombo.getSelectedItem();
        String section = (String) sectionCombo.getSelectedItem();

        if (name.isEmpty() || rollNo.isEmpty() || department == null || section == null) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if(!name.matches("[a-zA-Z ]+")){
            JOptionPane.showMessageDialog(this,"Name must contain only alphabets and spaces.", "Invalid Name",JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!rollNo.matches("\\d{2}[kK]-\\d{4}")) {
            JOptionPane.showMessageDialog(this, "Invalid roll number format.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String batch = rollNo.substring(0, 2);
        String fileName = batch + "_" + department.toUpperCase() + "_" + section + ".txt";
        String filePath = "src\\timetables\\" + fileName;

        File file = new File(filePath);
        if (!file.exists()) {
            JOptionPane.showMessageDialog(this, "Timetable file not found: " + fileName, "Missing", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Student student = Student.createStudentFromLogin(name, rollNo, department, section, batch);

        new StudentDashboardFrame(student).setVisible(true);
        dispose();
    }}

class TeacherLoginFrame extends JFrame {

    private JTextField nameField;
    private final Color PRIMARY_COLOR = new Color(0, 51, 102);
    private final Color SECONDARY_COLOR = new Color(200, 220, 255);
    private final Color BACKGROUND_COLOR = new Color(240, 245, 255);

    public TeacherLoginFrame() {
        setTitle("Teacher Login");
        setSize(500, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(0, 0, BACKGROUND_COLOR, 0, getHeight(), SECONDARY_COLOR);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel titleLabel = new JLabel("Teacher Login", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel nameLabel = new JLabel("Full Name:");
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        nameLabel.setForeground(PRIMARY_COLOR);
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(nameLabel, gbc);

        nameField = new JTextField(20);
        nameField.setFont(new Font("Arial", Font.PLAIN, 16));
        nameField.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, PRIMARY_COLOR));
        nameField.setMargin(new Insets(8, 10, 8, 10));

        gbc.gridx = 1; gbc.gridy = 0; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(nameField, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setOpaque(false);

        JButton loginButton = createStyledButton("Login", PRIMARY_COLOR, Color.WHITE);
        loginButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (!name.isEmpty()) {
                Teacher teacher = new Teacher(name, "");
                new TeacherDashboardFrame(teacher).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Please enter the teacher's name.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        buttonPanel.add(loginButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JButton createStyledButton(String text, Color bgColor, Color textColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
            }
        };

        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(textColor);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
                button.setBackground(bgColor.darker());
                button.repaint();
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                button.setBackground(bgColor);
                button.repaint();
            }
        });

        return button;
    }
}
class StudentDashboardFrame extends JFrame {
    private Student student;
    private final Color PRIMARY_COLOR = new Color(0, 51, 102);
    private final Color BACKGROUND_COLOR = new Color(240, 245, 255);

    public StudentDashboardFrame(Student student) {
        this.student = student;
        setTitle("Student Dashboard - " + student.getName());
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(BACKGROUND_COLOR);

        JLabel titleLabel = new JLabel(student.getName() + " - " + student.getSection() +
                " (" + student.getDepartment() + ")", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 16));
        textArea.setMargin(new Insets(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 1));
        panel.add(scrollPane, BorderLayout.CENTER);
        student.displayTimetable(textArea);
        add(panel);
    }
}


class TeacherDashboardFrame extends JFrame {
    private Teacher teacher;
    private final Color PRIMARY_COLOR = new Color(0, 51, 102);
    private final Color SECONDARY_COLOR = new Color(200, 220, 255);
    private final Color BACKGROUND_COLOR = new Color(240, 245, 255);

    public TeacherDashboardFrame(Teacher teacher) {
        this.teacher = teacher;
        setTitle("Teacher Dashboard - " + teacher.getName());
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(0, 0, BACKGROUND_COLOR, 0, getHeight(), SECONDARY_COLOR);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Teacher Dashboard - " + teacher.getName(), JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        JTextArea timetableArea = new JTextArea();
        timetableArea.setEditable(false);
        timetableArea.setFont(new Font("Monospaced", Font.PLAIN, 16));
        timetableArea.setMargin(new Insets(10, 10, 10, 10));

        timetableArea.setLineWrap(true);
        timetableArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(timetableArea);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
                        "Timetable"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setOpaque(false);

        JButton refreshButton = createStyledButton("Refresh", PRIMARY_COLOR, Color.WHITE);
        refreshButton.addActionListener(e -> teacher.displayTimetable(timetableArea));

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);

        teacher.displayTimetable(timetableArea);
    }

    private JButton createStyledButton(String text, Color bgColor, Color textColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
            }
        };

        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(textColor);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
                button.setBackground(bgColor.darker());
                button.repaint();
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                button.setBackground(bgColor);
                button.repaint();
            }
        });

        return button;
    }
}


class  Administrative {
    private static final String ADMIN_PASSWORD = "admin123";
    public boolean authenticate(String password) {
        return ADMIN_PASSWORD.equals(password);
    }
}


class AdminLoginFrame extends JFrame {
    public AdminLoginFrame() {
        setTitle("Administrator Login");
        setSize(450, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel gradientPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Color color1 = new Color(225, 235, 255);
                Color color2 = new Color(190, 210, 240);
                int w = getWidth();
                int h = getHeight();
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        gradientPanel.setLayout(new BoxLayout(gradientPanel, BoxLayout.Y_AXIS));
        gradientPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        JLabel titleLabel = new JLabel("Administrator Login");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(new Color(0, 51, 102));
        gradientPanel.add(titleLabel);
        gradientPanel.add(Box.createVerticalStrut(30));
        JPanel formPanel = new JPanel(new BorderLayout(10, 10));
        formPanel.setOpaque(false);
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        JPasswordField passwordField = new JPasswordField();
        formPanel.add(passwordLabel, BorderLayout.WEST);
        formPanel.add(passwordField, BorderLayout.CENTER);
        gradientPanel.add(formPanel);
        gradientPanel.add(Box.createVerticalStrut(30));

        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        loginButton.setBackground(new Color(0, 38, 77));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.setPreferredSize(new Dimension(120, 40));
        loginButton.setMaximumSize(new Dimension(140, 45));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setContentAreaFilled(false);
        loginButton.setOpaque(true);
        loginButton.addActionListener(e -> {
            String password = new String(passwordField.getPassword());
            Administrative admin = new Administrative();
            if (admin.authenticate(password)) {
                new AdminDashboardFrame(admin).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid password", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        gradientPanel.add(loginButton);
        add(gradientPanel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AdminLoginFrame loginFrame = new AdminLoginFrame();
            loginFrame.setVisible(true);
        });
    }
}


class CourseAssignmentTracker {
    private static final String ASSIGNMENTS_FILE = "src/timetables/course_assignments.dat";
    private static final Map<String, String> courseAssignments = new HashMap<>();
    static {
        loadAssignments();
    }
    private static synchronized void loadAssignments() {
        File file = new File(ASSIGNMENTS_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                courseAssignments.putAll((HashMap<String, String>) ois.readObject());
            } catch (Exception e) {
                System.err.println("Error loading course assignments: " + e.getMessage());
            }
        }
    }
    private static synchronized void saveAssignments() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ASSIGNMENTS_FILE))) {
            oos.writeObject(new HashMap<>(courseAssignments));
        } catch (IOException e) {
            System.err.println("Error saving course assignments: " + e.getMessage());
        }
    }
    private static String generateKey(String batch, String dept, String section, String course) {
        return String.format("%s|%s|%s|%s",
                batch.trim().toLowerCase(),
                dept.trim().toLowerCase(),
                section.trim().toLowerCase(),
                course.trim().toLowerCase());
    }
    public static synchronized boolean isCourseAssigned(String batch, String dept, String section, String course) {
        return courseAssignments.containsKey(generateKey(batch, dept, section, course));
    }
    public static synchronized boolean assignCourse(String batch, String dept, String section,
                                                    String course, String teacherName) {
        String key = generateKey(batch, dept, section, course);
        if (courseAssignments.containsKey(key)) {
            return false;
        }
        courseAssignments.put(key, teacherName);
        saveAssignments();
        return true;
    }
    public static synchronized boolean removeAssignment(String batch, String dept, String section, String course) {
        String key = generateKey(batch, dept, section, course);
        if (courseAssignments.containsKey(key)) {
            courseAssignments.remove(key);
            saveAssignments();
            return true;
        }
        return false;
    }
    public static synchronized boolean removeAssignmentCaseInsensitive(String batch, String dept, String section, String course) {
        String targetKey = generateKey(batch, dept, section, course).toLowerCase();
        for (String key : new ArrayList<>(courseAssignments.keySet())) {
            if (key.toLowerCase().equals(targetKey)) {
                courseAssignments.remove(key);
                saveAssignments();
                return true;
            }
        }
        return false;
    }
    public static synchronized String getAssignedTeacher(String batch, String dept, String section, String course) {
        return courseAssignments.getOrDefault(generateKey(batch, dept, section, course), "No teacher assigned");
    }
}

class RoomAllocationManager {
    private static final String ROOM_FILE = "src/timetables/room_assignments.dat";
    private static Map<String, String> roomAssignments = new HashMap<>();

    static {
        loadRoomAssignments();
    }

    private static synchronized void loadRoomAssignments() {
        File file = new File(ROOM_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                roomAssignments = (Map<String, String>) ois.readObject();
            } catch (Exception e) {
                System.err.println("Error loading room assignments: " + e.getMessage());
                roomAssignments = new HashMap<>();
            }
        }
    }
    private static synchronized void saveRoomAssignments() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ROOM_FILE))) {
            oos.writeObject(roomAssignments);
        } catch (Exception e) {
            System.err.println("Error saving room assignments: " + e.getMessage());
        }
    }
    public static synchronized boolean isRoomAvailable(String batch, String day, String timeslot, String room) {
        String key = generateKey(batch, day, timeslot, room);
        return !roomAssignments.containsKey(key);
    }
    public static synchronized boolean assignRoom(String batch, String day, String timeslot, String room) {
        String key = generateKey(batch, day, timeslot, room);
        roomAssignments.put(key, "occupied");
        saveRoomAssignments();
        return true;
    }
    public static synchronized void releaseRoom(String batch, String day, String timeslot, String room) {
        String key = generateKey(batch, day, timeslot, room);
        roomAssignments.remove(key);
        saveRoomAssignments();
    }
    public static synchronized List<String> getAvailableRooms(String batch, String day, String timeslot, List<String> allRooms) {
        List<String> available = new ArrayList<>();
        for (String room : allRooms) {
            if (isRoomAvailable(batch, day, timeslot, room)) {
                available.add(room);
            }
        }
        return available;
    }
    private static String generateKey(String batch, String day, String timeslot, String room) {
        return String.format("%s|%s|%s|%s",
                batch.toLowerCase(),
                day.toLowerCase(),
                timeslot.toLowerCase(),
                room.toLowerCase());
    }
}

class AdminDashboardFrame extends JFrame {
    private Administrative admin;
    private String currentTeacherName;
    private static final String[] ORDERED_DAYS = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};

    public AdminDashboardFrame(Administrative admin) {
        this.admin = admin;
        setTitle("Administrator Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Color color1 = new Color(230, 240, 255);
                Color color2 = new Color(200, 220, 255);
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, getHeight(), color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel titleLabel = new JLabel("Administrator Dashboard", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 38, 77));
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(10, 1, 10, 10));
        buttonPanel.setOpaque(false);
        JLabel studentLabel = new JLabel("Student Management");
        studentLabel.setFont(new Font("Arial", Font.BOLD, 16));
        studentLabel.setForeground(new Color(0, 38, 77));
        buttonPanel.add(studentLabel);

        buttonPanel.add(createStyledButton("Create Student Timetable", e -> showCreateStudentTimetableDialog()));
        buttonPanel.add(createStyledButton("View Student Timetable", e -> showViewStudentTimetableDialog()));
        buttonPanel.add(createStyledButton("Update Student Timetable", e -> showUpdateStudentTimetableDialog()));
        buttonPanel.add(createStyledButton("Delete Student Timetable", e -> showDeleteStudentTimetableDialog()));

        JLabel teacherLabel = new JLabel("Teacher Management");
        teacherLabel.setFont(new Font("Arial", Font.BOLD, 16));
        teacherLabel.setForeground(new Color(0, 38, 77));
        buttonPanel.add(teacherLabel);

        buttonPanel.add(createStyledButton("Create Teacher Timetable", e -> showCreateTeacherTimetableDialog()));
        buttonPanel.add(createStyledButton("View Teacher Timetable", e -> showViewTeacherTimetableDialog()));
        buttonPanel.add(createStyledButton("Update Teacher Timetable", e -> showUpdateTeacherTimetableDialog()));
        buttonPanel.add(createStyledButton("Delete Teacher Timetable", e -> showDeleteTeacherTimetableDialog()));

        panel.add(buttonPanel, BorderLayout.CENTER);
        add(panel);
    }

    private JButton createStyledButton(String text, ActionListener action) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setBackground(new Color(0, 38, 77));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.setRolloverEnabled(false);
        button.setMargin(new Insets(10, 20, 10, 20));
        button.addActionListener(action);
        return button;
    }


    private void showCreateStudentTimetableDialog() {
        JDialog dialog = new JDialog(this, "Create Student Timetable", true);
        dialog.setSize(1200, 800);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new GradientPanel(new Color(230, 240, 255), new Color(200, 210, 250));
        panel.setLayout(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));


        JSON_FUNCTIONS json = new JSON_FUNCTIONS("C:\\Users\\HP\\OneDrive\\Desktop\\baseclasstimetable.txt");
        Map<String, List<String>> batchRooms = new HashMap<>();
        batchRooms.put("24", Arrays.asList("C-12", "C-13", "C-14", "C-15", "C-16",
                "C-17", "C-18", "C-19", "C-20", "C-21",
                "D-22", "D-23", "D-24", "D-25", "D-26",
                "D-27", "D-28", "E-29", "E-30", "E-31",
                "E-32", "E-33", "E-34", "E-35"));
        batchRooms.put("23", Arrays.asList("A-1", "A-2", "A-3", "A-4", "A-5",
                "A-6", "A-7", "A-8", "B-9", "B-10", "B-11"));

        JPanel formPanel = new GradientPanel(new Color(255, 255, 255), new Color(230, 230, 255));
        formPanel.setLayout(new GridLayout(4, 2, 10, 15));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Basic Information"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        formPanel.setBackground(new Color(250, 250, 255));

        JComboBox<String> batchCombo = createStyledComboBox();
        for (String batch : json.getBatches()) {
            batchCombo.addItem(batch);
        }

        JComboBox<String> deptCombo = createStyledComboBox();
        JTextField sectionField = new JTextField();
        sectionField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 210)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        addStyledLabel(formPanel, "Batch:");
        formPanel.add(batchCombo);
        addStyledLabel(formPanel, "Department:");
        formPanel.add(deptCombo);
        addStyledLabel(formPanel, "Section:");
        formPanel.add(sectionField);

        formPanel.add(new JLabel());
        formPanel.add(new JLabel());

        batchCombo.addActionListener(e -> {
            String selectedBatch = (String) batchCombo.getSelectedItem();
            deptCombo.removeAllItems();
            if (selectedBatch != null) {
                for (String dept : json.getDepartments(selectedBatch)) {
                    deptCombo.addItem(dept);
                }
            }
        });

        if (batchCombo.getItemCount() > 0) {
            batchCombo.setSelectedIndex(0);
        }

        String[] timeSlots = {"8-9AM", "9-10AM", "10-11AM", "11-12PM"};
        String[] days = ORDERED_DAYS;

        JPanel schedulePanel = new JPanel(new GridLayout(days.length, 1, 10, 10));
        schedulePanel.setBackground(new Color(240, 240, 245));

        JScrollPane scrollPane = new JScrollPane(schedulePanel);
        scrollPane.setPreferredSize(new Dimension(1150, 600));
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Weekly Schedule"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JComboBox<String>[][] courseCombos = new JComboBox[days.length][timeSlots.length];
        JComboBox<String>[][] timeCombos = new JComboBox[days.length][timeSlots.length];
        JComboBox<String>[][] roomCombos = new JComboBox[days.length][timeSlots.length];

        for (int dayIdx = 0; dayIdx < days.length; dayIdx++) {
            final int finalDayIdx = dayIdx;
            JPanel dayPanel = new GradientPanel(new Color(250, 250, 255), new Color(220, 220, 250));
            dayPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
            dayPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 220, 230)),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));

            JLabel dayLabel = new JLabel(days[dayIdx]);
            dayLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            dayLabel.setForeground(new Color(70, 70, 90));
            dayLabel.setPreferredSize(new Dimension(80, 25));
            dayPanel.add(dayLabel);

            for (int slotIdx = 0; slotIdx < timeSlots.length; slotIdx++) {
                final int finalSlotIdx = slotIdx;

                courseCombos[dayIdx][slotIdx] = createStyledComboBox();
                courseCombos[dayIdx][slotIdx].setPreferredSize(new Dimension(180, 28));
                courseCombos[dayIdx][slotIdx].addItem("");
                dayPanel.add(courseCombos[dayIdx][slotIdx]);

                timeCombos[dayIdx][slotIdx] = createStyledComboBox(timeSlots);
                timeCombos[dayIdx][slotIdx].setPreferredSize(new Dimension(120, 28));
                timeCombos[dayIdx][slotIdx].setEnabled(false);
                dayPanel.add(timeCombos[dayIdx][slotIdx]);

                roomCombos[dayIdx][slotIdx] = createStyledComboBox();
                roomCombos[dayIdx][slotIdx].setPreferredSize(new Dimension(100, 28));
                roomCombos[dayIdx][slotIdx].setEnabled(false);
                dayPanel.add(roomCombos[dayIdx][slotIdx]);

                if (slotIdx < timeSlots.length - 1) {
                    dayPanel.add(Box.createHorizontalStrut(15));
                }

                courseCombos[dayIdx][slotIdx].addActionListener(e -> {
                    boolean hasSelection = courseCombos[finalDayIdx][finalSlotIdx].getSelectedItem() != null
                            && !courseCombos[finalDayIdx][finalSlotIdx].getSelectedItem().toString().isEmpty();
                    timeCombos[finalDayIdx][finalSlotIdx].setEnabled(hasSelection);
                    roomCombos[finalDayIdx][finalSlotIdx].setEnabled(hasSelection);
                    if (!hasSelection) {
                        timeCombos[finalDayIdx][finalSlotIdx].setSelectedIndex(0);
                        roomCombos[finalDayIdx][finalSlotIdx].removeAllItems();
                        updateSimpleTimes(finalDayIdx, timeCombos, roomCombos, timeSlots);
                    }
                });

                timeCombos[dayIdx][slotIdx].addActionListener(e -> {
                    updateSimpleTimes(finalDayIdx, timeCombos, roomCombos, timeSlots);
                    String batch = (String) batchCombo.getSelectedItem();
                    String day = days[finalDayIdx];
                    String timeSlot = (String) timeCombos[finalDayIdx][finalSlotIdx].getSelectedItem();
                    roomCombos[finalDayIdx][finalSlotIdx].removeAllItems();
                    if (batch != null && timeSlot != null && batchRooms.containsKey(batch)) {
                        List<String> availableRooms = RoomAllocationManager.getAvailableRooms(
                                batch, day, timeSlot, batchRooms.get(batch));
                        for (String room : availableRooms) {
                            roomCombos[finalDayIdx][finalSlotIdx].addItem(room);
                        }
                    }
                });
            }
            schedulePanel.add(dayPanel);
        }

        batchCombo.addActionListener(e -> populateCoursesWithEmptyDefault(batchCombo, deptCombo, courseCombos, json));
        deptCombo.addActionListener(e -> populateCoursesWithEmptyDefault(batchCombo, deptCombo, courseCombos, json));

        JPanel mainPanel = new GradientPanel(new Color(240, 240, 255), new Color(220, 220, 250));
        mainPanel.setLayout(new BorderLayout(10, 10));

        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JButton createButton = new JButton("Create Timetable");
        styleButton(createButton);
        createButton.addActionListener(e -> {
            String batch = (String) batchCombo.getSelectedItem();
            String department = (String) deptCombo.getSelectedItem();
            String section = sectionField.getText().trim();

            if (batch == null || department == null || section.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please select all required fields",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            List<String> tempRoomAssignments = new ArrayList<>();
            for (int dayIdx = 0; dayIdx < days.length; dayIdx++) {
                for (int slotIdx = 0; slotIdx < timeSlots.length; slotIdx++) {
                    String course = (String) courseCombos[dayIdx][slotIdx].getSelectedItem();
                    String timeSlot = (String) timeCombos[dayIdx][slotIdx].getSelectedItem();
                    String room = (String) roomCombos[dayIdx][slotIdx].getSelectedItem();

                    if (course != null && !course.isEmpty() && timeSlot != null && room != null) {
                        if (!RoomAllocationManager.assignRoom(batch, days[dayIdx], timeSlot, room)) {
                            String conflictingClass = findConflictingClass(batch, days[dayIdx], timeSlot, room);
                            JOptionPane.showMessageDialog(dialog,
                                    "Room " + room + " is already occupied at " + timeSlot + " by " + conflictingClass,
                                    "Room Conflict", JOptionPane.ERROR_MESSAGE);
                            for (String assignment : tempRoomAssignments) {
                                String[] parts = assignment.split("\\|");
                                RoomAllocationManager.releaseRoom(parts[0], parts[1], parts[2], parts[3]);
                            }
                            return;
                        }
                        tempRoomAssignments.add(batch + "|" + days[dayIdx] + "|" + timeSlot + "|" + room);
                    }
                }
            }

            String filename = String.format("%s_%s_%s.txt", batch, department, section);
            String filepath = "src\\timetables\\" + filename;

            File file = new File(filepath);
            if (file.exists()) {
                int option = JOptionPane.showConfirmDialog(dialog,
                        "Timetable for Section '" + section + "' already exists! Overwrite?",
                        "Warning", JOptionPane.YES_NO_OPTION);
                if (option != JOptionPane.YES_OPTION) {
                    for (String assignment : tempRoomAssignments) {
                        String[] parts = assignment.split("\\|");
                        RoomAllocationManager.releaseRoom(parts[0], parts[1], parts[2], parts[3]);
                    }
                    return;
                }
            }

            Timetable timetable = new Timetable();
            timetable.setBatch(batch);
            timetable.setDepartment(department);
            timetable.setSection(section);

            for (String day : ORDERED_DAYS) {
                int dayIdx = Arrays.asList(days).indexOf(day);
                List<String> periods = new ArrayList<>();

                for (int slotIdx = 0; slotIdx < timeSlots.length; slotIdx++) {
                    String course = (String) courseCombos[dayIdx][slotIdx].getSelectedItem();
                    String time = (String) timeCombos[dayIdx][slotIdx].getSelectedItem();
                    String room = (String) roomCombos[dayIdx][slotIdx].getSelectedItem();

                    if (course != null && !course.isEmpty() && time != null && room != null) {
                        periods.add(String.format("%s (%s) [%s]", course, time, room));
                    } else {
                        periods.add("");
                    }
                }
                timetable.addDaySchedule(day, periods.toArray(new String[0]));
            }

            File directory = new File("src\\timetables");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            try (FileWriter writer = new FileWriter(file)) {
                writer.write(timetable.toString());
                JOptionPane.showMessageDialog(dialog,
                        "Timetable created successfully!\nSaved as: " + filename,
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                json.addSection(batch, department, section);
                json.saveData();
                dialog.dispose();
            } catch (IOException ex) {
                for (String assignment : tempRoomAssignments) {
                    String[] parts = assignment.split("\\|");
                    RoomAllocationManager.releaseRoom(parts[0], parts[1], parts[2], parts[3]);
                }
                JOptionPane.showMessageDialog(dialog,
                        "Failed to create timetable. Error: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(240, 240, 245));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        buttonPanel.add(createButton);

        panel.add(mainPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }
    class GradientPanel extends JPanel {
        private Color startColor;
        private Color endColor;

        public GradientPanel(Color startColor, Color endColor) {
            this.startColor = startColor;
            this.endColor = endColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            int w = getWidth();
            int h = getHeight();

            GradientPaint gp = new GradientPaint(0, 0, startColor, 0, h, endColor);
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, w, h);
            g2d.dispose();

            super.paintComponent(g);
        }
    }

    private JComboBox<String> createStyledComboBox(String... items) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setBackground(Color.WHITE);
        combo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 210)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        return combo;
    }

    private void addStyledLabel(JPanel panel, String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(new Color(70, 70, 90));
        panel.add(label);
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.PLAIN, 16));
        button.setBackground(new Color(0, 38, 77));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(15, 50, 15, 50));
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
    }




    private void populateCoursesWithEmptyDefault(JComboBox<String> batchCombo,
                                                 JComboBox<String> deptCombo,
                                                 JComboBox<String>[][] courseCombos,
                                                 JSON_FUNCTIONS json) {
        String batch = (String) batchCombo.getSelectedItem();
        String dept = (String) deptCombo.getSelectedItem();

        if (batch != null && dept != null) {
            List<String> courses = json.getCourses(batch, dept);

            for (int i = 0; i < courseCombos.length; i++) {
                for (int j = 0; j < courseCombos[i].length; j++) {
                    String currentSelection = (String) courseCombos[i][j].getSelectedItem();
                    courseCombos[i][j].removeAllItems();
                    courseCombos[i][j].addItem("");
                    for (String course : courses) {
                        courseCombos[i][j].addItem(course);
                    }
                    if (currentSelection != null && courses.contains(currentSelection)) {
                        courseCombos[i][j].setSelectedItem(currentSelection);
                    }
                }
            }
        }
    }


    private void updateSimpleTimes(int dayIndex, JComboBox<String>[][] timeCombos,
                                   JComboBox<String>[][] roomCombos, String[] allSlots) {
        List<String> selected = new ArrayList<>();
        Map<Integer, String> roomSelections = new HashMap<>();

        for (int j = 0; j < allSlots.length; j++) {
            String selectedTime = (String) timeCombos[dayIndex][j].getSelectedItem();
            if (selectedTime != null && !selectedTime.isEmpty()) {
                selected.add(selectedTime);
            }
            roomSelections.put(j, (String) roomCombos[dayIndex][j].getSelectedItem());
        }

        for (int j = 0; j < allSlots.length; j++) {
            String currentSelected = (String) timeCombos[dayIndex][j].getSelectedItem();
            String currentRoom = roomSelections.get(j);
            timeCombos[dayIndex][j].removeAllItems();

            for (String slot : allSlots) {
                if (!selected.contains(slot) || slot.equals(currentSelected)) {
                    timeCombos[dayIndex][j].addItem(slot);
                }
            }
            timeCombos[dayIndex][j].setSelectedItem(currentSelected);

            if (currentSelected != null && currentRoom != null) {
                roomCombos[dayIndex][j].setSelectedItem(currentRoom);
            }
        }
    }

    private String findConflictingClass(String batch, String day, String timeslot, String room) {
        File timetableDir = new File("src/timetables");
        if (!timetableDir.exists()) return "unknown class";

        File[] batchFiles = timetableDir.listFiles((dir, name) ->
                name.startsWith(batch + "_") && name.endsWith(".txt"));

        if (batchFiles == null) return "unknown class";

        for (File file : batchFiles) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains(day + ":") && line.contains("[" + room + "]") && line.contains(timeslot)) {
                        String filename = file.getName();
                        String classId = filename.substring(0, filename.lastIndexOf('.'));
                        String course = line.split("\\(")[0].trim();
                        return classId + " (" + course + ")";
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return "unknown class";
    }

    private void showCreateTeacherTimetableDialog() {
        JDialog dialog = new JDialog(this, "Create Teacher Timetable", true);
        dialog.setSize(1100, 850);
        dialog.setLocationRelativeTo(this);

        currentTeacherName = null;

        JPanel panel = new GradientPanel(new Color(230, 240, 255), new Color(200, 210, 250));
        panel.setLayout(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JSON_FUNCTIONS json = new JSON_FUNCTIONS("C:\\Users\\HP\\OneDrive\\Desktop\\baseclasstimetable.txt");

        JPanel formPanel = new GradientPanel(new Color(255, 255, 255, 220), new Color(240, 240, 255, 220));
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Teacher Timetable Creator"),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        addStyledLabel(formPanel, "Teacher Name:", gbc);
        JTextField teacherNameField = new JTextField(25);
        styleTextField(teacherNameField);
        gbc.gridx = 1; gbc.gridy = 0;
        formPanel.add(teacherNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        addStyledLabel(formPanel, "Course Assignments:", gbc);

        JPanel assignmentsPanel = new JPanel();
        assignmentsPanel.setLayout(new BoxLayout(assignmentsPanel, BoxLayout.Y_AXIS));
        assignmentsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        assignmentsPanel.setBackground(new Color(245, 245, 255));

        JScrollPane assignmentsScrollPane = new JScrollPane(assignmentsPanel);
        assignmentsScrollPane.setPreferredSize(new Dimension(900, 220));
        assignmentsScrollPane.setBorder(BorderFactory.createEmptyBorder());
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        formPanel.add(assignmentsScrollPane, gbc);

        JButton addAssignmentButton = new JButton("Add Course Assignment");
        styleButton(addAssignmentButton, false);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        formPanel.add(addAssignmentButton, gbc);

        JTextArea timetableArea = new JTextArea(15, 60);
        timetableArea.setEditable(false);
        timetableArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        timetableArea.setBackground(Color.WHITE);
        timetableArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 210)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JScrollPane timetableScrollPane = new JScrollPane(timetableArea);
        timetableScrollPane.setBorder(BorderFactory.createTitledBorder("Generated Timetable"));
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.BOTH;
        formPanel.add(timetableScrollPane, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        buttonPanel.setOpaque(false);

        JButton generateButton = new JButton("Generate Timetable");
        styleButton(generateButton, true);
        buttonPanel.add(generateButton);

        JButton saveButton = new JButton("Save Timetable");
        styleButton(saveButton, true);
        buttonPanel.add(saveButton);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(buttonPanel, gbc);

        List<String> pendingAssignments = new ArrayList<>();

        addAssignmentButton.addActionListener(e -> {
            JPanel assignmentPanel = new GradientPanel(new Color(250, 250, 255), new Color(240, 240, 250));
            assignmentPanel.setLayout(new GridBagLayout());
            assignmentPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(210, 210, 230)),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));

            GridBagConstraints agbc = new GridBagConstraints();
            agbc.insets = new Insets(3, 3, 3, 3);
            agbc.anchor = GridBagConstraints.WEST;

            agbc.gridx = 0; agbc.gridy = 0;
            addStyledLabel(assignmentPanel, "Batch:", agbc);
            JComboBox<String> batchCombo = createStyledComboBox();
            batchCombo.setPreferredSize(new Dimension(200, 28));
            for (String batch : json.getBatches()) batchCombo.addItem(batch);
            agbc.gridx = 1; agbc.gridy = 0;
            assignmentPanel.add(batchCombo, agbc);

            agbc.gridx = 0; agbc.gridy = 1;
            addStyledLabel(assignmentPanel, "Department:", agbc);
            JComboBox<String> deptCombo = createStyledComboBox();
            deptCombo.setPreferredSize(new Dimension(200, 28));
            agbc.gridx = 1; agbc.gridy = 1;
            assignmentPanel.add(deptCombo, agbc);

            agbc.gridx = 0; agbc.gridy = 2;
            addStyledLabel(assignmentPanel, "Section:", agbc);
            JComboBox<String> sectionCombo = createStyledComboBox();
            sectionCombo.setPreferredSize(new Dimension(200, 28));
            agbc.gridx = 1; agbc.gridy = 2;
            assignmentPanel.add(sectionCombo, agbc);

            agbc.gridx = 0; agbc.gridy = 3;
            addStyledLabel(assignmentPanel, "Course:", agbc);
            JComboBox<String> courseCombo = createStyledComboBox();
            courseCombo.setPreferredSize(new Dimension(200, 28));
            agbc.gridx = 1; agbc.gridy = 3;
            assignmentPanel.add(courseCombo, agbc);

            JButton removeButton = new JButton("Remove");
            styleButton(removeButton, false);
            agbc.gridx = 2; agbc.gridy = 0; agbc.gridheight = 4;
            agbc.anchor = GridBagConstraints.CENTER;
            assignmentPanel.add(removeButton, agbc);

            batchCombo.addActionListener(evt -> {
                deptCombo.removeAllItems();
                String batch = (String) batchCombo.getSelectedItem();
                if (batch != null) {
                    for (String dept : json.getDepartments(batch)) deptCombo.addItem(dept);
                }
            });

            deptCombo.addActionListener(evt -> {
                sectionCombo.removeAllItems();
                courseCombo.removeAllItems();
                String batch = (String) batchCombo.getSelectedItem();
                String dept = (String) deptCombo.getSelectedItem();
                if (batch != null && dept != null) {
                    for (String section : json.getSections(batch, dept)) sectionCombo.addItem(section);
                    for (String course : json.getCourses(batch, dept)) courseCombo.addItem(course);
                }
            });

            if (batchCombo.getItemCount() > 0) batchCombo.setSelectedIndex(0);

            removeButton.addActionListener(evt -> {
                assignmentsPanel.remove(assignmentPanel);
                assignmentsPanel.revalidate();
                assignmentsPanel.repaint();
            });

            assignmentsPanel.add(assignmentPanel);
            assignmentsPanel.add(Box.createVerticalStrut(10));
            assignmentsPanel.revalidate();
            assignmentsPanel.repaint();
        });

        generateButton.addActionListener(e -> {
            currentTeacherName = teacherNameField.getText().trim();
            if (currentTeacherName.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Please enter teacher name",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (assignmentsPanel.getComponentCount() == 0) {
                JOptionPane.showMessageDialog(dialog,
                        "Please add at least one course assignment",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            pendingAssignments.clear();

            class CourseEntry {
                String batch;
                String dept;
                String section;
                String course;
                List<String> entries = new ArrayList<>();
            }

            List<CourseEntry> allCourses = new ArrayList<>();
            Map<String, List<String>> timetableMap = new HashMap<>();
            String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
            for (String day : days) timetableMap.put(day, new ArrayList<>());

            StringBuilder timetableBuilder = new StringBuilder();
            timetableBuilder.append("Timetable for ").append(currentTeacherName).append("\n\n");

            for (Component comp : assignmentsPanel.getComponents()) {
                if (comp instanceof JPanel) {
                    JPanel assignmentPanel = (JPanel) comp;
                    String batch = getSelectedValueFromPanel(assignmentPanel, "Batch");
                    String dept = getSelectedValueFromPanel(assignmentPanel, "Department");
                    String section = getSelectedValueFromPanel(assignmentPanel, "Section");
                    String course = getSelectedValueFromPanel(assignmentPanel, "Course");

                    if (batch != null && dept != null && section != null && course != null) {
                        if (CourseAssignmentTracker.isCourseAssigned(batch, dept, section, course)) {
                            String currentTeacher = CourseAssignmentTracker.getAssignedTeacher(batch, dept, section, course);
                            JOptionPane.showMessageDialog(dialog,
                                    String.format("%s (%s %s %s) is already assigned to %s",
                                            course, batch, dept, section, currentTeacher),
                                    "Assignment Conflict", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        pendingAssignments.add(batch + "|" + dept + "|" + section + "|" + course);
                    }
                }
            }
            for (Component comp : assignmentsPanel.getComponents()) {
                if (comp instanceof JPanel) {
                    JPanel assignmentPanel = (JPanel) comp;
                    CourseEntry courseEntry = new CourseEntry();
                    for (Component ac : assignmentPanel.getComponents()) {
                        if (ac instanceof JComboBox<?>) {
                            JComboBox<?> combo = (JComboBox<?>) ac;
                            String value = (String) combo.getSelectedItem();
                            if (value == null) continue;
                            Component[] components = assignmentPanel.getComponents();
                            int index = assignmentPanel.getComponentZOrder(combo);
                            if (index > 0 && components[index - 1] instanceof JLabel) {
                                String label = ((JLabel) components[index - 1]).getText();
                                switch (label) {
                                    case "Batch:" -> courseEntry.batch = value;
                                    case "Department:" -> courseEntry.dept = value;
                                    case "Section:" -> courseEntry.section = value;
                                    case "Course:" -> courseEntry.course = value;
                                }
                            }
                        }
                    }

                    if (courseEntry.batch == null || courseEntry.dept == null ||
                            courseEntry.section == null || courseEntry.course == null) {
                        timetableBuilder.append("Incomplete assignment information.\n\n");
                        continue;
                    }

                    timetableBuilder.append("Course: ").append(courseEntry.course)
                            .append(" (").append(courseEntry.batch).append(" ")
                            .append(courseEntry.dept).append(" ")
                            .append(courseEntry.section).append(")\n");

                    String filename = "src/timetables/" + courseEntry.batch + "_" + courseEntry.dept + "_" + courseEntry.section + ".txt";
                    File file = new File(filename);

                    if (!file.exists()) {
                        timetableBuilder.append("  Timetable file not found for this class\n\n");
                        continue;
                    }

                    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                        String line;
                        String currentDay = null;
                        boolean courseFound = false;

                        while ((line = reader.readLine()) != null) {
                            line = line.trim();
                            if (line.isEmpty()) continue;

                            if (line.endsWith(":")) {
                                currentDay = line.substring(0, line.length() - 1).trim();
                                continue;
                            }

                            if (currentDay != null) {
                                if (line.startsWith(courseEntry.course + " (") ||
                                        line.startsWith(courseEntry.course + " ") ||
                                        line.contains(" " + courseEntry.course + " ") ||
                                        line.equals(courseEntry.course)) {

                                    String entry = line.trim();
                                    String fullEntry = String.format("%s (%s %s %s)",
                                            entry,
                                            courseEntry.batch,
                                            courseEntry.dept,
                                            courseEntry.section);

                                    timetableMap.get(currentDay).add(fullEntry);
                                    courseEntry.entries.add(currentDay + ": " + entry);
                                    timetableBuilder.append("  ").append(currentDay).append(": ").append(entry).append("\n");
                                    courseFound = true;
                                }
                            }
                        }

                        if (!courseFound) {
                            timetableBuilder.append("  Course not found in class timetable\n");
                        }
                        timetableBuilder.append("\n");
                        allCourses.add(courseEntry);
                    } catch (IOException ex) {
                        timetableBuilder.append("  Error reading timetable file: ").append(ex.getMessage()).append("\n\n");
                    }
                }
            }
            timetableBuilder.append("\nConsolidated Timetable:\n");
            for (String day : days) {
                if (!timetableMap.get(day).isEmpty()) {
                    timetableBuilder.append(day).append(":\n");
                    for (String slot : timetableMap.get(day)) {
                        timetableBuilder.append("  ").append(slot).append("\n");
                    }
                }
            }

            timetableArea.setText(timetableBuilder.toString());
        });

        saveButton.addActionListener(e -> {
            if (currentTeacherName == null || currentTeacherName.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Please generate timetable first",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (timetableArea.getText().isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "No timetable to save",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (pendingAssignments.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "No course assignments to save",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(dialog,
                    "This will save the timetable and assign all courses to " + currentTeacherName +
                            "\nAre you sure you want to continue?",
                    "Confirm Save",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            try {
                File dir = new File("src/timetables");
                if (!dir.exists()) dir.mkdirs();

                String filename = "src/timetables/" + currentTeacherName.replace(" ", "_") + "_timetable.txt";

                File teacherFile = new File(filename);
                if (teacherFile.exists()) {
                    int overwrite = JOptionPane.showConfirmDialog(dialog,
                            "Timetable file already exists. Overwrite?",
                            "File Exists",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE);

                    if (overwrite != JOptionPane.YES_OPTION) {
                        return;
                    }
                }
                try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
                    writer.println(timetableArea.getText());
                }
                boolean allAssignmentsSuccessful = true;
                List<String> successfulAssignments = new ArrayList<>();

                for (String assignment : pendingAssignments) {
                    String[] parts = assignment.split("\\|");
                    if (CourseAssignmentTracker.assignCourse(parts[0], parts[1], parts[2], parts[3], currentTeacherName)) {
                        successfulAssignments.add(assignment);
                    } else {
                        allAssignmentsSuccessful = false;
                        break;
                    }
                }

                if (allAssignmentsSuccessful) {
                    JOptionPane.showMessageDialog(dialog,
                            "Timetable saved and courses assigned successfully!",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    pendingAssignments.clear();
                } else {
                    for (String assignment : successfulAssignments) {
                        String[] parts = assignment.split("\\|");
                        CourseAssignmentTracker.removeAssignment(parts[0], parts[1], parts[2], parts[3]);
                    }
                    teacherFile.delete();
                    JOptionPane.showMessageDialog(dialog,
                            "Failed to assign some courses. Changes have been rolled back.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Error saving timetable: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(formPanel, BorderLayout.CENTER);
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void addStyledLabel(JPanel panel, String text, GridBagConstraints gbc) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(new Color(70, 70, 90));
        panel.add(label, gbc);
    }

    private void styleTextField(JTextField field) {
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 210)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    }
    private void styleButton(JButton button, boolean isPrimary) {

        button.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 13));
        button.setBackground(new Color(0, 38, 77));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(0, 28, 67)),
                BorderFactory.createEmptyBorder(6, 18, 6, 18)
        ));

        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);

        button.setBorderPainted(false);
    }

    private JComboBox<String> createStyledComboBox() {
        JComboBox<String> combo = new JComboBox<>();
        combo.setBackground(Color.WHITE);
        combo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 210)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        return combo;
    }


    private String getSelectedValueFromPanel(JPanel panel, String fieldName) {
        for (Component comp : panel.getComponents()) {
            if (comp instanceof JLabel) {
                JLabel label = (JLabel) comp;
                if (label.getText().equals(fieldName + ":")){
                    int index = panel.getComponentZOrder(label);
                    if (index < panel.getComponentCount() - 1) {
                        Component nextComp = panel.getComponent(index + 1);
                        if (nextComp instanceof JComboBox) {
                            return (String) ((JComboBox<?>) nextComp).getSelectedItem();
                        } else if (nextComp instanceof JTextField) {
                            return ((JTextField) nextComp).getText();
                        }
                    }
                }
            }
        }
        return null;
    }

    private void showViewStudentTimetableDialog() {
        JSON_FUNCTIONS json = new JSON_FUNCTIONS("C:\\Users\\HP\\OneDrive\\Desktop\\baseclasstimetable.txt");

        final Color PRIMARY_COLOR = new Color(0, 51, 102);
        final Color BACKGROUND_COLOR = new Color(240, 245, 255);

        JDialog dialog = new JDialog(this, "View Student Timetable", true);
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Select Timetable Details", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JComboBox<String> batchCombo = new JComboBox<>();
        JComboBox<String> deptCombo = new JComboBox<>();
        JComboBox<String> sectionCombo = new JComboBox<>();

        for (String batch : json.getBatches()) batchCombo.addItem(batch);

        batchCombo.addActionListener(e -> {
            String selectedBatch = (String) batchCombo.getSelectedItem();
            deptCombo.removeAllItems();
            sectionCombo.removeAllItems();
            if (selectedBatch != null) {
                for (String dept : json.getDepartments(selectedBatch)) {
                    deptCombo.addItem(dept);
                }
            }
        });

        deptCombo.addActionListener(e -> {
            String selectedBatch = (String) batchCombo.getSelectedItem();
            String selectedDept = (String) deptCombo.getSelectedItem();
            sectionCombo.removeAllItems();
            if (selectedBatch != null && selectedDept != null) {
                for (String sec : json.getSections(selectedBatch, selectedDept)) {
                    sectionCombo.addItem(sec);
                }
            }
        });

        if (batchCombo.getItemCount() > 0) batchCombo.setSelectedIndex(0);

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 15));
        formPanel.setOpaque(false);

        JLabel batchLabel = new JLabel("Batch:");
        batchLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        batchLabel.setForeground(PRIMARY_COLOR);
        formPanel.add(batchLabel);
        formPanel.add(batchCombo);

        JLabel deptLabel = new JLabel("Department:");
        deptLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        deptLabel.setForeground(PRIMARY_COLOR);
        formPanel.add(deptLabel);
        formPanel.add(deptCombo);

        JLabel sectionLabel = new JLabel("Section:");
        sectionLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        sectionLabel.setForeground(PRIMARY_COLOR);
        formPanel.add(sectionLabel);
        formPanel.add(sectionCombo);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setOpaque(false);

        JButton okButton = new JButton("View Timetable");
        styleButton(okButton, true);
        okButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JButton cancelButton = new JButton("Cancel");
        styleButton(cancelButton, false);
        cancelButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setContentPane(mainPanel);

        okButton.addActionListener(e -> {
            String batch = (String) batchCombo.getSelectedItem();
            String dept = ((String) deptCombo.getSelectedItem()).toUpperCase();
            String section = ((String) sectionCombo.getSelectedItem()).toUpperCase();

            String fileName = batch + "_" + dept + "_" + section + ".txt";
            String filePath = "src\\timetables\\" + fileName;

            JDialog timetableDialog = new JDialog(dialog, batch + " - " + dept + " - " + section, true);
            timetableDialog.setSize(800, 600);
            timetableDialog.setLocationRelativeTo(dialog);

            JPanel panel = new JPanel(new BorderLayout(10, 10));
            panel.setBackground(BACKGROUND_COLOR);
            panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            JLabel headerLabel = new JLabel(batch + " - " + dept + " - " + section, JLabel.CENTER);
            headerLabel.setFont(new Font("Arial", Font.BOLD, 22));
            headerLabel.setForeground(PRIMARY_COLOR);
            headerLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
            panel.add(headerLabel, BorderLayout.NORTH);

            JTextArea textArea = new JTextArea();
            textArea.setEditable(false);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 16));
            textArea.setMargin(new Insets(10, 10, 10, 10));
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 1));
            panel.add(scrollPane, BorderLayout.CENTER);

            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                textArea.read(reader, null);
            } catch (IOException ex) {
                textArea.setText("Timetable file not found or could not be read.");
            }

            timetableDialog.setContentPane(panel);
            timetableDialog.setVisible(true);

            dialog.dispose();
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void showViewTeacherTimetableDialog() {

        JDialog inputDialog = new JDialog(this, "Enter Teacher Name", true);
        inputDialog.setSize(500, 250);
        inputDialog.setLocationRelativeTo(this);
        inputDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        Color PRIMARY_COLOR = new Color(0, 51, 102);
        Color SECONDARY_COLOR = new Color(200, 220, 255);
        Color BACKGROUND_COLOR = new Color(240, 245, 255);

        JPanel inputPanel = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(0, 0, BACKGROUND_COLOR, 0, getHeight(), SECONDARY_COLOR);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        inputPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel promptLabel = new JLabel("Enter Teacher Full Name:");
        promptLabel.setFont(new Font("Arial", Font.BOLD, 18));
        promptLabel.setForeground(PRIMARY_COLOR);

        JTextField nameField = new JTextField(20);
        nameField.setFont(new Font("Arial", Font.PLAIN, 16));
        nameField.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, PRIMARY_COLOR));
        nameField.setMargin(new Insets(8, 10, 8, 10));

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        centerPanel.add(promptLabel, gbc);
        gbc.gridy = 1;
        centerPanel.add(nameField, gbc);

        inputPanel.add(centerPanel, BorderLayout.CENTER);

        JButton okButton = new JButton("View Timetable") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(PRIMARY_COLOR);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
            }
        };
        okButton.setFont(new Font("Arial", Font.BOLD, 16));
        okButton.setForeground(Color.WHITE);
        okButton.setContentAreaFilled(false);
        okButton.setFocusPainted(false);
        okButton.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        okButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        okButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            inputDialog.dispose();

            if (!name.isEmpty()) {
                String baseDir = "src\\timetables\\";
                String fileName = name.toLowerCase().replace(" ", "_") + "_timetable.txt";
                String filePath = baseDir + fileName;

                JDialog dialog = new JDialog(this, "Teacher Timetable - " + name, false);
                dialog.setSize(900, 700);
                dialog.setLocationRelativeTo(this);
                dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

                JPanel mainPanel = new JPanel(new BorderLayout(10, 10)) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        Graphics2D g2d = (Graphics2D) g;
                        GradientPaint gradient = new GradientPaint(0, 0, BACKGROUND_COLOR, 0, getHeight(), SECONDARY_COLOR);
                        g2d.setPaint(gradient);
                        g2d.fillRect(0, 0, getWidth(), getHeight());
                    }
                };
                mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

                JLabel titleLabel = new JLabel("Teacher Timetable - " + name.toUpperCase(), JLabel.CENTER);
                titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
                titleLabel.setForeground(PRIMARY_COLOR);
                titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
                mainPanel.add(titleLabel, BorderLayout.NORTH);

                JTextArea textArea = new JTextArea();
                textArea.setEditable(false);
                textArea.setFont(new Font("Monospaced", Font.PLAIN, 16));
                textArea.setMargin(new Insets(10, 10, 10, 10));
                textArea.setLineWrap(true);
                textArea.setWrapStyleWord(true);

                JScrollPane scrollPane = new JScrollPane(textArea);
                scrollPane.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 1), "Timetable"),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));
                mainPanel.add(scrollPane, BorderLayout.CENTER);

                try {
                    File file = new File(filePath);
                    if (file.exists()) {
                        StringBuilder content = new StringBuilder();
                        content.append("TIMETABLE FOR ").append(name.toUpperCase()).append("\n\n");
                        BufferedReader reader = new BufferedReader(new FileReader(file));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            content.append(line).append("\n");
                        }
                        reader.close();
                        textArea.setText(content.toString());
                    } else {
                        textArea.setText("No timetable found for teacher: " + name +
                                "\nExpected file: " + fileName);
                    }
                } catch (IOException ex) {
                    textArea.setText("Error reading timetable file: " + ex.getMessage());
                }

                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
                buttonPanel.setOpaque(false);
                JButton closeButton = new JButton("Close") {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g;
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(PRIMARY_COLOR);
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                        super.paintComponent(g);
                    }
                };
                closeButton.setFont(new Font("Arial", Font.BOLD, 16));
                closeButton.setForeground(Color.WHITE);
                closeButton.setContentAreaFilled(false);
                closeButton.setFocusPainted(false);
                closeButton.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
                closeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                closeButton.addActionListener(event -> dialog.dispose());
                buttonPanel.add(closeButton);

                mainPanel.add(buttonPanel, BorderLayout.SOUTH);
                dialog.setContentPane(mainPanel);
                dialog.setVisible(true);
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.add(okButton);

        inputPanel.add(buttonPanel, BorderLayout.SOUTH);
        inputDialog.setContentPane(inputPanel);
        inputDialog.setVisible(true);
    }

    private void showDeleteStudentTimetableDialog() {
        JDialog dialog = new JDialog(this, "Delete Student Timetable", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);

        JSON_FUNCTIONS json = new JSON_FUNCTIONS("C:\\Users\\HP\\OneDrive\\Desktop\\baseclasstimetable.txt");

        JPanel gradientPanel = new GradientPanel(new Color(230, 240, 255), new Color(200, 210, 250));
        gradientPanel.setLayout(new BorderLayout(10, 10));
        gradientPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 12, 18));
        formPanel.setOpaque(false);

        JComboBox<String> batchCombo = new JComboBox<>();
        for (String batch : json.getBatches()) {
            batchCombo.addItem(batch);
        }

        JComboBox<String> deptCombo = new JComboBox<>();
        JComboBox<String> sectionCombo = new JComboBox<>();

        batchCombo.addActionListener(e -> {
            String selectedBatch = (String) batchCombo.getSelectedItem();
            deptCombo.removeAllItems();
            if (selectedBatch != null) {
                for (String dept : json.getDepartments(selectedBatch)) {
                    deptCombo.addItem(dept);
                }
            }
        });

        deptCombo.addActionListener(e -> {
            String selectedBatch = (String) batchCombo.getSelectedItem();
            String selectedDept = (String) deptCombo.getSelectedItem();
            sectionCombo.removeAllItems();
            if (selectedBatch != null && selectedDept != null) {
                for (String section : json.getSections(selectedBatch, selectedDept)) {
                    sectionCombo.addItem(section);
                }
            }
        });

        formPanel.add(new JLabel("Batch:"));
        formPanel.add(batchCombo);
        formPanel.add(new JLabel("Department:"));
        formPanel.add(deptCombo);
        formPanel.add(new JLabel("Section:"));
        formPanel.add(sectionCombo);

        gradientPanel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        JButton deleteButton = new JButton("Delete Section");
        styleButton(deleteButton, true);
        deleteButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        deleteButton.addActionListener(e -> {
            String batch = (String) batchCombo.getSelectedItem();
            String department = (String) deptCombo.getSelectedItem();
            String section = (String) sectionCombo.getSelectedItem();

            if (batch == null || department == null || section == null) {
                JOptionPane.showMessageDialog(dialog, "Please select all required fields", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (json.deleteSection(batch, department, section)) {
                String filename = "src/timetables/" + batch + "_" + department.toUpperCase() + "_" + section + ".txt";
                File fileToDelete = new File(filename);

                if (fileToDelete.exists()) {
                    if (fileToDelete.delete()) {
                        JOptionPane.showMessageDialog(dialog, "Section and timetable file deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(dialog, "Section deleted, but failed to delete timetable file.", "Warning", JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(dialog, "Section deleted, but timetable file not found.", "Info", JOptionPane.INFORMATION_MESSAGE);
                }

                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Error deleting section!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        buttonPanel.add(deleteButton);
        gradientPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(gradientPanel);
        dialog.setVisible(true);
    }

    private void showDeleteTeacherTimetableDialog() {
        final Color PRIMARY_COLOR = new Color(0, 51, 102);
        final Color BACKGROUND_COLOR = new Color(240, 245, 255);

        JDialog dialog = new JDialog(this, "Delete Teacher Timetable", true);
        dialog.setSize(550, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(BACKGROUND_COLOR);

        JLabel titleLabel = new JLabel("Delete Teacher Timetable", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(PRIMARY_COLOR);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        File dir = new File("src/timetables");
        File[] teacherFiles = dir.listFiles((d, name) -> name.endsWith("_timetable.txt"));

        if (teacherFiles == null || teacherFiles.length == 0) {
            JOptionPane.showMessageDialog(dialog, "No teacher timetable files found.", "Info", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
            return;
        }

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel selectLabel = new JLabel("Select Teacher:");
        selectLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        selectLabel.setForeground(PRIMARY_COLOR);

        JComboBox<String> teacherCombo = new JComboBox<>();
        teacherCombo.setPreferredSize(new Dimension(300, 35));
        teacherCombo.setFont(new Font("Arial", Font.PLAIN, 16));

        Map<String, String> teacherToFileMap = new HashMap<>();

        for (File file : teacherFiles) {
            String teacherName = file.getName().replace("_timetable.txt", "").replace("_", " ");
            teacherCombo.addItem(teacherName);
            teacherToFileMap.put(teacherName, file.getName());
        }

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(selectLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(teacherCombo, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setOpaque(false);

        JButton deleteButton = new JButton("Delete Timetable");
        JButton cancelButton = new JButton("Cancel");

        styleButton(deleteButton, true);
        styleButton(cancelButton, false);

        deleteButton.addActionListener(e -> {
            String teacherName = (String) teacherCombo.getSelectedItem();
            String fileName = teacherToFileMap.get(teacherName);

            if (fileName != null) {
                File fileToDelete = new File("src/timetables/" + fileName);

                try {
                    List<String[]> assignedCourses = new ArrayList<>();
                    try (BufferedReader reader = new BufferedReader(new FileReader(fileToDelete))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            if (line.startsWith("Course: ")) {
                                String coursePart = line.substring("Course: ".length());
                                String course = coursePart.substring(0, coursePart.indexOf("(")).trim();
                                String details = line.substring(line.indexOf("(") + 1, line.indexOf(")"));
                                String[] parts = details.split(" ");
                                if (parts.length >= 3) {
                                    assignedCourses.add(new String[]{parts[0], parts[1], parts[2], course});
                                }
                            }
                        }
                    }

                    boolean allRemoved = true;
                    for (String[] courseInfo : assignedCourses) {
                        String batch = courseInfo[0];
                        String dept = courseInfo[1];
                        String section = courseInfo[2];
                        String course = courseInfo[3];

                        boolean removed = CourseAssignmentTracker.removeAssignment(batch, dept, section, course);
                        if (!removed) {
                            removed = CourseAssignmentTracker.removeAssignmentCaseInsensitive(batch, dept, section, course);
                        }
                        allRemoved = allRemoved && removed;
                        releaseRoomAssignmentsForCourse(batch, dept, section, course);
                    }

                    if (fileToDelete.delete()) {
                        String message = "Timetable deleted successfully!";
                        if (!allRemoved) {
                            message += "\nNote: Some course assignments couldn't be removed from tracking system.";
                        }
                        JOptionPane.showMessageDialog(dialog, message, "Success", JOptionPane.INFORMATION_MESSAGE);
                        dialog.dispose();
                    } else {
                        for (String[] courseInfo : assignedCourses) {
                            CourseAssignmentTracker.assignCourse(courseInfo[0], courseInfo[1], courseInfo[2], courseInfo[3], teacherName);
                        }
                        JOptionPane.showMessageDialog(dialog, "Error deleting timetable file.", "Error", JOptionPane.ERROR_MESSAGE);
                    }

                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(dialog, "Error processing timetable: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(deleteButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setContentPane(mainPanel);
        dialog.setVisible(true);
    }


    private void showUpdateTeacherTimetableDialog() {
        final Color PRIMARY_COLOR = new Color(0, 51, 102);
        final Color BACKGROUND_COLOR = new Color(240, 245, 255);

        JDialog dialog = new JDialog(this, "Update Teacher Timetable", true);
        dialog.setSize(550, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(BACKGROUND_COLOR);

        JLabel titleLabel = new JLabel("Update Teacher Timetable", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(PRIMARY_COLOR);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        File dir = new File("src/timetables");
        File[] teacherFiles = dir.listFiles((d, name) -> name.endsWith("_timetable.txt"));

        if (teacherFiles == null || teacherFiles.length == 0) {
            JOptionPane.showMessageDialog(dialog, "No teacher timetable files found.", "Info", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
            return;
        }

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel selectLabel = new JLabel("Select Teacher:");
        selectLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        selectLabel.setForeground(PRIMARY_COLOR);

        JComboBox<String> teacherCombo = new JComboBox<>();
        teacherCombo.setPreferredSize(new Dimension(300, 35));
        teacherCombo.setFont(new Font("Arial", Font.PLAIN, 16));

        for (File file : teacherFiles) {
            String teacherName = file.getName().replace("_timetable.txt", "").replace("_", " ");
            teacherCombo.addItem(teacherName);
        }


        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(selectLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(teacherCombo, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setOpaque(false);

        JButton addButton = new JButton("Add Course");
        JButton removeButton = new JButton("Remove Course");
        JButton cancelButton = new JButton("Cancel");

        styleButton(addButton, true);
        styleButton(removeButton, false);
        styleButton(cancelButton, false);

        addButton.addActionListener(e -> {
            String teacherName = (String) teacherCombo.getSelectedItem();
            if (teacherName != null) {
                dialog.dispose();
                showAddCourseToTeacherDialog(teacherName);
            }
        });

        removeButton.addActionListener(e -> {
            String teacherName = (String) teacherCombo.getSelectedItem();
            if (teacherName != null) {
                dialog.dispose();
                showRemoveCourseFromTeacherDialog(teacherName);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setContentPane(mainPanel);
        dialog.setVisible(true);
    }


    private void showAddCourseToTeacherDialog(String teacherName) {
        final Color PRIMARY_COLOR = new Color(0, 51, 102);
        final Color BACKGROUND_COLOR = new Color(240, 245, 255);

        JDialog dialog = new JDialog(this, "Add Course to Teacher", true);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JSON_FUNCTIONS json = new JSON_FUNCTIONS("C:\\Users\\HP\\OneDrive\\Desktop\\baseclasstimetable.txt");

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(BACKGROUND_COLOR);

        JLabel titleLabel = new JLabel("Add Course to " + teacherName, JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(PRIMARY_COLOR);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel batchLabel = new JLabel("Batch:");
        JLabel deptLabel = new JLabel("Department:");
        JLabel sectionLabel = new JLabel("Section:");
        JLabel courseLabel = new JLabel("Course:");

        JComboBox<String> batchCombo = new JComboBox<>();
        JComboBox<String> deptCombo = new JComboBox<>();
        JComboBox<String> sectionCombo = new JComboBox<>();
        JComboBox<String> courseCombo = new JComboBox<>();

        Font comboFont = new Font("Arial", Font.PLAIN, 15);
        Dimension comboSize = new Dimension(250, 30);

        for (String batch : json.getBatches()) batchCombo.addItem(batch);
        batchCombo.setFont(comboFont); batchCombo.setPreferredSize(comboSize);
        deptCombo.setFont(comboFont); deptCombo.setPreferredSize(comboSize);
        sectionCombo.setFont(comboFont); sectionCombo.setPreferredSize(comboSize);
        courseCombo.setFont(comboFont); courseCombo.setPreferredSize(comboSize);

        batchCombo.addActionListener(e -> {
            String selectedBatch = (String) batchCombo.getSelectedItem();
            deptCombo.removeAllItems();
            if (selectedBatch != null) {
                for (String dept : json.getDepartments(selectedBatch)) {
                    deptCombo.addItem(dept);
                }
            }
        });
        deptCombo.addActionListener(e -> {
            String selectedBatch = (String) batchCombo.getSelectedItem();
            String selectedDept = (String) deptCombo.getSelectedItem();
            sectionCombo.removeAllItems();
            courseCombo.removeAllItems();
            if (selectedBatch != null && selectedDept != null) {
                for (String section : json.getSections(selectedBatch, selectedDept)) {
                    sectionCombo.addItem(section);
                }
                for (String course : json.getCourses(selectedBatch, selectedDept)) {
                    courseCombo.addItem(course);
                }
            }
        });
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(batchLabel, gbc); gbc.gridx = 1;
        formPanel.add(batchCombo, gbc);

        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(deptLabel, gbc); gbc.gridx = 1;
        formPanel.add(deptCombo, gbc);

        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(sectionLabel, gbc); gbc.gridx = 1;
        formPanel.add(sectionCombo, gbc);

        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(courseLabel, gbc); gbc.gridx = 1;
        formPanel.add(courseCombo, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        JButton addButton = new JButton("Add Course");
        styleButton(addButton, true);


        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.add(addButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> {
            String batch = (String) batchCombo.getSelectedItem();
            String dept = (String) deptCombo.getSelectedItem();
            String section = (String) sectionCombo.getSelectedItem();
            String course = (String) courseCombo.getSelectedItem();

            if (batch == null || dept == null || section == null || course == null) {
                JOptionPane.showMessageDialog(dialog, "Please select all fields", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (CourseAssignmentTracker.isCourseAssigned(batch, dept, section, course)) {
                String currentTeacher = CourseAssignmentTracker.getAssignedTeacher(batch, dept, section, course);
                JOptionPane.showMessageDialog(dialog,
                        "This course is already assigned to: " + currentTeacher,
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (CourseAssignmentTracker.assignCourse(batch, dept, section, course, teacherName)) {
                String filename = "src/timetables/" + teacherName.replace(" ", "_") + "_timetable.txt";
                File file = new File(filename);

                try {
                    List<String> lines = file.exists() ? Files.readAllLines(file.toPath()) : new ArrayList<>();
                    List<String> newContent = new ArrayList<>();
                    List<String> consolidatedLines = new ArrayList<>();
                    boolean inConsolidated = false;

                    for (String line : lines) {
                        if (line.startsWith("Consolidated Timetable:")) {
                            inConsolidated = true;
                            consolidatedLines.add(line);
                            continue;
                        }
                        if (inConsolidated) {
                            consolidatedLines.add(line);
                        } else {
                            newContent.add(line);
                        }
                    }
                    if (newContent.isEmpty()) {
                        newContent.add("Timetable for " + teacherName);
                        newContent.add("");
                    }

                    newContent.add("Course: " + course + " (" + batch + " " + dept + " " + section + ")");

                    String studentFilename = "src/timetables/" + batch + "_" + dept.toUpperCase() + "_" + section + ".txt";
                    File studentFile = new File(studentFilename);
                    Map<String, List<String>> dayEntries = new TreeMap<>();

                    if (studentFile.exists()) {
                        try (BufferedReader reader = new BufferedReader(new FileReader(studentFile))) {
                            String line;
                            String currentDay = null;

                            while ((line = reader.readLine()) != null) {
                                line = line.trim();
                                if (line.isEmpty()) continue;

                                if (line.endsWith(":")) {
                                    currentDay = line.substring(0, line.length() - 1).trim();
                                    continue;
                                }

                                if (currentDay != null && line.startsWith(course)) {

                                    newContent.add("  " + currentDay + ": " + line);


                                    String consolidatedEntry = "  " + line + " (" + batch + " " + dept + " " + section + ")";
                                    dayEntries.computeIfAbsent(currentDay, k -> new ArrayList<>()).add(consolidatedEntry);
                                }
                            }
                        }
                    } else {
                        newContent.add("  Timetable file not found for this class");
                    }

                    newContent.add("");

                    if (consolidatedLines.isEmpty()) {
                        consolidatedLines.add("");
                        consolidatedLines.add("Consolidated Timetable:");
                    }

                    for (Map.Entry<String, List<String>> entry : dayEntries.entrySet()) {
                        String day = entry.getKey();
                        boolean dayExists = false;

                        for (int i = 0; i < consolidatedLines.size(); i++) {
                            if (consolidatedLines.get(i).trim().equals(day + ":")) {

                                int insertPos = i + 1;
                                while (insertPos < consolidatedLines.size() &&
                                        consolidatedLines.get(insertPos).startsWith("  ")) {
                                    insertPos++;
                                }
                                for (String newEntry : entry.getValue()) {
                                    consolidatedLines.add(insertPos, newEntry);
                                    insertPos++;
                                }
                                dayExists = true;
                                break;
                            }
                        }

                        if (!dayExists) {
                            consolidatedLines.add(day + ":");
                            consolidatedLines.addAll(entry.getValue());
                        }
                    }

                    try (PrintWriter writer = new PrintWriter(file)) {
                        for (String line : newContent) {
                            writer.println(line);
                        }

                        for (String line : consolidatedLines) {
                            writer.println(line);
                        }
                    }

                    JOptionPane.showMessageDialog(dialog,
                            "Course added successfully to teacher's timetable",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                } catch (IOException ex) {
                    CourseAssignmentTracker.removeAssignment(batch, dept, section, course);
                    JOptionPane.showMessageDialog(dialog,
                            "Error updating timetable: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(dialog,
                        "Failed to assign course",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(addButton, BorderLayout.SOUTH);
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private void showRemoveCourseFromTeacherDialog(String teacherName) {
        JDialog dialog = new JDialog(this, "Remove Course from Teacher", true);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);

        String filename = "src/timetables/" + teacherName.replace(" ", "_") + "_timetable.txt";
        File file = new File(filename);

        if (!file.exists()) {
            JOptionPane.showMessageDialog(dialog, "No timetable found for this teacher", "Error", JOptionPane.ERROR_MESSAGE);
            dialog.dispose();
            return;
        }

        List<String> courses = new ArrayList<>();
        Map<String, String[]> courseDetails = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Course: ")) {
                    String coursePart = line.substring("Course: ".length());
                    String course = coursePart.substring(0, coursePart.indexOf("(")).trim();
                    courses.add(course);
                    String details = line.substring(line.indexOf("(") + 1, line.indexOf(")"));
                    String[] parts = details.split(" ");
                    if (parts.length >= 3) {
                        courseDetails.put(course, new String[]{parts[0], parts[1], parts[2]});
                    }
                }
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(dialog, "Error reading timetable: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            dialog.dispose();
            return;
        }

        if (courses.isEmpty()) {
            JOptionPane.showMessageDialog(dialog, "No courses assigned to this teacher", "Info", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
            return;
        }

        JComboBox<String> courseCombo = new JComboBox<>(courses.toArray(new String[0]));
        courseCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JTextArea detailsArea = new JTextArea(6, 40);
        detailsArea.setEditable(false);
        detailsArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        JScrollPane scrollPane = new JScrollPane(detailsArea);

        if (!courses.isEmpty()) {
            String[] firstDetails = courseDetails.get(courses.get(0));
            if (firstDetails != null) {
                detailsArea.setText("Course: " + courses.get(0) + "\nBatch: " + firstDetails[0] +
                        "\nDepartment: " + firstDetails[1] + "\nSection: " + firstDetails[2]);
            }
        }

        courseCombo.addActionListener(e -> {
            String selectedCourse = (String) courseCombo.getSelectedItem();
            if (selectedCourse != null) {
                String[] details = courseDetails.get(selectedCourse);
                if (details != null) {
                    detailsArea.setText("Course: " + selectedCourse + "\nBatch: " + details[0] +
                            "\nDepartment: " + details[1] + "\nSection: " + details[2]);
                }
            }
        });

        JButton removeButton = new JButton("Remove Course");
        styleButton(removeButton, true);

        removeButton.addActionListener(e -> {
            String courseToRemove = (String) courseCombo.getSelectedItem();
            if (courseToRemove == null) return;

            String[] details = courseDetails.get(courseToRemove);
            if (details == null || details.length < 3) {
                JOptionPane.showMessageDialog(dialog, "Invalid course format in timetable", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String batch = details[0];
            String dept = details[1];
            String section = details[2];

            boolean removedFromTracker = CourseAssignmentTracker.removeAssignment(batch, dept, section, courseToRemove);
            if (!removedFromTracker) {
                removedFromTracker = CourseAssignmentTracker.removeAssignmentCaseInsensitive(batch, dept, section, courseToRemove);
                if (!removedFromTracker) {
                    JOptionPane.showMessageDialog(dialog, "Failed to remove course from tracking system", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            try {
                List<String> lines = Files.readAllLines(file.toPath());
                List<String> newLines = new ArrayList<>();
                boolean inCourseSection = false;
                boolean courseFound = false;

                for (String line : lines) {
                    if (line.startsWith("Course: ") && line.contains(courseToRemove)) {
                        inCourseSection = true;
                        courseFound = true;
                        continue;
                    }
                    if (inCourseSection) {
                        if (line.trim().isEmpty()) {
                            inCourseSection = false;
                        }
                        continue;
                    }
                    if (line.contains(courseToRemove + " ") || line.contains(courseToRemove + "(") || line.contains(courseToRemove + ")")) {
                        continue;
                    }
                    newLines.add(line);
                }

                if (!courseFound) {
                    JOptionPane.showMessageDialog(dialog, "Course not found in timetable", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try (PrintWriter writer = new PrintWriter(file)) {
                    for (String line : newLines) {
                        writer.println(line);
                    }
                }

                JOptionPane.showMessageDialog(dialog,
                        "Course '" + courseToRemove + "' completely removed from:\n" +
                                "- Teacher's timetable\n" +
                                "- Course tracking system",
                        "Success", JOptionPane.INFORMATION_MESSAGE);

                dialog.dispose();
                showRemoveCourseFromTeacherDialog(teacherName);

            } catch (IOException ex) {
                CourseAssignmentTracker.assignCourse(batch, dept, section, courseToRemove, teacherName);
                JOptionPane.showMessageDialog(dialog, "Error updating timetable: " + ex.getMessage() +
                        "\nCourse assignment has been restored.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel gradientPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                int w = getWidth();
                int h = getHeight();
                Color color1 = new Color(230, 240, 255);
                Color color2 = new Color(200, 220, 255);
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        gradientPanel.setLayout(new BoxLayout(gradientPanel, BoxLayout.Y_AXIS));
        gradientPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel titleLabel = new JLabel("Remove Assigned Course");
        titleLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        courseCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        removeButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        gradientPanel.add(titleLabel);
        gradientPanel.add(Box.createVerticalStrut(20));
        gradientPanel.add(new JLabel("Select Course to Remove:"));
        gradientPanel.add(Box.createVerticalStrut(8));
        gradientPanel.add(courseCombo);
        gradientPanel.add(Box.createVerticalStrut(15));
        gradientPanel.add(scrollPane);
        gradientPanel.add(Box.createVerticalStrut(25));
        gradientPanel.add(removeButton);

        dialog.setContentPane(gradientPanel);
        dialog.setVisible(true);
    }



    private void releaseRoomAssignmentsForCourse(String batch, String dept, String section, String course) {
        String filename = "src/timetables/" + batch + "_" + dept.toUpperCase() + "_" + section + ".txt";
        File file = new File(filename);

        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            String currentDay = null;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                if (line.endsWith(":")) {
                    currentDay = line.substring(0, line.length() - 1).trim();
                    continue;
                }
                if (currentDay != null && line.contains(course) && line.contains("[")) {
                    String timeSlot = line.substring(line.indexOf("(") + 1, line.indexOf(")"));
                    String room = line.substring(line.indexOf("[") + 1, line.indexOf("]"));

                    RoomAllocationManager.releaseRoom(batch, currentDay, timeSlot, room);
                }
            }
        } catch (IOException ex) {
            System.err.println("Error releasing room assignments: " + ex.getMessage());
        }
    }

    private void showUpdateStudentTimetableDialog() {
        JDialog dialog = new JDialog(this, "Update Student Timetable", true);
        dialog.setSize(900, 700);
        dialog.setLocationRelativeTo(this);

        JSON_FUNCTIONS json = new JSON_FUNCTIONS("C:\\Users\\HP\\OneDrive\\Desktop\\baseclasstimetable.txt");

        final Color PRIMARY_COLOR = new Color(0, 51, 102);
        final Color BACKGROUND_COLOR = new Color(240, 245, 255);

        JPanel gradientPanel = new GradientPanel(new Color(230, 240, 255), new Color(200, 210, 250));
        gradientPanel.setLayout(new BorderLayout(20, 20));
        gradientPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 30, 30));

        JLabel titleLabel = new JLabel("Update Student Timetable", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(PRIMARY_COLOR);
        gradientPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(7, 2, 15, 15));
        formPanel.setOpaque(false);

        JComboBox<String> batchCombo = new JComboBox<>();
        JComboBox<String> deptCombo = new JComboBox<>();
        JComboBox<String> sectionCombo = new JComboBox<>();
        JComboBox<String> dayCombo = new JComboBox<>(ORDERED_DAYS);
        JComboBox<String> timeSlotCombo = new JComboBox<>(new String[]{"8-9AM", "9-10AM", "10-11AM", "11-12PM"});
        JComboBox<String> courseCombo = new JComboBox<>();
        JComboBox<String> roomCombo = new JComboBox<>();

        Map<String, List<String>> batchRooms = new HashMap<>();
        batchRooms.put("24", Arrays.asList("C-12", "C-13", "C-14", "C-15", "C-16",
                "C-17", "C-18", "C-19", "C-20", "C-21",
                "D-22", "D-23", "D-24", "D-25", "D-26",
                "D-27", "D-28", "E-29", "E-30", "E-31",
                "E-32", "E-33", "E-34", "E-35"));
        batchRooms.put("23", Arrays.asList("A-1", "A-2", "A-3", "A-4", "A-5",
                "A-6", "A-7", "A-8", "B-9", "B-10", "B-11"));

        for (String batch : json.getBatches()) batchCombo.addItem(batch);

        batchCombo.addActionListener(e -> {
            String selectedBatch = (String) batchCombo.getSelectedItem();
            deptCombo.removeAllItems();
            if (selectedBatch != null) {
                for (String dept : json.getDepartments(selectedBatch)) deptCombo.addItem(dept);
            }
        });

        deptCombo.addActionListener(e -> {
            String selectedBatch = (String) batchCombo.getSelectedItem();
            String selectedDept = (String) deptCombo.getSelectedItem();
            sectionCombo.removeAllItems();
            courseCombo.removeAllItems();
            if (selectedBatch != null && selectedDept != null) {
                for (String section : json.getSections(selectedBatch, selectedDept)) sectionCombo.addItem(section);
                for (String course : json.getCourses(selectedBatch, selectedDept)) courseCombo.addItem(course);
            }
        });

        sectionCombo.addActionListener(e -> updateRoomCombo(batchCombo, deptCombo, sectionCombo,
                dayCombo, timeSlotCombo, roomCombo, batchRooms));

        timeSlotCombo.addActionListener(e -> updateRoomCombo(batchCombo, deptCombo, sectionCombo,
                dayCombo, timeSlotCombo, roomCombo, batchRooms));

        formPanel.add(new JLabel("Batch:")).setFont(new Font("Arial", Font.PLAIN, 16));
        formPanel.add(batchCombo);
        formPanel.add(new JLabel("Department:")).setFont(new Font("Arial", Font.PLAIN, 16));
        formPanel.add(deptCombo);
        formPanel.add(new JLabel("Section:")).setFont(new Font("Arial", Font.PLAIN, 16));
        formPanel.add(sectionCombo);
        formPanel.add(new JLabel("Day:")).setFont(new Font("Arial", Font.PLAIN, 16));
        formPanel.add(dayCombo);
        formPanel.add(new JLabel("Time Slot:")).setFont(new Font("Arial", Font.PLAIN, 16));
        formPanel.add(timeSlotCombo);
        formPanel.add(new JLabel("Course:")).setFont(new Font("Arial", Font.PLAIN, 16));
        formPanel.add(courseCombo);
        formPanel.add(new JLabel("Room:")).setFont(new Font("Arial", Font.PLAIN, 16));
        formPanel.add(roomCombo);

        gradientPanel.add(formPanel, BorderLayout.CENTER);

        JTextArea infoArea = new JTextArea(10, 50);
        infoArea.setEditable(false);
        infoArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        infoArea.setBorder(BorderFactory.createTitledBorder("Current Timetable"));
        JScrollPane infoScroll = new JScrollPane(infoArea);
        infoScroll.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 1));
        gradientPanel.add(infoScroll, BorderLayout.EAST);

        Runnable updateTimetableDisplay = () -> {
            String batch = (String) batchCombo.getSelectedItem();
            String dept = (String) deptCombo.getSelectedItem();
            String section = (String) sectionCombo.getSelectedItem();

            if (batch == null || dept == null || section == null) {
                infoArea.setText("Please select Batch, Department, and Section");
                return;
            }

            String filename = "src/timetables/" + batch + "_" + dept.toUpperCase() + "_" + section + ".txt";
            File file = new File(filename);

            if (!file.exists()) {
                infoArea.setText("No timetable file found for selected class");
                return;
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                StringBuilder timetable = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    timetable.append(line).append("\n");
                }
                infoArea.setText(timetable.toString());
            } catch (IOException ex) {
                infoArea.setText("Error loading timetable: " + ex.getMessage());
            }
        };

        batchCombo.addActionListener(e -> updateTimetableDisplay.run());
        deptCombo.addActionListener(e -> updateTimetableDisplay.run());
        sectionCombo.addActionListener(e -> updateTimetableDisplay.run());

        updateTimetableDisplay.run();

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setOpaque(false);

        JButton updateButton = new JButton("Update Timetable");
        styleButton(updateButton, true);
        updateButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        buttonPanel.add(updateButton);


        gradientPanel.add(buttonPanel, BorderLayout.SOUTH);

        updateButton.addActionListener(e -> {
            String batch = (String) batchCombo.getSelectedItem();
            String dept = (String) deptCombo.getSelectedItem();
            String section = (String) sectionCombo.getSelectedItem();
            String day = (String) dayCombo.getSelectedItem();
            String timeSlot = (String) timeSlotCombo.getSelectedItem();
            String room = (String) roomCombo.getSelectedItem();
            String course = (String) courseCombo.getSelectedItem();

            if (batch == null || dept == null || section == null || day == null ||
                    timeSlot == null || room == null || course == null) {
                JOptionPane.showMessageDialog(dialog, "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String oldRoom = getCurrentRoomAssignment(batch, dept, section, day, timeSlot);

            if (updateTimetableFile(batch, dept, section, day, timeSlot, course, room, oldRoom)) {
                JOptionPane.showMessageDialog(dialog,
                        "Timetable updated successfully",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            }
        });

        dialog.setContentPane(gradientPanel);
        dialog.setVisible(true);
    }

    private void updateRoomCombo(JComboBox<String> batchCombo, JComboBox<String> deptCombo,
                                 JComboBox<String> sectionCombo, JComboBox<String> dayCombo,
                                 JComboBox<String> timeSlotCombo, JComboBox<String> roomCombo,
                                 Map<String, List<String>> batchRooms) {

        String batch = (String) batchCombo.getSelectedItem();
        String dept = (String) deptCombo.getSelectedItem();
        String section = (String) sectionCombo.getSelectedItem();
        String day = (String) dayCombo.getSelectedItem();
        String timeSlot = (String) timeSlotCombo.getSelectedItem();

        if (batch == null || dept == null || section == null || day == null || timeSlot == null) {
            return;
        }

        String currentRoom = getCurrentRoomAssignment(batch, dept, section, day, timeSlot);

        List<String> availableRooms = new ArrayList<>();
        if (batchRooms.containsKey(batch)) {
            availableRooms = RoomAllocationManager.getAvailableRooms(batch, day, timeSlot, batchRooms.get(batch));
        }
        roomCombo.removeAllItems();

        if (currentRoom != null && !currentRoom.isEmpty()) {
            roomCombo.addItem(currentRoom);
        }
        for (String room : availableRooms) {
            if (currentRoom == null || !room.equals(currentRoom)) {
                roomCombo.addItem(room);
            }
        }
    }

    private String getCurrentRoomAssignment(String batch, String dept, String section, String day, String timeSlot) {
        String filename = "src/timetables/" + batch + "_" + dept.toUpperCase() + "_" + section + ".txt";
        File file = new File(filename);

        if (!file.exists()) return null;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            String currentDay = null;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                if (line.endsWith(":")) {
                    currentDay = line.substring(0, line.length() - 1);
                    continue;
                }

                if (currentDay != null && currentDay.equals(day) && line.contains(timeSlot) && line.contains("[")) {
                    return line.substring(line.indexOf("[") + 1, line.indexOf("]"));
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private boolean updateTimetableFile(String batch, String dept, String section, String day,
                                        String timeSlot, String course, String room, String oldRoom) {

        String filename = "src/timetables/" + batch + "_" + dept.toUpperCase() + "_" + section + ".txt";
        File file = new File(filename);

        if (!file.exists()) {
            JOptionPane.showMessageDialog(null, "Timetable file not found", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            List<String> lines = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
            }
            boolean dayFound = false;
            boolean slotUpdated = false;
            String oldCourse = null;

            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i).trim();

                if (line.equals(day + ":")) {
                    dayFound = true;
                    continue;
                }

                if (dayFound && !line.isEmpty()) {
                    if (line.contains("(" + timeSlot + ")")) {
                        oldCourse = line.substring(0, line.indexOf("(")).trim();

                        lines.set(i, String.format("  %s (%s) [%s]", course, timeSlot, room));
                        slotUpdated = true;

                        if (oldRoom != null && !oldRoom.equals(room)) {
                            RoomAllocationManager.releaseRoom(batch, day, timeSlot, oldRoom);
                        }
                        RoomAllocationManager.assignRoom(batch, day, timeSlot, room);

                        if (oldCourse != null && !oldCourse.equals(course)) {
                            CourseAssignmentTracker.removeAssignment(batch, dept, section, oldCourse);
                        }
                        break;
                    }
                }
                if (dayFound && line.isEmpty()) {
                    dayFound = false;
                }
            }

            if (!slotUpdated) {
                boolean added = false;
                for (int i = 0; i < lines.size(); i++) {
                    if (lines.get(i).trim().equals(day + ":")) {
                        lines.add(i + 1, String.format("  %s (%s) [%s]", course, timeSlot, room));
                        added = true;
                        break;
                    }
                }

                if (!added) {
                    lines.add(day + ":");
                    lines.add(String.format("  %s (%s) [%s]", course, timeSlot, room));
                }

                RoomAllocationManager.assignRoom(batch, day, timeSlot, room);
            }
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                for (String line : lines) {
                    writer.println(line);
                }
            }
            return true;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null,
                    "Error updating timetable: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

}