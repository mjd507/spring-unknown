
## case 1
```java
@GetMapping("/default-with-open-in-view")
public void defaultOpenInView () {
    log.info("{}", sampleService.findAllUser());
    externalService.call();
}
```
by default, `spring.jpa.open-in-view=true`. if we have transaction, the connection won't be closed unless the entire view phase completed.


## case 2 

```java
@Transactional
public void defaultAutoCommit() {
	externalService.call();
	log.info("{}", personRepository.findAll());
}

```
by default, `spring.datasource.auto-commit=true`. so whenever enter the `@Transactional` method, it acquires a connection from pool, and change the auto-commit to false, if we have a long external call, performance issue happens, since we take db conn too long without doing anything. 

so, we can optimize it with explicit transactionTemplate call.

```java
public void defaultAutoCommit2() {
	externalService.call();
    transactionTemplate.executeWithoutResult(transactionStatus -> {
        log.info("{}", personRepository.findAll());
    });
}
```

## case 3

when we have nested transactions, the outer transaction would hold until inner transaction commits, this is also a resource wasting. and explicit transactionTemplate also a good choice.

```java
public void twoTransactions() {
    transactionTemplate.executeWithoutResult(transactionStatus -> {
        log.info("{}", personRepository.findAll());
    });
    externalService.runInNewTransaction();
}
```