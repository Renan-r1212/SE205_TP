public class BoundedBufferMain {

    public static void main (String[] args) {
        BoundedBuffer buffer;

        // Check the arguments of the command line
        if (args.length != 1){
            System.out.println ("PROGRAM FILENAME");
            System.exit(1);
        }
        Utils.init(args[0]);

        // Create a buffer
        if (Utils.sem_impl == 0)
            buffer = new NatBoundedBuffer(Utils.bufferSize);
        else
            buffer = new SemBoundedBuffer(Utils.bufferSize);

        // Create producers and then consumers
        
        Producer[] p = new Producer[(int) Utils.nProducers];
        Consumer[] c = new Consumer[(int) Utils.nConsumers];

        try{
            // Initialize/run each vector Producer/Consumer objects
            for(int id = 0; id < Utils.nProducers; id++){
                p[id] = new Producer(id, buffer);
                p[id].start();
            }

            for(int id = (int) Utils.nProducers; id < (Utils.nProducers + Utils.nConsumers); id++){
                c[id - (int) Utils.nProducers] = new Consumer(id, buffer);
                c[id - (int) Utils.nProducers].start();
            }        

            // Waits for the Producer/Consumer Threads to finishs
            for(int id = 0; id < Utils.nProducers; id++){
                p[id].join();
            }

            for(int id = (int) Utils.nProducers; id < (Utils.nProducers + Utils.nConsumers); id++){
                c[id - (int) Utils.nProducers].join();  
            }
        }catch(Exception e){}
    }
}
