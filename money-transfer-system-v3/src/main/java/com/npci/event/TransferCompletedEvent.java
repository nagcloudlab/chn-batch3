package com.npci.event;

import org.springframework.context.ApplicationEvent;

public class TransferCompletedEvent extends ApplicationEvent {

    private final double amount;
    private final String fromAccount;
    private final String toAccount;

    public TransferCompletedEvent(Object source, double amount, String fromAccount, String toAccount) {
        super(source);
        this.amount = amount;
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
    }

    public double getAmount() {
        return amount;
    }

    public String getFromAccount() {
        return fromAccount;
    }

    public String getToAccount() {
        return toAccount;
    }

    @Override
    public String toString() {
        return String.format("TransferCompletedEvent{amount=%.2f, from='%s', to='%s'}", amount, fromAccount, toAccount);
    }

}
