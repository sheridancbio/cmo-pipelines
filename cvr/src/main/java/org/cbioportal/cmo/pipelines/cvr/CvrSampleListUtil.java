/*
 * Copyright (c) 2017 Memorial Sloan-Kettering Cancer Center.
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

package org.cbioportal.cmo.pipelines.cvr;

import org.apache.log4j.Logger;
import java.util.*;
import org.cbioportal.cmo.pipelines.cvr.model.CvrResponse;
import org.springframework.context.annotation.*;

/**
 *
 * @author ochoaa
 */
@Configuration
public class CvrSampleListUtil {

    private CvrResponse cvrResponse;
    private Set<String> dmpMasterList = new HashSet<>();
    private Set<String> newDmpSamples = new HashSet<>();
    private Set<String> portalSamples = new HashSet<>();
    private Set<String> dmpSamplesNotInPortal = new HashSet<>();
    private Set<String> portalSamplesNotInDmp = new HashSet<>();
    private Set<String> newDmpGmlPatients = new HashSet<>();
    private Map<String, List<String>> gmlPatientSampleMap = new HashMap<>();
    private Integer maxNumSamplesToRemove;
    private Set<String> samplesRemovedList = new HashSet<>();
    private Set<String> samplesInvalidPatientIdList = new HashSet<>();
    private Map<String, String> sampleListStats = new HashMap<>();
    private Map<String, Integer> signedoutSampleSnpCounts = new HashMap<>();
    private Map<String, Integer> unfilteredSampleSnpCounts = new HashMap<>();
    private Set<String> whitelistedSamplesWithZeroVariants = new HashSet<>();
    private Set<String> nonWhitelistedZeroVariantSamples = new HashSet<>();
    private Set<String> newUnreportedSamplesWithZeroVariants = new HashSet<>();
    private Integer originalClinicalFileRecordCount = 0;
    private Integer newClinicalFileRecordCount = 0;

    Logger log = Logger.getLogger(CvrSampleListUtil.class);

    @Bean
    public CvrSampleListUtil CvrSampleListUtil() {
        return new CvrSampleListUtil();
    }

    public CvrSampleListUtil() {}

    /**
     * @return the cvrResponse
     */
    public CvrResponse getCvrResponse() {
        return cvrResponse;
    }

    /**
     * @param cvrResponse the cvrResponse to set
     */
    public void setCvrResponse(CvrResponse cvrResponse) {
        this.cvrResponse = cvrResponse;
    }

    /**
     * The complete DMP master sample list from CVR.
     *
     * @return the dmpMasterList
     */
    public Set<String> getDmpMasterList() {
        return dmpMasterList;
    }

    /**
     * @param dmpMasterList the dmpMasterList to set
     */
    public void setDmpMasterList(Set<String> dmpMasterList) {
        this.dmpMasterList = dmpMasterList;
    }

    /**
     * New DMP samples coming from CVR.
     *
     * @return the newDmpSamples
     */
    public Set<String> getNewDmpSamples() {
        return newDmpSamples;
    }

    /**
     * @param newDmpSamples the newDmpSamples to set
     */
    public void setNewDmpSamples(Set<String> newDmpSamples) {
        this.newDmpSamples = newDmpSamples;
        updatePortalSamples(newDmpSamples);
    }

    /**
     * @param sampleId the sampleId to add
     */
    public void addNewDmpSample(String sampleId) {
        this.newDmpSamples.add(sampleId);
        addPortalSample(sampleId); //adds id to portal samples list also
    }

    /**
     * Count of samples seen in original clinical data file
     *
     * @return the originalClinicalFileRecordCount
     */
    public Integer getOriginalClinicalFileRecordCount() {
        return originalClinicalFileRecordCount;
    }

    /**
     * @param originalClinicalFileRecordCount the originalClinicalFileRecordCount to set
     */
    public void setOriginalClinicalFileRecordCount(Integer originalClinicalFileRecordCount) {
        this.originalClinicalFileRecordCount = originalClinicalFileRecordCount;
    }

    /**
     * Count of samples written to new clinical file
     *
     * @return the newClinicalFileRecordCount
     */
    public Integer getNewClinicalFileRecordCount() {
        return newClinicalFileRecordCount;
    }

    /**
     * @param newClinicalFileRecordCount the newClinicalFileRecordCount to set
     */
    public void setNewClinicalFileRecordCount(Integer newClinicalFileRecordCount) {
        this.newClinicalFileRecordCount = newClinicalFileRecordCount;
    }

    /**
     * All samples seen in staging files generated by CVR pipeline (existing and new).
     * Returns all samples with exception of samples that are not seen in the DMP master list.
     *
     * @return the portalSamples
     */
    public Set<String> getPortalSamples() {
        portalSamples.removeAll(samplesInvalidPatientIdList);
        return portalSamples;
    }

    /**
     * @param portalSamples the portalSamples to set
     */
    public void setPortalSamples(Set<String> portalSamples) {
        this.portalSamples = portalSamples;
    }

