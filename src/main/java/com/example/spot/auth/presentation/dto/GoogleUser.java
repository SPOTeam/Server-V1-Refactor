package com.example.spot.auth.presentation.dto;

public record GoogleUser(
        String id,
        String email,
        Boolean verifiedEmail,
        String name,
        String givenName,
        String familyName,
        String picture,
        String locale
) {

}
