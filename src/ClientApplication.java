import javafx.application.Application;
import javafx.stage.Stage;

public class ClientApplication extends Application {

	public String ip;
	public String port;
	public String key;
	
	public ClientApplication(String ip, String port, String key) {
		this.ip  = ip;
		this.port = port;
		this.key = key;
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		
	}
}
