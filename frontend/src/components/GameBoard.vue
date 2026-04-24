<template>
  <div class="game-board">
    <div class="grid">
      <div 
        v-for="row in board?.grid" 
        :key="row[0].row"
        class="board-row"
      >
        <div 
          v-for="cell in row" 
          :key="`${cell.row}-${cell.col}`"
          :class="getCellClass(cell)"
          @click="cellClick(cell)"
          class="board-cell"
        >
          {{ getCellDisplay(cell) }}
        </div>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  name: 'GameBoard',
  props: {
    board: {
      type: Object,
      required: true
    },
    readonly: {
      type: Boolean,
      default: false
    },
    human: {
          type: Boolean,
          default: false
        },
    highlight: {
      type: Boolean,
      default: true
    }
  },
  emits: ['cell-click', 'own-cell-click'],
  methods: {
    getCellClass(cell) {
      const classes = ['cell-base']
      if (cell.status === 'HIT') {
        classes.push('hit')
      } else if (cell.status === 'MISS') {
        classes.push('miss')
      } else if (cell.status === 'EMPTY') {
        classes.push('empty')
      }
      if (cell.hasShip && !this.readonly) {
        classes.push('has-ship')
      }
      if (!this.readonly) {
        classes.push('clickable')
      }
      return classes
    },
    getCellDisplay(cell) {
      if (cell.status === 'HIT') return 'X'
      if (cell.status === 'MISS') return '*'
      if (cell.hasShip && this.readonly) return '+'
      return ''
    },
    cellClick(cell) {
      if (!this.readonly && !this.human) {
        this.$emit('cell-click', cell.row, cell.col)
      } else if(!this.readonly && this.human) {
        // Optional: Show ship details or other info when clicking on own board
        this.$emit('own-cell-click', cell.row, cell.col)
      }
    }
  }
}
</script>
<style scoped>
.game-board {
  display: flex;
  justify-content: center;
}
.grid {
  display: inline-grid;
  grid-template-columns: repeat(10, 1fr);
  gap: 2px;
  background: #333;
  padding: 10px;
  border-radius: 8px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}
.board-row {
  display: contents;
}
.board-cell {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.2em;
  font-weight: bold;
  background: #e8eef7;
  border: 1px solid #c5d3e0;
  border-radius: 4px;
}
.board-cell.cell-base {
  background: #e8eef7;
  border: 1px solid #c5d3e0;
}
.board-cell.empty {
  background: #f0f0f0;
}
.board-cell.hit {
  background: #ff6b6b;
  color: white;
}
.board-cell.miss {
  background: #4dabf7;
  color: white;
}
.board-cell.has-ship {
  background: #a8e6cf;
}
.board-cell.clickable:hover {
  background: #ffd666;
  cursor: pointer;
  transform: scale(1.1);
  transition: all 0.2s ease;
}
@media (max-width: 600px) {
  .board-cell {
    width: 30px;
    height: 30px;
    font-size: 1em;
  }
  .grid {
    gap: 1px;
    padding: 5px;
  }
}
</style>
