/* Java класс, генерирующий HTML таблицы классической игры Судоку для JSP страницы index.jsp
 * Каждая задача судоку при генерации решается естественным алгоритмом (SudokuSolver). 
 * Алгоритм проверяет задачу на наличие только одного логического решения
 * и возвращает ее в основной класс (Sudoku) для отображения на HTML странице.   
 * */

package sudoku_JSP_2015;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

 
public class Sudoku {
	static int[][] bigGameArray;                            // основной массив чисел, содержащий все ответы
	private static String htmlMenuCode;                            // строка содержит HTML код, отображающий 9 кнопок вариантов ответа
	private static String firstParameterName = "z";                // 1 параметр содержит уровень сложности, инициируется неиспользуемой буквой z 
	private static String secondParameterName = "z";               // 2 параметр содержит последнюю нажатую кнопку
	private static String firstParameterValue;                     
	private static String secondParameterValue;
	private static Enumeration<?> paramNamesEnumeration;           // используется для извлечения имен параметров
	static int gameStatus = 0;                              // 0 - режим ожидания (чистое поле без нажатых кнопок)
	// 1 - ожидается нажатие кнопки на рабочем поле(9х9), 2 - нажата кнопка на рабочем поле, ожидается нажатие кнопки меню (выбора значения)
	
	private static int lastButtonNum;                              // номер последней нажатой кнопки
	static  String message = "";                            // используется для вывода сообщений на HTML странице
	static SudokuGameButton[][] arrayOfButtons = new SudokuGameButton[9][9];    // массив экземпляров кнопок
	static int gameComplexity = 0;                          // сложность игры, от 0 до 3
	static boolean[][] openButtonsArray = new boolean [9][9];   // массив открытых кнопок, создается генератором каждый раз отдельно
	static boolean testMode = true;                         // для отображения тех. информации назначить true
	static String answerForJSP;                             // HTML код для JSP страницы
	static boolean showMenu = false;                        // разрешает отображение меню выбора (9 кнопок)

   
    public Sudoku() throws IOException {
    	try {
    		startGame();
    		}
    	catch (Exception e) {
    		throw new IOException("Sudoku constructor exception.\n" + displayErrorForWeb(e));   
    		// все ошибки конструктора выводятся в HTML ответе
    	}     	
    }    
     
    private static void startGame() {
    	SudokuGenerator sGenerator = new SudokuGenerator();    	
    	bigGameArray = sGenerator.generateSudokuArray();     // генерируем рабочий массив, 81 число
    	openButtonsArray = sGenerator.getOpenButtonsArray(); 
    	// генерируем массив открытых кнопок, который вместе с рабочим массивом уже проверен и имеет единственное решение	
    	createButtonMenuCode();                              
    	setButtonArray();
    	SudokuGameButton.initiateButtonsArray(bigGameArray);  // инициируем экземпляры кнопок рабочего поля
    	updateAllButtonsCode();    
    }
    
    private static void setButtonArray(){            // генерирует массив экземпляров кнопок                     
		for (int i = 0; i<9;i++ ){
    		for (int j = 0; j<9; j++){
    			arrayOfButtons[i][j] = new SudokuGameButton(i,j);	
    		}
    	}
	}
    
    private static void updateAllButtonsCode(){                     
    	// каждая кнопка генерирует свой HTML код, который впоследствии собирается в код рабочего поля 
		for (int i = 0; i<9;i++ ){
    		for (int j = 0; j<9; j++){	
    			arrayOfButtons[i][j].updateButtonHtmlCode();
    		}
    	}
	}
    
    static void printHtml(String inStr){           // выводит текст на HTML странице  
    	if (testMode == true){
    		message += "&nbsp;" + inStr;
		} 	
    } 
    
    private static void createButtonMenuCode(){             // создает HTML код меню (9 кнопок выбора значений)
    	htmlMenuCode = ""; 
    	for (int i = 0; i<9;i++ ){
    		htmlMenuCode += "<th><input class=\"buttdrk\" name=\"m0" + (i+1) +"\" type=\"submit\" value=\"" + (i+1) +"\"/></th>";    			
    		}
    }
     
    private static void getRequestParameters(HttpServletRequest httpReq){   
    	// извлекает имена и значения 2 параметров http запроса
    	paramNamesEnumeration = httpReq.getParameterNames();
	    if (paramNamesEnumeration.hasMoreElements()) {
	    	firstParameterName = (String) paramNamesEnumeration.nextElement();
	    	secondParameterName = (String) paramNamesEnumeration.nextElement();
	    	}
	    Iterator<String[]> reqIter = httpReq.getParameterMap().values().iterator();
	    if (reqIter.hasNext()) {
	    	firstParameterValue = reqIter.next()[0];
	    	secondParameterValue = reqIter.next()[0];
	    	}  
	    }
    
	
	public static String getBigHtmlForJSP(HttpServletRequest request) throws IOException{ 
		String answerForJSP = "";
		showMenu = false;
		message = "";
		getRequestParameters(request);
		if (secondParameterName.charAt(0) == "n".charAt(0)) {     // нажата кнопка New game 
			changeComplexity();
		}
		if (gameStatus == 2) {         // нажата кнопка меню выбора значения
			prepareForValueChange();		 		
		}
		if (gameStatus == 1) {         // нажата кнопка рабочего поля
			answerForJSP += prepareForColorChange();				
		} else {
			gameStatus = 0;
		}

		if (gameStatus == 0){          // режим ожидания (чистое поле без нажатых кнопок)
			answerForJSP = getHtmlBody(); 
			gameStatus = 1;
		}
		return answerForJSP;		
	}
	
