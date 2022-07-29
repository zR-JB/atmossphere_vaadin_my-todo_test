package com.example.application.services;

import java.io.IOException;

import com.example.application.context.ApplicationContextHolder;
import com.example.application.entity.Person;
import com.example.application.services.repos.PersonRepository;
import com.example.application.services.transfer.Message;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.atmosphere.config.managed.Decoder;
import org.atmosphere.config.managed.Encoder;
import org.atmosphere.config.service.Disconnect;
import org.atmosphere.config.service.ManagedService;
import org.atmosphere.config.service.Ready;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@ManagedService(path = "/chat")
public class ChatService {

	//marked as: Not sure if this is right
	////////////////////////////////////////////////////////////////////////
	PersonService service = ApplicationContextHolder.getBean(PersonService.class);
	///////////////////////////////////////////////////////////////////////




	public static class JacksonEncoderDecoder implements Encoder<Message, String>, Decoder<String, Message> {
		private final ObjectMapper mapper = new ObjectMapper();

		@Override
		public String encode(Message m) {
			try {
				return this.mapper.writeValueAsString(m);
			}
			catch (IOException ex) {
				throw new IllegalStateException(ex);
			}
		}

		@Override
		public Message decode(String s) {
			try {
				return this.mapper.readValue(s, Message.class);
			}
			catch (IOException ex) {
				throw new IllegalStateException(ex);
			}
		}
	}

	private final Logger logger = LoggerFactory.getLogger(ChatService.class);



	@Ready
	public void onReady(final AtmosphereResource resource) {		//I think the extra Parameter "PersonService service" breaks the onReady-Atmosphere Event
		this.logger.info("Connected {}", resource.uuid());								//This logger never gets triggered
		Person person = new Person();													//so onReady function gets invalid
		person.setFirstName("this is a test entity");									//but how do I add a new Person to
		service.update(person);													//the grid at the main page which is linked
	}																					//to the PersonRepository
																						//onReady normally gets triggered when connecting
																						//to ws://localhost:8080/chat with websocket-client
																						//then logger message should get triggered

	@Disconnect
	public void onDisconnect(AtmosphereResourceEvent event) {
		this.logger.info("Client {} disconnected [{}]", event.getResource().uuid(), (event.isCancelled() ? "cancelled" : "closed"));
	}

	@org.atmosphere.config.service.Message(encoders = JacksonEncoderDecoder.class, decoders = JacksonEncoderDecoder.class)
	public Message onMessage(Message message) throws IOException {
		this.logger.info("Author {} sent message {}", message.getAuthor(), message.getMessage());
		return message;
	}
}
