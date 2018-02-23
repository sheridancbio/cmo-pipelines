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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.mskcc.cmo.ks.darwin.pipeline.model;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;

public class Skcm_mskcc_2015_chantNormalizedClinicalRecord {
    private String patientId;
    private String sampleId;
    private String stageYear;
    private String stgGrpName;
    private String vitalStatus;
    private String stsSrcDesc;
    private String lastStatus;
    private String dermagrphxDes;
    private String presStgYear;
    private String familyHistory;
    private String timeToFirstRecurrence;
    private String localDesc;
    private String nodalDesc;
    private String intransitDesc;
    private String sysDesc;
    private String recurNdszDes;
    private String recurNodalNo;
    private String ldh;
    private String ldhYear;
    private String metastasis;
    private String adjvntTx;
    private String systemicTreatment;
    private String treatmentRadiation;
    private String surgery;
    private String tissueBankAvail;
    private String primSeq;
    private String yearOfDiagnosis;
    private String mskReviewDes;
    private String tumorThicknessMeasurement;
    private String clarkLevelAtDiagnosis;
    private String primaryMelanomaTumorUlceration;
    private String tumorTissueSite;
    private String detailedPrimarySite;
    private String lymphocyteInfiltration;
    private String regressionDes;
    private String marginStatus;
    private String mitoticIndex;
    private String histologicalType;
    private String satellitesDes;
    private String extSlidesDes;
    private String primaryLymphNodePresentationAssessment;
    private String lnclinStsDes;
    private String lnsentinbxDes;
    private String lnsentinbxYea;
    private String lnprolysctDes;
    private String lnprosuccDesc;
    private String lndsctCmpDes;
    private String lndsctYear;
    private String lnmattedDesc;
    private String lnextnodstDes;
    private String lnintrmetsDes;
    private String lnsize;
    private String lnsizeUnkDes;
    private String lnslnlargSize;
    private String lnihcDesc;
    private String s100Stain;
    private String lnimmhmb45Des;
    private String lnimmMelaDes;
    private String reportYear;
    private String procedureYear;
    private String tumorType;
    private String primarySite;
    private String metastaticSite;
    private String initialMetDisease;
    private String otherSitesOfMets;
    private String yearMetDiseaseIdentified;
    
    private Map<String, Object> additionalProperties;    
    
    public Skcm_mskcc_2015_chantNormalizedClinicalRecord() {
    }
    
