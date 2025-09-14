CREATE TABLE IF NOT EXISTS roles
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS users
(
    id       SERIAL PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255)        NOT NULL,
    enabled  BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS user_roles
(
    user_id INT NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_role FOREIGN KEY (role_id) REFERENCES roles (id)
);

CREATE TABLE IF NOT EXISTS customers
(
    id         SERIAL PRIMARY KEY,
    first_name VARCHAR(100)        NOT NULL,
    last_name  VARCHAR(100)        NOT NULL,
    email      VARCHAR(150) UNIQUE NOT NULL
);
