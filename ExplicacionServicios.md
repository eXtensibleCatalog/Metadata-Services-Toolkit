# Qué es un Servicio de Metadatos? #

El Metadata Service Toolkit (MST)  es una plataforma que recibe un set de registros (es decir, un repositorio) y presenta como resultado otro set de registros (es decir, un repositorio). Un registro es un documento xml de cualquier tipo. Un servicio de metadatos es el proceso por el cual 1 registro de entrada genera 0..N registros de salida del mismo u otro tipo. El protocolo usado para enviar los registros a un servicio es oai-pmh. Todos los servicios procesan los registros uno a uno, de forma secuencial. Así, oai-pmh puede recolectar 5.000 registros de una vez, pero un servicio debe procesarlos uno a uno. Cuando finaliza el procesado de un registro de entrada (un documento xml) el servicio decide si añade algún registro de salida a su repositorio. La plataforma MST gestiona todas las funcionalidades generales de este proceso para que los servicios individuales puedan dedicarse al desarrollo de sus procesos específicos.




## Desde el punto de vista de sus componentes ##
![https://docs.google.com/drawings/pub?id=1hGdfO5sgolyzCZOkOo1SP6rvvTSHwJkJeT0UXmbP1ZE&w=600&bogus=file.png](https://docs.google.com/drawings/pub?id=1hGdfO5sgolyzCZOkOo1SP6rvvTSHwJkJeT0UXmbP1ZE&w=600&bogus=file.png)


> ## Desde el punto de vista del documento ##
![https://docs.google.com/drawings/pub?id=1S06QAhOHti7oz3BOZidTetMHuyJX8WKl6uS_PhrLnzc&w=400&bogus=file.png](https://docs.google.com/drawings/pub?id=1S06QAhOHti7oz3BOZidTetMHuyJX8WKl6uS_PhrLnzc&w=400&bogus=file.png)

Plataforma  de estructuras de datos (Platform Data Structures)
El MST usa MySQL (una base de datos relacional de acceso abierto rápida y ampliamente extendida) para almacenar registros en los repositorios. Antes de recolectar y aplicar los procesos carga algunas de estas tablas en memoria para permitir un mayor rendimiento. Muchas de estas estructuras de datos alojadas en memoria usan trove (una biblioteca de colecciones de alto rendimiento para java). De todas formas, algunas tablas se consultan en tiempo real en base a la frecuencia estimada de dichas consultas. Por ejemplo, el objetivo principal de la optimización se centra en la carga inicial de los datos. Ya que la meta es procesar los registros a un ritmo de 1 registro/ms incluso una consulta rápida a una base de datos se puede considerar significativo.
La parte superior de este esquema es el equivalente a un esquema de clases y la parte inferior es un ERD [entidad-relación](modelo.md). Estos esquemas no son exhaustivos, pero nos dan una idea de cómo trabaja la plataforma y cómo la persona encargada de implementar un servicio puede trabajar con la información proporcionada a través de la plataforma MST. En los archivos sql se puede consultar un listado más exhaustivo.

•	Estructuras alojadas en memoria

o	oai\_id\_2\_record\_id map

	Descripción: Esta cache sirve para establecer si un determinado registro ha sido procesado anteriormente por el servicio. También realiza un seguimiento de cuál es el estado del registro en cada parte del proceso. Una instancia es una clase de tipo DynMap. Esta clase acepta identificadores alfanuméricos. Si los identificadores son numéricos, se requiere mucha menos memoria. Esta es la finalidad de la propiedad harvest.redundantToken en el fichero de propiedades. El valor de esta propiedad es una lista separada por comas de caracteres repetidos que se extraen de los oai-id. Esta función la realiza por el método Util.getNonRedundantOaiId. Quizás en un futuro este método podrá reemplazar la parte repetida por un código numérico en vez de simplemente copiarlo. Así, se aseguraría la unicidad de los códigos a lo largo de todos los repositorios.

<img src='https://docs.google.com/drawings/pub?id=1UGYpBYBRILrmVRPjpvUy-SrO1FAUUKt_XA3yo3GzBLA&w=750&h=386&filetype=.png' />
<table>
<tr>
<td valign='top'>
<img src='http://www.extensiblecatalog.org/doc/MST/4wiki/repo.png' />
</td>

<td valign='top'>
<img src='http://www.extensiblecatalog.org/doc/MST/4wiki/harvest_repo.png' />

<img src='http://www.extensiblecatalog.org/doc/MST/4wiki/service_repo.png' />
</td>
</tr>
</table>

<img src='http://www.extensiblecatalog.org/doc/MST/web_safe_GIFs/gifs/ffffff.gif' height='500' width='1' />