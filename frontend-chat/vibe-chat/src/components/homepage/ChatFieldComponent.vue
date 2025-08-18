<template>
  <div class="messages-wrapper">
    <template v-for="(msg, index) in messages" :key="index">
      <!-- Assistant plain text -->
      <div
        v-if="msg.role === 'ASSISTANT' && typeof msg.content === 'string'"
        class="message-row assistant-row"
      >
        <!--<img src="../../../public/Robot.ico" class="avatar" alt="Assistant avatar" />-->
        <div class="message-block" v-html="renderMarkdown(msg.content)"></div>
      </div>

      <!-- Assistant events summary -->
      <div
        v-else-if="msg.role === 'ASSISTANT' && msg.type === 'event_list'"
        class="message-row assistant-row"
      >
        <!--<img src="../../../public/Robot.ico" class="avatar" alt="Assistant avatar" />-->
        <div class="message-block">
          <button v-if="showEventButton" class="reopen-events-button" @click="openEventsModal">
            Evenementen bekijken
          </button>
        </div>
      </div>
      
      <!-- Route response -->
      <div v-else-if="msg.role === 'ROUTE'" class="message-row assistant-row">
        <!-- <img src="../../../public/Robot.ico" class="avatar" alt="Assistant avatar" /> -->
        <RouteComponent :start="msg.start" :end="msg.end" />
      </div>

      <!-- User messages -->
      <div v-else class="message-row user-row">
        <div class="message-block">{{ msg.content }}</div>
        <img src="../../../public/User.ico" class="avatar" alt="User avatar" />
      </div>
    </template>

    <Transition name="modal">
      <div v-if="isEventsModalOpen" class="modal-lower">
        <EventOverviewModal :isOpen="isEventsModalOpen" @close="closeEventsModal" />
      </div>
    </Transition>
  </div>
</template>

<script setup>
import { defineProps, ref, watch, nextTick } from 'vue'
import RouteComponent from './RouteComponent.vue'
import EventOverviewModal from './EventOverviewModal.vue'
import MarkdownIt from 'markdown-it'
import DOMPurify from 'dompurify'

// ——— Markdown parser + sanitizer ———
const md = new MarkdownIt()
function renderMarkdown(raw) {
  return DOMPurify.sanitize(md.render(raw))
}

const props = defineProps({
  messages: {
    type: Array,
    default: () => [],
  },
})

const emit = defineEmits(['add-reopen-message'])

const isEventsModalOpen = ref(false)
const showEventButton = ref(false)

watch(
  () => props.messages,
  (newMessages) => {
    const lastMessage = newMessages[newMessages.length - 1]
    console.log('Last message type:', lastMessage?.type)

    if (lastMessage?.role === 'ASSISTANT' && lastMessage?.type === 'event_list') {
      isEventsModalOpen.value = true
      nextTick(() => {
        scrollToBottom()
      })

      showEventButton.value = false
      setTimeout(() => {
        showEventButton.value = true
        nextTick(() => {
          scrollToBottom()
        })
      }, 3000)
    }
  },
  { deep: true },
)

function openEventsModal() {
  isEventsModalOpen.value = true
  nextTick(() => {
    scrollToBottom()

    setTimeout(() => {
      scrollToBottom()
    }, 100)
  })
}

function closeEventsModal() {
  isEventsModalOpen.value = false
  emit('add-reopen-message')
  nextTick(() => {
    scrollToBottom()
  })
}

function scrollToBottom() {
  const chatContainer = document.querySelector('.chat-container')
  if (chatContainer) {
    chatContainer.scrollTop = chatContainer.scrollHeight
    setTimeout(() => {
      chatContainer.scrollTop = chatContainer.scrollHeight
    }, 50)
  }
}
</script>


<style scoped>
.messages-wrapper {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  position: relative;
}
.message-row {
  display: flex;
  align-items: flex-start;
  gap: 0.5rem;
  margin: 0px 10px;
}
.assistant-row { justify-content: flex-start; }
.user-row { justify-content: flex-end; }
.message-block {
  background-color: #6f5e6c;
  color: #fff;
  padding: 10px;
  border-radius: 10px;
  max-width: 70%;
  word-wrap: break-word;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}
.reopen-events-button {
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
  font-size: 1rem;
  align-self: flex-start;
}
.reopen-events-button:hover { transform: translateY(-2px); }
.reopen-events-button:active { transform: translateY(0px) scale(0.98); }
.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}
.modal-lower {
  width: 100%;
  margin-top: 1rem;
  background-color: transparent;
}
.modal-enter-active, .modal-leave-active { transition: all 0.5s ease; }
.modal-enter-from, .modal-leave-to { opacity: 0; transform: translateY(50px) scale(0.95); }
.modal-enter-to, .modal-leave-from { opacity: 1; transform: translateY(0) scale(1); }
</style>
