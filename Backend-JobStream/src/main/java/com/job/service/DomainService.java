package com.job.service;

import com.job.dto.request.DomainCreateRequestDTO;
import com.job.dto.response.DomainResponseDTO;

import java.util.List;
import java.util.Optional;

public interface DomainService {
    
    /**
     * Crée un nouveau domaine
     * @param dto Les informations du domaine à créer
     * @return Le domaine créé
     */
    DomainResponseDTO create(DomainCreateRequestDTO dto);
    
    /**
     * Récupère un domaine par son ID
     * @param id L'ID du domaine
     * @return Le domaine trouvé ou empty
     */
    Optional<DomainResponseDTO> getById(Long id);
    
    /**
     * Récupère tous les domaines
     * @return La liste de tous les domaines
     */
    List<DomainResponseDTO> getAll();
    
    /**
     * Récupère un domaine par son nom
     * @param name Le nom du domaine
     * @return Le domaine trouvé ou empty
     */
    Optional<DomainResponseDTO> getByName(String name);
    
    /**
     * Met à jour un domaine
     * @param id L'ID du domaine à mettre à jour
     * @param dto Les nouvelles informations
     * @return Le domaine mis à jour
     */
    DomainResponseDTO update(Long id, DomainCreateRequestDTO dto);
    
    /**
     * Supprime un domaine
     * @param id L'ID du domaine à supprimer
     */
    void delete(Long id);
    
    /**
     * Vérifie si un domaine existe par son nom
     * @param name Le nom du domaine
     * @return true si le domaine existe
     */
    boolean existsByName(String name);
}
