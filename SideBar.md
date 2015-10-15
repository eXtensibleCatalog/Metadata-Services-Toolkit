  * [Try the Demo](http://demo.extensiblecatalog.org/)
  * [Downloads](Downloads.md)
  * Installing the Toolkit
    * [Hardware Requirements](HardwareRequirements.md)
    * Installing 3rd Party Tools
      * [Installing the Java SE Development Kit](InstallandCustomizejava.md)
      * [Installing and Customizing Tomcat](InstallandCustomizetomcat.md)
      * [Installing the MySQL Server](InstallMysql.md)
    * Installing the Metadata Services Toolkit
      * [In Windows](InstallinWindows.md)
      * [In Unix](InstallinUnix.md)
    * Configuring
      * [Configuring the MST](MstConfig.md)
      * [Configuring Tomcat](TomcatConfig.md)
      * Configuring MySQL
        * [MySQL Permissions](MysqlPermissions.md)
        * [MySQL Configurations](MySQLConfigurations.md)
      * [Configuring server](ServerConfig.md)
    * Starting the MST
      * [In Windows](StartinWindows.md)
      * [In Unix](StartinUnix.md)
    * [Uninstalling and Reinstalling the MST](MstUninstall.md)
    * [Upgrading the MST](MstUpgrade.md)
    * [Useful Info](UsefulInfo.md)
  * Using the Toolkit
    * [Setting Up the MST](MstSetup.md)
    * [Managing Users](UserManagement.md)
    * [Managing the Repository](RepositoryManagement.md)
    * [Harvesting Metadata](MetadataHarvesting.md)
    * [Optimizing the SOLR Index](OptimizeSolrIndex.md)
    * [Adding and Upgrading Metadata Services](Metadata.md)
    * [Controlling the Data Flow](DataflowControl.md)
    * [Browsing Metadata Records](RecordsBrowsing.md)
    * [Viewing Logs](LogViewing.md)
    * [Verifying Record Counts](verifyRecordCounts.md)
    * [Status Bar](StatusBar.md)
  * Services
    * [What is a service?](ServicesExplained.md)
    * [What are Configuration 1 and Configuration 2?](https://docs.google.com/document/d/1aZ2o1b3oUclCmeAC1G7kcZ8sWUiDqZ-ZvKfoyIY5mSQ/edit)
    * XC MARCXML Normalization
      * [Intro to Normalization Service](NormserviceIntro.md)
      * [Configuring the Normalization Service](NormConfiguration.md)
      * [Bibliographic Record Steps](BibrecordSteps.md)
      * [Holdings Record Steps](HoldrecordSteps.md)
      * [Normalization Recommended Default Settings](https://docs.google.com/spreadsheet/ccc?key=0ArAewjrTTKSUdFhwYWtlMTJpQWJqc2ptdFJtei05Wnc)
      * [9XX fields for MARCXML Normalization](https://docs.google.com/spreadsheet/ccc?key=0ArAewjrTTKSUdFlZR09tZlhqb3NIcXdXWHFnZC1melE&hl=en_US)
      * [Leader 06 mappings](https://docs.google.com/spreadsheet/ccc?key=0ArAewjrTTKSUdE9aMHZVLTEzbXJaU0FXOW1kUnI3SEE&hl=en_US)
      * [007/00 mappings](https://docs.google.com/spreadsheet/ccc?key=0ArAewjrTTKSUdGxyZWgxa0h0TUE3aHg2TlctNWVfNkE&hl=en_US)
      * [008/23 mapping](https://docs.google.com/spreadsheet/ccc?key=0ArAewjrTTKSUdGY2OGZUZVpjWTZpcFNHUDNaYVJjWnc&hl=en_US)
      * [MARCXML Normalization 2 Embedded Holdings Staging](https://docs.google.com/spreadsheet/ccc?key=0ArAewjrTTKSUdDZtMVZmNW9wbDBVZEtKWWRvWHc0TEE&hl=en_US)
    * MARCXML to XC Transformation
      * [Intro to Transformation Service](TranserviceIntro.md)
      * [Transformation Service Default Settings](https://docs.google.com/spreadsheet/ccc?key=0ArAewjrTTKSUdGwxVVBIeEY5Z0lpN0daRHhzUWY3WlE)
      * [Transformation Service Mapping Table](https://docs.google.com/spreadsheet/ccc?key=0ArAewjrTTKSUdFVwODhobWl5Q3RCdWl0Z1RkdFZLUXc&hl=en_US)
      * [Mapping 7XX with 2nd indicator 2](https://docs.google.com/document/d/1I2k17F_2Qqen1VIa-9MWqW5NuXQ75Bu3Z3D67GD14Us/edit?hl=en_US)
      * [XC Role Translation Table](https://docs.google.com/spreadsheet/ccc?key=0ArAewjrTTKSUdElSVUQ2Q25yeHdOQkwxUjRaSnJBX1E&hl=en_US)
      * [XC Subject Attributes](https://docs.google.com/spreadsheet/ccc?key=0ArAewjrTTKSUdEd3ZGVLMXlaZXFBd0tTOGQ3eGhOS1E&hl=en_US)
      * [Authority Link Attributes](https://docs.google.com/spreadsheet/ccc?key=0ArAewjrTTKSUdG9NREdpQjFKN1I0RGpuZzVQRlhwaHc&hl=en_US)
      * [Identifier Tokens](https://docs.google.com/spreadsheet/ccc?key=0ArAewjrTTKSUdHhtNXRPbmU2Q3pRaDk2VXlxYk1KX1E&hl=en_US)
    * DC to XC Transformation
      * [Mappings](Mappings.md)
      * [Example Input and Output Records](ExampleRecords.md)
    * MARC Aggregation
      * [Intro](MarcAggIntro.md)
      * [Match Points (w/ error cases)](MarcAggMatchPointsAndErrorCases.md)
      * [Merging](MarcAggMerging.md)
      * [Implementation Design](MarcAggArchitecture.md)
    * Multiple Instances of the Same Service
      * [How to install multiple instances of the same service](MultipleServiceInstances.md)
    * Harvesting from an MST Service
      * [How to harvest from an MST Service](HarvestingFromMSTServices.md)
  * How To Implement a Service
    * [Quick and Dirty Tutorial](HowToImplementService.md)
    * [Details on the process method](http://www.extensiblecatalog.org/doc/MST/api/xc/mst/services/impl/GenericMetadataService.html#process%28xc.mst.bo.record.InputRecord%29)
    * [Testing your service](ServiceFileSystemTesting.md)
    * AdvancedFeatures
    * [Contribute to a core service](CoreServices.md)
  * [About the XC Schema](XCSchema.md)
  * [MST Frequently Asked Questions](https://docs.google.com/document/d/1adiBJNVyq-q1SVOHkj4fvWgx9LozZY4j2blisZqPY4k/edit)
  * [Performance Results](PerformanceResults.md)
    * RecordBreakdown
    * [MySQLCustomizations](MySQLCustomizations.md)
  * [Release Notes](ReleaseNotes.md)
  * [Next Coding Period Summary](CodeSprints.md)
  * [Glossary](GeneralGlossary.md)
  * Developer ScratchPad
    * ServerChart
    * Transformation 1.0
      * TransformationDocumentationNotes
      * new
        * TransformationDocumentation
      * old
        * AdditionalWorksAndExpressions
        * Transformation Service Documentation
        * TransformationServiceSteps
          * [Mapping Instructions](TransformationServiceStepsInstructions.md)
          * [Bibliographic](TransformationServiceStepsBib.md)
          * [Holdings](TransformationServiceStepsHoldings.md)
          * [VoyagerItem](TransformationServiceStepsVoyagerItem.md)
          * [MARCHoldingsItemData](TransformationServiceStepsMARCHoldingsItemData.md)
        * XcRoleTranslationTable
    * AggregationServices
      * MarcAggregation
        * [MySQL Tuning for MAS](MySQLTuning.md)
        * [Scratch Pad](MarcAggScratchPad.md)
      * TransformationTwoPointOh
      * old
        * FirstIteration
        * PriorDesign
    * [PackagingMST](PackagingMST.md)
    * 1.0 Decisions
      * RepositoryUpdatesDeletes
      * RecordCountProblems
      * [UIChanges](UIChanges.md)
      * ServiceUpdates
      * [LogsUI](LogsUI.md)
    * ReleaseWork
    * QuickInstallNotes
    * MST Implementation Details
      * OaiIdIndexAlgorithm
      * CacheDetails
      * MessageHandling
      * ServiceTests
      * ProcessingStepsExplained
      * [ResumptionToken->completeListSize](ResumptionToken#completeListSize.md)
      * UpdateDelete
      * OaiPmhImpl
    * record counts
      * RecordCountsOnePtTwoPtOne
      * [in production](RecordCounts.md)
      * [how to log and display](LoggingRecordCounts.md)
      * RecordCountsOnePtZero
      * RecordCountTestRestarted
      * UrRecordCounts
      * RecordCountTesting
      * TransformationWackiness
    * OaiImplementation
    * Testing
      * [randys-30](Randys30.md)
      * RegressionTests
    * QuickRef
    * UnicodeNormalization
    * LoggingHelp
    * CodeFormatPolicy
    * SvnBranchingStrategy
    * MultipleEclipseWorkspaces
    * DeleteReaddServiceForRetest
    * FileHarvests
    * CharsetEncodingWithEric
    * DrupalSolrOptimization
      * WorkPlan
      * MetricsForAssessment
      * IdeasForImprovement
      * RandomNotes
  * [Wiki en español](WikiEspanol.md)
    * Servicios
      * [Qué es un Servicio de Metadatos?](ExplicacionServicios.md)
      * Servicio de Normalización XC MARCXML
        * [Introducción al Servicio de Normalización](IntroServNorm.md)
        * [Cómo configurar el Servicio de Normalización](ConfigNorm.md)
        * [Servicio de Normalización para Bibliográficos MARCXML](PasosNorm.md)
        * [Servicio de Normalización para Ejemplares MARCXML](PasosNormEj.md)
      * Servicio de Transformación MARCXML a Esquema XC
        * [Introducción al Servicio de Transformación](IntroServTrans.md)
        * [Modificaciones en el Servicio de Transformación](ModTrans.md)
      * Servicio de Agregación MARC
        * [Introducción al Servicio de Agregación MARC](IntroAgreg.md)
        * [Servicio de Agregación MARCXML. Glosario](MASGlosario.md)
        * [Servicio de Agregación MARCXML. Puntos de comparación (y errores detectados)](MASComp.md)