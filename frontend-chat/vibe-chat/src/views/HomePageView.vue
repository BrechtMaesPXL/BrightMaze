<template>
  <div class="page-container">
      <div class="avatar-container" :class="{ 'chat-started': session_id !== null }">
        <Avatar ref="avatar" />
      </div>

      <div class="logo-container" :class="{ 'chat-started': session_id !== null }">
        <LogoComponent :currentBuilding="currentBuilding" />
      </div>

    <!-- New Chat button -->
    <div>
      <v-btn class="new-chat-btn" color="blue" @click="createNewSession" elevation="0">
        <v-icon start size="20" color="White"> mdi-square-edit-outline </v-icon>
      </v-btn>
    </div>

    <div class="chat-container" v-show="session_id !== null" ref="chatContainer">
      <ChatFieldComponent :messages="historyItems || []" />
    </div>

    <div class="input-field" :class="{ 'input-bottom': session_id !== null }">
      <InputFieldComponent @send-message="handleInput" :disabled="waitingForResponse" />
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted } from 'vue'
import LogoComponent from '../components/homepage/LogoComponent.vue'
import ChatFieldComponent from '@/components/homepage/ChatFieldComponent.vue'
import InputFieldComponent from '../components/homepage/InputFieldComponent.vue'
import { fetchData } from '@/assets/Utils/FetchHandeling.js'
import { CHATBOT_API_URL, CREATE_SESSION_URL } from '@/config/api'
import { LOCATION_API_URL } from '@/config/api'
import { useChatStore } from '@/stores/chatStore';
import Avatar from '../components/homepage/avatarComponent.vue'
import { watchEffect } from 'vue';
import { SETTINGS_URL } from '@/config/api';


const submittedMessage = ref('')
const data = ref(null)
const session_id = ref(null)
const chat_url = CHATBOT_API_URL
const session_url = CREATE_SESSION_URL
const historyItems = ref([])
const waitingForResponse = ref(false)
const chatContainer = ref(null)
const currentBuilding = ref(null)
const avatar = ref(null);

async function fetchCurrentLocation() {
  try {
    const response = await fetchData(LOCATION_API_URL, {
      method: 'GET',
      headers: { Accept: 'application/json' },
    })
    currentBuilding.value = response || null
  } catch {
    currentBuilding.value = null
  }
}

onMounted(() => {
  fetchCurrentLocation();

  watchEffect((onCleanup) => {
    if (avatar.value) {
      console.log('Avatar is ready');
      
      // Start de greeting na 2 seconden
      const greetingTimer = setTimeout(() => {
        avatar.value.greeting();
      }, 2000); // 2000ms = 2 seconden

      // Cleanup bij unmounten
      onCleanup(() => {
        clearTimeout(greetingTimer);
      });
    }
  });
});


function scrollToBottom() {
  nextTick(() => {
    if (chatContainer.value) {
      chatContainer.value.scrollTop = chatContainer.value.scrollHeight
    }
  })
}

