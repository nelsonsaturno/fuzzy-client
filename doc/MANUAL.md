# Manual de desarrollo

## JSqlParser

Este es un fork de JSqlParser, cuyo sitio oficial es http://jsqlparser.sourceforge.net/.

Actualmente no parece estar siendo mantenido, sin embargo, hay un fork en Github
(https://github.com/JSQLParser/JSqlParser) que si parece estar siendo mantenido. (01/06/2014)

#### Dependencias

- JavaCC
- Ant

#### Compilación

Desde el directorio principal hacer:

    $ ant clean jar

#### Estructura interna

La definición de la gramática se encuentra en src/net/sf/jsqlparser/parser/JSqlParserCC.jj

Las clases en expression/, schema/ y statement/ contienen las clases que conforman el árbol
de sintaxis. La mejor manera de entender cómo encaja todo es leer la gramática e identificar
qué clases se utilizan para construir los árboles de las principales sentencias SQL.

Para recorrer el árbol de sintaxis generado, JSqlParser implementa el patrón visitante
(http://en.wikipedia.org/wiki/Visitor_pattern). Las principales interfaces Visitor que interesa
conocer son statement/StatementVisitor.java, y expression/ExpressionVisitor.java.

En el directorio util/deparser/ se encuentran una serie de clases que implementan un
Visitor para el árbol de sintaxis, cuyo resultado es la representación en String del árbol.

#### Modificación

Es necesario modificar este proyecto cuando sea necesario cambiar o agregar elementos de sintaxis
a SQL. Para ello es necesario modificar JSqlParserCC.jj para cambiar la gramática, y cambiar o
crear clases para el árbol de sintaxis.

Cuando se creen clases nuevas, especialmente aquellas que hereden de Expression o Statement, será
necesario alterar la interfaz en StatementVisitor.java o ExpressionVisitor.java para agregar
la nueva clase. Esto luego va hacer necesario actualizar cualquier implementación (dentro o fuera de
JSqlParser) de estos Visitor.
Por ejemplo, hará falta actualizar util/deparser/ para que tome en cuenta la nueva clase.

#### TODO / Wishlist

No tengo idea de cuál fue la versión base de este fork de JSqlParser, pero lo que si es seguro
es que no es la más reciente. Una mejora interesante sería actualizar este fork con los cambios
upstream.

Eso probablemente será complicado porque se han hecho bastantes cambios. Una mejora aún más
interesante entonces sería refactorizar este fork de forma que sea fácil mezclar continuamente
los cambios que se hagan upstream.

Esto es importante porque, hasta donde vi, los cambios upstream agregan elementos a la sintaxis
bastante importantes, tal como poder declarar restricciones FOREIGN KEY en la creación de tablas.
