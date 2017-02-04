# LWM2M client performing OTA firmware update triggered from ARTIK Cloud 

This sample is java LWM2M client application. It acts on the OTA (Over the Air)firmware update triggered by manual operations in Device Management dashboard in ARTIK Cloud.

After completing this sample, you will learn the following objectives:

- Implement an LWM2M client based on ARTIK Cloud LWM2M SDK. The client can perform the OTA firmware update triggered by ARTIK Cloud.
- Upload and initiate a firmware update from ARTIK Cloud (via Device Management Dashboard) to your end device.

## Requirements
- ARTIK Cloud LWM2M SDK (Java)
- Java version >= 7

## Overview of workflows
- Create Device Type in ARTIK Cloud and Enable Device Management.    
- Upload an Image for firmware update and note the firmware version number you are setting.
- Add a Device to your account of the same Device Type.   Note the device credentials.
- In the sample code, add credentials to the SampleApp.java file with your device credentials and firmware version you have uploaded to ARTIK Cloud.  
- Run the SampleApp - this will create a connection to ARTIK Cloud and wait for a firmware update.
- In ARTIK Cloud Device Management Dashboard, select your connected devices you would like to apply a firmware update.  Use the dropdown menu and select "Execute OTA Update".
- Watch the logs in your SampleApp for relevant firmware update information.

## Setup

- Clone this sample application if you haven't already and import/setup project in your favorite IDE.

