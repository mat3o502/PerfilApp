# PerfilApp

Aplicación Android (Kotlin, patrón MVVM) con registro de usuarios, inicio de sesión y pantalla de perfil con foto tomada con la cámara o elegida de la galería. Backend en Node.js + Express y base de datos relacional MySQL/MariaDB.

## Estructura

- `app/`: aplicación Android.
- `backend/`: API REST en Node.js.

## Cómo ejecutar

1. Iniciar MySQL (por ejemplo desde XAMPP) y ejecutar `backend/database/script.sql`.
2. En `backend/`: copiar `.env.example` como `.env`, luego `npm install` y `npm run dev`.
3. En `app/app/src/main/java/com/perfilapp/data/network/RetrofitClient.kt`, cambiar `BASE_URL` por la IP de la PC donde corre el backend.
4. Abrir la carpeta `app/` en Android Studio y ejecutar en un teléfono conectado a la misma red WiFi.

La foto de perfil se guarda únicamente en el almacenamiento privado del teléfono; nunca se envía al backend ni a la base de datos.