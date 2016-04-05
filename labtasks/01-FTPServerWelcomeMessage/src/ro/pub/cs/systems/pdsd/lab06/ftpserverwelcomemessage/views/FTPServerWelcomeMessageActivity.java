package ro.pub.cs.systems.pdsd.lab06.ftpserverwelcomemessage.views;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

import ro.pub.cs.systems.pdsd.lab06.ftpserverwelcomemessage.R;
import ro.pub.cs.systems.pdsd.lab06.ftpserverwelcomemessage.general.Constants;
import ro.pub.cs.systems.pdsd.lab06.ftpserverwelcomemessage.general.Utilities;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class FTPServerWelcomeMessageActivity extends Activity {
	
	private EditText FTPServerAddressEditText;
	private TextView welcomeMessageTextView;
	
	protected class FTPServerCommunicationThread extends Thread {
		
		@Override
		public void run() {
			try {
				
				// TODO: exercise 4
				// open socket with FTPServerAddress (taken from FTPServerAddressEditText edit text) and port (Constants.FTP_PORT = 21)
				// get the BufferedReader attached to the socket (call to the Utilities.getReader() method)
				// should the line start with Constants.FTP_MULTILINE_START_CODE, the welcome message is processed
				// read lines from server while 
				// - the value is different from Constants.FTP_MULTILINE_END_CODE1
				// - the value does not start with Constants.FTP_MULTILINE_START_CODE2
				// append the line to the welcomeMessageTextView text view content (on the UI thread!!!)
				// close the socket
				
//				1. deschiderea unui socket care primește ca parametrii:
//					* adresa Internet a serverului FTP (precizată anterior de utilizator, preluată din câmpul text FTPServerAddressEditText)
//					* portul 21 (preluat din Constants.FTP_PORT).
				
				String address = FTPServerAddressEditText.getText().toString();
				Log.v(Constants.TAG, "Address: " + address);
				
				Socket socket = new Socket(address, Constants.FTP_PORT);
				Log.v(Constants.TAG, "Connected to " + socket.getInetAddress() + "(port: " + socket.getLocalPort() + ")");
				
//			2. obținerea fluxului de intrare atașat socket-ului, printr-un apel al metodei Utilities.getBufferedReader();
				BufferedReader bufferedReader = Utilities.getReader(socket);
				
				
//			3. citirea unei linii de pe fluxul de intrare:
				String line = bufferedReader.readLine();
				
//				dacă aceasta începe cu șirul de caractere 220- (preluat din Constants.FTP_MULTILINE_START_CODE), mesajul este analizat;
				if(line != null && line.startsWith(Constants.FTP_MULTILINE_START_CODE)) {
//					se citesc linii de pe fluxul de intrare atașat socket-ului cât timp valoarea:
//						* nu este egală cu 220 (preluat din Constants.FTP_MULTILINE_END_CODE1);
//						* nu începe cu 220 (preluat din Constants.FTP_MULTILINE_END_CODE2);
					while((line=bufferedReader.readLine()) != null) {
						if (!line.equals(Constants.FTP_MULTILINE_END_CODE1) &&
								!line.startsWith(Constants.FTP_MULTILINE_END_CODE2)) {
								Log.v(Constants.TAG, "Line read from ftp server: " + line);
								final String finalLine = line;
//								valoarea primită de la serverul FTP este inclusă în câmpul text welcomeMEssageTextView;
								welcomeMessageTextView.post(new Runnable() {
									@Override
									public void run() {
										welcomeMessageTextView.append(finalLine + " ");
									}
								});
							} else {
//								altfel, mesajul este ignorat.
								break;
							}
					}
				}
//			5. închiderea socket-ului.
				socket.close();
				
				
			} catch (Exception exception) {
				Log.e(Constants.TAG, "An exception has occurred: "+exception.getMessage());
				if (Constants.DEBUG) {
					exception.printStackTrace();
				}
			}
		}
	}	
	
	private ButtonClickListener buttonClickListener = new ButtonClickListener();
	private class ButtonClickListener implements Button.OnClickListener {
		
		@Override
		public void onClick(View view) {
			new FTPServerCommunicationThread().start();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ftpserver_welcome_message);
		
		FTPServerAddressEditText = (EditText)findViewById(R.id.ftp_server_address_edit_text);
		welcomeMessageTextView = (TextView)findViewById(R.id.welcome_message_text_view);
		
		Button displayWelcomeMessageButton = (Button)findViewById(R.id.display_welcome_message_button);
		displayWelcomeMessageButton.setOnClickListener(buttonClickListener);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ftpserver_welcome_message, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
