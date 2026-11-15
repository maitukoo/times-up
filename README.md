# Time's Up (nombre provisional) — juego de equipos para Android

Un solo móvil que se pasan los equipos. Las **mismas cartas** se juegan en 3 rondas:
1. **Descríbelo** — se puede decir cualquier cosa menos el nombre.
2. **Una palabra** — solo una palabra por carta.
3. **Mímica** — sin hablar.

## Qué incluye
- 2–6 equipos con nombre y color, tiempo por turno (30/45/60/90 s), 20–60 cartas por partida.
- 6 mazos (famosos de aquí, del mundo, ficción, históricos, deportistas, música) + **cartas propias** guardadas en el móvil.
- Al final de cada turno se pueden anular aciertos dudosos (la carta vuelve al mazo).
- Si el mazo se acaba en mitad de un turno, ese equipo empieza la siguiente ronda con el tiempo sobrante.
- Pasar desactivado en la ronda 1 (como el original), activable en ajustes.
- La partida se guarda al terminar cada turno: si se cierra la app, "Continuar partida".
- Pausa automática al salir de la app o con el botón atrás.

## Estructura
- `app/src/main/assets/index.html` → todo el juego (HTML/JS). Los mazos están en el array `CATS`.
- `MainActivity.kt` → WebView a pantalla completa, botón atrás y vibración nativa.

## APK
GitHub Actions compila en cada push (pestaña *Actions* → *Artifacts*). Se firma con la clave de Oscar
(`keystore/alafrente.jks.enc`, cifrada) usando el secreto `KEYSTORE_PASSWORD`; sin el secreto, se firma con la clave debug.
