package com.netlight.fnnl.postalservice
import org.scalatest._
import akka.util.ByteString
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.matchers.MustMatchers

class MessageFactoryTest extends FlatSpec with ShouldMatchers {
	
  val messageFactory = new MessageFactory()
  
	"A MessageFactory" should 
	  "Handle Register messages" in {
        testRoundTrip(Register(RegisterData(List("action1", "action2"))))
	}
	it should "Handle Action messages" in {
	    testRoundTrip(Action(ActionData("walk", List("left", "100m"))))
	}
	it should "Handle empty messages" in {
	    val actual = messageFactory.create(ByteString(""))
	    actual should equal( Unknown())
	}
	it should "Handle malformed json" in {
	    val actual = ByteString("""action: {"bla": 6, "args":{[}""")
	    asMessage(actual) should equal(Unknown())
	}
	
	
	def testRoundTrip(expected: Message){
	  val byteString = asByteString(expected);
	  val actual = asMessage(byteString)
	  actual should equal (expected)
	}
	
	def asMessage(bytes: ByteString): Message = {
	  messageFactory.create(bytes)
	}
	
	def asByteString(message: Message) : ByteString = {
	  messageFactory.create(message)
	}
}