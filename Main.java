package com.company;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        final Main.Counter counter1 = new Counter();
        final Thread numberPrint = new Thread(new NumberPrinter(counter1), "Printer");
        final Thread numberInk = new Thread(new NumberInkrementer(counter1), "Inkrementer");

        numberPrint.start();
        numberInk.start();
    }

    private static class Counter {
        private final static int n = 1_000_000;
        private volatile int count;

        public Counter() {
            this.count = 0;
        }
    }

    private static class NumberPrinter implements Runnable {

        private final Counter counter;

        public NumberPrinter(Counter counter) {
            this.counter = counter;
        }

        public void run() {

            synchronized (counter) {

                while (counter.count != Counter.n) {

                    try {
                        //this.wait();
                        counter.wait();
                        System.out.println("Printer: " + counter.count);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private static class NumberInkrementer implements Runnable {

        private final Counter counter;

        public NumberInkrementer(Counter counter) {
            this.counter = counter;
        }

        public void run() {
            inkrement();
        }

        public void inkrement() {


            while (true) {

                synchronized (counter) {
                    // If you must ... but the counter is volatile anyway so it'll always be read fresh.
                    // Andd there's no other writers.
                    counter.count++;
                    System.out.println("Ink sleeping, counter=" + counter.count);
                    counter.notify();
                }

                if (!(counter.count < Counter.n)) {
                    break;
                }
            }
        }
    }
}
