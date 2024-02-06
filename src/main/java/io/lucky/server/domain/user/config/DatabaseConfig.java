package io.lucky.server.domain.user.config;

import org.springframework.boot.sql.init.DatabaseInitializationSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {

    // TODO 서비스 환경에서 사용할 빈을 등록하기
//    @Bean
//    public DataSource dataSource(){
//        new SimpleDriverDataSource()
//    }
}
