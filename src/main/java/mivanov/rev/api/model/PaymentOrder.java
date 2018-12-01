package mivanov.rev.api.model;

public class PaymentOrder {
    private Double amount;
    private String toUser;

    public PaymentOrder() {
    }

    public Double getAmount() { return this.amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public String getToUser() { return this.toUser; }
    public void setToUser(String toUser) { this.toUser = toUser; }
}
