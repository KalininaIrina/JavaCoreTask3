package com.task.port.reader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

public class PortReader {
    private static final Logger logger = LogManager.getLogger();

    public List<String> readLines(String filePath) {
        Path path = Paths.get(filePath);
        try {
            return Files.readAllLines(path);
        } catch (IOException e) {
            logger.fatal("Error reading file: {}", filePath, e);
            return Collections.emptyList();
        }
    }
}