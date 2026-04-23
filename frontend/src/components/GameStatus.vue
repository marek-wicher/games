<template>
  <div class="game-status">
    <div class="status-box">
      <h3>Game Status</h3>
      <p><strong>State:</strong> {{ formatStatus(gameState?.gameStatus) }}</p>
      <p><strong>Current Player:</strong> {{ gameState?.currentPlayer }}</p>
      <p v-if="gameState?.message" class="message">
        {{ gameState.message }}
      </p>
      <div v-if="gameState?.computerBoard" class="stats">
        <p><strong>Enemy Ships:</strong> {{ gameState.computerBoard.shipsRemaining }}/5</p>
        <p><strong>Your Ships:</strong> {{ gameState.humanBoard.shipsRemaining }}/5</p>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  name: 'GameStatus',
  props: {
    gameState: {
      type: Object,
      required: true
    },
    gameMessage: {
      type: String,
      default: ''
    }
  },
  methods: {
    formatStatus(status) {
      if (!status) return 'Unknown'
      return status
        .split('_')
        .map(word => word.charAt(0) + word.slice(1).toLowerCase())
        .join(' ')
    }
  }
}
</script>
<style scoped>
.game-status {
  padding: 0;
}
.status-box {
  background: #f8f9fa;
  border: 2px solid #667eea;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}
.status-box h3 {
  margin-top: 0;
  color: #667eea;
  border-bottom: 2px solid #667eea;
  padding-bottom: 10px;
}
.status-box p {
  margin: 10px 0;
  font-size: 1em;
}
.message {
  background: #e7f3ff;
  border-left: 4px solid #2196F3;
  padding: 10px;
  border-radius: 4px;
  color: #1565c0;
  font-weight: bold;
}
.stats {
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid #ddd;
}
.stats p {
  margin: 8px 0;
  font-size: 0.95em;
}
</style>
