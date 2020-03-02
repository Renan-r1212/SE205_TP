class Consumer extends Thread {
    BoundedBuffer buffer;

    Consumer (int n, BoundedBuffer buffer) {
        super("consumer " + n);
        this.buffer = buffer;
    }

    public void run() {
        Integer value;
        long deadline = Utils.startTime;

        for (int i = 0; i < (Utils.nValues/Utils.nConsumers); i++) {
            deadline = deadline + Utils.consumerPeriod;
            switch (Utils.semantics) {
            case Utils.BLOCKING :
                value = (Integer)buffer.get();
                break;
            case Utils.NONBLOCKING :
                value = (Integer)buffer.remove();
                break;
            case Utils.TIMEDOUT :
                value = (Integer)buffer.poll(deadline);
                break;
            default :
                return;
            }
            Utils.printLog
                (Utils.elapsedTime(), getName(), Utils.semantics, value);
            Utils.delayUntil (deadline);
        }
    }
}
