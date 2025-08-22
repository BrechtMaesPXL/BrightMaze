<template>
  <div class="bar-chart-wrapper">
    <Bar v-if="chartData" :data="chartData" :options="chartOptions" />
    <p v-else>Loading chart data...</p>
    <WildfireModal :wildfire="selectedWildfire" @close="closeModal" />
  </div>
</template>

<script setup>
import { computed, ref, onMounted, watch } from 'vue'
import { Bar } from 'vue-chartjs'
import {
  Chart as ChartJS,
  Title,
  Tooltip,
  Legend,
  BarElement,
  CategoryScale,
  LinearScale,
} from 'chart.js'
import { useBiggestFireSizeStore } from '@/stores/biggestFireSizeStore'
import WildfireModal from '@/components/WildfireModal.vue'

ChartJS.register(Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale)

const fireStore = useBiggestFireSizeStore()
const selectedWildfire = ref(null)

onMounted(async () => {
  console.log('[MOUNTED] Component mounted')
  await fireStore.fetchWildfireData()
})

const wildfireData = computed(() => fireStore.wildfireData)

watch(wildfireData, (val) => {
  console.log('[WATCH wildfireData]', val)
})

const chartData = computed(() => {
  console.log('[CHART DATA] Computing chart data...')

  if (!wildfireData.value || wildfireData.value.length === 0) {
    console.warn('[CHART DATA] No wildfire data available')
    return null
  }

  return {
    labels: wildfireData.value.map(item => item.FIRE_NAME || 'Unknown'),
    datasets: [
      {
        label: 'Fire size (acres)',
        data: wildfireData.value.map(item => item.FIRE_SIZE || 0),
        backgroundColor: 'rgba(255, 165, 0, 0.6)',
        borderColor: 'rgba(255, 165, 0, 1)',
        borderWidth: 1,
      },
    ],
  }
})

const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      display: true,
    },
    title: {
      display: true,
    },
  },
  scales: {
    y: {
      beginAtZero: true,
    },
  },
  onClick: (event, elements) => {
    if (elements.length > 0) {
      const index = elements[0].index
      selectedWildfire.value = wildfireData.value[index]
      console.log('[BAR CLICKED]', selectedWildfire.value)
    }
  },
}

const closeModal = () => {
  selectedWildfire.value = null
}
</script>

<style scoped>
.bar-chart-wrapper {
  height: 500px;
  padding: 20px;
}
</style>
