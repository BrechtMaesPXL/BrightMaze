<template>
    <v-container>
        <v-row class="mb-4">
            <v-col cols="12" sm="4">
                <v-text-field 
                    v-model="filters.name" 
                    label="Filter op naam" 
                    clearable 
                    class="custom-filter"
                    prepend-inner-icon="mdi-magnify"
                ></v-text-field>
            </v-col>
            <v-col cols="12" sm="4">
                <v-text-field 
                    v-model="filters.startDate" 
                    type="date" 
                    label="Filter op startdatum" 
                    class="custom-filter"
                    prepend-inner-icon="mdi-calendar"
                ></v-text-field>
            </v-col>
            <v-col cols="12" sm="4">
                <v-text-field 
                    v-model="filters.location" 
                    label="Filter op locatie" 
                    clearable 
                    class="custom-filter"
                    prepend-inner-icon="mdi-map-marker"
                ></v-text-field>
            </v-col>
        </v-row>
        
        <v-list class="event-list">
            <v-alert v-if="sortedFilteredEvents.length === 0" type="info" class="mt-4">
                Er zijn momenteel geen evenementen beschikbaar.
            </v-alert>
            <v-list-item-group>
                <v-list-item v-for="event in sortedFilteredEvents" :key="event.id" class="event-item">
                    <v-list-item-content>
                        <v-list-item-title class="event-title">{{ event.eventName }}</v-list-item-title>
                        <v-list-item-content class="event-date">
                            🕐 Start: <span class="date-text">{{ formatDutchDate(splitDateTime(event.startDate).date) }}</span>  
                            {{ splitDateTime(event.startDate).time }} 
                            <br>
                            🕐 Einde: <span class="date-text">{{ formatDutchDate(splitDateTime(event.endDate).date) }}</span>
                            {{ splitDateTime(event.endDate).time }}
                        </v-list-item-content>
                        <br>
                        <v-list-item-content class="event-location">
                            📍 {{ event.location }}
                        </v-list-item-content>
                        <br>
                        <v-list-item-content class="event-description">
                            Extra info: {{ event.eventDescription }}
                        </v-list-item-content>
                    </v-list-item-content>
                    
                    <v-list-item-action>
                        <v-btn color="blue" @click="updateEvent(event)">Bijwerken</v-btn>
                        <v-btn color="red" @click="deleteEvent(event.id)" class="ml-2">Verwijderen</v-btn>
                    </v-list-item-action>
                </v-list-item>
            </v-list-item-group>
        </v-list>
    </v-container>
</template>

<script>
import { ref, computed, onMounted } from 'vue';
import { storeToRefs } from 'pinia';
import { useEventStore } from '@/stores/eventStore';

export default {
    setup() {
        const store = useEventStore();
        const { events } = storeToRefs(store);
        const filters = ref({ name: '', startDate: '', location: '' });
        
        const sortedFilteredEvents = computed(() => {
            return events.value.filter(event => {
                return (
                    (!filters.value.name || event.eventName.toLowerCase().includes(filters.value.name.toLowerCase())) &&
                    (!filters.value.startDate || splitDateTime(event.startDate).date === filters.value.startDate) &&
                    (!filters.value.location || event.location.toLowerCase().includes(filters.value.location.toLowerCase()))
                );
            });
        });

        const deleteEvent = async (id) => {
            if (!confirm("Weet je zeker dat je dit evenement wilt verwijderen?")) return;
            try {
                await store.deleteEvent(id);
            } catch (err) {
                console.error("Delete error:", err);
            }
        };

        const updateEvent = (event) => {
            store.setSelectedEvent(event);

        };

        const splitDateTime = (dateTimeString) => {
            if (!dateTimeString) return { date: '', time: '' };
            const [date, timeWithSeconds] = dateTimeString.split('T');
            const time = timeWithSeconds ? timeWithSeconds.split(':').slice(0, 2).join(':') : '';
            return { date, time };
        };

        const formatDutchDate = (isoDate) => {
            if (!isoDate) return '';
            const [year, month, day] = isoDate.split('-');
            return `${day}-${month}-${year}`;
        };

        onMounted(async() => {
            await store.fetchEvents();
        });

        return {
            events,
            filters,
            sortedFilteredEvents,
            deleteEvent,
            updateEvent,
            splitDateTime,
            formatDutchDate,
        };
    }
};
</script>

<style scoped>
.event-list {
    margin-top: 5px;
    background: transparent;
}

.event-item {
    border: 2px solid white;
    padding: 10px !important;
    margin-bottom: 10px;
    border-radius: 1rem !important;
    margin-left: 10px;
    margin-right: 10px;
}
.v-list-item{
    transition: box-shadow 0.3s ease;
    /* Alternatieve oplossing */
    transform-origin: center;
    contain: content;
}
.v-list-item:hover{
    box-shadow: 0 10px 20px rgba(0,0,0,0.5) !important;
}

.event-title {
    font-weight: bold;
    font-size: 22px;
    color: white;
}

.event-date,
.event-location,
.event-description {
    color: white !important;
    font-size: 18px;
}

.date-text {
    color: rgba(255, 255, 255, 0.8) !important;
}

.v-btn {
    color: white;
    text-transform: none;
}

.v-list-item-action {
    float: right;
    margin: 20px;
}

.custom-filter :deep(.v-input__control),
.custom-filter :deep(.v-input__slot),
.custom-filter :deep(.v-text-field__slot),
.custom-filter :deep(input),
.custom-filter :deep(.v-label) {
    color: rgba(255, 255, 255, 0.8) !important; /* Subtiel wit met 80% opacity */
}

/* Icoontjes - iets transparanter */
.custom-filter :deep(.v-icon) {
    color: rgba(255, 255, 255, 0.7) !important;
    opacity: 0.9;
}

/* Datumveld specifieke aanpassingen */
.custom-filter :deep(input[type="date"]),
.custom-filter :deep(input[type="date"]::-webkit-datetime-edit),
.custom-filter :deep(input[type="date"]::-webkit-datetime-edit-fields-wrapper),
.custom-filter :deep(input[type="date"]::-webkit-datetime-edit-text),
.custom-filter :deep(input[type="date"]::-webkit-datetime-edit-month-field),
.custom-filter :deep(input[type="date"]::-webkit-datetime-edit-day-field),
.custom-filter :deep(input[type="date"]::-webkit-datetime-edit-year-field) {
    color: rgba(255, 255, 255, 0.8) !important;
}

/* Kalender icoon in datumveld */
.custom-filter :deep(input[type="date"]::-webkit-calendar-picker-indicator) {
    filter: invert(0.8) brightness(0.8);
}

/* Placeholder tekst */
.custom-filter :deep(input::placeholder) {
    color: rgba(255, 255, 255, 0.6) !important;
}

/* Wanneer een veld is geselecteerd */
.custom-filter :deep(.v-input--is-focused .v-input__slot) {
    border-color: rgba(255, 255, 255, 0.5) !important;
}

/* Event items datum tekst - consistent met filters */
.date-text {
    color: rgba(255, 255, 255, 0.7) !important;
}
</style>
