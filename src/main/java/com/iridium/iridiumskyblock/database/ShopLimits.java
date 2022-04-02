package com.iridium.iridiumskyblock.database;

import com.google.gson.*;
import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.exceptions.ShopLimitExceededException;
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

public final class ShopLimits {

    private final Map<String, Double> counters;
    private Island island;

    public ShopLimits() {
        this(null, new LinkedHashMap<>());
    }

    public ShopLimits(@Nullable Island island) {
        this(island, new LinkedHashMap<>());
    }

    private ShopLimits(@Nullable Island island, @NotNull Map<String, Double> counters) {
        this.island = island;
        this.counters = counters;
    }

    void setIsland(@NotNull Island island) {
        this.island = island;
    }

    public @NotNull OptionalDouble getCountOf(@NotNull String currency) {
        synchronized (counters) {
            return counters.containsKey(currency) ? OptionalDouble.of(counters.get(currency)) : OptionalDouble.empty();
        }
    }

    private double getCountOrZero(@NotNull String currency) {
        return counters.getOrDefault(currency, 0D);
    }

    public boolean isEmpty() {
        synchronized (counters) {
            return counters.isEmpty();
        }
    }

    public void clear() {
        synchronized (counters) {
            counters.clear();
        }
    }

    public boolean canAdd(@NotNull String currency, double amount) {
        synchronized (counters) {
            double limit = IridiumSkyblock.getInstance().getShop().shopLimitsConfig.getActualLimit(island, currency);
            return getCountOrZero(currency) + amount <= limit;
        }
    }

    public double add(@NotNull String currency, double amount) throws ShopLimitExceededException {
        synchronized (counters) {
            double count = counters.compute(currency, (key, value) -> value != null ? value + amount : amount);

            double limit = IridiumSkyblock.getInstance().getShop().shopLimitsConfig.getActualLimit(island, currency);
            if(limit != -1D && count > limit) {
                counters.put(currency, limit);
                throw new ShopLimitExceededException(count, limit);
            }

            return count;
        }
    }

    public boolean set(@NotNull String currency, double amount) {
        synchronized (counters) {
            double count = getCountOrZero(currency);
            if(count == amount)
                return false;

            counters.put(currency, amount);
            return true;
        }
    }

    @Override
    public @NotNull String toString() {
        return "ShopLimits{" +
                "wallets=" + counters +
                '}';
    }

    public static final class Persister extends StringType {

        private static final Persister SINGLETON = new Persister();

        private final Gson gson;

        private Persister() {
            super(SqlType.STRING, new Class[]{ShopLimits.class});
            this.gson = new GsonBuilder()
                    .registerTypeAdapter(ShopLimits.class, new ShopLimits.JsonAdapter())
                    .create();
        }

        public static @NotNull Persister getSingleton() {
            return SINGLETON;
        }

        @Override
        public String javaToSqlArg(FieldType fieldType, Object object) {
            return object instanceof ShopLimits ? gson.toJson(object) : null;
        }

        @Override
        public ShopLimits sqlArgToJava(FieldType fieldType, Object object, int columnPos) {
            return object instanceof String ? gson.fromJson((String) object, ShopLimits.class) : null;
        }

    }

    public static final class JsonAdapter implements JsonSerializer<ShopLimits>, JsonDeserializer<ShopLimits> {

        @Override
        public @Nullable ShopLimits deserialize(
                @NotNull JsonElement jsonElement,
                @NotNull Type type,
                @NotNull JsonDeserializationContext context
        ) throws JsonParseException {
            Map<?, ?> asMap = context.deserialize(jsonElement, HashMap.class);
            if(asMap == null || asMap.isEmpty())
                return null;

            Map<String, Double> counters = new LinkedHashMap<>();
            asMap.forEach((key, value) -> {
                String keyAsString = key.toString();
                String valueAsString = value.toString();

                try {
                    double valueAsDouble = Double.parseDouble(valueAsString);
                    counters.put(keyAsString, valueAsDouble);
                } catch (NumberFormatException ignored) {
                }
            });

            return new ShopLimits(null, counters);
        }

        @Override
        public @NotNull JsonElement serialize(
                @Nullable ShopLimits shopLimit,
                @NotNull Type type,
                @NotNull JsonSerializationContext context
        ) {
            return shopLimit != null ? context.serialize(shopLimit.counters) : JsonNull.INSTANCE;
        }

    }

}
