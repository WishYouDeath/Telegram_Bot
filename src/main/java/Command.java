import org.telegram.telegrambots.meta.api.objects.Update;

public abstract class Command {
    public abstract void execute(Update update);
}
