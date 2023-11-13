package exercises11;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;

// Hint: You may generate random numbers using Random::ints
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.IntStream;

public class MobileApp extends AbstractBehavior<MobileApp.MobileAppCommand> {

    /* --- Messages ------------------------------------- */
    public interface MobileAppCommand { }

    public static final class NewBank implements MobileAppCommand {
        public final String name;
        public final ActorRef<Bank.BankCommand> ref;

        public NewBank(String name, ActorRef<Bank.BankCommand> ref) {
            this.name = name;
            this.ref = ref;
        }
    }

    public static final class StartTransaction implements MobileAppCommand {
        public final String from;
        public final String to;
        public final String bank;
        public final int amount;

        public StartTransaction(String from, String to, String bank, int amount) {
            this.from = from;
            this.to = to;
            this.bank = bank;
            this.amount = amount;
        }
    }

    public static final class Makepayments implements MobileAppCommand {
        public final String from;
        public final String to;
        public final String bank;

        public Makepayments(String from, String to, String bank) {
            this.from = from;
            this.to = to;
            this.bank = bank;
        }
    }

    /* --- State ---------------------------------------- */
    private final Map<String, ActorRef<Bank.BankCommand>> banks;



    /* --- Constructor ---------------------------------- */
    // Feel free to extend the contructor at your convenience
    private MobileApp(ActorContext context) {
        super(context);
        context.getLog().info("Mobile app {} started!",
                context.getSelf().path().name());
        this.banks = new HashMap<String, ActorRef<Bank.BankCommand>>();
    }


    /* --- Actor initial state -------------------------- */
    public static Behavior<MobileApp.MobileAppCommand> create() {
        return Behaviors.setup(MobileApp::new);
        // You may extend the constructor if necessary
    }


    /* --- Message handling ----------------------------- */
    @Override
    public Receive<MobileAppCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(NewBank.class, this::onCreateBank)
                .onMessage(StartTransaction.class, this::onStartTransaction)
                .onMessage(Makepayments.class, this::onMakepayments)
                .build();
    }

    /* --- Handlers ------------------------------------- */
    public Behavior<MobileAppCommand> onCreateBank(NewBank msg) {
//        getContext().getLog().info("{} Actor received new bank {}",
//                getContext().getSelf().path().name(), msg.name);
        this.banks.put(msg.name, msg.ref);
        return this;
    }

    public Behavior<MobileAppCommand> onStartTransaction(StartTransaction msg) {
//        getContext().getLog().info("{} Actor received new transaction {}",
//                getContext().getSelf().path().name(), msg);
        ActorRef<Bank.BankCommand> bank = this.banks.get(msg.bank);
        bank.tell(new Bank.Transaction(msg.from, msg.to, msg.amount));
        return this;
    }

    public Behavior<MobileAppCommand> onMakepayments(Makepayments msg) {
//        getContext().getLog().info("{} Actor received Makepayments {}",
//                getContext().getSelf().path().name(), msg);
        // make 100 transactions, amount is random
        for (int i = 0; i < 100; i++) {
            Random random = new Random();
            int amount = random.nextInt(100);
            ActorRef<Bank.BankCommand> bank = this.banks.get(msg.bank);
            bank.tell(new Bank.Transaction(msg.from, msg.to, amount));
        }

        return this;
    }
}