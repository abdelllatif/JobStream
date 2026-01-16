package com.job.service.impl;

import com.job.dto.request.DomainCreateRequestDTO;
import com.job.dto.response.DomainResponseDTO;
import com.job.entity.Domain;
import com.job.mapper.DomainMapper;
import com.job.repository.DomainRepository;
import com.job.service.DomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DomainServiceImpl implements DomainService {

    private final DomainRepository domainRepository;
    private final DomainMapper domainMapper;

    @Override
    public DomainResponseDTO create(DomainCreateRequestDTO dto) {
        // Vérifier si le domaine existe déjà
        if (domainRepository.existsByName(dto.getName().toLowerCase())) {
            throw new IllegalArgumentException("Domain with name '" + dto.getName() + "' already exists");
        }

        Domain domain = domainMapper.toEntity(dto);
        domain.setName(dto.getName().toLowerCase());

        Domain savedDomain = domainRepository.save(domain);
        return domainMapper.toResponseDTO(savedDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DomainResponseDTO> getById(Long id) {
        return domainRepository.findById(id)
                .map(domainMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DomainResponseDTO> getAll() {
        return domainRepository.findAll().stream()
                .map(domainMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DomainResponseDTO> getByName(String name) {
        return domainRepository.findByName(name.toLowerCase())
                .map(domainMapper::toResponseDTO);
    }

    @Override
    public DomainResponseDTO update(Long id, DomainCreateRequestDTO dto) {
        Domain domain = domainRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Domain not found with id: " + id));

        // Vérifier si le nouveau nom est déjà utilisé par un autre domaine
        if (!domain.getName().equals(dto.getName().toLowerCase()) && 
            domainRepository.existsByName(dto.getName().toLowerCase())) {
            throw new IllegalArgumentException("Domain with name '" + dto.getName() + "' already exists");
        }

        domain.setName(dto.getName().toLowerCase());
        Domain updatedDomain = domainRepository.save(domain);
        return domainMapper.toResponseDTO(updatedDomain);
    }

    @Override
    public void delete(Long id) {
        if (!domainRepository.existsById(id)) {
            throw new IllegalArgumentException("Domain not found with id: " + id);
        }
        domainRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return domainRepository.existsByName(name.toLowerCase());
    }
}
