
package com.warmer.web.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "file")
@Data
public class WebAppConfig{

    /**
     * 在配置文件中配置的文件保存路径
     */
    private String location;
    
    private String serverurl;

}