    public Skcm_mskcc_2015_chantNormalizedClinicalRecord(
             String patientId,
             String sampleId,
             String stageYear,
             String stgGrpName,
             String vitalStatus,
             String stsSrcDesc,
             String lastStatus,
             String dermagrphxDes,
             String presStgYear,
             String familyHistory,
             String timeToFirstRecurrence,
             String localDesc,
             String nodalDesc,
             String intransitDesc,
             String sysDesc,
             String recurNdszDes,
             String recurNodalNo,
             String ldh,
             String ldhYear,
             String metastasis,
             String adjvntTx,
             String systemicTreatment,
             String treatmentRadiation,
             String surgery,
             String tissueBankAvail,
             String primSeq,
             String yearOfDiagnosis,
             String mskReviewDes,
             String tumorThicknessMeasurement,
             String clarkLevelAtDiagnosis,
             String primaryMelanomaTumorUlceration,
             String tumorTissueSite,
             String detailedPrimarySite,
             String lymphocyteInfiltration,
             String regressionDes,
             String marginStatus,
             String mitoticIndex,
             String histologicalType,
             String satellitesDes,
             String extSlidesDes,
             String primaryLymphNodePresentationAssessment,
             String lnclinStsDes,
             String lnsentinbxDes,
             String lnsentinbxYea,
             String lnprolysctDes,
             String lnprosuccDesc,
             String lndsctCmpDes,
             String lndsctYear,
             String lnmattedDesc,
             String lnextnodstDes,
             String lnintrmetsDes,
             String lnsize,
             String lnsizeUnkDes,
             String lnslnlargSize,
             String lnihcDesc,
             String s100Stain,
             String lnimmhmb45Des,
             String lnimmMelaDes,
             String reportYear,
             String procedureYear,
             String tumorType,
             String primarySite,
             String metastaticSite,
             String initialMetDisease,
             String otherSitesOfMets,
             String yearMetDiseaseIdentified) {
        this.patientId = StringUtils.isNotEmpty(patientId) ? patientId : "NA";
        this.sampleId = StringUtils.isNotEmpty(sampleId) ? sampleId : "NA";
        this.stageYear = StringUtils.isNotEmpty(stageYear) ? stageYear : "NA";
        this.stgGrpName = StringUtils.isNotEmpty(stgGrpName) ? stgGrpName : "NA";
        this.vitalStatus = StringUtils.isNotEmpty(vitalStatus) ? vitalStatus : "NA";
        this.stsSrcDesc = StringUtils.isNotEmpty(stsSrcDesc) ? stsSrcDesc : "NA";
        this.lastStatus = StringUtils.isNotEmpty(lastStatus) ? lastStatus : "NA";
        this.dermagrphxDes = StringUtils.isNotEmpty(dermagrphxDes) ? dermagrphxDes : "NA";
        this.presStgYear = StringUtils.isNotEmpty(presStgYear) ? presStgYear : "NA";
        this.familyHistory = StringUtils.isNotEmpty(familyHistory) ? familyHistory : "NA";
        this.timeToFirstRecurrence = StringUtils.isNotEmpty(timeToFirstRecurrence) ? timeToFirstRecurrence : "NA";
        this.localDesc = StringUtils.isNotEmpty(localDesc) ? localDesc : "NA";
        this.nodalDesc = StringUtils.isNotEmpty(nodalDesc) ? nodalDesc : "NA";
        this.intransitDesc = StringUtils.isNotEmpty(intransitDesc) ? intransitDesc : "NA";
        this.sysDesc = StringUtils.isNotEmpty(sysDesc) ? sysDesc : "NA";
        this.recurNdszDes = StringUtils.isNotEmpty(recurNdszDes) ? recurNdszDes : "NA";
        this.recurNodalNo = StringUtils.isNotEmpty(recurNodalNo) ? recurNodalNo : "NA";
        this.ldh = StringUtils.isNotEmpty(ldh) ? ldh : "NA";
        this.ldhYear = StringUtils.isNotEmpty(ldhYear) ? ldhYear : "NA";
        this.metastasis = StringUtils.isNotEmpty(metastasis) ? metastasis : "NA";
        this.adjvntTx = StringUtils.isNotEmpty(adjvntTx) ? adjvntTx : "NA";
        this.systemicTreatment = StringUtils.isNotEmpty(systemicTreatment) ? systemicTreatment : "NA";
        this.treatmentRadiation = StringUtils.isNotEmpty(treatmentRadiation) ? treatmentRadiation : "NA";
        this.surgery = StringUtils.isNotEmpty(surgery) ? surgery : "NA";
        this.tissueBankAvail = StringUtils.isNotEmpty(tissueBankAvail) ? tissueBankAvail : "NA";
        this.primSeq = StringUtils.isNotEmpty(primSeq) ? primSeq : "NA";
        this.yearOfDiagnosis = StringUtils.isNotEmpty(yearOfDiagnosis) ? yearOfDiagnosis : "NA";
        this.mskReviewDes = StringUtils.isNotEmpty(mskReviewDes) ? mskReviewDes : "NA";
        this.tumorThicknessMeasurement = StringUtils.isNotEmpty(tumorThicknessMeasurement) ? tumorThicknessMeasurement : "NA";
        this.clarkLevelAtDiagnosis = StringUtils.isNotEmpty(clarkLevelAtDiagnosis) ? clarkLevelAtDiagnosis : "NA";
        this.primaryMelanomaTumorUlceration = StringUtils.isNotEmpty(primaryMelanomaTumorUlceration) ? primaryMelanomaTumorUlceration : "NA";
        this.tumorTissueSite = StringUtils.isNotEmpty(tumorTissueSite) ? tumorTissueSite : "NA";
        this.detailedPrimarySite = StringUtils.isNotEmpty(detailedPrimarySite) ? detailedPrimarySite : "NA";
        this.lymphocyteInfiltration = StringUtils.isNotEmpty(lymphocyteInfiltration) ? lymphocyteInfiltration : "NA";
        this.regressionDes = StringUtils.isNotEmpty(regressionDes) ? regressionDes : "NA";
        this.marginStatus = StringUtils.isNotEmpty(marginStatus) ? marginStatus : "NA";
        this.mitoticIndex = StringUtils.isNotEmpty(mitoticIndex) ? mitoticIndex : "NA";
        this.histologicalType = StringUtils.isNotEmpty(histologicalType) ? histologicalType : "NA";
        this.satellitesDes = StringUtils.isNotEmpty(satellitesDes) ? satellitesDes : "NA";
        this.extSlidesDes = StringUtils.isNotEmpty(extSlidesDes) ? extSlidesDes : "NA";
        this.primaryLymphNodePresentationAssessment = StringUtils.isNotEmpty(primaryLymphNodePresentationAssessment) ? primaryLymphNodePresentationAssessment : "NA";
        this.lnclinStsDes = StringUtils.isNotEmpty(lnclinStsDes) ? lnclinStsDes : "NA";
        this.lnsentinbxDes = StringUtils.isNotEmpty(lnsentinbxDes) ? lnsentinbxDes : "NA";
        this.lnsentinbxYea = StringUtils.isNotEmpty(lnsentinbxYea) ? lnsentinbxYea : "NA";
        this.lnprolysctDes = StringUtils.isNotEmpty(lnprolysctDes) ? lnprolysctDes : "NA";
        this.lnprosuccDesc = StringUtils.isNotEmpty(lnprosuccDesc) ? lnprosuccDesc : "NA";
        this.lndsctCmpDes = StringUtils.isNotEmpty(lndsctCmpDes) ? lndsctCmpDes : "NA";
        this.lndsctYear = StringUtils.isNotEmpty(lndsctYear) ? lndsctYear : "NA";
        this.lnmattedDesc = StringUtils.isNotEmpty(lnmattedDesc) ? lnmattedDesc : "NA";
        this.lnextnodstDes = StringUtils.isNotEmpty(lnextnodstDes) ? lnextnodstDes : "NA";
        this.lnintrmetsDes = StringUtils.isNotEmpty(lnintrmetsDes) ? lnintrmetsDes : "NA";
        this.lnsize = StringUtils.isNotEmpty(lnsize) ? lnsize : "NA";
        this.lnsizeUnkDes = StringUtils.isNotEmpty(lnsizeUnkDes) ? lnsizeUnkDes : "NA";
        this.lnslnlargSize = StringUtils.isNotEmpty(lnslnlargSize) ? lnslnlargSize : "NA";
        this.lnihcDesc = StringUtils.isNotEmpty(lnihcDesc) ? lnihcDesc : "NA";
        this.s100Stain = StringUtils.isNotEmpty(s100Stain) ? s100Stain : "NA";
        this.lnimmhmb45Des = StringUtils.isNotEmpty(lnimmhmb45Des) ? lnimmhmb45Des : "NA";
        this.lnimmMelaDes = StringUtils.isNotEmpty(lnimmMelaDes) ? lnimmMelaDes : "NA";
        this.reportYear = StringUtils.isNotEmpty(reportYear) ? reportYear : "NA";
        this.procedureYear = StringUtils.isNotEmpty(procedureYear) ? procedureYear : "NA";
        this.tumorType = StringUtils.isNotEmpty(tumorType) ? tumorType : "NA";
        this.primarySite = StringUtils.isNotEmpty(primarySite) ? primarySite : "NA";
        this.metastaticSite = StringUtils.isNotEmpty(metastaticSite) ? metastaticSite : "NA";
        this.initialMetDisease = StringUtils.isNotEmpty(initialMetDisease) ? initialMetDisease : "NA";
        this.otherSitesOfMets = StringUtils.isNotEmpty(otherSitesOfMets) ? otherSitesOfMets : "NA";
        this.yearMetDiseaseIdentified = StringUtils.isNotEmpty(yearMetDiseaseIdentified) ? yearMetDiseaseIdentified : "NA";
    }
    
