# MAS – GLOSARIO #
> ## dynamic\_content  - contenido\_dinámico ##

Al contrario que el contenido\_estático, el contenido dinámico es la parte de contenido de un registro de salida creada a partir de varios registros de entrada. Los campos necesarios obviamente son aquellos que contienen identificadores (ids) y referencias. Si es un registro bibliográfico, MAS traslada los 001 y 003 a 035. Si es un registro de ejemplar, MAS traslada los 004 y 014 a 904. En ambos casos se conservan en el registro de salida asociado los campos de todos los registros de entrada como contenido\_dinámico. Estos campos de identificadores/referencia mas los campos conservados (keep\_fields) conforman el Contenido\_dinámico.

## held\_records  - registros en espera ##

El subconjunto de registros de ejemplar dentro del conjunto de registros\_procesados que están esperando la llegada de los bibliográficos a los que se asociarán

## in\_process\_record  - registro\_en\_proceso ##

El registro de entrada que está siendo procesado por el MAS.

## keep\_fields  - Campos\_guardados ##

El contenido\_dinámico cuya preservación está garantizada dentro del registro de salida. Aparte de estos campos, el único contenido cuya preservación está garantizada dentro del registro de salida es el de los 001 y 003 para bibliográficos y el de los 001, 003, 004, y 014 para ejemplares. El resto del contenido se pierde en todos los registros fusionados que no sean registros\_fuente. Los campos\_guardados son los que realmente hacen posible la fusión de contenido de varios registros de entrada en un registro de salida.

## MAS  - Servicio de Agregación MARC, un Servicio del MST ##

## match\_point  – punto\_coincidente ##

Un campo o subcampo específico de un registro que se usa como base para establecer una coincidencia.

## match\_rule  - Regla de coincidencia ##

Un juicio condicional [computación - orden en un programa que contiene una condición](en.md) que cuando resulta verdadero significa que hay dos registros coincidentes. Una Regla de coincidencia está formada por uno o más puntos coincidentes (ver Paso 2A)

## matched\_records  – registros\_coincidentes ##

El conjunto de registros\_procesados que presentan coincidencias en el registro\_en\_proceso.

## merge\_score   - puntuación\_de\_fusión ##

La puntuación obtenida por un registro de entrada de acuerdo con  [regla (ejemplo 2)](https://docs.google.com/document/d/1e3fnpaiXuNpwZvbCFvVxlzDrgqvoBBMGsTtygD1R0Yo/editesta). Este cálculo se realiza para todos los registros\_coinciedentes, y el que tenga la máxima puntuación se convierte en registro\_fuente.

## output\_records  – registros de salida ##

El conjunto de registros del repositorio de salida del servicio (que son susceptibles de ser recolectados a través de OAI-PMH).

## existing\_output\_record  – registro\_de\_salida\_existente ##

Registro de salida que es un sucesor o bien del registro\_en\_proceso (si se trata de una actualización) o un registro dentro de registros\_procesados que presentaq una coincidencia con un registro\_en _proceso._

## procesed\_records  – registros\_procesados ##

El conjunto de registros\_de\_entrada que han sido procesados por el MAS

## procesed\_records\_match\_points  – puntos\_coincidentes\_de\_registros\_procesados ##

El conjunto de todos los puntos coincidentes de todos los registros\_procesados

== procesed\_records\_with\_common\_match\_points–
registros\_procesados\_con_puntos\_coincidentes\_comunes ==_

El conjunto de todos los registros que tienen puntos coincidentes en común con el registro\_en\_proceso (no tienen que ser necesariamente coincidencias). La diferencia entre estos registros y los registros\_coincidentes es que el hecho de tener puntos coincidentes no asegura que exista una coincidencia. Por ejemplo, el 010 podría presentar una coincidencia con otro registro, pero si el 020 no coincide también, los registros no se considerarán coincidentes. ([ver paso 2ª](http://code.google.com/p/xcmetadataservicestoolkit/wiki/MarcAggMatchPointsAndErrorCases#Step_2A:)).

## Record\_of\_source  – recurso\_fuente ##

Un registro de entrada concreto dentro del conjunto registros\_coincidentes empleado como base del contenido\_estático del documento. Este es el registro con la puntuación\_de\_fusión más alta dentro de un conjunto particular de registros\_coincidentes.

## Static\_content  – contenido¬¬ estático ##

El contenido estático de un registro de salida es la parte del recurso\_fuente que se copia del registro\_fuente y no se modifica. Para la implementación actual, se corresponde con los campos 001, 003, 035 $a, 004, y 014. Si se necesita preservar contenido de todos los registros fusionados, se debería hacer a través de campos\_guardados. El contenido\_estático se almacena en la columna xml de la tabla records\_xml (que se puede encontrar en la agrupación de tablas del “repository Core” en [este diagrama](http://code.google.com/p/xcmetadataservicestoolkit/wiki/MarcAggArchitecture#Diagrams)).