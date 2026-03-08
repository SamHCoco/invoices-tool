package com.samhcoco.tools.invoicetool.service.impl;

import com.samhcoco.tools.invoicetool.service.PdfFileReader;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.util.List;

import static java.lang.String.format;

@Slf4j
@Service
public class PdfFileReaderImpl extends FileReaderImpl implements PdfFileReader {

    @Override
    public String readFile(Path path) throws IOException {
        try (PDDocument document = Loader.loadPDF(path.toFile())) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        } catch (IOException e) {
            final String error = format("Failed to read PDF file for Path %s: %s", path, e.getMessage());
            log.error(error);
            throw new RuntimeException(error);
        }
    }

    @Override
    public List<String> getAllPDFsInDirectoryAsString(@NonNull Path directoryPath) {
        return getAllFilesInDirectoryWithExtensionAsString(directoryPath, "pdf");
    }

//    @Override
//    public DirectoryStream<Path> getAllFilePathsInDirectoryWithExtension(@NonNull Path directoryPath,
//                                                                         @NonNull String fileExtension) {
//        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directoryPath, format("*.%s", fileExtension))) {
//            return stream;
//        } catch (IOException e) {
//            final String error = format("Error reading PDFs from " + directoryPath + ": ", e);
//            log.error(error);
//            throw new RuntimeException(error);
//        }
//    }
}
