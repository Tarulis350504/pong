package screens;

import java.awt.event.*;
import java.io.*;

public class PingPongGameEngine implements Runnable, MouseMotionListener,
		ActionListener, KeyListener, GameConstants {

	private PingPongGreenTable table; // ������ �� ����
	Thread worker;
	FileIO file;
	Object monitor;
	int kidRacket_Y = KID_RACKET_Y_START;
	int computerRacket_Y = COMPUTER_RACKET_Y_START;
	int kidScore;
	int computerScore;
	int slp = SLEEP_TIME;
	int ballX; // ���������� X ����
	int ballY; // ���������� Y ����
	int gameLength = 0;
	int kidBounce = 0;
	int computerBounce = 0;
	int kidGoal = 0;
	int computerGoal = 0; 
	private int level = 1; // ���� ���������(���������� - ���)
	private boolean movingLeft = true;
	private boolean ballServed = false;
	public boolean recMode = false;
	public boolean REC = false;
	private int verticalSlide; // �������� ������������� ������������ ���� �
								// ��������

	// �����������. �������� ������ �� ������ �����
	public PingPongGameEngine(PingPongGreenTable greenTable) {
		
		table = greenTable;
		monitor = new Object();
		worker = new Thread(this);
		worker.start();
		file = new FileIO(this);
	}

	// ������������ ������ �� ���������� MouseMotionListener
	// (��������� �� ��� ������,�� ������ ���� �������� ��� �����)
	public void mouseDragged(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
		if (!recMode && !(level == 4)) {
			int mouse_Y = e.getY();
			if (mouse_Y > FIELD_TOP && (mouse_Y + RACKET_LENGTH) < FIELD_BOTTOM) {
				kidRacket_Y = mouse_Y;
			} else
				return;
			table.setKidRacket_Y(kidRacket_Y);
		}
	}

	// ������������ ������ �� ���������� KeyListener
	public void keyPressed(KeyEvent e) {
		char key = e.getKeyChar();
		if ('n' == key || 'N' == key || '�' == key || '�' == key) {
			startNewGame();
		} else if ('q' == key || 'Q' == key || '�' == key || '�' == key) {
			endGame();
		} else if ('s' == key || 'S' == key || '�' == key || '�' == key) {
			kidServe();
		}
	}

	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {
	}

	// ���������� ������� ������ ���� ActionListener
	public void actionPerformed(ActionEvent e) {
		String Comstr = e.getActionCommand();
		if (Comstr.equals("����� ����  N"))
			startNewGame();
		if (Comstr.equals("������        S"))
			kidServe();
		if (Comstr.equals("���"))
			level = 1;
		if (Comstr.equals("����"))
			level = 2;
		if (Comstr.equals("����"))
			level = 3;
		if (Comstr.equals("�����    Q"))
			System.exit(0);
		if (Comstr.equals("������� ���")) {
			REC = false;

			File fEx = new File("D://write//000.txt");
			if (fEx.exists()) fEx.delete(); 		//������� ���� ���� ������������ ����
			
			computerScore = 0;
			kidScore = 0;
			level = 0;
			table.gamesRating();
		}
		if (Comstr.equals("BOTmode")) {
			if (!(level == 4))
				level = 4;
			else
				level = 1;
			startNewGame();
		}

	}

	// ������ ����� ����
	public void startNewGame() {
		recMode = false;
		ballServed = false;
		computerScore = 0;
		kidScore = 0;
		slp = SLEEP_TIME;
		ballX = BALL_START_X;
		ballY = BALL_START_Y;
		table.setBallPosition(ballX, ballY);
		try {
			File fEx = new File("D://write//000.txt");
			if (fEx.exists()) fEx.delete(); 		//������� ���� ���� ������������ ����
			file.pw = new PrintWriter(new OutputStreamWriter(
					new FileOutputStream("D://write//000.txt", true)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		REC = true;
		if (level == 4) {
			table.setMessageText("Computer: " + computerScore + " Kid: "
					+ kidScore);
			kidServe();
		}
		
	}

	// ��������� ����
	public void endGame() {
		file.pw.close();
		File reFile = new File ("D://write//000.txt");
		reFile.renameTo(new File("D://write//" + computerScore +"-" + kidScore + ".txt"));
		gameLength=0;
		startNewGame();
	}

	// ������������ ����� run() �� ���������� Runnable
	@Override
	public void run() {

		boolean canBounce = false;
		while (true) {
			synchronized (monitor) {
				System.out.println("eng");
				if (recMode) {
					table.setBallPosition(ballX, ballY);
					table.setKidRacket_Y(kidRacket_Y);
					table.setComputerRacket_Y(computerRacket_Y);
					table.setMessageText("Computer: " + computerScore
							+ " Kid: " + kidScore + " recMode - ON");
				} else {

					if (level == 4) {
						int K = random(1, 7);
						if (ballY + BALL_SIZE <= (kidRacket_Y + K
								* (RACKET_LENGTH / 7))
								// if (ballY + BALL_SIZE <= (kidRacket_Y +
								// RACKET_LENGTH)
								&& kidRacket_Y >= FIELD_TOP) {
							kidRacket_Y -= RACKET_INCREMENT;
						} else if ((kidRacket_Y + RACKET_LENGTH) <= FIELD_BOTTOM) {
							kidRacket_Y += RACKET_INCREMENT;
						}
					}

					table.setKidRacket_Y(kidRacket_Y);
					if (ballServed) { // ���� ��� ��������

						// ��� 1. ��� �������� �����?
						if (movingLeft && ballX >= BALL_MIN_X) {

							canBounce = (ballY + BALL_SIZE >= computerRacket_Y
									&& ballY < (computerRacket_Y + RACKET_LENGTH) ? true
									: false);
							ballX -= BALL_INCREMENT;
							ballY += verticalSlide;

							// �������� �������� ����� ��� ���� � �����
							// ��������� ���� ����� ��� ������

							table.setBallPosition(ballX, ballY);
							// ����� ���������?
							if (ballX <= COMPUTER_RACKET_X + BALL_SIZE / 2
									&& canBounce) {
								movingLeft = false;
								computerBounce++;

								if (ballY + BALL_SIZE / 2 >= computerRacket_Y
										&& ballY + BALL_SIZE / 2 <= (computerRacket_Y + RACKET_LENGTH / 7))
									verticalSlide = -6;
								if (ballY + BALL_SIZE / 2 >= (computerRacket_Y + RACKET_LENGTH / 7)
										&& ballY + BALL_SIZE / 2 <= (computerRacket_Y + 2 * (RACKET_LENGTH / 7)))
									verticalSlide = -4;
								if (ballY + BALL_SIZE / 2 >= (computerRacket_Y + 2 * (RACKET_LENGTH / 7))
										&& ballY + BALL_SIZE / 2 <= (computerRacket_Y + 3 * (RACKET_LENGTH / 7)))
									verticalSlide = -2;
								if (ballY + BALL_SIZE / 2 >= (computerRacket_Y + 4 * (RACKET_LENGTH / 7))
										&& ballY + BALL_SIZE / 2 <= (computerRacket_Y + 5 * (RACKET_LENGTH / 7)))
									verticalSlide = 2;
								if (ballY + BALL_SIZE / 2 >= (computerRacket_Y + 5 * (RACKET_LENGTH / 7))
										&& ballY + BALL_SIZE / 2 <= (computerRacket_Y + 6 * (RACKET_LENGTH / 7)))
									verticalSlide = 4;
								if (ballY + BALL_SIZE / 2 >= (computerRacket_Y + 6 * (RACKET_LENGTH / 7))
										&& ballY <= (computerRacket_Y + 7 * (RACKET_LENGTH / 7)))
									verticalSlide = 6;
							}
						}

						// ��� 2. ��� �������� ������?
						if (!movingLeft && ballX <= BALL_MAX_X) {
							canBounce = (ballY + BALL_SIZE >= kidRacket_Y
									&& ballY <= (kidRacket_Y + RACKET_LENGTH) ? true
									: false);

							ballX += BALL_INCREMENT;
							ballY += verticalSlide;
							table.setBallPosition(ballX, ballY);

							// ����� ���������? �������� ������� � �������� �
							// ����������� �� ������ ���������
							if (ballX > KID_RACKET_X - BALL_SIZE && canBounce) {
								movingLeft = true;
								kidBounce++;
								if (level == 1 || level == 4) {
									if (ballY + BALL_SIZE / 2 >= kidRacket_Y
											&& ballY + BALL_SIZE / 2 <= (kidRacket_Y + RACKET_LENGTH / 7))
										verticalSlide = -6;
									if (ballY + BALL_SIZE / 2 >= (kidRacket_Y + RACKET_LENGTH / 7)
											&& ballY + BALL_SIZE / 2 <= (kidRacket_Y + 2 * (RACKET_LENGTH / 7)))
										verticalSlide = -4;
									if (ballY + BALL_SIZE / 2 >= (kidRacket_Y + 2 * (RACKET_LENGTH / 7))
											&& ballY + BALL_SIZE / 2 <= (kidRacket_Y + 3 * (RACKET_LENGTH / 7)))
										verticalSlide = -2;
									if (ballY + BALL_SIZE / 2 >= (kidRacket_Y + 4 * (RACKET_LENGTH / 7))
											&& ballY + BALL_SIZE / 2 <= (kidRacket_Y + 5 * (RACKET_LENGTH / 7)))
										verticalSlide = 2;
									if (ballY + BALL_SIZE / 2 >= (kidRacket_Y + 5 * (RACKET_LENGTH / 7))
											&& ballY + BALL_SIZE / 2 <= (kidRacket_Y + 6 * (RACKET_LENGTH / 7)))
										verticalSlide = 4;
									if (ballY + BALL_SIZE / 2 >= (kidRacket_Y + 6 * (RACKET_LENGTH / 7))
											&& ballY <= (kidRacket_Y + RACKET_LENGTH))
										verticalSlide = 6;
								} else {
									if (ballY + BALL_SIZE / 2 <= kidRacket_Y
											+ RACKET_LENGTH / 2)
										verticalSlide--;
									else
										verticalSlide++;

								}
							}
						}

						// ��� 3. ���������� ������� ���������� ����� ��� ����,
						// ����� ����������� ���
						int K = random(1, 7);

						if (ballY + BALL_SIZE <= (computerRacket_Y + K
								* (RACKET_LENGTH / 7))
								&& computerRacket_Y > FIELD_TOP) {
							computerRacket_Y -= RACKET_INCREMENT;
						} else if ((computerRacket_Y + RACKET_LENGTH) <= FIELD_BOTTOM) {
							computerRacket_Y += RACKET_INCREMENT;
						}
						table.setComputerRacket_Y(computerRacket_Y);

						// ������ �� ����
						if (!isBallOnTheTable()) {
							verticalSlide = verticalSlide * -1;
							ballY += verticalSlide;

						}
						//�������� ����
						if (ballX >= BALL_MAX_X) {
							computerScore++;
							computerGoal++;
							monitor.notify();
							displayScore(1);
						} else if (ballX <= BALL_MIN_X) {
							kidScore++;
							kidGoal++;
							monitor.notify();
							displayScore(2);
						}
					} // ����� if ball served
				} // ����� else

				monitor.notify();
				try {
					monitor.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}// ����� synchronized
				// ��� 4. ������������
			try {
				if (level == 1)
					slp = SLEEP_TIME;
				if (level == 2)
					if (slp > 6)
						slp = slp - 1;
				if (level == 3)
					if (slp > 4)
						slp = slp - 1;
				if (level == 4)
					slp = 0;
				if (recMode == true)
					slp = 10;
				Thread.sleep(slp);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			gameLength++;
		} // ����� while

	}// ����� run()

	// ������ � ������� ������� ������� �������

	private void kidServe() {

		slp = SLEEP_TIME;
		ballX = KID_RACKET_X - BALL_SIZE;
		ballY = kidRacket_Y + RACKET_LENGTH / 2;
		if (ballY > TABLE_HEIGHT / 2) {
			verticalSlide = -1;
		} else {
			verticalSlide = 1;
		}
		table.setMessageText("Computer: " + computerScore + " Kid: " + kidScore);
		table.setBallPosition(ballX, ballY);
		table.setKidRacket_Y(kidRacket_Y);
		ballServed = true;
	}

	private void computerServe() {

		slp = SLEEP_TIME;
		ballX = COMPUTER_RACKET_X + RACKET_WIDTH;
		ballY = computerRacket_Y + RACKET_LENGTH / 2;
		if (ballY > TABLE_HEIGHT / 2) {
			verticalSlide = -1;
		} else {
			verticalSlide = 1;
		}
		table.setMessageText("Computer: " + computerScore + " Kid: " + kidScore);
		table.setBallPosition(ballX, ballY);
		table.setComputerRacket_Y(computerRacket_Y);
		ballServed = true;
	}

	private void displayScore(int win) {

		if (computerScore == WINNING_SCORE) {
			table.setMessageText("Computer won! " + computerScore + ":"
					+ kidScore);
			ballServed = false;
			endGame();
			return;

		} else if (kidScore == WINNING_SCORE) {
			table.setMessageText("You won! " + kidScore + ":" + computerScore);
			ballServed = false;
			endGame();
			return; 
		} else {

			ballServed = false;
			table.setMessageText("Computer: " + computerScore + " Kid: "
					+ kidScore);
		}
		if (level == 4) {
			if (win == 1) {
				computerRacket_Y = random(FIELD_TOP, FIELD_BOTTOM
						- RACKET_LENGTH);
				computerServe();
			} else {
				if (win == 2) {
					kidRacket_Y = random(FIELD_TOP, FIELD_BOTTOM
							- RACKET_LENGTH);
					kidServe();
				}
			}
		}

	}

	// ���������, �� ������� �� ��� ������� ��� ������ ������� �����
	private boolean isBallOnTheTable() {

		if (ballY >= BALL_MIN_Y && ballY <= BALL_MAX_Y) {
			return true;
		} else {
			return false;
		}
	}

	private int random(int x, int y) {
		int rn = x + (int) (Math.random() * ((y - x)));
		return rn;
	}
}
