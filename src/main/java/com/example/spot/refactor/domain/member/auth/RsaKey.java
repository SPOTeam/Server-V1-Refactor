package com.example.spot.refactor.domain.member.auth;

import com.example.spot.refactor.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RsaKey extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Long id;

    @Column(columnDefinition = "text")
    private String privateKey;

    @Column(columnDefinition = "text")
    private String publicKey;

    @Column(columnDefinition = "text")
    private String modulus;

    @Column(columnDefinition = "text")
    private String exponent;
}
