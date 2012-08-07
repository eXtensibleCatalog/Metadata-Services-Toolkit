/**
 * Copyright (c) 2009 eXtensible Catalog Organization
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
 * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
 * website http://www.extensiblecatalog.org/.
 *
 */

package xc.mst.services.normalization;

/**
 * This class defines several constants as public static final variables
 * which may be used throughout the NormalizationService
 *
 * @author Eric Osisek
 */
public class NormalizationServiceConstants {
    // *******************************************************************
    // Normalization Step names (to check whether or not a step is enabled)
    // *******************************************************************

    /**
     * Parameter for looking up the whether or not the SourceOfMARCOrganizationCode normalization step is enabled.
     */
    public static final String CONFIG_SOURCE_OF_MARC_ORG = "SourceOfMARCOrganizationCode";
    public static final String CONFIG_ORG_CODE = "OrganizationCode";
    public static final String CONFIG_SOURCE_OF_9XX_FIELDS = "SourceOf9XXFields";

    // XC's org code
    public static final String XC_SOURCE_OF_MARC_ORG = "NyRoXCO";

    public static final String MSG_MISSING_001_FIELD = "Missing 001 field";
    public static final String MSG_UNEXPECTED_VALUE_001_FIELD = "Unexpected value in 001";
    public static final String MSG_MISSING_003_FIELD = "Missing 003 field";
    public static final String MSG_UNEXPECTED_003_FIELD = "Unexpected 003 field";
    public static final String MSG_INCORRECT_003_FIELD = "Incorrect value in 003 field";
    
    
    /**
     * Parameter for looking up the whether or not the RemoveOCoLC003 normalization step is enabled.
     */
    public static final String CONFIG_ENABLED_REMOVE_OCOLC_003 = "RemoveOCoLC003";
    
    /*
     * Parameters for decided whether or not this is a valid 004 field
     */
    public static final String CONFIG_VALID_FIRST_CHAR_014 = "ValidFirstChar014";
    
    /*
     * Parameters for decided whether or not this is an invalid 004 field
     */
    public static final String CONFIG_INVALID_FIRST_CHAR_014 = "InvalidFirstChar014";

    /**
     * Parameter for looking up the whether or not the RemoveOCoLC003 normalization step is enabled.
     */
    public static final String CONFIG_ENABLED_REPLACE_014 = "Replace014";
    
    /**
     * Parameter for looking up the whether or not the DCMIType06 normalization step is enabled.
     */
    public static final String CONFIG_ENABLED_DCMI_TYPE_06 = "DCMIType06";

    /**
     * Parameter for looking up the whether or not the Leader06Vocab normalization step is enabled.
     */
    public static final String CONFIG_ENABLED_LEADER_06_VOCAB = "Leader06Vocab";

    /**
     * Parameter for looking up the whether or not the 007Vocab06 normalization step is enabled.
     */
    public static final String CONFIG_ENABLED_007_VOCAB_06 = "007Vocab06";

    /**
     * Parameter for looking up the whether or not the ModeOfIssuance normalization step is enabled.
     */
    public static final String CONFIG_ENABLED_MODE_OF_ISSUANCE = "ModeOfIssuance";

    /**
     * Parameter for looking up the whether or not the ControlNumber normalization step is enabled.
     */
    public static final String CONFIG_ENABLED_CONTROL_NUMBER = "ControlNumber";

    /**
     * Parameter for looking up the whether or not the MoveMARCOrgCode normalization step is enabled.
     */
    public static final String CONFIG_ENABLED_MOVE_MARC_ORG_CODE = "MoveMARCOrgCode";

    /**
     * Parameter for looking up the whether or not the MoveMARCOrgCode normalization step is enabled.
     */
    public static final String CONFIG_MOVE_ALL_MARC_ORG_CODES = "MoveMARCOrgCode_moveAll";

