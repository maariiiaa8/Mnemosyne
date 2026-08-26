# Mnemosyne

Aplicación Android para la gestión y visita de museos: exposiciones, obras, reservas de entradas, tienda de merchandising con pagos reales y panel de administración. Trabajo de Fin de Grado (DAM).

[Memoria del TFG](docs/MEMORIA_TFG_MBN.pdf)

## Funcionalidades

**Para el visitante:**
- Explorar museos, exposiciones y obras
- Reservar entradas para exposiciones
- Tienda de merchandising con carrito de compra y pago con tarjeta (Stripe)
- Consultar noticias del museo
- Guardar exposiciones/obras como favoritos
- Perfil de usuario: editar datos, ver mis compras y mis entradas
- "Curiosidad del día" sobre arte

**Para el administrador:**
- Gestión de exposiciones, obras, noticias y stock de la tienda desde un panel propio

## Stack tecnológico

**App**: Kotlin, Jetpack Compose, Navigation Compose, ViewModel + LiveData, Coroutines, Coil (carga de imágenes)
**Backend**: Firebase (Authentication, Firestore, Storage, Cloud Functions)
**Pagos**: Stripe, con el intent de pago generado en una Cloud Function segura (la clave secreta de Stripe nunca viaja a la app)
**Build**: Gradle (Kotlin DSL)

## Arquitectura

```
ui/            → Pantallas Compose + ViewModels, organizadas por feature
                 (auth, home, museos, exposiciones, catalogo, tienda,
                 carrito, reservas, favoritos, perfil, admin)
data/model/    → Modelos de datos (Museo, Exposicion, Obra, Reserva, Stock...)
data/remote/   → Servicios de acceso a Firebase (Auth, Firestore)
data/repository/ → Repositorios que exponen los datos a los ViewModels
functions/     → Cloud Functions (Node.js) para el pago con Stripe
```

## Puesta en marcha

Requisitos: Android Studio, JDK 11+.

1. Clona el repositorio
2. Ábrelo en Android Studio y deja que sincronice Gradle
3. Ejecuta la app en un emulador o dispositivo

Las Cloud Functions (pago con Stripe) ya están desplegadas en el proyecto de Firebase; no hace falta configuración adicional para probar la app.

## Tests

```bash
./gradlew test
```
