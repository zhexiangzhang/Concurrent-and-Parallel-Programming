package exercises11;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;

import static java.lang.Thread.sleep;

public class Guardian extends AbstractBehavior<Guardian.GuardianCommand> {

    /* --- Messages ------------------------------------- */
    public interface GuardianCommand { }
    public static final class KickOff implements GuardianCommand { }
    // Feel free to add message types at your convenience

    /* --- State ---------------------------------------- */
    // empty


    /* --- Constructor ---------------------------------- */
    private Guardian(ActorContext<GuardianCommand> context) {
        super(context);
    }


    /* --- Actor initial state -------------------------- */
    // To be implemented
    public static Behavior<Guardian.GuardianCommand> create() {
        return Behaviors.setup(Guardian::new);
    }


    /* --- Message handling ----------------------------- */
    @Override
    public Receive<GuardianCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(KickOff.class, this::onKickOff)
                .build();
    }


    /* --- Handlers ------------------------------------- */
    // To be implemented
    public Behavior<GuardianCommand> onKickOff(KickOff msg) throws InterruptedException {
        // spawn the bank actors
        final ActorRef<Bank.BankCommand> bank1 = getContext().spawn(Bank.create(), "bank1");
        final ActorRef<Bank.BankCommand> bank2 = getContext().spawn(Bank.create(), "bank2");

        // spawn the mobile app actor
        final ActorRef<MobileApp.MobileAppCommand> mobileApp1 = getContext().spawn(MobileApp.create(), "mobileApp1");
        final ActorRef<MobileApp.MobileAppCommand> mobileApp2 = getContext().spawn(MobileApp.create(), "mobileApp2");

        // spawn the account actors
        final ActorRef<Account.AccountCommand> account1 = getContext().spawn(Account.create(), "account1");
        final ActorRef<Account.AccountCommand> account2 = getContext().spawn(Account.create(), "account2");

        mobileApp1.tell(new MobileApp.NewBank("bank1", bank1));
        mobileApp1.tell(new MobileApp.NewBank("bank2", bank2));
        mobileApp2.tell(new MobileApp.NewBank("bank1", bank1));
        mobileApp2.tell(new MobileApp.NewBank("bank2", bank2));

        bank1.tell(new Bank.NewAccount("account1", account1));
        bank1.tell(new Bank.NewAccount("account2", account2));
        bank2.tell(new Bank.NewAccount("account1", account1));
        bank2.tell(new Bank.NewAccount("account2", account2));

        // start payment 1
        mobileApp1.tell(new MobileApp.StartTransaction("account1", "account2", "bank1", 100));

        // start payment 2
        mobileApp2.tell(new MobileApp.StartTransaction("account2", "account1", "bank2", 50));

        // start make 100 random payment
        mobileApp1.tell(new MobileApp.Makepayments("account1", "account2", "bank1"));

        sleep(1000);

        // print balance
        account1.tell(new Account.PrintBalance());
        account2.tell(new Account.PrintBalance());

        return this;
    }
}