    /**
     * Parameter for looking up the whether or not the DCMIType0007 normalization step is enabled.
     */
    public static final String CONFIG_ENABLED_DCMI_TYPE_00_07 = "DCMIType007";

    /**
     * Parameter for looking up the whether or not the 007Vocab normalization step is enabled.
     */
    public static final String CONFIG_ENABLED_007_VOCAB = "007Vocab";

    /**
     * Parameter for looking up the whether or not the 007SMDVocab normalization step is enabled.
     */
    public static final String CONFIG_ENABLED_007_SMD_TYPE = "007SMDVocab";

    /**
     * Parameter for looking up the whether or not the FictionOrNonfiction normalization step is enabled.
     */
    public static final String CONFIG_ENABLED_FICTION_OR_NONFICTION = "FictionOrNonfiction";

    /**
     * Parameter for looking up the whether or not the 008DateRange normalization step is enabled.
     */
    public static final String CONFIG_ENABLED_008_DATE_RANGE = "008DateRange";

    /**
     * Parameter for looking up the whether or not the LanguageSplit normalization step is enabled.
     */
    public static final String CONFIG_ENABLED_LANGUAGE_SPLIT = "LanguageSplit";

    /**
     * Parameter for looking up the whether or not the LanguageTerm normalization step is enabled.
     */
    public static final String CONFIG_ENABLED_LANGUAGE_TERM = "LanguageTerm";

    /**
     * Parameter for looking up the whether or not the 008Audience normalization step is enabled.
     */
    public static final String CONFIG_ENABLED_008_AUDIENCE = "008Audience";

    /**
     * Parameter for looking up the whether or not the 008Audience normalization step is enabled.
     */
    public static final String CONFIG_ENABLED_006_AUDIENCE = "006Audience";

    /**
     * Parameter for looking up the whether or not the 006FormOfItem normalization step is enabled.
     */
    public static final String CONFIG_ENABLED_006_FORM = "006FormOfItem";

    /**
     * Parameter for looking up the whether or not the 008FormOfItem normalization step is enabled.
     */
    public static final String CONFIG_ENABLED_008_FORM = "008FormOfItem";

    /**

     * Parameter for looking up the whether or not the 008Thesis normalization step is enabled.
     */
    public static final String CONFIG_ENABLED_008_THESIS = "008Thesis";

    /**
     * Parameter for looking up the whether or not the ISBNCleanup normalization step is enabled.
     */
    public static final String CONFIG_ENABLED_ISBN_CLEANUP = "ISBNCleanup";

    /**
     * Parameter for looking up the whether or not the ISBNCleanup normalization step is enabled.
     */
    public static final String CONFIG_ENABLED_LCCN_CLEANUP = "LCCNCleanup";

    /**
     * Parameter for looking up the whether or not the ISBNCleanup normalization step is enabled.
     */
    public static final String CONFIG_ENABLED_ISBN_MOVE = "024ISBNMove";

    /**
     * Parameter for looking up the whether or not the SupplyMARCOrgCode normalization step is enabled.
     */
    public static final String CONFIG_ENABLED_SUPPLY_MARC_ORG_CODE = "SupplyMARCOrgCode";

    /**
     * Parameter for looking up the whether or not the Fix035 normalization step is enabled.
     */
    public static final String CONFIG_ENABLED_FIX_035 = "Fix035";

    /**
     * Parameter for looking up the whether or not the Dedup035 normalization step is enabled.
     */
    public static final String CONFIG_ENABLED_DEDUP_035 = "Dedup035";

    /**
     * Parameter for looking up the whether or not the RoleAuthor normalization step is enabled.
     */
    public static final String CONFIG_ENABLED_ROLE_AUTHOR = "RoleAuthor";

    /**
     * Parameter for looking up the whether or not the RoleComposer normalization step is enabled.
     */
    public static final String CONFIG_ENABLED_ROLE_COMPOSER = "RoleComposer";

