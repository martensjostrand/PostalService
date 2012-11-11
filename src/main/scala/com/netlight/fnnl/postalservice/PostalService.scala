package com.netlight.fnnl.postalservice

import akka.actor._
import java.net.InetSocketAddress
import akka.event.Logging
import akka.actor.IO.SocketHandle
import akka.util.ByteString
import scala.collection._
import scala.actors.Logger

class TCPServer(port: Int) extends Actor with ActorLogging {
  val sockets = mutable.HashMap[String, List[SocketHandle]]()
  val messageHandler = new MessageHandler(sockets)
  val messageFactory = new MessageFactory()
  override def preStart {
    log.debug("Listening for TCP connections on port {}", port)
    IOManager(context.system) listen new InetSocketAddress(port)
  }

  def receive = {
    case IO.NewClient(server) => {
      log.debug("Accepting new connection")
      server.accept()
      // Create an actor with this socket for future communication.
    }

    case IO.Read(rHandle, bytes) => {
      val socket = rHandle.asSocket;
      val message = messageFactory.create(bytes)
      log.debug("Got message: {}", message)
      messageHandler.dispatch(message, socket);
    }
  }
}

object PostalService extends App {
  val port = Option(System.getenv("PORT")) map (_.toInt) getOrElse 8080
  ActorSystem().actorOf(Props(new TCPServer(port)))
}

