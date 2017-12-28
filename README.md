# practica-final-isi
Practica final realizada para la asignatura de Ing de sistemas de informacion

# Lista de requisitos:
	- Vamos a tener varios ficheros de informacion de peliculas (datos: fecha de la pelicula, actores, canciones , subconjuntos de 18+ 12+..)
	- cargar la informacion en un grafo para realizar busquedas para saber que relaciones pueden tener esos actores, además podemos consultar medidas de distancia entre actores en el grafo -- queries sobre la base de datos mediante el software
	- el software lo tenemos en la linea de comandos tenemos que integrarlo para que luego lo podamos convertir en un servisio web en heroku
	- el software carga en memoria la estructura de datos. Puede haber ocasiones que queramos tener en tablas porque esa busqueda se realice más veces.
	- tenemos que saber que queremos en cada momento con esas queries, si las queremos tener o no ya calculadas para que no tengamos que acceder a memoria cada vez que se pregunte por ella si no que se tenga a precalculada en el disco

	- Integracion de las BBDD en la apis

  
# Formato sobre los datos de las peliculas : 
  
Formato de texto : Nombre_peli/Actor1/actor2/....
                      Nombre_peli2/Actor1/actor2/....
  
Tenemos ficheros con subconjuntos de peliculas - de donde sacaremos los generos de esas peliculas.
  
    todas -- nombrepeli1/nombrepeli2....
    18+   -- nombrepeli1/nombrepeli2....
    13+   -- nombrepeli1/nombrepeli2....

Podemos parsear estos datos a traves de json y  realizar las sentencias sql desde la api

Para las preguntas sobre las queries podemos realizar un formulario o como si fuera una apiREST
