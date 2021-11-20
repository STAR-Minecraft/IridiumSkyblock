package com.iridium.iridiumskyblock.task;

import lombok.Getter;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Getter
public final class DataCacheFile {

    private final Logger logger;
    private final Path filePath;

    public DataCacheFile(@NotNull Plugin plugin, @NotNull String fileName) {
        this.logger = plugin.getLogger();
        this.filePath = plugin.getDataFolder().toPath().resolve(fileName);
    }

    public @NotNull Optional<String> read(int lineIndex) {
        try {
            if(!Files.isRegularFile(filePath))
                return Optional.empty();

            List<String> content = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            if(content == null || content.size() <= lineIndex)
                return Optional.empty();

            return Optional.ofNullable(content.get(lineIndex));
        } catch (IOException ex) {
            logger.severe("Couldn't read content from file '" + filePath.toAbsolutePath() + "': " + ex);
            return Optional.empty();
        }
    }

    public @NotNull OptionalInt readAsInt(int lineIndex) {
        return readAndConvert(lineIndex, string -> OptionalInt.of(Integer.parseInt(string)), OptionalInt::empty);
    }

    public @NotNull OptionalLong readAsLong(int lineIndex) {
        return readAndConvert(lineIndex, string -> OptionalLong.of(Long.parseLong(string)), OptionalLong::empty);
    }

    public @NotNull OptionalDouble readAsDouble(int lineIndex) {
        return readAndConvert(lineIndex, string -> OptionalDouble.of(Double.parseDouble(string)), OptionalDouble::empty);
    }

    public <T> @NotNull T readAndConvert(int lineIndex, @NotNull Function<String, T> converter, @NotNull Supplier<T> defaultValue) {
        try {
            Optional<String> asString = read(lineIndex);
            return converter.apply(asString.get());
        } catch (NoSuchElementException | NumberFormatException ignored) {
            return defaultValue.get();
        }
    }

    public boolean write(Object... lines) {
        try {
            Path parentDirectory = filePath.getParent();
            if(!Files.isDirectory(parentDirectory))
                Files.createDirectories(parentDirectory);

            if(lines == null || lines.length == 0)
                return false;

            List<String> content = Arrays.stream(lines).map(String::valueOf).collect(Collectors.toList());
            Files.write(filePath, content, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            return true;
        } catch (IOException ex) {
            logger.severe("Couldn't write content to file '" + filePath.toAbsolutePath() + "': " + ex);
            return false;
        }
    }

}
