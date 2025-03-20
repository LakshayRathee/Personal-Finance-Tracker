import java.time.LocalDate;

public class Transaction {
    private LocalDate date;
    private TransactionType type;
    private double amount;
    private String category;
    private String description;

    public Transaction(LocalDate date, TransactionType type, double amount, String category, String description) {
        this.date = date;
        this.type = type;
        this.amount = amount;
        this.category = category;
        this.description = description;
    }

    public LocalDate getDate() {
        return date;
    }

    public TransactionType getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "date=" + date +
                ", type=" + type +
                ", amount=" + amount +
                ", category='" + category + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
