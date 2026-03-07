package com.samhcoco.tools.invoicetool.service;

import java.io.IOException;
import java.nio.file.Path;

public interface FileReader {
    public String readFile(Path path) throws IOException;
}
