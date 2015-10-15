# Servicio de Normalización para Bibliográficos MARCXML #

Los Pasos de Normalización harán una de estas dos cosas: o modifican los datos de la etiqueta MARC original, o crean un campo 9XX que contendrá la información normalizada. En caso de que se cree un campo 9XX, el Servicio de Normalización añadirá un subcampo 5 ($5) dentro del 9XX donde se consignará el código de institución del [MARC Code List for Organizations](http://www.loc.gov/marc/organizations/orgshome.html). Este código deberá configurarse en el “Fichero de Configuración del Servicio de Normalización” (alojado en el Fichero de Configuración, sección “Enabled Steps”, paso “SupplyMARCOrgCode”). El $5 permitirá que el campo 9XX que lo contenga se diferencie de otras etiquetas MARC que pudieran existir en los datos MARC dentro de una institución.


Los vocabularios MARC21 descritos en los pasos están disponibles aquí:
http://www.loc.gov/marc/bibliographic/ecbdhome.html

## 1. DCMIType06 ##

Convierte el código de la posición 06 de la cabecera a DC, basado aproximadamente en la conversión descrita en el Anexo 2 del mapeado de MARC a DC de la LC. Usar este vocabulario para registros creados en MARC puede facilitar la integración de registros MARC y DC en la misma aplicación.

Se creará un campo 931 que contenga el DCMI Type del registro con los datos de la posición 06 de la cabecera y los códigos del 006/00. El archivo leader06ToDcmiType.properties contiene un mapeado de los valores de DCMI Type que deberían usarse. Por ejemplo, la línea a= Material textual” le dice al servicio de Normalización que añada un DCMI Type de “Material textual” a cada registro cuya posición 06 de la cabecera o códigos del 006 sea “a”. Se puede modificar el contenido de este fichero para asociar cualquier valor con cualquier DCMI Type, pero un solo valor no puede estar asociado con más de un DCMI Type. Si no hay una cadena (row) para la posición 06 de la cabecera o posición 006 no se añadirá el DCMI Type para registros con ese valor y no se creará el campo 931.

## 2. Leader06Vocab ##

Reemplaza el código de la posición 06 de la cabecera MARC con el término correspondiente del MARC21 para que lo que vean los usuarios sea el término y no el código.

Se creará un campo 932 que contenga el término de vocabulario de la posición 06 de la cabecera MARC y los códigos del 006/00. El archivo Leader06ToMARCVocabulary.properties contiene un mapeado de los valores del vocabulario de la cabecera 06 de MARC que deberían usarse. Por ejemplo, el valor “a= language material [textual??](material.md)” le dice al Servicio de Normalización que añada un término “Language Material” para cada registro cuya posición 06 de la cabecera o el código del 006/00 sea “a”. Se puede modificar el contenido de este fichero para asociar cualquier valor con cualquier término del Vocabulario de la posición 06 de la cabecera MARC pero un solo valor no puede estar asociado con más de un término. Si no hay una cadena (row) para la posición 06 de la cabecera o el código 00 del 006 no se añadirá ningún término a los registros con ese valor y no se creará el campo 932.


## 3. ModeOfIssuance ##

Reemplaza el código de la posición 07 de la cabecera MARC con el término correspondiente del MARC21 para que lo que vean los usuarios sea el término y no el código.

Se creará un campo 935 que contenga el Nivel bibliográfico del registro como venga especificado en la posición 07 de la cabecera. El archivo Leader07ToModelOfIssuance.properties contiene un mapeado de los valores de la cabecera 07 al Nivel bibliográfico correspondiente. Por ejemplo, el valor “c= colección” le dice al Servicio de Normalización que el Nivel bibliográfico que añada el nivel bibliográfico “colección” para cada registro cuyo valor 07 sea “c”. Se puede modificar el contenido de este fichero para asociar cualquier valor con cualquier Nivel bibliográfico de la posición 07 de la cabecera MARC pero un solo valor no puede estar asociado con más de un Nivel bibliográfico. Si no hay una cadena (row) para la posición 07 de la cabecera no se añadirá ningún Nivel bibliográfico a los registros con ese valor y no se creará el campo 935.

## 4. RemoveOCoLC003 ##

Si el campo 003 tiene un valor de OCoLC, lo elimina.

Este paso está diseñado para eliminar información imprecisa del 003 detectada en el set de registros de la Universidad de Rochester.

## 5. 006Audience ##

Permite que los datos del campo MARC 006 (que a veces se utiliza además del 008) que hacen referencia al destinatario del recurso sea presentada de forma legible por los usuarios.

Si el Código para 006/00 del registro es “a”, “c”, “d”, “g”, “k”, “m”, “o” o “r”, se creará un campo 945 con el nivel de destinatarios, en base a la posición 22 del campo 006 del registro. La sección “FIELD 006/008 OFFSET 22 TO AUDIENCE” en el archivo de configuración contiene un mapeado de la posición 22 del campo 006 para el nivel de destinatario que debería usarse. Por ejemplo, el valor “a= Preescolar” le dice al Servicio de Normalización que añada un Nivel de destinatario “Preescolar” para cada registro cuya posición 22 del 006 sea una “a”. Se puede modificar el contenido de este fichero para asociar cualquier valor de la posición 22 del 006  con cualquier Nivel de destinatario pero un solo valor no puede estar asociado con más de un Nivel de destinatario. Si no hay una cadena (row) para una posición 22 del 006 dada, no se añadirá ningún Nivel de destinatario a los registros con ese valor y no se creará el campo 935.

## 6. 006FormOfItem ##

Para ciertos tipos de registros bibliográficos MARC, permite que datos del campo 006 de MARC relativos a Forma del Item (por ejemplo Microfilm o Braille) sean presentados de forma legible por los usuarios e incluidos como facetas.

Si existe 006 pero NO existen las siguientes etiquetas 00 del 006: e, q, u, v, w, x, y, o z, incluye el vocabulario MARC de los valores de la posición 23 del 006 en un campo 977, por ejemplo       977 $a Microfilm. La sección ‘FIELD 006/008 OFFSET 23 TO FORM OF ITEM‘ que se encuentra en el archivo de configuración contiene un mapeado entre la posición 23 del 006 y la Forma de Ítem correspondiente. Por ejemplo, el valor “a= Microfilm” le dice al Servicio de Normalización que añada una Forma de Ítem  “Microfilm” para cada registro cuya posición 23 del 006 sea una “a”. Se puede modificar el contenido de este fichero para asociar cualquier valor de la posición 23 del 006  con cualquier Forma de Ítem pero un solo valor no puede estar asociado con más de una Forma de Ítem. Si no hay una cadena (row) para una posición 23 del 006 dada, no se añadirá ninguna Forma de Ítem a los registros con ese valor y no se creará el campo 977.

## 7. DCMIType007 ##

Convierte el código de la posición 00 del campo 007 a vocabulario Dublin Core Type cuando esto es posible. Usar este vocabulario con datos MARC puede que facilite la integración de registros MARC y DC en la misma aplicación de descubrimiento.

Se creará un campo 931 que contenga el DCMI Type del registro como venga especificado en el código 00 del 007. La sección ‘FIELD 007 OFFSET 00 TO DCMI TYPE” que se encuentra en el archivo de configuración contiene un mapeado de los valores del código 00 del 007 al término del DCMI Type correspondiente. Por ejemplo, el valor “a= Imagen” le dice al Servicio de Normalización que añada un término “Imagen” para cada registro cuyo valor de la posición 00 del 007 sea “a”. Se puede modificar el contenido de este fichero para asociar cualquier valor con cualquier valor  con su correspondiente DCMI Type pero un solo valor no puede estar asociado con más de un término. Si no hay una cadena (row) para la posición 00 del 007 no se añadirá ningún DCMI Type a los registros con ese valor y no se creará el campo 931.

## 8. 007Vocab ##

Reemplaza el código de la posición 00 del 007 con el término correspondiente del formato MARC21 de forma que lo que vean los usuarios sea el término y no el código.

Se creará un campo 933 que contenga el término de vocabulario de MARC 007 del registro como venga especificado en el código  00 del 007. La sección ‘FIELD 007 OFFSET 00 TO FULL TYPE” que se encuentra en el archivo de configuración contiene un mapeado de los valores del código  00 del 007 al término del Vocabulario MARC21 correspondiente. Por ejemplo, el valor “a= mapa” le dice al Servicio de Normalización que añada un término “Mapa” para cada registro cuyo valor de la posición 00 del 007 sea “a”. Se puede modificar el contenido de este fichero para asociar cualquier valor con cualquier valor de la posición 00 del 007 con cualquier término de vocabulario del 007 de MARC21 pero un solo valor no puede estar asociado con más de un término. Si no hay una cadena (row) para la posición 00 del 007 no se añadirá ningún término a los registros con ese valor y no se creará el campo 933.

## 9. 007SMDVocab ##

Reemplaza la posición del MARC 007/01con el término correspondiente del formato MARC21 de forma que lo que vean los usuarios sea el término y no el código.

Se creará un campo 934 que contenga el término de Designación específica del material del registro como venga especificado en el código  00-01 del 007. La sección ‘FIELD 007 OFFSET 00 TO SMD TYPE” que se encuentra en el archivo de configuración contiene un mapeado de los valores del código 00 y 01 del 007 al término de Designación específica del material correspondiente. Por ejemplo, el valor “ad= Atlas” le dice al Servicio de Normalización que añada un término “Atlas” para cada registro cuyo valor de la posición 00 del 007 sea “a” y el valor de la posición 01 del 007 sea “d” . Se puede modificar el contenido de este fichero para asociar cualquier valor de la posición 00-01 del 007 con cualquier Designación específica del material pero un solo par de valores (00-01) no puede estar asociado con más de un término. Si no hay una cadena (row) para la posición 00-01 del 007 no se añadirá ningún término a los registros con ese valor y no se creará el campo 934.

## 10. 007Vocab06 ##

Genera un término del vocabulario MARC 007/00 en base a la posición 06 de la cabecera para asegurar que se genera esta información y puede usarse como parte de una faceta incluso en los casos en los que el registro MARC no incluya un campo 007.

Se creará un campo 933 que contenga el término de vocabulario de MARC 007 del registro como venga especificado en la posición 06 de la cabecera. El archivo leader06ToFullType.properties contiene un mapeado de los valores de la posición 06 de la cabecera al término del vocabulario de MARC 007 correspondiente. Por ejemplo, el valor “a= Texto” le dice al Servicio de Normalización que añada un término “Texto” para cada registro cuyo valor de la posición 06 de la cabecera sea “a”. Se puede modificar el contenido de este fichero para asociar cualquier valor con cualquier valor de la posición 06 de la cabecera con cualquier término de vocabulario del 007 de MARC21 pero un solo valor no puede estar asociado con más de un término. Si no hay una cadena (row) para la posición 06 de la cabecera no se añadirá ningún término a los registros con ese valor y no se creará el campo 933.

## 11. FictionOrNonFiction ##

Reemplaza el código MARC como se detalla a continuación con el término correspondiente del formato MARC21 de forma que lo que vean los usuarios sea el término y no el código.

Este paso añade un campo 937 con un valor de “Ficción” o “No Ficción”. Si un registro reúne cualquiera de estas dos condiciones:

•	que el valor del 06 de la cabecera sea “a” o “t” y la posición 33 del 008 sea “1” ó

•	que la posición 00 del 006 sea “a” o “t” y la posición 33 del 006 sea “1” se creará un campo 937 de “Ficción”, y el resto de registros que no se ajusten a una de estas dos opciones tendrán un 937 de “No Ficción”.

## 12. 008DateRange ##

Normaliza las fechas en el 008 cuando existe un rango de fechas para hacerlo más entendible por los usuarios.

Si la posición 06 del 008 es “c”, “d” o “k”, en este paso se copia la posición 07-10 del 008 seguido por un guión y de la posición 11-14 del 008 en un campo 939. Si el campo resultante contiene “9999” como una de las fechas, será reemplazado por cuatro espacios en blanco. Si la posición 06 del 008 contiene cualquier otro valor, no se creará ningún campo 939.





## 13. 008FormOfItem ##

Para ciertos tipos de registros bibliográficos MARC, permite que datos del campo 008 de MARC relativos a Forma del Item (por ejemplo Microfilm o Braille) sean presentados de forma legible por los usuarios e incluidos como facetas.

Si el valor 06 de la cabecera NO es: e, q, u, v, w, x, y, o z, crea un campo 977 con el vocabulario MARC de los valores de la posición 23 del 008, por ejemplo 977 $a Microfilm. La sección ‘FIELD 006/008 OFFSET 23 TO FORM OF ITEM‘ que se encuentra en el archivo de configuración contiene un mapeado entre la posición 23 del 008 y la Forma de Ítem correspondiente. Por ejemplo, el valor “a= Microfilm” le dice al Servicio de Normalización que añada una Forma de Ítem  “Microfilm” para cada registro cuya posición 23 del 008 sea una “a”. Se puede modificar el contenido de este fichero para asociar cualquier valor de la posición 23 del 008 con cualquier Forma de Ítem pero un solo valor no puede estar asociado con más de una Forma de Ítem. Si no hay una cadena (row) para una posición 23 del 008 dada, no se añadirá ninguna Forma de Ítem a los registros con ese valor y no se creará el campo 977.

## 14. 008Thesis ##

Elimina una posible discrepancia (contradicción?) en datos MARC entre el 008 y la existencia de una nota.

Si el registro no tiene un campo 502 pero contiene un valor “a” en el 06 de la cabecera y un valor “m” en algún lugar de la posición 24-27 del 008, en este paso se añade un campo 502 con el valor “Tesis”. Si no se dan estas dos condiciones, no se crea ningún campo 502.


## 15. 008Audience ##

Permite que datos adicionales de la cabecera MARC relacionados con el destinatario del registro sean presentados de forma legible por los usuarios e incluidos como facetas.

Si el valor de la posición 06 de la cabecera es ‘a’, ‘c’, ‘d’, ‘g’, ‘k’, ‘m’, ‘o’, o ‘r’ este paso creará un campo 945 con los destinatarios en base a la posición 22 del 008. La sección ‘FIELD 008 OFFSET 22 TO AUDIENCE‘ que se encuentra en el archivo de configuración contiene un mapeado entre la posición 22 del 008 y el Nivel de Destinatario correspondiente. Por ejemplo, el valor “a= Preescolar” le dice al Servicio de Normalización que añada un Nivel de Destinatario “Preescolar” para cada registro cuya posición 22 del 008 sea una “a”. Se puede modificar el contenido de este fichero para asociar cualquier valor de la posición 22 del 008  con cualquier Nivel de Destinatario pero un solo valor no puede estar asociado con más de un Nivel de Destinatario. Si no hay una cadena (row) para una posición 22 del 008 dada, no se añadirá ningún Nivel de Destinatario a los registros con ese valor y no se creará el campo 945.

## 16. LCCNCleanup ##

Elimina sufijos sobrantes en el campo 010 para facilitar el uso de LCCN como un identificador.

Este paso busca la primera barra “/” del campo 010 y, si la encuentra, la borra junto con todos los caracteres que la siguen.

Para más información visita http://www.loc.gov/marc/bibliographic/bd010.html; la LC anunció su intención de borrar estos caracteres en todos los registros creados a partir de 1999; por tanto, no es necesario trasladar este dato a un campo 9xx para su depuración

## 17. ISBNCleanup ##

Elimina información sobrante del campo 020 que no forma parte del ISBN, para facilitar el uso del ISBN como un identificador.

Este paso copia el contenido del campo 020 a un campo 947 que se encuentra antes del primer paréntesis abierto hacia la izquierda o dos puntos. Si no hay 020, no se creará ningún 947.

## 18. 024ISBNMove ##

Traslada los ISBN-13 que había en campos 024 (práctica de la OCLC desde aprox. 2006) a campos 020 para que puedan usarse en la comparación que hace el Servicio de Agregación.

1. Este paso busca los ISBN-13 en  campos 024. Si un identificador en el 024 cumple con estos criterios:
• 1er  indicador = 3
• 1os 3 dígitos= 978 Ó 1os 4 dígitos= 9791, 9792, 9793, 9794, 9495, 9796, 9797, 9798, o 9799 (pero no 9790)
• el número=13 dígitos,
El número de 13 dígitos se pasa a un nuevo 020 $a

2. Si este proceso ha dado como resultado la creación de un campo 020 que es idéntico a un campo 020 ya existente, se elimina el duplicado. Después, el servicio borra el campo 024.

## 19. MoveMARCOrgCode ##

Crea un 035 válido que puede ser mapeado con la propiedad de Esquema XC “recordID” por el Servicio de Transformación. Cuando se activa el paso “mover todos” (move all), siempre se usarán el 001 y el 003 existentes sean cuales sean los valores existentes; cuando se desactiva “mover todos”, solo se usará el 003 cuando coincide con el código de la institución (Organization Code) facilitado en el Archivo de Configuración del Servicio de Normalización.

Se creará un campo 035 a partir de los campos 001 y 003. Cuando el registro tiene 001 y 003, se creará un 035 que contendrá el prefijo del 003 y el número de control del 001. El paso MoveMARCOrgCode\_moveAll, descrito más adelante, describe este paso. Si el MoveMARCOrgCode\_moveAll está configurado con un 0, solo se creará el 035 si el 003 coincide con el Código de Institución (Organization Code). Si es un 1, se creará el 035 si existen 001 y 003. La configuración por defecto del MoveMARCOrgCode\_moveAll es 1.

## 20.SupplyMARCOrgCode ##

Crea un 035 con el Código de Institución en caso de que el registro no tenga 003. Este paso es OBLIGATORIO cuando no se utiliza el OAI Toolkit si los datos de ejemplar se procesan a través del sistema y el registro MARCXML no contiene 003.

Si el registro no tiene 003, este paso traslada el 001 a un 035 cuyo prefijo será el código de la institución. Si se habilita este paso, la biblioteca debería cambiar el Código de Institución de la Universidad de Rochester (NRU) por el código de institución de la biblioteca extraído del MARC Code List for Organizations del Archivo de Configuración del Servicio de Normalización. (El código se encuentra en la sección “Enable Steps” del archivo de configuración, en el paso “SupplyMARCOrgCode).


## 21. Fix035 ##

Normaliza los distintos formatos de número OCLC en campos 035 solucionando contradicciones/discrepancias encontradas en registros MARCXML.

Este paso corrige todos los 035 con códigos OCLC mal creados, dándoles la forma “(OCoLC)

<control\_number>

”. Se corregirán los siguientes errores:

•	035 $b ocm $a 

<control\_number>


•	035 $b ocn $a 

<control\_number>


•	035 $b ocl $a 

<control\_number>


•	035 $a (OCoLC)ocm

<control\_number>


•	035 $a (OCoLC)ocn

<control\_number>


•	035 $a (OCoLC)ocl

<control\_number>


•	035 $9 ocm

<control\_number>


•	035 $9 ocn

<control\_number>


•	035 $9 ocl

<control\_number>




## 22. 035LeadingZero ##

Elimina los ceros a la izquierda de los identificadores numéricos del 035para facilitar la comparación.

Si la parte numérica del 035 (lo que va detrás del prefijo) comienza con uno o más ceros, los elimina.

## 23. Fix035Code9 ##

Este paso tiene como objetivo corregir el formato de los números OCLC de un bloque de registros de microformas de la UR.

Si el contenido del 035 $9 comienza con las letras “ocm”, traslada el número a 035 $a después del prefijo (OCoLC) para que coincida con el formato usado para el paso Fix035.


## 24. Dedup035 ##

Elimina posibles redundancias entre campos 035.

Si hay varios campos 035 con el mismo valor, se borran todos menos uno.

## 25. LanguageSplit ##

Separa cada código de lengua (salvo lengua original en caso de que sea una traducción y lengua de material anejo) para que puedan ser tratados de forma individual.

Se creará un campo 941 para cada código de lenguaje que aparezca en el registro. Los códigos de lenguaje se extraen de las posiciones 35-37 del 008 y del 041 $a y $d. Los códigos “mul”, “N/A”, “und”, “ZXX” and “XXX” se ignorarán y no se crearán campos 941 con ellos.


## 26. RoleAuthor ##

Permite mapear estos datos para el rol “author” de RDA, en vez de “creator”, que es más genérico. Este rol más específico puede usarse para generar una faceta en la aplicación de descubrimiento.

Si el registro tiene un valor “a” en el 06 de la cabecera y tienen un campo 100, 110 o 111 sin $4, este paso añade un subcampos $4 con valor “aut” a dichos campos.

## 27. RoleComposer ##

Permite que estos datos sean mapeados con el rol “Composer” del RDA, en vez de “creator”, que es más genérico. Este rol más específico puede usarse para generar una faceta en la aplicación de descubrimiento.

Si el registro tiene un valor “c” en el 06 de la cabecera y tienen un campo 100, 110 o 111 sin $4, este paso añade un subcampos $4 con valor “cmp” a dichos campos.

## 28. UniformTitle ##

Crea un título uniforme cuando no existe uno en el registro (puede que la causa sea que el 245 $a es idéntico). En este título uniforme se basarán los títulos de obra (work) y expresión del Esquema XC.

Si el registro no tiene 130, 240 o 243 y sí que contiene 245, este paso copia el 245 $a, $n, $p, $k, y  $f en un 240 que crea. El 240 nuevo tendrá los mismos indicadores que aquel 245.

## 29. TitleArticle ##

Se crea un 246 sin artículo inicial cuando el 245 comienza con ese artículo. Se mapeará el 246 con el título alternativo y puede usarse para facilitar la ordenación por título en XC.

Si el 1er indicador del 245 es 1 y el 2º no es 0, este paso ignora el número de caracteres del principio del campo establecidos en el 2º indicador y copia el resto del 245 $anpfk en un 246 nuevo con los mismos subcampos (anpfk). Los indicadores del 246 serán 3 y 0. El primer carácter mapeado se pondrá en mayúscula si no lo está. Si este paso da como resultado un 246 idéntico a otro ya existente en el registro, se eliminará.

## 30. TopicSplit ##

Separa datos de materias asociados a conceptos detectados en los campos 6XX en uno o más campos que puedan ser usados como base para hacer facetas en la aplicación de descubrimiento.

Este paso copia todos los subcampos hasta (y sin incluir) el primer $v, $y o $z del 600, 610, 611, 630 y  650 en distintos 965, conservando los códigos de subcampos e indicadores originales. Además, copia todos los subcampos hasta (y sin incluir) el primer $x de cualquier etiqueta 6XX menos 656, 657, 658 y 662 en distintos 965.

## 31. ChronSplit ##

Separa datos de materias asociados a periodos cronológicos detectados en los campos 6XX en uno o más campos que puedan ser usados como base para hacer facetas en la aplicación de descubrimiento.

Este paso copia todos los subcampos hasta (y sin incluir) el primer $v, $y o $z del 648 en distintos 963, conservando los códigos de subcampos e indicadores originales. Además, copia todos los subcampos hasta (y sin incluir) el primer $y de cualquier etiqueta 6XX menos 656, 657, 658 y 662 en distintos 963.

## 32. GeogSplit ##

Separa datos de materias asociados a áreas geográficas detectados en los campos 6XX en uno o más campos que pueden ser usados como base para hacer facetas en la aplicación de descubrimiento.

Este paso copia todos los subcampos hasta (y sin incluir) el primer $v, $y o $z del 651 en distintos 967, conservando los códigos de subcampos e indicadores originales. Además, copia todos los subcampos hasta (y sin incluir) el primer $z de cualquier etiqueta 6XX menos 656, 657, 658 y 662 en distintos 967.

## 33. GenreSplit ##

Separa datos de materias asociados a género detectados en los campos 6XX en uno o más campos que puedan ser usados como base para hacer facetas en la aplicación de descubrimiento.

Este paso copia todos los subcampos hasta (y sin incluir) el primer $v, $y o $z del 655 en distintos 969, conservando los códigos de subcampos e indicadores originales. Además, copia todos los subcampos hasta (y sin incluir) el primer $v de cualquier etiqueta 6XX menos 656, 657, 658 y 662 en distintos 969.


## 34. NRUGenre ##

Estandariza los datos de los registros de la Universidad de Rochester (NRU) para términos locales de género.

Por cada 655 con un $2 de “local” y un $5 de “NRU”, este paso cambia el valor del $2 a “NRUGenre” y elimina el subcampos $5.

## 35. NRUDatabaseGenre ##

Estandariza los datos de los registros de la Universidad de Rochester (NRU) para términos locales de género.

Por cada 999 con un $a que tenga el dato “database”, este paso crea un 655 $a “database” y establece el valor $2 coincidente con el 999 $a.


## 36. DedupDCMIType ##

Elimina información repetida

Si el Servicio de Normalización ha creado más de un campo 933 con la misma información, se borran todos los coincidentes menos uno.

## 37. Dedup007Vocab ##

Elimina información repetida

Si el Servicio de Normalización ha creado más de un campo 933 con la misma información, se borran todos los coincidentes menos uno.

## 38. LanguageTerm ##

Reemplaza el código de lengua MARC con el correspondiente término del formato MARC21 para que el término y no el código se presente al usuario.

Se crea un campo 943 que contenga la lengua para cada código de lenguaje de los campos 941. La sección “LANGUAGE CODE TO LANGUAGE” del fichero de configuración contiene un mapeado entre los valores del 941 y su lengua correspondiente. Por ejemplo, el valor “eng= English” ” le dice al Servicio de Normalización que añada una lengua “English” para cada registro con 941 $a “eng”.  Se puede modificar el contenido de este fichero para asociar cualquier código de lengua con cualquier lengua  pero un solo código de lengua no puede estar asociado con más de una lengua. Si no hay una cadena (row) para código de lengua, no se añadirá ninguna lengua y no se creará el campo 941


## 39. SeparateName ##

Reubica la parte “autor” del encabezamiento de autor/título en un campo separado para que pueda ser comparado con un archivo de autoridades. En estos momentos, los pasos de este servicio solo esán descritos para manejar analíticas de autor/título, que serán mapeadas para separar registros dentro del Esquema XC y no para manejar encabezamientos de autor/título.

Por cada 700, 710 o 711 cuyo 2º indicador sea 2, se copian todos los datos hasta (y sin incluir) el primer $t en un 959. Después, se añade un subcampo de enlace, $8, tanto al campo original como a su 959 para vincularlos.

## 40. Dedup9XX ##

Elimina información repetida

Si existen varios 963, 965, 967, 969, o 959 con el mismo contenido y el mismo valor de 2º indicador, se borran todos los coincidentes menos uno. Este paso ignorará los subcampos de enlace $8, los códigos de subcampo, 1er indicador y los espacios en blanco (trailing periods) para comprobar si existen campos duplicados. Si un campo con un subcampo de enlace $8 se elimina al aplicar este paso, aquel subcampo de enlace se añade al subcampo duplicado que no fue eliminado para asegurar que todos los enlaces se mantienen correctamente.

## 41. BibLocationName ##

Reemplaza el código de localización con una localización para que el término y no el código se presente al usuario.

Se reemplaza el código de localización del 852 $b con el nombre de la localización. La sección “LOCATION CODE TO LOCATION‘” del fichero de configuración contiene un mapeado entre los códigos de localización y los términos de localización. Por ejemplo, el valor “RareRev = Preservation/Rare Books” ” le dice al Servicio de Normalización que reemplace cada localización “RareRev” por  “Preservation/Rare Books”. Para hacer uso de este paso, cada biblioteca deberá reemplazar los datos de ejemplo del fichero de configuración con sus propios códigos de localización y localizaciones. Un solo código de localización no puede estar asociado con más de una localización. Si no hay una cadena (row) para código de localización, no se sustituirá ningún código por ninguna localización. Este paso está en Off por defecto porque el XC Drupal Toolkit también permitirá sustituir códigos de localización con términos de localización más extensos.

## 42. IIILocationName ##

Espacio reservado para que bibliotecas que usen III puedan mapear su información local.

Reemplaza la información del 945 $l con el término de localización de la sección “LOCATION CODE TO LOCATION‘”del fichero de configuración.


## 43. Remove945Field ##

Permite que las bibliotecas que hayan usado 945 $5 para otros propósitos puedan eliminar esta información para asegurar que no entra en conflicto con el proceso del Servicio de Normalización.

Se busca comprobar si el Código de institución del 945 $5 coincide con el Código de institución existente en el fichero service.xccfg. Si el código no coincide, se borrará el campo 945.

## 44. NRUDatabaseGenre ##

Convierte información local creada en la UR que identifica bases de datos online a un campo de género con un código local de fuente de género, y por lo tanto permite que sea mapeado por registros del Esquema XC y usado como parámetro para definir una pestaña de Buscar Base de datos en Drupal.

Si un registro bibliográfico contiene la palabra “Database” en cualquier parte del 999 $a (puede que a veces no sea la primera palabra del subcampo), añade el siguiente campo al registro:
655 # 0 $a Database $2 NRUGenre.