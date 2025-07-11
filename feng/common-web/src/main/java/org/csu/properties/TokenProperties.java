package org.csu.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * token配置类
 */
@Data
@ConfigurationProperties(prefix = "csu.token")
public class TokenProperties {
    private String shortSecretKey ="a-very-long-and-random-secret-key-with-32-chars";
    private String longSecretKey ="csu";
    private Duration shortTokenTTL = Duration.ofHours(12);
    private Duration longTokenTTL = Duration.ofDays(1);
}
