/*
 * Copyright (c) 2018 Memorial Sloan-Kettering Cancer Center.
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
*/

package org.mskcc.cmo.ks.darwin;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.SQLQueryFactory;

import java.util.*;

import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import org.mskcc.cmo.ks.darwin.pipeline.model.MskimpactBrainSpineTimeline;
import org.mskcc.cmo.ks.darwin.pipeline.mskimpactbrainspinetimeline.MskimpactTimelineBrainSpineReader;
import org.mskcc.cmo.ks.darwin.pipeline.util.DarwinSampleListUtil;

import org.springframework.batch.item.ItemStreamReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author Manda Wilson
 */
@Configuration
public class DarwinTestConfiguration {

    @Bean
    public SQLQueryFactory darwinQueryFactory() {
        SQLQueryFactory darwinQueryFactory = Mockito.mock(SQLQueryFactory.class);

        // mock behavior in MskimpactTimelineBrainSpineReader.getDarwinTimelineResults()
        // for MskimpactBrainSpineTimeline query
        SQLQuery<MskimpactBrainSpineTimeline> mskimpactBrainSpineTimelineSqlQuery = Mockito.mock(SQLQuery.class);
        Mockito.when(darwinQueryFactory.selectDistinct(ArgumentMatchers.<Expression<MskimpactBrainSpineTimeline>>any())).thenReturn(mskimpactBrainSpineTimelineSqlQuery);
        Mockito.when(mskimpactBrainSpineTimelineSqlQuery.from(ArgumentMatchers.<Expression<MskimpactBrainSpineTimeline>>any())).thenReturn(mskimpactBrainSpineTimelineSqlQuery);
        Mockito.when(mskimpactBrainSpineTimelineSqlQuery.where(ArgumentMatchers.any(Predicate.class))).thenReturn(mskimpactBrainSpineTimelineSqlQuery);
        Mockito.when(mskimpactBrainSpineTimelineSqlQuery.fetch()).thenReturn(makeMockMskimpactBrainSpineTimelineResults());

        return darwinQueryFactory;
    }

    @Bean
    public DarwinSampleListUtil darwinSampleListUtil() {
        return new DarwinSampleListUtil();
    }

    @Bean
    public ItemStreamReader<MskimpactBrainSpineTimeline> mskimpactTimelineBrainSpineReader() {
        return new MskimpactTimelineBrainSpineReader();
    }

    private List<MskimpactBrainSpineTimeline> makeMockMskimpactBrainSpineTimelineResults() {
        List<MskimpactBrainSpineTimeline> mskimpactBrainSpineTimelineResults = new ArrayList<MskimpactBrainSpineTimeline>();

        MskimpactBrainSpineTimeline mskimpactBrainSpineTimelineNAStartDate = new MskimpactBrainSpineTimeline();
        mskimpactBrainSpineTimelineNAStartDate.setDMP_PATIENT_ID_ALL_BRAINSPINETMLN("P-0000001");
        mskimpactBrainSpineTimelineNAStartDate.setSTART_DATE("NA");

        MskimpactBrainSpineTimeline mskimpactBrainSpineTimelineNullStartDate = new MskimpactBrainSpineTimeline();
        mskimpactBrainSpineTimelineNullStartDate.setDMP_PATIENT_ID_ALL_BRAINSPINETMLN("P-0000002");
        mskimpactBrainSpineTimelineNullStartDate.setSTART_DATE(null);

        MskimpactBrainSpineTimeline mskimpactBrainSpineTimelineValidStartDate = new MskimpactBrainSpineTimeline();
        mskimpactBrainSpineTimelineValidStartDate.setDMP_PATIENT_ID_ALL_BRAINSPINETMLN("P-0000003");
        mskimpactBrainSpineTimelineValidStartDate.setSTART_DATE("757");

        mskimpactBrainSpineTimelineResults.add(mskimpactBrainSpineTimelineNAStartDate); // should be filtered by MskimpactTimelineBrainSpineReader.read()
        mskimpactBrainSpineTimelineResults.add(mskimpactBrainSpineTimelineNullStartDate); // should be filtered by MskimpactTimelineBrainSpineReader.read()
        mskimpactBrainSpineTimelineResults.add(mskimpactBrainSpineTimelineValidStartDate); // should not be filtered by MskimpactTimelineBrainSpineReader.read()

        return mskimpactBrainSpineTimelineResults;
    }

}
