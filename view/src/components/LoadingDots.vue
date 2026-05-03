<script>
export default {
  name: 'LoadingDots'
}
</script>

<template>
  <div class="loading-dots">
    <div class="dot"></div>
    <div class="dot"></div>
    <div class="dot"></div>
  </div>
</template>

<style scoped>
.loading-dots {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 8px 0;
}

.dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: linear-gradient(135deg, #4caf50 0%, #45a049 100%);
  animation: bounce 1.4s infinite ease-in-out;
  box-shadow: 0 0 10px rgba(76, 175, 80, 0.5);
}

.dot:nth-child(1) {
  animation-delay: -0.32s;
}

.dot:nth-child(2) {
  animation-delay: -0.16s;
}

@keyframes bounce {
  0%, 80%, 100% {
    transform: scale(0.6);
    opacity: 0.6;
  }
  40% {
    transform: scale(1);
    opacity: 1;
    box-shadow: 0 0 15px rgba(76, 175, 80, 0.8);
  }
}
</style>
