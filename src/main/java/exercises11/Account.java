package exercises11;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;

public class Account extends AbstractBehavior<Account.AccountCommand> {

    /* --- Messages ------------------------------------- */
    public interface AccountCommand { }

    public static final class Deposit implements AccountCommand {
        public final int amount;

        public Deposit(int amount) {
            this.amount = amount;
        }
    }

    public static final class PrintBalance implements AccountCommand {}


    /* --- State ---------------------------------------- */
    private int balance;


    /* --- Constructor ---------------------------------- */
    // Feel free to extend the contructor at your convenience
    private Account(ActorContext<AccountCommand> context) {
        super(context);
        this.balance = 200;
    }


    /* --- Actor initial state -------------------------- */
    public static Behavior<Account.AccountCommand> create() {
        return Behaviors.setup(Account::new);
    }


    /* --- Message handling ----------------------------- */
    @Override
    public Receive<AccountCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(Deposit.class, this::onDeposit)
                .onMessage(PrintBalance.class, this::onPrintBalance)
                // To be implemented
                .build();
    }



    /* --- Handlers ------------------------------------- */
    public Behavior<AccountCommand> onDeposit(Deposit msg) {
        int oldBalance = this.balance;
        this.balance = this.balance + msg.amount;
//        getContext().getLog().info("{} Actor received deposit {}, old balance = {}, new balance = {}",
//                getContext().getSelf().path().name(), msg.amount, oldBalance, this.balance);
        return this;
    }

    public Behavior<AccountCommand> onPrintBalance(PrintBalance msg) {
        getContext().getLog().info("{} Actor balance = {}",
                getContext().getSelf().path().name(), this.balance);
        return this;
    }

}