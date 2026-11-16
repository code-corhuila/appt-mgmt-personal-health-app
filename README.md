# Salud Activa — Personal Health App

Aplicación Android independiente para consultar y actualizar los datos de contacto del perfil de
Salud Activa. Este repositorio implementa la interfaz móvil del bounded context `personal-health`.

## Funcionalidad

- Inicio de sesión en Salud Activa con credenciales existentes.
- Persistencia cifrada del token JWT y los datos básicos del usuario.
- Vista del nombre, correo y teléfono asociados a la cuenta.
- Edición del nombre y teléfono con validación antes de sincronizar.
- Cierre de sesión y manejo de sesión expirada.

El backend actual solo define `nombre`, `email` y `telefono` para el usuario. La app no inventa ni
persiste campos clínicos como alergias o grupo sanguíneo que el servicio no soporta. El correo se
muestra como dato de cuenta y no es editable en este flujo.

## Requisitos y ejecución

- Android Studio con Android SDK 34.
- JDK 17.
- Emulador o dispositivo Android 7.0 (API 24) o superior.

```powershell
.\gradlew.bat testDebugUnitTest assembleDebug
```

La URL predeterminada de la API es
`https://saludactiva-backend.onrender.com/`. Se puede sustituir con
`-PAPI_BASE_URL=https://api.example.com/`.

## API utilizada

| Operación | Método y ruta |
|---|---|
| Iniciar sesión | `POST /api/auth/login` |
| Actualizar el perfil autenticado | `PUT /api/auth/profile` |

El inicio de sesión usa el cuerpo `email` / `contrasena` y recibe `token` y `usuario`. La
actualización envía `nombre` y `telefono`; la identidad se deriva del JWT y no de un ID enviado por
el cliente.

## Flujo del repositorio

Los cambios entran por ramas `hu-xxx-dev` y Pull Request a `develop`. La promoción a `qa` y luego
`main` se realiza por Pull Requests y re-aplicación del cambio (`git cherry-pick -x`), según la
gobernanza del curso en
[`library-docs`](https://github.com/code-corhuila/library-docs). No se deben modificar directamente
las ramas protegidas.
