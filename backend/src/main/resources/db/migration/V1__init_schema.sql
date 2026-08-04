create table roles(
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(50)
);
CREATE TABLE usuarios(
    id BIGSERIAL PRIMARY KEY,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(128)  NOT NULL UNIQUE,
    activo BOOLEAN NOT NULL DEFAULT  TRUE,
    rol_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_usuario_rol
        FOREIGN KEY (rol_id)
        REFERENCES roles(id)
);
INSERT INTO roles(nombre, descripcion)
VALUES
    ('ADMINISTRADOR','Acceso total'),
    ('SUPERVISOR','Supervisa trabajadores'),
    ('TRABAJADOR','Usuario operativo');