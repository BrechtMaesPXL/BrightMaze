// src/plugins/vuetify.js
import { createVuetify } from 'vuetify'
import { icons } from './icons'

export default createVuetify({
  icons: {
    defaultSet: 'mdi',
    aliases: {
      ...icons
    },
    sets: {}
  }
})