    /**
     * Parameter for looking up the whether or not the CreatorDisplay normalization step is enabled.
     */
    public static final String CONFIG_ENABLED_CREATOR_DISPLAY = "CreatorDisplay";

    /**
     * Parameter for looking up the whether or not the UniformTitle normalization step is enabled.
     */
    public static final String CONFIG_ENABLED_UNIFORM_TITLE = "UniformTitle";

    /**
     * Parameter for looking up the whether or not the WorkTitleDisplay normalization step is enabled.
     */
    public static final String CONFIG_ENABLED_WORK_TITLE_DISPLAY = "WorkTitleDisplay";

    /**
     * Parameter for looking up the whether or not the SeriesDisplay normalization step is enabled.
     */
    public static final String CONFIG_ENABLED_SERIES_DISPLAY = "SeriesDisplay";

    /**
     * Parameter for looking up the whether or not the LCSHDisplay normalization step is enabled.
     */
    public static final String CONFIG_ENABLED_LCSH_DISPLAY = "LCSHDisplay";

    /**
     * Parameter for looking up the whether or not the NRUGenre normalization step is enabled.
     */
    public static final String CONFIG_ENABLED_NRU_GENRE = "NRUGenre";

    /**
     * Parameter for looking up the whether or not the NRUDatabaseGenre normalization step is enabled.
     */
    public static final String CONFIG_ENABLED_NRU_DATABASE_GENRE = "NRUDatabaseGenre";

    /**
     * Parameter for looking up the whether or not the TopicSplit normalization step is enabled.
     */
    public static final String CONFIG_ENABLED_TOPIC_SPLIT = "TopicSplit";

    /**
     * Parameter for looking up the whether or not the ChronSplit normalization step is enabled.
     */
    public static final String CONFIG_ENABLED_CHRON_SPLIT = "ChronSplit";

    /**
     * Parameter for looking up the whether or not the GeogSplit normalization step is enabled.
     */
    public static final String CONFIG_ENABLED_GEOG_SPLIT = "GeogSplit";

    /**
     * Parameter for looking up the whether or not the GenreSplit normalization step is enabled.
     */
    public static final String CONFIG_ENABLED_GENRE_SPLIT = "GenreSplit";

    /**
     * Parameter for looking up the whether or not the ContributorDisplay normalization step is enabled.
     */
    public static final String CONFIG_ENABLED_CONTRIBUTOR_DISPLAY = "ContributorDisplay";

    /**
     * Parameter for looking up the whether or not the RelatedDisplay normalization step is enabled.
     */
    public static final String CONFIG_ENABLED_RELATED_DISPLAY = "RelatedDisplay";

    /**
     * Parameter for looking up the whether or not the CreatorAnalytics normalization step is enabled.
     */
    public static final String CONFIG_ENABLED_CREATOR_ANALYTIC = "CreatorAnalytic";

    /**
     * Parameter for looking up the whether or not the RelTitleDisplay normalization step is enabled.
     */
    public static final String CONFIG_ENABLED_REL_TITLE_DISPLAY = "RelTitleDisplay";

    /**
     * Parameter for looking up the whether or not the DedupDCMIType normalization step is enabled.
     */
    public static final String CONFIG_ENABLED_DEDUP_DCMI_TYPE = "DedupDCMIType";

    /**
     * Parameter for looking up the whether or not the Dedup007Vocab normalization step is enabled.
     */
    public static final String CONFIG_ENABLED_DEDUP_007_VOCAB = "Dedup007Vocab";

    /**
     * Parameter for looking up the whether or not the BibLocationName normalization step is enabled.
     */
    public static final String CONFIG_ENABLED_BIB_LOCATION_NAME = "BibLocationName";

    /**
     * Parameter for looking up the whether or not the IIILocationName normalization step is enabled.
     */
    public static final String CONFIG_ENABLED_III_LOCATION_NAME = "IIILocationName";

    /**
     * Parameter for looking up the whether or not the remove945Field normalization step is enabled.
     */
    public static final String CONFIG_ENABLED_REMOVE_945_FIELD = "remove945Field";

