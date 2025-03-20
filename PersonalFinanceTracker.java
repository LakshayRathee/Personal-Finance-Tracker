import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class PersonalFinanceTracker {
    private static final Scanner scanner = new Scanner(System.in);
    private static final List<Transaction> transactions = new ArrayList<>();
    private static final String DATA_FILE = "finance_data.csv";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void main(String[] args) {
        loadData();
        boolean running = true;

        while (running) {
            System.out.println("\n===== Personal Finance Tracker =====");
            System.out.println("1. Add Income");
            System.out.println("2. Add Expense");
            System.out.println("3. View All Transactions");
            System.out.println("4. View Monthly Summary");
            System.out.println("5. View Category Summary");
            System.out.println("6. Exit");
            System.out.print("Choose an option: ");

            int choice = getIntInput();
            
            switch (choice) {
                case 1:
                    addTransaction(TransactionType.INCOME);
                    break;
                case 2:
                    addTransaction(TransactionType.EXPENSE);
                    break;
                case 3:
                    viewAllTransactions();
                    break;
                case 4:
                    viewMonthlySummary();
                    break;
                case 5:
                    viewCategorySummary();
                    break;
                case 6:
                    saveData();
                    running = false;
                    System.out.println("Thank you for using Personal Finance Tracker!");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
        scanner.close();
    }

    private static void addTransaction(TransactionType type) {
        System.out.print("Enter date (yyyy-MM-dd) or press Enter for today: ");
        String dateInput = scanner.nextLine().trim();
        
        LocalDate date;
        if (dateInput.isEmpty()) {
            date = LocalDate.now();
        } else {
            try {
                date = LocalDate.parse(dateInput, DATE_FORMATTER);
            } catch (Exception e) {
                System.out.println("Invalid date format. Using today's date.");
                date = LocalDate.now();
            }
        }

        System.out.print("Enter amount: ");
        double amount = getDoubleInput();

        System.out.print("Enter category: ");
        String category = scanner.nextLine().trim();

        System.out.print("Enter description: ");
        String description = scanner.nextLine().trim();

        Transaction transaction = new Transaction(date, type, amount, category, description);
        transactions.add(transaction);
        System.out.println(type + " added successfully!");
    }

    private static void viewAllTransactions() {
        if (transactions.isEmpty()) {
            System.out.println("No transactions to display.");
            return;
        }

        System.out.println("\n===== All Transactions =====");
        System.out.printf("%-12s %-8s %-10s %-15s %-30s%n", "Date", "Type", "Amount", "Category", "Description");
        System.out.println("--------------------------------------------------------------------------------");
        
        for (Transaction transaction : transactions) {
            System.out.printf("%-12s %-8s $%-9.2f %-15s %-30s%n", 
                transaction.getDate().format(DATE_FORMATTER),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getCategory(),
                transaction.getDescription());
        }
    }

    private static void viewMonthlySummary() {
        if (transactions.isEmpty()) {
            System.out.println("No transactions to display.");
            return;
        }

        Map<String, double[]> monthlySummary = new HashMap<>(); // [income, expense]

        for (Transaction transaction : transactions) {
            String monthYear = transaction.getDate().getYear() + "-" + 
                               String.format("%02d", transaction.getDate().getMonthValue());
            
            if (!monthlySummary.containsKey(monthYear)) {
                monthlySummary.put(monthYear, new double[2]);
            }
            
            double[] values = monthlySummary.get(monthYear);
            if (transaction.getType() == TransactionType.INCOME) {
                values[0] += transaction.getAmount();
            } else {
                values[1] += transaction.getAmount();
            }
        }

        System.out.println("\n===== Monthly Summary =====");
        System.out.printf("%-10s %-12s %-12s %-12s%n", "Month", "Income", "Expenses", "Balance");
        System.out.println("--------------------------------------------------");
        
        List<String> months = new ArrayList<>(monthlySummary.keySet());
        Collections.sort(months);
        
        for (String month : months) {
            double[] values = monthlySummary.get(month);
            double balance = values[0] - values[1];
            System.out.printf("%-10s $%-11.2f $%-11.2f $%-11.2f%n", 
                month, values[0], values[1], balance);
        }
    }

    private static void viewCategorySummary() {
        if (transactions.isEmpty()) {
            System.out.println("No transactions to display.");
            return;
        }

        Map<String, double[]> categorySummary = new HashMap<>(); // [income, expense]

        for (Transaction transaction : transactions) {
            String category = transaction.getCategory();
            
            if (!categorySummary.containsKey(category)) {
                categorySummary.put(category, new double[2]);
            }
            
            double[] values = categorySummary.get(category);
            if (transaction.getType() == TransactionType.INCOME) {
                values[0] += transaction.getAmount();
            } else {
                values[1] += transaction.getAmount();
            }
        }

        System.out.println("\n===== Category Summary =====");
        System.out.printf("%-15s %-12s %-12s%n", "Category", "Income", "Expenses");
        System.out.println("------------------------------------------");
        
        for (Map.Entry<String, double[]> entry : categorySummary.entrySet()) {
            double[] values = entry.getValue();
            System.out.printf("%-15s $%-11.2f $%-11.2f%n", 
                entry.getKey(), values[0], values[1]);
        }
    }

    private static void loadData() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            // Skip header
            reader.readLine();
            
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    LocalDate date = LocalDate.parse(parts[0], DATE_FORMATTER);
                    TransactionType type = TransactionType.valueOf(parts[1]);
                    double amount = Double.parseDouble(parts[2]);
                    String category = parts[3];
                    String description = parts[4];
                    
                    transactions.add(new Transaction(date, type, amount, category, description));
                }
            }
            System.out.println("Data loaded successfully.");
        } catch (IOException e) {
            System.out.println("Error loading data: " + e.getMessage());
        }
    }

    private static void saveData() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DATA_FILE))) {
            writer.println("Date,Type,Amount,Category,Description");
            
            for (Transaction transaction : transactions) {
                writer.printf("%s,%s,%.2f,%s,%s%n",
                    transaction.getDate().format(DATE_FORMATTER),
                    transaction.getType(),
                    transaction.getAmount(),
                    transaction.getCategory(),
                    transaction.getDescription());
            }
            System.out.println("Data saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    private static int getIntInput() {
        while (true) {
            try {
                String input = scanner.nextLine();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number: ");
            }
        }
    }

    private static double getDoubleInput() {
        while (true) {
            try {
                String input = scanner.nextLine();
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid amount: ");
            }
        }
    }
}
