//******************************************************************************
// Copyright (C) 2019-2020 University of Oklahoma Board of Trustees.
//******************************************************************************
// Last modified: Fri Feb 14 12:15:51 2020 by Chris Weaver
//******************************************************************************
// Major Modification History:
//
// 20190203 [weaver]:	Original file.
// 20190220 [weaver]:	Adapted from swingmvc to fxmvc.
// 20200212 [weaver]:	Overhauled for new PrototypeB in Spring 2020.
//
//******************************************************************************
//
//******************************************************************************
package edu.ou.cs.hci.assignment.prototypeb.pane;

//import java.lang.*;
import java.util.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import javafx.util.Callback;
import javafx.util.converter.*;
import edu.ou.cs.hci.assignment.prototypeb.*;
import edu.ou.cs.hci.resources.Resources;

//******************************************************************************

/**
 * The <CODE>CollectionPane</CODE> class.
 *
 * @author  Chris Weaver
 * @version %I%, %G%
 */

public final class CollectionPane extends AbstractPane
{
	//**********************************************************************
	// Private Class Members
	//**********************************************************************

	private static final String	NAME = "Collection";
	private static final String	HINT = "Movie Collection Browser";

	//**********************************************************************
	// Private Class Members (Layout)
	//**********************************************************************

	private static final double	W = 32;		// Item icon width
	private static final double	H = W * 1.5;	// Item icon height

	private static final Insets	PADDING =
		new Insets(40.0, 20.0, 40.0, 20.0);

	//**********************************************************************
	// Private Members
	//**********************************************************************

	// Data
	private final List<String>			gdata;		// Genre strings
	private final List<String>			rdata;		// Rating strings
	private final List<List<String>>	mdata;		// Movie attributes

	// Collection
	private final List<Movie>			movies;	// Movie objects

	// Layout
	private TableView<Movie>			table;
	private SelectionModel<Movie>		smodel;

	// Add members for your summary widgets here...
	private Label      labelTitle;
	private Label      labelRating;
	private Label      labelRuntime;
	private Label      labelAvgReviewScore;
	private TextField	ImageFile;
	private ImageView	ImageView;




	//**********************************************************************
	// Constructors and Finalizer
	//**********************************************************************

	public CollectionPane(Controller controller)
	{
		super(controller, NAME, HINT);

		// Load data sets from hardcoded file locations
		gdata = Resources.getLines("data/genres.txt");
		rdata = Resources.getLines("data/ratings.txt");
		mdata = Resources.getCSVData("data/movies.csv");

		// Convert the raw movie data into movie objects
		movies = new ArrayList<Movie>();

		for (List<String> item : mdata)
			movies.add(new Movie(item));

		// Construct the pane
		setBase(buildPane());
	}

	//**********************************************************************
	// Public Methods (Controller)
	//**********************************************************************

	// The controller calls this method when it adds a view.
	// Set up the nodes in the view with data accessed through the controller.
	public void	initialize()
	{
		smodel.selectedIndexProperty().addListener(this::changeIndex);
		int	index = (Integer)controller.get("selectedMovieIndex");
		smodel.select(index);
		labelTitle.setText((String)controller.get("movie.title"));
		labelRating.setText(handleRating((Integer)controller.get("movie.rating")));
		labelRuntime.setText(String.valueOf(((Integer)controller.get("movie.runtime") / 60)
				+ "h " + String.valueOf(((Integer)controller.get("movie.runtime")) % 60)
				+"min" ));
		labelAvgReviewScore.setText(String.valueOf(controller.get("movie.averageReviewScore"))+ " / 10.0");
		ImageFile.setText((String)(controller.get("movie.imageFile"))); 
		
		try 
		{
			ImageView.setImage(new Image(
				FX_ICON + (String)controller.get("movie.imageFile"),
				100, 100, true, false));
		}
		catch (Exception ex)
		{
			ImageView.setImage(null);
		} 
	} 

	
	// The controller calls this method when it removes a view.
	// Unregister event and property listeners for the nodes in the view.
	public void	terminate()
	{
		smodel.selectedIndexProperty().removeListener(this::changeIndex);
		ImageFile.setOnAction(null);
		labelTitle.setText(null);
		labelRating.setText(null);
		labelRuntime.setText(null);
		labelAvgReviewScore.setText(null);

		// Terminate your summary widgets here...
	}

