/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cbioportal.cmo.pipelines.crdb;

import java.sql.SQLException;
import javax.validation.constraints.NotNull;
import oracle.jdbc.pool.OracleDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author angelica
 */
@Configuration
@ConfigurationProperties("oracle")
public class OracleConfiguration {
    
    @NotNull
    private String username;
    
    @NotNull
    private String password;
    
    @NotNull
    private String url;
    
    public static final String CRDB_DATA_SOURCE = "crdbDataSource";
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    
    @Bean
    OracleDataSource crdbDataSource() throws SQLException {
        OracleDataSource crdbDataSource = new OracleDataSource();
        crdbDataSource.setUser(username);
        crdbDataSource.setPassword(password);
        crdbDataSource.setURL(url);
        return crdbDataSource;
    }    
    
}
