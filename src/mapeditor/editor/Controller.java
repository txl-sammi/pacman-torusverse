package src.mapeditor.editor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import src.GamePlayer;
import src.Driver;
import src.checking.LevelCheckingStrategy;
import src.mapeditor.grid.Camera;
import src.mapeditor.grid.Grid;
import src.mapeditor.grid.GridCamera;
import src.mapeditor.grid.GridModel;
import src.mapeditor.grid.GridView;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.JDOMException;
import src.utility.PropertiesLoader;

/**
 * Controller of the application.
 * 
 * @author Daniel "MaTachi" Jonsson
 * @version 1
 * @since v0.0.5
 * 
 */
public class Controller implements ActionListener, GUIInformation {

	/**
	 * The model of the map editor.
	 */
	private Grid model;

	private Tile selectedTile;
	private Camera camera;

	private List<Tile> tiles;

	private GridView grid;
	private View view;

	private int gridWith = Constants.MAP_WIDTH;
	private int gridHeight = Constants.MAP_HEIGHT;
/*
	current map file in the editor
	if the user load(build) a file, this variable will hold name of the file loaded.
	if the user click save, this variable will hold name of the file saved.
	if the user does not load and does not save, this variable will be null.
	 */
    private String currentMapFilePath = null;


	/**
	 * Construct the controller.
	 */
	public Controller() {
		init(Constants.MAP_WIDTH, Constants.MAP_HEIGHT);

	}
	public Controller(File file) {
		init(Constants.MAP_WIDTH, Constants.MAP_HEIGHT);
		buildFile(file);
	}

	public void init(int width, int height) {
		this.tiles = TileManager.getTilesFromFolder("pacman/src/data");
		this.model = new GridModel(width, height, tiles.get(0).getCharacter());
		this.camera = new GridCamera(model, Constants.GRID_WIDTH,
				Constants.GRID_HEIGHT);

		grid = new GridView(this, camera, tiles); // Every tile is
													// 30x30 pixels

		this.view = new View(this, camera, grid, tiles);
	}

