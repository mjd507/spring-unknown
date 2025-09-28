
1. limit the Tomcat thread to max 10

   `server.tomcat.threads.max=10`

2. using tool [hey](https://github.com/rakyll/hey) to test api, (this api will call remote api which takes 5s to give the result).

   `hey -n 30 -c 15 http://localhost:8080/delay`

3. compare virtual thread enabled vs disable. `spring.threads.virtual.enabled=true/false`

   virtual threads enabled:
   ```txt
   Summary:
   Total:	13.2449 secs
   Slowest:	7.4430 secs
   Fastest:	5.2830 secs
   Average:	5.7918 secs
   Requests/sec:	2.2650
   
   Total data:	8820 bytes
   Size/request:	294 bytes
   ```

   disabled virtual threads
   ```txt
   Summary:
   Total:	26.2987 secs
   Slowest:	16.9940 secs
   Fastest:	5.3011 secs
   Average:	10.6159 secs
   Requests/sec:	1.1407
   
   Total data:	8820 bytes
   Size/request:	294 bytes
   ```