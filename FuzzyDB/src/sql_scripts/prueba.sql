create fuzzy domain dominio_tipo3 as values('A', 'B', 'C', 'D') similarity{('A', 'B')/1.0, ('C', 'A')/0.5, ('C', 'D')/0.3}
CREATE FUZZY DOMAIN dominio_tipo5 AS POSSIBILITY DISTRIBUTION ON dominio_tipo3;
create table tabla_tipo5(a1 integer, a2 dominio_tipo5)
insert into tabla_tipo5 values (45322, {f 0.5/'A', 1.0/'B'})

-- Ejemplo de Pokemon del otro grupo
CREATE FUZZY DOMAIN tipos_pokemon AS VALUES ('Planta','Fuego','Agua','Electrico') SIMILARITY {('Planta','Fuego')/0.4,('Planta','Agua')/0.7,('Fuego','Electrico')/0.8,('Agua','Electrico')/0.6};
CREATE FUZZY DOMAIN alturas_pokemon AS POSSIBILITY DISTRIBUTION ON INTEGER;
CREATE TABLE pokemon (Nombre VARCHAR NOT NULL PRIMARY KEY, Altura alturas_pokemon, tipo tipos_pokemon);
INSERT INTO pokemon(nombre, altura, tipo) VALUES ('Pichu', {f(5,10,15,20)}, 'Electrico');
INSERT INTO pokemon(nombre, altura, tipo) VALUES ('Pikachu', {f(5,15,25,35)}, 'Electrico');
INSERT INTO pokemon(nombre, altura, tipo) VALUES ('Charmander', {f(20,30,50,60)}, 'Fuego');
INSERT INTO pokemon(nombre, altura, tipo) VALUES ('Squirtle', {f(10,15,35,50)}, 'Agua');
INSERT INTO pokemon(nombre, altura, tipo) VALUES ('Bulbasaur', {f(10,20,30,40)}, 'Planta');
INSERT INTO pokemon(nombre, altura, tipo) VALUES ('Blastoise', {f(90,120,150,180)}, 'Agua');
INSERT INTO pokemon(nombre, altura, tipo) VALUES ('Venusaur', {f(80,100,140,160)}, 'Planta');
INSERT INTO pokemon(nombre, altura, tipo) VALUES ('Charizard', {f(100,130,170,200)}, 'Fuego');

-- ORDER BY, GROUP BY tipo2
SELECT nombre, altura FROM pokemon ORDER BY altura;
SELECT altura, count(*) FROM pokemon GROUP BY altura ORDER BY altura;

-- ORDER BY tipo3
SELECT nombre, tipo FROM pokemon ORDER BY tipo STARTING FROM 'Electrico';
SELECT nombre, tipo FROM pokemon ORDER BY tipo STARTING FROM 'Electrico', nombre;
SELECT nombre, tipo FROM pokemon ORDER BY SIMILARITY ON tipo STARTING FROM 'Electrico'; -- SIN FROM, informe dice que es con FROM
SELECT nombre, tipo FROM pokemon ORDER BY SIMILARITY ON tipo STARTING 'Electrico', nombre;
SELECT nombre, tipo FROM pokemon ORDER BY SIMILARITY ON tipo START 'Electrico';
SELECT nombre, tipo FROM pokemon ORDER BY SIMILARITY ON tipo START 'Electrico', nombre;
-- GROUP BY tipo3
SELECT nombre, count(*) FROM pokemon GROUP BY nombre;
SELECT tipo, count(*) FROM pokemon GROUP BY SIMILAR tipo;
SELECT tipo, count(*) FROM pokemon GROUP BY tipo;
SELECT tipo, nombre, count(*) FROM pokemon GROUP BY nombre, tipo;
SELECT tipo, nombre, count(*) FROM pokemon GROUP BY SIMILAR tipo, nombre; -- ??????? =S

SELECT tipo, count(*) FROM pokemon GROUP BY SIMILAR tipo ORDER BY tipo STARTING FROM 'Electrico'; -- ???????

-- Query Mixto
SELECT nombre, altura, tipo FROM pokemon ORDER BY tipo STARTING FROM 'Electrico';

-- Create fuzzy domain tipo 3:

-- Sintaxis alterna 1:
create table tabla_prueba_tipo3_1(id integer, etiquetas TEXT);
insert into tabla_prueba_tipo3_1 values (1, 'A'); insert into tabla_prueba_tipo3_1 values (2, 'B'); insert into tabla_prueba_tipo3_1 values (3, 'C');
create fuzzy domain dominio_prueba_tipo3_1 as values from tabla_prueba_tipo3_1.etiquetas; -- no funciona, null en deparser --> arreglado