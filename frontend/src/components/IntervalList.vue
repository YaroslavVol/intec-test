<template>
  <v-card class="pa-4">
    <h2 class="text-h6 mb-4">Список интервалов</h2>
    
    <div v-if="loading" class="text-center py-8">
      <v-progress-circular indeterminate color="primary" />
      <p class="mt-4">Загрузка...</p>
    </div>

    <div v-else-if="intervals.length === 0" class="text-center py-8">
      <p class="text-grey">Нет интервалов</p>
      <p class="text-caption text-grey">Добавьте первый интервал</p>
    </div>

    <div v-else>
      <v-row class="mb-4">
        <v-col cols="4">
          <div class="text-center pa-2 bg-grey-lighten-3 rounded">
            <div class="text-h6">{{ intervals.length }}</div>
            <div class="text-caption">Всего</div>
          </div>
        </v-col>
        <v-col cols="4">
          <div class="text-center pa-2 bg-blue-lighten-5 rounded">
            <div class="text-h6">{{ formatHours(totalWorkTime) }}</div>
            <div class="text-caption">Работа</div>
          </div>
        </v-col>
        <v-col cols="4">
          <div class="text-center pa-2 bg-green-lighten-5 rounded">
            <div class="text-h6">{{ formatHours(totalBreakTime) }}</div>
            <div class="text-caption">Перерыв</div>
          </div>
        </v-col>
      </v-row>

      <v-divider class="mb-4" />

      <div class="intervals-list">
        <v-card
          v-for="interval in intervals"
          :key="interval.id"
          class="mb-3 pa-3"
          :class="interval.type === 'WORK' ? 'border-l-4 border-blue' : 'border-l-4 border-green'"
        >
          <div class="d-flex justify-space-between align-center">
            <div>
              <div class="text-subtitle-1 font-weight-medium">
                {{ interval.type === 'WORK' ? 'Работа' : 'Перерыв' }}
              </div>
              <div class="text-body-2 text-grey">
                {{ formatTime(interval.start) }} - {{ formatTime(interval.end) }}
              </div>
            </div>

            <div class="text-right">
              <v-chip size="small" :color="interval.type === 'WORK' ? 'blue' : 'green'">
                {{ formatDuration(interval.end - interval.start) }}
              </v-chip>
            </div>
          </div>
        </v-card>
      </div>
    </div>
  </v-card>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  intervals: {
    type: Array,
    default: () => []
  },
  loading: {
    type: Boolean,
    default: false
  }
})

const totalWorkTime = computed(() => {
  return props.intervals
    .filter(i => i.type === 'WORK')
    .reduce((sum, i) => sum + (i.end - i.start), 0)
})

const totalBreakTime = computed(() => {
  return props.intervals
    .filter(i => i.type === 'BREAK')
    .reduce((sum, i) => sum + (i.end - i.start), 0)
})

function formatTime(seconds) {
  const h = Math.floor(seconds / 3600)
  const m = Math.floor((seconds % 3600) / 60)
  const s = seconds % 60
  return `${String(h).padStart(2, '0')}:${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`
}

function formatDuration(seconds) {
  const hours = Math.floor(seconds / 3600)
  const minutes = Math.floor((seconds % 3600) / 60)
  
  if (hours > 0 && minutes > 0) {
    return `${hours}ч ${minutes}м`
  } else if (hours > 0) {
    return `${hours}ч`
  } else {
    return `${minutes}м`
  }
}

function formatHours(seconds) {
  const hours = (seconds / 3600).toFixed(1)
  return `${hours}ч`
}
</script>

<style scoped>
.intervals-list {
  max-height: 600px;
  overflow-y: auto;
}

.border-l-4 {
  border-left-width: 4px;
  border-left-style: solid;
}

.border-blue {
  border-left-color: rgb(33, 150, 243);
}

.border-green {
  border-left-color: rgb(76, 175, 80);
}
</style>