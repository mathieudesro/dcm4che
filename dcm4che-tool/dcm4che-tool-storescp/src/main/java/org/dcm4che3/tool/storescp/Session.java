/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dcm4che3.tool.storescp;

import com.google.gson.Gson;
import org.dcm4che3.data.Tag;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.io.DicomInputStream;

/**
 *
 * @author mathieu
 */

public class Session {
    private final File path;    
    private final HashMap table = new HashMap();
    private final HashMap sequences = new HashMap();
    private final HashMap<String, SequenceStatistics> statisticsMap = new HashMap<>();

    public Session(File path) {
        this.path = path;
        try {
            Files.walkFileTree(path.toPath(), new SimpleFileVisitor<Path>(){
            
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        
                        readDicomFile(file);

                        return FileVisitResult.CONTINUE;    
                }
            });
            
        } catch (IOException ex) {
            Logger.getLogger(Session.class.getName()).log(Level.SEVERE, null, ex);
        }
 
        Long numberOfFiles = 0L;
        Long diskUsageInBit = 0L;
                
        for (SequenceStatistics statistics: statisticsMap.values()) {
            String uid = statistics.getUid();
            HashMap sequence = (HashMap)sequences.get(uid);
            sequence.put("NumberOfFiles", statistics.getNumberOfFiles());
            sequence.put("DiskUsageInBit", statistics.getDiskUsageInBit());
            sequences.replace(uid, sequence);
                    
            numberOfFiles += statistics.getNumberOfFiles();
            diskUsageInBit += statistics.getDiskUsageInBit();
        }
               
        table.put("DiskUsageInBit", diskUsageInBit);
        table.put("NumberOfFiles", numberOfFiles); 
        table.put("children", sequences.values());
        table.put("Available", Boolean.TRUE);        
    }
    
    
    private void readDicomFile(Path file){
        
        HashMap sequence = new HashMap();
               
        try {

            DicomInputStream dicom = new DicomInputStream(file.toFile());
            Attributes attrs = dicom.readDataset(-1, -1);
            
            HashMap map = new HashMap();
            map = extractASCCONVAttributes(file);
            
            
            String patientName = attrs.getString(Tag.PatientName);
            table.putIfAbsent("PatientName", patientName);
            table.putIfAbsent("name", patientName);
            table.putIfAbsent("checked", false);
            table.putIfAbsent("archive", false);
            table.putIfAbsent("iconCls", "task-folder");
            
            table.putIfAbsent("PatientID", attrs.getString(Tag.PatientID));
            table.putIfAbsent("ReferringPhysicianName", attrs.getString(Tag.ReferringPhysicianName));
            table.putIfAbsent("PatientBirthDate", attrs.getString(Tag.PatientBirthDate));
            table.putIfAbsent("PatientBirthTime", attrs.getString(Tag.PatientBirthTime));
            table.putIfAbsent("PatientSize", attrs.getString(Tag.PatientSize));
            table.putIfAbsent("PatientWeight", attrs.getString(Tag.PatientWeight));
            table.putIfAbsent("PatientSex", attrs.getString(Tag.PatientSex));
            table.putIfAbsent("PatientAge", attrs.getString(Tag.PatientAge));            
            
            table.putIfAbsent("StudyInstanceUID", attrs.getString(Tag.StudyInstanceUID));            
            table.putIfAbsent("StudyDescription", attrs.getString(Tag.StudyDescription));            
            table.putIfAbsent("StudyDate", attrs.getString(Tag.StudyDate));            
            table.putIfAbsent("StudyTime", attrs.getString(Tag.StudyTime));
                       
            String seriesInstanceUID =  attrs.getString(Tag.SeriesInstanceUID);
            sequence.putIfAbsent("SeriesInstanceUID", seriesInstanceUID);
            sequence.putIfAbsent("ImageType", attrs.getString(Tag.ImageType));

            SequenceStatistics statistics = statisticsMap.getOrDefault(seriesInstanceUID, new SequenceStatistics(seriesInstanceUID));
            statistics.incrementNumberOfFilesAndAddDiskUsageInBit(file.toFile().length());
           
            String seriesNumber = attrs.getString(Tag.SeriesNumber);
            String protocoleName = attrs.getString(Tag.ProtocolName);
            sequence.putIfAbsent("SeriesNumber", seriesNumber);
            sequence.putIfAbsent("ProtocolName", protocoleName);
            sequence.putIfAbsent("name", String.format("%02d",Integer.parseInt(seriesNumber))+"-"+protocoleName);
            sequence.putIfAbsent("checked", false);
            sequence.putIfAbsent("iconCls", "task");
            sequence.putIfAbsent("leaf", true);            
            sequence.putIfAbsent("archive", false);
            sequence.putIfAbsent("NumberOfEchoes", map.get("NumberOfEchoes"));
            
            statisticsMap.put(seriesInstanceUID, statistics);
            sequences.putIfAbsent(seriesInstanceUID, sequence);

            
            
            
        } catch (IOException ex) {
            Logger.getLogger(Session.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public String getUid(){
        return (String)table.get("StudyInstanceUID");
    }
    
    public String getPatientId(){
        return (String)table.get("PatientID");
    }   

    public String getReferringPhysicianName(){
        return (String)table.get("ReferringPhysicianName");
    }    
    
    public String getStudyDate(){
        return (String)table.get("StudyDate");
    }
    
    public Boolean getAvailable(){
        return (Boolean)table.get("Available");
    }     
    
    public String toJson(){
        Gson gson = new Gson(); 
        return gson.toJson(table); 
    }
    
    @Override
    public String toString() {
        return toJson();
    }
    
    public int lenght(){
        return toJson().length();    
    }  
    
    public HashMap extractASCCONVAttributes(Path file){
        HashMap map = new HashMap();
            
        try {
            String text = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
            text = text.substring(text.lastIndexOf("### ASCCONV BEGIN ###")+1);
            text = text.substring(0, text.indexOf("### ASCCONV END ###"));

            for (String line: text.split("\n")){
                if(line.contains("lContrasts")){
                    map.putIfAbsent("NumberOfEchoes", line.substring(line.lastIndexOf("=")+1).trim());
                }            
            }
        } catch (IOException ex) {
            Logger.getLogger(Session.class.getName()).log(Level.SEVERE, null, ex);
        }
        return map;
    
    }   
}
