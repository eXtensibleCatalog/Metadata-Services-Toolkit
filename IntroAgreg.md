# Servicio de Agregación MARC. Fusión de registros y Errores (22 may 2012) #

> (nota: la sección de Errores está en revisión)
## Registros bibliográficos no coincidentes ##
Si el resultado del  algoritmo de comparación es FALSE (no encuentra coincidencias para un registro de entrada), el Servicio crea un registro de salida con un nuevo OAI ID. El Servicio no realiza más cambios en el contenido del registro bibliográfico para el registro de salida
## Registros bibliográficos coincidentes ##
Si el resultado del  algoritmo de comparación es TRUE (encuentra coincidencias entre un registro de entrada y uno o más registros ya procesados), el Servicio elegirá uno de estos registros para usarlo de base para la creación del registro sucesor de salida. El servicio incorporará  algunos elementos de los registros no seleccionados al Registro Fuente (el “registro seleccionado”) para crear el registro de salida, como se explica más abajo.
## Cómo se elige el “Registro Fuente” para usarlo como base para la creación del Registro Bibliográfico de Salida ##
El Registro Fuente se usará como base para crear el registro de salida. Existen dos opciones de configuración que deberán combinarse para establecer los criterios que determinarán cuál es el registro fuente:

1) Encoding Level (leader\_byte17\_weighting\_enabled = true/false):  1-True, 2-False
> Nivel de codificación (cabecera\_byte17\_peso\_habilitado=true/false): 1-True, 2-False

Cuando es TRUE, el Servicio compara los códigos de la cabecera/byte17 [posición 17 de la cabecera?] de los registros coincidentes y coge el registro que encuentra antes en una lista de valores configurable como Registro Fuente, por ejemplo:

(leader.order='\u0020', 1, I, L, 4, 7, 5, K, M)
donde '\u0020' representa un espacio en blanco, es decir, Nivel de Codificación “Full”. Solo se listan aquellos códigos de codificación relevantes para los datos del MARC bibliográfico, en orden de preferencia

2) Overall Record Size:  (bigger\_record\_weighting\_enabled = true/false):  1-True, 2-False
Tamaño Registro Completo: (registro\_de\_más\_peso\_habilitado= true/false):  1-True, 2-False)

Cuando es TRUE, el Servicio compara el número de bytes de los registros y usa el que tenga más como Registro Fuente.

Estas configuraciones pueden usarse por separado o de forma conjunta para crear las cuatro combinaciones posibles:

1-true, 2-false: en este caso el Servicio compara Leader/byte17 (Nivel de Codificación), y escoge el registro con el valor Leader byte17 que aparece antes en la cadena “leader.order”. Si los valores son iguales, el Servicio escoge el registro que está siendo procesado como Registro Fuente.

1-true, 2-true: en este caso el Servicio compara Leader/byte17 (Nivel de Codificación), y escoge el registro con el valor Leader byte17 que aparece antes en la cadena “leader.order”. Si los valores son iguales, el Servicio escoge el registro que tiene más términos como Registro Fuente.

1-false, 2-true: en este caso el Servicio escoge el registro que tiene más términos como Recurso Fuente.

1-false, 2-false: Este caso no se puede dar, ya que el Servicio daría un mensaje de error.

## Cómo se crea el Registro Bibliográfico de Salida ##
El Registro Fuente se usará como base para copiar la mayor parte del contenido en el Registro de Salida (Sucesor). Todo el contenido de este registro aparecerá en Registro de Salida. (la información del Registro  Fuente que se copia en el Registro  de Salida es “contenido estático”). Además, determinada información de los registros no seleccionados como fuente se modificará y copiará en el registro de salida y determinada información del registro fuente se modificará antes de añadirla al registro de salida. Esto es lo que se llama “contenido dinámico”. Existen distintos tipos de contenido dinámico, descritos más abajo.


## Identificadores (IDs) de Registros Bibliográficos (001, 003 y sus correspondientes 035) ##
Los campos 001 y 003 del Registro Fuente NO se copian en el Registro de Salida, sino que se usan los campos 001 y 003 TANTO del Registro Fuente COMO de los Registros No Seleccionados como Fuente para crear nuevos 035 en el Registro de Salida, con el valor 003 (código de institución) como prefijo del número de control del $a, entre paréntesis. El número del 001 se pondrá seguido de los paréntesis sin espacio.
## Fecha (005) ##
El campo 005 del Recurso Fuente NOT se copia en el Registro de Salida, sino que el Servicio añade un nuevo 005 al Registro de Salida que incluye la fecha/hora en la que el Servicio termina de procesar el registro.
Aquí puedes encontrar instrucciones para formatear estos datos:
http://www.loc.gov/marc/bibliographic/bd005.html


