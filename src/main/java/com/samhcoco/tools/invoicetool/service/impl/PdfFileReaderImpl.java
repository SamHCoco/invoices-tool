package com.samhcoco.tools.invoicetool.service.impl;

import com.samhcoco.tools.invoicetool.service.PdfFileReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;

@Slf4j
@Service
public class PdfFileReaderImpl implements PdfFileReader {

    @Override
    public String readFile(Path path) throws IOException {
        try (PDDocument document = Loader.loadPDF(path.toFile())) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        } catch (IOException e) {
            log.error("Failed to read PDF file for Path {}: {}", path, e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
