package com.example.spot.legacy.repository.rsa;

import com.example.spot.legacy.domain.auth.RsaKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface RSAKeyRepository extends JpaRepository<RsaKey, Long> {

    void deleteByCreatedAtBefore(LocalDateTime localDateTime);
}
