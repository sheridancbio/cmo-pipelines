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

package org.cbioportal.cmo.pipelines.crdb;

import org.cbioportal.cmo.pipelines.crdb.model.CRDBDataset;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.springframework.batch.item.ItemProcessor;

/**
 * @author Benjamin Gross
 */
public class CRDBDatasetProcessor implements ItemProcessor<CRDBDataset, String>
{
    ObjectMapper mapper = new ObjectMapper();
    
    @Override
    public String process(final CRDBDataset crdbDataset) throws Exception
    {
        List<String> record = Arrays.asList(crdbDataset.getDMP_ID(), crdbDataset.getCONSENT_DATE_DAYS(), 
                crdbDataset.getPRIM_DISEASE_12245(), crdbDataset.getINITIAL_SX_DAYS(), crdbDataset.getINITIAL_DX_DAYS(), 
                crdbDataset.getFIRST_METASTASIS_DAYS(), crdbDataset.getINIT_DX_STATUS_ID(), crdbDataset.getINIT_DX_STATUS(), 
                crdbDataset.getINIT_DX_STATUS_DAYS(), crdbDataset.getINIT_DX_STAGING_DSCRP(), crdbDataset.getINIT_DX_STAGE(), 
                crdbDataset.getINIT_DX_STAGE_DSCRP(), crdbDataset.getINIT_DX_GRADE(), crdbDataset.getINIT_DX_GRADE_DSCRP(), 
                crdbDataset.getINIT_DX_T_STAGE(), crdbDataset.getINIT_DX_T_STAGE_DSCRP(), crdbDataset.getINIT_DX_N_STAGE(), 
                crdbDataset.getINIT_DX_N_STAGE_DSCRP(), crdbDataset.getINIT_DX_M_STAGE(), crdbDataset.getINIT_DX_M_STAGE_DSCRP(), 
                crdbDataset.getINIT_DX_HIST(), crdbDataset.getINIT_DX_SUB_HIST(), crdbDataset.getINIT_DX_SUB_SUB_HIST(), 
                crdbDataset.getINIT_DX_SUB_SUB_SUB_HIST(), crdbDataset.getINIT_DX_SITE(), crdbDataset.getINIT_DX_SUB_SITE(), 
                crdbDataset.getINIT_DX_SUB_SUB_SITE(), crdbDataset.getENROLL_DX_STATUS_ID(), crdbDataset.getENROLL_DX_STATUS(), 
                crdbDataset.getENROLL_DX_STATUS_DAYS(), crdbDataset.getENROLL_DX_STAGING_DSCRP(), crdbDataset.getENROLL_DX_STAGE(), 
                crdbDataset.getENROLL_DX_STAGE_DSCRP(), crdbDataset.getENROLL_DX_GRADE(), crdbDataset.getENROLL_DX_GRADE_DSCRP(), 
                crdbDataset.getENROLL_DX_T_STAGE(), crdbDataset.getENROLL_DX_T_STAGE_DSCRP(), crdbDataset.getENROLL_DX_N_STAGE(), 
                crdbDataset.getENROLL_DX_N_STAGE_DSCRP(), crdbDataset.getENROLL_DX_M_STAGE(), crdbDataset.getENROLL_DX_M_STAGE_DSCRP(), 
                crdbDataset.getENROLL_DX_HIST(), crdbDataset.getENROLL_DX_SUB_HIST(), crdbDataset.getENROLL_DX_SUB_SUB_HIST(), 
                crdbDataset.getENROLL_DX_SUB_SUB_SUB_HIST(), crdbDataset.getENROLL_DX_SITE(), crdbDataset.getENROLL_DX_SUB_SITE(), 
                crdbDataset.getENROLL_DX_SUB_SUB_SITE(), crdbDataset.getSURVIVAL_STATUS(), crdbDataset.getTREATMENT_END_DAYS(), 
                crdbDataset.getOFF_STUDY_DAYS(), crdbDataset.getCOMMENTS());

        return StringUtils.join(record, "\t");
    }
}
