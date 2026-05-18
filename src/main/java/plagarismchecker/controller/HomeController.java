package plagarismchecker.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.multipart.MultipartFile;

import plagarismchecker.service.PlagiarismService;
import plagarismchecker.service.WebSearchService;

import plagarismchecker.utils.FileTextExtractor;

@Controller
public class HomeController {

    @Autowired
    private PlagiarismService plagiarismService;

    @Autowired
    private WebSearchService webSearchService;

    @GetMapping("/")
    public String home() {

        return "index";
    }

    @PostMapping("/check")
    public String check(

            @RequestParam(required = false)
            String text1,

            @RequestParam(required = false)
            String text2,

            @RequestParam(required = false)
            String webText,

            @RequestParam(required = false)
            MultipartFile file1,

            @RequestParam(required = false)
            MultipartFile file2,

            @RequestParam(required = false)
            MultipartFile webFile,

            @RequestParam String mode,

            Model model

    ) {

        try {

            // TEXT MODE

            if(mode.equals("text")){

                double similarity =
                        plagiarismService.calculateSimilarity(
                                text1,
                                text2
                        );

                model.addAttribute(
                        "similarity",
                        similarity
                );
            }

            // FILE MODE

            else if(mode.equals("file")){

                if(file1 == null
                || file1.isEmpty()
                || file2 == null
                || file2.isEmpty()){

                    model.addAttribute(
                            "error",
                            "Please upload both files"
                    );

                    return "result";
                }

                String fileText1 =
                        FileTextExtractor
                        .extractText(file1);

                String fileText2 =
                        FileTextExtractor
                        .extractText(file2);

                double similarity =
                        plagiarismService
                        .calculateSimilarity(
                                fileText1,
                                fileText2
                        );

                model.addAttribute(
                        "similarity",
                        similarity
                );
            }

            // WEB MODE

            else if(mode.equals("web")){

                String content = "";

                // CLEAN TEXT

                if(webText != null){

                    webText =
                            webText.trim();
                }

                // FILE INPUT

                if(webFile != null
                && !webFile.isEmpty()){

                    content =
                            FileTextExtractor
                            .extractText(webFile);

                    System.out.println(content);
                }

                // TEXT INPUT

                else if(webText != null
                && !webText.isEmpty()){

                    content = webText;

                    System.out.println(content);
                }

                // NOTHING PROVIDED

                else{

                    model.addAttribute(
                            "error",
                            "No text or file provided"
                    );

                    return "result";
                }

                // SEARCH WEB

                List<String> sources =
                        webSearchService
                        .searchWeb(content);

                double highestSimilarity = 0;

                String bestSource = "";

                for(String source : sources){

                    try{

                        String webpageText =
                                webSearchService
                                .getWebpageText(source);

                        double similarity =
                                plagiarismService
                                .calculateSimilarity(
                                        content,
                                        webpageText
                                );

                        if(similarity
                                > highestSimilarity){

                            highestSimilarity =
                                    similarity;

                            bestSource =
                                    source;
                        }

                    } catch(Exception e){

                        e.printStackTrace();
                    }
                }

                model.addAttribute(
                        "similarity",
                        highestSimilarity
                );

                model.addAttribute(
                        "bestSource",
                        bestSource
                );

                model.addAttribute(
                        "sources",
                        sources
                );
            }

            // INVALID MODE

            else{

                model.addAttribute(
                        "similarity",
                        0
                );
            }

        }

        catch(Exception e){

            e.printStackTrace();

            model.addAttribute(
                    "similarity",
                    0
            );

            model.addAttribute(
                    "error",
                    e.getMessage()
            );
        }

        return "result";
    }
}