import Vue from 'vue'
import Router from 'vue-router'
import ChatRoom from '@/components/ChatRoom'
import VueMaterial from 'vue-material'
import 'vue-material/dist/vue-material.min.css'

Vue.use(VueMaterial)
Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'ChatRoom',
      component: ChatRoom
    }
  ]
})
