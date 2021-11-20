package com.iridium.iridiumskyblock.exceptions;

import lombok.Getter;

@Getter
public final class ShopBalanceInsufficientFundsException extends Exception {

    private final double amount;
    private final double balance;

    public ShopBalanceInsufficientFundsException(double amount, double balance) {
        super(String.format("Shop balance insufficient funds: %s < %s", balance, amount));
        this.amount = amount;
        this.balance = balance;
    }

    public double getMissingAmount() {
        return Math.max(0, amount - balance);
    }

}
