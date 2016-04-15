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
package org.cbioportal.cmo.pipelines.crdb.model;

import java.util.*;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.*;
import org.apache.commons.lang.builder.ToStringBuilder;

public class CRDBSurvey {
    String dmpId;
    Date qsDate;
    String adjTxt;
    String noSysTxt;
    String priorRx;
    String brainMet;
    String ecog;
    String comments;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

/**
* No args constructor for use in serialization
* 
*/
public CRDBSurvey() {
}

/**
* 
* @param dmpId
* @param qsDate
* @param adjTxt
* @param noSysTxt
* @param priorRx
* @param brainMet
* @param ecog
* @param comments
*/
public CRDBSurvey(String dmpId, Date qsDate, String adjTxt, String noSysTxt,
        String priorRx, String brainMet, String ecog, String comments) {
this.dmpId = dmpId;
this.qsDate = qsDate;
this.adjTxt = adjTxt;
this.noSysTxt = noSysTxt;
this.priorRx = priorRx;
this.brainMet = brainMet;
this.ecog = ecog;
this.comments = comments;
}

    /**
     * 
     * @return dmpId
     */
    public String getDmpId() {
        return dmpId;
    }
    
    /**
     * 
     * @param dmpId 
     */
    public void setDmpId(String dmpId) {
        this.dmpId = dmpId;
    }

    /**
     * 
     * @param dmpId
     * @return 
     */
    public CRDBSurvey withDmpId(String dmpId) {
        this.dmpId = dmpId;
        return this;
    }

    /**
     * 
     * @return qsDate
     */
    public Date getQsDate() {
        return qsDate;
    }
    
    /**
     * 
     * @param qsDate 
     */
    public void setQsDate(Date qsDate) {
        this.qsDate = qsDate;
    }
    
    /**
     * 
     * @param qsDate
     * @return 
     */
    public CRDBSurvey withQsDate(Date qsDate) {
        this.qsDate = qsDate;
        return this;
    }
    
    /**
     * 
     * @return adjTxt
     */
    public String getAdjTxt() {
        return adjTxt;
    }
    
    /**
     * 
     * @param adjTxt 
     */
    public void setAdjTxt(String adjTxt) {
        this.adjTxt = adjTxt;
    }
    
    /**
     * 
     * @param adjTxt
     * @return 
     */
    public CRDBSurvey withAdjTxt(String adjTxt) {
        this.adjTxt = adjTxt;
        return this;
    }
    
    /**
     * 
     * @return noSysTxt
     */
    public String getNoSysTxt() {
        return noSysTxt;
    }
    
    /**
     * 
     * @param noSysTxt 
     */
    public void setNoSysTxt(String noSysTxt) {
        this.noSysTxt = noSysTxt;
    }
    
    /**
     * 
     * @param noSysTxt
     * @return 
     */
    public CRDBSurvey withNoSysTxt(String noSysTxt) {
        this.noSysTxt = noSysTxt;
        return this;
    }

    /**
     * 
     * @return priorRx
     */
    public String getPriorRx() {
        return priorRx;
    }
    
    /**
     * 
     * @param priorRx 
     */
    public void setPriorRx(String priorRx) {
        this.priorRx = priorRx;
    }
    
    /**
     * 
     * @param priorRx
     * @return 
     */
    public CRDBSurvey withPriorRx(String priorRx) {
        this.priorRx = priorRx;
        return this;
    }    
    
    /**
     * 
     * @return brainMet
     */
    public String getBrainMet() {
        return brainMet;
    }
    
    /**
     * 
     * @param brainMet 
     */
    public void setBrainMet(String brainMet) {
        this.brainMet = brainMet;
    }
    
    /**
     * 
     * @param brainMet
     * @return 
     */
    public CRDBSurvey withBrainMet(String brainMet) {
        this.brainMet = brainMet;
        return this;
    }

    /**
     * 
     * @return ecog
     */
    public String getEcog() {
        return ecog;
    }
    
    /**
     * 
     * @param ecog 
     */
    public void setEcog(String ecog) {
        this.ecog = ecog;
    }
    
    /**
     * 
     * @param ecog
     * @return 
     */
    public CRDBSurvey withEcog(String ecog) {
        this.ecog = ecog;
        return this;
    }    

    /**
     * 
     * @return comments
     */
    public String getComments() {
        return comments;
    }
    
    /**
     * 
     * @param comments 
     */
    public void setComments(String comments) {
        this.comments = comments;
    }
    
    /**
     * 
     * @param comments
     * @return 
     */
    public CRDBSurvey withComments(String comments) {
        this.comments = comments;
        return this;
    }    

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public CRDBSurvey withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}