	// The controller calls this method whenever something changes in the model.
	// Update the nodes in the view to reflect the change.
	public void	update(String key, Object value)
	{
		if ("selectedMovieIndex".equals(key))
		{
			int	index = (Integer)value;
			Movie	movie = movies.get(index);
			smodel.select(index);
			labelTitle.setText(movie.getTitle());
			labelRating.setText(String.valueOf(movie.getRating()));
			labelRuntime.setText(String.valueOf(((Integer)movie.getRuntime() / 60)
					+ "h " + String.valueOf(((Integer)movie.getRuntime()) % 60)
					+"min" ));
			labelAvgReviewScore.setText(String.valueOf(movie.getReviewScore())+ " / 10.0");
			ImageFile.setText(String.valueOf(value));

			try
			{
				ImageView.setImage(new Image(FX_ICON + movie.getImage(),
											  100, 100, true, false));
			}
			catch (Exception ex)
			{
				ImageView.setImage(null);
			}
			
		}
			// Update your summary widgets here, using movie attributes...
	}

	//**********************************************************************
	// Private Methods (Layout)
	//**********************************************************************

	private Pane	buildPane()
	{
		Node	bregion = buildTableView();
		Node	tregion = buildCoverFlow();
		Node	lregion = buildLaterView();
		Node	rregion = buildMovieView();

		// Create a split pane to share space between the cover pane and table
		SplitPane	splitPane = new SplitPane();

		splitPane.setOrientation(Orientation.VERTICAL);
		splitPane.setDividerPosition(0, 0.1);	// Put divider at 50% T-to-B

		splitPane.getItems().add(tregion);		// Cover flow at the top...
		splitPane.getItems().add(bregion);		// ...table view at the bottom

		StackPane	lpane = new StackPane(lregion);
		StackPane	rpane = new StackPane(rregion);
		
		return new BorderPane(splitPane, null, rregion, null, lregion);
	}

	private TableView<Movie>	buildTableView()
	{
		// Create the table and grab its selection model
		table = new TableView<Movie>();
		smodel = table.getSelectionModel();

		// Set up some helpful stuff including single selection mode
		table.setEditable(true);
		table.setPlaceholder(new Text("No Data!"));
		table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

		// Add columns for title and image
		table.getColumns().add(buildTitleColumn());
		table.getColumns().add(buildImageColumn());

		// TODO #8: Uncomment these to add columns for your three attributes.
		table.getColumns().add(buildRatingColumn());
		table.getColumns().add(buildAvgReviewScoreColumn());
		table.getColumns().add(buildRuntimeCoulumn());

		// Put the movies into an ObservableList to use as the table model
		table.setItems(FXCollections.observableArrayList(movies));

		return table;
	}

	private Node	buildCoverFlow()
	{
		Label	label = new Label("this space reserved for cover flow (later)");

		label.setPadding(PADDING);

		return label;
	}

	private Node	buildLaterView()
	{
		Label	label = new Label("saving for later");

		label.setPadding(PADDING);

		return label;
	}

