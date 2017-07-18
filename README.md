### my_queue
#### storage
using memory mapped file to storage message, theoretically a message queue could be unbounded. Using a lock(spin or re-entrant) to ensure thread-safe when appending new message. Reading is thread-safe.
 
#### MessageHandler
1. return ```CompletableFuture\<Integer\>``` when receiving a new message, so the clint is non-blocking, and could get the offset of the message.
2. withing a in memory index, the look up is O(1).

#### ChannelListener
1. using ```ConcurrentLinkedQueue``` as a buffer between the message receiver thread and the thread extracting message from message queue to the out put queue.
2. using ```ArrayBlockingQueue``` with size of 500 as the out put queue, when the array is full, the thread extracting message will block.

#### demo
see ```Demo.class```

#### todo
1. recover message queue after crash from the data files.
2. split a data file to multiple data files
#### dependency
```logback```,```slf4j```

