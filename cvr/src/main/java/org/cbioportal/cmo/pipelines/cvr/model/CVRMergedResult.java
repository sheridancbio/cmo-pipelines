/*
 * Copyright (c) 2016, 2017, 2024 Memorial Sloan Kettering Cancer Center.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF MERCHANTABILITY OR FITNESS
 * FOR A PARTICULAR PURPOSE. The software and documentation provided hereunder
 * is on an "as is" basis, and Memorial Sloan Kettering Cancer Center has no
 * obligations to provide maintenance, support, updates, enhancements or
 * modifications. In no event shall Memorial Sloan Kettering Cancer Center be
 * liable to any party for direct, indirect, special, incidental or
 * consequential damages, including lost profits, arising out of the use of this
 * software and its documentation, even if Memorial Sloan Kettering Cancer
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

package org.cbioportal.cmo.pipelines.cvr.model;

/**
 *
 * @author heinsz
 */

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.annotation.Generated;
import java.util.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "cnv-intragenic-variants",
    "cnv-variants",
    "meta-data",
    "seg-data",
    "snp-indel-exonic",
    "snp-indel-exonic-np",
    "snp-indel-silent",
    "snp-indel-silent-np",
    "sv-variants"
})
public class CVRMergedResult {
    @JsonProperty("cnv-intragenic-variants")
    private List<CVRCnvIntragenicVariant> cnvIntragenicVariants;
    @JsonProperty("cnv-variants")
    private List<CVRCnvVariant> cnvVariants;
    @JsonProperty("meta-data")
    private CVRMetaData metaData;
    @JsonProperty("seg-data")
    private CVRSegData segData;
    @JsonProperty("snp-indel-exonic")
    private List<CVRSnp> snpIndelExonic;
    @JsonProperty("snp-indel-exonic-np")
    private List<CVRSnp> snpIndelExonicNp;
    @JsonProperty("snp-indel-silent")
    private List<CVRSnp> snpIndelSilent;
    @JsonProperty("snp-indel-silent-np")
    private List<CVRSnp> snpIndelSilentNp;
    @JsonProperty("sv-variants")
    private List<CVRSvVariant> svVariants;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    @JsonIgnore
    private List<CVRSnp> snps = new ArrayList<>();
    /**
    * No args constructor for use in serialization
    *
    */
    public CVRMergedResult() {
    }

    /**
    *
    *@param cnvIntragenicVariants
    *@param cnvVariants
    *@param metaData
    *@param snpIndelExonic
    *@param snpIndelExonicNp
    *@param snpIndelSilent
    *@param snpIndelSilentNp
    *@param svVariants
    *@param segData
    */
    public CVRMergedResult(List<CVRCnvIntragenicVariant> cnvIntragenicVariants, List<CVRCnvVariant> cnvVariants,
            CVRMetaData metaData, CVRSegData segData, List<CVRSnp> snpIndelExonic,
            List<CVRSnp> snpIndelExonicNp, List<CVRSnp> snpIndelSilent,
            List<CVRSnp> snpIndelSilentNp, List<CVRSvVariant> svVariants) {
        this.cnvIntragenicVariants = cnvIntragenicVariants;
        this.cnvVariants = cnvVariants;
        this.metaData = metaData;
        this.segData = segData;
        this.snpIndelExonic = snpIndelExonic;
        this.snpIndelExonicNp = snpIndelExonicNp;
        this.snpIndelSilent = snpIndelSilent;
        this.snpIndelSilentNp = snpIndelSilentNp;
        this.svVariants = svVariants;
        this.snps.clear();
    }

    public CVRMergedResult(CVRResult result, CVRSegData segData) {
        this.cnvIntragenicVariants = result.getCnvIntragenicVariants();
        this.cnvVariants = result.getCnvVariants();
        this.metaData = result.getMetaData();
        this.segData = segData;
        this.snpIndelExonic = result.getSnpIndelExonic();
        this.snpIndelExonicNp = result.getSnpIndelExonicNp();
        this.snpIndelSilent = result.getSnpIndelSilent();
        this.snpIndelSilentNp = result.getSnpIndelSilentNp();
        this.svVariants = result.getSvVariants();
        this.snps.clear();
    }

    /**
    *
    *@return
    *The cnvIntragenicVariants
    */
    @JsonProperty("cnv-intragenic-variants")
    public List<CVRCnvIntragenicVariant> getCnvIntragenicVariants() {
        return cnvIntragenicVariants != null ? cnvIntragenicVariants : new ArrayList();
    }

    /**
    *
    *@param cnvIntragenicVariants
    *The cnv-intragenic-variants
    */
    @JsonProperty("cnv-intragenic-variants")
    public void setCnvIntragenicVariants(List<CVRCnvIntragenicVariant> cnvIntragenicVariants) {
        this.cnvIntragenicVariants = cnvIntragenicVariants;
    }

