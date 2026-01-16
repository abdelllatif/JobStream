package com.job.service;

import com.job.dto.response.TagExtractionResponseDTO;

public interface TagService {
    
    /**
     * Extrait les tags d'un texte donné
     * @param text Le texte à analyser
     * @return Les informations sur les tags extraits
     */
    TagExtractionResponseDTO extractTags(String text);
    
    /**
     * Extrait les tags d'un texte et les retourne comme liste
     * @param text Le texte à analyser
     * @return Liste des tags extraits
     */
    java.util.List<String> extractTagsAsList(String text);
    
    /**
     * Extrait les tags d'un texte et les retourne comme chaîne
     * @param text Le texte à analyser
     * @return Chaîne de tags séparés par des virgules
     */
    String extractTagsAsString(String text);
    
    /**
     * Vérifie si un texte contient des tags
     * @param text Le texte à vérifier
     * @return true si des tags sont présents
     */
    boolean hasTags(String text);
    
    /**
     * Compte le nombre de tags dans un texte
     * @param text Le texte à analyser
     * @return Nombre de tags trouvés
     */
    int countTags(String text);
}
