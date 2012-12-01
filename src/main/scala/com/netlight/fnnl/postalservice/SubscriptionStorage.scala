package com.netlight.fnnl.postalservice

import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.ActorLogging
import akka.actor.IO.SocketHandle
import akka.agent.Agent
import scala.collection.mutable.HashMap
import scala.collection.immutable.HashSet
import org.slf4j.LoggerFactory

case class AddSubscriber(socket: SocketHandle, actions: List[String])
case class GetListeners(action: String)
case class GetAllListeners()
case class Subscribers(sockets: Set[SocketHandle])
case class Subscription(socket:SocketHandle, action:String)

class SubscriptionStorage extends Actor with ActorLogging {
  
  override def receive = {
    case AddSubscriber(socket, actions) => {
      log.debug("received request to add socket {} to actions: {}", socket, actions)
      registerSubscriber(socket, actions)
    }
    case GetListeners(action) => {
      log.debug("Request to get listeneras for action: {}", action)
      sender ! getSubscribers(action)
    }
    case GetAllListeners() => {
      log.debug("Request to get all listeneras")
      sender ! getAllSubscribers()
    }
  }

  private def registerSubscriber(socket:SocketHandle, actions: List[String]) = {
    log.debug("Registering socket: {} to actions: {}", socket, actions)
    val addFunction = addSubscriber(socket, actions)
    SubscriptionStorage.subscribers.send(addFunction)
  }

  private def addSubscriber(socket: SocketHandle, actions: List[String]) = {
    (subscriptions: Set[Subscription]) => {
      actions.foldLeft(subscriptions){(subs, action)  =>
        log.debug("Adding ({} -> {}) to {}", socket, action, subs)
        subs + Subscription(socket, action)
      }
    }
  }

  private def getSubscribers(action: String) = {
    val sockets = for(subsrciption <- SubscriptionStorage.subscribers(); if action.equals(subsrciption.action)) yield subsrciption.socket
    // SubscriptionStorage.subscribers map {s => s.socket}
    log.debug("Found sockets:'{}' when searching by action: '{}'", sockets, action)
    Subscribers(sockets)
  }

  private def getAllSubscribers() = {
    val sockets = for(subsrciption <- SubscriptionStorage.subscribers()) yield subsrciption.socket
    // SubscriptionStorage.subscribers map {s => s.socket}
    log.debug("Found sockets:'{}' when searching all subscribers", sockets)
    Subscribers(sockets)
  }
}

object SubscriptionStorage{
  private var subscribers = Agent(Set[Subscription]())(ActorSystem("storage"))
}