    /**
     * Parameter for looking up the whether or not the SeperateName normalization step is enabled.
     */
    public static final String CONFIG_ENABLED_SEPARATE_NAME = "SeparateName";

    /**
     * Parameter for looking up the whether or not the Dedup9XX normalization step is enabled.
     */
    public static final String CONFIG_ENABLED_DEDUP_9XX = "Dedup9XX";

    /**
     * Parameter for looking up the whether or not the TitleArticle normalization step is enabled.
     */
    public static final String CONFIG_ENABLED_TITLE_ARTICLE = "TitleArticle";

    /**
     * Parameter for looking up the whether or not the 035LeadingZero normalization step is enabled.
     */
    public static final String CONFIG_ENABLED_035_LEADING_ZERO = "035LeadingZero";

    /**
     * Parameter for looking up the whether or not the HoldingsLocationName normalization step is enabled.
     */
    public static final String CONFIG_ENABLED_HOLDINGS_LOCATION_NAME = "HoldingsLocationName";

    /**
     * Parameter for looking up the whether or not the LocationLimitName normalization step is enabled.
     */
    public static final String CONFIG_ENABLED_LOCATION_LIMIT_NAME = "LocationLimitName";

    /**
     * Parameter for looking up the whether or not the Fix035Code9 normalization step is enabled.
     */
    public static final String CONFIG_ENABLED_FIX_035_CODE_9 = "Fix035Code9";

    /**
     * Parameter for looking up the whether or not the Add014Source normalization step is enabled.
     */
    public static final String CONFIG_ENABLED_014_SOURCE = "Add014Source";

    // *******************************************************************
    // Properties file names
    // *******************************************************************

    /**
     * Properties file configuring normalization steps are enabled
     */
    public static final String PROPERTIES_ENABLED_STEPS = "enabledNormalizationSteps.properties";

    /**
     * Properties file mapping the leader06 value to a DCMI Type
     */
    public static final String PROPERTIES_LEADER06_DCMI_TYPE_MAPPING = "leader06ToDcmiType.properties";

    /**
     * Properties file mapping the leader06 value to a MARC vocabulary term
     */
    public static final String PROPERTIES_LEADER06_MARC_VOCABULARY_MAPPING = "leader06ToMarcVocab.properties";

    /**
     * Properties file mapping the leader06 value to the full type
     */
    public static final String PROPERTIES_LEADER06_FULL_TYPE_MAPPING = "leader06ToFullType.properties";

    /**
     * Properties file mapping the leader07 value to a mode of issuance
     */
    public static final String PROPERTIES_LEADER07_MODE_OF_ISSUANCE_MAPPING = "leader07ToModeOfIssuance.properties";

    /**
     * Properties file mapping the 007 offset 00 value to a DCMI Type
     */
    public static final String PROPERTIES_007_00_DCMI_TYPE_MAPPING = "field007Offset00ToDcmiType.properties";

    /**
     * Properties file mapping the 007 offset 00 and 01 values to the full type
     */
    public static final String PROPERTIES_007_00_007_FULL_TYPE_MAPPING = "field007Offset00ToFullType.properties";

    /**
     * Properties file mapping the 007 offset 00 and 01 values to an SMD type
     */
    public static final String PROPERTIES_007_SMD_TYPE_MAPPING = "field007ToSmdType.properties";

    /**
     * Properties file mapping language codes to languages
     */
    public static final String PROPERTIES_LANGUAGE_CODE_TO_LANGUAGE = "languageCodeToLanguage.properties";

    /**
     * Properties file mapping the 008 offset 22 value to the intended audience
     */
    public static final String PROPERTIES_008_22_TO_AUDIENCE = "field008Offset22ToAudience.properties";

    /**
     * Properties file mapping the 008 offset 22 value to the intended audience
     */
    public static final String PROPERTIES_006_008_23_TO_FORM = "formFrom006_008Properties.properties";

