# codingExerciseValuation
A simple attempt at a java coding excercise which could be utilised to review a candidates approach to a task

The Task 
I've purposely not included the question itself within this repo, to avoid it appearing should anyone be searching for an answer to the same question.

To summarise my understanding of the task:
* A JSON data object should be parsed by a Java application
* The JSON data represents sample log events, each event had an ID and contains up to 2 messages (Event Started, Event Ended)
* The timestamp is stored as EPOCH to milliseconds
* The Java app should parse the contents, storing any event that took more than 4ms to complete inside a file-based HSQLDB (Created the table schema as required)
* There's a few bonus points on offer for performance and scalability considerations alongside code coverage 

## Initial assessment of the question
As with most coding assessments, the obvious real-world solution differs greatly from what has been requested.

Given the input and expected output, loading the logs straight into an product such as Splunk would allow for an out-of-the-box solution which allowed querying or monitoring (even graphical representation) or data points such as events exceeding 4ms and would indeed handle file sizes over in GBs, whilst also allowing automated incremental loading of new logs.

Albeit this example is being shared as part of a coding excercise, so the contexct and suitability of the solution can largely being ignored.

There task specifically requests that an in-memory database is used, presumably for the following reasons:
* To demonstrate a potentail candidates knowledge of relational databases (both in schema creation, structured queries and interfacing to it from java)
* The in-memory database specified would allows for easy provisioning and testing of a solution, without incurring any cost

The task itself specifically determines that a flag should be set based on the duration exceeding 4ms, the ordering of the instructions implies this determination/logic should be made at the java level, prior to storing the value inside the database inside an 'Alert' column.

If this was to become a productionised application, I would express concerns that this extremely limits usability, and doesn't effectively utilise the relational database.

Even if configured as a updatable variable, the 4ms value cannot re-applied to already processed data without additional processing, it is also an extremely generic value, given we're storing the additional attributes host and type a more re-usable solution might make use of 3rd normal form to and database joins to:
* Store acceptable thresholds based on log type in a new table
* Set the 'Alert' attribute utilising a SQL update with a join to our new thresholds reference table, allowing for the optional re-processing of historic records following a threshold update 
* Alternatively, utilise a database view to dynamically calculate the Alert attribute based on the current values in the threshold reference table
* The alert attribute could continue to be stored alongside the base data, or stored in its own results table, which might make re-processing cleaner

At this point it seems clear what the question is aiming to determine and assess, though reviewing the question thus far has identified a key consideration:
Each row within the database should be derived from two rows (json items) inside the log file, one providing the start time and one providing the end time.
Presumably in an ideal scenario every event would have two rows, however in the real world it's plausible that a crashed application or server could result in a single started row, let's call that an orpahaned log entry. However the task itself states 'Every event has 2 entries in the file' - I guess this makes life alot easier

Had it not, it was going to cause us problems if we didn't carefully consider it in the design, specifically with regards to WHEN Java should write to the database. However that remains an important consideration with scalability (e.g Program that can handle very large files (gigabytes)). You also need to consider within the Schema design whether start and end times are required / nullable, along with duration. I.E can we insert a row if half the data is missing

By assuming that every event has 2 entries, you could loop the entire file grouping the start and end entries into some form of hashmap, using the event id as the key.
The task highlighted that the start and end rows could be seperated, even allowing the end time to be before the start time in the logs. So you'd have to consider that the first half of event could be waiting a long time until it finds it's other half.


There seems to be 3 obvious options for when to write an event to the database, presuming you loop the data in the log file one row at a time (let's not worry about the multithreading requirement just yet (if at all, at this point it feels like overkill / stretching to include extra stuff in the task)
1. At the end - This is an easy option, everything has two entries so at the end we'll have everything coupled up, we then add it to the database
2. Once a pair is found - We incremently add the event to the database once we have a start and and end time
3. Immediately - We add or update the values into the database utilising the Alert ID as a primary key, Database syntax to do this is going to be platform specific, ON DUPLIATE KEY UPDATE in Mysql vs writing something more trivial in MSSQL checking if the key exists, assessing the row count of the update or catching a failed insert due to primary key existing.

There's pros and cons to each of these, and the task itself doesn't explicitly specify where attention should be focussed, key considerations include:
* Matching events within Java is likely to utilise memory to hold the hashmap / data, this will grow exponentially with the size of the file / number of log entries. In the real world this could exceed available memory or result in un-neccessary costs (where memory on a cloud environmennt was just increased and increased to cater for it). It also stresses the importance of resilience
* The longer we leave it until we write to the database, the more dependance we place on our code working and application not falling over inside a later loop iteration, the same considerations apply for how we're writing to the database (a bulk commit could fail due to a single row, preventing other good rows being written)
* Excessive database I/O operations - Commiting more frequently will place move overhead on the database, excessive table writes here might cause contention with other processes which want to consume it, or indeed this process itself if we go down the multi-threading route. As with all solutions the non-technical requirements should be explored to ensure we're not over-engineering.
* Data integrity - This scenario is based on accepting a file and loading that data into a file based database - it's unclear what the expectation would be around data retention. If you were to assume that the same solution and database were to be used daily, to incrementally load new logs - Then the application would need to consider how it would roll-back a data load which failed half way through processing, to allow the re-load of that file without duplication. This would be easier to manage with option 1, though could be achieved in option 2 and 3 through either the use of transcations or tagging each row with an upload ID for manual deletion in a cache block.

Most of these coniderations are around understanding which parts of the processing would be more effectively performed by the Application vs the Database. The utilisation of HSQLDB on the same machine running the code would likely yield different results to using dedicated database / application deployments.
For the purpose of similicity, I would assume that even an implementation of HSQLDB would allow for the more effective processing of large amounts of data using option 2/3, given that it can utilise file based disc storage as well as memory, This would allow the file to be 'streamed' into the database rather than operated on entirely in memory, which is what would often cause an out of memory exception when processing large volumes of data.

At this point I've spent half the 2 hour recommended time limit thinking through a solution, I wouldn't expect most software development candidates to do that. 
That said if I were hiring for a more senior position such as an Engineering Team Lead, I would care more around their assessment of problems than how they name their variables.

That said, I've just installed IntelliJ on my Macbook, let's see how it fares...
