// src/stores/locationsStore.js
import { defineStore } from 'pinia'

export const useLocationsStore = defineStore('locations', {
  state: () => ({
    locations: [
      { label: 'corda 1', value: 'corda 1' },
      { label: 'corda 2', value: 'corda 2' },
      { label: 'corda 3', value: 'corda 3' },
      { label: 'corda 4', value: 'corda 4' },
      { label: 'corda 5', value: 'corda 5' },
      { label: 'corda 6', value: 'corda 6' },
      { label: 'corda 7', value: 'corda 7' },
      { label: 'corda 8', value: 'corda 8' },
      { label: 'corda arena', value: 'corda arena' },
      { label: 'corda bar', value: 'corda bar' },
      { label: 'cordaat', value: 'cordaat' }
    ]
  })
})
