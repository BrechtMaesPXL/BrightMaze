// stores/firesStore.js
import { reactive } from 'vue';

export const useHomeStore = reactive({
  cardsMap: [
    {
      route: '/map',
      img: new URL('../assets/CardFotos/Schermafbeelding 2025-04-01 012734.png', import.meta.url).href,
      title: 'USA Wildfire Map',
      description: 'Real-time data visualization of wildfires across the USA',
    },
    {
      route: '/map/scatterplot',
      img: new URL('../assets/CardFotos/Schermafbeelding 2025-04-01 224637.png', import.meta.url).href,
      title: 'Scatterplot Analysis Map',
      description: 'Visualize wildfire density with scatterplots',
    },
    {
      route: '/map/predictions/weather',
      img: new URL('../assets/CardFotos/weather-prediction.png', import.meta.url).href,
      title: 'Weather Prediction',
      description: 'Predict temperature, wind, and precipitation for any location',
    },
    {
      route: '/map/predictions/fire-cause',
      img: new URL('../assets/CardFotos/fire_cause.jpg', import.meta.url).href,
      title: 'Fire Causes',
      description: 'Prediction of fire causes',
    },
    {
      route: '/map/prediction',
      img: new URL('../assets/CardFotos/Geografische.png', import.meta.url).href,
      title: 'Geographical Distribution of Fires in California',
      description: '*** WORK IN PROGRESS ***',
    },
  ],

  cardsData: [
    {
      route: '/map/FireSizePrediction',
      img: new URL('../assets/CardFotos/Schermafbeelding 2025-05-24 230811.png ', import.meta.url).href,
      title: 'How big will the next fire be? ',
      description: 'Try to predict the size of the next fire',
    },
    {
      route: '/safety',
      img: new URL('../assets/CardFotos/safety.jpg', import.meta.url).href,
      title: 'Top 5 Causes per state',
      description: 'Also find here your safety tips to prevent wildfires',
    },
    {
      route: '/charts',
      img: new URL('../assets/CardFotos/perJaar.png', import.meta.url).href,
      title: 'Average Fire Size per Year',
      description: 'Wildfire size trends over years',
    },
    {
      route: '/fire-size',
      img: new URL('../assets/CardFotos/schade.png', import.meta.url).href,
      title: 'Top 10 Wildfires with the Most Damage',
      description: 'Overview of the top 10 wildfires with the most damage.',
    },
    {
      route: '/quiz',
      img: new URL('../assets/CardFotos/quiz.png', import.meta.url).href,
      title: 'Test your knowledge',
      description: "Let Fireman Sam test your wildfire knowledge!"
    },
  ]
});
