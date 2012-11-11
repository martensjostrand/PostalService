package com.netlight.fnnl.postalservice
import akka.util.ByteString
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.core.JsonProcessingException
import org.slf4j.LoggerFactory



class MessageFactory {
  val log = LoggerFactory.getLogger(classOf[MessageFactory])
  val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)
  
  def create(data: ByteString): Message = {
    val stringData = data.decodeString("utf-8")
    log.debug("Creting message from {}", stringData)
    MessageParser.parse(stringData) match {
      case Sucessful(messageType, messageData) => {
        buildMessage(messageType, messageData)
      }
      case Unsucessful() => {
        log.debug("Could not parse '{}', returning Unknown", stringData)
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
      case jpe:JsonProcessingException => {
        log.error("Parse exception:{}, returning Unknown() ", jpe);
        Unknown()} 
    }
    log.debug("Unrecogniced message type:'{}', returning unknown", messageType)
    return Unknown()
  }
  
  def create(message: Message) : ByteString = {
	 ByteString(message.typeName + ": " + mapper.writeValueAsString(message.data))
  }
}