	/**
	 * Different commands that comes from the view.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		for (Tile t : tiles) {
			if (e.getActionCommand().equals(
					Character.toString(t.getCharacter()))) {
				selectedTile = t;
				break;
			}
		}
		if (e.getActionCommand().equals("flipGrid")) {
			// view.flipGrid();
		} else if (e.getActionCommand().equals("save")) {
			saveFile();
		} else if (e.getActionCommand().equals("load")) {
			loadFile();
		} else if (e.getActionCommand().equals("update")) {
			updateGrid(gridWith, gridHeight);
		} else if (e.getActionCommand().equals("start_game")) {
			// TODO: code to switch to pacman game check
			// start a new thread to run the game
			Thread thread = new Thread() {
				public void run() {
					startGame();
				}
			};
			thread.start();
		}
	}


	private void startGame() {
		if (this.currentMapFilePath == null) {
			JOptionPane.showMessageDialog(null, "no map file loaded or saved in the editor, can not start game!", "error",
					JOptionPane.ERROR_MESSAGE);
				return;
		}

		// close the editor
		view.close();

		// start the game using the currentFilePath
		String propertiesPath = Driver.DEFAULT_PROPERTIES_PATH; // this is only used to get the auto property and seed

		List<File> xmlFiles = new ArrayList<>();
		xmlFiles.add(new File(this.currentMapFilePath));
		final Properties properties = PropertiesLoader.loadPropertiesFile(propertiesPath);

		GamePlayer gamePlayer = new GamePlayer(xmlFiles, properties.getProperty("PacMan.isAuto"),
				properties.getProperty("seed"));


		LevelCheckingStrategy levelChecker = new LevelCheckingStrategy(xmlFiles);
		Properties gameProperties = levelChecker.loadFromXML(xmlFiles.get(0));
;		String levelName = xmlFiles.get(0).getName();
		boolean validLevel =  levelChecker.check();

		if (validLevel) {
			gamePlayer.playLevel(gameProperties);
		}
		else {
			JOptionPane.showMessageDialog(null, "Level " + levelName + " is invalid. Check Log", "error",
					JOptionPane.ERROR_MESSAGE);
		}
		view.open();

	}

	public void updateGrid(int width, int height) {
		view.close();
		init(width, height);
		view.setSize(width, height);
	}

	DocumentListener updateSizeFields = new DocumentListener() {

		public void changedUpdate(DocumentEvent e) {
			gridWith = view.getWidth();
			gridHeight = view.getHeight();
		}

		public void removeUpdate(DocumentEvent e) {
			gridWith = view.getWidth();
			gridHeight = view.getHeight();
		}

		public void insertUpdate(DocumentEvent e) {
			gridWith = view.getWidth();
			gridHeight = view.getHeight();
		}
	};

	private void saveFile() {

		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"xml files", "xml");
		chooser.setFileFilter(filter);
		File workingDirectory = new File(System.getProperty("user.dir"));
		chooser.setCurrentDirectory(workingDirectory);

		int returnVal = chooser.showSaveDialog(null);
		try {
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				this.currentMapFilePath = chooser.getSelectedFile().getAbsolutePath();

				Element level = new Element("level");
				Document doc = new Document(level);
				doc.setRootElement(level);

				Element size = new Element("size");
				int height = model.getHeight();
				int width = model.getWidth();
				size.addContent(new Element("width").setText(width + ""));
				size.addContent(new Element("height").setText(height + ""));
				doc.getRootElement().addContent(size);

				for (int y = 0; y < height; y++) {
					Element row = new Element("row");
					for (int x = 0; x < width; x++) {
						char tileChar = model.getTile(x,y);
						String type = "PathTile";

						if (tileChar == 'b')
							type = "WallTile";
						else if (tileChar == 'c')
							type = "PillTile";
						else if (tileChar == 'd')
							type = "GoldTile";
						else if (tileChar == 'e')
							type = "IceTile";
						else if (tileChar == 'f')
							type = "PacTile";
						else if (tileChar == 'g')
							type = "TrollTile";
						else if (tileChar == 'h')
							type = "TX5Tile";
						else if (tileChar == 'i')
							type = "PortalWhiteTile";
						else if (tileChar == 'j')
							type = "PortalYellowTile";
						else if (tileChar == 'k')
							type = "PortalDarkGoldTile";
						else if (tileChar == 'l')
							type = "PortalDarkGrayTile";

						Element e = new Element("cell");
						row.addContent(e.setText(type));
					}
					doc.getRootElement().addContent(row);
				}

				XMLOutputter xmlOutput = new XMLOutputter();
				xmlOutput.setFormat(Format.getPrettyFormat());
				xmlOutput.output(doc, new FileWriter(chooser.getSelectedFile()));

				File selectedFile = chooser.getSelectedFile();
				List<File> xmlFile = new ArrayList<>();
				xmlFile.add(selectedFile);

				LevelCheckingStrategy levelChecker = new LevelCheckingStrategy(xmlFile);
				levelChecker.check();

			}
		} catch (FileNotFoundException e1) {
			JOptionPane.showMessageDialog(null, "Invalid file!", "error",
					JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
		}
	}

	public void loadFile() {
		try {
			JFileChooser chooser = new JFileChooser();
			File selectedFile;
			File workingDirectory = new File(System.getProperty("user.dir"));
			chooser.setCurrentDirectory(workingDirectory);

			int returnVal = chooser.showOpenDialog(null);
			if (returnVal == JFileChooser.APPROVE_OPTION) {

				selectedFile = chooser.getSelectedFile();
				List<File> xmlFile = new ArrayList<>();
				xmlFile.add(selectedFile);

				LevelCheckingStrategy levelChecker = new LevelCheckingStrategy(xmlFile);
				levelChecker.check();
				buildFile(selectedFile);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void buildFile(File selectedFile) {
		Document document;
		SAXBuilder builder = new SAXBuilder();
		try {
			if (selectedFile.canRead() && selectedFile.exists()) {
				this.currentMapFilePath = selectedFile.getAbsolutePath();

				document = (Document) builder.build(selectedFile);

				Element rootNode = document.getRootElement();

				List sizeList = rootNode.getChildren("size");
				Element sizeElem = (Element) sizeList.get(0);
				int height = Integer.parseInt(sizeElem
						.getChildText("height"));
				int width = Integer
						.parseInt(sizeElem.getChildText("width"));
				updateGrid(width, height);

				List rows = rootNode.getChildren("row");
				for (int y = 0; y < rows.size(); y++) {
					Element cellsElem = (Element) rows.get(y);
					List cells = cellsElem.getChildren("cell");

					for (int x = 0; x < cells.size(); x++) {
						Element cell = (Element) cells.get(x);
						String cellValue = cell.getText();

						char tileNr = 'a';
						if (cellValue.equals("PathTile"))
							tileNr = 'a';
						else if (cellValue.equals("WallTile"))
							tileNr = 'b';
						else if (cellValue.equals("PillTile"))
							tileNr = 'c';
						else if (cellValue.equals("GoldTile"))
							tileNr = 'd';
						else if (cellValue.equals("IceTile"))
							tileNr = 'e';
						else if (cellValue.equals("PacTile"))
							tileNr = 'f';
						else if (cellValue.equals("TrollTile"))
							tileNr = 'g';
						else if (cellValue.equals("TX5Tile"))
							tileNr = 'h';
						else if (cellValue.equals("PortalWhiteTile"))
							tileNr = 'i';
						else if (cellValue.equals("PortalYellowTile"))
							tileNr = 'j';
						else if (cellValue.equals("PortalDarkGoldTile"))
							tileNr = 'k';
						else if (cellValue.equals("PortalDarkGrayTile"))
							tileNr = 'l';
						else
							tileNr = '0';

						model.setTile(x, y, tileNr);
					}
				}

				String mapString = model.getMapAsString();
				grid.redrawGrid();

			}
		} catch (JDOMException e) {
			// Exception handling
			e.printStackTrace(); // Print the stack trace for debugging
		} catch (IOException e) {
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Tile getSelectedTile() {
		return selectedTile;
	}
}
