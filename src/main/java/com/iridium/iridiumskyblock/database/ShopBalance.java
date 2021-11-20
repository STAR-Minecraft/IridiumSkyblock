package com.iridium.iridiumskyblock.database;

import com.google.gson.*;
import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.exceptions.ShopBalanceInsufficientFundsException;
import com.iridium.iridiumskyblock.exceptions.ShopBalanceLimitExceededException;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.StringType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.OptionalDouble;

public final class ShopBalance {

    private final Map<String, Double> wallets;
    private Island island;

    public ShopBalance() {
        this(null, new LinkedHashMap<>());
    }

    public ShopBalance(@Nullable Island island) {
        this(island, new LinkedHashMap<>());
    }

    private ShopBalance(@Nullable Island island, @NotNull Map<String, Double> wallets) {
        this.island = island;
        this.wallets = wallets;
    }

    void setIsland(@NotNull Island island) {
        this.island = island;
    }

    public @NotNull OptionalDouble getBalanceOf(@NotNull String currency) {
        synchronized (wallets) {
            return wallets.containsKey(currency) ? OptionalDouble.of(wallets.get(currency)) : OptionalDouble.empty();
        }
    }

    private double getBalanceOrZero(@NotNull String currency) {
        return wallets.getOrDefault(currency, 0D);
    }

    public boolean isEmpty() {
        synchronized (wallets) {
            return wallets.isEmpty();
        }
    }

    public void clear() {
        synchronized (wallets) {
            wallets.clear();
        }
    }

    public boolean has(@NotNull String currency, double amount) {
        synchronized (wallets) {
            return getBalanceOrZero(currency) >= amount;
        }
    }

    public double add(@NotNull String currency, double amount) throws ShopBalanceLimitExceededException {
        synchronized (wallets) {
            double balance = getBalanceOrZero(currency);
            if(amount <= 0D)
                return balance;

            double afterAdding = balance + amount;
            double limit = IridiumSkyblock.getInstance().getShop().shopBalanceConfig.getBalanceLimit(island, currency);
            if(limit != -1D && afterAdding > limit)
                throw new ShopBalanceLimitExceededException(afterAdding, limit);

            wallets.put(currency, afterAdding);
            return afterAdding;
        }
    }

    public double take(@NotNull String currency, double amount) throws ShopBalanceInsufficientFundsException {
        synchronized (wallets) {
            double balance = getBalanceOrZero(currency);
            if(amount <= 0D)
                return balance;

            double afterTaking = balance - amount;
            if(afterTaking < 0D)
                throw new ShopBalanceInsufficientFundsException(amount, balance);

            wallets.put(currency, afterTaking);
            return afterTaking;
        }
    }

    public boolean set(@NotNull String currency, double amount) {
        synchronized (wallets) {
            double balance = getBalanceOrZero(currency);
            if(balance == amount)
                return false;

            wallets.put(currency, amount);
            return true;
        }
    }

    @Override
    public @NotNull String toString() {
        return "ShopBalance{" +
                "wallets=" + wallets +
                '}';
    }

    public static final class Persister extends StringType {

        private static final Persister SINGLETON = new Persister();

        private final Gson gson;

        private Persister() {
            super(SqlType.STRING, new Class[]{ShopBalance.class});
            this.gson = new GsonBuilder()
                    .registerTypeAdapter(ShopBalance.class, new ShopBalance.JsonAdapter())
                    .create();
        }

        public static @NotNull Persister getSingleton() {
            return SINGLETON;
        }

        @Override
        public String javaToSqlArg(FieldType fieldType, Object object) {
            return object instanceof ShopBalance ? gson.toJson(object) : null;
        }

        @Override
        public ShopBalance sqlArgToJava(FieldType fieldType, Object object, int columnPos) {
            return object instanceof String ? gson.fromJson((String) object, ShopBalance.class) : null;
        }

    }

    public static final class JsonAdapter implements JsonSerializer<ShopBalance>, JsonDeserializer<ShopBalance> {

        @Override
        public @Nullable ShopBalance deserialize(
                @NotNull JsonElement jsonElement,
                @NotNull Type type,
                @NotNull JsonDeserializationContext context
        ) throws JsonParseException {
            Map<?, ?> asMap = context.deserialize(jsonElement, HashMap.class);
            if(asMap == null || asMap.isEmpty())
                return null;

            Map<String, Double> wallets = new LinkedHashMap<>();
            asMap.forEach((key, value) -> {
                String keyAsString = key.toString();
                String valueAsString = value.toString();

                try {
                    double valueAsDouble = Double.parseDouble(valueAsString);
                    wallets.put(keyAsString, valueAsDouble);
                } catch (NumberFormatException ignored) {
                }
            });

            return new ShopBalance(null, wallets);
        }

        @Override
        public @NotNull JsonElement serialize(
                @Nullable ShopBalance shopBalance,
                @NotNull Type type,
                @NotNull JsonSerializationContext context
        ) {
            return shopBalance != null ? context.serialize(shopBalance.wallets) : JsonNull.INSTANCE;
        }

    }

}
