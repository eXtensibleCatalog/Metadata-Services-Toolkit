#folder1=MARCNormalization
folder1=MARCToXCTransformation

input_folder2=expected_output_records
#input_folder2=mock_harvest_expected_output

output_folder2=actual_output_records
#output_folder2=mock_harvest_actual_output

folder3=orig-186
#folder3=randys-30

input_file_prefix="oai-mst.rochester.edu-MetadataServicesToolkit MARCNormalization "
input_file_suffix=.xml
output_file_prefix=${input_file_prefix}
output_file_suffix=.xml

#input_file_prefix=
#input_file_suffix=
#output_file_prefix=
#output_file_suffix=

#TortoiseMerge ./mst-service/custom/MARCNormalization/test/expected_output_records/orig-186/${1}.xml ./mst-service/custom/MARCNormalization/build/test/actual_output_records/orig-186/${1}.xml &
#TortoiseMerge ./mst-service/custom/MARCToXCTransformation/test/expected_output_records/orig-186/oai-mst.rochester.edu-MetadataServicesToolkit\ MARCNormalization\ ${1}.xml ./mst-service/custom/MARCToXCTransformation/build/test/actual_output_records/orig-186/oai-mst.rochester.edu-MetadataServicesToolkit\ MARCNormalization\ ${1}.xml &
ls -lad "./mst-service/custom/${folder1}/test/${input_folder2}/${folder3}/${input_file_prefix}${1}${input_file_suffix}" "./mst-service/custom/${folder1}/build/test/${output_folder2}/${folder3}/${output_file_prefix}${1}${output_file_suffix}"
#diff ./mst-service/custom/${folder1}/test/${input_folder2}/${folder3}/${input_file_prefix}${1}${input_file_suffix} ./mst-service/custom/${folder1}/build/test/${output_folder2}/${folder3}/${output_file_prefix}${1}${output_file_suffix}
TortoiseMerge "./mst-service/custom/${folder1}/test/${input_folder2}/${folder3}/${input_file_prefix}${1}${input_file_suffix}" "./mst-service/custom/${folder1}/build/test/${output_folder2}/${folder3}/${output_file_prefix}${1}${output_file_suffix}" &
