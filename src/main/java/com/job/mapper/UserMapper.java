package com.job.mapper;

import com.job.dto.request.UserCreateRequestDTO;
import com.job.dto.request.UserUpdateRequestDTO;
import com.job.dto.response.UserResponseDTO;
import com.job.dto.response.UserDTO;
import com.job.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = {CandidateProfileMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    User toEntity(UserCreateRequestDTO dto);
    User toEntity(UserUpdateRequestDTO dto);
    @Mapping(source = "profilePicture", target = "profileImagePath")

    UserResponseDTO toResponse(User user);

    @Mapping(source = "profilePicture", target = "profileImagePath")
    @Mapping(source = "candidateProfile", target = "candidateProfile")
    UserDTO toResponseDto(User user);
}
