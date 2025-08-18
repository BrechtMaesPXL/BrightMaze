<template>
  <div class="input-group">
    <!-- Speech-to-text microphone button -->
    <button
      class="btn mic-button"
      @click="toggleListening"
      :disabled="disabled"
      :class="{ listening: isListening }"
      title="mic button"
    >
      <span class="material-icons">
        {{ isListening ? 'mic_off' : 'mic' }}
      </span>
    </button>

    <input
      type="text"
      class="form-control rounded-pill"
      placeholder="Stel een vraag..."
      v-model="localMessage"
      @keyup.enter="handleSend"
    />

    <button
      class="btn send-button"
      type="button"
      @click="handleSend"
      :disabled="disabled || localMessage.trim() === ''"
    >
      <span class="material-icons send-icon">arrow_upward</span>
    </button>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';

// Emits
const emit = defineEmits(['send-message']);

// Props
const props = defineProps({
  disabled: {
    type: Boolean,
    default: false,
  },
});

const localMessage = ref('');
const isListening = ref(false);
let recognition;

onMounted(() => {
  if (typeof window !== 'undefined' && ('SpeechRecognition' in window || 'webkitSpeechRecognition' in window)) {
    const SpeechRec = window.SpeechRecognition || window.webkitSpeechRecognition;
    recognition = new SpeechRec();
    recognition.lang = 'nl-NL';
    recognition.interimResults = true;
    recognition.maxAlternatives = 1;

    recognition.addEventListener('speechstart', () => {
      isListening.value = true;
    });
    recognition.addEventListener('speechend', () => {
      recognition.stop();
    });
    recognition.addEventListener('result', (event) => {
      const lastResult = event.results[event.results.length - 1][0];
      localMessage.value = lastResult.transcript;
    });

    recognition.addEventListener('end', () => {
      isListening.value = false;
    });

    recognition.addEventListener('error', (e) => {
      console.error('Speech recognition error', e);
      isListening.value = false;
    });
  }
});

onUnmounted(() => {
  if (recognition && isListening.value) {
    recognition.stop();
  }
});

function toggleListening() {
  if (!recognition) {
    console.warn('Speech recognition niet ondersteund');
    return;
  }
  if (isListening.value) {
    recognition.stop();
  } else {
    localMessage.value = '';
    recognition.start();
  }
}

function handleSend(event) {
  if (event && event.preventDefault) event.preventDefault();
  if (props.disabled || localMessage.value.trim() === '') return;

  emit('send-message', localMessage.value);
  localMessage.value = '';

  if (recognition && isListening.value) {
    recognition.stop();
  }
}
</script>

<style scoped>
.input-group {
  display: flex;
  align-items: center;
  border: 1px solid #ced4da;
  border-radius: 30px;
  padding: 0.25rem;
  background-color: #fff;
  max-width: 700px;
  min-width: 400px;
  margin: 0 auto;
}

.form-control {
  border: none;
  padding: 0.75rem 1rem;
  box-shadow: none !important;
  background: transparent;
  flex-grow: 1;
  font-size: 1rem;
}

.form-control:focus {
  outline: none;
  border: none;
  box-shadow: none;
}

.send-button {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 40px;
  height: 40px;
  border-radius: 50% !important;
  background: linear-gradient(180deg, #7647A2, #DA498C);
  color: white;
  border: none;
  cursor: pointer;
  padding: 0;
  margin-left: 0.5rem;
  transition: transform 0.2s ease, box-shadow 0.2s ease, background 0.3s ease;
}

.send-button:hover {
  transform: scale(1.1) translateY(-2px);
  box-shadow: 0 5px 12px rgba(0, 0, 0, 0.2);
  background: linear-gradient(180deg, #693d91, #c23f7a);
}

.send-button:disabled {
  background: #ccc;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.send-icon {
  font-size: 1.5em;
  transition: transform 0.2s ease;
}

.send-button:hover .send-icon {
  transform: translateY(-1px);
}

/* Microphone button styling */
.mic-button {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 40px;
  height: 40px;
  margin-right: 0.5rem;
  border-radius: 50% !important;
  background: #f0f0f0;
  color: #555;
  border: none;
  cursor: pointer;
  transition: background 0.2s ease, transform 0.2s ease;
}

/* pulse animation when listening */
@keyframes pulse {
  0% { box-shadow: 0 0 0 0 rgba(255,75,92, 0.7); }
  70% { box-shadow: 0 0 0 10px rgba(255,75,92, 0); }
  100% { box-shadow: 0 0 0 0 rgba(255,75,92, 0); }
}

.mic-button.listening {
  background: #ff4b5c;
  color: #fff;
  animation: pulse 1.5s infinite;
}

.mic-button:hover {
  transform: scale(1.1) translateY(-2px);
  background: #e0e0e0;
}

.mic-button:disabled {
  background: #eee;
  cursor: not-allowed;
  transform: none;
}

@media (max-width: 768px) {
  .input-group {
    padding: 0.15rem;
  }

  .form-control {
    font-size: 0.9rem;
    padding: 0.6rem;
  }

  .send-button,
  .mic-button {
    width: 35px;
    height: 35px;
  }

  .send-icon,
  .mic-button .material-icons {
    font-size: 1.2em;
  }
}

</style>
