package com.warmer.web.config;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.neo4j") //指明配置节点
public class Neo4jConfig {
	@Value("${spring.neo4j.url}")
	private String url;

	@Value("${spring.neo4j.username}")
	private String username;

	@Value("${spring.neo4j.password}")
	private String password;

	/**
	 * 图数据库驱动模式
	 */

	@Bean
	public Driver neo4jDriver() {
		return GraphDatabase.driver(url, AuthTokens.basic(username, password));
	}

}