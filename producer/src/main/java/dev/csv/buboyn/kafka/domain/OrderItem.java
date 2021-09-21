package dev.csv.buboyn.kafka.domain;


public class OrderItem {
    private String itemName;
    private int amount;

    public String getItemName() {
        return itemName;
    }
    public OrderItem setItemName(String itemName) {
        this.itemName = itemName;
        return this;
    }
    public int getAmount() {
        return amount;
    }
    public OrderItem setAmount(int amount) {
        this.amount = amount;
        return this;
    }
    @Override
    public String toString() {
        return "OrderItem [itemName=" + itemName + ", amount=" + amount + "]";
    }
}





