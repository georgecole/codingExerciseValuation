package com.company;

import ch.qos.logback.core.util.StringCollectionUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
//import java.util.Scanner;

public class FileProcessor {

    Logger logger;
    String[] commandLineArguments;

    public FileProcessor(String[] args) {
        /* Main  Constructor - Called by Main.java
        * Initalizes logging
        * Sets local variable of args so this class can access it
        */
        logger = LoggerFactory.getLogger("com.company.FileProcessor");
        commandLineArguments = args;
    }

    public Integer doStuff() {
        logger.info("Executing doStuff");

        //     Take the path to logfile.txt as an input argument
        String filePath = determineFilePath();
        logger.debug("variable filePath set by determineFilePath to {}", filePath);
        logger.info("Validating File Path {}", filePath);
        File file = getFile(filePath);
        if(file.exists() && !file.isDirectory()) {
            logger.debug("file size {}", file.getTotalSpace());
        }
        else {
            logger.warn("File was not found at path: {}", filePath);
            return 1;
        }
            // Parse the contents of logfile.txt
        List<LogEntry> logEntriesFromFile = parseFile(file);
        // Flag any long events that take longer than 4ms
        flagEvents();
        // Write the found event details to file-based HSQLDB (http://hsqldb.org/) in the working folder
        // TODO loop events ?
        Boolean successfulWrite = writeEventToDb();
        // TODO What if failed?

        return 0;
    }

    /* https://www.w3schools.com/java/java_files_read.asp
        https://stackoverflow.com/questions/1816673/how-do-i-check-if-a-file-exists-in-java
     */

    private String determineFilePath() {
        /* I've got limited experience in command line apps, and utilising custom user provided args in the main processing of the app
        The obvious place to grab them is directly in the Main class as they're already there as String[] args - however this would need me to pass them down to this class, and any other that needed them

        Java doesn't allow for named arguments, instead we're going to have to rely on ordering, or an assumption that the user is going to pass a single arg that will be the path?
        As this is a coding exercise, they'll probably do that - but it also seems like the first obvious way of breaking the solution.

        A quick google around options here and there's a suggestion to use Java 'Options' via the -D flag - this would allow us access to the filepath via
        a named attribute providing they started the application with -Dfilepath=c:/etc/file.extension
        As we're expecting a single argument, ordered args will lead to an easier customer experience starting the app
        For parsing, we could use the Apache Commons CLI or a number of other libraries to perform some pre-built validation on the input params,
        but that's probably overkill as we're expecting a single value

        Just for the sake of writing some code, we'll check there's something there and use it - failing that we'll prompt for it

        Further considerations in the real world should probably be given the security aspect alongside usability.
        Are we using relative or full path, by default the code will likely handle both -
        But that's only really applicable if we take the user input as face value without any sanitisation
        There's also file/folder permissions - In a real world example you'd have to consider what folders the user/account running the application had access to, which
        could (and probably should) be different to that of the interfacing user.

        Given the 2 hour expected time length of this exercise - we'll keep this simple, the user is going to give a path - we'll check there's a file there we can access, and proceed if that's the case.

         */


        /*
        Basic input Validation - Args should have at least one argument - we'll use the first one, it shouldn't be empty - it would be safer to use a library / sanitise it
        */

        String filePath = null; /* Initialising as null as that's being handled in isEmptyString */

        if (commandLineArguments.length > 0)
        {
            filePath = commandLineArguments[0]; // Take first element
        }
        while (HelperFunctions.isEmptyString(filePath))
        {
            // The person testing for whatever reason didn't give us a path when executing
            logger.warn("File Path was not detected");
            /* Initially attempted to use Scanner class usage in preferance of BufferedReader however given up as it's not playing ball https://www.geeksforgeeks.org/ways-to-read-input-from-console-in-java/ */
            //Scanner in = new Scanner(System.in);

            /*  Buffered reader implementation - get user input for path */
            BufferedReader reader = new BufferedReader(
            new InputStreamReader(System.in));

            try {
                System.out.println("Please enter file path:");
                try {
                    System.out.println("HINT: Your  OS Home Path is " + System.getProperty("user.home"));
                }
                catch (Exception e)
                {
                    /* This is reference only - we can safely ignore it if it fails with no impact
                    Adding the catch as I've not ran this on anything other than a mac (which for reference returns /Users/georgecole) */
                }
                // String s = in.nextLine();
                //in.close();
                filePath = reader.readLine();
            }
            catch (Exception e)
            {
                logger.error("Exception occured getting user input (not really part of requirement ", e);
                System.out.println("Something went wrong whilst getting filepath - please retry");
            }
            finally {
                /* Legacy Comment for Scanner implementation - Not used Scanner before but been caught out by not ensuring connections are closed before */
                /* Legacy Comment for Scanner implementation - Closing scanner here broke everything when it hit the catch block - presumably it then tried using it whilst it was closed */
//                in.close();
            }


        }

        return filePath;
    }

    private File getFile(String filePath) {
        File userSpecifiedFile = new File(filePath);
        return userSpecifiedFile;
    }

    private List<LogEntry> parseFile(File file) {
        ObjectMapper objectMapper = new ObjectMapper();
        List<LogEntry> listCar = new ArrayList<LogEntry>();
        try {
            //LogEntry example = objectMapper.readValue(file, LogEntry.class);
           listCar = objectMapper.readValue(file, new TypeReference<List<LogEntry>>(){});
        }
        catch (IOException ex)
        {
            logger.error("parseFile - Failed to read file ", ex);
        }
        return listCar;
    }

    private void flagEvents() {
  
    }

    private Boolean writeEventToDb() {
        // The application should create a new table if necessary and store the following values:
// Event id
// Event duration
// Type and Host if applicable
// Alert (true if the event took longer than 4ms, otherwise false)

        return true;
    }
}
