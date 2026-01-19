<template>
  <v-card class="pa-4">
    <h2 class="text-h6 mb-4">Добавить интервал</h2>

    <v-form @submit.prevent="handleSubmit">
      <v-text-field
        v-model.number="form.start"
        label="Начало (секунды)"
        type="number"
        variant="outlined"
        density="compact"
        hint="Например: 32400 = 09:00"
        class="mb-3"
      />
      <v-text-field
        v-model.number="form.end"
        label="Конец (секунды)"
        type="number"
        variant="outlined"
        density="compact"
        hint="Например: 46800 = 13:00"
        class="mb-3"
      />
      <div class="mb-4">
        <div class="text-subtitle-2 mb-2">Тип активности</div>
        <v-radio-group v-model="form.type" inline>
          <v-radio label="Работа" value="WORK" />
          <v-radio label="Перерыв" value="BREAK" />
        </v-radio-group>
      </div>
      <v-btn
        type="submit"
        color="primary"
        block
        :loading="submitting"
      >
        Добавить
      </v-btn>
    </v-form>

    <v-divider class="my-4" />

    <div>
      <div class="text-subtitle-2 mb-2">Быстрые примеры:</div>
      
      <v-btn
        variant="outlined"
        size="small"
        block
        class="mb-2 text-left"
        @click="fillExample(32400, 46800, 'WORK')"
      >
        9:00 - 13:00 (Работа)
      </v-btn>

      <v-btn
        variant="outlined"
        size="small"
        block
        class="mb-2 text-left"
        @click="fillExample(46800, 50400, 'BREAK')"
      >
        13:00 - 14:00 (Обед)
      </v-btn>

      <v-btn
        variant="outlined"
        size="small"
        block
        class="text-left"
        @click="fillExample(50400, 64800, 'WORK')"
      >
        14:00 - 18:00 (Работа)
      </v-btn>
    </div>
  </v-card>
</template>

<script setup>
import { ref, inject } from 'vue'
import axios from 'axios'

const emit = defineEmits(['interval-added'])
const showNotification = inject('showNotification')

const form = ref({
  start: null,
  end: null,
  type: 'WORK'
})

const submitting = ref(false)

async function handleSubmit() {

  if (!form.value.start || !form.value.end) {
    showNotification('Заполните все поля', 'warning')
    return
  }

  if (form.value.start >= form.value.end) {
    showNotification('Начало должно быть меньше конца', 'warning')
    return
  }

  submitting.value = true

  try {
    await axios.post('/api/intervals', form.value)
    showNotification('Интервал добавлен!', 'success')
 
    form.value = {
      start: null,
      end: null,
      type: 'WORK'
    }
    
    emit('interval-added')
    
  } catch (error) {
    const errorMessage = error.response?.data?.error || error.message
    showNotification(errorMessage, 'error')
  } finally {
    submitting.value = false
  }
}

function fillExample(start, end, type) {
  form.value = { start, end, type }
}
</script>
