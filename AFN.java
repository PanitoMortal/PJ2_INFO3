/*
	Utilice esta clase para guardar la informacion de su
	AFN. NO DEBE CAMBIAR LOS NOMBRES DE LA CLASE NI DE LOS 
	METODOS que ya existen, sin embargo, usted es libre de 
	agregar los campos y metodos que desee.
*/

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;
import java.io.FileWriter;

public class AFN{
	
	/*
		Implemente el constructor de la clase AFN
		que recibe como argumento un string que 
		representa el path del archivo que contiene
		la informacion del AFN (i.e. "Documentos/archivo.AFN").
		Puede utilizar la estructura de datos que desee
	*/
	private String path;
	private String[] alfabeto; //primera linea alfabeto
	private String states; // segunda linea n estados
	private String[] final_state; //tercera linea n estados finales
	private ArrayList<String[]> transitions;
	private ArrayList<String[]> combinedTransitions;
	private String[] Nstates;
	private String[][] transitionMatrix;

	public AFN(String path){
		this.path = path;
		this.transitions = new ArrayList<>();
		this.combinedTransitions = new ArrayList<>();

		try {	
			FileReader fileReader = new FileReader(path);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			
			String line;
            int lineCount = 0;
			int cont = 0;
            while ((line = bufferedReader.readLine()) != null) {
                if(lineCount == 0){
					String[] temp = line.split(",");
                    // Crear un nuevo arreglo con un tamaño mayor para insertar "lambda"
                    alfabeto = new String[temp.length + 1];
                    // Agregar "lambda" en la posición 1
                    alfabeto[0] = "lambda";
                    for (int i = 0; i < temp.length; i++) {
                        alfabeto[i + 1] = temp[i];
                    }
				}else if (lineCount == 1) {
					states = line;
				}else if (lineCount == 2) {
					final_state = line.split(",");
				}else {
					transitions.add(line.split(","));
					
				}

				
                lineCount++;
            }
		
			//System.out.println(Arrays.toString(alfabeto));
			int transitionLength = transitions.get(0).length;

			for (int i = 0; i < transitionLength; i++) {
				String[] combined = new String[transitions.size()];
				for (int j = 0; j < transitions.size(); j++) {
					combined[j] = transitions.get(j)[i];
				}
				combinedTransitions.add(combined);
			}

			//crear el reglo para los estados
			Nstates = new String[Integer.parseInt(states)];
			int pos = 0;
			for (String[] transition : combinedTransitions) {
				if (pos < Integer.parseInt(states) ) {
					//System.out.println(pos + "--" +Arrays.toString(transition));
					Nstates[pos] = String.valueOf(pos);
					pos++;
				}
			
			}

			
			// Determinar las dimensiones de la matriz
			int numRows = Nstates.length;
			int numCols = alfabeto.length + 1; // Sumar 1 para incluir los estados

			// Crear una matriz para representar las transiciones
			transitionMatrix = new String[numRows][numCols];

			// Iterar sobre los elementos de Nstates y combinedTransitions para combinarlos
			for (int i = 0; i < Nstates.length; i++) {
				String[] combined = new String[combinedTransitions.get(i).length + 1];
				combined[0] = Nstates[i]; // Agrega el estado actual al principio del arreglo combinado

				// Copia los elementos de combinedTransitions[i] al arreglo combinado
				System.arraycopy(combinedTransitions.get(i), 0, combined, 1, combinedTransitions.get(i).length);

				// Llenar la primera columna de la matriz con los estados
				transitionMatrix[i][0] = Nstates[i];

				// Asociar con el alfabeto y llenar la matriz de transiciones
				for (int j = 0; j < alfabeto.length; j++) {
					// (estado, símbolo) = estado relacionado
					String estadoRelacionado = combined[j + 1]; // El estado relacionado empieza desde el segundo elemento de combined
					transitionMatrix[i][j + 1] = estadoRelacionado;
				}
			}

			/*// Imprimir la matriz de transiciones
			System.out.println("Transiciones:");
			for (int i = 0; i < numRows; i++) {
				for (int j = 0; j < numCols; j++) {
					System.out.print(transitionMatrix[i][j] + "\t");
				}
				System.out.println();
			}*/





			//----------------------------------------------------------------------------------------------------------------------
			//System.out.println(transitionMatrix[1][0]);
			bufferedReader.close();
			
		} catch (IOException e) {
			System.out.println("Error");
		}

	}

