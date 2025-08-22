<template>
  <div class="chart-container">
    <h1 class="text-center">Top 5 Causes of Forest Fires in {{ currentState }}</h1>
    <canvas ref="chartRef"></canvas>
    <div class="legend-row">
      <div
        v-for="(label, i) in safeStore.FireCausesString"
        :key="label"
        class="legend-item"
      >
        <span
          class="color-box"
          :style="{ backgroundColor: ['#ff9999', '#66b3ff', '#99ff99', '#ffcc99', '#c2c2f0'][i] }"
        ></span>
        {{ label }}
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from "vue";
import { Chart, registerables } from "chart.js";
import { userSafeStore } from "@/stores/safe.js";
import { US_STATES } from "@/constants/states.js";

Chart.register(...registerables);

const safeStore = userSafeStore();
const chartRef = ref(null);
let chartInstance = null;

const getFullName = (abbr) => US_STATES[abbr]?.name || abbr;
const currentState = ref(getFullName(safeStore.CurrentState));

const updatePie = () => {
  if (!chartRef.value) return;
  const ctx = chartRef.value.getContext("2d");

  if (chartInstance) {
    chartInstance.destroy();
  }

  const total = safeStore.FireCausesInt.reduce((acc, val) => acc + val, 0);
  const percentages = safeStore.FireCausesInt.map((val) =>
    ((val / total) * 100).toFixed(1)
  );

  chartInstance = new Chart(ctx, {
    type: "pie",
    data: {
      labels: safeStore.FireCausesString.map((label, i) => `${label} (${percentages[i]}%)`),
      datasets: [
        {
          data: safeStore.FireCausesInt,
          backgroundColor: ["#ff9999", "#66b3ff", "#99ff99", "#ffcc99", "#c2c2f0"],
        },
      ],
    },
    options: {
      responsive: true,
      plugins: {
        legend: {
          display: false,
        },
      },
    },
  });
};

onMounted(async () => {
  await safeStore.getFireCauses();
  currentState.value = getFullName(safeStore.CurrentState);
  updatePie();
});

watch(
  () => safeStore.CurrentState,
  async (newState) => {
    currentState.value = getFullName(newState);
    await safeStore.getFireCauses();
    updatePie();
  }
);
</script>

<style scoped>
.chart-container {
  text-align: center;
}

canvas {
  max-width: 450px;
  width: 100%;
  margin: auto;
  display: block;
}

.legend-row {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 16px;
  margin-top: 16px;
}

.legend-item {
  display: flex;
  align-items: center;
  font-size: 14px;
  color: #2c3e50;
}

.color-box {
  width: 16px;
  height: 16px;
  display: inline-block;
  margin-right: 8px;
  border-radius: 4px;
  border: 1px solid #ccc;
}
</style>
