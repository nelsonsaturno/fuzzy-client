README

Cómo montar y ejecutar la aplicación difusa para este proyecto.



Montar la BD en Postgres:

- Instalar Postgres.
- Configurarlo según el documento en el Drive. Al final debería existir
  una base de datos 'fuzzy', un usuario 'fuzzy' con clave 'fuzzy'.
  NOTA: hagan que el usuario 'fuzzy' sea superusuario en Postgres, o
  CREATE FUZZY DOMAIN no va a servir.



Compilar el parser y el traductor:

- Instalar el Java JDK.
- Ejecutar el script 'parser.sh' en la raíz del repositorio.
- En este punto pueden probar el traductor ejecutando el comando
  java -jar "nosequeverga/FuzzyDB.jar" que sale al final de la corrida
  de parser.sh.
  Si explota, no configuraron bien Postgres. Si funciona, pueden usar
  la consola del traductor tal cual lo mostré en la presentación para
  los profesores y así ven que tal.



Montar el gateway para conectar Django con el traductor en Java:

- Compilar y ejecutar el gateway:

    $ cd FuzzyGateway
    $ ant clean run

    Esto va a ejecutar un servidor corriendo en no-se-que-puerto.
    Déjenlo corriendo ahí, o Django no va a correr.
    Si explota, probablemente es porque no se pudo conectar a
    Postgres, o porque no compilaron el parser/traductor.



Montar el app en Django:

- Instalar el paquete python-virtualenv:

  # apt-get install python-virtualenv

- Crear el entorno virtual para python en alguna carpeta del universo.
  Recomiendo que sea la carpeta fuzzyapp dentro de este proyecto.

  $ cd alguna_carpeta_del_universo
  $ virtualenv env_fuzzyapp

- Activar el environment e instalar django y py4j:

  $ source env_fuzzyapp/bin/activate
  $ pip install -r requirements.txt

- Probar si se conecta al gateway:

  $ python manage.py shell

  Si los deja en una consola de Python sin errores, todo está de pinga.
  Si explotó, puede ser porque no activaron el environment (paso anterior)
  o no estaba corriendo el gateway (sección anterior)

------------------

API para hacer consultas dentro de Python:

Pueden importar la siguientes funciones en Python:

   from fuzzyapp.database import fuzzyStatement, fuzzyQuery

Las cuales tienen la siguiente firmas:

   def fuzzyStatement(sql)

   Ejecuta una consulta de una vez. No devuelve nada. Sirve para ejecutar
   cosas que no devuelven nada, como INSERT, UPDATE y DELETEs.

- sql es un string con la consulta a ejecutar.


   def fuzzyQuery(sql, columns)

   Devuelve un iterador que devuelve las filas retornadas por la consulta
   en forma de diccionarios de python. La forma en que se generan estas
   filas depende de la definición que se le pase en 'columns'.

- sql es un string con la consulta a ejecutar.
- columns es un diccionario donde se definen qué columnas devuelve esa
  consulta y su tipo. Esto permite que fuzzyQuery pueda leer los objetos
  Java que devuelve el traductor y sepa qué columnas debe extraer y
  a qué tipos de Python convertirlas. Es un miniframework realmente.

Por ejemplo, para usar fuzzyQuery sobre la tabla de personas del paper,
se puede hacer lo siguiente:

fuzzyQuery(
    "SELECT nombre, apellido, edad, sueldo FROM personas",
    columns={
       "nombre": {"type": "string", },
       "apellido": {"type": "string", },
       "edad": {"type": "fuzzy", "subtype_converter": int},
       "sueldo": {"type": "integer", }
    }
)

El diccionario columnas lleva adentro pares columna : definición,
donde la definición es una diccionario que debe tener la clave "type"
indicando su tipo.

Los tipos disponibles son:
"integer"
"string"
"boolean"
"default" (le hace toString() de Java a la columna)
"fuzzy" (nuestro tipo difuso)

"fuzzy" además debe llevar otro argumento, "subtype_converter", el cual
debe ser una función que reciba un string y lo convierta a un valor del
tipo sobre el cual está definido el tipo difuso. Por ejemplo, edad
es un tipo difuso sobre int, entonces hay que pasar una función que reciba
el string devuelto por Postgres y lo convierta a un entero en Python.
En el ejemplo simplemente pasé la función int de python.
