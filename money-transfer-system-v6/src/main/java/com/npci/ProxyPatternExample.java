package com.npci;

// Aspect
class Authrization {
    public void doAuthrization() {
        System.out.println("Authrization--👮‍♀️");
    }
}

// Aspect
class Logging {
    // Advice
    // Expression- Pointcut
    public void doLogging() {
        System.out.println("Logging--📜");
    }
}

// @Component
class Trainer {
    // Join Point
    public void getSpringBootTraining() {
        System.out.println("Spring Boot Training");
    }

    public void getSQLTraining() {
        System.out.println("SQL Training");
    }
}

class TrainerProxy {
    Trainer trainer = new Trainer(); // Target
    Authrization authrization = new Authrization(); // Aspect 1
    Logging logging = new Logging(); // Aspect 2

    public void getSpringBootTraining() {
        authrization.doAuthrization();
        logging.doLogging();
        trainer.getSpringBootTraining();
    }

    public void getSQLTraining() {
        authrization.doAuthrization();
        logging.doLogging();
        trainer.getSQLTraining();
    }
}

public class ProxyPatternExample {

    public static void main(String[] args) {

        TrainerProxy trainerProxy = new TrainerProxy();
        trainerProxy.getSpringBootTraining();
        System.out.println("-------------");
        trainerProxy.getSQLTraining();

    }

}
