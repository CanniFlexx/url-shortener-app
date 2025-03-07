package org.anish.urlshortener.service;

import lombok.AllArgsConstructor;
import org.anish.urlshortener.dtos.UrlMappingDTO;
import org.anish.urlshortener.models.UrlMapping;
import org.anish.urlshortener.models.User;
import org.anish.urlshortener.repository.UrlMappingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UrlMappingService {

    private UrlMappingRepository urlMappingRepository;

    public UrlMappingDTO createShortUrl(String originalUrl, User user) {
        String shortUrl = generateShortUrl();
        UrlMapping urlMapping = new UrlMapping();
        urlMapping.setOriginalUrl(originalUrl);
        urlMapping.setShortUrl(shortUrl);
        urlMapping.setUser(user);
        urlMapping.setCreatedDate(LocalDateTime.now());
        UrlMapping savedUrlMapping = urlMappingRepository.save(urlMapping);
        return convertToDto(savedUrlMapping);
    } // This method creates a short URL for the given original URL and user.
    // It generates a short URL, creates a new UrlMapping entity, and saves it to the database.
    // It then converts the saved entity to a UrlMappingDTO object and returns it.

    private UrlMappingDTO convertToDto(UrlMapping urlMapping){
        UrlMappingDTO urlMappingDTO = new UrlMappingDTO();
        urlMappingDTO.setId(urlMapping.getId());
        urlMappingDTO.setOriginalUrl(urlMapping.getOriginalUrl());
        urlMappingDTO.setShortUrl(urlMapping.getShortUrl());
        urlMappingDTO.setClickCount(urlMapping.getClickCount());
        urlMappingDTO.setCreatedDate(urlMapping.getCreatedDate());
        urlMappingDTO.setUsername(urlMapping.getUser().getUsername());
        return urlMappingDTO;
    } // This method converts the UrlMapping entity to a UrlMappingDTO object.

    private String generateShortUrl() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        Random random = new Random();
        StringBuilder shortUrl = new StringBuilder(8);

        for (int i = 0; i < 8; i++) {
            shortUrl.append(characters.charAt(random.nextInt(characters.length())));
        }
        return shortUrl.toString();
    } //the crux logic of the application which generates a random 8-character string for the short URL.
    // // Total possible combinations = 62^8 = 218,340,105,584,896

    public List<UrlMappingDTO> getUrlsByUser(User user) {
        List<UrlMapping> urlMappings = urlMappingRepository.findByUser(user);
        return urlMappings.stream().map(this::convertToDto).collect(Collectors.toList());
    } // This method returns a list of UrlMappingDTO objects for the given user.

}
