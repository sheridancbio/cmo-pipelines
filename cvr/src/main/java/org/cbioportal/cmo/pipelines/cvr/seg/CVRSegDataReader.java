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
package org.cbioportal.cmo.pipelines.cvr.seg;

/**
 *
 * @author jake-rose
 */
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import org.cbioportal.cmo.pipelines.cvr.model.CVRData;
import org.cbioportal.cmo.pipelines.cvr.model.CVRMergedResult;
import org.cbioportal.cmo.pipelines.cvr.model.CVRSegData;
import org.cbioportal.cmo.pipelines.cvr.model.CVRSegRecord;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.core.io.FileSystemResource;
import org.apache.log4j.Logger;
import org.cbioportal.cmo.pipelines.cvr.CVRUtilities;

public class CVRSegDataReader implements ItemStreamReader<CVRSegRecord>{
    
    
    @Value("#{jobParameters[stagingDirectory]}")
    private String stagingDirectory;
        
    @Autowired
    public CVRUtilities cvrUtilities;
    
    private CVRData cvrData;
    private List<CVRSegData> cvrSegData = new ArrayList<>();
    private List<CVRSegRecord> cvrSegRecords = new ArrayList<>();
    
    Logger log = Logger.getLogger(CVRSegDataReader.class);
    
    @Override
    public void open(ExecutionContext ec) throws ItemStreamException{
        try{
            if(ec.get("cvrData") == null) {
                cvrData = cvrUtilities.readJson(Paths.get(stagingDirectory).resolve(cvrUtilities.CVR_FILE).toString());
            }
            else {
                cvrData = (CVRData)ec.get("cvrData");
            }
        }
        catch(IOException e)
        {
            throw new ItemStreamException("Failure to read " + stagingDirectory + "/" + cvrUtilities.CVR_FILE);
        }
        try{//Catches exception when file does not exist (e.g. first run, file not yet written)
            FlatFileItemReader<CVRSegRecord> reader = new FlatFileItemReader<>();
            reader.setResource(new FileSystemResource(stagingDirectory + "/" + cvrUtilities.SEG_FILE));
            DefaultLineMapper<CVRSegRecord> mapper = new DefaultLineMapper<>();
            DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
            tokenizer.setDelimiter("\t");
            mapper.setLineTokenizer(tokenizer);
            mapper.setFieldSetMapper(new CVRSegFieldSetMapper());
            reader.setLineMapper(mapper);
            reader.setLinesToSkip(1);
            reader.open(ec);
            CVRSegRecord to_add;
            try{
                while((to_add = reader.read()) != null && to_add.getID() != null){
                    if(!cvrUtilities.getNewIds().contains(to_add.getID())) {
                        cvrSegRecords.add(to_add);
                    }
                }
            }
            catch(Exception e){
                throw new ItemStreamException(e);
            }
            reader.close();
        }
        catch(Exception b){
            String message = "File " + cvrUtilities.SEG_FILE + " does not exist";
            log.info(message);
        }
        
        for(CVRMergedResult result : cvrData.getResults()){
            CVRSegData cvrSegData = result.getSegData();
            if (cvrSegData.getSegData() == null) {
                continue;
            }
            HashMap<Integer,String> indexMap = new HashMap<>();
            boolean first = true;
            String id = result.getMetaData().getDmpSampleId();
            for(List<String> segData : cvrSegData.getSegData()){
                
                CVRSegRecord cvrSegRecord = new CVRSegRecord();
                if(first){
                    for(int i=0;i<segData.size();i++){
                        indexMap.put(i, segData.get(i));
                    }
                    first = false;
                }
                else{
                    for(int i=0;i<segData.size();i++){
                        cvrSegRecord.setID(id);
                        
                        String field = indexMap.get(i).replace(".", "_");//dots in source; replaced for method
                        try{
                            cvrSegRecord.getClass().getMethod("set" + field, String.class).invoke(cvrSegRecord, segData.get(i));
                    
                        }
                        catch(Exception e){
                            String message = "No set method exists for " + field;
                            log.warn(message);
                        }
                    }
                    cvrSegRecord.setIsNew(cvrUtilities.IS_NEW);
                    cvrSegRecords.add(cvrSegRecord);
                }
            }
        }
    }
    
    @Override
    public CVRSegRecord read() throws Exception{
        if(!cvrSegRecords.isEmpty()){
            return cvrSegRecords.remove(0);
        }
        return null;
    }
    
    @Override
    public void update(ExecutionContext ec) throws ItemStreamException {}
    
    @Override
    public void close() throws ItemStreamException {}
    
}
