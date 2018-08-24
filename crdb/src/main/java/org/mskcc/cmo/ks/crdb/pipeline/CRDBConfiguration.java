/*
 * Copyright (c) 2016 - 2018 Memorial Sloan-Kettering Cancer Center.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF MERCHANTABILITY OR FITNESS
 * FOR A PARTICULAR PURPOSE. The software and documentation provided hereunder
 * is on an "as is" basis, and Memorial Sloan-Kettering Cancer Center has no
 * obligations to provide maintenance, support, updates, enhancements or
 * modifications. In no event shall Memorial Sloan-Kettering Cancer Center be
 * liable to any party for direct, indirect, special, incidental or
 * consequential damages, including lost profits, arising out of the use of this
 * software and its documentation, even if Memorial Sloan-Kettering Cancer
 * Center has been advised of the possibility of such damage.
 */

/*
 * This file is part of cBioPortal CMO-Pipelines.
 *
 * cBioPortal is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.mskcc.cmo.ks.crdb;

import com.querydsl.sql.OracleTemplates;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.SQLTemplates;
import oracle.jdbc.pool.OracleDataSource;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for the CRDB query factory.
 *
 * @author ochoaa
 */

@Configuration
public class CRDBConfiguration {
    @Value("${crdb.username}")
    private String username;

    @Value("${crdb.password}")
    private String password;

    @Value("${crdb.connection_string}")
    private String connection_string;

    @Bean
    public SQLQueryFactory crdbQueryFactory() throws SQLException {
        SQLTemplates templates = new OracleTemplates();
        com.querydsl.sql.Configuration config = new com.querydsl.sql.Configuration(templates);
        return  new SQLQueryFactory(config, crdbDataSource());
    }

    public OracleDataSource crdbDataSource() throws SQLException {
        OracleDataSource crdbDataSource = new OracleDataSource();
        crdbDataSource.setUser(username);
        crdbDataSource.setPassword(password);
        crdbDataSource.setURL(connection_string);
        return crdbDataSource;
    }
}
