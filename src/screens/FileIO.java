package screens;

import java.io.*;

public class FileIO implements Runnable {

	Thread IO;
	PingPongGameEngine Engine;
	BufferedReader br;
	PrintWriter pw ;

	public FileIO(PingPongGameEngine gameEngine) {
		Engine = gameEngine;

		IO = new Thread(this);
		IO.start();
	}

	public void fileOpen(String fileName){
		try {
			br = new BufferedReader(new FileReader("D://write//" + fileName + ".txt"));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		Engine.recMode = true;
	}
	@Override
	public void run() {

		while (true) {
				synchronized (Engine.monitor) {
					
					System.out.println("fio");
					if (Engine.REC) {

						pw.println(Engine.ballX);
						pw.println(Engine.ballY);
						pw.println(Engine.computerRacket_Y);
						pw.println(Engine.kidRacket_Y);
						pw.println(Engine.computerScore);
						pw.println(Engine.kidScore);

					}

					Engine.monitor.notify();
					try {
						Engine.monitor.wait();
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					if (Engine.recMode) {
						try {
							String line = null;
							line = br.readLine();
							Integer ballX = new Integer(line);
							line = br.readLine();
							Integer ballY = new Integer(line);
							line = br.readLine();
							Integer computerRacket_Y = new Integer(line);
							line = br.readLine();
							Integer kidRacket_Y = new Integer(line);
							line = br.readLine();
							Integer computerScore = new Integer(line);
							line = br.readLine();
							Integer kidScore = new Integer(line);
							//ScalaSorting scala = new ScalaSorting();
							Engine.ballX = ballX;
							Engine.ballY = ballY;
							Engine.computerRacket_Y = computerRacket_Y;
							Engine.kidRacket_Y = kidRacket_Y;
							Engine.computerScore = computerScore;
							Engine.kidScore = kidScore;

						} catch (IOException e) {
							e.printStackTrace();
						} catch (NumberFormatException e) {
							Engine.recMode = false;
						}

					}
				}

		}

	}
}

