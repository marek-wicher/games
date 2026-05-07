# Battleship Game Frontend
Vue.js frontend dla gry Battleship. Aplikacja Single-Page (SPA) komunikuj?ca si? z REST API backendu.
## Technologie
- Vue.js 3
- Vite (bundler)
- Axios (HTTP client)
- CSS 3 (scoped styles)
## Instalacja i uruchomienie
### Instalacja zale?no?ci
```bash
npm install
```
### Development
```bash
npm run dev
```
Serwer dost?pny b?dzie pod adresem `http://localhost:5173`
### Build produkcyjny
```bash
npm run build
```
Wynik b?dzie w folderze `../src/main/resources/static/dist`
## Struktura projektu
```
src/
??? App.vue              # G?ówny komponent aplikacji
??? main.js              # Entry point Vue
??? components/          # Komponenty Vue
?   ??? GameBoard.vue   # Wy?wietlanie plansz gry
?   ??? GameStatus.vue  # Status gry
?   ??? ShipPlacement.vue # Umieszczanie statków
??? services/           # Serwisy API
?   ??? gameService.js  # Komunikacja z backendem
??? ...
public/                 # Pliki statyczne
index.html              # HTML entry point
package.json           # Zale?no?ci npm
vite.config.js         # Konfiguracja Vite
```
## Komponenty
### App.vue
G?ówny komponent aplikacji, zarz?dza stanem gry i komunikacj? z API.
**Funkcjonalno??:**
- Ekran startowy
- Wy?wietlanie dwóch plansz (gracza i komputera)
- Zarz?dzanie stanem gry
- Obs?uga b??dów
### GameBoard.vue
Komponent wy?wietlaj?cy plansz? 10x10.
**Props:**
- `board` - dane planszy
- `readonly` - czy plansza jest tylko do odczytu
- `highlight` - czy pod?wietla? pola
**Events:**
- `cell-click` - klikni?cie na komórk?
### ShipPlacement.vue
Formularz do umieszczania statków.
**Events:**
- `place-ship` - wys?anie danych o umieszczeniu statku
### GameStatus.vue
Wy?wietla status gry (stan, gracz na turze, wiadomo?ci).
## API Communication
Serwis `gameService.js` zawiera metody do komunikacji z REST API:
- `startGame()` - POST /api/game/start
- `getGameState()` - GET /api/game/state
- `placeShip(shipName, row, col, horizontal)` - POST /api/game/place-ship
- `attack(row, col)` - POST /api/game/attack
- `resetGame()` - POST /api/game/reset
## Rozwi?zywanie problemów
### CORS errors
Je?li widzisz b??dy CORS, upewnij si?, ?e backend ma w??czon? konfiguracj? CORS na porcie 5173.
### Timeout b??dy
Upewnij si?, ?e backend dzia?a na `http://localhost:8080`
### Komponent si? nie ?aduje
Sprawd? konsol? przegl?darki (F12) pod k?tem b??dów JavaScript.
## Budowanie i deployowanie
### Razem z Jav?
```bash
npm run build
```
Wbuduj dist folder do `src/main/resources/static/dist`
### Oddzielnie
Mo?esz budowa? frontend oddzielnie i serwowa? z innego portu (np. nginx).
