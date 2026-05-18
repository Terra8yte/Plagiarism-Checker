package plagarismchecker.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;

@Service
public class PlagiarismService {

    public double calculateSimilarity(
            String text1,
            String text2) {

        Set<String> set1 = processText(text1);
        Set<String> set2 = processText(text2);

        Set<String> intersection =
                new HashSet<>(set1);

        intersection.retainAll(set2);

        Set<String> union =
                new HashSet<>(set1);

        union.addAll(set2);

        if(union.size() == 0){
                return 0;
                }

        return ((double)
        intersection.size()
        / union.size()) * 100;
    }

    private Set<String> processText(
            String text) {

        text = text.toLowerCase();

        text = text.replaceAll(
                "[^a-zA-Z0-9 ]", "");

        String[] words =
                text.split("\\s+");

        return new HashSet<>(
                Arrays.asList(words));
    }
}