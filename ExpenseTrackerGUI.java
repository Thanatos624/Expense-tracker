import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.io.*;
import java.text.*;

public class ExpenseTrackerGUI extends JFrame {
    private CardLayout cardLayout;
    private JPanel cards;
    private Connection conn;

    private JTextField regUserField, loginUserField;
    private JPasswordField regPassField, loginPassField;
    private JTextField dateField, categoryField, amountField;
    private JTextArea outputArea;
    private int currentUserId;

    public ExpenseTrackerGUI() {
        connectDB();
        setupGUI();
    }

    private void connectDB() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/expense_tracker", "root", "root");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "DB Connection Failed!");
            System.exit(1);
        }
    }

    private void setupGUI() {
        setTitle("Expense Tracker");
        setSize(600, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);

        cards.add(loginPanel(), "login");
        cards.add(registerPanel(), "register");
        cards.add(mainPanel(), "main");

        add(cards);
        cardLayout.show(cards, "login");

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel loginPanel() {
        JPanel panel = new JPanel(null);
        panel.setBackground(Color.WHITE);

        JLabel title = new JLabel("Login", JLabel.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 30));
        title.setBounds(200, 30, 200, 40);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        userLabel.setBounds(100, 100, 100, 30);
        loginUserField = new JTextField();
        loginUserField.setBounds(220, 100, 200, 30);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        passLabel.setBounds(100, 150, 100, 30);
        loginPassField = new JPasswordField();
        loginPassField.setBounds(220, 150, 200, 30);

        JButton loginBtn = new JButton("Login");
        loginBtn.setBounds(220, 200, 100, 30);
        loginBtn.addActionListener(e -> login());

        JButton toRegister = new JButton("Register");
        toRegister.setBounds(330, 200, 90, 30);
        toRegister.addActionListener(e -> cardLayout.show(cards, "register"));

        panel.add(title);
        panel.add(userLabel);
        panel.add(loginUserField);
        panel.add(passLabel);
        panel.add(loginPassField);
        panel.add(loginBtn);
        panel.add(toRegister);

        return panel;
    }

    private JPanel registerPanel() {
        JPanel panel = new JPanel(null);
        panel.setBackground(Color.WHITE);

        JLabel title = new JLabel("Register", JLabel.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 30));
        title.setBounds(200, 30, 200, 40);

        JLabel userLabel = new JLabel("New Username:");
        userLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        userLabel.setBounds(80, 100, 140, 30);
        regUserField = new JTextField();
        regUserField.setBounds(220, 100, 200, 30);

        JLabel passLabel = new JLabel("New Password:");
        passLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        passLabel.setBounds(80, 150, 140, 30);
        regPassField = new JPasswordField();
        regPassField.setBounds(220, 150, 200, 30);

        JButton regBtn = new JButton("Register");
        regBtn.setBounds(220, 200, 100, 30);
        regBtn.addActionListener(e -> register());

        JButton toLogin = new JButton("Back to Login");
        toLogin.setBounds(330, 200, 130, 30);
        toLogin.addActionListener(e -> cardLayout.show(cards, "login"));

        panel.add(title);
        panel.add(userLabel);
        panel.add(regUserField);
        panel.add(passLabel);
        panel.add(regPassField);
        panel.add(regBtn);
        panel.add(toLogin);

        return panel;
    }

    private JPanel mainPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel top = new JPanel(new GridLayout(4, 2));
        dateField = new JTextField("2025-05-13");
        categoryField = new JTextField();
        amountField = new JTextField();

        top.add(new JLabel("Date (YYYY-MM-DD):"));
        top.add(dateField);
        top.add(new JLabel("Category:"));
        top.add(categoryField);
        top.add(new JLabel("Amount:"));
        top.add(amountField);

        JButton addBtn = new JButton("Add Expense");
        JButton viewBtn = new JButton("View Expenses");
        JButton exportBtn = new JButton("Export to CSV");

        addBtn.addActionListener(e -> addExpense());
        viewBtn.addActionListener(e -> viewExpenses());
        exportBtn.addActionListener(e -> exportToCSV());

        top.add(addBtn);
        top.add(viewBtn);

        outputArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(outputArea);

        panel.add(top, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(exportBtn, BorderLayout.SOUTH);
        return panel;
    }

    private void register() {
        String user = regUserField.getText();
        String pass = new String(regPassField.getPassword());

        try {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)");
            ps.setString(1, user);
            ps.setString(2, pass);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Registered successfully!");
            cardLayout.show(cards, "login");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Registration failed. Username might exist.");
        }
    }

    private void login() {
        String user = loginUserField.getText();
        String pass = new String(loginPassField.getPassword());

        try {
            PreparedStatement ps = conn.prepareStatement("SELECT id FROM users WHERE username = ? AND password = ?");
            ps.setString(1, user);
            ps.setString(2, pass);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                currentUserId = rs.getInt("id");
                cardLayout.show(cards, "main");
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addExpense() {
        String date = dateField.getText();
        String cat = categoryField.getText();
        String amtText = amountField.getText();

        if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
            JOptionPane.showMessageDialog(this, "Date must be in YYYY-MM-DD format");
            return;
        }

        java.sql.Date sqlDate;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            java.util.Date parsed = sdf.parse(date);
            sqlDate = new java.sql.Date(parsed.getTime());
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid date entered.");
            return;
        }

        double amt;
        try {
            amt = Double.parseDouble(amtText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Amount must be a number");
            return;
        }

        try {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO expenses (user_id, date, category, amount) VALUES (?, ?, ?, ?)");
            ps.setInt(1, currentUserId);
            ps.setDate(2, sqlDate);
            ps.setString(3, cat);
            ps.setDouble(4, amt);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Expense Added!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error adding expense");
            e.printStackTrace();
        }
    }

    private void viewExpenses() {
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT category, SUM(amount) AS total FROM expenses WHERE user_id = " + currentUserId + " GROUP BY category");

            outputArea.setText("Category-wise Summary:\n");
            while (rs.next()) {
                outputArea.append(rs.getString("category") + " : ₹" + rs.getDouble("total") + "\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void exportToCSV() {
        try (PrintWriter pw = new PrintWriter(new File("expenses.csv"))) {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT date, category, amount FROM expenses WHERE user_id = " + currentUserId);

            pw.println("Date,Category,Amount");
            while (rs.next()) {
                pw.println(rs.getDate("date") + "," + rs.getString("category") + "," + rs.getDouble("amount"));
            }
            JOptionPane.showMessageDialog(this, "Exported to expenses.csv!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error exporting to CSV.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ExpenseTrackerGUI::new);
    }
}
