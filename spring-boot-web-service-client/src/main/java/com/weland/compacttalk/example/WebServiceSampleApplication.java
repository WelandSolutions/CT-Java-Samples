/* ========================================================================
* MIT License
*
* Copyright (c) 2021 Weland Solutions AB
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*
* ======================================================================*/

package com.weland.compacttalk.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.weland.compacttalk.wsdl.ActivateEventsResponse;
import com.weland.compacttalk.wsdl.AddToQueueResponse;
import com.weland.compacttalk.wsdl.CTEvent;
import com.weland.compacttalk.wsdl.CTOrderStatusChangeEvent;
import com.weland.compacttalk.wsdl.GetVersionResponse;
import com.weland.compacttalk.wsdl.ObjectFactory;
import com.weland.compacttalk.wsdl.OrderMode;
import com.weland.compacttalk.wsdl.OrderStatus;
import com.weland.compacttalk.wsdl.PickOrder;
import com.weland.compacttalk.wsdl.PollForEventResponse;

@SpringBootApplication
public class WebServiceSampleApplication {

	private static final Logger log = LoggerFactory.getLogger(WebServiceSampleApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(WebServiceSampleApplication.class, args);
	}

	@Bean
	CommandLineRunner lookupVersion(CompactTalkClient ctClient) {
		return args -> {
			GetVersionResponse response = ctClient.getVersion();
			log.info("Compact Talk version " + response.getGetVersionResult().getValue());
		};
	}
	@Bean
	CommandLineRunner executeOrder(CompactTalkClient ctClient) {
		return args -> {
			ActivateEventsResponse activateEventsResponse = ctClient.activateEvents();
			System.err.println("Activated events");
			log.info("Activated events with token " + activateEventsResponse.getActivateEventsResult());

			AddToQueueResponse addToQueueResponse = AddToQueue(ctClient);
			log.info("Added order with ID = " + addToQueueResponse.getAddToQueueResult().intValue());
			
			PickOrder orderAtPlace = awaitAtPlace(ctClient, activateEventsResponse.getActivateEventsResult());
			
			ctClient.acknowledgeOrder(orderAtPlace.getElevatorName().getValue(), orderAtPlace.getServiceOpening(), 1f);
			log.info("Acknowledged order with ID = " + orderAtPlace.getId());

		};
	}
	
	private AddToQueueResponse AddToQueue(CompactTalkClient ctClient) {
		ObjectFactory factory = new ObjectFactory();

		PickOrder order = new PickOrder();
		order.setArtDescr(factory.createAddToQueueArtDescr("My test article"));
		order.setArtNo(factory.createAddToQueueArtNo("TestArticle1"));
		order.setElevatorName(factory.createAddToQueueElevatorId("Sim_1"));
		order.setServiceOpening(1);
		order.setQuantity(2f);
		order.setTransId(factory.createAddToQueueTransId("trans1"));
		order.setTrayNo(1);
		order.setServiceOpening(1);
		order.setTrayCoord(factory.createAddToQueueTrayCoord(""));
		order.setMode(OrderMode.OUT);
		order.setNoReturnOfTray(0);
		order.setPriority(1L);
		order.setJob(factory.createAddToQueueJob(""));
		order.setCurrentBoxName(factory.createAddToQueueCurrentBoxName(""));
		order.setInfo1(factory.createAddToQueueInfo1(""));
		order.setInfo2(factory.createAddToQueueInfo2(""));
		order.setInfo3(factory.createAddToQueueInfo3(""));
		order.setInfo4(factory.createAddToQueueInfo4(""));
		order.setInfo5(factory.createAddToQueueInfo5(""));

	    return ctClient.addToQueue(order);
	}
	
	private PickOrder awaitAtPlace(CompactTalkClient ctClient, String token) throws InterruptedException {
        
        while(true)
        {
        	PollForEventResponse pollForEventResponse = ctClient.pollForEvent(token);
        	for (CTEvent ctEvent : pollForEventResponse.getPollForEventResult().getValue().getCTEvent()) {
        		if (ctEvent instanceof CTOrderStatusChangeEvent) {
        			CTOrderStatusChangeEvent ctOrderStatusChangeEvent = (CTOrderStatusChangeEvent) ctEvent;
        			if (ctOrderStatusChangeEvent.getOrder().getValue().getStatus() == OrderStatus.AT_PLACE) {
        				log.info("Order with ID = " + ctOrderStatusChangeEvent.getOrder().getValue().getId() + " is at place");
        				return ctOrderStatusChangeEvent.getOrder().getValue();
        			}
        		}
			}
        	Thread.sleep(1000);
        }
	}
	
}
