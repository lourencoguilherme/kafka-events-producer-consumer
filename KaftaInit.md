
C:\kafka>.\bin\windows\zookeeper-server-start.bat .\config\zookeeper.properties

C:\kafka>.\bin\windows\kafka-server-start.bat .\config\server.properties

C:\kafka>.\bin\windows\kafka-topics.bat --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic TestTopic

C:\kafka>.\bin\windows\kafka-topics.bat --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic NewTopic

C:\kafka>.\bin\windows\kafka-topics.bat --list --zookeeper localhost:2181


C:\kafka>.\bin\windows\kafka-console-producer.bat --broker-list localhost:9092 --topic TestTopic
>Teste input data
>New data
>Welcome to daily Code Buffer!
>Happy Learning!!!
>


C:\kafka>.\bin\windows\kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic TestTopic --from-beginning

kafka-topics.bat --zookeeper localhost:2181 --list

kafka-consumer-groups.bat --bootstrap-server localhost:9092 --list