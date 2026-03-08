package com.samhcoco.tools.invoicetool.service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface FileReader {
    String readFile(Path path) throws IOException;
    List<String> getAllFilesInDirectoryWithExtensionAsString(Path directoryPath, String fileExtension);
}
