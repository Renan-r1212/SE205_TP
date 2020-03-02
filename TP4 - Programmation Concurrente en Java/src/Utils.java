import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

class Utils {
    static final int BLOCKING = 0;
    static final int NONBLOCKING = 1;
    static final int TIMEDOUT = 2;
    static char semImg[] = {'B', 'N', 'T'};
    
    static long startTime;

    static int  sem_impl = 0;
    static int  semantics = BLOCKING;
    static int  bufferSize = 0;
    static long nValues = 0;
    static long nConsumers = 0;
    static long nProducers = 0;
    static long consumerPeriod = 0;
    static long producerPeriod = 0;

    static long getLong(Scanner s, String key) {
        String l;
        
        while (s.hasNextLine()) {
            l = s.nextLine ();
            if (l.compareTo (key) == 0) {
                l = s.nextLine();
                return Long.parseLong (l);
            }
        }
        return 0;
    }
    
    static public void init (String n) {
        Scanner s = null;
        try {
            s = new Scanner (new BufferedReader (new FileReader (n)));
            System.out.println("open " + n);
        } catch (Exception e) {
            System.out.println ("file " + n + " not found");
            return;
        } 
        
        sem_impl = (int) getLong(s, "#sem_impl");
        System.out.println("sem_impl = " + sem_impl);
        semantics = (int) getLong(s, "#semantics");
        System.out.println("semantics = " + semantics);
        bufferSize = (int) getLong (s, "#buffer_size");
        System.out.println("buffer_size = " + bufferSize);
        nValues = (long) getLong (s, "#n_values");
        System.out.println("n_values = " + nValues);
        nConsumers = (long) getLong (s, "#n_consumers");
        System.out.println("n_consumers = " + nConsumers);
        nProducers = (long) getLong (s, "#n_producers");
        System.out.println("n_producers = " + nProducers);
        consumerPeriod = (long) getLong (s, "#consumer_period");
        System.out.println("consumer_period = " + consumerPeriod);
        producerPeriod = (long) getLong (s, "#producer_period");
        System.out.println("producer_period = " + producerPeriod);
        startTime = System.currentTimeMillis();
    }
    

    static void delayUntil (long deadline) {
        try {
            Thread.sleep (deadline - System.currentTimeMillis());
        } catch (Exception e){};
    }

    static long elapsedTime(){
        return System.currentTimeMillis() - startTime;
    }

    static void printLog(long time, String name, int kind, Integer d){
        String s = String.format("%1$06d", time);
        if (d == null) 
            System.out.println(s + " " + name + " (" + semImg[kind] +
                               ") - data=NULL");
        else
            System.out.println(s + " " + name + " (" + semImg[kind] +
                               ") - data=" + d);
    }
}
