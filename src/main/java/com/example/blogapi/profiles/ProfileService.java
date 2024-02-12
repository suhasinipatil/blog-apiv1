package com.example.blogapi.profiles;

import com.example.blogapi.articles.ArticlesController;
import com.example.blogapi.profiles.dto.ProfileResponseDTO;
import com.example.blogapi.users.UserEntity;
import com.example.blogapi.users.UserRepository;
import com.example.blogapi.users.UserService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProfileService {

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    private static final Logger logger = LoggerFactory.getLogger(ProfileService.class);
    public ProfileService(UserRepository userRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    public List<ProfileResponseDTO> getAllProfiles() {
        logger.info("Received request to get all profiles");
        List<ProfileResponseDTO> profileResponseDTOList = new ArrayList<>();
        List<UserEntity> userEntityList = userRepository.findAll();
        for (UserEntity userEntity: userEntityList) {
            profileResponseDTOList.add(modelMapper.map(userEntity, ProfileResponseDTO.class));
        }
        return profileResponseDTOList;
    }

    public ProfileResponseDTO getProfileByUsername(String username) {
        logger.info("Received request to get profile by username: {}", username);
        var userEntity = userRepository.findByUsername(username);
        if(userEntity == null){
            logger.error("User with username: {} not found", username);
            throw new UserService.UserNotFoundException(username);
        }
        logger.info("User with username: {} found", username);
        return modelMapper.map(userEntity, ProfileResponseDTO.class);
    }
}