	// TODO #9: Build the layout for your movie summary here, showing the title,
	// image, and your three attributes. For any widgets you use, add members
	// and/or code to initialize(), terminate(), and update() above, as needed.
	// Keep in mind that the movie summary is meant for display, not editing.
	private Node	buildMovieView()
	{
		// The label is just a placeholder. Replace it with your own widgets!
		
		//Label for title 
		labelTitle = new Label((String)controller.get("movie.title"));
		
		// Label for rating
		labelRating = new Label((String.valueOf(controller.get("movie.rating"))));
		
		// Label for runtime
		labelRuntime = new Label(String.valueOf(((Integer)controller.get("movie.runtime")) / 60)
				+ "h " + String.valueOf(((Integer)controller.get("movie.runtime")) % 60)
				+"min" );
		// Label for average review score 
		labelAvgReviewScore = new Label(String.valueOf(controller.get("movie.averageReviewScore"))+ " / 10.0");
		
		// ImageView for Poster
		ImageFile = new TextField(String.valueOf(controller.get("movie.imageFile")));
		ImageView = new ImageView();
		try
		{
			ImageView.setImage(new Image(
				FX_ICON + (String)controller.get("movie.imageFile"),
				100, 200, false, true));
		}
		catch (Exception ex)
		{
			ImageView.setImage(null);
		}
		
		// Set title label attributes 
		labelTitle.setPrefSize(150, 80);
		labelTitle.setAlignment(Pos.CENTER);
		labelTitle.setWrapText(true);
		labelTitle.setTextAlignment(TextAlignment.CENTER);
		
		
		// Set rating label attributes 
		labelRating.setPadding(PADDING);
		labelRating.setAlignment(Pos.CENTER);
		
		// Set runtime label attributes 
		labelRuntime.setPadding(PADDING);
		labelRuntime.setAlignment(Pos.CENTER);
		
		// Set average review score label attributes 
		labelAvgReviewScore.setPadding(PADDING);
		labelAvgReviewScore.setAlignment(Pos.CENTER);
		initialize();	
		
		// Create a Vbox for the top of the summary pane
		VBox top = new VBox(10);
		
		// Add widgets to the top of the summary pane Vbox
		top.getChildren().addAll(labelTitle,ImageView);
		
		// Set attributes of the Vbox
		top.setPrefWidth(100);
		top.setAlignment(Pos.CENTER); 
		
		// Create a Vbox for the bottom of the summary pane
		VBox bottom = new VBox(1);
		
		// Add widgets to the bottom of the summary pane Vbox
		bottom.getChildren().addAll(labelRating,labelRuntime,labelAvgReviewScore);
		bottom.setAlignment(Pos.CENTER);
		
		// Create a Vbox for the summary pane
		VBox summaryBox = new VBox(5);
		
		// Add widgets to the summary pane Vbox
		summaryBox.getChildren().addAll(top,bottom);
		summaryBox.setPrefWidth(125);
		
		// Return the summary pane Vbox //
		 return summaryBox;
	}

	//**********************************************************************
	// Private Methods (Table Columns)
	//**********************************************************************

	// This TableColumn displays titles, and allows editing.
	private TableColumn<Movie, String>	buildTitleColumn()
	{
		TableColumn<Movie, String>	column =
			new TableColumn<Movie, String>("Title");

		column.setEditable(true);
		column.setPrefWidth(250);
		column.setCellValueFactory(
			new PropertyValueFactory<Movie, String>("title"));
		column.setCellFactory(new TitleCellFactory());
		// Edits in this column update movie titles
		column.setOnEditCommit(new TitleEditHandler());

		return column;
	}

	// This TableColumn displays images, and does not allow editing.
	private TableColumn<Movie, String>	buildImageColumn()
	{
		TableColumn<Movie, String>	column =
			new TableColumn<Movie, String>("Image");

		column.setEditable(false);
		column.setPrefWidth(W + 8.0);
		column.setCellValueFactory(
			new PropertyValueFactory<Movie, String>("image"));
		column.setCellFactory(new ImageCellFactory());

		return column;
	}
	
	// This TableColumn displays ratings, and allows editing.
	private TableColumn<Movie, String>	buildRatingColumn()
	{
		TableColumn<Movie, String>	column =
			new TableColumn<Movie, String>("Rating");

		column.setEditable(true);
		column.setPrefWidth(140);
		column.setCellValueFactory(
			new PropertyValueFactory<Movie, String>("rating"));
		column.setCellFactory(new RatingCellFactory());

		// Edits in this column update movie titles
		column.setOnEditCommit(new RatingEditHandler());

		return column;
	}
	
