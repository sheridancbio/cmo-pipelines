/*
 * Copyright (c) 2016-2017 Memorial Sloan-Kettering Cancer Center.
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

/**
 *
 * @author heinsz
 */
public class MskimpactPathology {
    private String ptIdPathology;
    private String dmpIdPathology;
    private String rptIdPathology;
    private String ageAtPathologyReportInDays;
    private String pathologyReportProcedureYear;
    private String pathologyReportType;
    private String pathologyReportYear;
    private String ageAtPathologyReportDateInDays;
    private String pathologyReportText;

    public MskimpactPathology() {}

    public String getPT_ID_PATHOLOGY() {
        return ptIdPathology;
    }

    public void setPT_ID_PATHOLOGY(String ptIdPathology) {
        this.ptIdPathology = ptIdPathology;
    }

    public String getDMP_ID_PATHOLOGY() {
        return dmpIdPathology;
    }

    public void setDMP_ID_PATHOLOGY(String dmpIdPathology) {
        this.dmpIdPathology = dmpIdPathology;
    }

    public String getRPT_ID_PATHOLOGY() {
        return rptIdPathology;
    }

    public void setRPT_ID_PATHOLOGY(String rptIdPathology) {
        this.rptIdPathology = rptIdPathology;
    }

    public String getAGE_AT_PATHOLOGY_REPORT_PROCEDURE_DATE_IN_DAYS() {
        return ageAtPathologyReportInDays;
    }

    public void setAGE_AT_PATHOLOGY_REPORT_PROCEDURE_DATE_IN_DAYS(String ageAtPathologyReportInDays) {
        this.ageAtPathologyReportInDays = ageAtPathologyReportInDays;
    }
    
    public String getPATHOLOGY_REPORT_PROCEDURE_YEAR() {
        return pathologyReportProcedureYear;
    }

    public void setPATHOLOGY_REPORT_PROCEDURE_YEAR(String pathologyReportProcedureYear) {
        this.pathologyReportProcedureYear = pathologyReportProcedureYear;
    }
    
    public String getPATHOLOGY_REPORT_TYPE() {
        return pathologyReportType;
    }

    public void setPATHOLOGY_REPORT_TYPE(String pathologyReportType) {
        this.pathologyReportType = pathologyReportType;
    }
    
    public String getPATHOLOGY_REPORT_YEAR() {
        return pathologyReportYear;
    }

    public void setPATHOLOGY_REPORT_YEAR(String pathologyReportYear) {
        this.pathologyReportYear = pathologyReportYear;
    }

    public String getAGE_AT_PATHOLOGY_REPORT_DATE_IN_DAYS() {
        return ageAtPathologyReportDateInDays;
    }

    public void setAGE_AT_PATHOLOGY_REPORT_DATE_IN_DAYS(String ageAtPathologyReportDateInDays) {
        this.ageAtPathologyReportDateInDays = ageAtPathologyReportDateInDays;
    }

    public String getPATHOLOGY_REPORT_TEXT() {
        return pathologyReportText;
    }

    public void setPATHOLOGY_REPORT_TEXT(String pathologyReportText) {
        this.pathologyReportText = pathologyReportText;
    }    
}
