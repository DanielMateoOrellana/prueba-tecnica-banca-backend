-- Inserts del caso de uso de la prueba tecnica.
-- Las contrasenas son hashes BCrypt de "1234" (cost 10).

USE banca;

-- ===== CLIENTES =====
INSERT INTO cliente (nombre, genero, edad, identificacion, direccion, telefono, cliente_id, contrasena, estado) VALUES
('Jose Lema',         'MASCULINO', 35, '0102030405', 'Otavalo sn y principal',     '098254785', 'jlema',     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', TRUE),
('Marianela Montalvo','FEMENINO',  29, '0102030406', 'Amazonas y NNUU',            '097548965', 'mmontalvo', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', TRUE),
('Juan Osorio',       'MASCULINO', 41, '0102030407', '13 junio y Equinoccial',     '098874587', 'josorio',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', TRUE),
('Leyner Rivera',     'MASCULINO', 33, '0102030408', 'Av. Republica y La Pradera', '098112233', 'lrivera',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', TRUE);

-- ===== CUENTAS =====
INSERT INTO cuenta (numero_cuenta, tipo_cuenta, saldo_inicial, saldo_actual, estado, cliente_id_fk) VALUES
('478758', 'AHORRO',    2000.00, 2000.00, TRUE, (SELECT id FROM cliente WHERE cliente_id = 'jlema')),
('225487', 'CORRIENTE',  100.00,  100.00, TRUE, (SELECT id FROM cliente WHERE cliente_id = 'mmontalvo')),
('495878', 'AHORRO',       0.00,    0.00, TRUE, (SELECT id FROM cliente WHERE cliente_id = 'josorio')),
('496825', 'AHORRO',     540.00,  540.00, TRUE, (SELECT id FROM cliente WHERE cliente_id = 'lrivera')),
('585545', 'CORRIENTE', 1000.00, 1000.00, TRUE, (SELECT id FROM cliente WHERE cliente_id = 'jlema'));

-- ===== MOVIMIENTOS DEL CASO DE USO =====
-- Los movimientos del PDF se ejecutan a traves del endpoint POST /movimientos para que la
-- logica de negocio (cupo diario, saldo no disponible) se aplique. Aqui solo dejamos
-- saldo_actual igual al saldo_inicial; ejecuta los movimientos via Postman para reproducir
-- el caso 4 del enunciado y obtener el reporte esperado.
