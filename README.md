#### SOBRE ESTE PROYECTO

##### DESCRIPCIÓN
- Proyecto realizado mediante la utilización del Framework Spring Boot 4.0.6 para el backend (Servidor), para el frontend se utilizo el motor de plantillas thymeleaf junto con Bootstrap v5.3.

##### EJECUCIÓN
**Configuración por defecto**
- Puerto: 8089
- Requiere la creación de una base de datos con el nombre: botica_db2
- Cambiar el usuario y contraseña en función a la configuración de MySQL.

#### REGISTRO DE CAMBIOS
Versión 0.7 | Actual
- [Solucionado] corregí un error relacionado a que no se renderizaba la fecha del producto a vencer debido a que faltó poner el formateador en la fecha de vencimiento de la entidad Producto (	@DateTimeFormat(pattern = "yyyy-MM-dd") ) -> Gabriel
- [Solucionado] Corregí el error relacionado a que no activava el producto, puesto que había un input invisible en el formulario de Productos que seteaba el valor de estado por defecto y Thymeleaf usaba ese valor primero antes del editado, para ello eliminé dicho input hidden. -> Gabriel
- [Solucionado] Corregir que no se actualiza el estado de Producto al reactivar, clientes funciona utilizar eso para la corrección -> Gabriel
- [Añadir | Priodidad Media] La reactivación de Categorias, Proveedores y Usuarios, junto al select de Inactivo, Activo, Todos. -> James
- [Añadir | Priodidad Baja] En las listas de las tablas poner todos las columnas.
- [Añadir | Priodidad Baja] Añadir la barra de navegación a los formularios (side-bar | menú de pestañás), añadir la seguridad a las páginas y los diseños de los formularios.
- [solucionado | Priodidad Baja] Cambiar en el dashboard, en el gráfico el "Ventas por días" por "Ingresos del día". -> James
- [solucionado | Priodidad Baja] Cambiar en el dashboard, el filtro de año, ya que por el momento solo muestra 2025 y 2026, debe permitir o ir creciendo. Y si crece mucho se podría utilizar la librería Select2 de JavaScript. -> James

