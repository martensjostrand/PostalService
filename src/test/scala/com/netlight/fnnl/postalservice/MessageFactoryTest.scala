package com.netlight.fnnl.postalservice
import org.specs2.mutable._
import akka.util.ByteString
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.module.scala.DefaultScalaModule


@JsonCreator
case class MyClass(@JsonProperty("name") name: String)

class MessageFactoryTest extends Specification {
	"A MessageFactory" should {
	  "Handle Register messages" in {
        testRoundTrip(Register(RegisterData("myhost", 9090, List("action1", "action2"))))
	  }
	  "Handle Action messages" in {
	    testRoundTrip(Action(ActionData("walk", List("left", "100m"))))
	  }
	  "Handle empty messages" in {
	    val actual = MessageFactory.create(ByteString(""))
	    actual must be equalTo Unknown()
	  }
	  "Handle malformed json" in {
	    val actual = ByteString("""action: {"bla": 6, "args":{[}""")
	    asMessage(actual) must be equalTo Unknown()
	  }
	}
	
	def testRoundTrip(expected: Message){
	  val byteString = asByteString(expected);
	  val actual = asMessage(byteString)
	  actual must be equalTo expected
	}
	
	def asMessage(bytes: ByteString): Message = {
	  MessageFactory.create(bytes)
	}
	
	def asByteString(message: Message) : ByteString = {
	  MessageFactory.create(message)
	}
}