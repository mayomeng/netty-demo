package ad.publish.task;


public interface Task {

    public Object getParams();

    public Object getResult();

    public Context getContext();

    public void setContext(Object context);
}
