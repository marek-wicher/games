# Battleship Game Frontend
Vue.js frontend dla gry Battleship. Aplikacja Single-Page (SPA) komunikująca się z REST API backendu.
## Technologie
- Vue.js 3
- Vite (bundler)
- Axios (HTTP client)
- CSS 3 (scoped styles)
## Instalacja i uruchomienie
### Instalacja zależności
```bash
npm install
```
### Development
```bash
npm run dev
```
Serwer dostępny będzie pod adresem `http://localhost:5173`
### Build produkcyjny
```bash
npm run build
```
Wynik będzie w folderze `../src/main/resources/static/dist`
## Struktura projektu
```
src/
├── App.vue              # Główny komponent aplikacji
├── main.js              # Entry point Vue
├── components/          # Komponenty Vue
│   ├── GameBoard.vue   # Wyświetlanie planszy gry
│   ├── GameStatus.vue  # Status gry
│   └── ShipPlacement.vue # Umieszczanie statków
├── services/           # Serwisy API
│   └── gameService.js  # Komunikacja z backendem
└── ...
public/                 # Pliki statyczne
index.html              # HTML entry point
package.json           # Zależności npm
vite.config.js         # Konfiguracja Vite
```
## Komponenty
### App.vue
Główny komponent aplikacji, zarządza stanem gry i komunikacją z API.
**Funkcjonalność:**
- Ekran startowy
- Wyświetlanie dwóch plansz (gracza i komputera)
- Zarządzanie stanem gry
- Obsługa błędów
### GameBoard.vue
Komponent wyświetlający planszę 10x10.
**Props:**
- `board` - dane planszy
- `readonly` - czy plansza jest tylko do odczytu
- `highlight` - czy podświetlać pola
**Events:**
- `cell-click` - kliknięcie na komórkę
### ShipPlacement.vue
Formularz do umieszczania statków.
**Events:**
- `place-ship` - wysłanie danych o umieszczeniu statku
### GameStatus.vue
Wyświetla status gry (stan, gracz na turze, wiadomości).
## API Communication
Serwis `gameService.js` zawiera metody do komunikacji z REST API:
- `startGame()` - POST /api/game/start
- `getGameState()` - GET /api/game/state
- `placeShip(shipName, row, col, horizontal)` - POST /api/game/place-ship
- `attack(row, col)` - POST /api/game/attack
- `resetGame()` - POST /api/game/reset
## Rozwiązywanie problemów
### CORS errors
Jeśli widzisz błędy CORS, upewnij się, że backend ma włączoną konfigurację CORS na porcie 5173.
### Timeout błędy
Upewnij się, że backend działa na `http://localhost:8080`
### Komponent się nie ładuje
Sprawdź konsolę przeglądarki (F12) pod kątem błędów JavaScript.
## Budowanie i deployowanie
### Razem z Java
```bash
npm run build
```
Wbuduj dist folder do `src/main/resources/static/dist`
### Oddzielnie
Możesz budować frontend oddzielnie i serwować z innego portu (np. nginx).
