This page was used during development of record counts code.

## Requirements ##

Dave in email on 2011-03-31

> I guess I have an initial comment to start:

> We are counting active, updates, and deletes, but I believe there are at least four categories - perhaps we should count the following kinds of arriving records (I am assuming that active is defined as a record that is not marked-deleted):

> `1`) new record (active)<br />
> 2) new record (marked-deleted)<br />
> 3) updated record (active)<br />
> 4) updated record (marked-deleted)

> We could also count some additional categories that would also look at what the updated records are replacing (two of the above cases) :

> 3a) updated (active replacing an existing active)<br />
> 3b) updated (active replacing an existing marked-deleted)<br />
> 4a) updated (marked-deleted replacing an existing active)<br />
> 4b) updated (marked-deleted replacing an existing marked-deleted)<br />

**additional facets**
  * num output records ?
  * status (default to active only)
  * unknown processing errors

<br />
## Operation ##
### after harvest step completes ###
**external repository's completeListSize for this run**: 5,800,000 records

**records added to repository this run**
(stored in a structured db table)
| **set-name** | **active records** | **updates** | **deleted records** |
|:-------------|:-------------------|:------------|:--------------------|
|              | 5,000,000          | 5,000       | 50                  |
| bib          | 2,500,000          | 2,500       | 25                  |
| hold         | 2,500,000          | 2,500       | 25                  |

**column definitions**

| **active records** | all incoming records not marked as deleted |
|:-------------------|:-------------------------------------------|
| **updates**        | incoming records that _have_ previously been harvested from this provider |
| **deleted records** | incoming records that have been marked as deleted (if a record is active, then deleted, then active, then deleted, this counter will be incremented twice |

**total records in this repository**
(queried from live db tables and archived in a structured db summation table)
| **set-name** | **active records** | **updates** | **deleted records** |
|:-------------|:-------------------|:------------|:--------------------|
|              | 5,000,000          | 5,000       | 50                  |
| bib          | 2,500,000          | 2,500       | 25                  |
| hold         | 2,500,000          | 2,500       | 25                  |

**column definitions**

| **active records** | all records available for harvest |
|:-------------------|:----------------------------------|
| **updates**        | # of record updates minus # of records |
| **deleted records** | # of records marked deleted       |

<br />
### after service completes ###

**records received this run**
| **type** | **active records** | **updates** | **deleted records** |
|:---------|:-------------------|:------------|:--------------------|
|          | 5,000,000          | 5,000       | 50                  |
| bib      | 2,500,000          | 2,500       | 25                  |
| hold     | 2,500,000          | 2,500       | 25                  |

**column definitions**

| **active records** | all incoming records not marked as deleted |
|:-------------------|:-------------------------------------------|
| **updates**        | incoming records that _have_ previously been harvested from this provider |
| **deleted records** | incoming records that have been marked as deleted (if a record is active, then deleted, then active, then deleted, this counter will be incremented twice |

**records added to repository this run**
| **service name** | **type** | **active records** | **held records** | **deleted records**  | **updates** |
|:-----------------|:---------|:-------------------|:-----------------|:---------------------|:------------|
| marcnormalization |          | 5,000,000          | 5,000            | 5,000                | 50          |
| marcnormalization | bib      | 2,500,000          | 2,500            | 25                   | 10          |
| marcnormalization | hold     | 2,500,000          | 2,500            | 25                   | 0           |

**total records in repository**

| **service name** | **type** | **active records** | **updates** | **deleted records** | **unknown processing errors**  |
|:-----------------|:---------|:-------------------|:------------|:--------------------|:-------------------------------|
| marcnormalization |          | 5,000,000          | 5,000       | 50                  | 10                             |
| marcnormalization | bib      | 2,500,000          | 2,500       | 25                  | 10                             |
| marcnormalization | hold     | 2,500,000          | 2,500       | 25                  | 0                              |

**column definitions**
| **active records** | the # of (A)ctive records in the live repo |
|:-------------------|:-------------------------------------------|
| **updated records** | the # of updates to records beyond the initial insert |
| **deleted records** | the # of (D)eleted records in the live repo |

<br />
### after solr index completes for a specific repo ###

**records received this run**
| **set** | **type** | **active records** | **deleted records** |
|:--------|:---------|:-------------------|:--------------------|
|         |          | 5,000,000          | 5,000               |
|         | bib      | 2,500,000          | 2,500               |
|         | hold     | 2,500,000          | 2,500               |

**records added to solr this run**
| **set** | **type** | **active records** | **deleted records** |
|:--------|:---------|:-------------------|:--------------------|
|         |          | 5,000,000          | 5,000               |
|         | bib      | 2,500,000          | 2,500               |
|         | hold     | 2,500,000          | 2,500               |

**total records in solr (this repository only)**
| **set** | **type** | **active records** | **deleted records** |
|:--------|:---------|:-------------------|:--------------------|
|         |          | 5,000,000          | 5,000               |
|         | bib      | 2,500,000          | 2,500               |
|         | hold     | 2,500,000          | 2,500               |

<br />
### structured db table ###

| repo name | incoming/outgoing | incremental/total | date | set | type | active | update | deleted | held |
|:----------|:------------------|:------------------|:-----|:----|:-----|:-------|:-------|:--------|:-----|

Example:
```
$ mysql -u root --password=root -D xc_marcaggregation -e 'describe incoming_record_counts'
+--------------------------+-------------+------+-----+---------+----------------+
| Field                    | Type        | Null | Key | Default | Extra          |
+--------------------------+-------------+------+-----+---------+----------------+
| incoming_record_count_id | int(11)     | NO   | PRI | NULL    | auto_increment |
| harvest_start_date       | datetime    | NO   | MUL | NULL    |                |
| type_name                | varchar(35) | NO   | MUL | NULL    |                |
| new_act_cnt              | int(11)     | NO   |     | 0       |                |
| new_del_cnt              | int(11)     | NO   |     | 0       |                |
| upd_act_cnt              | int(11)     | NO   |     | 0       |                |
| upd_del_cnt              | int(11)     | NO   |     | 0       |                |
| upd_act_prev_act_cnt     | int(11)     | NO   |     | 0       |                |
| upd_act_prev_del_cnt     | int(11)     | NO   |     | 0       |                |
| upd_del_prev_act_cnt     | int(11)     | NO   |     | 0       |                |
| upd_del_prev_del_cnt     | int(11)     | NO   |     | 0       |                |
| unexpected_error_cnt     | int(11)     | NO   |     | 0       |                |
+--------------------------+-------------+------+-----+---------+----------------+
```

## Rules ##
  * there's a hook after each service runs to check rules about the record counts
  * at the end of service processing, this method gets called:  processServiceRecordCounts((RecordCounts mostRecentIncomingRecordCounts), at the end of this method, applyRulesToRecordCounts(RecordCounts mostRecentIncomingRecordCounts) gets called.  The individual services need to subclass applyRulesToRecordCounts(RecordCounts mostRecentIncomingRecordCounts) and check that the record counts are as expected by applying any pertinent rules.