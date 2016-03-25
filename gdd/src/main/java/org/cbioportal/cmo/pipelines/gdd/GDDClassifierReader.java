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

package org.cbioportal.cmo.pipelines.gdd;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import com.fasterxml.jackson.databind.ObjectMapper;
import static com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
import org.cbioportal.cmo.pipelines.gdd.model.*;

import org.springframework.http.*;
import org.springframework.batch.item.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.io.FileSystemResource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.beans.factory.annotation.Value;

import java.util.*;
import org.springframework.web.bind.annotation.*;
/**
 * @author Benjamin Gross
 */
public class GDDClassifierReader implements ItemStreamReader<GDDResult>
{
    @Value("${gdd.url}")
    private String gddURL;

    @Value("#{jobParameters[maf]}")
    private String maf;

    @Value("#{jobParameters[cna]}")
    private String cna;

    @Value("#{jobParameters[seg]}")
    private String seg;

    @Value("#{jobParameters[sv]}")
    private String sv;

    @Value("#{jobParameters[clinical]}")
    private String clinical;

    private List<GDDResult> results;
    
    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException
    {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = getRequestEntity();  
        List<HttpMessageConverter<?>> converters = restTemplate.getMessageConverters();                
        converters.add(jsonTextHtmlConverter());            
        restTemplate.setMessageConverters(converters);
        ResponseEntity<GDDClassifier> responseEntity =
            restTemplate.exchange(gddURL, HttpMethod.POST, requestEntity, GDDClassifier.class);  
        this.results = responseEntity.getBody().getResult();
    }

    private HttpEntity<LinkedMultiValueMap<String, Object>> getRequestEntity()
    {
        LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("maf", new FileSystemResource(maf));
        map.add("cna", new FileSystemResource(cna));
        map.add("seg", new FileSystemResource(seg));
        map.add("sv", new FileSystemResource(sv));
        map.add("clinical", new FileSystemResource(clinical));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        return new HttpEntity<LinkedMultiValueMap<String, Object>>(map, headers);
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {}

    @Override
    public void close() throws ItemStreamException {}

    @Override
    public GDDResult read() throws Exception
    {
        // necessary until bug that generates duplicate results in classifier script "generate_feature_table.R" gets fixed
        if (!results.isEmpty() && results.get(0).getSampleId() != null) {            
            return results.remove(0);            
        }
        return null;
    }
    
    public MappingJackson2HttpMessageConverter jsonTextHtmlConverter() {
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(FAIL_ON_EMPTY_BEANS, false);
        objectMapper.setSerializationInclusion(NON_NULL);        
        jsonConverter.setObjectMapper(objectMapper);
        
        List<MediaType> mediaTypes = new ArrayList<>(); 
        mediaTypes.add(new MediaType("text","html",MappingJackson2HttpMessageConverter.DEFAULT_CHARSET));
        jsonConverter.setSupportedMediaTypes(mediaTypes);
        
        return jsonConverter;
    }    
}
