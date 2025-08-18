<template>
  <v-form ref="form">
    <v-card class="mx-auto" max-width="600">
      <v-card-title class="bg-primary text-white">
        <h2>{{ formTitle }}</h2>
      </v-card-title>

      <v-card-text>
        <!-- Evenement Naam -->
        <v-text-field
          v-model="event.eventName"
          label="Evenement Naam*"
          :rules="[v => !!v || 'Naam is verplicht']"
          :error-messages="errors.eventName"
          required
          outlined
          class="mb-4"
        ></v-text-field>

        <!-- Locatie -->
        <v-select
          v-model="event.location"
          :items="locations"
          label="Locatie*"
          item-title="label"
          item-value="value"
          :rules="[v => !!v || 'Locatie is verplicht']"
          :error-messages="errors.location"
          required
          outlined
          class="mb-4"
          :menu-props="{ maxHeight: '400' }"
        />

        <!-- Beschrijving -->
        <v-textarea
          v-model="event.eventDescription"
          label="Beschrijving"
          outlined
          rows="3"
          class="mb-4"
        ></v-textarea>

        <!-- Start Datum en Tijd -->
        <div class="mb-4" style="display: flex; gap: 16px;">
          <div style="flex: 1">
            <label for="start-date">Startdatum*</label>
            <v-text-field
              id="start-date"
              type="date"
              v-model="startDate"
              :min="minDate"
              :error-messages="errors.startDate"
              required
              outlined
              dense
              @change="validateStartDate"
            ></v-text-field>
          </div>
          <div style="flex: 1">
            <label for="start-time">Starttijd*</label>
            <v-text-field
              id="start-time"
              type="time"
              v-model="startTime"
              :min="startDate === minDate ? currentTime : null"
              :error-messages="errors.startTime"
              required
              outlined
              dense
              @change="validateStartTime"
            ></v-text-field>
          </div>
        </div>

        <!-- Eind Datum en Tijd -->
        <div class="mb-4" style="display: flex; gap: 16px;">
          <div style="flex: 1">
            <label for="end-date">Einddatum*</label>
            <v-text-field
              id="end-date"
              type="date"
              v-model="endDate"
              :min="startDate || minDate"
              :error-messages="errors.endDate"
              required
              outlined
              dense
              @change="validateEndDate"
            ></v-text-field>
          </div>
          <div style="flex: 1">
            <label for="end-time">Eindtijd*</label>
            <v-text-field
              id="end-time"
              type="time"
              v-model="endTime"
              :min="startDate === endDate ? startTime : null"
              :error-messages="errors.endTime"
              required
              outlined
              dense
              @change="validateEndTime"
            ></v-text-field>
          </div>
        </div>

        <!-- File input -->
        <div class="mb-4">
          <template v-if="imagePreview && isEditing">
            <v-img
              :src="imagePreview"
              max-height="200"
              contain
              class="mb-2"
            ></v-img>
            <v-btn
              color="error"
              small
              class="deletebutton"
              @click="removeCurrentImage"
            >
              Huidige afbeelding verwijderen
            </v-btn>
          </template>

          <v-file-input
            v-if="!imagePreview || !isEditing"
            v-model="event.file"
            label="Bestand toevoegen*"
            accept=".png,.jpg,.jpeg"
            outlined
            show-size
            :error-messages="errors.file"
            @change="validateFile"
          ></v-file-input>
        </div>

        <!-- Actieknoppen -->
        <v-card-actions class="justify-end">
          <v-btn
            @click="submitForm"
            color="primary"
            :disabled="!isFormValid"
          >
            Opslaan
          </v-btn>
          <v-btn
            color="error"
            @click="resetForm"
          >
            Wissen
          </v-btn>
        </v-card-actions>
      </v-card-text>
    </v-card>
  </v-form>
</template>

<script>
import { useEventStore } from '@/stores/eventStore';
import { useLocationsStore } from '@/stores/locationsStore';
import { storeToRefs } from 'pinia';

