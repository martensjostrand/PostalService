package com.netlight.fnnl.postalservice

import akka.actor.IO.SocketHandle
import scala.collection.mutable.HashMap
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import akka.dispatch.Await
import akka.actor.ActorRef
import akka.util.Timeout
import akka.util.duration._
import akka.pattern.ask


class MessageHandler(subscriptionStorage: ActorRef) {
  val messageFactory = new MessageFactory()
  val log = LoggerFactory.getLogger(classOf[MessageHandler]);
  implicit val timeout = Timeout(1 seconds)
  /*
   * Should be  an actor and receive Registration, Actions and (probably) Events. 
   * Should only parse payload of Register messages and forward other messages.
   */
  def dispatch(message: Message, socket: SocketHandle) = {
    def getSubscribersBy(action: String) = {
      Await.result(subscriptionStorage ? GetListeners(action), timeout.duration).asInstanceOf[Subscribers]
    }

    def getAllSubscribers() = {
      Await.result(subscriptionStorage ? GetAllListeners(),timeout.duration).asInstanceOf[Subscribers]
    }
    
    message match {
      case action: Action => {
        val subscribers = getSubscribersBy(action.data.command)
        subscribers.sockets.map{socket=>
          socket.write(messageFactory.create(action))
        }
      }
      case Register(registration) => {
        subscriptionStorage ! AddSubscriber(socket, registration.actions)
      }
      case event: Event => {
	val subscribers = getAllSubscribers()
	subscribers.sockets.map{socket=>
	  socket.write(messageFactory.create(event))
	}
      }
      case Unknown() => {}
    }
  }
}
