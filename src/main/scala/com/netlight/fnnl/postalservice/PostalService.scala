package com.netlight.fnnl.postalservice

import akka.actor._
import java.net.InetSocketAddress

import akka.event.Logging
import akka.actor.IO.SocketHandle
import akka.util.ByteString
import scala.collection._

class MessageHandler(listeners: mutable.HashMap[String, List[InetSocketAddress]]){
  
  def dispatch(message: Message) = {
    message match {
      case action:Action => {
          listeners.getOrElse(action.data.command, Nil) map {address =>
        	ActorSystem().actorOf(Props(new TCPSender(address))) ! MessageFactory.create(action)  
        }
      }
      case Register(registration) => {
        val address = new InetSocketAddress(registration.host, registration.port); 
        for(action <- registration.actions){
          val sockets = listeners.getOrElse(action, List[InetSocketAddress]());
          val updatedSockets = address :: sockets;
       	  listeners.put(action, updatedSockets);
        }
      }
    }
  }
}

class TCPServer(port: Int) extends Actor with ActorLogging {
  val messageHandler = new MessageHandler(listeners);
  val listeners = mutable.HashMap[String, List[InetSocketAddress]]();
  
  override def preStart {
    log.debug("Listening for TCP connections on port {}", port)
    IOManager(context.system) listen new InetSocketAddress(port)
  }

  def receive = {
    case IO.NewClient(server) => {
      log.debug("Accepting new connection")
      server.accept()}
    case IO.Read(rHandle, bytes) => {
      val message = MessageFactory.create(bytes);
      messageHandler.dispatch(message);
    }
  }
}
  

class TCPSender(socketAddress: InetSocketAddress) extends Actor with ActorLogging {
  def receive = {
    case message:String => {
    	IOManager(context.system).connect(socketAddress).write(ByteString(message))
    	log.debug("Sending '{}' to {}:{}", message, socketAddress.getHostName(), socketAddress.getPort());
    }
  }
}

object PostalService extends App{
	val port = Option(System.getenv("PORT")) map (_.toInt) getOrElse 8080
	ActorSystem().actorOf(Props(new TCPServer(port)))
}

