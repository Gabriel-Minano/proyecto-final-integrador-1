Versión 0.6.a | Actual
- [Solucionado] corregí un error relacionado a que no se renderizaba la fecha del producto a vencer debido a que faltó poner el formateador en la fecha de vencimiento de la entidad Producto (	@DateTimeFormat(pattern = "yyyy-MM-dd") ) -> Gabriel
- [Solucionado] Corregí el error relacionado a que no activava el producto, puesto que había un input invisible en el formulario de Productos que seteaba el valor de estado por defecto y Thymeleaf usaba ese valor primero antes del editado, para ello eliminé dicho input hidden. -> Gabriel
- [Error] Corregir que no se actualiza el estado de Producto al reactivar, clientes funciona utilizar eso para la corrección -> Gabriel
- [Añadir | Priodidad Media] La reactivación de Categorias, Proveedores y Usuarios, junto al select de Inactivo, Activo, Todos. -> James
- [Añadir | Priodidad Baja] En las listas de las tablas poner todos las columnas.

- [Añadir | Priodidad Baja] Añadir la barra de navegación a los formularios (side-bar | menú de pestañás), añadir la seguridad a las páginas y los diseños de los formularios.
- [Añadir | Priodidad Baja] Cambiar en el dashboard, en el gráfico el "Ventas por días" por "Ingresos del día". -> James
- [Añadir | Priodidad Baja] Cambiar en el dashboard, el filtro de año, ya que por el momento solo muestra 2025 y 2026, debe permitir o ir creciendo. Y si crece mucho se podría utilizar la librería Select2 de JavaScript. -> James


Versión 0.5
- [Actualizacion] se completo una versión totalmente funcional del módulo de reportes, se ubican en Productos y Ventas.
- [Solucionado] Arreglar el error de transito de datos, al dejar en blanco el proveedor o categoría de un producto, este bug es causado por el select. En todo caso hay que agregar una validación por JS
Error:
Caused by: java.lang.IllegalStateException: org.hibernate.TransientPropertyValueException: Instance of 'com.sistema.botica.entity.Producto' references an unsaved transient instance of 'com.sistema.botica.entity.Categoria' (persist the transient instance) [com.sistema.botica.entity.Producto.categoria -> com.sistema.botica.entity.Categoria] -> *Faltaría mejorar la vista y corregir algunos detalles menores como la disposición del mensaje*

- [A mejorar] En el dashboard añadir más años, solo hay 2025 y 2026
- [*Solucionado*] Solucionar el error que muestra dos veces el mensaje "No se puede registrar una venta sin productos en el detalle" ubicado en el Módulo de Ventas, al momento de registrar una venta, Por otro lado añadir un dismiss, para ocultar el mensaje de error, de la misma forma corregir el tamaño del mensaje al momento de concretar una venta, puesto que es muy pequeño y el botón de X se sale.

- [*Solucionado parcialmente*] Añadir la posibilidad de que los empleados puedan acceder al módulo de productos, categorías y ventas, pero que no tengan acceso a reportes, edición, agregación o eliminación. *Falta asegurar los formularios*

- [*Solucionado parcialmente*] Añadir paginación en Ventas, Productos y Clientes. Los otros no requieren de la paginación. En Ventas, poner un filtro obligatorio que especifique que se debe seleccionar entre meses, puesto que paginar todo un año no es adecuado y ocasionará desbordamiento. *Falta actualizar los módulos de usuarios, categorías y proveedores*
- [*Solucionado parcialmente*] En todos los formularios añadir la posibilidad de reactivar los datos desde la edición, para ello se debe filtrar los productos, categorías, empleados, proveedores y clientes desactivados. *Solo agregué para clientes y productos, falta usuarios, categorías etc.*

- [A mejorar | Media prioridad] Mejorar la presentación de las tablas, poner todos los campos en las columnas o cabeceras, evitar traer los datos de salto (Enlaces con otras tablas, join references, etc).
- [A mejorar | baja prioridad] Cambiar los iconos de los módulos por iconos de BootStrap, para mejorar el diseño *Por el momento le quité los íconos por que eran demasiado feos*