async function handleInput(message) {
  if (waitingForResponse.value) return

  submittedMessage.value = message
  waitingForResponse.value = true
  historyItems.value.push({ role: 'USER', content: message })
  await nextTick()
  scrollToBottom()
  await fetchCurrentLocation()

  const options = {
    method: 'POST',
    headers: {
      Accept: 'application/json',
      'Content-Type': 'application/json',
    },
  }

  try {
    if (!session_id.value) {
      session_id.value = await fetchData(session_url, options)
    }

    options.body = JSON.stringify({
      id: session_id.value,
      message,
      history: historyItems.value,
    })

    data.value = await fetchData(chat_url, options)

    let fullResponse
    let type = null
    let start = null
    let end = null
    let botReply = null
    let events = null
    let lastMessage = data.value?.history?.historyItems[data.value.history.historyItems.length - 1]

    try {
      if (typeof lastMessage?.content === 'string') {
        const parsed = JSON.parse(lastMessage.content)
        if (parsed && typeof parsed === 'object') {
          lastMessage.content = parsed // Overwrite the string with the parsed object
        }
      }
    } catch {
      console.error('Invalid JSON in lastMessage.content', lastMessage.content)
    }

    type = lastMessage?.type || null
    botReply = lastMessage?.content?.response || null

    fullResponse = botReply
    fullResponse = fullResponse.replace(/\\n/g, '\n') // Replace \n with actual newline

    if (type === 'route') {
      start = lastMessage?.content?.start || null
      end = lastMessage?.content?.end || null
    } else if (type === 'events_time_specific') {
      events = lastMessage?.content.events
      if (events) {
        try {
          const eventsArray = JSON.parse(events.startsWith('[') ? events : `[${events}]`)
          let formattedEvents = '\n\n'

          eventsArray.forEach((event) => {
            const startTime = new Date(event.startDate).toLocaleTimeString('nl-NL', {
              hour: '2-digit',
              minute: '2-digit',
            })
            const endTime = new Date(event.endDate).toLocaleTimeString('nl-NL', {
              hour: '2-digit',
              minute: '2-digit',
            })

            // Format dates
            const startDate = new Date(event.startDate).toLocaleDateString('nl-NL', {
              day: '2-digit',
              month: '2-digit',
              year: 'numeric',
            })
            const endDate = new Date(event.endDate).toLocaleDateString('nl-NL', {
              day: '2-digit',
              month: '2-digit',
              year: 'numeric',
            })

            // Compare dates and format accordingly
            const dateStr =
              startDate === endDate
                ? `**Date**: ${startDate}  \n`
                : `**Date**: ${startDate} - ${endDate}  \n`

            formattedEvents += `### ${event.eventName}\n\n`
            formattedEvents += `**Location**: ${event.location}  \n`
            formattedEvents += `**Time**: ${startTime} - ${endTime}  \n`
            formattedEvents += dateStr
            formattedEvents += `${event.eventDescription}\n\n`
            formattedEvents += `---\n\n`
          })

          fullResponse += formattedEvents
        } catch (error) {
          console.error('Error parsing events:', error, '\nEvents string:', events)
          fullResponse = "Sorry, I couldn't process the events information."
        }
      }
    }

    const thinkingMessage = ref({ role: 'ASSISTANT', content: 'Aan het nadenken' })
    historyItems.value.push(thinkingMessage.value)
    await nextTick()
    scrollToBottom()

    let thinkingDotsCount = 0
    const thinkingInterval = setInterval(() => {
      thinkingMessage.value.content = 'Aan het nadenken' + '.'.repeat((thinkingDotsCount % 3) + 1)
      thinkingDotsCount++
      scrollToBottom()
    }, 500)

    await new Promise((resolve) => setTimeout(resolve, 1000 + Math.random() * 1000))

    const thinkingIndex = historyItems.value.indexOf(thinkingMessage.value)
    if (thinkingIndex !== -1) {
      historyItems.value.splice(thinkingIndex, 1)
    }
    clearInterval(thinkingInterval)

    if (fullResponse) {
      const responseMessage = ref({ role: 'ASSISTANT', content: '', type: type })
      historyItems.value.push(responseMessage.value)
      await nextTick()
      scrollToBottom()
      await typeWriterEffect(responseMessage.value, fullResponse)
    }

    if (type === 'route') {
      if (start && end && start.toLowerCase() === end.toLowerCase()) {
        const sameLocationMessage = ref({ role: 'ASSISTANT', content: '', type: type })
        historyItems.value.push(sameLocationMessage.value)
        await nextTick()
        scrollToBottom()
        await typeWriterEffect(sameLocationMessage.value, `Je bevindt je al bij ${end}.`)
      } else {
        historyItems.value.push({ role: 'ROUTE', start, end, type })
        await nextTick()
        scrollToBottom()
      }
    }

    if (type === 'event_list') {
      historyItems.value.push({ role: 'ASSISTANT', type: type })
      await nextTick()
      scrollToBottom()
    }
  } catch {
    const errorMessage = ref({ role: 'ASSISTANT', content: '' })
    historyItems.value.push(errorMessage.value)
    await nextTick()
    scrollToBottom()
    await typeWriterEffect(
      errorMessage.value,
      'Er is een fout opgetreden. Probeer het later opnieuw.',
    )
  } finally {
    waitingForResponse.value = false
    scrollToBottom()
  }
}

