package plagarismchecker.utils;

import java.io.InputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import org.springframework.web.multipart.MultipartFile;

public class FileTextExtractor {

    public static String extractText(
            MultipartFile file)
            throws Exception {

        if(file == null || file.isEmpty()){

            System.out.println("FILE EMPTY");

            return "";
        }

        String fileName =
                file.getOriginalFilename();

        if(fileName == null){

            System.out.println("FILENAME NULL");

            return "";
        }

        fileName =
                fileName.toLowerCase();

        System.out.println("FILE NAME: "
                + fileName);

        // TXT

        if(fileName.endsWith(".txt")){

            String text =
                    new String(
                            file.getBytes()
                    );

            System.out.println("TXT TEXT:");
            System.out.println(text);

            return text.trim();
        }

        // PDF

        else if(fileName.endsWith(".pdf")){

            PDDocument document =
                    PDDocument.load(
                            file.getInputStream()
                    );

            PDFTextStripper stripper =
                    new PDFTextStripper();

            String text =
                    stripper.getText(document);

            document.close();

            System.out.println("PDF TEXT:");
            System.out.println(text);

            return text.trim();
        }

        // DOCX

        else if(fileName.endsWith(".docx")){

            InputStream inputStream =
                    file.getInputStream();

            XWPFDocument document =
                    new XWPFDocument(
                            inputStream
                    );

            XWPFWordExtractor extractor =
                    new XWPFWordExtractor(
                            document
                    );

            String text =
                    extractor.getText();

            extractor.close();
            document.close();

            System.out.println("DOCX TEXT:");
            System.out.println(text);

            return text.trim();
        }

        System.out.println("UNSUPPORTED FILE");

        return "";
    }
}