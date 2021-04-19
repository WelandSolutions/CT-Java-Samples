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

import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.client.core.SoapActionCallback;

import com.weland.compacttalk.wsdl.ActivateEvents;
import com.weland.compacttalk.wsdl.ActivateEventsResponse;
import com.weland.compacttalk.wsdl.AddToQueue;
import com.weland.compacttalk.wsdl.AddToQueueResponse;
import com.weland.compacttalk.wsdl.ExtAckOrder;
import com.weland.compacttalk.wsdl.ExtAckOrderResponse;
import com.weland.compacttalk.wsdl.GetVersion;
import com.weland.compacttalk.wsdl.GetVersionResponse;
import com.weland.compacttalk.wsdl.ObjectFactory;
import com.weland.compacttalk.wsdl.PickOrder;
import com.weland.compacttalk.wsdl.PollForEvent;
import com.weland.compacttalk.wsdl.PollForEventResponse;

public class CompactTalkClient extends WebServiceGatewaySupport {

	private static final Logger log = LoggerFactory.getLogger(CompactTalkClient.class);
	
	private static final String compactTalkUri = "http://localhost:20012/CommandConnection";
	private static final String soapActionBase = "http://www.weland.com/CompactTalk/ICommandConnection/";
	private static final String soapActionPollBase = "http://www.weland.com/CompactTalk/ICommandConnectionWithPolling/";

	public GetVersionResponse getVersion() {

		GetVersion request = new GetVersion();

		log.info("Requesting version");

		GetVersionResponse response = (GetVersionResponse) getWebServiceTemplate()
				.marshalSendAndReceive(compactTalkUri, request,
						new SoapActionCallback(
								soapActionBase +"GetVersion"));

		return response;
	}
	public AddToQueueResponse addToQueue(PickOrder order) {

		AddToQueue request = new AddToQueue();
		request.setArtDescr(order.getArtDescr());
		request.setArtNo(order.getArtNo());
		request.setElevatorId(order.getElevatorName());
		request.setOpening(order.getServiceOpening());
		request.setQuantity(order.getQuantity());
		request.setTransId(order.getTransId());
		request.setTray(order.getTrayNo());
		request.setOpening(order.getServiceOpening());
		request.setTrayCoord(order.getTrayCoord());
		request.setMode(order.getMode());
		request.setNoReturnOfTray(order.getNoReturnOfTray());
		request.setPriority(order.getPriority().intValue());
		request.setJob(order.getJob());
		request.setCurrentBoxName(order.getCurrentBoxName());
		request.setInfo1(order.getInfo1());
		request.setInfo2(order.getInfo2());
		request.setInfo3(order.getInfo3());
		request.setInfo4(order.getInfo4());
		request.setInfo5(order.getInfo5());


		log.info("Add order with trans ID " + order.getTransId());

		AddToQueueResponse response = (AddToQueueResponse) getWebServiceTemplate()
				.marshalSendAndReceive(compactTalkUri, request,
						new SoapActionCallback(
								soapActionBase + "AddToQueue"));

		return response;
	}
	public ActivateEventsResponse activateEvents() {

		ActivateEvents request = new ActivateEvents();

		log.info("Activate events");

		ActivateEventsResponse response = (ActivateEventsResponse) getWebServiceTemplate()
				.marshalSendAndReceive(compactTalkUri, request,
						new SoapActionCallback(
								soapActionPollBase + "ActivateEvents"));

		return response;
	}
	public PollForEventResponse pollForEvent(String token) {

		PollForEvent request = new PollForEvent();
		request.setToken(token);

		log.info("Poll for events");

		PollForEventResponse response = (PollForEventResponse) getWebServiceTemplate()
				.marshalSendAndReceive(compactTalkUri, request,
						new SoapActionCallback(
								soapActionPollBase + "PollForEvent"));

		return response;
	}
	public ExtAckOrderResponse acknowledgeOrder(String elevatorId, int opening, float quantity) {

		ObjectFactory factory = new ObjectFactory();
		
		ExtAckOrder request = new ExtAckOrder();
		request.setElevatorId(factory.createExtAckOrderElevatorId(elevatorId));
		request.setOpening(opening);
		request.setQuantity(quantity);

		log.info("Acknowledge order");

		ExtAckOrderResponse response = (ExtAckOrderResponse) getWebServiceTemplate()
				.marshalSendAndReceive(compactTalkUri, request,
						new SoapActionCallback(
								soapActionBase + "ExtAckOrder"));

		return response;
	}
}