	// This TableColumn displays Avg. review score, and allows editing.
	private TableColumn<Movie, Double>	buildAvgReviewScoreColumn()
	{
		TableColumn<Movie, Double>	column =
			new TableColumn<Movie, Double>("Avg. Review Score");

		column.setEditable(true);
		column.setPrefWidth(140);
		column.setCellValueFactory(
			new PropertyValueFactory<Movie, Double>("reviewScore"));
		column.setCellFactory(new AvgReviewScoreCellFactory());

		// Edits in this column update movie titles
		column.setOnEditCommit(new AvgReviewScoreEditHandler());

		return column;
	}
	
	// This TableColumn displays runtime, and allows editing.
	private TableColumn<Movie, Integer>	buildRuntimeCoulumn()
	{
		TableColumn<Movie, Integer>	column =
			new TableColumn<Movie, Integer>("Runtime");

		column.setEditable(true);
		column.setPrefWidth(140);
		column.setCellValueFactory(
			new PropertyValueFactory<Movie, Integer>("runtime"));
		column.setCellFactory(new RuntimeCellFactory());

		// Edits in this column update movie titles
		column.setOnEditCommit(new RuntimeEditHandler());

		return column;
	}

	// TODO #7: Complete the TableColumn methods for your three attributes.
	// You must adapt the code to the column's attribute type in each case.

	//private TableColumn<Movie, String>	buildAttr1Column()
	//{
	//}

	//private TableColumn<Movie, String>	buildAttr2Column()
	//{
	//}

	//private TableColumn<Movie, String>	buildAttr3Column()
	//{
	//}

	//**********************************************************************
	// Private Methods (Change Handlers)
	//**********************************************************************

	private void	changeIndex(ObservableValue<? extends Number> observable,
								Number oldValue, Number newValue)
	{
		int	index = (Integer)newValue;

		controller.set("selectedMovieIndex", index);
	}

	//**********************************************************************
	// Inner Classes (Cell Factories)
	//**********************************************************************

	// This CellFactory creates Cells for the title column in the table.
	private final class TitleCellFactory
		implements Callback<TableColumn<Movie, String>,
							TableCell<Movie, String>>
	{
		public TableCell<Movie, String>	call(TableColumn<Movie, String> v)
		{
			return new TitleCell();
		}
	}

	// This CellFactory creates Cells for the image column in the table.
	private final class ImageCellFactory
		implements Callback<TableColumn<Movie, String>,
							TableCell<Movie, String>>
	{
		public TableCell<Movie, String>	call(TableColumn<Movie, String> v)
		{
			return new ImageCell();
		}
	}
	
	// This CellFactory creates Cells for the rating column in the table.
	private final class RatingCellFactory
	implements Callback<TableColumn<Movie, String>,
						TableCell<Movie, String>>
	{
	public TableCell<Movie, String>	call(TableColumn<Movie, String> v)
		{
		return new RatingCell();
		}
	}
	
	// This CellFactory creates Cells for the Avg. Review Score column in the table.
	private final class AvgReviewScoreCellFactory
	implements Callback<TableColumn<Movie, Double>,
						TableCell<Movie, Double>>
	{
	public TableCell<Movie, Double>	call(TableColumn<Movie, Double> v)
		{
		return new AvgReviewScoreCell();
		}
	}
	
	// This CellFactory creates Cells for the runtime column in the table.
	private final class RuntimeCellFactory
	implements Callback<TableColumn<Movie, Integer>,
						TableCell<Movie, Integer>>
	{
	public TableCell<Movie, Integer>	call(TableColumn<Movie, Integer> v)
		{
		return new RuntimeCell();
		}
	}


	// TODO #6: Complete the CellFactory classes for your three attributes.
	// You must adapt the code to the column's attribute type in each case.

