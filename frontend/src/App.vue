<template>
  <div id="app" class="battleship-app">
    <header class="app-header">
      <h1>Battleship Game</h1>
      <p>Play against the computer</p>
    </header>
    <main class="app-main">
      <div v-if="!gameStarted" class="home-screen">
        <button @click="startGame" class="btn btn-primary btn-large">
          Start New Game
        </button>
      </div>
      <div v-else class="game-screen">
        <div class="game-content">
          <div class="boards-container">
            <div class="board-section">
              <h2>Your Board</h2>
              <GameBoard 
                :board="gameState?.humanBoard"
                @own-cell-click="markOwnShipPosition"
                :human="true"
                :readonly="false"
                :highlight="false"
              />
            </div>
            <div class="board-section">
              <h2>Enemy Board</h2>
              <GameBoard 
                :board="gameState?.computerBoard" 
                @cell-click="handleAttack"
                :readonly="false"
              />
            </div>
          </div>
          <div class="game-info">
            <GameStatus 
              :gameState="gameState"
              :gameMessage="gameMessage"
            />
            <div v-if="gameState?.gameStatus === 'PLACING_SHIPS'" class="placement-section">
              <ShipPlacement @place-ship="handlePlaceShip" />
            </div>
            <div v-if="gameState?.gameOver" class="game-over-section">
              <h3>{{ gameState.winner === 'HUMAN' ? 'You Won!' : 'You Lost!' }}</h3>
              <button @click="resetGame" class="btn btn-primary">
                Play Again
              </button>
            </div>
          </div>
        </div>
      </div>
    </main>
    <div v-if="loading" class="loading-spinner">
      <p>Loading...</p>
    </div>
    <div v-if="error" class="error-message">
      <p>{{ error }}</p>
      <button @click="error = null" class="btn btn-small">Dismiss</button>
    </div>
  </div>
</template>
<script>
import { ref, onMounted } from 'vue'
import GameBoard from './components/GameBoard.vue'
import GameStatus from './components/GameStatus.vue'
import ShipPlacement from './components/ShipPlacement.vue'
import { gameService } from './services/gameService'
export default {
  name: 'App',
  components: {
    GameBoard,
    GameStatus,
    ShipPlacement
  },
  setup() {
    const gameStarted = ref(false)
    const gameState = ref(null)
    const gameMessage = ref('')
    const loading = ref(false)
    const error = ref(null)
    const startGame = async () => {
      loading.value = true
      try {
        const response = await gameService.startGame()
        gameState.value = response.data
        gameStarted.value = true
        error.value = null
      } catch (err) {
        error.value = 'Failed to start game: ' + (err.response?.data?.message || err.message)
      } finally {
        loading.value = false
      }
    }
    const handlePlaceShip = async (shipData) => {
      loading.value = true
      try {
        const response = await gameService.placeShip(
          shipData.shipName,
          shipData.row,
          shipData.col,
          shipData.horizontal
        )
        gameState.value = response.data.gameState
        gameMessage.value = response.data.message
        error.value = null
      } catch (err) {
        error.value = 'Failed to place ship: ' + (err.response?.data?.message || err.message)
      } finally {
        loading.value = false
      }
    }
    const handleAttack = async (row, col) => {
      if (gameState.value?.gameStatus !== 'PLAYING') return
      loading.value = true
      try {
        const response = await gameService.attack(row, col)
        gameState.value = response.data.gameState
        gameMessage.value = response.data.message
        error.value = null
      } catch (err) {
        error.value = 'Failed to attack: ' + (err.response?.data?.message || err.message)
      } finally {
        loading.value = false
      }
    }
    const markOwnShipPosition = async (row, col) => {
          if (gameState.value?.gameStatus !== 'PLACING_SHIPS') return
          loading.value = true
          try {
            alert('You clicked on your own board at (' + row + ', ' + col + '). This is just a demo action.')
            error.value = null
          } catch (err) {
            error.value = 'Failed to attack: ' + (err.response?.data?.message || err.message)
          } finally {
            loading.value = false
          }
        }
    const resetGame = async () => {
      loading.value = true
      try {
        const response = await gameService.resetGame()
        gameState.value = response.data
        gameStarted.value = true
        gameMessage.value = ''
        error.value = null
      } catch (err) {
        error.value = 'Failed to reset game: ' + (err.response?.data?.message || err.message)
      } finally {
        loading.value = false
      }
    }
    return {
      gameStarted,
      gameState,
      gameMessage,
      loading,
      error,
      startGame,
      handlePlaceShip,
      handleAttack,
      markOwnShipPosition,
      resetGame
    }
  }
}
</script>
<style scoped>
.battleship-app {
  font-family: 'Arial', sans-serif;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #333;
}
.app-header {
  background: rgba(0, 0, 0, 0.2);
  color: white;
  padding: 20px;
  text-align: center;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}
.app-header h1 {
  margin: 0;
  font-size: 2.5em;
}
.app-header p {
  margin: 5px 0 0;
  font-size: 1.1em;
  opacity: 0.9;
}
.app-main {
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
}
.home-screen {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 500px;
}
.game-screen {
  background: white;
  border-radius: 10px;
  padding: 30px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}
.game-content {
  display: grid;
  grid-template-columns: 1fr 1fr 300px;
  gap: 30px;
  align-items: start;
}
.boards-container {
  display: flex;
  gap: 30px;
}
.board-section {
  flex: 1;
}
.board-section h2 {
  margin-top: 0;
  color: #667eea;
}
.game-info {
  display: flex;
  flex-direction: column;
  gap: 20px;
}
.placement-section {
  background: #f5f5f5;
  padding: 20px;
  border-radius: 8px;
}
.game-over-section {
  background: #fff3cd;
  padding: 20px;
  border-radius: 8px;
  text-align: center;
}
.loading-spinner {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background: white;
  padding: 40px;
  border-radius: 10px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.3);
  z-index: 1000;
}
.error-message {
  background: #f8d7da;
  border: 1px solid #f5c6cb;
  color: #721c24;
  padding: 15px;
  border-radius: 5px;
  margin-top: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.btn {
  padding: 10px 20px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 1em;
  font-weight: bold;
  transition: all 0.3s ease;
}
.btn-primary {
  background: #667eea;
  color: white;
}
.btn-primary:hover {
  background: #5568d3;
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
}
.btn-small {
  padding: 5px 10px;
  font-size: 0.9em;
}
.btn-large {
  padding: 15px 40px;
  font-size: 1.2em;
}
@media (max-width: 1200px) {
  .game-content {
    grid-template-columns: 1fr 1fr;
  }
  .game-info {
    grid-column: 1 / -1;
  }
}
@media (max-width: 768px) {
  .game-content {
    grid-template-columns: 1fr;
  }
  .boards-container {
    flex-direction: column;
  }
}
</style>
