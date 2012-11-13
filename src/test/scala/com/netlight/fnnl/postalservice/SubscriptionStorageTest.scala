package com.netlight.fnnl.postalservice

import akka.actor.ActorSystem
import akka.actor.Actor
import akka.pattern.ask
import akka.actor.Props
import akka.testkit.TestKit
import org.scalatest.WordSpec
import org.scalatest.matchers.MustMatchers
import org.scalatest.BeforeAndAfterAll
import akka.testkit.ImplicitSender
import akka.agent.Agent
import akka.util.Timeout
import akka.util.duration._
import org.scalatest.mock.MockitoSugar
import akka.actor.IO.SocketHandle
import akka.dispatch.Await

class SubscriptionStorageTest(_system: ActorSystem) extends TestKit(_system) with ImplicitSender
  with WordSpec with MustMatchers with BeforeAndAfterAll with MockitoSugar {
  implicit val timeout = Timeout(1 seconds)

  def this() = this(ActorSystem("MySpec"))

  override def afterAll {
    system.shutdown()
  }

  "SubscriptionStorage" must {
    "register subscribers with action" in {
      implicit val timeout = Timeout(1 seconds)
      
      val storage1 = system.actorOf(Props[SubscriptionStorage])
      val storage2 = system.actorOf(Props[SubscriptionStorage])

      storage1 ! GetListeners("some_action")
      expectMsg(Subscribers(Set()))

      val socket1 = mock[SocketHandle]
      storage2 ! AddSubscriber(socket1, List("some_action"))

      Thread.sleep(50)
      storage1 ! GetListeners("some_action")
      expectMsg(Subscribers(Set(socket1)))

      val socket2 = mock[SocketHandle]
      storage1 ! AddSubscriber(socket2, List("some_action", "some_other_action"))

      Thread.sleep(50)
      val subscribersF = storage1 ? GetListeners("some_action")
      val subscribers = Await.result(subscribersF, timeout.duration).asInstanceOf[Subscribers]
      subscribers must equal(Subscribers(Set(socket1, socket2)))

      Thread.sleep(50)
      storage2 ! GetListeners("some_other_action")
      expectMsg(Subscribers(Set(socket2)))
    }
  }
}