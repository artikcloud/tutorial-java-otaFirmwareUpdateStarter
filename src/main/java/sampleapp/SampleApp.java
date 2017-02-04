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

	public static final String DEVICE_ID = "YOUR DEVICE ID";
	public static final String DEVICE_TOKEN = "YOUR DEVICE TOKEAN";
	
	//image version you specificed in ARTIK Cloud
	public static final String FINAL_FIRMWARE_VERSION = "YOUR FIRMWARE IMAGE VERSION";  
	
	//generic time to keep the sample app running
	public static final long MAX_KEEP_CONNECTION_OPEN = 600000;
	
	protected static class Bootstrap {
		private static final String Manufacture = "Sample Company XYZ";
		private static final String Model = "XYZ-1";
		private static final String SerialNumber = "1234567890";
		private static final String InitialFirmwareVersion = "0.001";
	}
	
    public static void main (String args[]) throws InterruptedException {
    	 
      final Logger LOGGER = LoggerFactory.getLogger(SampleApp.class);
    	     
      final Device device = new Device(
    		 Bootstrap.Manufacture, Bootstrap.Model, Bootstrap.SerialNumber, SupportedBinding.UDP) {
             
             @Override
             public ExecuteResponse executeReboot() {
                 LOGGER.info(">>>executeReboot");
                 return ExecuteResponse.success();
             }
             
             @Override
             public ExecuteResponse executeFactoryReset() {
                 LOGGER.info(">>>executeFactoryReset");
                 return ExecuteResponse.success();
             }
             
             @Override
             protected ExecuteResponse executeResetErrorCode() {
                 LOGGER.info(">>>executeResetErrorCode");
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
        	   
               LOGGER.info(">>>downloadPackage(String packageUri).  Image url:" + packageUri);
               
               try {
            	   LOGGER.info(">>>simulate downloading ...");
                   Thread.sleep(1000);
                   LOGGER.info(">>>simluate downloading complete!");
               } catch (InterruptedException exc) {
                   
               }

               //returning success here puts FirmwareUpdate State to "Downloaded".
               return FirmwareUpdateResult.SUCCESS;
           }
           
           @Override
           public FirmwareUpdateResult executeUpdateFirmware() {
               LOGGER.info(">>>executeUpdateFirmware()");
               
               try {
            	   LOGGER.info(">>>simulate updating ...");
                   Thread.sleep(1000);
                   LOGGER.info(">>>simulate updateding complete!");
               } catch (InterruptedException exc) {
                   
               }
               
               device.setFirmwareVersion(FINAL_FIRMWARE_VERSION, true);
              
               return FirmwareUpdateResult.SUCCESS;
               
           }
           
       };
       
       //wire client with FirewareUpdate
       client.setFirmwareUpdate(sampleFirmwareUpdate);
       device.setFirmwareVersion(Bootstrap.InitialFirmwareVersion, true);
       
       // Register
       client.start();
       
       // Sample just keeps the connection open for set period of time and will exit. 
       Thread.sleep(MAX_KEEP_CONNECTION_OPEN);

       // De-Register
       client.stop(true);
       
       // Finish
       client.close();
	    	 
     }
}