	// private final class Attr1CellFactory
	// {
	// }

	// private final class Attr2CellFactory
	// {
	// }

	// private final class Attr3CellFactory
	// {
	// }

	//**********************************************************************
	// Inner Classes (Cells)
	//**********************************************************************

	// Each Cell determines the contents of one row/column intersection in the
	// table. The code for each one maps its attribute object into text and/or
	// graphic in different ways.

	// To modify the styling of cells, use methods in the ancestor classes of
	// javafx.scene.control.TableCell, especially javafx.scene.control.Labeled
	// and javafx.scene.layout.Region. (You can also edit View.css. It currently
	// sets background-color and text-fill properties for entire rows of cells.)

	// To make a cell editable...although only shallowly at this point:
	// Extend a javafx.scene.control.cell.*TableCell class to allow editing.
	// Match a javafx.util.converter.*StringConverter to each attribute type.

	// This TableCell displays the title, and allows editing in a TextField.
	private final class TitleCell
		extends TextFieldTableCell<Movie, String>
	{
		public TitleCell()
		{
			super(new DefaultStringConverter());	// Since values are Strings
		}

		public void	updateItem(String value, boolean isEmpty)
		{
			super.updateItem(value, isEmpty);		// Prepare for setup

			if (isEmpty || (value == null))		// Handle special cases
			{
				setText(null);
				setGraphic(null);

				return;
			}

			// This cell shows the value of the title attribute as simple text.
			// If the title is too long, an ellipsis is inserted in the middle.
			String	title = value;

			setText(title);
			setTextOverrun(OverrunStyle.CENTER_ELLIPSIS);
			setGraphic(null);
		}
	}

	// This TableCell displays the image, and doesn't allow editing.
	private final class ImageCell
		extends TableCell<Movie, String>
	{
		public void	updateItem(String value, boolean isEmpty)
		{
			super.updateItem(value, isEmpty);		// Prepare for setup

			if (isEmpty || (value == null))		// Handle special cases
			{
				setText(null);
				setGraphic(null);
				setAlignment(Pos.CENTER);

				return;
			}

			// This cell uses the value of the posterFileName attribute
			// to show an image loaded from resources/example/fx/icon.
			String		posterFileName = value;
			ImageView	image = createFXIcon(posterFileName, W, H);

			setText(null);
			setGraphic(image);
			setAlignment(Pos.CENTER);
		}
	}
	
	// This TableCell displays the rating, and allows editing in a TextField.
	private final class RatingCell
	extends TextFieldTableCell<Movie, String>
{
		public RatingCell()
		{
			super(new DefaultStringConverter());	// Since values are Strings
		}
	public void	updateItem(String value, boolean isEmpty)
	{
		super.updateItem(String.valueOf(value), isEmpty);		// Prepare for setup

		if (isEmpty || (value == null))		// Handle special cases
		{
			setText(null);
			setGraphic(null);

			return;
		}
		
		String	rating = value;

		setText(rating);
		setTextOverrun(OverrunStyle.CENTER_ELLIPSIS);
		setGraphic(null);
	}
}
	
	// This TableCell displays the Avg Review Score, and allows editing in a TextField.
	private final class AvgReviewScoreCell
	extends TextFieldTableCell<Movie, Double>
{
	 public AvgReviewScoreCell()
	{
		super(new DoubleStringConverter());	// Since values are Strings

	}
	 
	public void	updateItem(Double value, boolean isEmpty)
	{
		super.updateItem(value, isEmpty);		// Prepare for setup

		if (isEmpty || (value == null))		// Handle special cases
		{
			setText(null);
			setGraphic(null);

			return;
		}

		Double	reviewScore = value;
		setText(String.valueOf(reviewScore));
		setTextOverrun(OverrunStyle.CENTER_ELLIPSIS);
		setGraphic(null);
	}
}

