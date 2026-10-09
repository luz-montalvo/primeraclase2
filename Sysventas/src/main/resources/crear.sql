MERGE INTO categoria (id_categoria, nombre) KEY (id_categoria) VALUES (1, 'Televisor'),
    (2, 'ropas'),
    (3, 'bebidas');

MERGE INTO marca (id_marca, nombre) KEY (id_marca) VALUES (1, 'LG'),
(2, 'Samsumg'),
(3, 'Lenovo');