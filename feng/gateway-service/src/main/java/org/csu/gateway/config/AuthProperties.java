package org.csu.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * auth配置类
 */
@Data
@ConfigurationProperties(prefix = "csu.auth")
public class AuthProperties {
    private List<String> includePaths;
    private List<String> excludePaths;
    private String frontShortTokenName;
    private String frontLongTokenName;
}
