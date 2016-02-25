/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dcm4che3.tool.storescp;

/**
 *
 * @author mathieu
 */
public class SequenceStatistics {
    private String uid;
    private Long numberOfFiles = 0L;
    private Long diskUsageInBit = 0L; 

    public SequenceStatistics(String uid) {
        this.uid = uid;
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

}