    /**
    *
    *@return
    *The cnvVariants
    */
    @JsonProperty("cnv-variants")
    public List<CVRCnvVariant> getCnvVariants() {
        return cnvVariants != null ? cnvVariants : new ArrayList();
    }

    /**
    *
    *@param cnvVariants
    *The cnv-variants
    */
    @JsonProperty("cnv-variants")
    public void setCnvVariants(List<CVRCnvVariant> cnvVariants) {
        this.cnvVariants = cnvVariants;
    }

    /**
    *
    *@return
    *The metaData
    */
    @JsonProperty("meta-data")
    public CVRMetaData getMetaData() {
        return metaData;
    }

    /**
    *
    *@param metaData
    *The meta-data
    */
    @JsonProperty("meta-data")
    public void setMetaData(CVRMetaData metaData) {
        this.metaData = metaData;
    }

    /**
    *
    *@return
    *The segData
    */
    @JsonProperty("seg-data")
    public CVRSegData getSegData() {
        return segData;
    }

    /**
    *
    *@param segData
    *The seg-data
    */
    @JsonProperty("seg-data")
    public void setSegData(CVRSegData segData) {
        this.segData = segData;
    }

    /**
    *
    *@return
    *The snpIndelExonic
    */
    @JsonProperty("snp-indel-exonic")
    public List<CVRSnp> getSnpIndelExonic() {
        return snpIndelExonic != null ? snpIndelExonic : new ArrayList();
    }

    /**
    *
    *@param snpIndelExonic
    *The snp-indel-exonic
    */
    @JsonProperty("snp-indel-exonic")
    public void setSnpIndelExonic(List<CVRSnp> snpIndelExonic) {
        this.snpIndelExonic = snpIndelExonic;
    }

    /**
    *
    *@return
    *The snpIndelExonicNp
    */
    @JsonProperty("snp-indel-exonic-np")
    public List<CVRSnp> getSnpIndelExonicNp() {
        return snpIndelExonicNp != null ? snpIndelExonicNp : new ArrayList();
    }

    /**
    *
    *@param snpIndelExonicNp
    *The snp-indel-exonic-np
    */
    @JsonProperty("snp-indel-exonic-np")
    public void setSnpIndelExonicNp(List<CVRSnp> snpIndelExonicNp) {
        this.snpIndelExonicNp = snpIndelExonicNp;
    }

    /**
    *
    *@return
    *The snpIndelSilent
    */
    @JsonProperty("snp-indel-silent")
    public List<CVRSnp> getSnpIndelSilent() {
        return snpIndelSilent != null ? snpIndelSilent : new ArrayList();
    }

    /**
    *
    *@param snpIndelSilent
    *The snp-indel-silent
    */
    @JsonProperty("snp-indel-silent")
    public void setSnpIndelSilent(List<CVRSnp> snpIndelSilent) {
        this.snpIndelSilent = snpIndelSilent;
    }

    /**
    *
    *@return
    *The snpIndelSilentNp
    */
    @JsonProperty("snp-indel-silent-np")
    public List<CVRSnp> getSnpIndelSilentNp() {
        return snpIndelSilentNp != null ? snpIndelSilentNp : new ArrayList();
    }

    /**
    *
    *@param snpIndelSilentNp
    *The snp-indel-silent-np
    */
    @JsonProperty("snp-indel-silent-np")
    public void setSnpIndelSilentNp(List<CVRSnp> snpIndelSilentNp) {
        this.snpIndelSilentNp = snpIndelSilentNp;
    }

    /**
    *
    *@return
    *The svVariants
    */
    @JsonProperty("sv-variants")
    public List<CVRSvVariant> getSvVariants() {
        return svVariants != null ? svVariants : new ArrayList();
    }

    /**
    *
    *@param svVariants
    *The sv-variants
    */
    @JsonProperty("sv-variants")
    public void setSvVariants(List<CVRSvVariant> svVariants) {
        this.svVariants = svVariants;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public List<CVRSnp> getAllCvrSnps() {
        List<CVRSnp> allSnps = new ArrayList<>();
        allSnps.addAll(getSnpIndelExonic());
        allSnps.addAll(getSnpIndelExonicNp());
        allSnps.addAll(getSnpIndelSilent());
        allSnps.addAll(getSnpIndelSilentNp());
        return allSnps;
    }

    public List<CVRSnp> getAllSignedoutCvrSnps() {
        List<CVRSnp> signedoutSnps = new ArrayList<>();
        for (CVRSnp snp : getAllCvrSnps()) {
            if (snp.getClinicalSignedOut().equals("1")) {
                signedoutSnps.add(snp);
            }
        }
        return signedoutSnps;
    }

    public List<CVRSnp> getAllNonSignedoutCvrSnps() {
        List<CVRSnp> nonSignedoutSnps = new ArrayList<>();
        for (CVRSnp snp : getAllCvrSnps()) {
            if (snp.getClinicalSignedOut().equals("0")) {
                nonSignedoutSnps.add(snp);
            }
        }
        return nonSignedoutSnps;
    }
}
