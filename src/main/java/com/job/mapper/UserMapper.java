package com.job.mapper;

import com.job.dto.request.UserCreateRequestDTO;
import com.job.dto.request.UserUpdateRequestDTO;
import com.job.dto.response.UserResponseDTO;
import com.job.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(UserCreateRequestDTO dto);
    User toEntity(UserUpdateRequestDTO dto);

    UserResponseDTO toResponse(User user);
}