    public String getPATIENT_ID() {
        return patientId;
    }
    
    public void setPATIENT_ID(String patientId) {
        this.patientId = patientId;
    }
    
    public String getSAMPLE_ID() {
        return sampleId;
    }
    
    public void setSAMPLE_ID(String sampleId) {
        this.sampleId = sampleId;
    }
    
    public String getSTAGE_YEAR() {
        return this.stageYear;
    }

    public void setSTAGE_YEAR(String stageYear) {
        this.stageYear = stageYear;
    }

    public String getSTG_GRP_NAME() {
        return this.stgGrpName;
    }

    public void setSTG_GRP_NAME(String stgGrpName) {
        this.stgGrpName = stgGrpName;
    }

    public String getVITAL_STATUS() {
        return this.vitalStatus;
    }

    public void setVITAL_STATUS(String vitalStatus) {
        this.vitalStatus = vitalStatus;
    }

    public String getSTS_SRCE_DESC() {
        return this.stsSrcDesc;
    }

    public void setSTS_SRCE_DESC(String stsSrcDesc) {
        this.stsSrcDesc = stsSrcDesc;
    }

    public String getLAST_STATUS() {
        return this.lastStatus;
    }

    public void setLAST_STATUS(String lastStatus) {
        this.lastStatus = lastStatus;
    }

