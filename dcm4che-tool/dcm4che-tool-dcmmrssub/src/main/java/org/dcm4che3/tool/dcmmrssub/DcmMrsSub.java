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

package org.dcm4che3.tool.dcmmrssub;

import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.tool.common.CLIUtils;
import org.dcm4che3.io.DicomInputStream; 
import org.dcm4che3.io.DicomOutputStream;


/**
 * @author Mathieu Desrtosiers <mathieu.desrosiers@theroyal.ca>
 */
public class DcmMrsSub {

    private static ResourceBundle rb =
        ResourceBundle.getBundle("org.dcm4che3.tool.dcmmrssub.messages");
  
    public static void main(String[] args) {
        try {
            CommandLine commandLine = parseComandLine(args);
            if(commandLine.getArgs().length == 2){
                
                
                
                
                /*
                //7fr1,1010
                File dicomFile1 = new File(commandLine.getArgs()[0]);
                File dicomFile2 = new File(commandLine.getArgs()[1]);

                DicomInputStream dicom1 = new DicomInputStream(dicomFile1);
                DicomInputStream dicom2 = new DicomInputStream(dicomFile2);
                DicomOutputStream output1 = new DicomOutputStream(new File(commandLine.getArgs()[0].replace(".dcm", "-subtract-8bits.dcm")));  
                //DicomOutputStream output2 = new DicomOutputStream(new File(commandLine.getArgs()[0].replace(".dcm", "-subtract-32bits.dcm")));  


                Attributes meta = dicom1.readFileMetaInformation();
                Attributes attrs1 = dicom1.readDataset(-1, -1);
                */
                //Attributes attrs2 = dicom2.readDataset(-1, -1);

                //dicom1.readAttributes(attrs1, 0, 0);
                //dicom2.readAttributes(attrs2, 0, 0);
                
                
                
/*
                byte[] array1 = attrs1.getBytes(2145456144);
                byte[] array2 = attrs2.getBytes(2145456144);           
                byte[] results = new byte[array1.length];

                for(int i =0; i< array1.length; i++){
                    results[i] = (byte) (array1[i]- array2[i]);
                }

                int j = 0;
                int[] arrayOfInt1 = new int[array1.length/4];
                int[] arrayOfInt2 = new int[array2.length/4];
                int[] results2 = new int[arrayOfInt1.length];

                for(int i =0; i< arrayOfInt1.length; i++){
                    j = i*4;
                    arrayOfInt1[i]= (array1[0]<<24)&0xff000000|
                                        (array1[1]<<16)&0x00ff0000|
                                        (array1[2]<< 8)&0x0000ff00|
                                        (array1[3]<< 0)&0x000000ff;    

                    arrayOfInt2[i]= (array2[0]<<24)&0xff000000|
                                        (array2[1]<<16)&0x00ff0000|
                                        (array2[2]<< 8)&0x0000ff00|
                                        (array2[3]<< 0)&0x000000ff;    
                }
                for(int i =0; i< arrayOfInt1.length; i++){
                    results2[i] = arrayOfInt1[i]- arrayOfInt2[i];
                }                

                ByteBuffer byteBuffer = ByteBuffer.allocate(results2.length * 4);        
                IntBuffer intBuffer = byteBuffer.asIntBuffer();
                intBuffer.put(results2);


                attrs1.setBytes(2145456144, VR.OB, results);
                attrs2.setBytes(2145456144, VR.OB, byteBuffer.array());
*/
                output1.writeDataset(meta, attrs1);
                output1.close();


                //output2.writeDataset(meta, attrs2);
                //output2.close();            
            }
           
        } catch (IOException ex) {
            Logger.getLogger(DcmMrsSub.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    @SuppressWarnings("static-access")
    private static CommandLine parseComandLine(String[] args){
        try {
            Options opts = new Options();
            CLIUtils.addCommonOptions(opts);
            /*opts.addOption(OptionBuilder
            .withLongOpt("width")
            .hasArg()
            .withArgName("col")
            .withDescription(rb.getString("width"))
            .create("w"));
            */
            return CLIUtils.parseComandLine(args, opts, rb, DcmMrsSub.class);
        } catch (ParseException ex) {
            Logger.getLogger(DcmMrsSub.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