		// This TableCell displays the runtime, and allows editing in a TextField.
	private final class RuntimeCell
	extends TextFieldTableCell<Movie, Integer>
{
	public RuntimeCell()
		{
				super(new IntegerStringConverter());	// Since values are Strings
		}
	public void	updateItem(Integer value, boolean isEmpty)
	{
		super.updateItem(value, isEmpty);		// Prepare for setup

		if (isEmpty || (value == null))		// Handle special cases
		{
			setText(null);
			setGraphic(null);

			return;
		}

		// This cell shows the value of the title attribute as simple text.
		// If the title is too long, an ellipsis is inserted in the middle.
		Integer	rating = value;
		setText(String.valueOf(rating));
		setTextOverrun(OverrunStyle.CENTER_ELLIPSIS);
		setGraphic(null);
	}
}
	// TODO #5: Complete the Cell classes for your three attributes.
	// You must adapt the code to the column's attribute type in each case.
	// Allow editing (shallowly) in at least one of the three columns.

	// private final class Attr1Cell
	// {
	// }

	// private final class Attr2Cell
	// {
	// }

	// private final class Attr3Cell
	// {
	// }

	//**********************************************************************
	// Inner Classes (Table Column Edit Handlers)
	//**********************************************************************

	// This EventHander processes edits in the title column.
	private final class TitleEditHandler
		implements EventHandler<TableColumn.CellEditEvent<Movie, String>>
	{
		public void	handle(TableColumn.CellEditEvent<Movie, String> t)
		{
			// Get the movie for the row that was edited
			int	index = t.getTablePosition().getRow();
			Movie	movie = movies.get(index);

			// Set its title to the new value that was entered
			movie.setTitle(t.getNewValue());
		}
	}

	// No EventHander implemented, since the image column isn't editable.
	//private final class ImageEditHandler
	//{
	//}

	// This EventHander processes edits in the Rating column.
	private final class RatingEditHandler
	implements EventHandler<TableColumn.CellEditEvent<Movie, String>>
{
	public void	handle(TableColumn.CellEditEvent<Movie, String> t)
	{
		// Get the movie for the row that was edited
		int	index = t.getTablePosition().getRow();
		Movie movie = movies.get(index);
		// Set its title to the new value that was entered
		movie.setRating(t.getNewValue());
	}
}
	// This EventHander processes edits in the Avg. Review column.
	private final class AvgReviewScoreEditHandler
	implements EventHandler<TableColumn.CellEditEvent<Movie, Double>>
{
	public void	handle(TableColumn.CellEditEvent<Movie, Double> t)
	{
		// Get the movie for the row that was edited
		int	index = t.getTablePosition().getRow();
		Movie movie = movies.get(index);
		// Set its title to the new value that was entered
		movie.setReviewScore(t.getNewValue());
	}
}
	
	// This EventHander processes edits in the runtime column.
	private final class RuntimeEditHandler
	implements EventHandler<TableColumn.CellEditEvent<Movie, Integer>>
{
	public void	handle(TableColumn.CellEditEvent<Movie, Integer> t)
	{
		// Get the movie for the row that was edited
		int	index = t.getTablePosition().getRow();
		Movie movie = movies.get(index);
		// Set its title to the new value that was entered
		movie.setRuntime(t.getNewValue());
	}
}
	// TODO #4: Add an EventHandler class for each of your editable columns.
	// You must adapt the code to the column's attribute type in each case.
	// Allow editing (shallowly) in at least one of the three columns.

	// private final class Attr1EditHandler
	// {
	// }

	// private final class Attr2EditHandler
	// {
	// }

	// private final class Attr3EditHandler
	// {
	// }
	
// Class to handing rating, gets the integer value and returns the string value
	
	private String	handleRating(Integer rating)
	{
		if (rating.equals(0))
		{
			return "G";
		}
		else if (rating.equals(1))
		{
			return "PG";
		}
		else if (rating.equals(2))
		{
			return "PG-13";
		}
		else
		{
			return "R";
		}
	}
}

//******************************************************************************
