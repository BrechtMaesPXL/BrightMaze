import { ref } from 'vue'
import mapboxgl from 'mapbox-gl'
import 'mapbox-gl/dist/mapbox-gl.css'

export function useMapboxPointer(mapboxToken) {
  const map = ref(null)
  const marker = ref(null)

  function initMap(container, options) {
    mapboxgl.accessToken = mapboxToken
    map.value = new mapboxgl.Map({
      container,
      style: 'mapbox://styles/mapbox/dark-v10',
      ...options
    })
    return map.value
  }

  function setPointer(lat, lng) {
    if (!map.value) return
    if (marker.value) marker.value.remove()
    marker.value = new mapboxgl.Marker({ color: '#ff9800' })
      .setLngLat([lng, lat])
      .addTo(map.value)
  }

  function clearPointer() {
    if (marker.value) marker.value.remove()
    marker.value = null
  }

  return { map, marker, initMap, setPointer, clearPointer }
}
