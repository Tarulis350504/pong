package screens;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.io.File;

/**
 * ���� ����� ������ ������� ���� ��� ����-�����, ���, �������, ���������� ����
 */

public class PingPongGreenTable extends JPanel implements GameConstants {

	private static final long serialVersionUID = 1L;
	private JLabel label;
	PingPongGameEngine gameEngine;
	private int computerRacket_Y = COMPUTER_RACKET_Y_START;
	private int kidRacket_Y = KID_RACKET_Y_START;
	private int ballX = BALL_START_X;
	private int ballY = BALL_START_Y;

	Dimension preferredSize = new Dimension(TABLE_WIDTH, TABLE_HEIGHT);

	public Dimension getPreferredSize() {
		return preferredSize;
	}

	// �����������. ������� ���������� ������� ���� � ����������
	PingPongGreenTable() {
		gameEngine = new PingPongGameEngine(this);
		addMouseMotionListener(gameEngine);
		addKeyListener(gameEngine);
	}

	// ������� � ���� ������ � JLabel
	void addLabeltoFrame(Container container) {
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
		container.add(this);
		label = new JLabel("Press N for a new game, S to serve or Q to quit");
		label.setBounds(0, TABLE_HEIGHT, TABLE_WIDTH, 20);
		container.add(label);
	}

	// ������ ����, ������ ����
	void addMenuBartoFrame(Container container) {

		JMenuBar menu = new JMenuBar();
		container.add(menu);
		menu.setLayout(new BoxLayout(menu, BoxLayout.X_AXIS));

		JMenu menuGame = new JMenu("����");
		menuGame.setAlignmentX(LEFT_ALIGNMENT);
		menu.add(menuGame);
		JMenuItem item1 = new JMenuItem("����� ����  N");
		item1.addActionListener(gameEngine);
		menuGame.add(item1);
		JMenuItem item2 = new JMenuItem("������        S");
		item2.addActionListener(gameEngine);
		menuGame.add(item2);

		JMenu menuLevel = new JMenu("���������");
		menuGame.add(menuLevel);
		JMenuItem item3 = new JMenuItem("���");
		item3.addActionListener(gameEngine);
		menuLevel.add(item3);
		JMenuItem item4 = new JMenuItem("����");
		item4.addActionListener(gameEngine);
		menuLevel.add(item4);
		JMenuItem item5 = new JMenuItem("����");
		item5.addActionListener(gameEngine);
		menuLevel.add(item5);
		menuGame.addSeparator();

		JMenuItem item8 = new JMenuItem("BOTmode");
		item8.addActionListener(gameEngine);
		menuGame.add(item8);

		JMenuItem item7 = new JMenuItem("������� ���");
		item7.addActionListener(gameEngine);
		menuGame.add(item7);

		JMenuItem item6 = new JMenuItem("�����    Q");
		item6.addActionListener(gameEngine);
		menuGame.add(item6);
	}

	// ������������ ����. ���� ����� ���������� �����������
	// �������, ����� ����� �������� ����� ���
	// ���������� ����� repaint() �� PingPoingGameEngine
	public void paintComponent(Graphics g) {

		super.paintComponent(g);

		// ���������� ������� ����
		g.setColor(Color.green);
		g.fillRect(0, 0, TABLE_WIDTH, TABLE_HEIGHT);
		// ���������� ����� �����

		g.setColor(Color.white);
		g.drawRect(FIELD_LEFT, FIELD_TOP, FIELD_WIGTH, FIELD_HEIGTH);
		g.drawLine(TABLE_WIDTH / 2, FIELD_TOP, TABLE_WIDTH / 2, FIELD_BOTTOM);

		// ���������� ������ �������
		g.setColor(Color.blue);
		g.fillRect(KID_RACKET_X, kidRacket_Y, RACKET_WIDTH, RACKET_LENGTH);

		// ���������� ����� �������
		g.setColor(Color.red);
		g.fillRect(COMPUTER_RACKET_X, computerRacket_Y, RACKET_WIDTH,
				RACKET_LENGTH);

		// ���������� ���
		g.setColor(Color.orange);
		g.fillOval(ballX, ballY, BALL_SIZE, BALL_SIZE);

		// ���������� ����� �� ����, �����
		// ���������� ���������� ��� �������� ������� �����
		requestFocus();
	}

	// ���������� ������� ��������� ������� �������
	public void setKidRacket_Y(int yCoordinate) {
		this.kidRacket_Y = yCoordinate;
		repaint();
	}

	// ���������� ������� ��������� ������� ����������
	public void setComputerRacket_Y(int yCoordinate) {
		this.computerRacket_Y = yCoordinate;
		repaint();
	}

	// ���������� ������� ���������
	public void setMessageText(String text) {
		label.setText(text);
		repaint();
	}

	// ���������� ������� ����
	public void setBallPosition(int xPos, int yPos) {
		ballX = xPos;
		ballY = yPos;
		repaint();
	}

	// ���� � ��������� ���
	public void gamesRating(){
		JFrame ratingFrame = new JFrame("Rating");
		ratingFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 210, 201, 240);
		ratingFrame.getContentPane().add(scrollPane);
		
		File myFolder = new File("D://write//");
		File[] files = myFolder.listFiles();
		String[] list = new String[files.length];
		int[] sortingNames = new int[files.length];
		///////////////////////////////////////////////
		for (int i = 0 ; i < files.length; i++){
			int l = files[i].getName().lastIndexOf('.');
			sortingNames[i] = new Integer(files[i].getName().substring(0, l));
		}
		//ScalaSorting scala = new ScalaSorting();
		//scala.sort(sortingNames);
		
		for (int i = 0 ; i < files.length; i++){
			list[i] = String.valueOf(sortingNames[i]);
		}
		///////////////////////////////////////////////
		JList fileList = new JList<Object>(list);
		scrollPane.setViewportView(fileList);
		fileList.addListSelectionListener(new ListSelectionListener(){
			public void  valueChanged(ListSelectionEvent arg0) {
				String fileName = (fileList.getSelectedValue()).toString();
				gameEngine.file.fileOpen(fileName);
			}
			
		});
		ratingFrame.setBounds(1100, 50, 200, 400);
		ratingFrame.setResizable(false);
		ratingFrame.setVisible(true);
	}

	public static void main(String[] args) {

		// ������� ��������� ����
		JFrame mainFrame = new JFrame("Ping Pong Green Table");

		mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		PingPongGreenTable table = new PingPongGreenTable();
		table.addMenuBartoFrame(mainFrame.getContentPane());
		table.addLabeltoFrame(mainFrame.getContentPane());
		// ���������� ������ ���� � ������� ��� �������
		mainFrame.setBounds(100, 50, TABLE_WIDTH + 15, TABLE_HEIGHT + 80);
		mainFrame.setResizable(false);
		mainFrame.setVisible(true);

	}

}
