package org.anish.urlshortener.controller;

import lombok.AllArgsConstructor;
import org.anish.urlshortener.dtos.UrlMappingDTO;
import org.anish.urlshortener.models.UrlMapping;
import org.anish.urlshortener.models.User;
import org.anish.urlshortener.service.UrlMappingService;
import org.anish.urlshortener.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/urls")
@AllArgsConstructor
public class UrlMappingController {
    private UrlMappingService urlMappingService;
    private UserService userService;

    // {"originalUrl":"https://example.com"} map contains in this structure
    //    https://abc.com/v3ZNwgDb --> https://example.com (abc.com is the domain of our host application)
    //    https://abc.com/v3ZNwgDb --> is linked to the original URL https://example.com by our application
    //    and the user is redirected to the original URL when they visit the short URL .

    @PostMapping("/shorten")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UrlMappingDTO> createShortUrl(@RequestBody Map<String, String> request , Principal principal) {
        String originalUrl = request.get("originalUrl");
        User user = userService.findByUsername(principal.getName());
        UrlMappingDTO urlMappingDTO = urlMappingService.createShortUrl(originalUrl, user);
        return ResponseEntity.ok(urlMappingDTO);
    }
    // this method is used to create a short URL for the given original URL.
    // The user must be authenticated to create a short URL.
    // The method takes the original URL and the principal object as input and returns the short URL as a response.

    @GetMapping("/myurls")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<UrlMappingDTO>> getUserUrls(Principal principal){
        User user = userService.findByUsername(principal.getName());
        List<UrlMappingDTO> urls = urlMappingService.getUrlsByUser(user);
        return ResponseEntity.ok(urls);
    }
}

