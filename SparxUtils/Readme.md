# Sample client application for Sparx EA API

Build the project with maven (e.g 'mvn clear install')
Use the **scripts/addConnectorBetweenPackages.bat** to attempt to add a connector of type DEPENDENCY between two Packages in a sample EAP file. The arguments expected by
the batch file are 

* **path-to-the-EAP-file**     the path the the EAP file
* **source_package_path**      as 'model-name/package1/.../package-source' 
* **destination_package_path** as 'model-name/package1/.../package-destination' 
* **connector-type**           refer to the Enterprise Architect Object Model @ Connector Class

## Case 1

With the sample.EAP provided in the scripts folder, if the command line would look like:

```
 addConnectorBetweenPackages.bat sample.EAP "Model/Component Model/Components/Sample1" "Model/Component Model/Components/Sample2" DEPENDENCY
```

This will end in failure. The API doesn't fail by itself by the connector is actually not added 

```
 java.lang.Exception: The connection has not been added
	at com.maiereni.sample.sparx.AddConnectorBetweenPackages.main(AddConnectorBetweenPackages.java:158)
```

## Case 2

With the sample.EAP provided in the scripts folder, if the command line would look like:

```
 addConnectorBetweenPackages.bat sample.EAP "Model/Component Model/Components/SampleMore/simple1/sample12" "Model/Component Model/Components/SampleMore/simple2/sample44" DEPENDENCY
```
then the API throws and exception

```
 java.lang.Exception: Cannot update connector as either the Start or the End object is NULL
	at org.sparx.Connector.comUpdate(Native Method)
	at org.sparx.Connector.Update(Connector.java:567)
```
