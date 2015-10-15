## I need persistent storage for my service ##
You're in luck.  MST uses MySQL for persistent storage and we provide hooks for you to create, alter, and delete database tables when your service is installed, updated, or deleted.  Your custom tables will be contained in its own database/schema, so make sure your database user has the proper privileges to create and drop schemas.

  * There are 3 files that will be run automatically when you install, update, or delete a service in the MST.
    * install.sql
    * update.{version}.sql
      * This could actually be more than one file because your customers might not all upgrade for each version you release.  When a service is updated, the MST will run each update script from the previous version (exclusive) to the version being upgraded to (inclusive).  For example
        * previous version: 1.0.1
        * version being upgraded to: 1.0.4
        * the mst will run the scripts (not failing if they don't exist)
          * update.1.0.2.sql
          * update.1.0.3.sql
          * update.1.0.4.sql
    * delete.sql
      * since your database tables, procedures, functions are all contained within a schema, the MST will just drop that schema.  So it's not necessary for you to explicitly drop your tables.  However, you might have a reason to do some cleanup processing for whatever reason.

## Dependency Injection ##
Your custom developer environment is already configured with spring.  This means that many utilities are automatically injected into your MetadataService implementation.  If your service needs custom database tables, we suggest you follow the [DAO pattern](http://en.wikipedia.org/wiki/Data_access_object).  We've made it easy for you to follow this pattern by adhering to [convention over configuration](http://en.wikipedia.org/wiki/Convention_over_configuration).  The diagram below shows the pattern we follow for coordinating our classes.

![http://xcmetadataservicestoolkit.googlecode.com/svn/trunk/docs/manager_service_dao.png](http://xcmetadataservicestoolkit.googlecode.com/svn/trunk/docs/manager_service_dao.png)

What does this mean to me?
  * **note: Despite having "service" in its name, classes which extend GenericMetadataService are actually managers (extends [BaseManager](http://code.google.com/p/xcmetadataservicestoolkit/source/browse/trunk/mst-common/src/java/xc/mst/manager/BaseManager.java)) according to the above diagram.  When this section speaks of services, it is referring to the service pattern in the above diagram (not an implementation of MetadataService).**
  * If you're doing any database code, you'll want to do that in 1 or more DAOs.  You may also optionally want to have 1 or more Service classes to coordinate your DAOs.  Service classes provide an additional layer between your MetadataService class and your DAOs.
  * To follow our DAO pattern
    * Your class must extend [GenericMetadataServiceDAO](http://code.google.com/p/xcmetadataservicestoolkit/source/browse/trunk/mst-service/impl/src/java/xc/mst/services/impl/dao/GenericMetadataServiceDAO.java)
    * you must add a setter method to the class you wish to inject your DAO into.  This can either be your MetadataService (manager) class or your own service classes.  eg
```
public class TransformationService extends GenericMetadataService {

    protected HeldHoldingRecordDAO heldHoldingRecordDAO = null;

    public void setHeldHoldingRecordDAO(HeldHoldingRecordDAO heldHoldingRecordDAO) {
        this.heldHoldingRecordDAO = heldHoldingRecordDAO;
    }
...
}
```
  * To follow our Service pattern
    * Your class must be part of our classpath structure, i.e. xc.mst.services
    * Your class must extend [GenericMetadataServiceService](http://code.google.com/p/xcmetadataservicestoolkit/source/browse/trunk/mst-service/impl/src/java/xc/mst/services/impl/service/GenericMetadataServiceService.java)
    * you must add a setter method to the class you wish to inject your Service into.  This would be in your MetadataService (manager) class.  eg
```
public class TransformationService extends GenericMetadataService {

    protected MyTotallyAwesomeService myTotallyAwesomeService= null;

    public void setMyTotallyAwesomeService (MyTotallyAwesomeService myTotallyAwesomeService) {
        this.myTotallyAwesomeService= myTotallyAwesomeService;
    }
...
}
```

But what benefit does this have?

Without any configuration (beyond the above instructions), you'll have access to a [spring JdbcTemplate](http://static.springsource.org/spring/docs/3.0.x/spring-framework-reference/html/jdbc.html#jdbc-JdbcTemplate) which makes it super easy to do database operations.  eg
```
String lastName = this.jdbcTemplate.queryForObject(
        "select last_name from t_actor where id = ?", 
        new Object[]{1212L}, String.class);

this.jdbcTemplate.update(
        "update t_actor set = ? where id = ?", 
        "Banjo", 5276L);
```