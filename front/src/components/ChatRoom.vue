<template>
  <div>
    <md-button class="md-button" @click="enterRoom('myroom')">Enter Chat Room</md-button>
    <div v-for="message in messageQueue" v-bind:key="message.id">
      <md-card>
        <md-card-content>
          {{ message }}
        </md-card-content>
      </md-card>
    </div>
    <form @submit="sendMessage">
      <md-field md-inline>
        <md-input v-model="form.message"></md-input>
      </md-field>
      <md-button type="submit" submit="sendMessage">Send Message</md-button>
    </form>
  </div>
</template>

<script>
/* eslint-disable new-cap */

export default {
  name: 'ChatRoom',
  data () {
    return {
      messageId: 0,
      ws: null,
      messageQueue: [],
      form: {
        message: null
      }
    }
  },
  methods: {
    enterRoom: function (roomName) {
      let wsuri = 'ws://localhost:9000/chat/' + roomName + '?user-id=14'
      this.ws = new WebSocket(wsuri)
    },
    sendMessage: function () {
      console.log(this.form.message)
      this.ws.send(JSON.stringify({message: 'test'}))
      this.messageQueue.push({id: this.messageId, message: this.form.message, from: 'To'})
      this.messageId = this.messageId + 1
      this.form.message = null
    }
  },
  mounted () {
    let wsuri = 'ws://localhost:9000/chat/myroom?user-id=14'
    this.ws = new WebSocket(wsuri)

    const self = this
    this.ws.onopen = function () {
      if (this.ws !== undefined && this.ws !== null) {
        this.ws.send(JSON.stringify({message: 'test'}))
      }
    }
    this.ws.onmessage = function (message) {
      console.log(message)
      let json = JSON.parse(message.data)
      if (json.request_message !== undefined) {
        console.log(json)
        self.message = json.request_message.message
        self.messageQueue.push({id: self.messageId, message: self.message, from: 'From'})
        self.messageId = self.messageId + 1
      }
    }
    this.ws.onclose = function () {
      console.log('closed')
    }
    this.ws.onerror = function (error) {
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
