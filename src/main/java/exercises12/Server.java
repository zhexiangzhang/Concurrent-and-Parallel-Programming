package exercises12;

// Hint: The imports below may give you hints for solving the exercise.
//       But feel free to change them.

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.ChildFailed;
import akka.actor.typed.Terminated;
import akka.actor.typed.javadsl.*;

import java.util.ArrayList;
import java.util.Queue;
import java.util.List;
import java.util.LinkedList;
import java.util.stream.IntStream;

import exercises12.Task;
import exercises12.Task.BinaryOperation;

public class Server extends AbstractBehavior<Server.ServerCommand> {
    /* --- Messages ------------------------------------- */
    public interface ServerCommand { }

    public static final class ComputeTasks implements ServerCommand {
        public final List<Task> tasks;
        public final ActorRef<Client.ClientCommand> client;

        public ComputeTasks(List<Task> tasks,
                            ActorRef<Client.ClientCommand> client) {
            this.tasks  = tasks;
            this.client = client;
        }
    }

    public static final class WorkDone implements ServerCommand {
        ActorRef<Worker.WorkerCommand> worker;

        public WorkDone(ActorRef<Worker.WorkerCommand> worker) {
            this.worker = worker;
        }
    }

    /* --- State ---------------------------------------- */
    // To be implemented
    private int workId = 0;
    private final int minWorkers;
    private final int maxWorkers;
    private final List<ActorRef<Worker.WorkerCommand>> idleWorkers;
    private final List<ActorRef<Worker.WorkerCommand>> busyWorkers;
    private final List<ComputeTasks> pendingTasks;


    /* --- Constructor ---------------------------------- */
    private Server(ActorContext<ServerCommand> context,
                   int minWorkers, int maxWorkers) {
        super(context);
        // To be implemented
        this.minWorkers = minWorkers;
        this.maxWorkers = maxWorkers;
        this.idleWorkers = new ArrayList<>();
        this.busyWorkers = new ArrayList<>();
        this.pendingTasks = new ArrayList<>();
        // Create minWorkers workers
        for (int i = 0; i < minWorkers; i++) {
            ActorRef<Worker.WorkerCommand> worker = context.spawn(Worker.create(context.getSelf()), "worker" + (++workId));
            getContext().watch(worker);
            idleWorkers.add(worker);
        }

    }


    /* --- Actor initial state -------------------------- */
    public static Behavior<ServerCommand> create(int minWorkers, int maxWorkers) {
        return Behaviors.setup(context -> new Server(context, minWorkers, maxWorkers));
    }


    /* --- Message handling ----------------------------- */
    @Override
    public Receive<ServerCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(ComputeTasks.class, this::onComputeTasks)
                .onMessage(WorkDone.class, this::onWorkDone)
                // To be extended
                .onSignal(ChildFailed.class, this::onChildFailed)
                .onSignal(Terminated.class, this::onTerminated)
                .build();
    }


    /* --- Handlers ------------------------------------- */
    public Behavior<ServerCommand> onComputeTasks(ComputeTasks msg) {
        // To be implemented

        List<Task> unProcessedTasks = new ArrayList<>();
        for (Task task : msg.tasks) {
            if (idleWorkers.isEmpty()) {
                // If there are no idle workers
                // case1: busyWorkers.size() < maxWorkers, spawn a new worker
                if (busyWorkers.size() < maxWorkers) {
                    ActorRef<Worker.WorkerCommand> worker = getContext().spawn(Worker.create(getContext().getSelf()), "worker" + (++workId));
                    getContext().watch(worker);
                    busyWorkers.add(worker);
                    worker.tell(new Worker.ComputeTask(task, msg.client));
                }
                // case2: busyWorkers.size() == maxWorkers, add the task to pendingTasks
//                pendingTasks.add(msg);
                else {
                    unProcessedTasks.add(task);
                }
            } else {
                // If there are idle workers the task is sent to a worker
                ActorRef<Worker.WorkerCommand> worker = idleWorkers.remove(0);
                busyWorkers.add(worker);
                worker.tell(new Worker.ComputeTask(task, msg.client));
            }
        }
        if (!unProcessedTasks.isEmpty()) {
            pendingTasks.add(new ComputeTasks(unProcessedTasks, msg.client));
        }
        return this;
    }

    public Behavior<ServerCommand> onWorkDone(WorkDone msg) {
        // To be implemented
        if (pendingTasks.isEmpty()) {
            // If there are no pending tasks
            busyWorkers.remove(msg.worker);
            // case1: idleWorkers.size() < minWorkers, add the worker to idleWorkers
            if (idleWorkers.size() < minWorkers) {
                idleWorkers.add(msg.worker);
            }
            // case2: idleWorkers.size() >= minWorkers, just stop the worker
            else {
                msg.worker.tell(new Worker.Stop());
            }
        } else {
            // If there are pending tasks, send the first task to the worker
            ComputeTasks task = pendingTasks.remove(0);
            msg.worker.tell(new Worker.ComputeTask(task.tasks.get(0), task.client));
            task.tasks.remove(0);
            if (!task.tasks.isEmpty()) {
                // If there are more tasks in the ComputeTasks, add the ComputeTasks to pendingTasks
                pendingTasks.add(task);
            }
        }
        return this;
    }

    public Behavior<ServerCommand> onChildFailed(ChildFailed msg) {
        // To be implemented
        busyWorkers.remove(msg.getRef()); // remove the failed worker from busyWorkers
        final ActorRef<Worker.WorkerCommand> worker = getContext().spawn(Worker.create(getContext().getSelf()), "worker" + (++workId));
        getContext().watch(worker);
        idleWorkers.add(worker);
        getContext().getLog().info("worker {} crashed due to {}, new worker {} adding to the idle list", msg.getRef().path().name(), msg.cause(), worker.path().name());
        return this;
    }

    public Behavior<ServerCommand> onTerminated(Terminated msg) {
        return this;
    }
}