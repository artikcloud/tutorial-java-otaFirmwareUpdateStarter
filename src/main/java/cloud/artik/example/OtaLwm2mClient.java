package cloud.artik.example;

import org.eclipse.leshan.core.response.ExecuteResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import cloud.artik.lwm2m.ArtikCloudClient;
import cloud.artik.lwm2m.Device;
import cloud.artik.lwm2m.FirmwareUpdate;
import cloud.artik.lwm2m.enums.FirmwareUpdateResult;
import cloud.artik.lwm2m.enums.SupportedBinding;

public class OtaLwm2mClient {
    private static String deviceID = null;
    private static String deviceToken = null;
    private static String firmwareVersionAfterUpdate = null;

    private static String saveFirmwarePath = "./";
    private static final int EXPECTED_ARGUMENT_NUMBER = 6;
    
    //generic time to keep the sample app running (millisec)
    private static final long MAX_MS_KEEP_CONNECTION_OPEN = 600000;
    
    private static class Bootstrap {
        private static final String MANUFACTURE = "Sample Company XYZ";
        private static final String MODEL = "XYZ-1";
        private static final String SERIAL_NUMBER = "1234567890";
        private static final String INITIAL_FIRMWARE_VERSION = "V0";
    }

    public static void main (String args[]) throws InterruptedException {
        if (!succeedParseCommand(args)) {
            return;
        }
        
        final Device device = new Device(
             Bootstrap.MANUFACTURE, Bootstrap.MODEL, Bootstrap.SERIAL_NUMBER, SupportedBinding.UDP) {
             
             @Override
             public ExecuteResponse executeReboot() {
                 System.out.println("\n" + ">>>executeReboot");
                 //TODO add your code
                 return ExecuteResponse.success();
             }
             
             @Override
             public ExecuteResponse executeFactoryReset() {
                 System.out.println("\n" + ">>>executeFactoryReset");
                 //TODO add your code
                 return ExecuteResponse.success();
             }
             
             @Override
             protected ExecuteResponse executeResetErrorCode() {
                 System.out.println("\n" + ">>>executeResetErrorCode");
                 //TODO add your code
                 return super.executeResetErrorCode();
             }
      };
      
      ArtikCloudClient client = new ArtikCloudClient(deviceID, deviceToken, device);       
       
      /**
       * Must subclass and override the following methods:
       * 
       * downloadPackage(packageURI) - called when packageUri is written.
       * executeUpdateFirmware() - called when FirmwareUpdate State is in Downloaded state.
       * 
       */
      FirmwareUpdate sampleFirmwareUpdate = new FirmwareUpdate() {
            
           @Override
           public FirmwareUpdateResult downloadPackage(String packageUri) {
//               System.out.println("\n" + ">>>downloadPackage(String packageUri)." + "\n" 
//                                + "   Image url:" + packageUri);
               
               try {
                   System.out.println("\n>>>Downloading firmware image ...");
                   System.out.println("   Image url:" + packageUri);
                   downloadFile(packageUri, saveFirmwarePath);
               } catch (IOException exc) {
                   // Something went wrong when downloading
                   return FirmwareUpdateResult.OUT_OF_MEMORY; // an example of failures
               }

               //returning success here puts FirmwareUpdate State to "Downloaded".
               return FirmwareUpdateResult.SUCCESS;
           }
           
           @Override
           public FirmwareUpdateResult executeUpdateFirmware() {
//               System.out.println(">>>executeUpdateFirmware()");
               
               try {
                   System.out.println("\n>>>Simulate updating firmware...");
                   Thread.sleep(1000);
                   
                   System.out.println(">>>Updating completed with version " + firmwareVersionAfterUpdate + "");
              } catch (InterruptedException exc) {
                   // Something went wrong during installing new firmware
                   return FirmwareUpdateResult.FAILED; // an example of failures
               }
               
               // #1 In the real world, get the firmware version from the new image installed
               // #2 Must call this method. This ensures that this version number is passed to ARTIK Cloud when the cloud polls
               // the device for the lwm2m object 0/3/3.
               device.setFirmwareVersion(firmwareVersionAfterUpdate, true);
               
               return FirmwareUpdateResult.SUCCESS;
           }
       };
 
       //wire client with FirewareUpdate
       client.setFirmwareUpdate(sampleFirmwareUpdate);
       device.setFirmwareVersion(Bootstrap.INITIAL_FIRMWARE_VERSION, true);

       // Register
       client.start();

       // Sample just keeps the connection open for set period of time and will exit. 
       Thread.sleep(MAX_MS_KEEP_CONNECTION_OPEN);

       // De-Register
       client.stop(true);
       
       // Finish
       client.close();
       System.out.println("\n>>>Exiting....");
     }

     ////////////////////////////////////////////
    // Helper functions
    private static boolean succeedParseCommand(String args[]) {
        // java -jar target/OtaLwm2mClient-1.0.jar -d YOUR_DEVICE_ID -t YOUR_DEVICE_TOKEN -f FIRMWARE_VER_AFTER_UPDATE
        if (args.length != EXPECTED_ARGUMENT_NUMBER) {
            printUsage();
            return false; 
        }
        int index = 0;
        while (index < args.length) {
            String arg = args[index];
            if ("-d".equals(arg)) {
                ++index; // Move to the next argument the value of device id
                deviceID = args[index];
            } else if ("-t".equals(arg)) {
                ++index; // Move to the next argument the value of device token
                deviceToken = args[index];
            } else if ("-f".equals(arg)) {
                ++index; // Move to the next argument the value of firmware version after update
                firmwareVersionAfterUpdate = args[index];
            }
            ++index;
        }
        if (deviceToken == null || deviceID == null || firmwareVersionAfterUpdate == null) {
            printUsage();
            return false;
        }
        saveFirmwarePath = saveFirmwarePath + "firmware_" + firmwareVersionAfterUpdate;
        return true;
    }
    
    private static void printUsage() {
        System.out.println("Usage: " + OtaLwm2mClient.class.getSimpleName() + " -d YOUR_DEVICE_ID -t YOUR_DEVICE_TOKEN -f FIRMWARE_VER_AFTER_UPDATE");
    }

    /**
     * Downloads a file from a URL
     * @param fileURL HTTP URL of the file to be downloaded
     * @param saveFilePath path of the directory to save the file including the file name
     * @throws IOException
     */
     private static void downloadFile(String fileURL, String saveFilePath) throws IOException {
        final int BUFFER_SIZE = 4096;
        URL url = new URL(fileURL);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        int responseCode = httpConn.getResponseCode();

        // always check HTTP response code first
        if (responseCode == HttpURLConnection.HTTP_OK) {
            String disposition = httpConn.getHeaderField("Content-Disposition");
            String contentType = httpConn.getContentType();

            int contentLength = httpConn.getContentLength();
//            System.out.println("   Content-Type = " + contentType);
//            System.out.println("   Content-Disposition = " + disposition);
            System.out.println("   Content-Length = " + contentLength);

            // opens input stream from the HTTP connection
            InputStream inputStream = httpConn.getInputStream();
   
            // opens an output stream to save into file
            FileOutputStream outputStream = new FileOutputStream(saveFilePath);

            int bytesRead = -1;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();

            System.out.println(">>>Downloading completed and saved to " + saveFilePath);
        } else {
            System.out.println(">>>No file to download. Server replied HTTP code: " + responseCode);
        }
        httpConn.disconnect();
   }
}
