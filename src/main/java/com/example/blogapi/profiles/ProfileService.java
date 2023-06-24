package com.example.blogapi.profiles;

import com.example.blogapi.profiles.dto.ProfileResponseDTO;
import com.example.blogapi.users.UserEntity;
import com.example.blogapi.users.UserRepository;
import com.example.blogapi.users.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProfileService {

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    public ProfileService(UserRepository userRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    public List<ProfileResponseDTO> getAllProfiles() {
        List<ProfileResponseDTO> profileResponseDTOList = new ArrayList<>();
        List<UserEntity> userEntityList = userRepository.findAll();
        for (UserEntity userEntity: userEntityList) {
            profileResponseDTOList.add(modelMapper.map(userEntity, ProfileResponseDTO.class));
        }
        return profileResponseDTOList;
    }

    public ProfileResponseDTO getProfileByUsername(String username) {
        var userEntity = userRepository.findByUsername(username);
        if(userEntity == null){
            throw new UserService.UserNotFoundException(username);
        }
        return modelMapper.map(userEntity, ProfileResponseDTO.class);
    }
}
