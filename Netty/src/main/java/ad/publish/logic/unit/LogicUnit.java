package ad.publish.logic.unit;

import ad.publish.logic.LogicContainer;
import ad.publish.task.Task;

public interface LogicUnit {

    public void setTask(Task task);

    public void setContainer(LogicContainer container);

    public Object getResult();
}
