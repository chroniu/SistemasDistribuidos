package project02;

import java.util.ArrayList;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
Implementa a interface gráfica
*/
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
//			Client.getIntance().setApplicationDialog(this);
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
            	ServerMessage m = Client.getIntance().requestBorrowBook(((Book)booksListView.getItems().get(selectIndex)).id );
//            	((Book)booksListView.getItems().get	(selectIndex)).setBackGroundColor(Color.RED);
            	serverTextArea.setText(m+"");
            	
	            if(m.equals(ServerMessage.OPERATION_SUCESSFULL)||m.equals(ServerMessage.RENEWED_SUCESSFULLY)){
//	            	this.tableEmprestados.
	            }
            	
            }
		});
		
		Button btnDevolver = new Button("Devolver");
		btnDevolver.setOnAction(event -> {
            ObservableList selectedIndices = booksListView.getSelectionModel().getSelectedIndices();
            for(Object b : selectedIndices){
            	int selectIndex = (int)b;
            	long m = Client.getIntance().requestgiveBackBook(((Book)booksListView.getItems().get(selectIndex)).id );
            	serverTextArea.setText(m+"");
            }
		});
		
		Button btnReservar = new Button("Reservar");
		btnReservar.setOnAction(event -> {
            ObservableList selectedIndices = booksListView.getSelectionModel().getSelectedIndices();
            for(Object b : selectedIndices){
            	int selectIndex = (int)b;
            	ServerMessage m = Client.getIntance().requestReserveBook(((Book)booksListView.getItems().get(selectIndex)).id );
            	serverTextArea.setText(m+"");
            }
		});
		
		
		

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
