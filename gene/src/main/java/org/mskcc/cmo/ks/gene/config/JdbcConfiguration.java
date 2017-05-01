/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mskcc.cmo.ks.gene.config;

import org.springframework.context.annotation.Configuration;

import java.sql.SQLException;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.*;

/**
 *
 * @author ochoaa
 */
@Configuration
public class JdbcConfiguration {
    
    @Value("${db.user}")
    private String dbUser;
    
    @Value("${db.password}")
    private String dbPassword;
    
    @Value("${db.driver}")
    private String dbDriver;
        
    @Value("${db.url}")
    private String dbUrl;
    
    @Value("${DATABASE_NAME}")
    private String DATABASE_NAME;

    public DataSource mainDataSource() throws SQLException {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUsername(dbUser);
        dataSource.setPassword(dbPassword);
        dataSource.setDriverClassName(dbDriver);        
        dataSource.setUrl(dbUrl + DATABASE_NAME);
        dataSource.setDefaultTransactionIsolation(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);

        return dataSource; 
    }

    /**
     * Bean that holds the named parameter jdbc template.
     * 
     * @return NamedParameterJdbcTemplate
     * @throws SQLException 
     */
    @Bean(name="namedParameterJdbcTemplate")
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate() throws SQLException {
        return new NamedParameterJdbcTemplate(mainDataSource());
    }
}
