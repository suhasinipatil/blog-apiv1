package com.example.blogapi.profiles;

import com.example.blogapi.articles.ArticlesController;
import com.example.blogapi.profiles.dto.ProfileResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/profiles")
public class ProfileController {

    private  final ProfileService profileService;

    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);
    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("")
    public ResponseEntity<List<ProfileResponseDTO>> getProfile(@RequestParam(required = false) String username){
        logger.info("Received request to get profile by username: {}", username);
        if(username != null){
            return ResponseEntity.ok(List.of(profileService.getProfileByUsername(username)));
        }
        return ResponseEntity.ok(profileService.getAllProfiles());
    }

}
