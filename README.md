# Envers ManyToOne NPE

This is a simple project for showcasing a NullPointerException occuring in Hibernate Envers.
A many-to-many relationship between Document and Employee is represented with an entity, with additional fields.

When a Document entity is saved, the RevisionListener kicks in. In this custom listener the AuditReader cannot create the revisioned entity.

This is an issue since Hibernate 6.0. The `org.hibernate.envers.internal.entities.mapper.relation.ToOneIdMapper` class used to skip getting the referenced entity name when it is audited:
https://github.com/hibernate/hibernate-orm/commit/b384b37f395f14f5e64c6b6d4d00e0868d62fe01#diff-3435a0e26ab386be275368ac69b447f002655fba8802e9d6d9397d5176b6af17L161

Not skipping it causes a NullPointerException, since `referencingEntityName` is null because the `data` map contains no `$type$`:

https://github.com/hibernate/hibernate-orm/commit/b384b37f395f14f5e64c6b6d4d00e0868d62fe01#diff-3435a0e26ab386be275368ac69b447f002655fba8802e9d6d9397d5176b6af17R205

```

org.springframework.orm.jpa.JpaSystemException: Unable to perform beforeTransactionCompletion callback: Cannot invoke "org.hibernate.envers.internal.entities.EntityConfiguration.getRelationDescription(String)" because "entCfg" is null
	at org.springframework.orm.jpa.vendor.HibernateJpaDialect.convertHibernateAccessException(HibernateJpaDialect.java:320) ~[spring-orm-6.0.15.jar:6.0.15]
	at org.springframework.orm.jpa.vendor.HibernateJpaDialect.translateExceptionIfPossible(HibernateJpaDialect.java:229) ~[spring-orm-6.0.15.jar:6.0.15]
	at org.springframework.orm.jpa.JpaTransactionManager.doCommit(JpaTransactionManager.java:565) ~[spring-orm-6.0.15.jar:6.0.15]
	at org.springframework.transaction.support.AbstractPlatformTransactionManager.processCommit(AbstractPlatformTransactionManager.java:743) ~[spring-tx-6.0.15.jar:6.0.15]
	at org.springframework.transaction.support.AbstractPlatformTransactionManager.commit(AbstractPlatformTransactionManager.java:711) ~[spring-tx-6.0.15.jar:6.0.15]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.commitTransactionAfterReturning(TransactionAspectSupport.java:660) ~[spring-tx-6.0.15.jar:6.0.15]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:410) ~[spring-tx-6.0.15.jar:6.0.15]
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:119) ~[spring-tx-6.0.15.jar:6.0.15]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:184) ~[spring-aop-6.0.15.jar:6.0.15]
	at org.springframework.dao.support.PersistenceExceptionTranslationInterceptor.invoke(PersistenceExceptionTranslationInterceptor.java:137) ~[spring-tx-6.0.15.jar:6.0.15]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:184) ~[spring-aop-6.0.15.jar:6.0.15]
	at org.springframework.data.jpa.repository.support.CrudMethodMetadataPostProcessor$CrudMethodMetadataPopulatingMethodInterceptor.invoke(CrudMethodMetadataPostProcessor.java:164) ~[spring-data-jpa-3.1.7.jar:3.1.7]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:184) ~[spring-aop-6.0.15.jar:6.0.15]
	at org.springframework.aop.interceptor.ExposeInvocationInterceptor.invoke(ExposeInvocationInterceptor.java:97) ~[spring-aop-6.0.15.jar:6.0.15]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:184) ~[spring-aop-6.0.15.jar:6.0.15]
	at org.springframework.aop.framework.JdkDynamicAopProxy.invoke(JdkDynamicAopProxy.java:244) ~[spring-aop-6.0.15.jar:6.0.15]
	at jdk.proxy2/jdk.proxy2.$Proxy114.save(Unknown Source) ~[na:na]
	at com.github.csaadaam.envers.manytoone.npe.EnversManyToOneNpeApplication.lambda$initDatabase$0(EnversManyToOneNpeApplication.java:57) ~[classes/:na]
	at org.springframework.boot.SpringApplication.lambda$callRunner$5(SpringApplication.java:782) ~[spring-boot-3.1.7.jar:3.1.7]
	at org.springframework.util.function.ThrowingConsumer$1.acceptWithException(ThrowingConsumer.java:83) ~[spring-core-6.0.15.jar:6.0.15]
	at org.springframework.util.function.ThrowingConsumer.accept(ThrowingConsumer.java:60) ~[spring-core-6.0.15.jar:6.0.15]
	at org.springframework.util.function.ThrowingConsumer$1.accept(ThrowingConsumer.java:88) ~[spring-core-6.0.15.jar:6.0.15]
	at org.springframework.boot.SpringApplication.callRunner(SpringApplication.java:790) ~[spring-boot-3.1.7.jar:3.1.7]
	at org.springframework.boot.SpringApplication.callRunner(SpringApplication.java:781) ~[spring-boot-3.1.7.jar:3.1.7]
	at org.springframework.boot.SpringApplication.lambda$callRunners$3(SpringApplication.java:766) ~[spring-boot-3.1.7.jar:3.1.7]
	at java.base/java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:184) ~[na:na]
	at java.base/java.util.stream.SortedOps$SizedRefSortingSink.end(SortedOps.java:357) ~[na:na]
	at java.base/java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:510) ~[na:na]
	at java.base/java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:499) ~[na:na]
	at java.base/java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:151) ~[na:na]
	at java.base/java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:174) ~[na:na]
	at java.base/java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234) ~[na:na]
	at java.base/java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:596) ~[na:na]
	at org.springframework.boot.SpringApplication.callRunners(SpringApplication.java:766) ~[spring-boot-3.1.7.jar:3.1.7]
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:331) ~[spring-boot-3.1.7.jar:3.1.7]
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:1321) ~[spring-boot-3.1.7.jar:3.1.7]
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:1310) ~[spring-boot-3.1.7.jar:3.1.7]
	at com.github.csaadaam.envers.manytoone.npe.EnversManyToOneNpeApplication.main(EnversManyToOneNpeApplication.java:23) ~[classes/:na]
Caused by: org.hibernate.HibernateException: Unable to perform beforeTransactionCompletion callback: Cannot invoke "org.hibernate.envers.internal.entities.EntityConfiguration.getRelationDescription(String)" because "entCfg" is null
	at org.hibernate.engine.spi.ActionQueue$BeforeTransactionCompletionProcessQueue.beforeTransactionCompletion(ActionQueue.java:1018) ~[hibernate-core-6.2.17.Final.jar:6.2.17.Final]
	at org.hibernate.engine.spi.ActionQueue.beforeTransactionCompletion(ActionQueue.java:548) ~[hibernate-core-6.2.17.Final.jar:6.2.17.Final]
	at org.hibernate.internal.SessionImpl.beforeTransactionCompletion(SessionImpl.java:1967) ~[hibernate-core-6.2.17.Final.jar:6.2.17.Final]
	at org.hibernate.engine.jdbc.internal.JdbcCoordinatorImpl.beforeTransactionCompletion(JdbcCoordinatorImpl.java:439) ~[hibernate-core-6.2.17.Final.jar:6.2.17.Final]
	at org.hibernate.resource.transaction.backend.jdbc.internal.JdbcResourceLocalTransactionCoordinatorImpl.beforeCompletionCallback(JdbcResourceLocalTransactionCoordinatorImpl.java:169) ~[hibernate-core-6.2.17.Final.jar:6.2.17.Final]
	at org.hibernate.resource.transaction.backend.jdbc.internal.JdbcResourceLocalTransactionCoordinatorImpl$TransactionDriverControlImpl.commit(JdbcResourceLocalTransactionCoordinatorImpl.java:267) ~[hibernate-core-6.2.17.Final.jar:6.2.17.Final]
	at org.hibernate.engine.transaction.internal.TransactionImpl.commit(TransactionImpl.java:101) ~[hibernate-core-6.2.17.Final.jar:6.2.17.Final]
	at org.springframework.orm.jpa.JpaTransactionManager.doCommit(JpaTransactionManager.java:561) ~[spring-orm-6.0.15.jar:6.0.15]
	... 35 common frames omitted
Caused by: java.lang.NullPointerException: Cannot invoke "org.hibernate.envers.internal.entities.EntityConfiguration.getRelationDescription(String)" because "entCfg" is null
	at org.hibernate.envers.internal.entities.EntitiesConfigurations.getRelationDescription(EntitiesConfigurations.java:101) ~[hibernate-envers-6.2.17.Final.jar:6.2.17.Final]
	at org.hibernate.envers.internal.entities.mapper.relation.ToOneIdMapper.isIgnoreNotFound(ToOneIdMapper.java:226) ~[hibernate-envers-6.2.17.Final.jar:6.2.17.Final]
	at org.hibernate.envers.internal.entities.mapper.relation.ToOneIdMapper.nullSafeMapToEntityFromMap(ToOneIdMapper.java:171) ~[hibernate-envers-6.2.17.Final.jar:6.2.17.Final]
	at org.hibernate.envers.internal.entities.mapper.relation.ToOneIdMapper.nullSafeMapToEntityFromMap(ToOneIdMapper.java:152) ~[hibernate-envers-6.2.17.Final.jar:6.2.17.Final]
	at org.hibernate.envers.internal.entities.mapper.relation.AbstractToOneMapper.mapToEntityFromMap(AbstractToOneMapper.java:57) ~[hibernate-envers-6.2.17.Final.jar:6.2.17.Final]
	at org.hibernate.envers.internal.entities.mapper.MultiPropertyMapper.mapToEntityFromMap(MultiPropertyMapper.java:201) ~[hibernate-envers-6.2.17.Final.jar:6.2.17.Final]
	at org.hibernate.envers.internal.entities.EntityInstantiator.createInstanceFromVersionsEntity(EntityInstantiator.java:90) ~[hibernate-envers-6.2.17.Final.jar:6.2.17.Final]
	at org.hibernate.envers.internal.entities.EntityInstantiator.addInstancesFromVersionsEntities(EntityInstantiator.java:153) ~[hibernate-envers-6.2.17.Final.jar:6.2.17.Final]
	at org.hibernate.envers.query.internal.impl.AbstractAuditQuery.applyProjections(AbstractAuditQuery.java:352) ~[hibernate-envers-6.2.17.Final.jar:6.2.17.Final]
	at org.hibernate.envers.query.internal.impl.EntitiesAtRevisionQuery.list(EntitiesAtRevisionQuery.java:139) ~[hibernate-envers-6.2.17.Final.jar:6.2.17.Final]
	at org.hibernate.envers.query.internal.impl.AbstractAuditQuery.getSingleResult(AbstractAuditQuery.java:119) ~[hibernate-envers-6.2.17.Final.jar:6.2.17.Final]
	at org.hibernate.envers.internal.reader.AuditReaderImpl.find(AuditReaderImpl.java:122) ~[hibernate-envers-6.2.17.Final.jar:6.2.17.Final]
	at org.hibernate.envers.internal.reader.AuditReaderImpl.find(AuditReaderImpl.java:95) ~[hibernate-envers-6.2.17.Final.jar:6.2.17.Final]
	at org.hibernate.envers.internal.reader.AuditReaderImpl.find(AuditReaderImpl.java:89) ~[hibernate-envers-6.2.17.Final.jar:6.2.17.Final]
	at com.github.csaadaam.envers.manytoone.npe.listener.RevisionListener.entityChanged(RevisionListener.java:20) ~[classes/:na]
	at org.hibernate.envers.internal.revisioninfo.DefaultRevisionInfoGenerator.entityChanged(DefaultRevisionInfoGenerator.java:98) ~[hibernate-envers-6.2.17.Final.jar:6.2.17.Final]
	at org.hibernate.envers.internal.synchronization.EntityChangeNotifier.entityChanged(EntityChangeNotifier.java:46) ~[hibernate-envers-6.2.17.Final.jar:6.2.17.Final]
	at org.hibernate.envers.internal.synchronization.AuditProcess.executeInSession(AuditProcess.java:126) ~[hibernate-envers-6.2.17.Final.jar:6.2.17.Final]
	at org.hibernate.envers.internal.synchronization.AuditProcess.doBeforeTransactionCompletion(AuditProcess.java:175) ~[hibernate-envers-6.2.17.Final.jar:6.2.17.Final]
	at org.hibernate.envers.internal.synchronization.AuditProcessManager$1.doBeforeTransactionCompletion(AuditProcessManager.java:47) ~[hibernate-envers-6.2.17.Final.jar:6.2.17.Final]
	at org.hibernate.engine.spi.ActionQueue$BeforeTransactionCompletionProcessQueue.beforeTransactionCompletion(ActionQueue.java:1012) ~[hibernate-core-6.2.17.Final.jar:6.2.17.Final]
	... 42 common frames omitted
```