    public String getDERMAGRPHX_DES() {
        return this.dermagrphxDes;
    }

    public void setDERMAGRPHX_DES(String dermagrphxDes) {
        this.dermagrphxDes = dermagrphxDes;
    }

    public String getPRES_STG_YEAR() {
        return this.presStgYear;
    }

    public void setPRES_STG_YEAR(String presStgYear) {
        this.presStgYear = presStgYear;
    }

    public String getFAMILY_HISTORY() {
        return this.familyHistory;
    }

    public void setFAMILY_HISTORY(String familyHistory) {
        this.familyHistory = familyHistory;
    }

    public String getTIME_TO_FIRST_RECURRENCE() {
        return this.timeToFirstRecurrence;
    }

    public void setTIME_TO_FIRST_RECURRENCE(String timeToFirstRecurrence) {
        this.timeToFirstRecurrence = timeToFirstRecurrence;
    }

    public String getLOCAL_DESC() {
        return this.localDesc;
    }

    public void setLOCAL_DESC(String localDesc) {
        this.localDesc = localDesc;
    }

    public String getNODAL_DESC() {
        return this.nodalDesc;
    }

    public void setNODAL_DESC(String nodalDesc) {
        this.nodalDesc = nodalDesc;
    }

    public String getINTRANSIT_DESC() {
        return this.intransitDesc;
    }

    public void setINTRANSIT_DESC(String intransitDesc) {
        this.intransitDesc = intransitDesc;
    }

    public String getSYS_DESC() {
        return this.sysDesc;
    }

    public void setSYS_DESC(String sysDesc) {
        this.sysDesc = sysDesc;
    }

    public String getRECUR_NDSZ_DES() {
        return this.recurNdszDes;
    }

    public void setRECUR_NDSZ_DES(String recurNdszDes) {
        this.recurNdszDes = recurNdszDes;
    }

    public String getRECUR_NODAL_NO() {
        return this.recurNodalNo;
    }

    public void setRECUR_NODAL_NO(String recurNodalNo) {
        this.recurNodalNo = recurNodalNo;
    }

    public String getLDH_LEVEL() {
        return this.ldh;
    }

    public void setLDH_LEVEL(String ldh) {
        this.ldh = ldh;
    }

    public String getLDH_YEAR() {
        return this.ldhYear;
    }

    public void setLDH_YEAR(String ldhYear) {
        this.ldhYear = ldhYear;
    }

    public String getMETASTASIS() {
        return this.metastasis;
    }

    public void setMETASTASIS(String metastasis) {
        this.metastasis = metastasis;
    }

    public String getADJUVANT_TX() {
        return this.adjvntTx;
    }

    public void setADJUVANT_TX(String adjvntTx) {
        this.adjvntTx = adjvntTx;
    }

    public String getSYSTEMIC_TREATMENT() {
        return this.systemicTreatment;
    }

    public void setSYSTEMIC_TREATMENT(String systemicTreatment) {
        this.systemicTreatment = systemicTreatment;
    }

    public String getTREATMENT_RADIATION() {
        return this.treatmentRadiation;
    }

    public void setTREATMENT_RADIATION(String treatmentRadiation) {
        this.treatmentRadiation = treatmentRadiation;
    }

    public String getSURGERY() {
        return this.surgery;
    }

    public void setSURGERY(String surgery) {
        this.surgery = surgery;
    }

    public String getTISSUE_BANK_AVAIL() {
        return this.tissueBankAvail;
    }

    public void setTISSUE_BANK_AVAIL(String tissueBankAvail) {
        this.tissueBankAvail = tissueBankAvail;
    }

    public String getPRIM_SEQ() {
        return this.primSeq;
    }

    public void setPRIM_SEQ(String primSeq) {
        this.primSeq = primSeq;
    }

    public String getYEAR_OF_DIAGNOSIS() {
        return this.yearOfDiagnosis;
    }

