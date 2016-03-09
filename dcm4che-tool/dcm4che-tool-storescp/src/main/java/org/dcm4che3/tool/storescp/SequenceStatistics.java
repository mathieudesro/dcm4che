/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dcm4che3.tool.storescp;

import java.util.HashSet;

/**
 *
 * @author mathieu
 */
public class SequenceStatistics {
    private String uid;
    private HashSet<String> echoesNumber;
    private Long numberOfFiles = 0L;
    private Long diskUsageInBit = 0L; 


    public SequenceStatistics(String uid) {
        this.uid = uid;
        this.echoesNumber = new HashSet();
    }
    
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Long getNumberOfFiles() {
        return numberOfFiles;
    }

    public void setNumberOfFiles(Long numberOfFiles) {
        this.numberOfFiles = numberOfFiles;
    }

    public Long getDiskUsageInBit() {
        return diskUsageInBit;
    }

    public void setDiskUsageInBit(Long diskUsageInBit) {
        this.diskUsageInBit = diskUsageInBit;
    }
    
    public void incrementNumberOfFilesAndAddDiskUsageInBit(Long diskUsageInBit) {
        this.diskUsageInBit +=diskUsageInBit;
        this.numberOfFiles++;
    }

    public HashSet<String> getEchoesNumber() {
        return echoesNumber;
    }

    public void setEchoesNumber(HashSet<String> echoesNumber) {
        this.echoesNumber = echoesNumber;
    }
    
    public void addEchoesNumber(String echoesNumber) {
        this.echoesNumber.add(echoesNumber);
    }    
    
}
