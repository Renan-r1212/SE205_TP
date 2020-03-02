class Producer extends Thread {
    BoundedBuffer buffer;
    int id;

    Producer (int n, BoundedBuffer buffer) {
        super("producer " + n);
        this.id     = n;
        this.buffer = buffer;
    }

    public void run() {
        Integer value;
        boolean done;
        long deadline = Utils.startTime;

        for (int i = 0; i < (Utils.nValues/Utils.nProducers); i++) {
            value = new Integer (id);
            deadline = deadline + Utils.producerPeriod;
            switch (Utils.semantics) {
            case Utils.BLOCKING :
                done = buffer.put(value);
                break;
            case Utils.NONBLOCKING :
                done = buffer.add(value);
                break;
            case Utils.TIMEDOUT :
                done = buffer.offer(value, deadline);
                break;
            default :
                return;
            }
            if (!done) value=null;
            Utils.printLog
                (Utils.elapsedTime(), getName(), Utils.semantics, value);
            Utils.delayUntil (deadline);
        }
    }
}

