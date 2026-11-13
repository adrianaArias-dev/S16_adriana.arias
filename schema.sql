-- Base de datos "cine" ya la crea Docker (MYSQL_DATABASE=cine)
USE cine;

CREATE TABLE IF NOT EXISTS libros (
    id_libro         INT AUTO_INCREMENT PRIMARY KEY,
    titulo           VARCHAR(150) NOT NULL,
    autor            VARCHAR(100) NOT NULL,
    editorial        VARCHAR(100) NOT NULL,
    categoria        VARCHAR(60)  NOT NULL,
    `año_publicacion` INT         NOT NULL,
    cantidad         INT          NOT NULL DEFAULT 0,
    estado           VARCHAR(20)  NOT NULL DEFAULT 'Disponible'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
