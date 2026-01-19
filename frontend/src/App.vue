<template>
  <v-app>
    <v-main class="bg-grey-lighten-4">
      <v-container class="py-8">
        <h1 class="text-h4 text-center mb-8">
          Управление активностями
        </h1>

        <v-row>
          <v-col cols="12" md="5">
            <IntervalForm @interval-added="loadIntervals" />
          </v-col>

          <v-col cols="12" md="7">
            <IntervalList 
              :intervals="intervals" 
              :loading="loading" 
            />
          </v-col>
        </v-row>
      </v-container>
    </v-main>

    <v-snackbar
      v-model="snackbar.show"
      :color="snackbar.color"
      :timeout="3000"
    >
      {{ snackbar.message }}
    </v-snackbar>
  </v-app>
</template>

<script setup>
import { ref, onMounted, provide } from 'vue'
import axios from 'axios'
import IntervalForm from './components/IntervalForm.vue'
import IntervalList from './components/IntervalList.vue'

const API_URL = '/api/intervals'

const intervals = ref([])

const loading = ref(false)

const snackbar = ref({
  show: false,
  message: '',
  color: 'info'
})

async function loadIntervals() {
  loading.value = true
  try {
    const response = await axios.get(API_URL)
    intervals.value = response.data
  } catch (error) {
    showNotification('Ошибка загрузки: ' + error.message, 'error')
  } finally {
    loading.value = false
  }
}

function showNotification(message, color = 'info') {
  snackbar.value = {
    show: true,
    message,
    color
  }
}

provide('showNotification', showNotification)

onMounted(() => {
  loadIntervals()
})
</script>
