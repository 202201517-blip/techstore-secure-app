INSERT INTO categories (name, description) VALUES
('Celulares', 'Teléfonos inteligentes'),
('Laptops', 'Computadoras portátiles'),
('Accesorios', 'Accesorios tecnológicos'),
('Audio', 'Audífonos y dispositivos de sonido');

INSERT INTO products (category_id, name, description, price, stock, image_url) VALUES
(1, 'Smartphone Galaxy S23', 'Smartphone de gama alta color negro', 14999.00, 10, 'galaxy_s23.png'),
(2, 'Laptop ASUS ROG Strix', 'Laptop gamer de alto rendimiento', 28999.00, 5, 'asus_rog.png'),
(4, 'Audífonos Sony WH-1000XM5', 'Audífonos inalámbricos con cancelación de ruido', 6499.00, 8, 'sony_wh1000xm5.png'),
(3, 'Smartwatch Xiaomi Watch 2', 'Reloj inteligente deportivo', 3499.00, 12, 'xiaomi_watch_2.png');
