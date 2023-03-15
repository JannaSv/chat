import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Client implements Runnable {
	
	private Socket client;
	private BufferedReader in;
	private PrintWriter out;
	private boolean done;
	
	
	@Override
	public void run() {
		try {
			client = new Socket("127.0.0.1", 9999);
			out = new PrintWriter (new OutputStreamWriter(client.getOutputStream(), StandardCharsets.UTF_8), true);
			in = new BufferedReader(new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8));
			InputHandler inHandler = new InputHandler();
			Thread t = new Thread(inHandler);
			t.start();
			
			String inMessage;
			while ((inMessage = in.readLine()) !=null) {
				System.out.println(inMessage);
			}
		}
		catch (IOException e) {
			shutdown();
		}
	}
	
	public void shutdown() {
		done = true;
		try {
			in.close();
			out.close();
			if (!client.isClosed()) {
				client.close();
			}
		}
		catch (IOException e) {
			
		}
	
	}
	
	
	class InputHandler implements Runnable {
		
		@Override
		public void run() {
			try {
				BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
				while (!done) {
					String message = inReader.readLine();
					if (message.equals("/quit")) {
						out.println(message);
						inReader.close();
						shutdown();
					}
					else {
						out.println(message);
					}
				}
			}
			catch (IOException e) {
				shutdown();
			}
		}
	}

	public static void main(String [] args) {
		Client client = new Client();
		client.run();
	}
}