export default {
  setup() {
    const eventStore = useEventStore();
    const locationsStore = useLocationsStore();
    const { selectedEvent } = storeToRefs(eventStore);
    const { locations } = locationsStore;
    return { selectedEvent, locations };
  },
  data() {
    return {
      event: {
        eventName: '',
        location: '',
        eventDescription: '',
        file: null,
      },
      startDate: null,
      startTime: null,
      endDate: null,
      endTime: null,
      imagePreview: null,

      errors: {
        eventName: '',
        location: '',
        startDate: '',
        startTime: '',
        endDate: '',
        endTime: '',
        file: '',
      },
      isEditing: false,
      currentEventId: null,
    };
  },
  computed: {
    formTitle() {
      return this.isEditing ? 'Evenement bewerken' : 'Evenement toevoegen';
    },
    minDate() {
      const today = new Date();
      const yyyy = today.getFullYear();
      const mm = (today.getMonth() + 1).toString().padStart(2, '0');
      const dd = today.getDate().toString().padStart(2, '0');
      return `${yyyy}-${mm}-${dd}`;
    },
    currentTime() {
      const now = new Date();
      const hours = now.getHours().toString().padStart(2, '0');
      const minutes = now.getMinutes().toString().padStart(2, '0');
      return `${hours}:${minutes}`;
    },
    isFormValid() {
      const baseFieldsValid = (
        this.event.eventName &&
        this.event.location &&
        this.startDate &&
        this.startTime &&
        this.endDate &&
        this.endTime &&
        !Object.values(this.errors).some(error => error !== '')
      );

      if (!this.isEditing) {
        return baseFieldsValid && !!this.event.file;
      }

      return baseFieldsValid && (this.imagePreview || this.event.file);
    },
  },
  watch: {
    selectedEvent: {
      handler(newEvent) {
        if (newEvent) {
          this.loadEventData(newEvent);
        } else {
          this.resetForm();
        }
      },
      immediate: true,
    },
  },
  methods: {
    loadEventData(event) {
      this.isEditing = true;
      this.currentEventId = event.id;
      this.imagePreview = event.base64Image
        ? `data:image/jpeg;base64,${event.base64Image}`
        : null;
      const [startDateStr, startTimeStr] = event.startDate.split('T');
      const [endDateStr, endTimeStr] = event.endDate.split('T');
      const [startHour, startMinute] = startTimeStr.split(':');
      const [endHour, endMinute] = endTimeStr.split(':');

      this.event = {
        eventName: event.eventName,
        location: event.location,
        eventDescription: event.eventDescription,
        file: event.file || null,
      };
      this.startDate = startDateStr;
      this.startTime = `${startHour}:${startMinute}`;
      this.endDate = endDateStr;
      this.endTime = `${endHour}:${endMinute}`;
    },
    removeCurrentImage() {
      this.imagePreview = null;
      this.event.file = null;
      this.errors.file = '';
      if (this.isEditing) {
        this.selectedEvent.base64Image = null;
      }
    },
    validateFile() {
      if (!this.event.file && this.imagePreview && this.isEditing) {
        this.errors.file = '';
        return true;
      }

      if (!this.event.file) {
        this.errors.file = 'Bestand is verplicht';
        return false;
      }

      const allowedTypes = ['image/jpeg', 'image/png'];
      const fileExtension = this.event.file.name.split('.').pop().toLowerCase();
      const isTypeValid =
        allowedTypes.includes(this.event.file.type) ||
        ['jpg', 'jpeg', 'png'].includes(fileExtension);

      if (!isTypeValid) {
        this.errors.file = 'Alleen JPG, PNG of JPEG bestanden zijn toegestaan';
        return false;
      }
      if (this.event.file.size > 2 * 1024 * 1024) {
        this.errors.file = 'Bestand mag maximaal 2MB zijn';
        return;
      }

      const reader = new FileReader();
      reader.onload = (e) => {
        this.imagePreview = e.target.result;
      };
      reader.readAsDataURL(this.event.file);

      this.errors.file = '';
      return true;
    },
    validateStartDate() {
      if (!this.startDate) {
        this.errors.startDate = 'Startdatum is verplicht';
        return;
      }
      const selectedDate = new Date(this.startDate);
      const today = new Date();
      today.setHours(0, 0, 0, 0);
      if (selectedDate < today) {
        this.errors.startDate = 'Startdatum mag niet in het verleden liggen';
      } else {
        this.errors.startDate = '';
        if (this.endDate) {
          this.validateEndDate();
        }
      }
    },
    validateStartTime() {
      if (!this.startTime) {
        this.errors.startTime = 'Starttijd is verplicht';
        return;
      }
      if (!this.startDate) return;
      const selectedDateTime = new Date(`${this.startDate}T${this.startTime}`);
      const now = new Date();
      if (selectedDateTime < now) {
        this.errors.startTime = 'Starttijd mag niet in het verleden liggen';
      } else {
        this.errors.startTime = '';
        if (this.endDate === this.startDate && this.endTime) {
          this.validateEndTime();
        }
      }
    },
    validateEndDate() {
      if (!this.endDate) {
        this.errors.endDate = 'Einddatum is verplicht';
        return;
      }
      if (!this.startDate) return;
      const endDate = new Date(this.endDate);
      const startDate = new Date(this.startDate);

      if (endDate < startDate) {
        this.errors.endDate = 'Einddatum mag niet voor startdatum liggen';
      } else {
        this.errors.endDate = '';
        if (this.endTime) {
          this.validateEndTime();
        }
      }
    },
    validateEndTime() {
      if (!this.endTime) {
        this.errors.endTime = 'Eindtijd is verplicht';
        return;
      }
      if (!this.startDate || !this.startTime || !this.endDate) return;
      const startDateTime = new Date(`${this.startDate}T${this.startTime}`);
      const endDateTime = new Date(`${this.endDate}T${this.endTime}`);
      if (endDateTime < startDateTime) {
        this.errors.endTime = 'Eindtijd mag niet voor starttijd liggen';
      } else {
        this.errors.endTime = '';
      }
    },
    async submitForm() {
      // Valideer velden
      this.validateStartDate();
      this.validateStartTime();
      this.validateEndDate();
      this.validateEndTime();

      if (!this.isEditing || this.event.file) {
        this.validateFile();
      }

      if (!this.isFormValid) return;

      const formatDate = (dateStr, timeStr) => {
        const [year, month, day] = dateStr.split('-');
        const [hours, minutes] = timeStr.split(':');
        return `${day}-${month}-${year} ${hours}:${minutes}:00`;
      };

      const eventData = {
        eventName: this.event.eventName,
        startDate: formatDate(this.startDate, this.startTime),
        endDate: formatDate(this.endDate, this.endTime),
        location: this.event.location,
        eventDescription: this.event.eventDescription,
      };

      try {
        const store = useEventStore();

        if (this.isEditing) {
          await store.updateEvent(
            this.currentEventId,
            eventData,
            (this.event.file || this.imagePreview === null) ? this.event.file : undefined
          );
        } else {
          await store.addEvent(eventData, this.event.file);
        }

        this.isEditing = false;
        this.resetForm();
      } catch (error) {
        console.error('Error:', error);
      }
    },
    resetForm() {
      this.isEditing = false;
      this.event = {
        eventName: '',
        location: '',
        eventDescription: '',
        file: null,
      };
      this.startDate = null;
      this.startTime = null;
      this.endDate = null;
      this.endTime = null;
      this.errors = {
        eventName: '',
        location: '',
        startDate: '',
        startTime: '',
        endDate: '',
        endTime: '',
        file: '',
      };
      this.selectedEvent = null;
      this.$nextTick(() => {
        this.$refs.form.resetValidation();
      });
    },
  },
};
</script>