    /**
     * @param portalSamples the portalSamples to add
     */
    public void updatePortalSamples(Set<String> portalSamples) {
        this.portalSamples.addAll(portalSamples);
    }

    /**
     * @param sampleId the sampleId to add
     */
    public void addPortalSample(String sampleId) {
        this.portalSamples.add(sampleId);
    }

    /**
     * @return the dmpSamplesNotInPortal
     */
    public Set<String> getDmpSamplesNotInPortal() {
        return dmpSamplesNotInPortal;
    }

    /**
     * updates the dmpSamplesNotInPortal
     */
    public void updateDmpSamplesNotInPortal() {
        this.dmpSamplesNotInPortal = new HashSet<>(dmpMasterList);
        dmpSamplesNotInPortal.removeAll(portalSamples);
    }

    /**
     * @return the portalSamplesNotInDmp
     */
    public Set<String> getPortalSamplesNotInDmp() {
        return portalSamplesNotInDmp;
    }

    /**
     * updates the portalSamplesNotInDmp
     */
    public void updatePortalSamplesNotInDmp() {
        this.portalSamplesNotInDmp = new HashSet<>(portalSamples);
        portalSamplesNotInDmp.removeAll(dmpMasterList);
    }

    /**
     * @return the newDmpGmlPatients
     */
    public Set<String> getNewDmpGmlPatients() {
        return newDmpGmlPatients;
    }

    /**
     * @param newDmpGmlPatients the newDmpGmlPatients to set
     */
    public void setNewDmpGmlPatients(Set<String> newDmpGmlPatients) {
        this.newDmpGmlPatients = newDmpGmlPatients;
    }

    /**
     * @param patientId the patientId to add
     */
    public void addNewDmpGmlPatient(String patientId) {
        this.newDmpGmlPatients.add(patientId);
    }

    /**
     * @return the gmlPatientSampleMap
     */
    public Map<String, List<String>> getGmlPatientSampleMap() {
        return gmlPatientSampleMap;
    }

    /**
     * @param gmlPatientSampleMap the gmlPatientSampleMap to set
     */
    public void setGmlPatientSampleMap(Map<String, List<String>> gmlPatientSampleMap) {
        this.gmlPatientSampleMap = gmlPatientSampleMap;
    }

    /**
     * Updates the patient-sample map with a new patient/sample pair.
     *
     * @param patientId
     * @param sampleId
     */
    public void updateGmlPatientSampleMap(String patientId, String sampleId) {
        List<String> patientSamples = gmlPatientSampleMap.getOrDefault(patientId, new ArrayList());
        patientSamples.add(sampleId);
        this.gmlPatientSampleMap.put(patientId, patientSamples);
    }

    /**
     * @return the maxNumSamplesToRemove
     */
    public Integer getMaxNumSamplesToRemove() {
        return maxNumSamplesToRemove;
    }

    /**
     * @param maxNumSamplesToRemove the maxNumSamplesToRemove to set
     */
    public void setMaxNumSamplesToRemove(Integer maxNumSamplesToRemove) {
        this.maxNumSamplesToRemove = maxNumSamplesToRemove;
    }

    public void updateSampleLists() {
        // update  portal samples not in dmp and dmp samples not in portal lists
        updatePortalSamplesNotInDmp();
        updateDmpSamplesNotInPortal();

        boolean maxSamplesToRemoveThresholdExceeded = (maxNumSamplesToRemove <= 0 || (maxNumSamplesToRemove > 0 && portalSamplesNotInDmp.size() >= maxNumSamplesToRemove));
        if (maxSamplesToRemoveThresholdExceeded) {
            String message;
            if (maxNumSamplesToRemove > 0) {
                message = "updateSampleLists(),  Number of samples in 'portalSamplesNotInDmp' exceeds threshold set by user (" +
                    String.valueOf(maxNumSamplesToRemove) + " samples)";
            }
            else {
                message = "updateSampleLists(),  Something is wrong with 'dmpMasterList' or 'portalSamples' - sample data will not be filtered";
            }
             saveSampleListStats();
            log.warn(message);
        }
        else {
            portalSamples.removeAll(portalSamplesNotInDmp);
            String message = "updateSampleLists(),  Number of samples in 'portalSamplesNotInDmp' passes threshold check - ";
            if (portalSamplesNotInDmp.size() == 0) {
                message += "'portalSamplesNotInDmp' list is empty, no sample data will be removed";
            }
            else {
                message += "'portalSamplesNotInDmp' contains " + String.valueOf(portalSamplesNotInDmp.size()) + " samples. Data for these samples will be removed";
            }
            log.info(message);
        }
    }

    /**
     * @return the samplesRemovedList
     */
    public Set<String> getSamplesRemovedList() {
        return samplesRemovedList;
    }

    /**
     * @param samplesRemovedList the samplesRemovedList to set
     */
    public void setSamplesRemovedList(Set<String> samplesRemovedList) {
        this.samplesRemovedList = samplesRemovedList;
    }

    /**
     * @param sampleId the sampleId to add
     */
    public void addSampleRemoved(String sampleId) {
        this.samplesRemovedList.add(sampleId);
    }