- Following instructions for the [ARTIK Cloud LWM2M JAVA SDK](https://github.com/artikcloud/artikcloud-lwm2m-java) & import the libraries into your project.   

  *Please note that this is different from the ARTIK Cloud Java SDK which is an SDK into our REST APIs.*

- Run the SampleApp.java appliation as a running process that will wait your FirmwareUpdate command.

- Follow additional steps from Workflow.

## Workflow

1. **Create a Device Type** (or use one you already own) from your [Developer Dashboard](#resources).   

2. Then **Enable <u>[Device Management Properties](#resources)</u>** for your Device Type.   

   *Note: You can do this in the [Device Type Dashboard](#resources)—> Select Your Device Type —> Select Device Management —> <u>Enable Device Properties</u>*

3. Then **Upload a Firmware Image** for your Device Type.   You can select any file for the sample.

   Note: You can do this in the [Device Type Dashboard](#resources)—> Select Your Device Type —> Select Device Management —> <u>OTA Updates</u> —> <u>Upload New Image</u>.

   Note: Make a note of the "version number" you are setting.   This is needed later.

4. **Connect a device** (create one, or use one you already own) of the Device Type you enabled earlier.  Note the device credentials for the device which you will need later.

   *Note: The <u>Device Id,</u> <u>Device Token</u> and <u>Device Type Id</u> are available in the [User Portal](#resources) —> Device Settings.   Generate the token in the settings page if you haven't already.*

   Note:  You can add a device from https://my.artik.cloud/ —> Devices

5. **Update the SampleApp.java** file by replacing with your Device ID and Device Token.

   ```
   public static final String DEVICE_ID = "your device id";
   public static final String DEVICE_TOKEN = "your device token";

   //image version you specificed in ARTIK Cloud
   public static final String FINAL_FIRMWARE_VERSION = "your image version";  
   ```

6. **Run the SampleApp.java code**.

   #### What's happening:

   The client application establishes a connection to ARTIK Cloud LWM2M server and is now waiting to receive an Over The Air Firmware Update from the server.   When the client application receives the Firmware Update command it will output relevant log outputs including the PackageUri which contains the Firmware Image path for the client to download.

7. You can initiate/execute an Firmware Update using the Device Management dashboard.

   1. Go to your Device Type —> Device Management Dashboard and select devices you want to initiate a firmware update.
   2. Then locate the <u>Execute</u> drop down menu and run the <u>OTA Update</u> command.   You should now see in your SampleApp relevant output logs regarding the Firmware Update.

   #### Here's the sample output from the sample application:

   ```java
   16:17:20.707 [Thread-1] INFO  cloud.artik.lwm2m.FirmwareUpdate - >>>downloadPackage(String packageUri).  Image url:https://api.artik.cloud/v1.1/updates/urls/2f079003c0e544739bf40ef4b3e01e4a/5414%2FR7chx8wlOpAshp4JiuVOJlt%2F0RiUR8rx1GAPQTrtagsVIGou2iriJiPi54aG6xhDDK4zZVpDhsOXe8XtlYlkGmknryUesqa1fUTVZQ%3D
   16:17:20.707 [Thread-1] INFO  cloud.artik.lwm2m.FirmwareUpdate - >>>simulate downloading ...
   16:17:21.709 [Thread-1] INFO  cloud.artik.lwm2m.FirmwareUpdate - >>>simluate downloading complete!
   16:17:22.940 [Thread-2] INFO  cloud.artik.lwm2m.FirmwareUpdate - >>>executeUpdateFirmware()
   16:17:22.940 [Thread-2] INFO  cloud.artik.lwm2m.FirmwareUpdate - >>>simulate updating ...
   16:17:23.945 [Thread-2] INFO  cloud.artik.lwm2m.FirmwareUpdate - >>>simulate updateding complete!
   ```

## Implementation

All that is required to run the code is overriding the following two methods from the FirmwareUpdate class.  This follows the LWM2M Specifications for Object 5 FirmwareUpdate Resources.

 - downloadPackage(String packageUri)
 - executeUpdateFirmware()

   ```javascript
   //SampleApp.java snip
   FirmwareUpdate sampleFirmwareUpdate = new FirmwareUpdate() {

     @Override
     public FirmwareUpdateResult downloadPackage(String packageUri) {
       LOGGER.info(">>>downloadPackage(String packageUri).  Image url:" + packageUri);
       // ... url to your image is provided in the packageURI.   Download the image
       // and returning a success below will put the FirmwareUpdateStatus to a 'DOWNLOADED'
       // state.
       return FirmwareUpdateResult.SUCCESS;
     }

     @Override
     public FirmwareUpdateResult executeUpdateFirmware() {
       LOGGER.info(">>>executeUpdateFirmware()");
       // ... update the firmware and set to new version.
       // version here should match the image version you provided in ARTIK Cloud
       // to see a successful status
       device.setFirmwareVersion(FINAL_FIRMWARE_VERSION, true);

       return FirmwareUpdateResult.SUCCESS;

     }
   };
   ```


Check out our documentation for more OTA (Over the Air) Updates  [OTA documentation](https://developer.artik.cloud/documentation/advanced-features/ota-updates.html).

## Resources

###### Referenced Code Samples / Documentation

| Description                     | Type          | Source                                   |
| ------------------------------- | ------------- | ---------------------------------------- |
| ARTIK Cloud LWM2M Client (Java) | Code/  SDK    | https://github.com/artikcloud/artikcloud-lwm2m-java |
| ARTIK Cloud LWM2M Client (C)    | Code / SDK    | https://github.com/artikcloud/artikcloud-lwm2m-c |
| ARTIK Cloud SDK                 | Code / SDK    | https://github.com/artikcloud/artikcloud-java |
| Documentation: LWM2M            | Documentation | https://developer.artik.cloud/documentation/advanced-features/manage-devices-using-lwm2m.html) |
| OTA Updates                     | Documenation  | https://developer.artik.cloud/documentation/advanced-features/ota-updates.html |
| Developer Dashboard:            | Dashboard     | https://developer.artik.cloud/dashboard  |
| User Portal                     | Dashboard     | https://my.artik.cloud                   |
| API Console                     | API-Console   | https://developer.artik.cloud/api-console/ |

### <u>Dashboards</u>

#### **User Portal** - https://my.artik.cloud

- **User Portal —> Devices**: for adding devices and generating device token

#### **Device Type Dashboard** - https://developer.artik.cloud/dashboard/devicetypes

- **Device Type —> Device Management —> Properties**:  Enable Server and Device Properties for Device Management
- **Device Type —> Device Management —> Tasks**:  View Task Status

More about ARTIK Cloud
----------------------

If you are not familiar with ARTIK Cloud, we have extensive documentation at https://developer.artik.cloud/documentation

The full ARTIK Cloud API specification can be found at https://developer.artik.cloud/documentation/api-reference/

Check out advanced sample applications at https://developer.artik.cloud/documentation/samples/

To create and manage your services and devices on ARTIK Cloud, create an account at https://developer.artik.cloud

Also see the ARTIK Cloud blog for tutorials, updates, and more: http://artik.io/blog/cloud


License and Copyright
---------------------

Licensed under the Apache License. See [LICENSE](LICENSE).

Copyright (c) 2017 Samsung Electronics Co., Ltd.