005 DEFINICIÓN Y ALCANCE
16 caracteres que hacen referencia a la fecha y hora de la última transacción en el registro y sirve para identificar la versión del mismo. La forma de representación está basada en la ISO 8601 "Elementos de datos y formatos intercambiables — Intercambio de información — Representación de fechas y horas". Con 8 caracteres cada uno, el formato de fecha es yyyymmdd, el de hora hhmmss.f expresado en forma 24-horas (00-23)
Yyyymmdd: 4 para el año, 2 para el mes, 2 para el día
hhmmss.f: 2 para hora, dos para minutos, 2 para segundos, 2 para fracción decimal de segundo.
La fecha en que entra el registro por primera vez en formato legible por ordenador está en el 008/00-05. La fecha introducida en un fichero nunca cambia.
EJEMPLO
005	19940223151047.0
[23, 1994, 3:10:47 P.M. (15:10:47)](February.md)

## Otros campos 035 ##
Todos los 035 del Registro Fuente y los Registros No Seleccionados como Fuente se copian en el Registro de Salida. Después, el Servicio revisa el Registro de Salida para eliminar duplicados en los 035, comparando sus $a para asegurarse de que no se han creado duplicados en este proceso (es decir, ni puntos coincidentes duplicados (número OCLC, etc.) en 035, ni nuevos 035 creados a partir de 001 y 003. Cualquier subcampo del 035 que no sean el $a ($z, $6, $8 u otros subcampos locales) se eliminará durante el proceso. TAMPOCO se copiarán en el Registro de Salida los 035 que no tengan prefijo numérico.
Ejemplo:


Registro No Seleccionado
001 	123455
003	NRU
035	$a (OCoLC)55555	Registro Fuente (coincide con el número OCLC del 035)
001	23456
003	CUL
035	$a (OCoLC)55555	Registro de Salida
[001](sin.md)
[003](sin.md)
035	$a (OCoLC)55555
035	$a (NRU)123455
035	$a (CUL)23456



## Otros Puntos Coincidentes ##

Se copiarán en el Registro de Salida otros Puntos Coincidentes (010, 020, 022, 024)  de los Registros No Seleccionados como Fuente si estos no aparecen en el Registro Fuente (se eliminará la información de los subcampos que no sean los que aparecen listados aquí, y los indicadores que no sean los de esta lista serán sustituidos por un “blanco”). Los datos MARC capturados son:
010 $a
020 $a
022 $a, $l, $m, $z
024 $a, $2 (conservando el primer indicador)

Se eliminarán los duplicados de estos campos antes de copiarlos en el Registro de Salida. Si tras el depurado queda más de un 010, el Servicio solo copiará el primero que se encuentre, ya que el 010 es no repetible en MARC21.


## Colocación de la información sobre Puntos Coincidentes dentro del Registro Bibliográfico de Salida. ##

Cuando el Servicio ha terminado todos los procesos y la eliminación de duplicados de datos de Puntos Coincidentes (match point data), estos datos (por ejemplo 010, 020, 022, 024, 035) se agrupa en un bloque de texto, con los campos colocados en orden numérico. Este bloque de campos se inserta en el Registro de Salida directamente después del 008
## Información de los Ejemplares dentro de los Registros Bibliográficos ##

Si alguno de los registros bibliográficos contiene información de ejemplar en campos MARC específicos, y estos campos son compatibles con el Servicio de Normalización MARC, el Servicio de Normalización se puede configurar para que ejecute los ejemplares integrados en campos 953 a la hora de su salida (ver la sección “Fuente de Ejemplares Integrados” del Fichero de Configuración del Servicio de Normalización). Solo la “Opción C”, que ejecuta la información de los ejemplares integrados en campos 953 es compatible con el Servicio de Agregación.
Cuando el  Servicio de Agregación encuentra un 953 creado por el Servicio de Normalización, reconocible porque los valores de subcampo/campo son $1 NyRoXCO (el  MARC Organization Code  para XC) en un  Registro No Seleccionado o en el Registro Fuente, copia todos los campos 953 de todos los registros en el Registro de Salida. Todos los 953 en el conjunto de registros bibliográficos coincidentes que contengan $1 NyRoXCO  se agregan e insertan al final del contenido estático del registro dentro del Registro de Salida. Si el Servicio encuentra otros campos 953 que no contengan $1 NyRoXCO  en el Registro Fuente, estos campos se consideran parte del contenido estático del registro, y son eliminados como duplicados y copiados en el registro de salida como un grupo al final del registro. Los campos 953 sin $1 NyRoXCO  en registros no seleccionados serán ignorados.
## Cómo se procesan Registros de Ejemplar MARC Separados ##
No se hará comparación ni fusión sobre registros MARC de Ejemplar. Por tanto, el contenido de un registro de ejemplar de salida será idéntico al contenido del registro de entrada.
## Errores ##
Actualmente en revisión