    public void setYEAR_OF_DIAGNOSIS(String yearOfDiagnosis) {
        this.yearOfDiagnosis = yearOfDiagnosis;
    }

    public String getMSK_REVIEW_DES() {
        return this.mskReviewDes;
    }

    public void setMSK_REVIEW_DES(String mskReviewDes) {
        this.mskReviewDes = mskReviewDes;
    }

    public String getTUMOR_THICKNESS_MEASUREMENT() {
        return this.tumorThicknessMeasurement;
    }

    public void setTUMOR_THICKNESS_MEASUREMENT(String tumorThicknessMeasurement) {
        this.tumorThicknessMeasurement = tumorThicknessMeasurement;
    }

    public String getCLARK_LEVEL_AT_DIAGNOSIS() {
        return this.clarkLevelAtDiagnosis;
    }

    public void setCLARK_LEVEL_AT_DIAGNOSIS(String clarkLevelAtDiagnosis) {
        this.clarkLevelAtDiagnosis = clarkLevelAtDiagnosis;
    }

    public String getPRIMARY_MELANOMA_TUMOR_ULCERATION() {
        return this.primaryMelanomaTumorUlceration;
    }

    public void setPRIMARY_MELANOMA_TUMOR_ULCERATION(String primaryMelanomaTumorUlceration) {
        this.primaryMelanomaTumorUlceration = primaryMelanomaTumorUlceration;
    }

    public String getTUMOR_TISSUE_SITE() {
        return this.tumorTissueSite;
    }

    public void setTUMOR_TISSUE_SITE(String tumorTissueSite) {
        this.tumorTissueSite = tumorTissueSite;
    }

    public String getDETAILED_PRIMARY_SITE() {
        return this.detailedPrimarySite;
    }

    public void setDETAILED_PRIMARY_SITE(String detailedPrimarySite) {
        this.detailedPrimarySite = detailedPrimarySite;
    }

    public String getLYMPHOCYTE_INFILTRATION() {
        return this.lymphocyteInfiltration;
    }

    public void setLYMPHOCYTE_INFILTRATION(String lymphocyteInfiltration) {
        this.lymphocyteInfiltration = lymphocyteInfiltration;
    }

    public String getREGRESSION_DES() {
        return this.regressionDes;
    }

    public void setREGRESSION_DES(String regressionDes) {
        this.regressionDes = regressionDes;
    }

    public String getMARGIN_STATUS() {
        return this.marginStatus;
    }

    public void setMARGIN_STATUS(String marginStatus) {
        this.marginStatus = marginStatus;
    }

    public String getMITOTIC_INDEX() {
        return this.mitoticIndex;
    }

    public void setMITOTIC_INDEX(String mitoticIndex) {
        this.mitoticIndex = mitoticIndex;
    }

    public String getHISTOLOGICAL_TYPE() {
        return this.histologicalType;
    }

    public void setHISTOLOGICAL_TYPE(String histologicalType) {
        this.histologicalType = histologicalType;
    }

    public String getSATELLITES_DES() {
        return this.satellitesDes;
    }

    public void setSATELLITES_DES(String satellitesDes) {
        this.satellitesDes = satellitesDes;
    }

    public String getEXT_SLIDES_DES() {
        return this.extSlidesDes;
    }

    public void setEXT_SLIDES_DES(String extSlidesDes) {
        this.extSlidesDes = extSlidesDes;
    }

    public String getPRIMARY_LYMPH_NODE_PRESENTATION_ASSESSMENT() {
        return this.primaryLymphNodePresentationAssessment;
    }

    public void setPRIMARY_LYMPH_NODE_PRESENTATION_ASSESSMENT(String primaryLymphNodePresentationAssessment) {
        this.primaryLymphNodePresentationAssessment = primaryLymphNodePresentationAssessment;
    }

    public String getLNCLIN_STS_DES() {
        return this.lnclinStsDes;
    }

    public void setLNCLIN_STS_DES(String lnclinStsDes) {
        this.lnclinStsDes = lnclinStsDes;
    }

    public String getLNSENTINBX_DES() {
        return this.lnsentinbxDes;
    }

    public void setLNSENTINBX_DES(String lnsentinbxDes) {
        this.lnsentinbxDes = lnsentinbxDes;
    }

    public String getLNSENTINBX_YEA() {
        return this.lnsentinbxYea;
    }

    public void setLNSENTINBX_YEA(String lnsentinbxYea) {
        this.lnsentinbxYea = lnsentinbxYea;
    }

