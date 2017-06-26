/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is part of dcm4che, an implementation of DICOM(TM) in
 * Java(TM), hosted at https://github.com/gunterze/dcm4che.
 *
 * The Initial Developer of the Original Code is
 * Agfa Healthcare.
 * Portions created by the Initial Developer are Copyright (C) 2011
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 * See @authors listed below
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK ***** */

package org.dcm4che3.tool.rearrange;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.io.DicomInputStream;
import org.dcm4che3.tool.common.CLIUtils;


/**
 * @author Mathieu Desrtosiers <mathieu.desrosiers@theroyal.ca>
 */
public class Rearrange {

    private static ResourceBundle rb = ResourceBundle.getBundle("org.dcm4che3.tool.rearrange.messages");
  
    public static void main(String[] args) {
        
            CommandLine commandLine = parseComandLine(args);
            if(commandLine.getArgs().length == 2){
                File inputDirectory = new File(commandLine.getArgs()[0]);
                File outputDirectory = new File(commandLine.getArgs()[1]);
                
                if(inputDirectory.exists() && inputDirectory.list().length != 0){
                    if(outputDirectory.exists() &&
                        outputDirectory.isDirectory() &&
                        outputDirectory.list().length == 0){
                        try {
                            Stream<Path> paths = Files.walk(Paths.get(inputDirectory.getPath()));
                            paths
                                .filter((path) -> !path.toFile().isDirectory())
                                .forEach(path -> {
                                    readTagsAndConvert(path, outputDirectory);
                                });                                            
                        } catch (IOException ex) {
                            Logger.getLogger(Rearrange.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    else{
                        System.out.println(rb.getString("output_missing")+outputDirectory.toString());                
                    }
                }
                else{
                    System.out.println(rb.getString("input_error")+inputDirectory.toString());
                }
            }
        
    }
    
    private static void readTagsAndConvert(final Path file, final File outputDirectory){
        try {
            DicomInputStream dicom = null;
            dicom = new DicomInputStream(file.toFile());
            
            Attributes attrs = dicom.readDataset(-1, -1);
            String manufacturer = attrs.getString(Tag.Manufacturer);
            if (manufacturer.contains("SIEMENS")){
                HashMap values = extractASCCONVAttributes(file);
                
                String numberOfEchoes = (String) values.get("NumberOfEchoes");
                String patientName = attrs.getString(Tag.PatientName);
                String echoTime = attrs.getString(Tag.EchoTime);
                String seriesNumber = attrs.getString(Tag.SeriesNumber);
                String seriesDescription = attrs.getString(Tag.SeriesDescription);
                String modality = attrs.getString(Tag.Modality);
                String instanceNumber = attrs.getString(Tag.InstanceNumber);
    /*
                System.out.println("--start--");            
                System.out.println(patientName);
                System.out.println(echoTime);
                System.out.println(seriesNumber);
                System.out.println(seriesDescription);
                System.out.println(modality);
                System.out.println(instanceNumber);
                System.out.println(numberOfEchoes);
                System.out.println("--end--");
    */            
                StringBuilder dicomFilename = new StringBuilder();
                dicomFilename.append(outputDirectory.toString())
                                .append("/")
                                .append(patientName)
                                .append("/")
                                .append(String.format("%02d", Integer.parseInt(seriesNumber)))
                                .append("-")
                                .append(seriesDescription);

                if(!"1".contains(numberOfEchoes)){
                    System.out.println("--multiple echoes--");
                    dicomFilename.append("/echo_")
                            //format me
                            .append(echoTime);
                }
                dicomFilename.append("/")                    
                        .append(patientName)
                        .append("-")
                        .append(String.format("%04d", Integer.parseInt(instanceNumber)))
                        .append(".dcm");

                System.out.println("convert : "+ file);                                  
                File outputDicomFile = new File(dicomFilename.toString());
                File outputDicomDirectory = outputDicomFile.getParentFile();
                if (! outputDicomDirectory.exists()){
                    System.out.println("directory :"+outputDicomDirectory.toString());
                    outputDicomDirectory.mkdirs();
                }
                Files.copy(file, outputDicomFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                System.out.println(" to : " +dicomFilename.toString());
                
            }
            else{
                System.out.println(rb.getString("not_siemens"));            
            }
            
            
        } catch (IOException ex) {
            Logger.getLogger(Rearrange.class.getName()).log(Level.SEVERE, null, ex);
        }
            
    }     
    
    @SuppressWarnings("static-access")
    private static CommandLine parseComandLine(String[] args){
        try {
            Options opts = new Options();
            CLIUtils.addCommonOptions(opts);
            return CLIUtils.parseComandLine(args, opts, rb, Rearrange.class);
        } catch (ParseException ex) {
            Logger.getLogger(Rearrange.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
               
    }    
    
    public static HashMap extractASCCONVAttributes(Path file){
        try {
            HashMap map = new HashMap();
            
            String text = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
            
            if(text.contains("### ASCCONV BEGIN ###")){
                text = text.substring(text.lastIndexOf("### ASCCONV BEGIN ###")+1);
                text = text.substring(0, text.indexOf("### ASCCONV END ###"));

                for (String line: text.split("\n")){
                    if(line.contains("lContrasts")){
                        map.putIfAbsent("NumberOfEchoes", line.substring(line.lastIndexOf("=")+1).trim());
                    }
                }            
            }
            return map;
        } catch (IOException ex) {
            Logger.getLogger(Rearrange.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }   
}