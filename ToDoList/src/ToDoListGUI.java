import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ToDoListGUI extends Application implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2299601043533513736L;
	private ObservableList<String> todos;
	private ListView<String> listView;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		Alert alert1 = new Alert(AlertType.CONFIRMATION);
		alert1.setTitle("Confirmation");
		alert1.setHeaderText("Click cancel to start with zero todos");
		alert1.setContentText("Click OK to start with persistant ToDoList");
		Optional<ButtonType> result1 = alert1.showAndWait();

		todos = FXCollections.observableArrayList();

		if (result1.get() == ButtonType.OK) {
			try {
				FileInputStream fis = new FileInputStream("todos.ser");
				ObjectInputStream ois = new ObjectInputStream(fis);
				List<String> saved = (List<String>) ois.readObject();
				ois.close();
				todos = FXCollections.observableList(saved);
			} catch (IOException | ClassNotFoundException e) {
				System.out.println("Exception");
			}

		}

		listView = new ListView<>(todos);
		listView.setCellFactory(param -> new ToDoListCell());

		Label label = new Label("Enter a new ToDo");
		TextField textField = new TextField();
		Button saveButton = new Button("Save current list");
		VBox vb = new VBox(2, label, textField, saveButton);
		vb.setAlignment(Pos.CENTER_LEFT);
		vb.setPadding(new Insets(5, 10, 5, 10));

		Button topButton = new Button("Top");
		topButton.setPrefWidth(100);
		Button bottomButton = new Button("Bottom");
		bottomButton.setPrefWidth(100);
		Button raiseButton = new Button("Raise");
		raiseButton.setPrefWidth(100);
		Button lowerButton = new Button("Lower");
		lowerButton.setPrefWidth(100);
		Button removeButton = new Button("Remove");
		removeButton.setPrefWidth(100);

		saveButton.setOnAction(event -> {
			if (textField.getText() != "") {
				todos.add(0, textField.getText());
				textField.setText("");
				listView.getSelectionModel().select(0);
			}
		});

		topButton.setOnAction(event -> {
			int i = listView.getSelectionModel().getSelectedIndex();
			if (i >= 0) {
				String task = todos.remove(i);
				todos.add(0, task);
				listView.getSelectionModel().select(0);
			}
		});

		bottomButton.setOnAction(event -> {
			int i = listView.getSelectionModel().getSelectedIndex();
			if (i >= 0) {
				String task = todos.remove(i);
				todos.add(task);
				listView.getSelectionModel().select(todos.size() - 1);
			}
		});

		raiseButton.setOnAction(event -> {
			int i = listView.getSelectionModel().getSelectedIndex();
			if (i > 0) {
				String task = todos.remove(i);
				todos.add(i - 1, task);
				listView.getSelectionModel().select(i - 1);
			}
		});

		lowerButton.setOnAction(event -> {
			int i = listView.getSelectionModel().getSelectedIndex();
			if (i >= 0 && i < todos.size() - 1) {
				String task = todos.remove(i);
				todos.add(i + 1, task);
				listView.getSelectionModel().select(i + 1);
			}
		});

		removeButton.setOnAction(event -> {
			int i = listView.getSelectionModel().getSelectedIndex();
			if (i >= 0) {
				todos.remove(i);
			}
		});

		HBox hb = new HBox(10, topButton, bottomButton, raiseButton, lowerButton, removeButton);
		hb.setAlignment(Pos.CENTER);
		hb.setPadding(new Insets(5, 5, 10, 5));

		BorderPane root = new BorderPane();
		root.setTop(vb);
		root.setCenter(listView);
		root.setBottom(hb);

		primaryStage.setScene(new Scene(root, 500, 300));
		primaryStage.setOnCloseRequest(event -> {
			Alert alert2 = new Alert(Alert.AlertType.CONFIRMATION);
			alert2.setTitle("ToDo List");
			alert2.setHeaderText("Click cancel to not save any changes");
			alert2.setContentText("To Save the current ToDo list, click OK");

			Optional<ButtonType> result2 = alert2.showAndWait();
			if (result2.isPresent()) {
				if (result2.get() == ButtonType.OK) {
					try {
						FileOutputStream fos = new FileOutputStream("todos.ser");
						ObjectOutputStream oos = new ObjectOutputStream(fos);
						oos.writeObject(new ArrayList<String> (todos));
						oos.close();
					} catch (IOException ioe) {
						System.out.println("Writing list failed");
					}
				}
				Platform.exit();
				System.exit(0);
				primaryStage.close();
			}
		});

		primaryStage.show();
	}

	private class ToDoListCell extends ListCell<String> {
		@Override
		protected void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);
			if (empty || item == null) {
				setText(null);
				setGraphic(null);
			} else {
				setText(item);
			}
		}
	}
}