    public String getLNPROLYSCT_DES() {
        return this.lnprolysctDes;
    }

    public void setLNPROLYSCT_DES(String lnprolysctDes) {
        this.lnprolysctDes = lnprolysctDes;
    }

    public String getLNPROSUCC_DESC() {
        return this.lnprosuccDesc;
    }

    public void setLNPROSUCC_DESC(String lnprosuccDesc) {
        this.lnprosuccDesc = lnprosuccDesc;
    }

    public String getLNDSCT_CMP_DES() {
        return this.lndsctCmpDes;
    }

    public void setLNDSCT_CMP_DES(String lndsctCmpDes) {
        this.lndsctCmpDes = lndsctCmpDes;
    }

    public String getLNDSCT_YEAR() {
        return this.lndsctYear;
    }

    public void setLNDSCT_YEAR(String lndsctYear) {
        this.lndsctYear = lndsctYear;
    }

    public String getLNMATTED_DESC() {
        return this.lnmattedDesc;
    }

    public void setLNMATTED_DESC(String lnmattedDesc) {
        this.lnmattedDesc = lnmattedDesc;
    }

    public String getLNEXTNODST_DES() {
        return this.lnextnodstDes;
    }

    public void setLNEXTNODST_DES(String lnextnodstDes) {
        this.lnextnodstDes = lnextnodstDes;
    }

    public String getLNINTRMETS_DES() {
        return this.lnintrmetsDes;
    }

    public void setLNINTRMETS_DES(String lnintrmetsDes) {
        this.lnintrmetsDes = lnintrmetsDes;
    }

    public String getLNSIZE() {
        return this.lnsize;
    }

    public void setLNSIZE(String lnsize) {
        this.lnsize = lnsize;
    }

    public String getLNSIZE_UNK_DES() {
        return this.lnsizeUnkDes;
    }

    public void setLNSIZE_UNK_DES(String lnsizeUnkDes) {
        this.lnsizeUnkDes = lnsizeUnkDes;
    }

    public String getLNSLNLARG_SIZE() {
        return this.lnslnlargSize;
    }

    public void setLNSLNLARG_SIZE(String lnslnlargSize) {
        this.lnslnlargSize = lnslnlargSize;
    }

    public String getLNIHC_DESC() {
        return this.lnihcDesc;
    }

    public void setLNIHC_DESC(String lnihcDesc) {
        this.lnihcDesc = lnihcDesc;
    }

    public String getS_100_STAIN() {
        return this.s100Stain;
    }

    public void setS_100_STAIN(String s100Stain) {
        this.s100Stain = s100Stain;
    }

    public String getLNIMMHMB45_DES() {
        return this.lnimmhmb45Des;
    }

    public void setLNIMMHMB45_DES(String lnimmhmb45Des) {
        this.lnimmhmb45Des = lnimmhmb45Des;
    }

    public String getLNIMM_MELA_DES() {
        return this.lnimmMelaDes;
    }

    public void setLNIMM_MELA_DES(String lnimmMelaDes) {
        this.lnimmMelaDes = lnimmMelaDes;
    }

    public String getREPORT_YEAR() {
        return this.reportYear;
    }

    public void setREPORT_YEAR(String reportYear) {
        this.reportYear = reportYear;
    }

    public String getPROCEDURE_YEAR() {
        return this.procedureYear;
    }

    public void setPROCEDURE_YEAR(String procedureYear) {
        this.procedureYear = procedureYear;
    }

    public String getTUMOR_TYPE() {
        return this.tumorType;
    }

    public void setTUMOR_TYPE(String tumorType) {
        this.tumorType = tumorType;
    }

    public String getPRIMARY_SITE() {
        return this.primarySite;
    }

    public void setPRIMARY_SITE(String primarySite) {
        this.primarySite = primarySite;
    }

    public String getMETASTATIC_SITE() {
        return this.metastaticSite;
    }

    public void setMETASTATIC_SITE(String metastaticSite) {
        this.metastaticSite = metastaticSite;
    }

    public String getINITIAL_MET_DISEASE() {
        return this.initialMetDisease;
    }

    public void setINITIAL_MET_DISEASE(String initialMetDisease) {
        this.initialMetDisease = initialMetDisease;
    }

    public String getOTHER_SITES_OF_METS() {
        return this.otherSitesOfMets;
    }

