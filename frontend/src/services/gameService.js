import axios from 'axios'
const API_BASE = '/api/game'
const api = axios.create({
  baseURL: API_BASE,
  withCredentials: true
})
export const gameService = {
  startGame: () => api.post('/start'),
  getGameState: () => api.get('/state'),
  placeShip: (shipName, row, col, horizontal) => 
    api.post('/place-ship', { shipName, row, col, horizontal }),
  attack: (row, col) => 
    api.post('/attack', { row, col }),
  resetGame: () => 
    api.post('/reset')
}
export default api
