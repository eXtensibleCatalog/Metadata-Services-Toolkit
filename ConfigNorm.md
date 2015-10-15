# Cómo configurar el Servicio de Normalización #

Antes de configurar el Servicio de Normalización, hay que leer las secciones anteriores, donde se explica cómo configurar el MST para que recolecte registros y los envíe a los Servicios de Metadatos.

El Servicio de Normalización se configura a partir del directorio:
MST-instances/MetadataServicesToolkit/services/Normalization/serviceConfig

Este archivo contiene información sobre qué pasos debería dar el Servicio de Normalización y cuáles se saltará. El archivo contiene varias líneas con el siguiente formato:



<normalization\_step\_name>

 = 1

Donde 

<normalization\_step\_name>

 es el nombre del paso y en “1” indica que está habilitado (“0” sería inhabilitado).

También incluye algunas secciones que definen las propiedades usadas en cada paso. Las secciones se definen de la siguiente forma:
#-----------------------------------------
<Section name>
#-----------------------------------------
Los apartados Pasos de Registros Bibliográficos y Pasos de Registros de Ejemplar (a continuación) describen todas las propiedades de este archivo.

Hay una propiedad que hay que cambiar OBLIGATORIAMENTE para que el Servicio de Normalización funcione. Ir a:

MST-instances/MetadataServicesToolkit/services/marcnormalization/META-INF/classes/service.xccfg

y poner el código de institución aquí:

OrganizationCode = CHANGE\_ME

Sustituir CHANGE\_ME por el código de la institución

Nota importante:

Se puede cambiar la configuración de los pasos del Servicio de Normalización habilitando o deshabilitando los que consideremos oportuno. Añadir nuevos pasos o modificar los existentes requiere modificar el archivo de configuración y el archivo JAR  de Normalización.

Cuando cualquiera de ellos sufre un cambio, todos los datos deben ser reprocesados a través de este servicio para permitir que el procesamiento OAI-PMH actualice los registros y así puedan ser manipulados de forma automática.

Recomendamos a las instituciones que quieran realizar cambios en el Servicio de Normalización (o cualquier otro servicio del MST) que recopilen todos los cambios y los implementen a la vez, para así minimizar el número de veces que los datos se reprocesarán.