async function typeWriterEffect(messageObject, fullText, speed = 20) {
  const chatStore = useChatStore();
  messageObject.content = "";

  let allowVoice = false;
  try {
    const response = await fetch(`${SETTINGS_URL}/voice-settings`);
    allowVoice = await response.json(); // verwacht true of false
  } catch (error) {
    console.warn("Kon instellingen niet ophalen, voice wordt overgeslagen:", error);
  }

  await Promise.all([
    new Promise((resolve) => {
      let i = 0;
      function addLetter() {
        if (i < fullText.length) {
          messageObject.content += fullText[i];
          i++;
          if (i % 5 === 0) nextTick(); // eventueel met await, zie vorige opmerking
          setTimeout(addLetter, speed);
        } else {
          resolve();
        }
      }
      addLetter();
    }),
    allowVoice ? avatar.value.speak(fullText) : Promise.resolve()
  ]);
}

function createNewSession() {
  session_id.value = null
  data.value = null
  historyItems.value = []
  avatar.value.greeting();
}
</script>

<style scoped>
.page-container {
  max-width: 2560px;
  max-height: 1440px;
  width: 100%;
  min-height: 100vh;
  overflow: hidden;
}


.top-section {
  display: flex;
  flex-direction: row;
  align-items: center;
  position: absolute;
  top: 20%;
  left: 50%;
  transform: translate(-55%, -50%);
  transition:
    top 0.5s ease,
    left 0.5s ease,
    transform 0.5s ease;
}
.logo-container {
  position: absolute;
  top: 20%;
  left: 45%;
  transform: translate(-50%, -50%);
  transition: all 0.5s ease;
}

.logo-container.chat-started {
  top: 10%; /* Afstand vanaf de bovenkant */
  left: 50%;
}

.avatar-container {
  position: absolute;
  top: 20%;
  left: 65%;
  transform: translate(-50%, -50%);
  width: 420px;
  height: 320px;
  transition: all 0.5s ease;
  z-index: 2;
}
.avatar-container > * {
  width: 100%;
  height: 100%;
}
.avatar-container.chat-started {
  width: 420px !important; 
  height: 300px !important;
  top: 120px;
  left: 20px;
  transform: none;
}

.avatar-container.chat-started :deep(*) {
  width: 100% !important;
  height: 100% !important;
}



.avatar-container > * {
  width: 100%;
  height: 100%;
}

.vibe-text {
  font-size: 4em;
  font-family: 'Sigmar', sans-serif;
  font-weight: bold;
  color: white;
  padding-bottom: 50px;
}

.new-session-container {
  position: absolute;
  top: 3.5rem;
  right: 2rem;
}
.new-chat-btn {
  position: absolute;
  top: 3.5rem;
  right: 2rem;
  border-radius: 50%;
  width: 60px;
  height: 60px;
  min-width: 48px !important;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0;
}

.new-chat-btn:hover {
  transform: scale(1.1);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
}

.new-chat-btn .v-icon {
  font-size: 40px !important;
  margin: 0;
}

.new-chat-enter-active,
.new-chat-leave-active {
  transition: all 0.5s ease;
}

.new-chat-enter-from,
.new-chat-leave-to {
  opacity: 0;
  transform: scale(0.8) translateY(-20px);
}

.new-chat-enter-to,
.new-chat-leave-from {
  opacity: 1;
  transform: scale(1) translateY(0);
}

.button-container {
  background: linear-gradient(180deg, #c76cbd, #a84393);
  border: none;
  outline: none;
  color: #fff;
  padding: 0.75rem 1.5rem;
  border-radius: 8px;
  cursor: pointer;
  transition:
    background 0.3s ease,
    transform 0.3s ease;
}

.button-container:hover {
  transform: translateY(-2px);
}

.button-container:active {
  transform: translateY(0) scale(0.98);
}

.chat-container {
  position: absolute;
  top: 12rem;
  left: 55%;
  transform: translateX(-50%);
  width: 70%;
  bottom: 6rem;
  overflow-y: auto;
}

.input-field {
  position: fixed;
  bottom: 50%;
  left: 50%;
  transform: translateX(-50%) translateY(-50%);
  width: 90%;
  max-width: 600px;
  padding: 5px;
  transition: all 0.5s ease;
}

.input-field.input-bottom {
  bottom: 20px;
  transform: translateX(-50%);
  opacity: 1;
}

@media (max-width: 768px) {
  .top-section {
    flex-direction: column;
    transform: translate(-50%, -50%);
    padding: 10px;
  }

  .vibe-text {
    font-size: 2.5em;
    margin: 5px 0;
  }

  .input-field {
    width: 80%;
  }
}
</style>
