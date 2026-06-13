# Simulador de Cartera de Inversión

Aplicación web full stack para simular una cartera de inversión, registrar operaciones y visualizar la evolución del patrimonio de forma sencilla.

El proyecto permite gestionar activos financieros ficticios, operaciones de compra, venta y dividendos, así como consultar un resumen de la cartera mediante KPIs, tablas y gráficas.

> Este proyecto es únicamente un simulador educativo. No constituye asesoramiento financiero ni recomienda comprar o vender ningún activo.


## Tecnologías utilizadas

### Backend

* Java 17
* Spring Boot
* Spring Data JPA
* Hibernate
* H2 Database
* Maven
* API REST

### Frontend

* React
* TypeScript
* Vite
* Recharts
* CSS

## Funcionalidades

### Dashboard

* Valor total de la cartera.
* Dinero invertido.
* Beneficio o pérdida total.
* Rentabilidad porcentual.
* Dividendos cobrados.
* Número de activos.
* Gráfico de evolución de la cartera.
* Gráfico de distribución por tipo de activo.
* Tabla de posiciones actuales.

### Gestión de activos

* Listado de activos.
* Búsqueda por nombre o ticker.
* Creación de nuevos activos.
* Edición de activos.
* Eliminación de activos.
* Clasificación por tipo:

  * Acción
  * ETF
  * Crypto
  * Bono
  * Fondo

### Gestión de operaciones

* Registro de compras.
* Registro de ventas.
* Registro de dividendos.
* Filtro por tipo de operación.
* Cálculo automático del importe total.
* Validación para evitar ventas superiores a la cantidad disponible.

### Mi cartera

* Posición actual por activo.
* Cantidad total.
* Precio medio de compra.
* Precio actual.
* Coste total.
* Valor actual.
* Beneficio o pérdida.
* Rentabilidad.
* Dividendos cobrados.
* Peso de cada activo dentro de la cartera.

## Datos iniciales

La aplicación carga datos ficticios al arrancar para poder probarla sin configuración adicional.

Incluye ejemplos de:

* activos simulados
* operaciones de compra
* operaciones de venta
* dividendos
* evolución de cartera

Todos los datos son ficticios y se usan únicamente con fines demostrativos.

## Estructura del proyecto

```text
simulador-cartera-inversion/
├── backend/
│   ├── src/
│   └── pom.xml
├── frontend/
│   ├── src/
│   └── package.json
├── docs/
│   ├── dashboard.png
│   ├── activos.png
│   ├── operaciones.png
│   └── cartera.png
├── README.md
└── .gitignore
```

## Cómo ejecutar el proyecto

Para ejecutar la aplicación es necesario arrancar primero el backend y después el frontend.

### Backend

Requisitos:

* Java 17
* Maven

Desde la carpeta del proyecto:

```bash
cd backend
mvn spring-boot:run
```

El backend se ejecuta en:

```text
http://localhost:8080
```

La base de datos H2 se crea en memoria al arrancar la aplicación.

### Frontend

Requisitos:

* Node.js
* npm

Desde la carpeta del proyecto:

```bash
cd frontend
npm install
npm run dev
```

El frontend se ejecuta en:

```text
http://localhost:5173
```


## Aviso

La aplicación no utiliza datos reales de mercado ni realiza recomendaciones de inversión. Los activos, precios y operaciones son ficticios y se incluyen únicamente para demostrar el funcionamiento técnico del proyecto.

## Posibles mejoras futuras

