USE tienda_acacios;

/* Triggers para automatización y validaciones del sistema. @author RADJ */

DELIMITER $$


/* Valida cantidad positiva en movimientos. @author RADJ */

CREATE TRIGGER trg_validar_cantidad_movimiento

BEFORE INSERT
ON movimientos_inventario

FOR EACH ROW

BEGIN

    IF NEW.cantidad <= 0 THEN

        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT =
        'La cantidad debe ser mayor a cero';

    END IF;

END$$



/* Valida precio de compra positivo. @author RADJ */

CREATE TRIGGER trg_validar_precio_compra

BEFORE INSERT
ON productos

FOR EACH ROW

BEGIN

    IF NEW.precio_compra < 0 THEN

        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT =
        'El precio de compra no puede ser negativo';

    END IF;

END$$



/* Valida precio de venta positivo. @author RADJ */

CREATE TRIGGER trg_validar_precio_venta

BEFORE INSERT
ON productos

FOR EACH ROW

BEGIN

    IF NEW.precio_venta < 0 THEN

        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT =
        'El precio de venta no puede ser negativo';

    END IF;

END$$



/* Valida IVA correcto. @author RADJ */

CREATE TRIGGER trg_validar_iva

BEFORE INSERT
ON productos

FOR EACH ROW

BEGIN

    IF NEW.iva < 0 THEN

        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT =
        'El IVA no puede ser negativo';

    END IF;

END$$



/* Valida stock mínimo positivo. @author RADJ */

CREATE TRIGGER trg_validar_stock_minimo

BEFORE INSERT
ON productos

FOR EACH ROW

BEGIN

    IF NEW.stock_minimo < 0 THEN

        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT =
        'El stock mínimo no puede ser negativo';

    END IF;

END$$



/* Valida subtotal en detalle de ventas. @author RADJ */

CREATE TRIGGER trg_validar_subtotal_venta

BEFORE INSERT
ON detalle_ventas

FOR EACH ROW

BEGIN

    IF NEW.subtotal <= 0 THEN

        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT =
        'El subtotal de venta debe ser mayor a cero';

    END IF;

END$$



/* Valida subtotal en detalle de compras. @author RADJ */

CREATE TRIGGER trg_validar_subtotal_compra

BEFORE INSERT
ON detalle_compras

FOR EACH ROW

BEGIN

    IF NEW.subtotal <= 0 THEN

        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT =
        'El subtotal de compra debe ser mayor a cero';

    END IF;

END$$



/* Evita ventas sin productos (cantidad > 0). @author RADJ */

CREATE TRIGGER trg_validar_cantidad_venta

BEFORE INSERT
ON detalle_ventas

FOR EACH ROW

BEGIN

    IF NEW.cantidad <= 0 THEN

        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT =
        'La cantidad vendida debe ser mayor a cero';

    END IF;

END$$



/* Evita compras sin productos (cantidad > 0). @author RADJ */

CREATE TRIGGER trg_validar_cantidad_compra

BEFORE INSERT
ON detalle_compras

FOR EACH ROW

BEGIN

    IF NEW.cantidad <= 0 THEN

        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT =
        'La cantidad comprada debe ser mayor a cero';

    END IF;

END$$



/* Valida formato de correo de usuario. @author RADJ */

CREATE TRIGGER trg_validar_email_usuario

BEFORE INSERT
ON usuarios

FOR EACH ROW

BEGIN

    IF NEW.email NOT LIKE '%@%' THEN

        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT =
        'Correo electrónico inválido';

    END IF;

END$$



/* Valida formato de correo de proveedor. @author RADJ */

CREATE TRIGGER trg_validar_email_proveedor

BEFORE INSERT
ON proveedores

FOR EACH ROW

BEGIN

    IF NEW.email IS NOT NULL
    AND NEW.email NOT LIKE '%@%' THEN

        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT =
        'Correo del proveedor inválido';

    END IF;

END$$



/* Valida formato de correo de cliente. @author RADJ */

CREATE TRIGGER trg_validar_email_cliente

BEFORE INSERT
ON clientes

FOR EACH ROW

BEGIN

    IF NEW.email IS NOT NULL
    AND NEW.email NOT LIKE '%@%' THEN

        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT =
        'Correo del cliente inválido';

    END IF;

END$$

DELIMITER ;