Веб-приложение для управления рабочими активностями

Full Stack приложение на Java/Spring Boot + Vue.js 3/Vuetify 
для управления временными интервалами активностей в течение дня с проверкой пересечений.

### Предварительные требования
```bash
# Проверьте установленные версии
java -version       # Нужна Java 17+
mvn -version        # Нужен Maven 3.6+
node -version       # Нужен Node.js 20+
npm -version        # Нужен npm 8+
```
### Быстрый запуск с Docker
# 1. Клонируйте репозиторий
git clone https://github.com/YaroslavVol/intec-test.git
cd intec-test

# 2. Запустите весь стек (PostgreSQL + Backend + Frontend)
docker-compose up 

# 3. Проверьте статус
docker-compose ps

# 4. Откройте приложение
http://localhost

## Установка Backend

### 1. Настройка базы данных PostgreSQL

```bash
# Запустите PostgreSQL
# В psql консоли выполните:
psql -U postgres

CREATE DATABASE activity_manager;
CREATE USER activity_user WITH PASSWORD 'password123';
GRANT ALL PRIVILEGES ON DATABASE activity_manager TO activity_user;
\q
```

### 2. Сборка и запуск

```bash
cd backend

# Установка зависимостей и компиляция
mvn clean install

# Запуск приложения
mvn spring-boot:run
```

Backend будет запущен на http://localhost:8080

## Установка Frontend

### 1. Установка зависимостей

```bash
cd frontend

# Установка всех пакетов из package.json
npm install
```

### 2. Запуск в режиме разработки

```bash
npm run dev
```

Frontend будет запущен на http://localhost:5173


## 📡 API Документация

### Base URL
```
http://localhost:8080/api/intervals
```

### Endpoints

#### 1. GET /api/intervals
Получить все интервалы

**Запрос:**
```bash
curl http://localhost:8080/api/intervals
```

#### 2. POST /api/intervals
Добавить новый интервал

**Запрос:**
```bash
curl -X POST http://localhost:8080/api/intervals \
  -H "Content-Type: application/json" \
  -d '{
    "start": 32400,
    "end": 46800,
    "type": "WORK"
  }'
```

**Успешный ответ (201 Created):**
```json
{
  "id": 1,
  "start": 32400,
  "end": 46800,
  "type": "WORK"
}
```

**Ошибка пересечения (409 Conflict):**
```json
{
  "error": "Interval overlaps with existing interval: 09:00:00 - 13:00:00 (Работа)"
}
```

**Ошибка валидации (400 Bad Request):**
```json
{
  "error": "Start must be less than end"
}
```



