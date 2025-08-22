<template>
  <HomeButtonComponent />
  <div class="chart-container">
    <Line v-if="chartData" :data="chartData" :options="chartOptions" />
    <p v-else>Loading data...</p>
    <WildfireModal :year-details="yearDetails" @close="closeModal" />
  </div>
</template>

<script setup>
import { Line } from 'vue-chartjs'
import {
  Chart as ChartJS,
  Title,
  Tooltip,
  Legend,
  LineElement,
  CategoryScale,
  LinearScale,
  PointElement
} from 'chart.js'
import { onMounted } from 'vue'
import { useAverageFireSizeStore } from '@/stores/averageFireSizeStore'
import { storeToRefs } from 'pinia'
import HomeButtonComponent from '@/components/Menu/HomeButtonComponent.vue'
import WildfireModal from '@/components/WildfireModal.vue'

ChartJS.register(
  Title,
  Tooltip,
  Legend,
  LineElement,
  CategoryScale,
  LinearScale,
  PointElement
)

const averageFireSizeStore = useAverageFireSizeStore()
const { chartData, yearDetails } = storeToRefs(averageFireSizeStore)

onMounted(() => {
  averageFireSizeStore.fetchAverageFireSize()
})

const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      display: true
    },
    title: {
      display: true,
      text: 'Average Fire Size Per Year'
    }
  },
  scales: {
    y: {
      title: {
        display: true,
        text: 'Fire Size (acres)'
      }
    },
    x: {
      title: {
        display: true,
        text: 'Year'
      }
    }
  },
  onClick: (event, elements) => {
    if (elements.length > 0) {
      const index = elements[0].index
      const year = chartData.value.labels[index]
      averageFireSizeStore.fetchYearDetails(year)
    }
  }
}

const closeModal = () => {
  averageFireSizeStore.yearDetails = null
}
</script>

<style scoped>
.chart-container {
  position: relative;
  height: 900px;
  width: 100%;
}
</style>