    /**
     * @return the samplesInvalidPatientIdList
     */
    public Set<String> getSamplesInvalidPatientIdList() {
        return samplesInvalidPatientIdList;
    }

    /**
     * @param sampleId to add to samplesInvalidPatientIdList
     */
    public void addSamplesInvalidPatientIdList(String sampleId) {
        this.samplesInvalidPatientIdList.add(sampleId);
        this.samplesRemovedList.add(sampleId);
    }

    /**
     * @return the whitelistedSamplesWithZeroVariants
     */
    public Set<String> getWhitelistedSamplesWithZeroVariants() {
        return whitelistedSamplesWithZeroVariants;
    }

    /**
     * @param whitelistedSamplesWithZeroVariants the whitelistedSamplesWithZeroVariants to set
     */
    public void setWhitelistedSamplesWithZeroVariants(Set<String> whitelistedSamplesWithZeroVariants) {
        this.whitelistedSamplesWithZeroVariants = whitelistedSamplesWithZeroVariants;
    }

    /**
     * Initialize list of non-whitelisted samples with zero signed out variants.
     */
    private void initNonWhitelistedZeroVariantSamples() {
        for (String sampleId : getPortalSamples()) {
            if (signedoutSampleSnpCounts.getOrDefault(sampleId, 0) == 0) {
                this.nonWhitelistedZeroVariantSamples.add(sampleId);
            }
        }
        this.nonWhitelistedZeroVariantSamples.removeAll(getWhitelistedSamplesWithZeroVariants());
    }

    /**
     * @return the non whitelisted sample ids of samples that have zero signed out variants
     */
    public Set<String> getNonWhitelistedZeroVariantSamples() {
        if (nonWhitelistedZeroVariantSamples.isEmpty()) {
            initNonWhitelistedZeroVariantSamples();
        }
        return nonWhitelistedZeroVariantSamples;
    }

    /**
     * @return the signedoutSampleSnpCounts
     */
    public Map<String, Integer> getSignedoutSampleSnpCounts() {
        return signedoutSampleSnpCounts;
    }

    public void updateSignedoutSampleSnpCounts(String sampleId, Integer count) {
        Integer currentCount = signedoutSampleSnpCounts.getOrDefault(sampleId, 0);
        this.signedoutSampleSnpCounts.put(sampleId, currentCount + count);
    }

    /**
     * @param signedoutSampleSnpCounts the signedoutSampleSnpCounts to set
     */
    public void setSignedoutSampleSnpCounts(Map<String, Integer> signedoutSampleSnpCounts) {
        this.signedoutSampleSnpCounts = signedoutSampleSnpCounts;
    }

    /**
     * @param sampleId the sampleId to add
     * @param count the number of unfiltered snps for that sample
     */
    public void updateUnfilteredSampleSnpCount(String sampleId, Integer count) {
        Integer currentCount = unfilteredSampleSnpCounts.getOrDefault(sampleId, 0);
        this.unfilteredSampleSnpCounts.put(sampleId, currentCount + count);
    }

    /**
     * Updates list of samples with both signedout and non-signedout variant counts of zero.
     * This list is used to update the whitelisted samples list file.
     * @return
     */
    public void updateNewUnreportedSamplesWithZeroVariants(String sampleId) {
        this.newUnreportedSamplesWithZeroVariants.add(sampleId);
    }

    /**
     * Returns list of samples with signedout and non-signedout variant counts of zero.
     * This list is used to update the whitelisted samples list.
     * @return
     */
    public Set<String> getNewUnreportedSamplesWithZeroVariants() {
        return newUnreportedSamplesWithZeroVariants;
    }

    /**
     * @param newUnreportedSamplesWithZeroVariants the newUnreportedSamplesWithZeroVariants to set
     */
    public void setNewUnreportedSamplesWithZeroVariants(Set<String> newUnreportedSamplesWithZeroVariants) {
        this.newUnreportedSamplesWithZeroVariants = newUnreportedSamplesWithZeroVariants;
    }

    /**
     * @return the count of unfiltered snps for every sample
     */
    public Map<String, Integer> getUnfilteredSampleSnpCounts() {
        return this.unfilteredSampleSnpCounts;
    }

    public String getSamplePatientId(String sampleId) {
        for (String patientId : gmlPatientSampleMap.keySet()) {
            if (sampleId.contains(patientId)) {
                return patientId;
            }
        }
        return null;
    }

    private void saveSampleListStats() {
        Map<String, String> sampleListStats = new HashMap<>();
        sampleListStats.put("dmpMasterList", String.valueOf(dmpMasterList.size()));
        sampleListStats.put("portalSamples", String.valueOf(portalSamples.size()));
        sampleListStats.put("portalSamplesNotInDmp", String.valueOf(portalSamplesNotInDmp.size()));
        sampleListStats.put("dmpSamplesNotInPortal", String.valueOf(dmpSamplesNotInPortal.size()));
        this.sampleListStats = sampleListStats;
    }

    public Map<String, String> getSampleListStats() {
        return sampleListStats;
    }

}
