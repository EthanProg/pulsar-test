# pulsar-test
Pulsar function test and practice
## Set up standalone Pulsar
### Deploy pulsar standalone:
[Pulsar deployment](https://pulsar.apache.org/docs/3.1.x/getting-started-standalone/)
### Open the transaction feature:
```
//mandatory configuration, used to enable transaction coordinator
transactionCoordinatorEnabled=true

//mandatory configuration, used to create systemTopic used for transaction buffer snapshot
systemTopicEnabled=true
```
### Create a topic:
```bash
bin/pulsar-admin topics create persistent://public/default/my-topic
bin/pulsar-admin topics create persistent://public/default/my-topic-1
bin/pulsar-admin topics create persistent://public/default/my-topic-2
bin/pulsar-admin topics create persistent://public/default/my-topic-3

bin/pulsar-admin topics create persistent://public/default/topic1
bin/pulsar-admin topics create persistent://public/default/acc-topic1
bin/pulsar-admin topics create persistent://public/default/acc-topic2
bin/pulsar-admin topics create persistent://public/default/topic-schema
bin/pulsar-admin topics create persistent://public/default/topic-schema-avro

bin/pulsar-admin topics create persistent://public/default/topic-schema-2-1
```

### Check the messages:
```bash
bin/pulsar-client consume my-topic-2 -s 'my-subscription' -p Earliest -n 0
```


## ~~Use VP standalone Pulsar~~
### Create the nodeport:
```yaml
apiVersion: v1 
kind: Service
metadata:
  name: pulsar-broker-nodeport
  namespace: lfp-pulsar
spec:
  ports:
  - name: pulsar-nodeport
    nodePort: 30363
    port: 6651
    protocol: TCP
    targetPort: 6650
  - name: http
    nodePort: 30207
    port: 8081
    protocol: TCP
    targetPort: 8080
  selector:
    lfp.app.name: pulsar-standalone
  type: NodePort
```
### Create a topic:
```bash
    curl -sL -X PUT "http://10.14.0.208:30207/admin/v2/tenants/test1" -H "Content-Type: application/json" --data-raw  "{\"allowedClusters\": [\"standalone\"]}"
curl -sL -X PUT "http://10.14.0.208:30207/admin/v2/namespaces/test1/test-namespace" -H "Content-Type: application/json" --data-raw "{
    \"retention_policies\": {\"retentionTimeInMinutes\": 1440, \"retentionSizeInMB\": 1024},
    \"subscription_expiration_time_minutes\": 43200,
    \"message_ttl_in_seconds\": 86400
  }"
```


