# RoundRobin-LoadBalancer
A load Balancer with the concept of Round Robin and consistency written in java. There are 3 servers, 1 load balancer and 2 clients implemented. The clients send requests to read or write to the load balancer. The balance keep this requests in a ArrayList and uses the round robin politic to decide wich of the 3 servers is going to answer the request. After that, it send signal to do the consistency and keep the files coherent.

## Getting Started

For the program work you have first execute the servers, then the Balancer and then the Client.

The ips are passed as arguments therefore you must execute like the exemples below:

```
java Server1 172.23.3.10
java Server2 172.23.3.10
java Server3 172.23.3.10
java Balancer 172.23.3.11 172.23.3.12 172.23.3.13
java Client 172.23.3.10
```
In wich "172.23.3.10" is the ip of the balancer and "172.23.3.11","172.23.3.12" and "172.23.3.13" are the ips of Server1, Server2 and Server3 respectively

### Expected Answer
The files SD1.txt, SD2.txt, SD3.txt will end up with the same content.
