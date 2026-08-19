**GameHub App**

Una aplicación móvil Android nativa y completa diseñada para explorar un catálogo global de videojuegos, gestionar títulos favoritos de forma local y registrar reseñas personales.

**Descripción de la App**

GameHub permite al usuario consultar y explorar un catálogo de videojuegos obtenido desde una API REST, visualizando sus datos, realizando búsquedas y accediendo a la información detallada de cada título. Además, permite guardar juegos como favoritos y almacenar reseñas personales localmente incluyendo valoración por estrellas y comentarios, cuenta también con gestión de perfil con selección de fotografía mediante cámara o galería, y configuración de Modo Oscuro, la aplicación fue desarrollada utilizando Kotlin y Jetpack Compose.

**Arquitectura Elegida**

El proyecto utiliza una arquitectura **MVVM (Model-View-ViewModel)** combinada con **Repository Pattern**, separando la interfaz de usuario, la lógica de presentación y el acceso a los datos.

**Capa UI (Compose)**

Desarrollada con Jetpack Compose para las vistas de Inicio, Catálogo, Búsqueda, Detalle, Favoritos, Perfil y Ajustes.

**ViewModel — VideojuegoViewModel**

Administra el estado de la aplicación y contiene la lógica de presentación. Se encarga de coordinar las operaciones entre la interfaz y el Repository, utilizando corrutinas y StateFlow/Flow para gestionar los datos de forma reactiva.

**Repository — VideojuegoRepository**

Actúa como intermediario entre el ViewModel y las fuentes de datos. Centraliza el acceso tanto a los datos remotos obtenidos mediante Retrofit como a los datos almacenados localmente mediante Room.

**Fuentes de Datos**

GameHub utiliza tres fuentes principales de datos, cada una con una función específica:

**1. Datos remotos — FreeToGame API**

La información general de los videojuegos se obtiene desde la API pública FreeToGame mediante Retrofit.

**API utilizada:**

https://www.freetogame.com/api/

**Endpoint principal**

**GET** `/games`

Este endpoint proporciona información como:

* Nombre del videojuego.
* Descripción.
* Género.
* Desarrollador.
* Plataforma.
* Fecha de lanzamiento.
* Imagen.

**Procesamiento:** Retrofit realiza las solicitudes HTTP y Gson convierte las respuestas JSON recibidas en objetos de Kotlin.

**2. Datos locales — Room**

Room se utiliza como base de datos local de la aplicación.

Permite almacenar información que debe permanecer disponible en el dispositivo, principalmente:

* Videojuegos favoritos.
* Reseñas personales de los videojuegos.
* Valoraciones mediante estrellas.
* Comentarios realizados por el usuario.

De esta manera, los datos guardados localmente pueden ser consultados sin depender de una nueva solicitud a la API.

**3. Preferencias del usuario — DataStore**

DataStore se utiliza específicamente para conservar las preferencias y datos de configuración del usuario, actualmente permite mantener, nombre del usuario y preferencia de Modo Oscuro.

La aplicación utiliza DataStore para que estas preferencias permanezcan guardadas, aunque el usuario cierre y vuelva a abrir GameHub.

**Capturas de Pantalla**

A continuación, se incluyen capturas de las principales pantallas y funcionalidades de GameHub:

**Captura 1: Pantalla de Inicio**
<img width="720" height="1600" alt="WhatsApp Image 2026-08-17 at 19 52 39 (6)" src="https://github.com/user-attachments/assets/6f6904ea-1584-4ebf-ba63-132dccc48d9f" />

**Captura 2: Pantalla de Catálogo de videojuegos**
<img width="720" height="1600" alt="WhatsApp Image 2026-08-17 at 19 52 39 (7)" src="https://github.com/user-attachments/assets/b12d5623-9def-446d-8913-d263025df22f" />

**Captura 3: Pantalla de Búsqueda**
<img width="720" height="1600" alt="WhatsApp Image 2026-08-17 at 19 52 39 (5)" src="https://github.com/user-attachments/assets/dc3573f5-13d6-4447-a254-f20af8c1f7fb" />

**Captura 4: Pantalla de Detalle y reseña**
<img width="720" height="1600" alt="WhatsApp Image 2026-08-17 at 19 52 39 (3)" src="https://github.com/user-attachments/assets/8315fe4b-ab9c-4eff-8b76-65200bf52a28" />

**Captura 5: Pantalla de Favoritos**
<img width="720" height="1600" alt="WhatsApp Image 2026-08-17 at 19 52 39 (2)" src="https://github.com/user-attachments/assets/37761278-2586-45d6-8c7a-d64a8d1db048" />

**Captura 6: Pantalla de Perfil**
<img width="720" height="1600" alt="WhatsApp Image 2026-08-17 at 19 52 39 (1)" src="https://github.com/user-attachments/assets/55b0749c-0122-47d7-8d2b-a9b03c60b58d" />

**Captura 7: Pantalla de Ajustes**
<img width="720" height="1600" alt="WhatsApp Image 2026-08-17 at 19 43 58" src="https://github.com/user-attachments/assets/12700ad1-7411-408c-8010-a8296fced459" />

**Captura 8: Diagrama de la Arquitectura**
<img width="811" height="1350" alt="WhatsApp Image 2026-08-18 at 19 27 02" src="https://github.com/user-attachments/assets/27b6cdee-6112-4cb3-a959-b1f869b8b7fd" />
