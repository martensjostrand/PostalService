package com.netlight.fnnl.postalservice
import org.specs2.mutable.Specification

class MessageParserTest extends Specification {
  "The MessageParser" should {
    "accept correct messages" in {
      val messageType = "action"
      val messageArgs = """{"command": "walk", "args": ["arg1", "arg2"]}"""
      val message = messageType + ": " + messageArgs
      val actual = parse(message)
      val expected = Sucessful(messageType, messageArgs)
      actual must be equalTo expected
    }
    "deny emty messages" in {
      val message = ""
      val actual = parse(message)
      val expected = Unsucessful()
      actual must be equalTo expected
    }
  }

  def parse(test: String): ParserResult = {
    MessageParser.parse(test)
  }
}