    public void setOTHER_SITES_OF_METS(String otherSitesOfMets) {
        this.otherSitesOfMets = otherSitesOfMets;
    }

    public String getYEAR_MET_DISEASE_IDENTIFIED() {
        return this.yearMetDiseaseIdentified;
    }

    public void setYEAR_MET_DISEASE_IDENTIFIED(String yearMetDiseaseIdentified) {
        this.yearMetDiseaseIdentified = yearMetDiseaseIdentified;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public List<String> getFieldNames() {
        List<String> fieldNames = new ArrayList<>();
        fieldNames.add("PATIENT_ID");
        fieldNames.add("SAMPLE_ID");
        fieldNames.add("STAGE_YEAR");
        fieldNames.add("STG_GRP_NAME");
        fieldNames.add("VITAL_STATUS");
        fieldNames.add("STS_SRCE_DESC");
        fieldNames.add("LAST_STATUS");
        fieldNames.add("DERMAGRPHX_DES");
        fieldNames.add("PRES_STG_YEAR");
        fieldNames.add("FAMILY_HISTORY");
        fieldNames.add("TIME_TO_FIRST_RECURRENCE");
        fieldNames.add("LOCAL_DESC");
        fieldNames.add("NODAL_DESC");
        fieldNames.add("INTRANSIT_DESC");
        fieldNames.add("SYS_DESC");
        fieldNames.add("RECUR_NDSZ_DES");
        fieldNames.add("RECUR_NODAL_NO");
        fieldNames.add("LDH_LEVEL");
        fieldNames.add("LDH_YEAR");
        fieldNames.add("METASTASIS");
        fieldNames.add("ADJUVANT_TX");
        fieldNames.add("SYSTEMIC_TREATMENT");
        fieldNames.add("TREATMENT_RADIATION");
        fieldNames.add("SURGERY");
        fieldNames.add("TISSUE_BANK_AVAIL");
        fieldNames.add("PRIM_SEQ");
        fieldNames.add("YEAR_OF_DIAGNOSIS");
        fieldNames.add("MSK_REVIEW_DES");
        fieldNames.add("TUMOR_THICKNESS_MEASUREMENT");
        fieldNames.add("CLARK_LEVEL_AT_DIAGNOSIS");
        fieldNames.add("PRIMARY_MELANOMA_TUMOR_ULCERATION");
        fieldNames.add("TUMOR_TISSUE_SITE");
        fieldNames.add("DETAILED_PRIMARY_SITE");
        fieldNames.add("LYMPHOCYTE_INFILTRATION");
        fieldNames.add("REGRESSION_DES");
        fieldNames.add("MARGIN_STATUS");
        fieldNames.add("MITOTIC_INDEX");
        fieldNames.add("HISTOLOGICAL_TYPE");
        fieldNames.add("SATELLITES_DES");
        fieldNames.add("EXT_SLIDES_DES");
        fieldNames.add("PRIMARY_LYMPH_NODE_PRESENTATION_ASSESSMENT");
        fieldNames.add("LNCLIN_STS_DES");
        fieldNames.add("LNSENTINBX_DES");
        fieldNames.add("LNSENTINBX_YEA");
        fieldNames.add("LNPROLYSCT_DES");
        fieldNames.add("LNPROSUCC_DESC");
        fieldNames.add("LNDSCT_CMP_DES");
        fieldNames.add("LNDSCT_YEAR");
        fieldNames.add("LNMATTED_DESC");
        fieldNames.add("LNEXTNODST_DES");
        fieldNames.add("LNINTRMETS_DES");
        fieldNames.add("LNSIZE");
        fieldNames.add("LNSIZE_UNK_DES");
        fieldNames.add("LNSLNLARG_SIZE");
        fieldNames.add("LNIHC_DESC");
        fieldNames.add("S_100_STAIN");
        fieldNames.add("LNIMMHMB45_DES");
        fieldNames.add("LNIMM_MELA_DES");
        fieldNames.add("REPORT_YEAR");
        fieldNames.add("PROCEDURE_YEAR");
        fieldNames.add("TUMOR_TYPE");
        fieldNames.add("PRIMARY_SITE");
        fieldNames.add("METASTATIC_SITE");
        fieldNames.add("INITIAL_MET_DISEASE");
        fieldNames.add("OTHER_SITES_OF_METS");
        fieldNames.add("YEAR_MET_DISEASE_IDENTIFIED");
        return fieldNames;
    }
    
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }    
    
}
