package com.netlight.fnnl.postalservice
import akka.util.ByteString
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.core.JsonProcessingException



object MessageFactory {
  val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)
  
  def create(data: ByteString): Message = {
    val stringData = data.decodeString("utf-8")
    MessageParser.parse(stringData) match {
      case Sucessful(messageType, messageData) => {
        buildMessage(messageType, messageData)
      }
      case Unsucessful() => {
        Unknown()
      }
    }
  }
  
  
  
  def buildMessage(messageType:String, messageData:String): Message = {
    try {
      if("register".equals(messageType)){
        val registerData = mapper.readValue(messageData, classOf[RegisterData])
        return Register(registerData)
      }
      if ("action".equals(messageType)) {
        val actionData = mapper.readValue(messageData, classOf[ActionData])
        return Action(actionData)
      }
    } catch {
      case jpe:JsonProcessingException => Unknown() 
    }
    return Unknown()
  }
  
  def create(message: Message) : ByteString = {
	 ByteString(message.typeName + ": " + mapper.writeValueAsString(message.data))
  }
}