	/*
		Implemente el metodo accept, que recibe como argumento
		un String que representa la cuerda a evaluar, y devuelve
		un boolean dependiendo de si la cuerda es aceptada o no 
		por el AFN. Recuerde lo aprendido en el proyecto 1.
	*/
	public boolean accept(String string) {
		// Estado inicial
		String currentState = transitionMatrix[1][0];
		
		// Iterar sobre cada carácter de la cadena
		for (int i = 0; i < string.length(); i++) {
			char symbol = string.charAt(i);
			//System.out.println("simbolo: "+ symbol);
			// Obtener el índice del símbolo en el alfabeto
			int symbolIndex = -1;
			for (int j = 0; j < alfabeto.length; j++) {
				if (alfabeto[j].equals(String.valueOf(symbol))) {
					//System.out.println("antes: "+ symbolIndex + "j : " + j);
					symbolIndex = j;
					//System.out.println("Despues: " +symbolIndex + "j : " + j);
					break;
				}
			}
			
			// Verificar si hay una transición definida para el símbolo y el estado actual
			if (symbolIndex != -1) {
				String nextState = transitionMatrix[Integer.parseInt(currentState)][symbolIndex + 1];
				// Mostrar la transición en pantalla
				//System.out.println("Transición: (" + currentState + ", " + symbol + ") -> " + nextState);
				currentState = nextState;
			} else {
				// No hay transición definida para el símbolo y el estado actual
				//System.out.println("No hay transición definida para el símbolo " + symbol + " en el estado " + currentState);
				return false;
			}
		}
		
		// Verificar si el estado actual es uno de los estados finales
		for (String state : final_state) {
			if (currentState.equals(state)) {
				System.out.println("La cadena es aceptada");
				return true;
			}
		}
		
		// La cadena no es aceptada
		System.out.println("La cadena no es aceptada");
		return false;
	}
		
	/*
		Implemente el metodo toAFD. Este metodo debe generar un archivo
		de texto que contenga los datos de un AFD segun las especificaciones
		del proyecto.
	*/
	public void toAFD(String afdPath) {
		// Estado inicial
		
		String currentState = transitionMatrix[1][0];
		Clausura_lambda_estado_inicial(currentState);
		

	}
	
	public void Clausura_lambda_estado_inicial(String currentstate){
		//System.out.println("Aqui comienza la clausura lambda");
		int ascii = 65;
		String currentState = currentstate;
		String[] NewState;
		
		// Encuentra el índice del símbolo "lambda"
		int lambdaIndex = -1;
		for (int j = 0; j < alfabeto.length; j++) {
			if (alfabeto[j].equals("lambda")) {
				lambdaIndex = j;
				break;
			}
		}
		
		// Verifica si el índice de "lambda" fue encontrado
		if (lambdaIndex != -1) {
			// Mostrar las transiciones con "lambda" desde el estado inicial
		
			String nextState = transitionMatrix[Integer.parseInt(currentState)][lambdaIndex + 1];
			if (nextState.contains(";")) {
				NewState = nextState.split(";");
				
				System.out.println("Transición: (" + currentState + ", lambda) -> " +Arrays.toString(NewState));


			}else{
				System.out.println("Transición: (" + currentState + ", lambda) -> " + nextState);
				NewState = new String[nextState.length()];
				for(int i=0; i < nextState.length(); i++){
					NewState[i] = nextState;
				}
				System.out.println("Nuevo estado " + (char) ascii +" "+ Arrays.toString(NewState));
			}
			Cambio_inicial(NewState);
			
		} else {
			System.out.println("No se encontró el símbolo lambda en el alfabeto.");
		}
	}

