# Modificaciones en el Servicio de Transformación MARC para 700, 710, 711 y 730 cuyo 2º indicador es 2 #

Los nombres RDA  han vuelto a cambiar. En Julio de 2010 los nombres de los elementos deberían ser titleOfThe Work y titleOfTheExpression.
## Las instrucciones anteriores  para 700 $t cuyo 2º indicador es 2 eran: ##

El Servicio de Transformación debería crear  un registro de obra SEPARADO para cada 7XX cuyo 2º indicador es 2. Se mapea klmnoprst con workTitle para cada campo 7XX original de forma que cada registro de Obra contenga este elemento más el work/creator mapeado desde su 959 correspondiente.

Aquí está la instrucción modificada/extendida/actualizada para los 700, 710, 711:

El Servicio de Transformación debería crear  un registro de obra SEPARADO para cada 7XX cuyo 2º indicador es 2. Mapear los siguientes subcampos:

700 	kmnoprst [Nota: de todos ellos se ha eliminado el $l]
710	kmnoprst
711 	fkpst

con el titleOfTheWork para cada uno de estos campos de forma que cada registro de Obra contenga este elemento más el work/creator mapeado con el 959 correspondiente. El Servicio de Transformación también debería copiar el resto de elementos del registro de Obra ORIGINAL (excepto el creador y  el titleOfTheWork en el nuevo registro de Obra.

[Nota: el Servicio estaba configurado para que mapeara esto con title, no workTitle. Ahora debería cambiarse para que mapeara con titleOfTheWork].

El Servicio de Transformación debería crear  un registro de obra SEPARADO para cada 7XX cuyo 2º indicador es 2 por cada registro de obra separado que se crea. Este registro de Expresión debería contener TODA la información del registro de expresión creado a partir del MARC original (es decir, simplemente hacer una copia de él). Hay una excepción: reemplaza el titleOfTheExpression (antes expressionTitle) del registro de expresión original (el que ha sido mapeado con el 130 y el 240) con el titleOfTheWork (antes workTitle) del correspondiente registro de Obra (es decir, que ha sido mapeado con 7XX $t y los subcampos detrás del $t), de forma que cada registro de Expresión y cada registro de Obra creados a partir del 7XX tengan la misma información en sus respectivos elementos titleOfTheWork y titleOfTheExpression. La única diferencia aquí es que el 730 $l (lengua) se mapea para el titleOfTheExpression pero no para el titleOfTheWork.

## Instrucción original  para 730 cuyo 2º indicador es 2: ##

El Servicio de Transformación debería crear una sección de obra del registro XC SEPARADA por cada 7XX cuyo 2º indicador es 2. Se mapea adgklmnoprst con workTitle. Nota: a pesar de que se ha definido el $t para el 730, raramente se utiliza. Si aparece, debería tratarse como otros subcampos (ya que todo el campo es un título).

## Y aquí está la instrucción REVISADA  para 730 cuyo 2º indicador es 2: ##

El Servicio de Transformación debería crear un registro de obra SEPARADA por cada 7XX cuyo 2º indicador es 2. Se mapean los siguientes subcampos:

730 adgklmnoprst. Ojo: el $l se ha eliminado. No se mapeará con titleOfTheWork, solo con titleOfTheExpression, como se especifica más abajo

con titleOfTheWork para cada uno de estos campos, de forma que cada registro de obra contenga este elemento (ojo, no debería haber un 959 correspondiente). El Servicio de Transformación debería también copiar todos los demás elementos del registro de obra ORIGINAL (excepto el creador y el titleOfTheWork) en el nuevo registro de obra.

[Nota: antes , el servicio mapeaba esto con title, no workTitle, que debería cambiarse ahora por titleOfTheWork]

El Servicio de Transformación debería crear también un registro de expresión SEPARADO por cada 7XX cuyo 2º indicador es 2, que acompañe al registro de obra separado que ha sido creado también. Este registro de expresión debería incluir TODA la información del registro de Expresión creado a partir del registro MARC original completo (es decir, lo único que hace es una copia de él).

Hay una excepción: reemplaza el titleOfTheExpression (antes, expressionTitle) del registro de Expresión original (el que está mapeado con el 130 y el 240) con el titleOfTheWork (antes, workTitle) del registro de Obra correspondiente (es decir, el que ha sido mapeado desde el 7XX $t y el resto de campos delante del $t) para que cada registro de Expresión y cada registro de Obra creados a partir de los 7XX contengan la misma información en sus respectivos elementos titleOfTheWork y titleOfTheExpression. La única diferencia aquí es que el 730 $l (lengua) DEBERÍA ser mapeado con el titleOfTheExpression pero no con el titleOfTheWork.


## Otros comentarios ##
Hay que tener en cuenta que cada registro de obra y expresión que se cree, idealmente debería tener su propia información, información que es exclusiva de cada tipo de registro (igual que la lengua de esa expresión particular, o la materia de aquella obra particular) pero la mayoría de la información MARC no va a proporcionar la información suficiente para determinar qué va con qué, así que básicamente copiamos toda la información, es la mejor solución.
Aquí hay una lista de los registros de prueba a los que afecta esto:
2117963 (batch del Servicio de Normalización)

El resto están en el Transformation batch:

1177208
1586760
1714983
2117963