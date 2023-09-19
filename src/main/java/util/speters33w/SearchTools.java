package util.speters33w;

import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.ArrayList;
import java.util.List;

/**
 * A class that contains methods to perform fuzzy searches.
 * Written for a CSUGlobal project, but portable across other projects.
 *
 * @author Stephan Peters (speters33w)
 */
public class SearchTools {
    /**
     * Compares a search term with terms in a list and returns the closest match(es).
     *
     * @param list A list of search terms to be compared.
     * @param term A search term to be compared with terms in the list.
     * @return A list of search terms that are closest to the search term.
     */
    public static List<String> fuzzySearch(List<String> list, String term) {
        List<String> fuzzySearch = new ArrayList<>();
        LevenshteinDistance distance = new LevenshteinDistance();
        int minScore = 10;
        for (String item : list) {
            int score = distance.apply(item, term);
            if (score < minScore) {
                fuzzySearch.clear();
                fuzzySearch.add(item);
                minScore = score;
            } else if (score == minScore) {
                fuzzySearch.add(item);
            }
        }
        return fuzzySearch;
    }

    /**
     * Compares a search term with terms in a list and returns the closest match(es)
     * in the first checkUpTo characters.
     *
     * @param list      A list of search terms to be compared.
     * @param term      A search term to be compared with terms in the list.
     * @param checkUpTo Checks match for the first checkUpTo characters only.
     * @return A list of search terms that are closest to the search term.
     */
    public static List<String> fuzzySearch(List<String> list, String term, int checkUpTo) {
        List<String> fuzzySearch = new ArrayList<>();
        LevenshteinDistance distance = new LevenshteinDistance();
        int minScore = checkUpTo + 1;
        int score;
        for (String item : list) {
            if (item.length() >= checkUpTo) {
                score = distance.apply(item.substring(0, checkUpTo - 1), term.substring(0, checkUpTo - 1));
            } else {
                score = distance.apply(item, term);
            }
            if (score < minScore) {
                fuzzySearch.clear();
                fuzzySearch.add(item);
                minScore = score;
            } else if (score == minScore) {
                fuzzySearch.add(item);
            }
        }
        return fuzzySearch;
    }

    /**
     * Compares a search term with terms in a list and prompts the user with close match(es).
     *
     * @param list A list of search terms to be compared.
     * @param term A search term to be compared with terms in the list.
     **/
    public static void possibleMatches(List<String> list, String term) {
        List<String> possibleMatches = fuzzySearch(list, term, term.length());
        if (possibleMatches.size() >= 1) {
            System.out.print("Did you mean ");
            int i = possibleMatches.size();
            for (String possibleMake : possibleMatches) {
                if (i > 2) {
                    System.out.print(possibleMake + ", ");
                } else if (i == 2) {
                    System.out.print(possibleMake + ", or ");
                } else {
                    System.out.print(possibleMake + "?\n");
                }
                i--;
            }
        }
    }
}
