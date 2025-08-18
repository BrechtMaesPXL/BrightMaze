import { defineStore } from 'pinia';
import { ref } from 'vue';

export const useChatStore = defineStore('chat', () => {
  const voices = ref([]);

  if (typeof window !== 'undefined' && 'speechSynthesis' in window) {
    const loadVoices = () => {
      voices.value = window.speechSynthesis.getVoices();
      console.log('TTS voices loaded:', voices.value.map(v => `${v.name} (${v.lang})`));
    };

    window.speechSynthesis.addEventListener('voiceschanged', loadVoices);
    loadVoices();
  }

  function speak(text) {
    if (typeof window === 'undefined' || !('speechSynthesis' in window)) {
      console.warn('Speech synthesis wordt niet ondersteund in deze browser');
      return;
    }

    window.speechSynthesis.cancel();

    const utterance = new SpeechSynthesisUtterance(text);

    const nlVoices = voices.value.filter(v => v.lang.startsWith('nl'));

    if (nlVoices.length > 0) {
      utterance.voice = nlVoices[1];
    } else {
      console.warn('Geen Nederlandse stem gevonden, gebruik standaard stem');
    }

    utterance.rate = 1;
    utterance.pitch = 1;
    utterance.volume = 1;

    window.speechSynthesis.speak(utterance);
  }

  return { speak, voices };
});
