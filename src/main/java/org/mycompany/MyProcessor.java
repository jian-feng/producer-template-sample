package org.mycompany;


import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyProcessor implements Processor {
	
	private static final Logger LOG = LoggerFactory.getLogger("MyProcessor");

	ProducerTemplate producer;
	String endpointUri;


	public void setProducer(ProducerTemplate producer) {
	    this.producer = producer;
	}

	public void setEndpointUri(String endpointUri) {
		this.endpointUri = endpointUri;
	}

	@Override
	public void process(Exchange exchange) throws Exception {
		
		// camel-contextにて初期化されたProducerTempalteを使ってリクエストレスポンスを実行します。	
		String respBody = producer.requestBody(endpointUri,"test message from myprocessor", String.class);
		LOG.info("respBody={}", respBody);
		
		// ProducerTemplateから得られたレスポンスを現在処理中のExchangeにセットします。
		exchange.getIn().setBody(respBody);
	}
}
