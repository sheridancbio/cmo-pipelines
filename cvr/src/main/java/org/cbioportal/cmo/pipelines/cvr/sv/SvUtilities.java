/*
 * Copyright (c) 2022 Memorial Sloan-Kettering Cancer Center.
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

package org.cbioportal.cmo.pipelines.cvr.sv;

import com.google.common.base.Strings;
import org.apache.log4j.Logger;
import org.cbioportal.cmo.pipelines.cvr.model.CVRSvVariant;
import org.cbioportal.cmo.pipelines.cvr.model.GMLCnvIntragenicVariant;
import org.cbioportal.cmo.pipelines.cvr.model.staging.CVRSvRecord;
import org.cbioportal.cmo.pipelines.cvr.sv.SvException;

public class SvUtilities {

    Logger log = Logger.getLogger(SvUtilities.class);

    public SvUtilities() {}

    /* when processing new sv events fetched from cvr, blank out site2_hugo_symbol if site1_hugo_symbol matches 
     *
     * CVR fetches do not populate site1EntrezGeneId or site2EntrezGeneId, so only site1HugoSymbol and site2HugoSymbol are examined
     *
    */
    public void simplifyIntergenicEventGeneReferences(CVRSvRecord svRecord) throws SvException {
        String site1HugoSymbol = svRecord.getSite1_Hugo_Symbol();
        String site2HugoSymbol = svRecord.getSite2_Hugo_Symbol();
        String sampleId = svRecord.getSample_ID();
        if (Strings.isNullOrEmpty(site1HugoSymbol) && Strings.isNullOrEmpty(site2HugoSymbol)) { 
            // illegal case ... every sv record must have at least 1 gene specified
            String msg = String.format("attempting to standardize an event from sv-variants for sample %s where both site1HugoSymbol and site2HugoSymbol are null/empty", sampleId == null ? "" : sampleId);
            log.warn(msg);
            throw new SvException(msg);
        }
        if (site1HugoSymbol != null && site2HugoSymbol != null && site1HugoSymbol.trim().equals(site2HugoSymbol.trim())) {
            // blank out site2 gene when it is a duplicate
            svRecord.setSite2_Hugo_Symbol("");
        }
    }

    public boolean eventInfoIsEmpty(CVRSvRecord svRecord) {
        String eventInfo = svRecord.getEvent_Info();
        if (Strings.isNullOrEmpty(eventInfo)) {
            return true;
        }
        if (eventInfo.trim().equals("-")) {
            return true;
        }
        return false;
    }

    public void populateEventInfo(CVRSvRecord svRecord) throws SvException {
        String site1HugoSymbol = svRecord.getSite1_Hugo_Symbol();
        String site2HugoSymbol = svRecord.getSite2_Hugo_Symbol();
        String sampleId = svRecord.getSample_ID();
        if (Strings.isNullOrEmpty(site1HugoSymbol) && Strings.isNullOrEmpty(site2HugoSymbol)) { 
            // illegal case ... every sv record must have at least 1 gene specified
            String msg = String.format("attempting to populate an empty Event_Info field for an event from sv-variants for sample %s where both site1HugoSymbol and site2HugoSymbol are null/empty", sampleId == null ? "" : sampleId);
            log.warn(msg);
            throw new SvException(msg);
        }
        if (Strings.isNullOrEmpty(site1HugoSymbol) || Strings.isNullOrEmpty(site2HugoSymbol) || site1HugoSymbol.trim().equals(site2HugoSymbol.trim())) {
            // intergenic case
            String hugoSymbol = null;
            if (Strings.isNullOrEmpty(site1HugoSymbol)) {
                hugoSymbol = site2HugoSymbol.trim();
            } else {
                hugoSymbol = site1HugoSymbol.trim();
            }
            svRecord.setEvent_Info(String.format("%s-intragenic", hugoSymbol));
        } else {
            svRecord.setEvent_Info(String.format("%s-%s Fusion", site1HugoSymbol.trim(), site2HugoSymbol.trim()));
        }
    }

    public void populateEventInfoWhenEmpty(CVRSvRecord svRecord) throws SvException {
        if (eventInfoIsEmpty(svRecord)) {
            populateEventInfo(svRecord);
        }
    }

    public void populateEventInfoFromGmlCnvIntragenicVariant(CVRSvRecord svRecord) throws SvException {
        String sampleId = svRecord.getSample_ID();
        String site1HugoSymbol = svRecord.getSite1_Hugo_Symbol();
        if (Strings.isNullOrEmpty(site1HugoSymbol)) {
            String msg = String.format("attempting to populate the Event_Info field for an event from cnv-intragenic-variants-gml for sample %s where site1HugoSymbol is null/empty", sampleId == null ? "" : sampleId);
            throw new SvException(msg);
        }
        String cnvClassName = svRecord.getSV_Class();
        String shortClassName = "";
        if (!Strings.isNullOrEmpty(cnvClassName)) {
            shortClassName = String.format(" %s", cnvClassName.replace("INTRAGENIC_", "").toLowerCase());
        }
        svRecord.setEvent_Info(String.format("%s-intragenic%s", site1HugoSymbol, shortClassName));
    }

    public CVRSvRecord makeCvrSvRecordFromCvrSvVariant(CVRSvVariant variant, String sampleId) {
        CVRSvRecord cvrSvRecord = new CVRSvRecord();
        cvrSvRecord.setSample_ID(sampleId);
        cvrSvRecord.setAnnotation(variant.getAnnotation());
        cvrSvRecord.setBreakpoint_Type(variant.getBreakpoint_Type());
        cvrSvRecord.setComments(variant.getComments());
        // CVR confirmed Site1_GENE/Gene1 is a NOT_NULL field
        // Use Gene1 to test whether v1 or v2 schema
        if (variant.getSite1_Gene() != null && !variant.getSite1_Gene().isEmpty()) {
            cvrSvRecord.setSite1_Hugo_Symbol(variant.getSite1_Gene());
            cvrSvRecord.setSite2_Hugo_Symbol(variant.getSite2_Gene());
            cvrSvRecord.setSite1_Region_Number(variant.getSite1_Exon());
            cvrSvRecord.setSite2_Region_Number(variant.getSite2_Exon());
        } else {
            cvrSvRecord.setSite1_Hugo_Symbol(variant.getGene1());
            cvrSvRecord.setSite2_Hugo_Symbol(variant.getGene2());
            cvrSvRecord.setSite1_Region_Number(variant.getExon1());
            cvrSvRecord.setSite2_Region_Number(variant.getExon2());
        }
        cvrSvRecord.setConnection_Type(variant.getConnection_Type());
        cvrSvRecord.setEvent_Info(variant.getEvent_Info());
        cvrSvRecord.setNCBI_Build("GRCh37"); // default, not provided by CVR
        cvrSvRecord.setNormal_Read_Count(variant.getNormal_Read_Count());
        cvrSvRecord.setNormal_Variant_Count(variant.getNormal_Variant_Count());
        cvrSvRecord.setSite1_Chromosome(variant.getSite1_Chrom());
        cvrSvRecord.setSite1_Description(variant.getSite1_Desc());
        cvrSvRecord.setSite1_Position(variant.getSite1_Pos());
        cvrSvRecord.setSite2_Chromosome(variant.getSite2_Chrom());
        cvrSvRecord.setSite2_Description(variant.getSite2_Desc());
        cvrSvRecord.setSite2_Position(variant.getSite2_Pos());
        cvrSvRecord.setSV_Class(variant.getSv_Class_Name());
        cvrSvRecord.setSV_Length(variant.getSv_Length());
        cvrSvRecord.setTumor_Read_Count(variant.getTumor_Read_Count());
        cvrSvRecord.setTumor_Variant_Count(variant.getTumor_Variant_Count());
        cvrSvRecord.setSV_Status("SOMATIC"); // default, not provided by CVR
        try {
            simplifyIntergenicEventGeneReferences(cvrSvRecord);
            populateEventInfoWhenEmpty(cvrSvRecord);
        } catch (SvException e) {
            log.warn(String.format("invalid sv-variants record for sample '%s' will be output to data_sv.txt without filling in Event_Info", sampleId));
        }
        return cvrSvRecord;
    }

    public CVRSvRecord makeCvrSvRecordFromGmlCnvIntragenicVariant(GMLCnvIntragenicVariant variant, String sampleId) {
        CVRSvRecord cvrSvRecord = new CVRSvRecord();
        cvrSvRecord.setSample_ID(sampleId);
        if (Strings.isNullOrEmpty(variant.getInterpretation())) {
            cvrSvRecord.setComments("");
        } else {
            cvrSvRecord.setComments(variant.getInterpretation().replaceAll("[\\t\\n\\r]+"," "));
        }
        cvrSvRecord.setSite1_Hugo_Symbol(variant.getGeneId().trim());
        cvrSvRecord.setSite1_Chromosome(variant.getChromosome());
        cvrSvRecord.setSV_Status("GERMLINE");
        cvrSvRecord.setSV_Class(variant.getCnvClassName());
        try {
            populateEventInfoFromGmlCnvIntragenicVariant(cvrSvRecord);
        } catch (SvException e) {
            log.warn(String.format("invalid cnv-intragenic-variant-gml record for sample '%s' will be output to data_sv.txt without filling in Event_Info", sampleId));
        }
        return cvrSvRecord;
    }

}
