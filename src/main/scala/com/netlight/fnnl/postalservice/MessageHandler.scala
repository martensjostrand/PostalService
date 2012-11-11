package com.netlight.fnnl.postalservice

import akka.actor.IO.SocketHandle
import scala.collection.mutable.HashMap

class MessageHandler(listeners: HashMap[String, List[SocketHandle]]) {

  def dispatch(message: Message, socket: SocketHandle) = {
    message match {
      case action: Action => {
        /* Send message to all registered sockets */
        listeners.getOrElse(action.data.command, Nil) map { socket =>
          socket.write(MessageFactory.create(action));
        }
      }
      case Register(registration) => {
        /* Add socket to actions */
        for (action <- registration.actions) {
          val sockets = listeners.getOrElse(action, List[SocketHandle]());
          val updatedSockets = socket :: sockets
          listeners.put(action, updatedSockets)
        }
      }
      case Unknown() => {}
    }
  }
}