	public void Cambio_inicial(String[] NewState){
		//System.out.println("Aqui comienza el cambio");
		String[] NewTransitionMatrix = new String[alfabeto.length -1];
		for (String state : NewState) {
				for (int i = 1; i < alfabeto.length; i++) { // Empezamos desde 1 para evitar el elemento "lambda"
				//nuevos estados al realcionar cada state con cada elemento de alfabeto
				System.out.println("Cambio: "+"(" + state + ", " + alfabeto[i] + ") -> " + transitionMatrix[Integer.parseInt(state)][i + 1]);
				NewTransitionMatrix[i-1] = transitionMatrix[Integer.parseInt(state)][i + 1];
			}
		}
		int num = 66;
		for(String state: NewTransitionMatrix){
			Clausura_lambda(state, num);
			num++;
		}
		System.out.println("este es el arreglo de nuevas transi "+Arrays.toString(NewTransitionMatrix));
		//Cambio(NewTransitionMatrix);
	}

	public void Clausura_lambda(String current, int ascii){
		String currentState = current;
		String[] NewState;
		
		// Encuentra el índice del símbolo "lambda"
		int lambdaIndex = -1;
		for (int j = 0; j < alfabeto.length; j++) {
			if (alfabeto[j].equals("lambda")) {
				lambdaIndex = j;
				break;
			}
		}
		
		// Verifica si el índice de "lambda" fue encontrado
		if (lambdaIndex != -1) {
			// Mostrar las transiciones con "lambda" desde el estado inicial
		
			String nextState = transitionMatrix[Integer.parseInt(currentState)][lambdaIndex + 1];
			if (nextState.contains(";")) {
				NewState = nextState.split(";");
				
				System.out.println("Transición: (" + currentState + ", lambda) -> " +Arrays.toString(NewState));


			}else{
				System.out.println("Transición: (" + currentState + ", lambda) -> " + nextState);
				NewState = new String[nextState.length()];
				for(int i=0; i < nextState.length(); i++){
					NewState[i] = nextState;
				}
				System.out.println("Nuevo estado " + (char) ascii +" "+ Arrays.toString(NewState));
		
			}
			
		} else {
			System.out.println("No se encontró el símbolo lambda en el alfabeto.");
		}
		
	}

	



	

	/*
		-El metodo main debe recibir como primer argumento el path
		donde se encuentra el archivo ".afd",
		-como segundo argumento una bandera ("-f" o "-i"). Si la bandera es "-f", debe recibir
		como tercer argumento el path del archivo con las cuerdas a 
		evaluar, y si es "-i", debe empezar a evaluar cuerdas ingresadas
		por el usuario una a una hasta leer una cuerda vacia (""), en cuyo
		caso debe terminar. Tiene la libertad de implementar este metodo
		de la forma que desee. 
	*/
	public static void main(String[] args) throws Exception{
		Scanner scanner = new Scanner(System.in);
		String filepath = args[0];
		String cadena;
		AFN afn = new AFN(filepath);

		if(args.length != 0){

			if(args[1].equals("-f")){
				String path_archivo = args[2];
				FileReader fileReader = new FileReader(path_archivo);
				BufferedReader bufferedReader = new BufferedReader(fileReader);
				
				String line;
				try {
					while ((line = bufferedReader.readLine()) != null) {
						String[] parts = line.split(" ");
                        String dato = parts[0];
                        afn.accept(dato);
					}
					
				} catch (IOException e) {
					System.out.println("Error");
				}
				bufferedReader.close();

			}else if (args[1].equals("-i")) {
				while (true) {
					System.out.print("Ingrese la cuerda a evaluar: ");
					cadena = scanner.nextLine();
					
					if(cadena.length() == 0){
						System.out.println("Saliendo del programa...");
						break;
					}
					afn.accept(cadena);
				}
				
			}else if (args[1].equals("-to-afd")) {
				String afdpath = args[2];
				System.out.println(afdpath);
				afn.toAFD(afdpath);
				
			}else{
				System.out.println("Bandera incorrecta");
			}

		}else{
			System.out.print("No ingreso el path en el argumento");
		}
	}
}