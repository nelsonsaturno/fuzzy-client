create fuzzy domain dominio_tipo3 as values('A', 'B', 'C', 'D') similarity{('A', 'B')/1.0, ('C', 'A')/0.5, ('C', 'D')/0.3}
CREATE FUZZY DOMAIN dominio_tipo5 AS POSSIBILITY DISTRIBUTION ON dominio_tipo3;
create table tabla_tipo5(a1 integer, a2 dominio_tipo5)
