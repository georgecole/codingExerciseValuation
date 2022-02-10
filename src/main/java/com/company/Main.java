package com.company;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class Main {

    /* Taking in logger as we're in the parent static class */
    public static void loopExecutionUntilSuccessOrThreshold(Logger logger, FileProcessor fp) {
         int maxRetries = 100;

        /* LEGACY Comment Infinite loop to allow quick debugging / re-running of the process
           As it's a command line app this seemed simpler,
           typically I guess I'd be hitting an endpoint via postman or have a scheduler running

           I had initially set this up to allow quick testing, however on deciding what to do if the file didn't exist, I figured we could just start again with a retry cap
           */

        int currentTry = 1;
        boolean fileLoadSuccess = false; // Default to false, break loop when true */

        while (!fileLoadSuccess && currentTry < maxRetries)
        {
            logger.info("Log File Loader Attempt: {}", currentTry);
             Integer exitCode = fp.doStuff();
            // Convert to enum ?
            // 0 = Success

             fileLoadSuccess = exitCode.equals(0);
            currentTry++;
            try {
                /* Give some leeway between executions */
                Thread.sleep(4000);
            }
            catch (InterruptedException ex)
            {
                // If the sleep fails we'll abort - this shouldn't happen and we'll err on the side of caution if it does
                // I'd initially added this as it was an infinate loop for debugging prior to repurposing
                System.exit(0);
            }
        }
    }

    public static void main(String[] args) {
        Logger logger  = LoggerFactory.getLogger("com.company.Main");
        logger.debug("Debug Mode: ON");
        logger.debug("Arguments provided: {}", Arrays.toString(args));
        logger.info("Hello world.");
        logger.info("Executing main class");

        /* Should we write everything in the main class, probably not... */
        FileProcessor mainThread = new FileProcessor(args);

        loopExecutionUntilSuccessOrThreshold(logger, mainThread);

        logger.info("Finished main class");

    }
}
