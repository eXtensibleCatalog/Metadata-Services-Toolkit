-- ----------------------------------------------------
-- This script inserts the minimum amount of data
-- into the database to run a harvest.  Since it
-- assumes the value of auto increment fields will be 1, 
-- it should only be used on empty databases.
--
-- After this script is run, the harvest it sets up
-- can be run by calling "new XC_Harvester(1);"
-- ----------------------------------------------------

USE MetadataServicesToolkit;

-- Insert a dummy server.  A xc_user is required to have data
-- on which server created it, so we'll give it this server

-- Insert the provider which will be harvested.
-- This will currently insert the OAI Toolkit on the development
-- server, but the URL may be changed to point to a different repository

INSERT INTO providers (created_at,
                       name,
                       oai_provider_url,
                       protocol_version,
                       user_id,
                       service,
                       log_file_name) 
VALUES ('2008-11-25 15:00:00',
        'Dummy Provider',
        'http://128.151.244.132:8080/OAIToolkit/oai-request.do', -- The URL of the OAI repository to harvest
        '1.0',
        1,
        1,
        'logs/dummyLog');

-- Insert processing directives to run the Normalization Service after harvesting the provider
-- and to run the Transformation Service after running the Normalization Service.

INSERT INTO processing_directives (source_provider_id,
                                   source_service_id,
                                   service_id,
                                   output_set_id,
                                   maintain_source_sets)
VALUES (1, -- input is the provider we're harvesting
        0,
        1, -- will run the Normalization Service
        NULL,
        false);

INSERT INTO processing_directives (source_provider_id,
                                   source_service_id,
                                   service_id,
                                   output_set_id,
                                   maintain_source_sets)
VALUES (0,
        1, -- input is the Normalization Service
        2, -- will run the Transformation Service
        NULL,
        false);

-- Insert the processing directives to input formats rows so both processing
-- directives accept records in the marcxml format

INSERT INTO processing_directives_to_input_formats (processing_directive_id,
                                                    format_id)
VALUES (1,
        1);

INSERT INTO processing_directives_to_input_formats (processing_directive_id,
                                                    format_id)
VALUES (2,
        1);