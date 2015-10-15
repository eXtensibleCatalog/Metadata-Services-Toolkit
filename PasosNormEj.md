# Servicio de Normalización para Ejemplares MARCXML #

## 1. ValidFirstChar014 ##

Si el campo 014 tiene como primer indicador un “1” y un valor dentro del $a que comience con cualquier valor EXCEPTO aquellos incluidos en la lista del Fichero de Configuración para este paso (por ejemplo ValidFirstChar014 =), este paso elimina el 014.

Este paso está diseñado para eliminar campos 014 que incluyan links a números de registros bibliográficos que no se encuentran en el sistema actualmente y que no se procesarán a través del Servicio de Transformación MARCXML a DC. Si habilitamos este paso, evitaremos que registros de ejemplar que incluyan dichos 014 queden “en espera” de forma indefinida hasta que el registro bibliográfico inexistente sea procesado por el sistema.

## 2. InvalidFirstChar014 ##

Si el campo 014 tiene como primer indicador un “1” y un valor dentro del $a que comience con cualquier valor incluido en la lista del Fichero de Configuración para este paso (por ejemplo ValidFirstChar014 =), este paso elimina el 014.

Este paso está diseñado para eliminar campos 014 que incluyan links a números de registros bibliográficos que no se encuentran en el sistema actualmente y que no se procesarán a través del Servicio de Transformación MARCXML a DC. Si habilitamos este paso, evitaremos que registros de ejemplar que incluyan dichos 014 queden “en espera” de forma indefinida hasta que el registro bibliográfico inexistente sea procesado por el sistema.

## 3. Add014Source ##

Si el campo 014 contiene un valor que comience con los términos listados en el fichero substitutions.014.key, dentro del  MARCNormalization custom.properties, añade un subcampo “b” al 014 con el término del fichero substitutions.014.key, dentro del  MARCNormalization custom.properties.

Por ejemplo, por defecto el sistema contiene:

substitutions.014.key=ocm
substitutions.014.value=OCoLC

En este caso, si el 014 comienza con ocm, se añade un subcampo “b” con el término “OCLC”

Este paso está diseñado para identificar de forma correcta números de vinculación (linkage numbers) encontrados en la base de datos de la Universidad de Rochester.








## 4. Replace014 ##

Este campo, además de On y Off, puede establecerse como Protect

Sustituye los campos 004 (que no sean formato MARC) que hacen referencia a relaciones con otros registros (bound-withs) (por ejemplo, un registro de ejemplar que está vinculado a varios registros bibliográficos) por campos 014 para todos los registros bibliográficos menos el primero (004). Este paso se creó para manipular datos Voyager extraídos como archivos bibliográficos y de ejemplar diferenciados, que contendrán varios 004 donde se consignan las relaciones entre ellos.

o	On

Cuando los registros que ingresan en el sistema contengan varios 004, este paso deberá habilitarse  para que dichos registros puedan ser procesados.

o	Protect

Si una biblioteca quiere localizar registros con varios 004 sin machacar (overlay) los campos 014 que estuvieran presentes en el registro, deberá configurarse como “Protect”

o	Off

Si configuramos este paso como “Off” y en el registro de ejemplar hay varios 004, el registro NO será procesado correctamente por otros servicios del MST (solo puede usarse un único 004 que relacione el registro de ejemplar con su correspondiente bibliográfico)!

Este paso comprueba en primer lugar si un registro de ejemplar tiene varios 004.
- Si solo tiene uno, el paso no hace nada.
- Si tiene varios 004 y está configurado como “on”, borrará los 014 cuyo primer indicador sea “1”, eliminará todos los 004 menos el primero y creará un nuevo 014 con primer indicador “1” por cada 004 borrado. El nuevo 014 contendrá la información del 004 borrado (el vínculo con el registro bibliográfico). El primer 004 se mantiene en el registro tal y como estaba.
> - Si tiene varios 004 y está configurado como “protect”, el paso notifica (logs) un error en el registro y no lo procesa.
Nota: los registros con varios campos 004 no serán procesados correctamente por otros servicios del MST!

## 5. HoldingsLocationName ##

Proporciona un nombre de localización por cada código de localización para que pueda ser  presentado de forma legible por los usuarios.

Este paso coge el nombre de localización del 852 $b incluido en el fichero de configuración  y lo inserta en un 852 $c. La sección del fichero de configuración  ‘LOCATION CODE TO LOCATION LIMIT NAME’ contiene ejemplos de mapeado entre códigos de localización  y Nombres de Localización. Estos deberían ser borrados y sustituidos por los códigos de localización reales de la Institución si se usa este paso. Por ejemplo, el valor “RareRev = Preservation/Rare Books” le dice al Servicio de Normalización que use la localización “Preservation/Rare Books” para el código “RareRev”. Un único código no podrá estar asociado a más de una localización. Si no hay una cadena (row) para un código de localización dado, no será sustituido por ninguna localización.


## 6. LocationLimitName ##

Agrupa códigos de localización determinados en un número limitado de Nombres de Localización para facilitar la creación de facetas más amplias de Localización.

Sustituye el código de localización del 852 $b por el Nombre de Localización correspondiente incluido en el fichero de configuración  ‘LOCATION CODE TO LOCATION LIMIT NAME’. La configuración por defecto contiene ejemplos de mapeado entre códigos de localización  y Nombres de Localización. Estos deberían ser borrados y sustituidos por los códigos de localización reales de la Institución si se usa este paso. Por ejemplo, el valor “RareRev = Rare Books” le dice al Servicio de Normalización que use la localización “Rare Books” para el código “RareRev”. Un único código no podrá estar asociado a más de una localización. Si no hay una cadena (row) para un código de localización dado, no será sustituido por ninguna localización.