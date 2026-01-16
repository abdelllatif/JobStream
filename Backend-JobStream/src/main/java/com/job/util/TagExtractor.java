package com.job.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TagExtractor {

    private static final Pattern HASHTAG_PATTERN = Pattern.compile("#\\w+");

    /**
     * Extrait tous les tags (#word) d'un texte
     * @param text Le texte à analyser
     * @return Liste des tags trouvés (sans le #)
     */
    public static List<String> extractTags(String text) {
        List<String> tags = new ArrayList<>();
        
        if (text == null || text.trim().isEmpty()) {
            return tags;
        }

        Matcher matcher = HASHTAG_PATTERN.matcher(text);
        
        while (matcher.find()) {
            String tag = matcher.group();
            // Remove the # symbol and convert to lowercase
            tags.add(tag.substring(1).toLowerCase());
        }
        
        return tags;
    }

    /**
     * Extrait les tags et retourne une chaîne unique séparée par des virgules
     * @param text Le texte à analyser
     * @return Chaîne de tags séparés par des virgules
     */
    public static String extractTagsAsString(String text) {
        List<String> tags = extractTags(text);
        return String.join(",", tags);
    }

    /**
     * Vérifie si un texte contient des tags
     * @param text Le texte à vérifier
     * @return true si des tags sont présents
     */
    public static boolean hasTags(String text) {
        if (text == null || text.trim().isEmpty()) {
            return false;
        }
        return HASHTAG_PATTERN.matcher(text).find();
    }

    /**
     * Compte le nombre de tags dans un texte
     * @param text Le texte à analyser
     * @return Nombre de tags trouvés
     */
    public static int countTags(String text) {
        if (text == null || text.trim().isEmpty()) {
            return 0;
        }
        
        Matcher matcher = HASHTAG_PATTERN.matcher(text);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }
}
