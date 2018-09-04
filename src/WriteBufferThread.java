import java.util.ArrayList;

public class WriteBufferThread implements Runnable {
	
	private ArrayList<Act> buffer;
	private Thread ActionThreadth;
	private ActionThread at;
	
	public WriteBufferThread(ActionThread at) {
		buffer = new ArrayList<Act>();
		ActionThreadth = new Thread(this);
		this.at = at;
	}
	
	public void run() {
		while(buffer.size()!=0) {
			try {
				if(at.AddAct(buffer.get(0)) == ActionThread.Perhaps)
					buffer.remove(0);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	
	public void AddTask(Act act) {
		
		buffer.add(act);
		
		if(!ActionThreadth.isAlive()){
			ActionThreadth = new Thread(this);
			ActionThreadth.start();
		}
	}

}



