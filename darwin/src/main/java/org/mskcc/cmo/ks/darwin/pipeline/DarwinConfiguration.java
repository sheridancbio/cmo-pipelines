/*
 * Copyright (c) 2016 Memorial Sloan-Kettering Cancer Center.
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
package org.mskcc.cmo.ks.darwin.pipeline;

import com.ibm.db2.jcc.DB2SimpleDataSource;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.DB2Templates;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Pattern;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.beans.factory.annotation.Value;
/**
 *
 * @author jake
 */
@Configuration
@PropertySource("classpath:application.properties")
public class DarwinConfiguration {

    @Value("${darwin.username}")
    private String username;

    @Value("${darwin.password}")
    private String password;

    @Value("${darwin.server}")
    private String server;

    @Value("${darwin.port}")
    private Integer port;

    @Value("${darwin.database}")
    private String database;

    @Value("${darwin.schema}")
    private String schema;

    private final Pattern MSKIMPACT_REGEX = Pattern.compile("P-\\d*-T\\d*-IM\\d*");
    private final Pattern MSKIMPACT_HEME_REGEX = Pattern.compile("P-\\d*-T\\d*-IH\\d*");
    private final Pattern MSKARCHER_REGEX = Pattern.compile("P-\\d*-T\\d*-A[H|S]\\d*");
    private final Pattern MSKRAINDANCE_REGEX = Pattern.compile("P-\\d*-T\\d*-T[B|S]\\d*");

    @Bean
    public SQLQueryFactory darwinQueryFactory() throws SQLException{
        DB2Templates templates = new DB2Templates();
        com.querydsl.sql.Configuration config = new com.querydsl.sql.Configuration(templates);
        return new SQLQueryFactory(config, darwinDataSource());
    }

    public DB2SimpleDataSource darwinDataSource(){
        DB2SimpleDataSource dataSource = new DB2SimpleDataSource();
        dataSource.setPortNumber(port);
        dataSource.setUser(username);
        dataSource.setPassword(password);
        dataSource.setDatabaseName(database);
        dataSource.setCurrentSchema(schema);
        dataSource.setServerName(server);
        dataSource.setDriverType(4);
        return dataSource;
    }

    @Bean(name="studyIdRegexMap")
    public Map<String, Pattern> studyIdRegexMap() {
        Map<String, Pattern> studyIdRegexMap = new HashMap<>();
        studyIdRegexMap.put("mskimpact", MSKIMPACT_REGEX);
        studyIdRegexMap.put("mskimpact_heme", MSKIMPACT_HEME_REGEX);
        studyIdRegexMap.put("mskarcher", MSKARCHER_REGEX);
        studyIdRegexMap.put("mskraindance", MSKRAINDANCE_REGEX);
        return studyIdRegexMap;
    }
}
