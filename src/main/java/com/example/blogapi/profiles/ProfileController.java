package com.example.blogapi.profiles;

import com.example.blogapi.profiles.dto.ProfileResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/profiles")
public class ProfileController {

    private  final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("")
    public ResponseEntity<List<ProfileResponseDTO>> getProfiles(){
        return ResponseEntity.ok(profileService.getAllProfiles());
    }

    @GetMapping("/{username}")
    public ResponseEntity<ProfileResponseDTO> getProfileByUsername(@PathVariable String username){
        return ResponseEntity.ok(profileService.getProfileByUsername(username));
    }

}
