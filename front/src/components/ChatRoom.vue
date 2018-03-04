<template>
  <div>
    <MdButton class="md-button">Enter Chat Room</MdButton>
  </div>
</template>

<script>
/* eslint-disable new-cap */

export default {
  name: 'ChatRoom',
  mounted () {
    let wsuri = 'ws://localhost:9000/chat/myroom?user-id=14'
    const ws = new WebSocket(wsuri)

    ws.onopen = function () {
      ws.send(JSON.stringify({message: 'test'}))
    }
    ws.onmessage = function (message) {
      console.log(message)
    }
    ws.onclose = function () {
      console.log('closed')
    }
    ws.onerror = function (error) {
      console.log(error)
    }
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.md-button {
  text-transform: capitalize !important; /*For Lower case use lowercase*/
  background-color: #B3E5FC;
}
</style>
