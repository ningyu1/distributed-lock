# distributed-lock
分布式锁，默认是redis实现，可扩展接口增加zk、等其他实现

## 这个分布式锁采用redis实现，根据CAP理论保证了可用性、分区容错性、和最终一致性。

## 我们实现的分布式锁

### 特性：

#### 1. 这把锁是非阻塞锁，可以根据超时时间和重试频率来定义重试次数

#### 2. 这把锁支持失效时间，极端情况下解锁失败，到达时间之后锁会自动删除

#### 3. 这把锁是非重入锁，一个线程获得锁之后，在释放锁之前，其他线程无法再次获得锁，只能根据获取锁超时时间和重试策略进行多次尝试获取锁。

#### 4. 因为这把锁是非阻塞的，所以性能很好，支持高并发

#### 5. 使用方无需手动获取锁和释放锁，锁的控制完全由框架控制操作，避免使用方由于没有释放锁或释放锁失败导致死锁的问题

### 缺点：

#### 1. 通过超时时间来控制锁的失效时间其实并不完美，但是根据性能和CAP理论有做取舍

#### 2. 这把锁不支持阻塞，因为要达到高的性能阻塞的特性是要牺牲

## 使用步骤

### 1. 在pom中引入包依赖
``` xml
<dependency>
    <groupId>cn.tsoft.framework</groupId>
    <artifactId>distributed-lock</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### 2. 在spring.xml中引入lock配置
``` xml
<import resource="classpath:spring-lock.xml" />
```

### 3. lock使用到了redisclient，因此spring.xml也需要引用redis配置
``` xml
<import resource="classpath:spring-redis.xml" />
```

### 4. 代码中lock使用
``` java
import cn.tsoft.framework.lock.Lock;
import  cn.tsoft.framework.lock.LockCallBack;
import  cn.tsoft.framework.lock.DefaultLockCallBack;
 
@Autowired
Lock lock;
 
T t = lock.lock("Test_key_2",20,60,new LockCallBack<T>(){
    public T handleObtainLock(){
        dosomething();
    }
    public T handleNotObtainLock() throws LockCantObtainException{
        return T;//throw new LockCantObtainException();
    }
    public T handleException(LockInsideExecutedException e) throws LockInsideExecutedException{
        return T;//throw new e;
    }
});
或
T t = lock.lock("Test_key_2",LockRetryFrequncy.VERY_QUICK,20,60,new DefaultLockCallBack<T>(T,T){
    public T handleObtainLock(){
        dosomething();
    }
});
```

### 5. 锁重试策略说明
``` java
/**
 * 锁重试获取频率策略
 * 
 * @author ningyu
 *
 */
LockRetryFrequncy.VERY_QUICK;  //非常快
LockRetryFrequncy.QUICK;       //快
LockRetryFrequncy.NORMAL;      //中
LockRetryFrequncy.SLOW;        //慢
LockRetryFrequncy.VERYSLOW;    //很慢
//例如：
//以获取锁的超时时间为：1秒来计算
//VERY_QUICK的重试次数为：100次
//QUICK的重试次数为：20次
//NORMAL的重试次数为：10次
//SLOW的重试次数为：2次
//QUICK的重试次数为：1次
//这个重试策略根据自身业务来选择合适的重试策略
```

### 6. example

#### 6.1. 第一种用法

``` java
//锁名称：Test_key_2
//获取锁超时时间：20秒
//锁最大过期时间：60秒
//内部执行回调，包含（1.获取到锁回调，2.没有获取到锁回调，3.获取到锁内部执行业务代码报错）
//默认策略：NORMAL
lock.lock("Test_key_2",20,60,new LockCallBack<String>() {
   @Override
   public String handleException(LockInsideExecutedException e) throws LockInsideExecutedException {
       logger.error("获取到锁，内部执行报错");
       return "Exception";         
   }
 
   @Override
   public String handleNotObtainLock() throws LockCantObtainException {
          logger.error("没有获取到锁");
       return "NotObtainLock";
   }
 
   @Override
   public String handleObtainLock() {
       logger.info("获取到锁");
       dosomething();
       return "ok";
   }
);
```

#### 6.2. 第二种用法

``` java
//锁名称：Test_key_2
//获取锁超时时间：20秒
//锁最大过期时间：60秒
//内部执行回调，使用默认回调实现，只需要实现获取到锁后需要执行的方法，当遇到没有获取锁和获取锁内部执行错误时会返回构造函数中设置的值（支持泛型）
//默认策略：NORMAL
lock.lock("Test_key_2",20,60,new DefaultLockCallBack<String>("NotObtainLock", "Exception") {
   @Override
   public String handleObtainLock() {
       logger.info("获取到锁");
       dosomething();
       return "ok";
   }
);
```

#### 6.3. 第三种用法

``` java
//锁名称：Test_key_2
//锁重试获取频率：VERY_QUICK 非常快
//获取锁超时时间：20秒
//锁最大过期时间：60秒
//内部执行回调，包含（1.获取到锁回调，2.没有获取到锁回调，3.获取到锁内部执行业务代码报错）
lock.lock("Test_key_2",LockRetryFrequncy.VERY_QUICK,20,60,new LockCallBack<String>() {
   @Override
   public String handleException(LockInsideExecutedException e) throws LockInsideExecutedException {
       logger.error("获取到锁，内部执行报错");
       return "Exception";         
   }
 
   @Override
   public String handleNotObtainLock() throws LockCantObtainException {
          logger.error("没有获取到锁");
       return "NotObtainLock";
   }
 
   @Override
   public String handleObtainLock() {
       logger.info("获取到锁");
       dosomething();
       return "ok";
   }
);
```

#### 6.4. 第四种用法

``` java
//锁名称：Test_key_2
//锁重试获取频率：VERY_QUICK 非常快
//获取锁超时时间：20秒
//锁最大过期时间：60秒
//内部执行回调，使用默认回调实现，只需要实现获取到锁后需要执行的方法，当遇到没有获取锁和获取锁内部执行错误时会返回构造函数中设置的值（支持泛型）
lock.lock("Test_key_2",LockRetryFrequncy.VERY_QUICK,20,60,new DefaultLockCallBack<String>("NotObtainLock", "Exception") {
   @Override
   public String handleObtainLock() {
       logger.info("获取到锁");
       dosomething();
       return "ok";
   }
);
```

### 7. 注意事项

#### 7.1. 获取锁的超时时间和重试策略直接影响获取锁重试的次数，根据业务场景来定义适合的重试获取锁的频次，避免线程阻塞。

##### 7.1.1 场景：

##### 7.1.1.1. 快速响应给客户端的场景，超时时间尽量短，超时时间 < 锁后执行时间，例如：秒杀、抢购

##### 7.1.1.2. 可以容忍响应速度的场景，锁后执行时间*2 > 超时时间 >=锁后执行时间

#### 7.2. 根据业务场景来定义锁的最大过期时间，理论上业务执行越慢过期时间越大，因为是并发锁，为了杜绝因为获得锁而没有释放造成的问题

##### 7.2.1. 建议 锁后执行时间*1.5 > 锁超时时间 > 锁后执行时间，避免并发问题

#### 7.3. 获取锁后执行的代码块一定是小而快的，就像事务块使用原则一样，禁止重而长的逻辑包在里面造成其他线程获取锁失败率过高，如果逻辑很复杂需要分析那一块需要支持并发就把需要并发的代码包在里面。