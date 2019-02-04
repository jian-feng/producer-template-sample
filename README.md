# Spring-Boot Camel ProducerTemplate inside Processor

[ProducerTemplate][1]は、Camelで用意されたインタフェースです。このインタフェースを利用することで、Javaコード([Processor][2], Bean)の中でCamelが用意された豊富な[コンポーネント][3]を簡単にインスタンス化し、外部システムと送受信できるようになります。

[1]: http://camel.apache.org/producertemplate.html
[2]: http://camel.apache.org/processor.html
[3]: http://camel.apache.org/components.html


このサンプルでは、[Processor][2]の中で、HTTPコンポーネントを使ったリクエストレスポンスの実装方法を紹介します。

## 実装方法

### 1. camel-conotext.xml

camelContext内で`<template>` (ProducerTemplateのこと)を初期化します。  
また、MyProcessorはこのtemplate、とendpointUriで初期化します。

```xml
<bean id="myprocessor" class="org.mycompany.MyProcessor">
	<property name="producer" ref="myTemplate" />
	<property name="endpointUri" value="http://inet-ip.info/ip"/>
</bean>
<camelContext id="camel" ...">
	<template id="myTemplate" />
	...
</camelContext>
```

### 2. MyProcessor.java

```java
@Override
public void process(Exchange exchange) throws Exception {
	
	// camel-contextにて初期化されたProducerTempalteを使ってリクエストレスポンスを実行します。	
	String respBody = producer.requestBody(endpointUri,"test message from myprocessor", String.class);
	LOG.info("respBody={}", respBody);
	
	// ProducerTemplateから得られたレスポンスを現在処理中のExchangeにセットします。
	exchange.getIn().setBody(respBody);
}
```

### 3. camel-conotext.xml

camelContext内のCamel Routeは以下に定義します。

```xml
<route>
	...
	<log id="log1" message="body before myprocessor >>> ${body}" />
	<process ref="myprocessor" />
	<log id="log2" message="body after myprocessor >>> ${body}" />
</route>
```

## 実行方法

	mvn spring-boot:run

このサンプルを実行すると、１０秒間隔で、MyProcessorの中から指定のendpointUri([Camelコンポーネント][3]が使われる)にアクセスし、endpointのレスポンスをログに出力します。

	[Camel (camel) thread #1 - timer://foo] INFO  simple-route - body before myprocessor >>> Hello World from camel-context.xml
	[Camel (camel) thread #1 - timer://foo] INFO  MyProcessor - respBody=xx.xx.xx.xx
	[Camel (camel) thread #1 - timer://foo] INFO  simple-route - body after myprocessor >>> xx.xx.xx.xx

- 1行目のログは、simple-routeがMyProcessor実行前Bodyの出力
- 2行目は、MyProcessorの中でendpointのレスポンスをログに出力
- 3行目は、simple-routeがMyProcessor実行後Bodyの出力
- 1 - 3は、同じスレッドで処理されていること

