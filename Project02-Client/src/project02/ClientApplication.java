package project02;

import java.awt.TextArea;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ClientApplication extends Application {
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Cliente");

		ListView booksListView = new ListView();
		Label serverTextArea = new Label("MSGs");
		ArrayList<Book> booksList = new ArrayList<>();;
		
		
		Button btnConectar = new Button("Conectar");

		btnConectar.setOnAction(event -> {
			Client.getIntance().connectToServer();
			booksListView.getItems().removeAll(booksListView.getItems());
			booksList.removeAll(booksList);
			booksList.addAll(Client.getIntance().requestBookList());
			booksListView.getItems().addAll(booksList);
		});
		
		
		Button btnEmprestar = new Button("Emprestar");
		
		btnEmprestar.setOnAction(event -> {
            ObservableList selectedIndices = booksListView.getSelectionModel().getSelectedIndices();
            for(Object b : selectedIndices){
            	int selectIndex = (int)b;
            	ServerMessage m = Client.getIntance().requestBorrowBook( booksList.get(selectIndex).id);
            	serverTextArea.setText(m+"");
            }
		});
		
		Button btnDevolver = new Button("Devolver");
		
		Button btnReservar = new Button("Reservar");
		
		

		VBox vBoxLista = new VBox(booksListView, btnConectar);
		VBox vBoxControl = new VBox(btnEmprestar, btnDevolver, btnReservar);
		HBox hBox = new HBox(vBoxLista, vBoxControl, serverTextArea);
		Scene scene = new Scene(hBox, 800, 600);
		primaryStage.setScene(scene);
		primaryStage.show();

	}
	
	public  static void main(String [] args){
		Application.launch(args);
	}

}
