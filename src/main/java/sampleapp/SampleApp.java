package sampleapp;

import org.eclipse.leshan.core.response.ExecuteResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cloud.artik.lwm2m.ArtikCloudClient;
import cloud.artik.lwm2m.Device;
import cloud.artik.lwm2m.FirmwareUpdate;
import cloud.artik.lwm2m.enums.FirmwareUpdateResult;
import cloud.artik.lwm2m.enums.SupportedBinding;

public class SampleApp {

	private static final String DEVICE_ID = "YOUR DEVICE ID";
	private static final String DEVICE_TOKEN = "YOUR DEVICE TOKEN";
	
	// Track the firmware version number at the device
	private static int currentFirmwareVer;
	
	//generic time to keep the sample app running
	private static final long MAX_KEEP_CONNECTION_OPEN = 600000;
	
	private static class Bootstrap {
		private static final String MANUFACTURE = "Sample Company XYZ";
		private static final String MODEL = "XYZ-1";
		private static final String SERIAL_NUMBER = "1234567890";
		private static final int INITIAL_FIRMWARE_VERSION = 0;
	}
	
    public static void main (String args[]) throws InterruptedException {
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
      
      ArtikCloudClient client = new ArtikCloudClient(DEVICE_ID, DEVICE_TOKEN, device);       
       
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
               System.out.println("\n" + ">>>downloadPackage(String packageUri)." + "\n" 
        	                    + "   Image url:" + packageUri);
               
               try {
            	   System.out.println(">>>simulate downloading ...");
                   Thread.sleep(1000);
                   System.out.println(">>>simluate downloading complete!");
               } catch (InterruptedException exc) {
                   // Something went wrong when downloading
            	   return FirmwareUpdateResult.NO_STORAGE; // an example of failures
               }

               //returning success here puts FirmwareUpdate State to "Downloaded".
               return FirmwareUpdateResult.SUCCESS;
           }
           
           @Override
           public FirmwareUpdateResult executeUpdateFirmware() {
               System.out.println(">>>executeUpdateFirmware()");
               
               try {
            	   System.out.println(">>>simulate updating ...");
                   Thread.sleep(1000);
                   // In the real world, get the firmware version from the new image installed
                   ++currentFirmwareVer; 
                   System.out.println(">>>simulate updating complete with version " + currentFirmwareVer + "");
              } catch (InterruptedException exc) {
                   // Something went wrong during installing new firmware
            	   return FirmwareUpdateResult.FAILED; // an example of failures
               }
               
               // Must call this method. This ensures that this version number is passed to ARTIK Cloud when the cloud polls
               // the device for the lwm2m object 0/3/3.
               device.setFirmwareVersion(String.valueOf(currentFirmwareVer), true);
               return FirmwareUpdateResult.SUCCESS;
           }
       };
 
       currentFirmwareVer = Bootstrap.INITIAL_FIRMWARE_VERSION;

       //wire client with FirewareUpdate
       client.setFirmwareUpdate(sampleFirmwareUpdate);
       device.setFirmwareVersion(String.valueOf(currentFirmwareVer), true);

       // Register
       client.start();

       // Sample just keeps the connection open for set period of time and will exit. 
       Thread.sleep(MAX_KEEP_CONNECTION_OPEN);

       // De-Register
       client.stop(true);
       
       // Finish
       client.close();
       System.out.println("\n>>>Exiting....");
	    	 
     }
    
     
}
