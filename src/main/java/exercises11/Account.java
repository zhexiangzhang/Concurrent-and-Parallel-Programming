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

    public static final class ConditionDeposit implements AccountCommand {
        public final int amount;

        public final String from;
        public final String to;

        public final ActorRef<Account.AccountCommand> receiver;

        public final ActorRef<Bank.BankCommand> bank;

        public ConditionDeposit(int amount, String from, String to, ActorRef<Account.AccountCommand> receiver, ActorRef<Bank.BankCommand> bank) {
            this.amount = amount;
            this.from = from;
            this.to = to;
            this.receiver = receiver;
            this.bank = bank;
        }
    }

    public static final class PrintBalance implements AccountCommand {}


    /* --- State ---------------------------------------- */
    private int balance;

    // for challenging 9
    private ActorRef<Bank.BankCommand> bank; // this is the bank that this account is associated with, only withdraw money from this bank


    /* --- Constructor ---------------------------------- */
    // Feel free to extend the contructor at your convenience
    private Account(ActorContext<AccountCommand> context, ActorRef<Bank.BankCommand> bank) {
        super(context);
        this.balance = 200;
        this.bank = bank;
    }


    /* --- Actor initial state -------------------------- */
    public static Behavior<Account.AccountCommand> create(ActorRef<Bank.BankCommand> bank) {
        return Behaviors.setup(context -> new Account(context, bank));
    }


    /* --- Message handling ----------------------------- */
    @Override
    public Receive<AccountCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(Deposit.class, this::onDeposit)
                .onMessage(ConditionDeposit.class, this::onConditionDeposit)
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

    // // for Challenging 9 and 10.
    public Behavior<AccountCommand> onConditionDeposit(ConditionDeposit msg) {

        // we will first subtract the money from the sender's account and then add the money to the receiver's account
        // check if amount is negative, if so then this account is the sender's account

        // for question 9
        if (msg.amount < 0 && (!msg.bank.equals(this.bank))) {
            this.getContext().getLog().info("Not asso bank, cannot withdraw money");
            return this;
        }

        // for question 10
        if (msg.amount < 0 && this.balance + msg.amount < 0) {
            this.getContext().getLog().info("No enough money, withdraw reject");
            // notify the bank that the transaction is rejected, and will not send the money to the receiver
            // get its own account address

            msg.bank.tell(new Bank.NoEnoughBalance(msg.from, msg.to, msg.amount));
            return this;
        }

        // subtract the money
        this.balance += msg.amount;

        //  add the money to the receiver's account
        msg.receiver.tell(new Account.Deposit(-1*msg.amount));

//        getContext().getLog().info("{} 66Actor received deposit {}, old balance = {}, new balance = {}",
//                getContext().getSelf().path().name(), msg.amount, this.balance - msg.amount, this.balance);
        return this;
    }

    public Behavior<AccountCommand> onPrintBalance(PrintBalance msg) {
        getContext().getLog().info("{} Actor balance = {}",
                getContext().getSelf().path().name(), this.balance);
        return this;
    }

}