    /**
     * Properties file mapping location codes to the full name of the location
     */
    public static final String PROPERTIES_LOCATION_CODE_TO_LOCATION_TERM = "locationCodeToLocation.properties";

    // *******************************************************************
    // 9xx fields
    // *******************************************************************

    /**
     * The 9xx field for the DCMI Type
     */
    public static final String FIELD_9XX_DCMI_TYPE = "931";

    /**
     * The 9xx field for the 007 MARC Vocabulary
     */
    public static final String FIELD_9XX_007_MARC_VOCAB = "932";

    /**
     * The 9xx field for the 007 Vocabulary
     */
    public static final String FIELD_9XX_007_VOCAB = "933";

    /**
     * The 9xx field for the SMD Vocabulary
     */
    public static final String FIELD_9XX_SMD_VOCAB = "934";

    /**
     * The 9xx field for the mode of issuance
     */
    public static final String FIELD_9XX_MODE_OF_ISSUANCE = "935";

    /**
     * The 9xx field for the fiction or nonfiction field
     */
    public static final String FIELD_9XX_FICTION_OR_NONFICTION = "937";

    /**
     * The 9xx field for the date range
     */
    public static final String FIELD_9XX_DATE_RANGE = "939";

    /**
     * The 9xx field for the language split results
     */
    public static final String FIELD_9XX_LANGUAGE_SPLIT = "941";

    /**
     * The 9xx field for the language terms
     */
    public static final String FIELD_9XX_LANGUAGE_TERM = "943";

    /**
     * The 9xx field for the audience
     */
    public static final String FIELD_9XX_AUDIENCE = "945";
    
    /**
     * The 9xx field for the form
     */
    public static final String FIELD_9XX_FORM = "977";

    /**
     * The 9xx field for the cleaned up ISBN
     */
    public static final String FIELD_9XX_CLEAN_ISBN = "947";

    /**
     * The 9xx field for the creator display
     */
    public static final String FIELD_9XX_CREATOR_DISPLAY_CONTENT = "951";

    /**
     * The 9xx field for the work title display
     */
    public static final String FIELD_9XX_WORK_TITLE_DISPLAY_CONTENT = "953";

    /**
     * The 9xx field for the series display
     */
    public static final String FIELD_9XX_SERIES_DISPLAY_CONTENT = "955";

    /**
     * The 9xx field for the LCSH display
     */
    public static final String FIELD_9XX_LCSH_DISPLAY_CONTENT = "961";

    /**
     * The 9xx field for the topic split
     */
    public static final String FIELD_9XX_TOPIC_SPLIT = "965";

    /**
     * The 9xx field for the chron split
     */
    public static final String FIELD_9XX_CHRON_SPLIT = "963";

    /**
     * The 9xx field for the geog split
     */
    public static final String FIELD_9XX_GEOG_SPLIT = "967";

    /**
     * The 9xx field for the genre split
     */
    public static final String FIELD_9XX_GENRE_SPLIT = "969";

    /**
     * The 9xx field for the contributor display
     */
    public static final String FIELD_9XX_CONTRIBUTOR_DISPLAY_CONTENT = "957";

    /**
     * The 9xx field for the contributor display
     */
    public static final String FIELD_9XX_RELATED_DISPLAY_CONTENT = "958";

    /**
     * The 9xx field for the creator analytic display
     */
    public static final String FIELD_9XX_CREATOR_ANALYTIC_CONTENT = "959";

    /**
     * The 9xx field for the rel title display
     */
    public static final String FIELD_9XX_REL_TITLE_DISPLAY_CONTENT = "958";

    /**
     * The 9xx field for the separate name
     */
    public static final String FIELD_9XX_SEPARATE_NAME = "959";

    // *******************************************************************
    // Other
    // *******************************************************************

    /**
     * The location of the configuration directory.
     */
    public static final String CONFIG_DIRECTORY = "config";
}
