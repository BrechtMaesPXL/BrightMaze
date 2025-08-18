import { defineStore } from 'pinia';
import { ref } from 'vue';
import axios from 'axios';

export const useRouteSettingsStore = defineStore('routeSettings', () => {
  const isRouteEnabled = ref(false);

  async function fetchRouteSettings() {
    try {
      const res = await axios.get(`${import.meta.env.VITE_API_BASE_URL}admin/api/settings/route-settings`);
      console.log('Route settings fetch response:', res.data);

      if (typeof res.data === 'object' && 'enabled' in res.data) {
        isRouteEnabled.value = res.data.enabled;
      } else {
        isRouteEnabled.value = res.data;
      }

      console.log('isRouteEnabled after assignment:', isRouteEnabled.value);
    } catch (error) {
      console.error('Fout bij ophalen route settings:', error);
    }
  }

  return { isRouteEnabled, fetchRouteSettings };
});
