# Mühlespiel

Mühle (Nine Men's Morris) als Java-Desktop-Anwendung mit Swing-Oberfläche, entwickelt im
Modul Software Engineering (FS 2025, OST) von Silvio Bellini, Christian Sutter, Ryan Mikus
und Adrian Kretz.

## Funktionen

- Spiel gegen eine zweite Person oder gegen den Computer
- Computergegner in drei Schwierigkeitsstufen (Easy, Medium, Hard)
- Alle Spielphasen: Setzen, Ziehen und Springen
- Undo/Redo von Zügen und Zug-Timer
- Speichern und Laden von Spielständen (JSON)

## Starten

Einstiegspunkt ist `view.GUI` (`src/view/GUI.java`). Für das Speichern und Laden wird
[Gson](https://github.com/google/gson) im Classpath benötigt.

## Struktur

```
src/
├── boardLogic/     Spielbrett, Regeln, Spielablauf, Timer
├── phases/         Setz-, Zieh-, Sprung- und Game-Over-Phase
├── players/        Menschliche und Computer-Spieler
├── difficulties/   Strategien des Computergegners
├── persistance/    Speichern/Laden und Zug-Historie
├── view/           Swing-Oberfläche
└── resources/      Bilder und Animationen
JavaDoc/            Generierte API-Dokumentation
Abgabedokumente/    Benutzerdokumentation, Scrum-Bericht, Schlusspräsentation
```
