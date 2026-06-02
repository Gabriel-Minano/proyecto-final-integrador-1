- Arreglar el error de transito de datos, al dejar en blanco el proveedor o categoría de un producto, este bug es causado por el select. En todo caso hay que agregar una validación por JS
Error:
Caused by: java.lang.IllegalStateException: org.hibernate.TransientPropertyValueException: Instance of 'com.sistema.botica.entity.Producto' references an unsaved transient instance of 'com.sistema.botica.entity.Categoria' (persist the transient instance) [com.sistema.botica.entity.Producto.categoria -> com.sistema.botica.entity.Categoria]

- En el dashboard añadir más años, solo hay 2025 y 2026
- Solucionar el error que muestra dos veces el mensaje "No se puede registrar una venta sin productos en el detalle" ubicado en el Módulo de Ventas, al momento de registrar una venta, Por otro lado añadir un dismiss, para ocultar el mensaje de error, de la misma forma corregir el tamaño del mensaje al momento de concretar una venta, puesto que es muy pequeño y el botón de X se sale.

- Añadir la posibilidad de que los empleados puedan acceder al módulo de productos, categorías y ventas, pero que no tengan acceso a reportes, edición, agregación o eliminación.

- Añadir paginación en Ventas, Productos y Clientes. Los otros no requieren de la paginación. En Ventas, poner un filtro obligatorio que especifique que se debe seleccionar entre meses, puesto que paginar todo un año no es adecuado y ocasionará desbordamiento.
- En todos los formularios añadir la posibilidad de reactivar los datos desde la edición, para ello se debe filtrar los productos, categorías, empleados, proveedores y clientes desactivados.

-Mejorar la presentación de las tablas, poner todos los campos en las columnas o cabeceras, evitar traer los datos de salto (Enlaces con otras tablas, join references, etc).
