import java.text.SimpleDateFormat;
import java.util.Date;

// Simple Clock Application using Java Threads
public class SimpleClockApp {
    
    static class Clock {
        private String currentTime;
        private String currentDate;
        
        public void updateTime() {
            try {
                // Get current date and time
                Date now = new Date();
                
                // Format for time: HH:mm:ss
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                currentTime = timeFormat.format(now);
                
                // Format for date: dd-MM-yyyy
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                currentDate = dateFormat.format(now);
                
            } catch (Exception e) {
                System.out.println("Error updating time: " + e.getMessage());
            }
        }
        
        public String getDisplayString() {
            return currentTime + " " + currentDate;
        }
    }
    
    //Thread for updating time in background
    static class TimeUpdaterThread extends Thread {
        private Clock clock;
        private boolean running = true;
        
        public TimeUpdaterThread(Clock clock) {
            this.clock = clock;
        }
        
        @Override
        public void run() {
            System.out.println("Time Updater Thread started (Priority: " + getPriority() + ")");
            
            while (running) {
                try {
                    // Update time every second
                    clock.updateTime();
                    Thread.sleep(1000); // Wait 1 second
                    
                } catch (InterruptedException e) {
                    System.out.println("Time updater thread interrupted");
                    running = false;
                } catch (Exception e) {
                    System.out.println("Error in time updater: " + e.getMessage());
                }
            }
        }
        
        public void stopUpdating() {
            running = false;
        }
    }
    
    // Thread for displaying time
    static class TimeDisplayThread extends Thread {
        private Clock clock;
        private boolean running = true;
        private int displayCount = 0;
        private final int MAX_DISPLAYS = 10; // Show 10 updates then stop
        
        public TimeDisplayThread(Clock clock) {
            this.clock = clock;
        }
        
        @Override
        public void run() {
            System.out.println("Time Display Thread started (Priority: " + getPriority() + ")");
            System.out.println("=============================================================");
            
            while (running && displayCount < MAX_DISPLAYS) {
                try {
                    // Display current time
                    System.out.println("Current Time: " + clock.getDisplayString());
                    displayCount++;
                    
                    // Wait before next display
                    Thread.sleep(1000); // Update every second
                    
                } catch (InterruptedException e) {
                    System.out.println("Display thread interrupted");
                    running = false;
                } catch (Exception e) {
                    System.out.println("Error displaying time: " + e.getMessage());
                }
            }
            
            System.out.println("=============================================================");
            System.out.println("Clock display completed. Total updates shown: " + displayCount);
        }
        
        public void stopDisplaying() {
            running = false;
        }
    }
    
    // Main method - entry point of the program
    public static void main(String[] args) {
        System.out.println("Starting Simple Clock Application");
        System.out.println("==================================================================");
        
        try {
            // Create clock object
            Clock myClock = new Clock();
            
            // Create threads
            TimeUpdaterThread updater = new TimeUpdaterThread(myClock);
            TimeDisplayThread display = new TimeDisplayThread(myClock);
            
            // Set thread priorities
            display.setPriority(Thread.MAX_PRIORITY);     // Priority 10
            updater.setPriority(Thread.MIN_PRIORITY + 1); // Priority 2
            
            // Display thread info before starting
            System.out.println("Thread Configuration:");
            System.out.println("- Display Thread Priority: " + display.getPriority());
            System.out.println("- Updater Thread Priority: " + updater.getPriority());
            System.out.println();
            
            // Start the threads
            updater.start();
            display.start();
            
            // Wait for display thread to complete
            display.join();
            
            // Stop the updater thread
            updater.stopUpdating();
            updater.interrupt();
            
            // Wait for updater to finish
            updater.join(2000); // Wait up to 2 seconds
            
            System.out.println("==========================================================");
            System.out.println("Clock application stopped successfully");
            
        } catch (Exception e) {
            System.out.println("Error in main method: " + e.getMessage());
            e.printStackTrace();
        }
    }
}