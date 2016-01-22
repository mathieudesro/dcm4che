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




/**

--
-- Current Database: `mri`
--

CREATE DATABASE `mri`;
USE `mri`;
--
-- Table structure for table `session`
--

DROP TABLE IF EXISTS `session`;
CREATE TABLE `session` (
  `uid` varchar(100) NOT NULL DEFAULT '',
  `patientid` varchar(20) DEFAULT NULL,
  `protocolename` varchar(40) DEFAULT NULL,
  `gid` varchar(20) DEFAULT NULL,
  `studydate` varchar(20) DEFAULT NULL,
  `available` tinyint(1) DEFAULT '0',
  `metadata` mediumtext,
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
 */



public class Session {
    private final File path;    
    private HashMap table = new HashMap();
    private HashMap sequences = new HashMap();
    private Long numberOfFiles = 0L;
    private Long diskUsageInBit = 0L;  

    public Session(File path) {
        this.path = path;
        
    }
   
    public void map(){
        try {
            Files.walkFileTree(path.toPath(), new SimpleFileVisitor<Path>(){
            
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        readDicomFile(file);
                        diskUsageInBit = file.toFile().length();
                        numberOfFiles++;
                        return FileVisitResult.CONTINUE;    
                }
            });
            
        } catch (IOException ex) {
            Logger.getLogger(Session.class.getName()).log(Level.SEVERE, null, ex);
        }
 
        table.put("Sequences", sequences.values());
        table.put("DiskUsageInBit", diskUsageInBit);
        table.put("NumberOfFiles", numberOfFiles);
        table.put("Available", Boolean.TRUE);

    }
    
    
    public void readDicomFile(Path file){
        
        HashMap sequence = new HashMap();
               
        try {
            DicomInputStream dicom = new DicomInputStream(file.toFile());
            Attributes attrs = dicom.readDataset(-1, -1);
            
            table.putIfAbsent("PatientID", attrs.getString(Tag.PatientID));
            table.putIfAbsent("PatientName", attrs.getString(Tag.PatientName));
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
            sequence.putIfAbsent("ProtocolName", attrs.getString(Tag.ProtocolName));
            sequence.putIfAbsent("SeriesNumber", attrs.getString(Tag.SeriesNumber));
            sequence.putIfAbsent("ImageType", attrs.getString(Tag.ImageType));
            sequences.putIfAbsent(seriesInstanceUID, sequence);
            
        } catch (IOException ex) {
            Logger.getLogger(Session.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    public String getUid(){
        return (String)table.get("StudyInstanceUID");
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
}