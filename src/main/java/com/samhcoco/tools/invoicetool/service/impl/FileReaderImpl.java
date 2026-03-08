package com.samhcoco.tools.invoicetool.service.impl;

import com.samhcoco.tools.invoicetool.service.FileReader;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

@Slf4j
@Service
public class FileReaderImpl implements FileReader {

    @Override
    public String readFile(Path path) throws IOException {
        try {
            return Files.readString(path);
        } catch (IOException e) {
            final String error = String.format("Failed to read file for Path %s: %s", path, e.getMessage());
            log.error(error);
            throw new RuntimeException(error, e);
        }
    }

    @Override
    public List<String> getAllFilesInDirectoryWithExtensionAsString(@NonNull Path directoryPath,
                                                                    @NonNull String fileExtension) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directoryPath, format("*.%s", fileExtension))) {
            List<String> filesAsString = new ArrayList<>();

            for (Path path : stream) {
                String file = readFile(path);
                filesAsString.add(file);
            }

            return filesAsString;
        } catch (IOException e) {
            final String error = format("Error reading and converting '.%s' files in " + directoryPath + " to string: %s", fileExtension, e);
            log.error(error);
            throw new RuntimeException(error);
        }
    }
}
