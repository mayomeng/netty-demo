package ad.publish.logic.unit;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ad.publish.dao.Dao;
import ad.publish.info.AdColumn;
import ad.publish.logic.LogicContainer;
import ad.publish.task.Task;

@Component("LogicUnitImage")
@Scope("prototype")
public class ImageLogicUnit implements LogicUnit {

    @Autowired
    private Dao dao;

    private LogicContainer container;

    private Task task;

    public void setTask(Task task) {
        this.task = task;
    }

    @Override
    public void setContainer(LogicContainer container) {
        this.container = container;
    }

    @Override
    public Object getResult() {
        AdColumn adColumn = (AdColumn)task.getParams();
        String filePath = (String)dao.get(adColumn.getAdColumnId());
        File file = new File(filePath);
        return file;
    }

}
