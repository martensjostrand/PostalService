package com.netlight.fnnl.postalservice
import akka.util.ByteString
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule



object MessageFactory {
  val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)
  
  def create(data: ByteString): Message = {
    val stringData = data.decodeString("utf-8")
    val firstColonIndex = stringData.indexOf(":");
    val messageType = stringData.substring(0, firstColonIndex)
    val messageData = stringData.substring(firstColonIndex+1)
    
    if("register".equals(messageType)){    
      val registerData = mapper.readValue(messageData, classOf[RegisterData])
      return Register(registerData)
    }
    if ("action".equals(messageType)) {
       val actionData = mapper.readValue(messageData, classOf[ActionData])
       return Action(actionData)
    }
    return Unknown()
  }
  
  def create(message: Message) : ByteString = {
	 ByteString(message.typeName + ": " + mapper.writeValueAsString(message.data))
  }
}

