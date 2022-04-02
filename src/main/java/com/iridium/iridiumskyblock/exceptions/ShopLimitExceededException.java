package com.iridium.iridiumskyblock.exceptions;

import lombok.Getter;

@Getter
public final class ShopLimitExceededException extends Exception {

    private final double amount;
    private final double limit;

    public ShopLimitExceededException(double amount, double limit) {
        super(String.format("Shop balance limit exceeded: %s < %s", limit, amount));
        this.amount = amount;
        this.limit = limit;
    }

    public double getExceededAmount() {
        return Math.max(0, amount - limit);
    }

}
