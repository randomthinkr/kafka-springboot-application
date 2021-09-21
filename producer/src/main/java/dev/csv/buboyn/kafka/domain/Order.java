package dev.csv.buboyn.kafka.domain;

import java.util.ArrayList;
import java.util.List;


public class Order {
    private String uuid;
    private Customer customer = new Customer();
    private List<OrderItem> items = new ArrayList<OrderItem>();


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getTotalSum() {
        int sum = 0;
        for (OrderItem orderItem : items) {
            sum += orderItem.getAmount();
        }
        return sum;
    }

    public void addItem(OrderItem i) {
        items.add(i);
    }


    public List<OrderItem> getItems() {
        return items;
    }


    @Override
    public String toString() {
        return "Order [uuid=" + uuid + ", customer=" + customer + ", items=" + items + "]";
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}





