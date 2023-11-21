In Apache Pulsar, the subscriptionInitialPosition method is used to configure the initial position for a consumer when it subscribes to a topic. The SubscriptionInitialPosition enum specifies where a newly created subscription should start consuming messages within a topic.

In the provided code:

```java
Consumer<byte[]> consumer = pulsarClient.newConsumer()
.subscriptionName("my-subscription")
.subscriptionInitialPosition(SubscriptionInitialPosition.Earliest)
.subscribe();
```
The subscriptionInitialPosition is set to SubscriptionInitialPosition.Earliest. This means that when a new subscription is created, it will start consuming messages from the earliest available message in the topic. If there are existing messages in the topic, the consumer will start consuming from the beginning of the topic.

Alternatively, you can use SubscriptionInitialPosition.Latest to start consuming messages from the latest available message in the topic. This is useful when you are only interested in new messages that are produced after the subscription is created.

```java
Consumer<byte[]> consumer = pulsarClient.newConsumer()
.subscriptionName("my-subscription")
.subscriptionInitialPosition(SubscriptionInitialPosition.Latest)
.subscribe();
```

In summary:

SubscriptionInitialPosition.Earliest: Consumer starts consuming messages from the earliest available message in the topic.
SubscriptionInitialPosition.Latest: Consumer starts consuming messages from the latest available message in the topic.
Choose the appropriate initial position based on your use case. If you need to process all messages in a topic, including those produced before the consumer was created, you would use Earliest. If you only want to process messages produced after the consumer was created, you would use Latest.