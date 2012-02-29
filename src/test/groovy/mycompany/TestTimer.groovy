package mycompany

/**
 * @author berinle
 */
class TestTimer {
    private long startTime;
    private String message;

    /**
     * Initiate a timer
     */
    public TestTimer(String message) {
        startTime = System.currentTimeMillis();
        this.message = message;
    }

    /**
     * Reset the timer for another timing session.
     *
     */
    public void reset() {
        startTime = System.currentTimeMillis();
    }

    /**
     * End the timing session and output the results.
     */
    public void done() {

        println(message
                + " : "
                + (System.currentTimeMillis() - startTime)
                + " ms.");
    }
}
