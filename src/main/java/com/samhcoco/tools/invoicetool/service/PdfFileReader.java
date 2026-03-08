package com.samhcoco.tools.invoicetool.service;

import java.nio.file.Path;
import java.util.List;

public interface PdfFileReader extends FileReader {
    List<String> getAllPDFsInDirectoryAsString(Path directoryPath);
}
