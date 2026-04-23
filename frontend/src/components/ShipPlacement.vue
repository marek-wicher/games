<template>
  <div class="ship-placement">
    <h3>Place Your Ships</h3>
    <form @submit.prevent="submitPlacement">
      <div class="form-group">
        <label for="shipName">Ship:</label>
        <select v-model="form.shipName" id="shipName" required>
          <option value="">Select ship...</option>
          <option value="Carrier">Carrier (5)</option>
          <option value="Battleship">Battleship (4)</option>
          <option value="Cruiser">Cruiser (3)</option>
          <option value="Submarine">Submarine (3)</option>
          <option value="Destroyer">Destroyer (2)</option>
        </select>
      </div>
      <div class="form-group">
        <label for="row">Row (0-9):</label>
        <input v-model.number="form.row" type="number" id="row" min="0" max="9" required />
      </div>
      <div class="form-group">
        <label for="col">Column (0-9):</label>
        <input v-model.number="form.col" type="number" id="col" min="0" max="9" required />
      </div>
      <div class="form-group">
        <label class="checkbox-label">
          <input v-model="form.horizontal" type="checkbox" />
          Horizontal
        </label>
      </div>
      <button type="submit" class="btn btn-primary btn-block">Place Ship</button>
    </form>
  </div>
</template>
<script>
export default {
  name: 'ShipPlacement',
  data() {
    return {
      form: {
        shipName: '',
        row: 0,
        col: 0,
        horizontal: true
      }
    }
  },
  methods: {
    submitPlacement() {
      this.$emit('place-ship', { ...this.form })
      this.form = {
        shipName: '',
        row: 0,
        col: 0,
        horizontal: true
      }
    }
  }
}
</script>
<style scoped>
.ship-placement {
  padding: 20px;
  background: #f5f5f5;
  border-radius: 8px;
}
.ship-placement h3 {
  margin-top: 0;
  color: #667eea;
}
.form-group {
  margin-bottom: 15px;
  display: flex;
  flex-direction: column;
}
.form-group label {
  font-weight: bold;
  margin-bottom: 5px;
  color: #333;
}
.form-group input,
.form-group select {
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 5px;
  font-size: 1em;
  font-family: inherit;
}
.form-group input:focus,
.form-group select:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 5px rgba(102, 126, 234, 0.3);
}
.checkbox-label {
  display: flex;
  align-items: center;
  font-weight: normal;
  cursor: pointer;
}
.checkbox-label input {
  margin-right: 8px;
  cursor: pointer;
  width: auto;
}
.btn-block {
  width: 100%;
  margin-top: 10px;
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
</style>