<style scoped>
.v-card {
  margin-top: 2rem;
  border-radius: 10px;
  color: white;
  transition: box-shadow 0.3s ease;
  margin-bottom: 10%;
  background-color: rgb(0, 85, 169) !important;
}
.v-card:hover {
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.2) !important;
}

.v-card-title {
  border-top-left-radius: 10px;
  border-top-right-radius: 10px;
}

.v-card-text {
  margin-top: 10px;
}

.v-textarea textarea {
  min-height: 100px !important;
}
.v-btn {
  background-color: white;
}

:deep(.v-text-field__slot input),
:deep(.v-label),
:deep(.v-icon),
:deep(input[type="date"]),
:deep(input[type="time"]),
:deep(.v-select__slot) {
  color: rgba(255, 255, 255, 0.6) !important;
}

:deep(.v-input__slot) {
  border-color: rgba(255, 255, 255, 0.3) !important;
}

:deep(.v-input--is-focused .v-text-field__slot input),
:deep(.v-input--is-focused .v-label),
:deep(.v-input--is-focused .v-icon),
:deep(.v-input--is-focused input[type="date"]),
:deep(.v-input--is-focused input[type="time"]),
:deep(.v-input--is-focused .v-select__slot) {
  color: white !important;
}

:deep(.v-input--is-focused .v-input__slot) {
  border-color: white !important;
}

:deep(input[type="date"]::-webkit-calendar-picker-indicator),
:deep(input[type="time"]::-webkit-calendar-picker-indicator) {
  filter: invert(0.6);
}

:deep(.v-input--is-focused input[type="date"]::-webkit-calendar-picker-indicator),
:deep(.v-input--is-focused input[type="time"]::-webkit-calendar-picker-indicator) {
  filter: invert(1);
}

label {
  color: rgba(255, 255, 255, 0.6) !important;
  display: block;
  margin-bottom: 4px;
  font-size: 0.875rem;
}
.deletebutton {
  margin-left: 10%;
}
</style>
