-- Ejecutar una sola vez sobre una base eurekabank ya creada.
-- La columna global permite ordenar movimientos de distintas cuentas por inserción real.
ALTER TABLE Movimiento
    ADD COLUMN id_movimiento BIGINT NOT NULL AUTO_INCREMENT UNIQUE FIRST;