	public static String getMenuHtmlForJSP(){   // возвращвет HTML код меню выбора значений
		if (showMenu == true){
			return htmlMenuCode;
			};
		return "";
		}
	
	public static String getMessageForJSP(){
		return message;		
	}
	
	private static void changeComplexity(){                  // меняет сложность и перезапускает игру
		switch (firstParameterValue.charAt(0)) {
		case 'E':
			gameComplexity = 0;			
			break;
		case 'N':
			gameComplexity = 1;
			break;
		case 'H':
			gameComplexity = 2;
			break;}
		gameStatus = 0;
		startGame();
	}
	
	private static void prepareForValueChange(){                         // меняет значение кнопки рабочего поля
		if (secondParameterName.charAt(0) == "m".charAt(0)) {     // если нажата кнопка меню выбора значений
			int lastValue = Integer.parseInt(secondParameterValue);
			SudokuGameButton.changeHtmlFieldArray(lastButtonNum,lastValue);   // попытка изменить значение
			gameStatus = 0; 
		} else if (secondParameterName.charAt(0) == "b".charAt(0)) { // если нажата другая кнопка рабочего поля
			gameStatus = 1;
		}
	}
	
	private static String prepareForColorChange() throws IOException{    // меняет цвет кнопки рабочего поля
		if (secondParameterName.charAt(0) == "b".charAt(0)){
			String answer = "";
			lastButtonNum = Integer.parseInt(secondParameterName.substring(1, 3));   
			// запоминает номер нажатой кнопки
			SudokuGameButton.changeColor(lastButtonNum);
			answer = getHtmlBody(); 
			gameStatus = 2;	   
			return answer;
		}
		else {
			gameStatus = 0;
			return "";			
		}
	}
	
	private static String getHtmlBody(){	  // генерирует оставшийся HTML код 	
		// код кнопки начала новой игры
		 String htmlCenterCode = SudokuGameButton.createStringFromAllButtonsCode();   // генерирует и добавляет HTML код рабочего поля
		if (gameStatus==1){  
			showMenu = true;
			}	
		
		//htmlCenterCode += message;                                               // выводит сообщения от разработчика
		//message = "";
		return htmlCenterCode;  
	}
	
	String displayErrorForWeb(Throwable t) {            // получает StackTrace и приводит его к наглядному виду
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		String stackTrace = sw.toString();
		stackTrace = "\n" + stackTrace;
		return stackTrace.replace(System.getProperty("line.separator"), "\n");
	}
	
	static void printIntArray(int[][] incomeArr) {   // выводит массив чисел на HTML странице
		if (testMode == false){
			return;
		}
		for (int i = 0; i < incomeArr.length; i++) {
			printHtml("<br>");
			for (int j = 0; j < incomeArr.length; j++) {
				if (incomeArr[i][j]>0){
				printHtml(incomeArr[i][j] + "");}
				else {printHtml("&nbsp;&nbsp;"); }
			}
		}
	}
}	




/*
 * public static void sendToLogFile(String inStr){                                     // DEL or ?
    	try(FileWriter writer = new FileWriter("E:/servlet_logs.txt", true) )  {
            writer.write(getCurrentTime() + "  " +inStr + "\n");
        }catch(Exception ex){  
            ex.printStackTrace();  
        }
    }
	public static String getCurrentTime() {                            // ????????????????????
        String currentTime = new SimpleDateFormat("HH:mm:ss:SSS").format(Calendar.getInstance().getTime());
        return currentTime;
    }
 
 * public void doGet(HttpServletRequest request) throws ServletException, IOException {
		//sendToLogFile("WRONG !!!!  doGet ");
		// ïðè êàæäîì íàæàòèè íà êíîïêó èëè îáíîâëåíèè ñòðàíèöû
		
	}
 * 
 * try {
 
			
			getRequestParameters(request);
			if (secondParameterName.charAt(0) == "n".charAt(0)) {     // íàæàòà êíîïêà New game 
				changeComplexity();
			}
			if (gameStatus == 2) {         // íàæàòà êíîïêà ìåíþ âûáîðà çíà÷åíèÿ
				prepareForValueChange();				
			}
			if (gameStatus == 1) {         // íàæàòà êíîïêà ðàáî÷åãî ïîëÿ
				prepareForColorChange();				
			} else {
				gameStatus = 0;
			}

			if (gameStatus == 0){          // ðåæèì îæèäàíèÿ (÷èñòîå ïîëå áåç íàæàòûõ êíîïîê)
				//pw.println(getHtmlHead() + getHtmlBody());
				gameStatus = 1;
			}
			
			//pw.close();
		} catch (Exception e) {
			throw new ServletException("Sudoku doGet exception.\n" + displayErrorForWeb(e));
			// âñå îøèáêè âûâîäÿòñÿ â HTML îòâåòå 
		}*/