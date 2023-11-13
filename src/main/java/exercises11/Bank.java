package exercises11;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;

import java.util.HashMap;
import java.util.Map;

public class Bank extends AbstractBehavior<Bank.BankCommand> {

    /* --- Messages ------------------------------------- */
    public interface BankCommand { }

    public static final class NewAccount implements BankCommand {
        public final String name;
        public final ActorRef<Account.AccountCommand> ref;

        public NewAccount(String name, ActorRef<Account.AccountCommand> ref) {
            this.name = name;
            this.ref = ref;
        }
    }

    public static final class Transaction implements BankCommand {
        public final String from;
        public final String to;
        public final int amount;

        public Transaction(String from, String to, int amount) {
            this.from = from;
            this.to = to;
            this.amount = amount;
        }
    }


    /* --- State ---------------------------------------- */
    private final Map<String, ActorRef<Account.AccountCommand>> accounts;

    /* --- Constructor ---------------------------------- */
    // Feel free to extend the contructor at your convenience
    private Bank(ActorContext<BankCommand> context) {
        super(context);
        this.accounts = new HashMap<String, ActorRef<Account.AccountCommand>>();
    }


    /* --- Actor initial state -------------------------- */
    // To be Implemented
    public static Behavior<Bank.BankCommand> create() {
        return Behaviors.setup(Bank::new);
        // You may extend the constructor if necessary
    }


    /* --- Message handling ----------------------------- */
    @Override
    public Receive<BankCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(NewAccount.class, this::onCreateAccount)
                .onMessage(Transaction.class, this::onTransaction)
                .build();
    }


    /* --- Handlers ------------------------------------- */

    public Behavior<BankCommand> onCreateAccount(NewAccount msg) {
//        getContext().getLog().info("{} Actor add new account {}",
//                getContext().getSelf().path().name(), msg.name);
        this.accounts.put(msg.name, msg.ref);
        return this;
    }

    public Behavior<BankCommand> onTransaction(Transaction msg) {
        getContext().getLog().info("{} Actor received transaction from {} to {} amount {}",
                getContext().getSelf().path().name(), msg.from, msg.to, msg.amount);
        ActorRef<Account.AccountCommand> from = this.accounts.get(msg.from);
        ActorRef<Account.AccountCommand> to = this.accounts.get(msg.to);
        from.tell(new Account.Deposit(-msg.amount));
        to.tell(new Account.Deposit(msg.amount));
        return this;
    }
}