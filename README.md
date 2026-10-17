# VigíaRuta

App para reloj Wear OS que detecta cansancio en conductores con el sensor de pulso y el acelerómetro. Las alertas se mandan al celular y se guardan en SQLite.

Proyecto de Dispositivos Inteligentes - UTTT
Ángel Alexis Blas Castillo

## Estructura

- `wear`: app del reloj
- `mobile`: app del celular
- `shared`: modelo del evento que usan las dos apps

## Cómo correrlo

1. Abrir el proyecto en Android Studio y esperar el Gradle Sync.
2. Crear un emulador de Wear OS y uno de teléfono, los dos con Google Play.
3. Emparejarlos en Device Manager (Pair Wearable).
4. Correr `mobile` en el teléfono y `wear` en el reloj.
5. En el reloj dejar Simulación en sí y darle Iniciar. La primera alerta sale como al minuto.

Para las pruebas del detector:

```
./gradlew :wear:testDebugUnitTest
```
