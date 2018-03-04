<template>
  <div>
    <MdButton class="md-button">Enter Chat Room</MdButton>
    <div v-for="message in messageQueue" v-bind:key="message.id">
      <md-card>
        <md-card-content>
          {{ message }}
        </md-card-content>
      </md-card>
    </div>
  </div>
</template>

<script>
/* eslint-disable new-cap */

export default {
  name: 'ChatRoom',
  data () {
    return {
      messageQueue: []
    }
  },
  mounted () {
    let wsuri = 'ws://localhost:9000/chat/myroom?user-id=14'
    const ws = new WebSocket(wsuri)

    const self = this
    ws.onopen = function () {
      ws.send(JSON.stringify({message: 'test'}))
    }
    var i = 0
    ws.onmessage = function (message) {
      console.log(message)
      let json = JSON.parse(message.data)
      if (json.request_message !== undefined) {
        console.log(json)
        self.message = json.request_message.message
        self.messageQueue.push({id: i, message: self.message})
        i = i + 1
      }
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
