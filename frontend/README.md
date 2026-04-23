# Battleship Game Frontend
Vue.js frontend dla gry Battleship. Aplikacja Single-Page (SPA) komunikuj¹ca siê z REST API backendu.
## Technologie
- Vue.js 3
- Vite (bundler)
- Axios (HTTP client)
- CSS 3 (scoped styles)
## Instalacja i uruchomienie
### Instalacja zale¿noœci
```bash
npm install
```
### Development
```bash
npm run dev
```
Serwer dostêpny bêdzie pod adresem `http://localhost:5173`
### Build produkcyjny
```bash
npm run build
```
Wynik bêdzie w folderze `../src/main/resources/static/dist`
## Struktura projektu
```
src/
+¦¦ App.vue              # G³ówny komponent aplikacji
+¦¦ main.js              # Entry point Vue
+¦¦ components/          # Komponenty Vue
-   +¦¦ GameBoard.vue   # Wyœwietlanie plansz gry
-   +¦¦ GameStatus.vue  # Status gry
-   L¦¦ ShipPlacement.vue # Umieszczanie statków
+¦¦ services/           # Serwisy API
-   L¦¦ gameService.js  # Komunikacja z backendem
L¦¦ ...
public/                 # Pliki statyczne
index.html              # HTML entry point
package.json           # Zale¿noœci npm
vite.config.js         # Konfiguracja Vite
```
## Komponenty
### App.vue
G³ówny komponent aplikacji, zarz¹dza stanem gry i komunikacj¹ z API.
**Funkcjonalnoœæ:**
- Ekran startowy
- Wyœwietlanie dwóch plansz (gracza i komputera)
- Zarz¹dzanie stanem gry
- Obs³uga b³êdów
### GameBoard.vue
Komponent wyœwietlaj¹cy planszê 10x10.
**Props:**
- `board` - dane planszy
- `readonly` - czy plansza jest tylko do odczytu
- `highlight` - czy podœwietlaæ pola
**Events:**
- `cell-click` - klikniêcie na komórkê
### ShipPlacement.vue
Formularz do umieszczania statków.
**Events:**
- `place-ship` - wys³anie danych o umieszczeniu statku
### GameStatus.vue
Wyœwietla status gry (stan, gracz na turze, wiadomoœci).
## API Communication
Serwis `gameService.js` zawiera metody do komunikacji z REST API:
- `startGame()` - POST /api/game/start
- `getGameState()` - GET /api/game/state
- `placeShip(shipName, row, col, horizontal)` - POST /api/game/place-ship
- `attack(row, col)` - POST /api/game/attack
- `resetGame()` - POST /api/game/reset
## Rozwi¹zywanie problemów
### CORS errors
Jeœli widzisz b³êdy CORS, upewnij siê, ¿e backend ma w³¹czon¹ konfiguracjê CORS na porcie 5173.
### Timeout b³êdy
Upewnij siê, ¿e backend dzia³a na `http://localhost:8080`
### Komponent siê nie ³aduje
SprawdŸ konsolê przegl¹darki (F12) pod k¹tem b³êdów JavaScript.
## Budowanie i deployowanie
### Razem z Jav¹
```bash
npm run build
```
Wbudo dist folder do `src/main/resources/static/dist`
### Oddzielnie
Mo¿esz buildowaæ frontend oddzielnie i serwowaæ z innego portu (np. nginx).
